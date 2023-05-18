import AVFoundation

extension AVPlayer.TimeControlStatus {
  var playbackStatus: PlaybackStatus {
    switch self {
      case .paused:
        return .paused
      case .waitingToPlayAtSpecifiedRate:
        return .buffering
      case .playing:
        return .playing
      @unknown default:
        assertionFailure()
        return .paused
    }
  }
}
