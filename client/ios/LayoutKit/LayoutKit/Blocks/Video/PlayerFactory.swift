import Foundation
import CoreMedia

public protocol PlayerFactory {
  func makePlayer(data: VideoData?, config: PlaybackConfig?) -> Player
  func makePlayerView() -> PlayerView
}

public struct PlaybackConfig: Equatable {
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

  public static let `default` = PlaybackConfig(
    autoPlay: true,
    repeatable: false,
    isMuted: false,
    startPosition: .zero,
    settingsPayload: [:]
  )

  public static func ==(lhs: PlaybackConfig, rhs: PlaybackConfig) -> Bool {
    lhs.autoPlay == rhs.autoPlay &&
      lhs.repeatable == rhs.repeatable &&
      lhs.isMuted == rhs.isMuted &&
      lhs.startPosition == rhs.startPosition
  }
}
