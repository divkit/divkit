#if os(iOS)
import UIKit
import VGSL

extension StateBlock {
  public static func makeBlockView() -> BlockView {
    StateBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is StateBlockView
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    (view as! StateBlockView).configure(
      child: child,
      ids: Set(ids.map { BlockViewID(rawValue: $0) }),
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      parentBlock: self
    )
  }
}

private final class SubviewStorage: RenderingDelegate {
  typealias FrameWithID = (id: BlockViewID, frame: CGRect)

  private typealias ViewWithID = (id: BlockViewID, view: DetachableAnimationBlockView)

  private let wrappedRenderingDelegate: RenderingDelegate?
  private let ids: Set<BlockViewID>
  // views are ordered, so can't use Dictionary here
  private var views: [ViewWithID] = []

  init(
    wrappedRenderingDelegate: RenderingDelegate?,
    ids: Set<BlockViewID>
  ) {
    if let subviewStorage = wrappedRenderingDelegate as? SubviewStorage {
      self.wrappedRenderingDelegate = subviewStorage.wrappedRenderingDelegate
    } else {
      self.wrappedRenderingDelegate = wrappedRenderingDelegate
    }
    self.ids = ids
  }

  func mapView(_ view: BlockView, to id: BlockViewID) {
    if let view = view as? DetachableAnimationBlockView {
      if getView(id) == nil {
        views.append((id: id, view: view))
      } else {
        views.removeAll { $0.id == id }
        views.append((id: id, view: view))
      }
    }
    wrappedRenderingDelegate?.mapView(view, to: id)
  }

  func tooltipAnchorViewAdded(anchorView: TooltipAnchorView) {
    wrappedRenderingDelegate?.tooltipAnchorViewAdded(anchorView: anchorView)
  }

  func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView) {
    wrappedRenderingDelegate?.tooltipAnchorViewRemoved(anchorView: anchorView)
  }

  func reportRenderingError(message: String, isWarning: Bool, path: UIElementPath) {
    wrappedRenderingDelegate?.reportRenderingError(
      message: message,
      isWarning: isWarning,
      path: path
    )
  }

  func reportViewWasCreated() {
    wrappedRenderingDelegate?.reportViewWasCreated()
  }

  func reportBlockDidConfigure(path: UIElementPath) {
    wrappedRenderingDelegate?.reportBlockDidConfigure(path: path)
  }

  func reportBlockWillConfigure(path: UIElementPath) {
    wrappedRenderingDelegate?.reportBlockWillConfigure(path: path)
  }

  func reportViewDidLayout(path: UIElementPath) {
    wrappedRenderingDelegate?.reportViewDidLayout(path: path)
  }

  func reportViewWillLayout(path: UIElementPath) {
    wrappedRenderingDelegate?.reportViewWillLayout(path: path)
  }

  func getView(_ id: BlockViewID) -> DetachableAnimationBlockView? {
    views.first { $0.id == id }?.view
  }

  func getViewsToRemove(
    newIds: Set<BlockViewID>
  ) -> [DetachableAnimationBlockView] {
    let idsToRemove = ids.subtracting(newIds)
    return idsToRemove.compactMap {
      getView($0)
    }
  }

  func getViewsToAdd() -> [DetachableAnimationBlockView] {
    views
      .filter(\.view.hasAnimationIn)
      .map(\.view)
  }

  func getViewsToTransition(
    newIds: Set<BlockViewID>,
    container: UIView
  ) -> [FrameWithID] {
    let idsToTransition = ids.intersection(newIds)
    if idsToTransition.isEmpty {
      return []
    }

    return views.compactMap { id, view in
      idsToTransition.contains(id)
        ? (id: id, frame: view.convertFrame(to: container))
        : nil
    }
  }
}

private final class StateBlockView: BlockView {
  private var subviewStorage = SubviewStorage(wrappedRenderingDelegate: nil, ids: [])
  private var childView: BlockView?
  private var stateId: String?
  private var hasCompletedLayout = false
  private var hasPendingFirstChildLayout = false
  private var hasPendingNonAnimatedRelayout = false

  private var parentBlock: StateBlock?
  private weak var observer: ElementStateObserver?
  private weak var overscrollDelegate: ScrollDelegate?
  private weak var renderingDelegate: RenderingDelegate?

  var effectiveBackgroundColor: UIColor? { childView?.effectiveBackgroundColor }

  override func layoutSubviews() {
    super.layoutSubviews()
    hasCompletedLayout = true

    // See DetachableAnimationBlockView.layoutSubviews: a child view created by a
    // state switch inside a host animation block must receive its first geometry
    // without inheriting that animation. The same applies to the relayout that
    // follows an early layout done with stale bounds (see `configure`).
    if hasPendingFirstChildLayout || hasPendingNonAnimatedRelayout, bounds != .zero {
      hasPendingFirstChildLayout = false
      hasPendingNonAnimatedRelayout = false
      if UIView.inheritedAnimationDuration > 0 {
        UIView.withoutInheritedAnimation {
          childView?.setNonTransformedFrame(bounds)
          childView?.layoutIfNeeded()
        }
        return
      }
    }

    childView?.setNonTransformedFrame(bounds)
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }

  func configure(
    child: Block,
    ids: Set<BlockViewID>,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    parentBlock: StateBlock
  ) {
    defer {
      self.parentBlock = parentBlock
      self.observer = observer
      self.overscrollDelegate = overscrollDelegate
      self.renderingDelegate = renderingDelegate
    }

    if let oldParentChild = self.parentBlock?.child,
       child.self.equals(oldParentChild),
       self.observer === observer,
       self.overscrollDelegate === overscrollDelegate,
       self.renderingDelegate === renderingDelegate {
      return // The child block hasn't changed, stop configuring
    }

    let viewsToTransition = subviewStorage.getViewsToTransition(
      newIds: ids,
      container: self
    )
    // Change-bounds flights need a real source geometry. Before the first
    // layout pass, or while detached from a window, the captured frames are
    // meaningless (zero or stale) and a flight from them starts at the screen
    // corner. Such state changes are applied in place instead.
    let canAnimateChangeBounds = window != nil && hasCompletedLayout

    let viewsToRemove = subviewStorage.getViewsToRemove(newIds: ids)
    if !viewsToRemove.isEmpty {
      removeViewsWithUnfinishedAnimations()
    }

    for view in viewsToRemove {
      view.cancelAnimations()
      view.removeWithAnimation(in: self)
    }

    subviewStorage = SubviewStorage(
      wrappedRenderingDelegate: renderingDelegate,
      ids: ids
    )

    // A state switch that runs inside a host animation block (e.g. a tab bar
    // hide/show animation that rebinds the card) must not let the new subtree
    // inherit that animation: plain containers created by the switch would
    // otherwise animate from a zero frame and clip their content. Build and lay
    // out the new state without implicit animations; the explicit change_bounds
    // and transition_in animations (including those of nested state switches
    // built here) still run with their own parameters.
    let isInsideAmbientAnimation = UIView.inheritedAnimationDuration > 0
    let previousChildView = childView
    let reuseChild = {
      self.childView = child.reuse(
        self.childView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: self.subviewStorage,
        superview: self
      )
    }
    if isInsideAmbientAnimation {
      UIView.withoutInheritedAnimation(reuseChild)
    } else {
      reuseChild()
    }
    if childView !== previousChildView {
      hasPendingFirstChildLayout = true
    }

    let viewsToAdd = subviewStorage.getViewsToAdd()

    if isInsideAmbientAnimation, window != nil, hasFinalBounds(for: parentBlock) {
      UIView.withoutInheritedAnimation {
        forceLayout()
      }
      // The parent has not necessarily applied this view's new size yet, so the
      // layout above may have used stale bounds. When the parent resizes the view
      // during the same animation, the subtree must follow without inheriting it:
      // otherwise children sized by the container (e.g. a match_parent button)
      // would animate from the stale geometry to the right one.
      hasPendingNonAnimatedRelayout = true
    }

    if viewsToAdd.isEmpty, viewsToTransition.isEmpty {
      setNeedsLayout()
    } else {
      if window != nil {
        forceLayout()
      } else {
        setNeedsLayout()
      }

      let transformedViews = subviewStorage.getViewsToTransition(newIds: ids, container: self)
      if viewsToRemove.isEmpty, transformedViews != viewsToTransition || !viewsToAdd.isEmpty {
        removeViewsWithUnfinishedAnimations()
      }

      if canAnimateChangeBounds {
        changeBoundsWithAnimation(viewsToTransition)
      } else {
        settleWithoutAnimation(viewsToTransition)
      }
      addWithAnimations(viewsToAdd)
    }
  }

  // The early, non-animated layout of a switch inside an ambient animation is only
  // meaningful with the geometry the parent is going to keep. When the new state
  // changes the intrinsic size, the parent is about to resize this view: laying
  // out with the current bounds would produce a transient wrong geometry (e.g. a
  // match_parent child sized by stale bounds) that the following relayout would
  // animate from. In that case the regular parent-driven layout pass handles the
  // switch, as it did before the protection existed.
  private func hasFinalBounds(for block: StateBlock) -> Bool {
    let width = block.isHorizontallyResizable
      ? bounds.width
      : block.widthOfHorizontallyNonResizableBlock
    let height = block.isVerticallyResizable
      ? bounds.height
      : block.heightOfVerticallyNonResizableBlock(forWidth: width)
    return abs(width - bounds.width) < 0.5 && abs(height - bounds.height) < 0.5
  }

  private func addWithAnimations(_ views: [DetachableAnimationBlockView]) {
    for view in views {
      view.addWithAnimation()
    }
  }

  private func settleWithoutAnimation(_ views: [SubviewStorage.FrameWithID]) {
    for (id, _) in views {
      subviewStorage.getView(id)?.applyChangeBoundsWithoutAnimation()
    }
  }

  private func changeBoundsWithAnimation(_ views: [SubviewStorage.FrameWithID]) {
    for (id, frame) in views {
      if let view = subviewStorage.getView(id) {
        view.changeBoundsWithAnimation(in: self, startFrame: frame)
      }
    }
  }

  private func removeViewsWithUnfinishedAnimations() {
    for subview in subviews {
      if subview !== childView {
        subview.removeFromSuperview()
      }
    }
  }
}

extension StateBlockView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    childView.asArray()
  }
}
#endif
