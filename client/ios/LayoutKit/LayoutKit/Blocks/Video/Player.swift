import AVFoundation
import BasePublic
import Foundation

public protocol Player {
  var signal: Signal<PlayerEvent> { get }

  func set(data: VideoData, config: PlaybackConfig)
  func play()
  func pause()
  func set(isMuted: Bool)
  func seek(to position: CMTime)
}

public enum PlayerEvent {
  case pause
  case buffering
  case play
  case end
  case fatal

  case currentTimeUpdate(_ ms: Int)
  case durationUpdate(_ duration: CMTime)
}
