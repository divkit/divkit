import BasePublic
import CoreMedia
import Foundation

protocol CorePlayer: VideoEngineProvider {
  static func isMIMETypeSupported(_ mimeType: String) -> Bool

  var playerStatusDidChange: Signal<PlayerStatus> { get }
  var playerDurationDidChange: Signal<TimeInterval> { get }
  var playbackStatusDidChange: Signal<PlaybackStatus> { get }
  var playbackDidFail: Signal<PlayerError> { get }
  var playbackDidFinish: Signal<Void> { get }

  func periodicCurrentTimeSignal(interval: TimeInterval) -> Signal<TimeInterval>

  func set(source: Video)

  func play()
  func pause()
  func seek(to position: CMTime, completion: @escaping Action)

  func set(isMuted: Bool)
}

extension CorePlayer {
  var staticScope: Self.Type {
    Self.self
  }

  func seek(to position: CMTime) {
    seek(to: position, completion: {})
  }
}
