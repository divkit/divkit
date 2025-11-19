#if os(iOS)
import AVFoundation
import Foundation
import UIKit
import VGSL

extension VideoBlockLegacy {
  public static func makeBlockView() -> BlockView {
    VideoBlockLegacyView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is VideoBlockLegacyView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let videoView = view as! VideoBlockLegacyView
    videoView.autoplayAllowed.source = autoplayAllowed.currentAndNewValues
    if videoView.videoAssetHolder?.url != videoAssetHolder.url {
      videoView.videoAssetHolder = videoAssetHolder
    }
  }
}

private final class VideoBlockLegacyView: BlockView {
  override static var layerClass: AnyClass { AVPlayerLayer.self }

  var videoAssetHolder: VideoBlockLegacy.VideoAssetHolder! {
    didSet {
      let actualURL = videoAssetHolder.url
      videoAssetHolder.playerItem.map { (actualURL, $0) }.resolved { [weak self] url, playerItem in
        if self?.videoAssetHolder.url == url {
          self?.configurePlayer(with: playerItem)
        }
      }
    }
  }

  let autoplayAllowed: ObservableVariableConnection<Bool>

  let effectiveBackgroundColor: UIColor? = nil

  private let disposePool = AutodisposePool()
  private let avPlayer = AVPlayer()

  private var playerLayer: AVPlayerLayer { layer as! AVPlayerLayer }

  init() {
    autoplayAllowed = .init(initialValue: false)
    super.init(frame: .zero)
    autoplayAllowed.target.newValues.skipRepeats().addObserver { _ in
      self.resumePlayer()
    }.dispose(in: disposePool)
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(resumePlayer),
      name: UIApplication.willEnterForegroundNotification,
      object: nil
    )
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(playerItemDidReachEnd),
      name: AVPlayerItem.didPlayToEndTimeNotification,
      object: nil
    )
    playerLayer.player = avPlayer
    playerLayer.videoGravity = .resizeAspectFill
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
  }

  func onVisibleBoundsChanged(from _: CGRect, to _: CGRect) {}

  private func configurePlayer(with playerItem: AVPlayerItem) {
    avPlayer.replaceCurrentItem(with: playerItem)
    resumePlayer()
  }

  @objc private func resumePlayer() {
    if autoplayAllowed.value {
      avPlayer.play()
    }
  }

  @objc private func playerItemDidReachEnd(notification: Notification) {
    if (notification.object as? AVPlayerItem) === avPlayer.currentItem {
      avPlayer.seek(to: .zero)
      resumePlayer()
    }
  }
}
#endif
