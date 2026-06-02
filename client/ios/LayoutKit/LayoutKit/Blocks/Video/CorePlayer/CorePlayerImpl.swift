import AVFoundation
import Foundation
import VGSL

final class CorePlayerImpl: CorePlayer {
  private final class PlaybackRateManager {
    private enum State {
      case readyToSet
      case pending(Float)
    }

    private let player: AVPlayer

    private var state: State = .readyToSet

    private var pendingApply: Cancellable?

    private let stopRate: Float = 0

    private var defaultRate: Float {
      if #available(iOS 16.0, macOS 13.0, *) {
        player.defaultRate
      } else {
        1
      }
    }

    init(player: AVPlayer) {
      self.player = player
    }

    func cancelPendingApply() {
      pendingApply?.cancel()
      pendingApply = nil
    }

    func setRate(_ rate: Float) {
      guard rate != defaultRate,
            rate != stopRate else {
        cancelPendingApply()
        player.rate = rate
        state = .readyToSet
        return
      }

      switch state {
      case .readyToSet:
        cancelPendingApply()
        state = .pending(rate)
        pendingApply = after(0.05, onQueue: .main) { [weak self] in
          self?.flushPendingApply()
        }
      case let .pending(value):
        if handlePlayerRateChange(to: value) {
          setRate(rate)
          return
        }
        state = .pending(rate)
      }
    }

    @discardableResult
    func handlePlayerRateChange(to observedRate: Float) -> Bool {
      guard player.rate == observedRate else { return false }

      let deferredRate: Float? = if case let .pending(pendingRate) = state,
                                    pendingRate != observedRate {
        pendingRate
      } else {
        nil
      }

      cancelPendingApply()
      state = .readyToSet

      if let deferredRate {
        setRate(deferredRate)
      }

      return true
    }

    private func flushPendingApply() {
      guard case let .pending(rate) = state else { return }
      updateRate(rate)
    }

    private func updateRate(_ rate: Float) {
      pendingApply = nil
      player.rate = rate
      if #available(iOS 16.0, macOS 13.0, *) {
        player.defaultRate = rate
      }
    }
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
  private let playbackRateManager: PlaybackRateManager

  private var currentTimePipes = [TimeInterval: SignalPipe<TimeInterval>]()

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

  init(itemsProvider: PlayerItemsProvider) {
    let player = AVPlayer()
    self.player = player
    self.itemsProvider = itemsProvider
    self.playbackRateManager = PlaybackRateManager(player: player)

    setup(player)
  }

  static func isMIMETypeSupported(_ mimeType: String) -> Bool {
    AVURLAsset.isPlayableExtendedMIMEType(mimeType)
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
    playbackRateManager.cancelPendingApply()
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

  func set(playbackSpeed: Double) {
    playbackRateManager.setRate(Float(playbackSpeed))
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

  private func resetPlayerObservers() {
    playerObservers.drain()
  }

  private func resetItemObservers() {
    itemObservers.drain()
  }

  private func setup(_ player: AVPlayer) {
    player.actionAtItemEnd = .pause
    player.automaticallyWaitsToMinimizeStalling = true

    configureObservers(for: player)
  }

  private func configureObservers(for player: AVPlayer) {
    #if swift(>=6.2)
    weak let weakSelf = self
    #else
    weak var weakSelf = self
    #endif

    observe(player, path: \.timeControlStatus) { _, timeControlStatus in
      weakSelf?.playbackStatusDidChange(timeControlStatus.playbackStatus)
    }.dispose(in: playerObservers)

    observe(player, path: \.rate) { _, rate in
      weakSelf?.playbackSpeedDidChange(rate)
    }.dispose(in: playerObservers)

    observe(player, path: \.status) { player, _ in
      let status = PlayerStatus(player: player, item: player.currentItem)
      weakSelf?.playerStatusDidChange(status)
    }.dispose(in: playerObservers)
  }

  private func configureObservers(for item: AVPlayerItem) {
    #if swift(>=6.2)
    weak let weakSelf = self
    #else
    weak var weakSelf = self
    #endif

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
        self.playbackDidFail(nserror.map(BasePlayerError.init) ?? CustomPlayerError())
      }.dispose(in: itemObservers)
  }

  private func playbackStatusDidChange(_ status: PlaybackStatus) {
    playbackStatusPipe.send(status)
  }

  private func playbackSpeedDidChange(_ rate: Float) {
    playbackRateManager.handlePlayerRateChange(to: rate)
  }

  private func playerStatusDidChange(_ status: PlayerStatus) {
    playerStatusPipe.send(status)

    if status == .failed {
      let playerError = (player.error as? NSError).map(BasePlayerError.init)
      let itemError = (player.currentItem?.error as? NSError).map(BasePlayerError.init)

      playbackDidFail((playerError ?? itemError) ?? CustomPlayerError())
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
