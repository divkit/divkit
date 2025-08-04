#if os(iOS)
import AVFoundation
import UIKit

public final class DefaultPlayerView: UIView, PlayerView {
  public override static var layerClass: AnyClass { AVPlayerLayer.self }

  public var videoRatio: CGFloat? {
    guard let asset = playerLayer.player?.currentItem?.asset,
          let videoSize = asset.tracks(
            withMediaType: .video
          ).first?.naturalSize
    else {
      return nil
    }

    return videoSize.width / videoSize.height
  }

  private var playerLayer: AVPlayerLayer { layer as! AVPlayerLayer }

  public func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}

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

  public func set(scale: VideoScale) {
    if let videoGravity = scale.videoGravity {
      playerLayer.videoGravity = videoGravity
    }
  }

}

extension AVPlayer {
  fileprivate func bind(to layer: AVPlayerLayer) {
    let tmpRate = rate
    rate = 0
    layer.player = self
    rate = tmpRate
  }
}

extension VideoScale {
  fileprivate var videoGravity: AVLayerVideoGravity? {
    switch self {
    case .fill:
      .resizeAspectFill
    case .noScale:
      nil
    case .fit:
      .resizeAspect
    }
  }
}
#endif
