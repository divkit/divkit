import AVFoundation
import Foundation
import VGSL

public protocol Player {
  var signal: Signal<PlayerEvent> { get }

  func set(data: VideoData, config: PlaybackConfig)
  func play()
  func pause()

  /// Deprecated. Use ``configure(_:)`` instead.
  func set(isMuted: Bool)
  func configure(_ settings: PlaybackConfig.VideoPlaybackSettings)
  func seek(to position: CMTime)
}

extension Player {
  /// Default no-op implementation provided for source compatibility.
  /// Override to apply ``PlaybackConfig/VideoPlaybackSettings`` to your player engine
  /// (mute, speed, loop, autoplay, custom payload) without recreating the player.
  /// If not overridden, live updates to these settings when the video source has not changed
  /// (e.g. mute or speed changes) will have no effect.
  public func configure(_ settings: PlaybackConfig.VideoPlaybackSettings) {
    set(isMuted: settings.isMuted)
  }
}

public enum PlayerEvent {
  case pause
  case buffering
  case play
  case end
  case fatal(PlayerError)

  case currentTimeUpdate(_ ms: Int)
  case durationUpdate(_ ms: Int)
}
