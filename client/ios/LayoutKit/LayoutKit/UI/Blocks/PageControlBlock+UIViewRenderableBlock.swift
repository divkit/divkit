#if os(iOS)
import Foundation
import UIKit
import VGSL

extension PageControlBlock {
  public static func makeBlockView() -> BlockView { PageControlBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    (view as! PageControlBlockView).model = Model(
      state: state,
      layoutDirection: layoutDirection,
      configuration: configuration,
      source: weakify(self)
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is PageControlBlockView
  }
}

private struct Model: ReferenceEquatable {
  let state: PagerViewState
  let layoutDirection: UserInterfaceLayoutDirection
  let configuration: PageIndicatorConfiguration
  let source: Variable<AnyObject?>
}

private final class PageControlBlockView: BlockView, VisibleBoundsTrackingLeaf {
  var model: Model! {
    didSet {
      guard model != oldValue else { return }

      let isFirstConfiguration = oldValue == nil
      if isFirstConfiguration {
        addSubview(indicatorView)
      }

      // Animate indicator on user interaction and action animations
      configureIndicatorView(animated: model.state.animated)
    }
  }

  private let indicatorView = PageIndicatorView()

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  init() {
    super.init(frame: .zero)
    isUserInteractionEnabled = false
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    indicatorView.frame = bounds
  }

  private func configureIndicatorView(animated: Bool) {
    indicatorView.configuration = model.configuration
    indicatorView.numberOfPages = model.state.numberOfPages

    CATransaction.begin()
    CATransaction.setDisableActions(!animated)
    self.indicatorView.currentIndexPosition = model.state.currentPage
    CATransaction.commit()

    switch model.layoutDirection {
    case .leftToRight:
      indicatorView.transform = .identity
    case .rightToLeft:
      indicatorView.transform = CGAffineTransformMakeScale(-1.0, 1.0)
    @unknown default:
      assertionFailure("Unknown layoutDirection (UserInterfaceLayoutDirection)")
    }
  }
}
#endif
