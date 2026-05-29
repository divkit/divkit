import CoreMedia
import Foundation

/// The ``PlayerFactory`` protocol allows you to set a custom player factory that utilizes its own
/// engine for video playback. By conforming to this protocol, you can define your own video player
/// implementation, allowing you to use a different video playback engine other than the default one
/// provided by `AVFoundation`.
///
/// The ``PlayerFactory`` protocol requires the implementation of two methods:
/// - ``makePlayer(data:config:)``: This method should create and return a `Player` object based on
/// the provided ``VideoData`` and ``PlaybackConfig``.
/// - ``makePlayerView()``: This method should create and return a `PlayerView` that visually
/// displays the video content.
///
/// By adopting this protocol, you gain the flexibility to use custom video playback solutions,
/// which can be beneficial when you have specific requirements or prefer alternative video handling
/// mechanisms. It allows you to integrate third-party video players or your own player
/// implementation seamlessly into your DivKit card.
public protocol PlayerFactory {
  /// Creates and returns a `Player` object based on the provided ``VideoData`` and
  /// ``PlaybackConfig``.
  ///
  /// - Parameters:
  ///   - data: The `VideoData` containing information about the video.
  ///   - config: The ``PlaybackConfig`` representing the configuration options for video playback.
  ///
  /// - Returns: A `Player` object customized according to the given ``VideoData`` and
  /// ``PlaybackConfig``.
  func makePlayer(data: VideoData?, config: PlaybackConfig?) -> Player

  /// Creates and returns a `PlayerView` that visually displays the video content.
  ///
  /// - Returns: A `PlayerView` used to display the video content.
  func makePlayerView() -> PlayerView
}

public struct PlaybackConfig: Equatable {
  /// Options that control how the player engine behaves. Intended to change rarely compared to
  /// timeline.
  public struct VideoPlaybackSettings: Equatable {
    public static let `default` = VideoPlaybackSettings(
      autoPlay: true,
      repeatable: false,
      isMuted: false,
      settingsPayload: [:],
      speed: 1.0
    )

    public var autoPlay: Bool
    public var repeatable: Bool
    public var isMuted: Bool
    public var settingsPayload: [String: Any]
    public var speed: Double

    public init(
      autoPlay: Bool,
      repeatable: Bool,
      isMuted: Bool,
      settingsPayload: [String: Any],
      speed: Double
    ) {
      self.autoPlay = autoPlay
      self.repeatable = repeatable
      self.isMuted = isMuted
      self.settingsPayload = settingsPayload
      self.speed = speed
    }

    public static func ==(lhs: VideoPlaybackSettings, rhs: VideoPlaybackSettings) -> Bool {
      lhs.autoPlay == rhs.autoPlay &&
        lhs.repeatable == rhs.repeatable &&
        lhs.isMuted == rhs.isMuted &&
        lhs.speed == rhs.speed &&
        NSDictionary(dictionary: lhs.settingsPayload).isEqual(to: rhs.settingsPayload)
    }
  }

  /// Time axis parameters for video playback (e.g. resolved start position). Separate from
  /// ``VideoPlaybackSettings`` so UI can diff stable engine options without churn from timeline
  /// updates.
  public struct VideoPlaybackTimeline: Equatable {
    public static let zero = VideoPlaybackTimeline(startPosition: .zero)

    public var startPosition: CMTime

    public init(startPosition: CMTime) {
      self.startPosition = startPosition
    }
  }

  public static let `default` = PlaybackConfig(
    autoPlay: true,
    repeatable: false,
    isMuted: false,
    startPosition: .zero,
    settingsPayload: [:]
  )

  public let autoPlay: Bool
  public let repeatable: Bool
  public let isMuted: Bool
  public let startPosition: CMTime
  public let settingsPayload: [String: Any]
  public let speed: Double

  public var settings: VideoPlaybackSettings {
    VideoPlaybackSettings(
      autoPlay: autoPlay,
      repeatable: repeatable,
      isMuted: isMuted,
      settingsPayload: settingsPayload,
      speed: speed
    )
  }

  public var timeline: VideoPlaybackTimeline {
    VideoPlaybackTimeline(startPosition: startPosition)
  }

  public init(
    autoPlay: Bool,
    repeatable: Bool,
    isMuted: Bool,
    startPosition: CMTime,
    settingsPayload: [String: Any],
    speed: Double = 1.0
  ) {
    self.autoPlay = autoPlay
    self.repeatable = repeatable
    self.isMuted = isMuted
    self.startPosition = startPosition
    self.settingsPayload = settingsPayload
    self.speed = speed
  }

  public static func ==(lhs: PlaybackConfig, rhs: PlaybackConfig) -> Bool {
    lhs.settings == rhs.settings && lhs.timeline == rhs.timeline
  }
}
