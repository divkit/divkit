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
    videoView.usePlayerPool = usePlayerPool ?? false
    videoView.autoplayAllowed.source = autoplayAllowed.currentAndNewValues
    if videoView.videoAssetHolder?.url != videoAssetHolder.url {
      videoView.videoAssetHolder = videoAssetHolder
    }
    videoView.preview = preview
  }
}

private final class VideoBlockLegacyView: BlockView {
  override static var layerClass: AnyClass { AVPlayerLayer.self }

  var usePlayerPool = false

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

  var preview: ImageHolder? {
    didSet {
      updatePreview()
    }
  }

  let autoplayAllowed: ObservableVariableConnection<Bool>

  let effectiveBackgroundColor: UIColor? = nil

  private let disposePool = AutodisposePool()
  private var avPlayer: AVPlayer?
  private var playerAsset: AVAsset?
  private let previewImageView = UIImageView()
  private var visibilityUpdateWorkItem: DispatchWorkItem?
  private var pendingVisibilityIsVisible: Bool?

  private var playerLayer: AVPlayerLayer { layer as! AVPlayerLayer }

  init() {
    autoplayAllowed = .init(initialValue: false)
    super.init(frame: .zero)

    previewImageView.contentMode = .scaleAspectFill
    previewImageView.clipsToBounds = true
    addSubview(previewImageView)

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
    playerLayer.videoGravity = .resizeAspectFill
    playerLayer.addObserver(
      self,
      forKeyPath: "readyForDisplay",
      options: [.new],
      context: nil
    )
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
    playerLayer.removeObserver(self, forKeyPath: "readyForDisplay")
    visibilityUpdateWorkItem?.cancel()
    detachPlayer()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    previewImageView.frame = bounds
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if window == nil {
      detachPlayer()
    }
  }

  override func observeValue(
    forKeyPath keyPath: String?,
    of object: Any?,
    change _: [NSKeyValueChangeKey: Any]?,
    context _: UnsafeMutableRawPointer?
  ) {
    if keyPath == "readyForDisplay", object as? AVPlayerLayer === playerLayer {
      if playerLayer.isReadyForDisplay {
        runWithDelay { [self] in
          updateVisibility()
        }
      }
      updateVisibility()
      return
    }
  }

  func onVisibleBoundsChanged(from _: CGRect, to: CGRect) {
    guard usePlayerPool else {
      return
    }
    let isVisible = !to.isEmpty
    pendingVisibilityIsVisible = isVisible

    visibilityUpdateWorkItem?.cancel()
    let workItem = DispatchWorkItem { [weak self] in
      guard let self else { return }
      guard self.pendingVisibilityIsVisible == isVisible else { return }

      if isVisible {
        guard self.window != nil else { return }
        if self.avPlayer == nil {
          self.attachPlayer()
        }
        self.resumePlayer()
      } else {
        self.detachPlayer()
      }
    }
    visibilityUpdateWorkItem = workItem
    DispatchQueue.main.asyncAfter(deadline: .now() + visibilityThrottleInterval, execute: workItem)
  }

  private func updateVisibility() {
    guard let player = avPlayer,
          player.currentItem?.url == videoAssetHolder?.url
    else { return }
    previewImageView.isHidden = playerLayer.isReadyForDisplay
  }

  private func runWithDelay(_ action: @escaping Action) {
    let videoUrl = videoAssetHolder?.url
    after(previewHideDelay) { [self] in
      guard videoUrl == videoAssetHolder?.url else {
        return
      }
      action()
    }
  }

  private func updatePreview() {
    previewImageView.image = preview?.image ?? preview?.placeholder?.toImageHolder().image
    updateVisibility()

    let videoUrl = videoAssetHolder?.url
    preview?.requestImageWithCompletion { [weak self] image in
      guard let self, videoAssetHolder.url == videoUrl else { return }
      previewImageView.image = image
    }
  }

  private func configurePlayer(with playerItem: AVPlayerItem) {
    onMainThreadAsync { [self] in
      playerAsset = playerItem.asset
      if !usePlayerPool {
        avPlayer = AVPlayer()
        playerLayer.player = avPlayer
      }
      if let player = avPlayer {
        player.replaceCurrentItem(with: AVPlayerItem(asset: playerItem.asset))
        resumePlayer()
      }
    }
  }

  private func attachPlayer() {
    guard avPlayer == nil else { return }
    let player = AVPlayerPool.shared.acquire()
    avPlayer = player
    playerLayer.player = player

    if let asset = playerAsset {
      player.replaceCurrentItem(with: AVPlayerItem(asset: asset))
    }
  }

  private func detachPlayer() {
    guard usePlayerPool, let player = avPlayer else { return }
    playerLayer.player = nil
    AVPlayerPool.shared.release(player)
    avPlayer = nil
    previewImageView.isHidden = false
  }

  @objc private func resumePlayer() {
    if autoplayAllowed.value {
      avPlayer?.play()
    }
  }

  @objc private func playerItemDidReachEnd(notification: Notification) {
    if let player = avPlayer, (notification.object as? AVPlayerItem) === player.currentItem {
      player.seek(to: .zero)
      resumePlayer()
    }
  }
}

private final class AVPlayerPool {
  static let shared = AVPlayerPool()

  private var players: [AVPlayer] = []

  private var activeCount = 0

  func acquire() -> AVPlayer {
    players.popLast() ?? AVPlayer()
  }

  func release(_ player: AVPlayer) {
    player.pause()
    player.replaceCurrentItem(with: nil)
    players.append(player)
  }
}

extension AVPlayerItem {
  var url: URL? {
    (asset as? AVURLAsset)?.url
  }
}

private let visibilityThrottleInterval: TimeInterval = 0.2
private let previewHideDelay: TimeInterval = 0.05

#endif
