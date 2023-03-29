import UIKit

import CommonCorePublic

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
      renderingDelegate: renderingDelegate
    )
  }
}

private final class SubviewStorage: RenderingDelegate {
  private typealias ViewWithID = (id: BlockViewID, view: DetachableAnimationBlockView)
  typealias FrameWithID = (id: BlockViewID, frame: CGRect)

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
      } else if view.superview != nil {
        views.removeAll { $0.id == id }
        views.append((id: id, view: view))
      }
    }
    wrappedRenderingDelegate?.mapView(view, to: id)
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
      .filter { $0.view.hasAnimationIn }
      .map { $0.view }
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

  var effectiveBackgroundColor: UIColor? { childView?.effectiveBackgroundColor }

  init() {
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func configure(
    child: Block,
    ids: Set<BlockViewID>,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    // remove views with unfinished animations
    subviews.forEach {
      if $0 !== childView {
        $0.removeFromSuperview()
      }
    }

    let viewsToTransition = subviewStorage.getViewsToTransition(
      newIds: ids,
      container: self
    )

    subviewStorage.getViewsToRemove(newIds: ids).forEach {
      $0.cancelAnimations()
      $0.removeWithAnimation(in: self)
    }

    subviewStorage = SubviewStorage(
      wrappedRenderingDelegate: renderingDelegate,
      ids: ids
    )

    childView = child.reuse(
      childView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: subviewStorage,
      superview: self
    )

    forceLayout()

    subviewStorage.getViewsToAdd().forEach {
      $0.addWithAnimation(in: self)
    }

    viewsToTransition.forEach { id, frame in
      if let view = subviewStorage.getView(id) {
        view.changeBoundsWithAnimation(in: self, startFrame: frame)
      }
    }

    setNeedsLayout()
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    childView?.frame = bounds
  }
}

extension StateBlockView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    childView.asArray()
  }
}
