import DivKitExtensions
import Foundation
import RiveRuntime

final class RiveContainerView: UIView {
  private var riveViewModel: RiveViewModel?
  private var riveView: RiveView? {
    didSet {
      if let riveView {
        oldValue?.removeFrom(self)

        addSubview(riveView)
        riveView.frame = bounds
      }
    }
  }

  private let fit: RiveFit
  private let alignment: RiveAlignment
  private let loop: RiveLoop

  init(
    fit: RiveDivCustomData.Fit,
    alignment: RiveDivCustomData.Alignment,
    loop: RiveDivCustomData.Loop
  ) {
    switch fit {
    case .cover: self.fit = .cover
    case .contain: self.fit = .contain
    case .fill: self.fit = .fill
    case .fitWidth: self.fit = .fitWidth
    case .fitHeight: self.fit = .fitHeight
    case .none: self.fit = .noFit
    case .scaleDown: self.fit = .scaleDown
    }

    switch alignment {
    case .center: self.alignment = .center
    case .topLeft: self.alignment = .topLeft
    case .topCenter: self.alignment = .topCenter
    case .topRight: self.alignment = .topRight
    case .centerLeft: self.alignment = .centerLeft
    case .centerRight: self.alignment = .centerRight
    case .bottomLeft: self.alignment = .bottomLeft
    case .bottomCenter: self.alignment = .bottomCenter
    case .bottomRight: self.alignment = .bottomRight
    }

    switch loop {
    case .oneShot: self.loop = .oneShot
    case .loop: self.loop = .loop
    case .pingPong: self.loop = .pingPong
    case .auto: self.loop = .autoLoop
    }
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    riveView?.frame = bounds
  }
}

extension RiveContainerView: AsyncSourceAnimatableView {
  func play() {
    riveViewModel?.play(loop: loop)
  }

  func setSourceAsync(_ source: AnimationSourceType) async {
    if let source = source as? RiveAnimationSourceType {
      switch source {
      case let .data(data):
        guard riveViewModel == nil else {
          riveViewModel?.reset()
          return
        }

        if let riveFile = try? RiveFile(byteArray: [UInt8](data)) {
          let model = RiveModel(riveFile: riveFile)
          riveViewModel = RiveViewModel(model, fit: fit, alignment: alignment)
          riveView = riveViewModel?.createRiveView()
        }
      }
    }
  }
}
