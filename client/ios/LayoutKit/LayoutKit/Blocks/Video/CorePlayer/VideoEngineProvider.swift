import AVFoundation

protocol VideoEngineProvider {
  var videoEngine: VideoEngine { get }
}

struct VideoEngine {
  enum EngineType {
    case avPlayer(AVPlayer)
  }

  let type: EngineType
}
