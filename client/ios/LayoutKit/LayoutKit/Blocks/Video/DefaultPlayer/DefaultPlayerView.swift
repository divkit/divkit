import AVFoundation
import UIKit

public final class DefaultPlayerView: UIView, PlayerView {
  public func attach(player: Player) {
    guard let engine = (player as? VideoEngineProvider)?.videoEngine else {
      assertionFailure("Can't use DefaultPlayerView with not with DefaultPlayer")
      return
    }

    switch engine.type {
      case let .avPlayer(player):
        player.bind(to: playerLayer)
    }
  }

  private var playerLayer: AVPlayerLayer { layer as! AVPlayerLayer }

  public override static var layerClass: AnyClass { AVPlayerLayer.self }
}

extension AVPlayer {
  fileprivate func bind(to layer: AVPlayerLayer) {
    let tmpRate = rate
    rate = 0
    layer.player = self
    rate = tmpRate
  }
}
