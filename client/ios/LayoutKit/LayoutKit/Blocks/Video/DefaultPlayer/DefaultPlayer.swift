import AVFoundation
import BasePublic
import Foundation

final class DefaultPlayer: Player {
  private let eventPipe = SignalPipe<PlayerEvent>()
  var signal: Signal<PlayerEvent> {
    eventPipe.signal
  }

  private let player: CorePlayer
  private var context: SourceContext?

  private let playerObservers = AutodisposePool()
  private let playbackConfigObservers = AutodisposePool()

  init(player: CorePlayer? = nil) {
    self.player = player ?? CorePlayerImpl()
    configureObservers(for: self.player)
  }

  func set(data: VideoData, config: PlaybackConfig) {
    context = SourceContext(videoData: data, playbackConfig: config)
    guard let source = data.getSupportedVideo(player.staticScope.isMIMETypeSupported) else {
      assertionFailure("Empty source")
      eventPipe.send(.fatal)
      return
    }

    player.set(source: source)
    handle(config: config)
  }

  func play() {
    player.play()
  }

  func pause() {
    player.pause()
  }

  func set(isMuted: Bool) {
    player.set(isMuted: isMuted)
  }

  func seek(to position: CMTime) {
    player.seek(to: position)
  }

  private func handle(config: PlaybackConfig) {
    playbackConfigObservers.drain()

    if config.repeatable {
      player
        .playbackDidFinish
        .addObserver { [weak self] _ in
          self?.player.seek(to: .zero)
          self?.player.play()
        }
        .dispose(in: playbackConfigObservers)
    }

    player.set(isMuted: config.isMuted)
    config.autoPlay ? player.play() : player.pause()

    if config.startPosition != .zero {
      player.seek(to: config.startPosition)
    }
  }

  private func configureObservers(for player: CorePlayer) {
    weak var `self` = self

    player
      .playbackStatusDidChange
      .map { playbackStatus -> PlayerEvent in
        switch playbackStatus {
          case .playing:
            return .play
          case .paused:
            return .pause
          case .buffering:
            return .buffering
        }
      }
      .addObserver { self?.eventPipe.send($0) }
      .dispose(in: playerObservers)

    player
      .playbackDidFinish
      .addObserver { self?.eventPipe.send(.end) }
      .dispose(in: playerObservers)

    player
      .playbackDidFail
      .addObserver { _ in self?.eventPipe.send(.fatal) }
      .dispose(in: playerObservers)

    player
      .periodicCurrentTimeSignal(interval: 1)
      .addObserver { self?.eventPipe.send(.currentTimeUpdate(Int($0 * 1000))) }
      .dispose(in: playerObservers)
  }
}

extension DefaultPlayer: VideoEngineProvider {
  var videoEngine: VideoEngine {
    player.videoEngine
  }
}

private extension DefaultPlayer {
  struct SourceContext {
    var videoData: VideoData
    var playbackConfig: PlaybackConfig
  }
}

private extension VideoData {
  func getSupportedVideo(_ mimeTypeChecker: (String) -> Bool) -> Video? {
    videos.first { mimeTypeChecker($0.mimeType) }
  }
}
