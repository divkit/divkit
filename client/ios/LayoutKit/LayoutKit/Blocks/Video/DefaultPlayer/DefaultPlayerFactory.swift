import AVFoundation
import BasePublic
import Foundation
import NetworkingPublic
import UIKit

public final class DefaultPlayerFactory: PlayerFactory {
  private let itemsProvider: PlayerItemsProvider

  public func makePlayer(data: VideoData?, config: PlaybackConfig?) -> Player {
    let player = DefaultPlayer(itemsProvider: itemsProvider)
    guard let config, let data else { return player }
    player.set(data: data, config: config)
    return player
  }

  public func makePlayerView() -> PlayerView {
    DefaultPlayerView()
  }

  public init(itemsProvider: PlayerItemsProvider = DefaultPlayerItemsProvider()) {
    self.itemsProvider = itemsProvider
  }
}
