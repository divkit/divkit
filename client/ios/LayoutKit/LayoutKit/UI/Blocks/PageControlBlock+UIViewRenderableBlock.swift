import Foundation
import UIKit

import CommonCorePublic

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
  let configuration: PageIndicatorConfiguration
  let source: Variable<AnyObject?>
}

private final class PageControlBlockView: BlockView, VisibleBoundsTrackingLeaf {
  private let indicatorView = PageIndicatorView()

  var model: Model! {
    didSet {
      guard model != oldValue else { return }

      let isFirstConfiguration = oldValue == nil
      if isFirstConfiguration {
        addSubview(indicatorView)
      }
      configureIndicatorView(animated: !isFirstConfiguration)
    }
  }

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

    let currentPage = model.state.currentPage
    if animated {
      UIView.animate(withDuration: 0.2) { [weak self] in
        self?.indicatorView.currentIndexPosition = currentPage
      }
    } else {
      indicatorView.currentIndexPosition = currentPage
    }
  }
}
