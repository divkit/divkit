import AVFoundation
import Foundation
import VGSL

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
  case durationUpdate(_ ms: Int)
}
