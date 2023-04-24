import BasePublic
import Foundation
import AVFoundation

public protocol Player {
  var signal: Signal<Event> { get }

  func set(data: PlayerData, config: PlaybackConfig)
  func play()
  func pause()
  func set(isMuted: Bool)
  func seek(to position: CMTime)
}

public enum Event {
  case bufferOver
  case error
  case videoOver
  case currentTimeUpdate(time: Int)
  case started
}

public enum PlayerData: Equatable {
  case video(Video)
  case stream(URL)

  public var playerItem: AVPlayerItem {
    switch self {
    case .video(let video):
      return AVPlayerItem(url: video.url)
    case .stream(let url):
      return AVPlayerItem(url: url)
    }
  }
}
