import CoreMedia
import Foundation

/// The ``PlayerFactory`` protocol allows you to set a custom player factory that utilizes its own
/// engine for video playback. By conforming to this protocol, you can define your own video player
/// implementation, allowing you to use a different video playback engine other than the default one
/// provided by `AVFoundation`.
///
/// The ``PlayerFactory`` protocol requires the implementation of two methods:
/// - ``makePlayer(data:config:)``: This method should create and return a `Player` object based on
/// the provided `VideoData` and `PlaybackConfig`.
/// - ``makePlayerView()``: This method should create and return a `PlayerView` that visually
/// displays the video content.
///
/// By adopting this protocol, you gain the flexibility to use custom video playback solutions,
/// which can be beneficial when you have specific requirements or prefer alternative video handling
/// mechanisms. It allows you to integrate third-party video players or your own player
/// implementation seamlessly into your DivKit card.
public protocol PlayerFactory {
  /// Creates and returns a `Player` object based on the provided `VideoData` and `PlaybackConfig`.
  ///
  /// - Parameters:
  ///   - data: The `VideoData` containing information about the video.
  ///   - config: The `PlaybackConfig` representing the configuration options for video playback.
  ///
  /// - Returns: A `Player` object customized according to the given `VideoData` and
  /// `PlaybackConfig`.
  func makePlayer(data: VideoData?, config: PlaybackConfig?) -> Player

  /// Creates and returns a `PlayerView` that visually displays the video content.
  ///
  /// - Returns: A `PlayerView` used to display the video content.
  func makePlayerView() -> PlayerView
}

public struct PlaybackConfig: Equatable {
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

  public init(
    autoPlay: Bool,
    repeatable: Bool,
    isMuted: Bool,
    startPosition: CMTime,
    settingsPayload: [String: Any]
  ) {
    self.autoPlay = autoPlay
    self.repeatable = repeatable
    self.isMuted = isMuted
    self.startPosition = startPosition
    self.settingsPayload = settingsPayload
  }

  public static func ==(lhs: PlaybackConfig, rhs: PlaybackConfig) -> Bool {
    lhs.autoPlay == rhs.autoPlay &&
      lhs.repeatable == rhs.repeatable &&
      lhs.isMuted == rhs.isMuted &&
      lhs.startPosition == rhs.startPosition
  }
}
