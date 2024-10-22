import CoreMedia
import Foundation
import UIKit

import VGSL

extension VideoBlock {
  public static func makeBlockView() -> BlockView {
    VideoBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is VideoBlockView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let videoView = view as! VideoBlockView
    videoView.playerFactory = playerFactory
    videoView.configure(with: model)
    videoView.state = state
    videoView.observer = observer
  }
}

private final class VideoBlockView: BlockView, VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [videoView].compactMap { $0 }
  }

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  private var model: VideoBlockViewModel = .zero
  private var playerSignal: Disposable?
  private var previousTime: Int = 0

  private lazy var player: Player? = {
    let player = playerFactory?.makePlayer(
      data: model.videoData,
      config: model.playbackConfig
    )

    playerSignal = player?.signal.addObserver { [weak self] event in
      onMainThread {
        guard let self else { return }

        switch event {
        case let .currentTimeUpdate(time):
          self.model.elapsedTime?.value = Int(time)
          self.previousTime = time
        case .end:
          self.observer?.elementStateChanged(self.state, forPath: self.model.path)
          self.model.endActions.perform(sendingFrom: self)
        case .buffering:
          self.model.bufferingActions.perform(sendingFrom: self)
        case .pause:
          self.observer?.elementStateChanged(
            VideoBlockViewState(state: .paused),
            forPath: self.model.path
          )
          self.model.pauseActions.perform(sendingFrom: self)
        case .fatal:
          self.model.fatalActions.perform(sendingFrom: self)
        case .play:
          self.observer?.elementStateChanged(
            VideoBlockViewState(state: .playing),
            forPath: self.model.path
          )
          self.model.resumeActions.perform(sendingFrom: self)
          self.preview.isHidden = true
        case let .durationUpdate(duration):
          self.model.duration?.value = duration
        }
      }
    }

    player.flatMap { videoView?.attach(player: $0) }

    return player
  }()

  private lazy var preview: UIImageView = {
    let view = UIImageView(image: nil)
    view.contentMode = .scaleAspectFit
    view.backgroundColor = .black
    videoView.map { $0.addSubview(view) }
    return view
  }()

  private lazy var videoView: PlayerView? = {
    let view = playerFactory?.makePlayerView()
    view.flatMap { addSubview($0) }
    return view
  }()

  var state: VideoBlockViewState = .init(state: .playing) {
    didSet {
      guard oldValue != state else { return }
      switch state.state {
      case .playing:
        player?.play()
        preview.isHidden = true
      case .paused:
        player?.pause()
      }
    }
  }

  weak var observer: ElementStateObserver?
  var playerFactory: PlayerFactory?
  var effectiveBackgroundColor: UIColor?

  func configure(with model: VideoBlockViewModel) {
    let oldValue = self.model
    self.model = model

    if model.scale != oldValue.scale {
      videoView?.set(scale: model.scale)
    }

    if model.videoData != oldValue.videoData {
      player?.set(data: model.videoData, config: model.playbackConfig)
    }

    if model.playbackConfig.isMuted != oldValue.playbackConfig.isMuted {
      player?.set(isMuted: model.playbackConfig.isMuted)
    }

    preview.image = model.preview

    if let elapsedTime = model.elapsedTime?.value,
       elapsedTime != previousTime {
      player?.seek(to: CMTime(value: elapsedTime))
      previousTime = elapsedTime
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    videoView?.frame = bounds
    preview.frame = adjustPreviewFrame()
  }

  private func adjustPreviewFrame() -> CGRect {
    guard let videoView,
          let videoRatio = videoView.videoRatio else {
      return videoView?.frame ?? .zero
    }

    let containerSize = videoView.bounds.size
    let containerRatio = containerSize.width / containerSize.height

    if videoRatio > containerRatio {
      let height = containerSize.width / videoRatio
      let yOffset = (containerSize.height - height) / 2.0
      return CGRect(
        x: 0.0,
        y: yOffset,
        width: containerSize.width,
        height: height
      )
    } else {
      let width = containerSize.height * videoRatio
      let xOffset = (containerSize.width - width) / 2.0
      return CGRect(
        x: xOffset,
        y: 0.0,
        width: width,
        height: containerSize.height
      )
    }
  }

  deinit {
    player?.pause()
  }
}

extension VideoBlockViewModel {
  fileprivate static let zero: Self = VideoBlockViewModel(
    videoData: VideoData(videos: []),
    playbackConfig: .default,
    path: .init("video")
  )
}

extension CGSize {
  fileprivate var area: CGFloat {
    width * height
  }
}
