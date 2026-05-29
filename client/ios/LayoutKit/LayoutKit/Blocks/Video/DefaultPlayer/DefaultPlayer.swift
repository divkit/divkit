#if os(iOS)
import AVFoundation
import Foundation
import VGSL

final class DefaultPlayer: Player {
  private let eventPipe = SignalPipe<PlayerEvent>()
  private let player: CorePlayer
  private var context: SourceContext?

  private let playerObservers = AutodisposePool()
  private let playbackSettingsObservers = AutodisposePool()

  var signal: Signal<PlayerEvent> {
    eventPipe.signal
  }

  init(itemsProvider: PlayerItemsProvider) {
    self.player = CorePlayerImpl(itemsProvider: itemsProvider)
    configureObservers(for: self.player)
  }

  func set(
    data: VideoData,
    config: PlaybackConfig
  ) {
    let settings = config.settings
    let timeline = config.timeline

    context = SourceContext(
      videoData: data,
      settings: settings,
      timeline: timeline
    )
    guard let source = data.getSupportedVideo(player.staticScope.isMIMETypeSupported) else {
      eventPipe.send(
        .fatal(
          CustomPlayerError(errorDescription: "Unsupported MIME Type") as PlayerError
        )
      )
      return
    }

    player.set(source: source)
    configure(settings)

    if settings.autoPlay {
      player.play()
    } else {
      player.pause()
    }

    if timeline.startPosition != .zero {
      player.seek(to: timeline.startPosition) { [weak self] in
        guard let self, settings.autoPlay else { return }
        self.player.set(playbackSpeed: settings.speed)
      }
    }
  }

  func play() {
    context.map { player.set(playbackSpeed: $0.settings.speed) }
    player.play()
  }

  func pause() {
    player.pause()
  }

  /// Deprecated. Use ``configure(_:)`` instead.
  func set(isMuted: Bool) {
    player.set(isMuted: isMuted)
  }

  func configure(_ settings: PlaybackConfig.VideoPlaybackSettings) {
    playbackSettingsObservers.drain()

    if context?.settings != settings {
      context?.settings = settings
    }

    if settings.repeatable {
      player
        .playbackDidFinish
        .addObserver { [weak self] _ in
          guard let self else { return }
          self.player.seek(to: .zero) {
            self.player.play()
          }
        }
        .dispose(in: playbackSettingsObservers)
    }

    player.set(isMuted: settings.isMuted)
    player.set(playbackSpeed: settings.speed)
  }

  func seek(to position: CMTime) {
    player.seek(to: position)
  }

  private func configureObservers(for player: CorePlayer) {
    #if swift(>=6.2)
    weak let weakSelf = self
    #else
    weak var weakSelf = self
    #endif

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
      .addObserver { error in weakSelf?.eventPipe.send(.fatal(error)) }
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
    var settings: PlaybackConfig.VideoPlaybackSettings
    var timeline: PlaybackConfig.VideoPlaybackTimeline
  }
}

extension VideoData {
  fileprivate func getSupportedVideo(_ mimeTypeChecker: (String) -> Bool) -> Video? {
    videos.first { mimeTypeChecker($0.mimeType) }
  }
}
#endif
