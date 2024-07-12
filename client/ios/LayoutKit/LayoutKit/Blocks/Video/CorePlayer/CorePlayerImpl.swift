import AVFoundation
import Foundation
import VGSL

final class CorePlayerImpl: CorePlayer {
  static func isMIMETypeSupported(_ mimeType: String) -> Bool {
    AVURLAsset.isPlayableExtendedMIMEType(mimeType)
  }

  var videoEngine: VideoEngine {
    VideoEngine(type: .avPlayer(player))
  }

  var playerStatusDidChange: Signal<PlayerStatus> {
    playerStatusPipe.signal
  }

  var playerDurationDidChange: Signal<TimeInterval> {
    playerDurationPipe.signal
  }

  var playbackStatusDidChange: Signal<PlaybackStatus> {
    playbackStatusPipe.signal
  }

  var playbackDidFail: Signal<PlayerError> {
    playerErrorPipe.signal
  }

  var playbackDidFinish: Signal<Void> {
    playbackFinishPipe.signal
  }

  private let player: AVPlayer
  private let itemsProvider: PlayerItemsProvider

  private let playerObservers = AutodisposePool()
  private let itemObservers = AutodisposePool()

  private let playerStatusPipe = SignalPipe<PlayerStatus>()
  private let playerDurationPipe = SignalPipe<TimeInterval>()
  private let playbackStatusPipe = SignalPipe<PlaybackStatus>()
  private let playerErrorPipe = SignalPipe<PlayerError>()
  private let playbackFinishPipe = SignalPipe<Void>()

  private var currentTimePipes = [TimeInterval: SignalPipe<TimeInterval>]()

  init(itemsProvider: PlayerItemsProvider) {
    self.player = AVPlayer()
    self.itemsProvider = itemsProvider
    setup(player)
  }

  private func resetPlayerObservers() {
    playerObservers.drain()
  }

  private func resetItemObservers() {
    itemObservers.drain()
  }

  func set(source: Video) {
    itemsProvider.getAVPlayerItem(from: source.url) { [weak self] item in
      self?.resetItemObservers()
      self?.configureObservers(for: item)
      self?.player.replaceCurrentItem(with: item)
    }
  }

  func play() {
    guard player.timeControlStatus != .playing else {
      return
    }
    player.play()
  }

  func pause() {
    guard player.timeControlStatus != .paused else {
      return
    }
    player.pause()
  }

  func seek(to position: CMTime, completion: @escaping Action) {
    player.seek(
      to: position,
      toleranceBefore: .zero,
      toleranceAfter: .zero,
      completionHandler: { _ in
        completion()
      }
    )
  }

  func set(isMuted: Bool) {
    player.isMuted = isMuted
  }

  func periodicCurrentTimeSignal(interval: TimeInterval) -> Signal<TimeInterval> {
    if let pipe = currentTimePipes[interval] {
      return pipe.signal
    }

    let pipe = SignalPipe<TimeInterval>()
    currentTimePipes[interval] = pipe

    player
      .addPeriodicCurrentTimeObserver(interval: interval) { pipe.send($0) }
      .dispose(in: playerObservers)

    return pipe.signal
  }

  private func setup(_ player: AVPlayer) {
    player.actionAtItemEnd = .pause
    player.automaticallyWaitsToMinimizeStalling = true

    configureObservers(for: player)
  }

  private func configureObservers(for player: AVPlayer) {
    weak var weakSelf = self

    observe(player, path: \.timeControlStatus) { _, timeControlStatus in
      weakSelf?.playbackStatusDidChange(timeControlStatus.playbackStatus)
    }.dispose(in: playerObservers)

    observe(player, path: \.status) { player, _ in
      let status = PlayerStatus(player: player, item: player.currentItem)
      weakSelf?.playerStatusDidChange(status)
    }.dispose(in: playerObservers)
  }

  private func configureObservers(for item: AVPlayerItem) {
    weak var weakSelf = self

    observe(item, path: \.status) { item, _ in
      guard let self = weakSelf, self.player.currentItem == item else { return }

      let status = PlayerStatus(player: self.player, item: item)
      self.playerStatusDidChange(status)
    }.dispose(in: itemObservers)

    observe(item, path: \.duration) { item, duration in
      guard let self = weakSelf, self.player.currentItem == item else { return }
      self.playerDurationDidChange(duration)
    }.dispose(in: itemObservers)

    NotificationCenter.default
      .observe(item, name: .AVPlayerItemDidPlayToEndTime) { item, _ in
        guard let self = weakSelf, self.player.currentItem == item else { return }

        self.playbackDidFinish(item)
      }.dispose(in: itemObservers)

    NotificationCenter.default
      .observe(item, name: .AVPlayerItemFailedToPlayToEndTime) { item, notification in
        guard let self = weakSelf, self.player.currentItem == item else { return }

        let nserror = notification.userInfo?[AVPlayerItemFailedToPlayToEndTimeErrorKey] as? NSError
        self.playbackDidFail(nserror.map(BasePlayerError.init) ?? UnknownPlayerError())
      }.dispose(in: itemObservers)
  }

  private func playbackStatusDidChange(_ status: PlaybackStatus) {
    playbackStatusPipe.send(status)
  }

  private func playerStatusDidChange(_ status: PlayerStatus) {
    playerStatusPipe.send(status)

    if status == .failed {
      let playerError = (player.error as? NSError).map(BasePlayerError.init)
      let itemError = (player.currentItem?.error as? NSError).map(BasePlayerError.init)

      playbackDidFail((playerError ?? itemError) ?? UnknownPlayerError())
    }
  }

  private func playerDurationDidChange(_ duration: CMTime) {
    duration.safeSeconds.map { playerDurationPipe.send($0.notNegative) }
  }

  private func playbackDidFail(_ error: PlayerError) {
    playerErrorPipe.send(error)
  }

  private func playbackDidFinish(_: AVPlayerItem) {
    playbackFinishPipe.send()
  }

  private func observe<Target: NSObject, Prop>(
    _ target: Target,
    path: KeyPath<Target, Prop>,
    onChange: @escaping (Target, Prop) -> Void
  ) -> Disposable {
    let observer = target.observe(path, options: [.new, .initial]) { object, _ in
      onChange(object, object[keyPath: path])
    }

    return Disposable {
      observer.invalidate()
    }
  }
}
