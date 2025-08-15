import AVFoundation

public enum PlayerStatus {
  case unknown
  case readyToPlay
  case failed
}

extension PlayerStatus {
  init(playerStatus: PlayerStatus, itemStatus: PlayerStatus?) {
    switch (playerStatus, itemStatus) {
    case (.readyToPlay, .readyToPlay):
      self = .readyToPlay
    case (.failed, _), (_, .failed):
      self = .failed
    case (.unknown, _), (_, .unknown):
      self = .unknown
    default:
      self = .unknown
    }
  }
}

extension PlayerStatus {
  init(player: AVPlayer, item: AVPlayerItem?) {
    self.init(
      playerStatus: player.status.asPlayerStatus,
      itemStatus: item?.status.asPlayerStatus
    )
  }
}

extension AVPlayer.Status {
  var asPlayerStatus: PlayerStatus {
    switch self {
    case .readyToPlay:
      return .readyToPlay
    case .failed:
      return .failed
    case .unknown:
      return .unknown
    @unknown default:
      assertionFailure()
      return .unknown
    }
  }
}

extension AVPlayerItem.Status {
  var asPlayerStatus: PlayerStatus {
    switch self {
    case .readyToPlay:
      return .readyToPlay
    case .failed:
      return .failed
    case .unknown:
      return .unknown
    @unknown default:
      assertionFailure()
      return .unknown
    }
  }
}
