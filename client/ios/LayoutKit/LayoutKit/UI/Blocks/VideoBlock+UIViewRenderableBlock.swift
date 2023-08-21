import CoreMedia
import Foundation
import UIKit

import CommonCorePublic

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
  var visibleBoundsTrackingSubviews: [CommonCorePublic.VisibleBoundsTrackingView] {
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

  private lazy var player: Player? = {
    let player = playerFactory?.makePlayer(
      data: nil,
      config: nil
    )

    playerSignal = player?.signal.addObserver { [weak self] event in
      onMainThread {
        guard let self = self else { return }

        switch event {
        case let .currentTimeUpdate(time):
          self.model.elapsedTime?.setValue(Int(time), responder: self)
        case .end:
          self.observer?.elementStateChanged(self.state, forPath: self.model.path)
          self.model.endActions.perform(sendingFrom: self)
        case .buffering:
          self.model.bufferingActions.perform(sendingFrom: self)
        case .pause:
          self.model.pauseActions.perform(sendingFrom: self)
        case .fatal:
          self.model.fatalActions.perform(sendingFrom: self)
        case .play:
          self.model.resumeActions.perform(sendingFrom: self)
        }
      }
    }

    player.flatMap { videoView?.attach(player: $0) }

    return player
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

    if model.videoData != oldValue.videoData {
      player?.set(data: model.videoData, config: model.playbackConfig)
    }

    if model.playbackConfig.isMuted != oldValue.playbackConfig.isMuted {
      player?.set(isMuted: model.playbackConfig.isMuted)
    }

    if oldValue.elapsedTime != model.elapsedTime {
      model.elapsedTime.flatMap { player?.seek(to: .init(value: $0.wrappedValue)) }
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    videoView?.frame = bounds
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
