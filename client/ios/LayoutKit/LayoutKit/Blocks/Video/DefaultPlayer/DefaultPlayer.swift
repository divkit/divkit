import AVFoundation
import BasePublic
import Foundation

public final class DefaultPlayer: Player {
  let player = AVQueuePlayer()
  private var currentItem: AVPlayerItem? {
    didSet {
      player.replaceCurrentItem(with: currentItem)
    }
  }

  private var playerLooper: AVPlayerLooper?
  private let eventPipe = SignalPipe<Event>()
  private var statusObserving: NSKeyValueObservation?
  private var config: PlaybackConfig?

  public func set(data: PlayerData, config: PlaybackConfig) {
    self.config = config
    configureObservers()
    currentItem = data.playerItem

    if case .video = data {
      if config.repeatable {
        currentItem.flatMap { playerLooper = AVPlayerLooper(player: player, templateItem: $0) }
      } else {
        playerLooper = nil
      }
    }
    player.isMuted = config.isMuted
  }

  private func configureObservers() {
    player.addPeriodicTimeObserver(
      forInterval: CMTime(seconds: 1, preferredTimescale: 1),
      queue: .main
    ) { [weak self] time in
      guard let self = self else { return }
      self.eventPipe.send(.currentTimeUpdate(time: Int(time.seconds)))
    }

    NotificationCenter.default
      .addObserver(
        self,
        selector: #selector(playerDidFinishPlaying),
        name: .AVPlayerItemDidPlayToEndTime,
        object: player.currentItem
      )

    statusObserving = player.observe(\.status) { [weak self] player, _ in
      switch player.status {
      case .readyToPlay:
        player.seek(
          to: self?.config?.startPosition ?? .zero,
          toleranceBefore: .zero,
          toleranceAfter: .zero
        )
        if self?.config?.autoPlay == true {
          player.play()
        }
      case .failed:
        self?.eventPipe.send(.error)
      case .unknown:
        break
      default:
        break
      }
    }
  }

  @objc func playerDidFinishPlaying(notification _: NSNotification) {
    if config?.repeatable == false {
      eventPipe.send(.videoOver)
    }
  }

  public var signal: Signal<Event> {
    eventPipe.signal
  }

  public func play() {
    player.play()
  }

  public func pause() {
    player.pause()
  }

  public func set(isMuted: Bool) {
    player.isMuted = isMuted
  }

  public func seek(to position: CMTime) {
    player.seek(
      to: position,
      toleranceBefore: .zero,
      toleranceAfter: .zero
    )
  }
}
