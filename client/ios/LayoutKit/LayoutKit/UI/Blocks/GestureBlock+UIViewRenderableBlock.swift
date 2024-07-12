import UIKit

import VGSL

extension GestureBlock {
  public static func makeBlockView() -> BlockView {
    GestureView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is GestureView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! GestureView).configure(
      child: child,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      upActions: upActions,
      downActions: downActions,
      leftActions: leftActions,
      rightActions: rightActions
    )
  }
}

private final class GestureView: BlockView {
  private var childView: BlockView!
  private var upActions: [UserInterfaceAction]?
  private var downActions: [UserInterfaceAction]?
  private var leftActions: [UserInterfaceAction]?
  private var rightActions: [UserInterfaceAction]?

  private var swipeRecognizers: [UIGestureRecognizer]? {
    didSet {
      oldValue?.forEach { removeGestureRecognizer($0) }
      swipeRecognizers?.forEach { addGestureRecognizer($0) }
    }
  }

  var effectiveBackgroundColor: UIColor? {
    childView.backgroundColor
  }

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(
    child: Block,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    upActions: [UserInterfaceAction]?,
    downActions: [UserInterfaceAction]?,
    leftActions: [UserInterfaceAction]?,
    rightActions: [UserInterfaceAction]?
  ) {
    childView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )
    self.upActions = upActions
    self.downActions = downActions
    self.leftActions = leftActions
    self.rightActions = rightActions
    setupGestureRecognizers()
    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    self.childView.frame = bounds
  }

  private func setupGestureRecognizers() {
    swipeRecognizers = Array<UISwipeGestureRecognizer>.build {
      if upActions != nil {
        makeSwipeGestureRecognizer(for: .up)
      }
      if downActions != nil {
        makeSwipeGestureRecognizer(for: .down)
      }
      if leftActions != nil {
        makeSwipeGestureRecognizer(for: .left)
      }
      if rightActions != nil {
        makeSwipeGestureRecognizer(for: .right)
      }
    }
  }

  @objc private func handleSwipe(_ recognizer: UISwipeGestureRecognizer) {
    switch recognizer.direction {
    case .right:
      rightActions?.forEach { $0.perform(sendingFrom: self) }
    case .down:
      downActions?.forEach { $0.perform(sendingFrom: self) }
    case .left:
      leftActions?.forEach { $0.perform(sendingFrom: self) }
    case .up:
      upActions?.forEach { $0.perform(sendingFrom: self) }
    default:
      break
    }
  }

  private func makeSwipeGestureRecognizer(
    for direction: UISwipeGestureRecognizer.Direction
  ) -> UISwipeGestureRecognizer {
    let recognizer = UISwipeGestureRecognizer(
      target: self,
      action: #selector(handleSwipe(_:))
    )
    recognizer.direction = direction
    return recognizer
  }
}

extension GestureView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [childView]
  }
}
