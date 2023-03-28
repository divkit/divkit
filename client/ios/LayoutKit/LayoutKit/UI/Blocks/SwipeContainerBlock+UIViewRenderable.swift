

import CoreGraphics
import Foundation
import UIKit

import CommonCorePublic
import LayoutKitInterface

extension SwipeContainerBlock {
  public static func makeBlockView() -> BlockView { SwipeContainerBlockView() }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is SwipeContainerBlockView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! SwipeContainerBlockView
    view.configure(
      model: SwipeContainerBlockView.Model(
        child: child,
        state: state,
        path: path,
        swipeOutActions: swipeOutActions
      ),
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class SwipeContainerBlockView: BlockView, VisibleBoundsTrackingContainer {
  struct Model {
    let child: Block
    let state: SwipeContainerBlock.State
    let path: UIElementPath
    let swipeOutActions: [UserInterfaceAction]
  }

  private let swipeContainerView = SwipeContainerView()

  private var view: BlockView? {
    didSet {
      swipeContainerView.child = view
    }
  }

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { view.asArray() }
  var effectiveBackgroundColor: UIColor? { view?.effectiveBackgroundColor }

  private weak var observer: ElementStateObserver?
  private weak var renderingDelegate: RenderingDelegate?

  override init(frame: CGRect) {
    super.init(frame: frame)
    addSubview(swipeContainerView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(
    model: Model,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    self.observer = observer
    self.renderingDelegate = renderingDelegate

    view = model.child.reuse(
      view,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )
    swipeContainerView.state = model.state
    swipeContainerView.swipeOutAction = { [weak self] state in
      guard let self = self, model.state == .normal else { return }
      UIView.animate(
        withDuration: closeDuration,
        animations: {
          self.observer?.elementStateChanged(state, forPath: model.path)
          model.swipeOutActions.perform(sendingFrom: self)
        }
      )
    }

    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    swipeContainerView.frame = CGRect(origin: .zero, size: frame.size)
  }
}

private final class SwipeContainerView: UIScrollView, UIScrollViewDelegate {
  var swipeOutAction: ((SwipeContainerBlock.State) -> Void)?

  var child: UIView? {
    didSet {
      oldValue?.removeFromSuperview()
      maybeAddSubview(child)
    }
  }

  var state = SwipeContainerBlock.State.normal {
    didSet {
      guard state != oldValue else { return }
      switch state {
      case .left, .right:
        isScrollEnabled = false
        self.swipeOutAction?(state)
      case .normal:
        isScrollEnabled = true
      }
    }
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    isPagingEnabled = true
    showsHorizontalScrollIndicator = false
    delegate = self
    disableContentInsetAdjustmentBehavior()
    scrollsToTop = false
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  private func updateContentOffset() {
    let x: CGFloat
    switch state {
    case .left:
      x = 0
    case .normal:
      x = bounds.width
    case .right:
      x = bounds.width * 2
    }
    contentOffset = CGPoint(x: x, y: 0)
  }

  private func layoutChild() {
    child?.frame = CGRect(
      x: bounds.width,
      y: 0,
      width: bounds.width,
      height: bounds.height
    )
    if !isDragging, !isDecelerating {
      updateContentOffset()
    }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    contentSize = CGSize(width: bounds.width * 3, height: bounds.height)
    layoutChild()
  }

  func scrollViewDidEndDecelerating(_: UIScrollView) {
    switch bounds.minX {
    case let x where x < bounds.width * 0.5:
      state = .left
    case let x where x > bounds.width * 1.5:
      state = .right
    default:
      state = .normal
    }
  }
}

private let closeDuration: TimeInterval = 0.2
