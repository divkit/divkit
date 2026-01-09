#if os(iOS)
import AVFoundation
import Foundation
import VGSL

final class DefaultPlayer: Player {
  private let eventPipe = SignalPipe<PlayerEvent>()
  private let player: CorePlayer
  private var context: SourceContext?

  private let playerObservers = AutodisposePool()
  private let playbackConfigObservers = AutodisposePool()

  var signal: Signal<PlayerEvent> {
    eventPipe.signal
  }

  init(itemsProvider: PlayerItemsProvider) {
    self.player = CorePlayerImpl(itemsProvider: itemsProvider)
    configureObservers(for: self.player)
  }

  func set(data: VideoData, config: PlaybackConfig) {
    context = SourceContext(videoData: data, playbackConfig: config)
    guard let source = data.getSupportedVideo(player.staticScope.isMIMETypeSupported) else {
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
          self?.player.seek(
            to: .zero,
            completion: { self?.player.play() }
          )
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
    weak var weakSelf = self

    player
      .playbackStatusDidChange
      .map { playbackStatus -> PlayerEvent in
        switch playbackStatus {
        case .playing:
          .play
        case .paused:
          .pause
        case .buffering:
          .buffering
        }
      }
      .addObserver { weakSelf?.eventPipe.send($0) }
      .dispose(in: playerObservers)

    player
      .playbackDidFinish
      .addObserver { weakSelf?.eventPipe.send(.end) }
      .dispose(in: playerObservers)

    player
      .playbackDidFail
      .addObserver { _ in weakSelf?.eventPipe.send(.fatal) }
      .dispose(in: playerObservers)

    player
      .periodicCurrentTimeSignal(interval: 1)
      .addObserver { weakSelf?.eventPipe.send(.currentTimeUpdate(Int($0 * 1000))) }
      .dispose(in: playerObservers)

    player
      .playerDurationDidChange
      .addObserver { weakSelf?.eventPipe.send(.durationUpdate(Int($0 * 1000))) }
      .dispose(in: playerObservers)
  }
}

extension DefaultPlayer: VideoEngineProvider {
  var videoEngine: VideoEngine {
    player.videoEngine
  }
}

extension DefaultPlayer {
  fileprivate struct SourceContext {
    var videoData: VideoData
    var playbackConfig: PlaybackConfig
  }
}

extension VideoData {
  fileprivate func getSupportedVideo(_ mimeTypeChecker: (String) -> Bool) -> Video? {
    videos.first { mimeTypeChecker($0.mimeType) }
  }
}
#endif
