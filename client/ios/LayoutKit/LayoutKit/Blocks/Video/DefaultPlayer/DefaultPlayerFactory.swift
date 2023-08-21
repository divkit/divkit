import AVFoundation
import BasePublic
import Foundation
import UIKit

public final class DefaultPlayerFactory: PlayerFactory {
  public func makePlayer(data: VideoData?, config: PlaybackConfig?) -> Player {
    let player = DefaultPlayer()
    guard let config, let data else { return player }
    player.set(data: data, config: config)
    return player
  }

  public func makePlayerView() -> PlayerView {
    DefaultPlayerView()
  }

  public init() {}
}
