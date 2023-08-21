import BasePublic
import Foundation
import CoreMedia

protocol CorePlayer: VideoEngineProvider {
  static func isMIMETypeSupported(_ mimeType: String) -> Bool

  var playerStatusDidChange: Signal<PlayerStatus> { get }
  var playbackStatusDidChange: Signal<PlaybackStatus> { get }
  var playbackDidFail: Signal<PlayerError> { get }
  var playbackDidFinish: Signal<Void> { get }

  func periodicCurrentTimeSignal(interval: TimeInterval) -> Signal<TimeInterval>

  func set(source: Video)

  func play()
  func pause()
  func seek(to position: CMTime)

  func set(isMuted: Bool)
}

extension CorePlayer {
  var staticScope: Self.Type {
    Self.self
  }
}
