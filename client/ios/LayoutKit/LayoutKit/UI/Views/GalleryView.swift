#if os(iOS)
import UIKit
import VGSL

private typealias CellType = GenericCollectionViewCell

public final class GalleryView: BlockView {
  public typealias LayoutFactory = (GalleryViewModel, CGSize) -> GalleryViewLayouting

  public enum ObserverNotificationMode: Equatable {
    /// Publishes intermediate per-frame states and the final state.
    case allStates

    /// Suppresses intermediate per-frame states. Final and layout-driven states are still
    /// published.
    case suppressIntermediateScrollStates
  }

  public var layoutReporter: LayoutReporter?
  public weak var observer: ElementStateObserver? {
    didSet {
      dataSource.observer = observer
    }
  }

  public weak var updatesDelegate: GalleryViewModelDelegate?
  public weak var visibilityDelegate: GalleryVisibilityDelegate?

  private(set) var state: GalleryViewState!

  private let collectionView: VisibleBoundsTrackingCollectionView
  private let collectionViewLayout: GenericCollectionViewLayout
  private let dataSource = GalleryDataSource()
  private let compoundScrollDelegate = CompoundScrollDelegate()
  private let cellRegistrator = CollectionCellRegistrator()
  private var appliedScrollRequest: ScrollRequest?
  private var scrollTargetPosition: GalleryViewState.Position?
  private let scrollClock: ScrollAnimationClock
  private lazy var scrollAnimator = ScrollOffsetAnimator(
    scrollView: collectionView,
    clock: scrollClock
  )

  private var model: GalleryViewModel!
  private var layout: GalleryViewLayouting! {
    didSet {
      scrollHandler.layout = layout
    }
  }

  private var layoutFactory: LayoutFactory!
  private lazy var scrollHandler: ScrollHandler = {
    let scrollHandler = ScrollHandler(layout: layout)
    scrollHandler.delegate = self
    compoundScrollDelegate.add(scrollHandler)
    return scrollHandler
  }()

  private var pendingLayoutState: GalleryViewState?
  private var configurationInProgress = false
  private var observerNotificationMode = ObserverNotificationMode.allStates
  private weak var overscrollDelegate: ScrollDelegate? {
    didSet {
      if let previousDelegate = oldValue {
        compoundScrollDelegate.remove(previousDelegate)
      }
      if let currentDelegate = overscrollDelegate {
        compoundScrollDelegate.add(currentDelegate)
      }
      dataSource.overscrollDelegate = overscrollDelegate
    }
  }

  private weak var renderingDelegate: RenderingDelegate? {
    didSet {
      dataSource.renderingDelegate = renderingDelegate
    }
  }

  public var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { [collectionView] }

  public var path: UIElementPath {
    model.path
  }

  public var visibilityState: GalleryVisibilityState {
    let items: [GalleryVisibilityState.Item] = collectionView.indexPathsForVisibleItems.compactMap {
      guard let view = collectionView.cellForItem(at: $0) else { return nil }
      let viewFrameInWindow = view.frameInWindowCoordinates
      let visibilityState = VisibilityState(visibleFrame: viewFrameInWindow)
      return .init(state: visibilityState, index: $0.item)
    }
    let visibilityState = GalleryVisibilityState(
      visibleItems: items,
      selectedItemIndex: state.contentPosition.pageIndex,
      isChangingContentOffsetDueToUserActions: isChangingContentOffsetDueToUserActions
    )
    return visibilityState.intersected(with: frameInWindowCoordinates)
  }

  public var effectiveBackgroundColor: UIColor? { collectionView.backgroundColor }

  public override convenience init(frame: CGRect) {
    self.init(frame: frame, scrollClock: DisplayLinkScrollAnimationClock())
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  init(frame: CGRect, scrollClock: ScrollAnimationClock) {
    self.scrollClock = scrollClock
    collectionViewLayout = GenericCollectionViewLayout()
    collectionView = VisibleBoundsTrackingCollectionView(
      frame: frame,
      collectionViewLayout: collectionViewLayout
    )
    collectionView.alwaysBounceVertical = false
    collectionView.clipsToBounds = false
    collectionView.backgroundColor = .clear
    collectionView.scrollsToTop = false
    collectionView.dataSource = dataSource

    collectionView.disableContentInsetAdjustmentBehavior()

    super.init(frame: frame)
    compoundScrollDelegate.add(scrollAnimator)
    (collectionView as UIScrollView).delegate = compoundScrollDelegate
    addSubview(collectionView)
  }

  public override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let superResult = super.hitTest(point, with: event)
    if model.areEmptySpaceTouchesEnabled {
      return superResult
    } else {
      return superResult === collectionView ? nil : superResult
    }
  }

  public override func layoutSubviews() {
    guard !bounds.isEmpty else {
      return
    }
    layoutReporter?.willLayoutSubviews()
    if collectionView.bounds.size != bounds.size, scrollAnimator.isRunning {
      scrollAnimator.cancel()
      if case let .paging(index) = state.contentPosition {
        updateContentOffset(to: .paging(index: index.rounded()), animated: false)
      }
      scrollHandler.onDidEndScrollingAnimation(collectionView)
    }
    collectionView.frame = bounds

    var shouldResync = false
    if let model, layout?.isEqual(to: model, boundsSize: bounds.size) != true {
      updateLayout(to: model)
      configureScrollBehavior(for: model)
      updateInfiniteScrollIfNeeded()
      setState(state.withScrollRange(scrollRange), notifyingObservers: true)
      shouldResync = true
    }
    if let state = pendingLayoutState {
      state.scrollRequest?.markApplied()
      collectionView.performWithDetachedDelegate {
        updateContentOffset(to: state.contentPosition, animated: false)
      }
      if state.scrollRequest != nil {
        setState(self.state.completingScroll(
          itemsCount: self.state.itemsCount, animated: self.state.animated
        ), notifyingObservers: true)
      }
    } else if shouldResync {
      resyncPagerContentOffset()
    }
    pendingLayoutState = nil
    layoutReporter?.didLayoutSubviews()
  }

  public func configure(
    model: GalleryViewModel,
    state: GalleryViewState,
    layoutFactory: @escaping LayoutFactory = GalleryViewLayout.init,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    observerNotificationMode: ObserverNotificationMode = .allStates,
    navigationDirection: ScrollNavigationDirection = .none
  ) {
    guard !configurationInProgress else {
      return
    }

    configurationInProgress = true
    defer {
      configurationInProgress = false
    }

    self.layoutFactory = layoutFactory
    self.observer = observer
    self.overscrollDelegate = overscrollDelegate
    self.renderingDelegate = renderingDelegate
    self.observerNotificationMode = observerNotificationMode

    configureScroll(model: model, state: state, navigationDirection: navigationDirection)
  }

  func reusingDefaultLayout(model: GalleryViewModel, boundsSize: CGSize) -> GalleryViewLayout {
    if let layout = layout as? GalleryViewLayout,
       layout.isEqual(to: model, boundsSize: boundsSize) {
      return layout
    }
    return GalleryViewLayout(model: model, boundsSize: boundsSize)
  }

  private func configureScroll(
    model: GalleryViewModel,
    state: GalleryViewState,
    navigationDirection: ScrollNavigationDirection
  ) {
    let oldModel = self.model
    let oldState = self.state

    let (wasTimedScrollInterrupted, interruptedTarget) = interruptScrollIfContextChanged(to: model)
    self.model = model
    updateLayout(to: model)

    let isNewScrollRequest = state.scrollRequest != nil
      && state.scrollRequest != appliedScrollRequest
    let useStoredPosition = oldModel?.path != model.path || bounds.isEmpty
    let requestedState = state.resolvingScrollRequest(
      model: model, layout: layout,
      currentOffset: useStoredPosition ? nil : getOffset(collectionView),
      resolveDelta: isNewScrollRequest, finishImmediately: wasTimedScrollInterrupted,
      interruptedTarget: state.scrollRequest == nil || isNewScrollRequest ? nil : interruptedTarget
    )
    applyState(
      requestedState, oldState: oldState, oldModel: oldModel,
      isNewScrollRequest: isNewScrollRequest || wasTimedScrollInterrupted
    )

    configureModelChanges(model, oldModel: oldModel)
    if !isNewScrollRequest {
      retargetScrollIfNeeded()
    }
    applyScrollRequest(
      requestedState,
      oldState: oldState,
      oldModel: oldModel,
      isNewScrollRequest: isNewScrollRequest || wasTimedScrollInterrupted,
      navigationDirection: navigationDirection
    )
    if wasTimedScrollInterrupted, oldModel?.path != model.path {
      scrollHandler.onDidEndScrollingAnimation(collectionView)
    }
    // A reused view may already have its final bounds, so layoutSubviews will not publish
    // the range calculated during configuration to the new gallery's state storage.
    if self.state.scrollRange != state.scrollRange {
      observer?.elementStateChanged(self.state, forPath: model.path)
    }
  }

  private func interruptScrollIfContextChanged(
    to model: GalleryViewModel
  ) -> (Bool, GalleryViewState.Position?) {
    let oldModel = self.model
    let isScrollContextChanged = oldModel != nil && (
      oldModel?.path != model.path || oldModel?.direction != model.direction
        || oldModel?.scrollMode != model.scrollMode
        || oldModel?.layoutDirection != model.layoutDirection
        || oldModel?.infiniteScroll != model.infiniteScroll
    )
    let wasTimedScrollInterrupted = isScrollContextChanged && scrollAnimator.isRunning
    let target = wasTimedScrollInterrupted && oldModel?.path == model.path
      ? scrollTargetPosition : nil
    if isScrollContextChanged {
      scrollAnimator.cancel()
      scrollTargetPosition = nil
    }
    return (wasTimedScrollInterrupted, target)
  }

  private func retargetScrollIfNeeded() {
    if scrollAnimator.isRunning, let scrollTargetPosition {
      let target = GalleryViewState(
        contentPosition: scrollTargetPosition, itemsCount: model.items.count,
        isScrolling: false, animated: false
      ).resetToModelIfInconsistent(model, maxValidScrollRange: scrollRange)
      self.scrollTargetPosition = target.contentPosition
      let offset = target.contentPosition.offset
        ?? layout.contentOffset(pageIndex: target.contentPosition.pageIndex ?? 0)
      scrollAnimator.retarget(to: model.direction.isHorizontal
        ? CGPoint(x: offset, y: 0) : CGPoint(x: 0, y: offset)
      )
    }
  }

  private func configureModelChanges(_ model: GalleryViewModel, oldModel: GalleryViewModel?) {
    configureScrollBehavior(for: model)
    if oldModel != model {
      configureByNewModel(
        isLayoutDirectionChanged: oldModel?.layoutDirection != model.layoutDirection,
        isItemsNumberChanged: oldModel?.items.count != model.items.count
      )
    }
    configureScrollHandler(for: model)
  }

  private func applyState(
    _ state: GalleryViewState,
    oldState: GalleryViewState?,
    oldModel: GalleryViewModel?,
    isNewScrollRequest: Bool
  ) {
    let model = self.model!
    let maxValidScrollRange = bounds.isEmpty ? nil : scrollRange

    let isAppliedRelativeRequest = !isNewScrollRequest && state.scrollRequest != nil
      && state.scrollRequest == appliedScrollRequest && state.scrollRequest?.delta != nil
    let stateToApply = if let oldState, isAppliedRelativeRequest {
      oldState
    } else if let oldState, scrollAnimator.isRunning, !isNewScrollRequest {
      oldState
    } else if let oldState,
              let oldModel,
              oldModel.path == model.path,
              oldModel.direction == model.direction,
              oldModel.scrollMode == model.scrollMode,
              oldState.isScrolling,
              scrollHandler.isUserInitiatedScroll {
      oldState
    } else {
      state
    }
    setState(
      stateToApply.resetToModelIfInconsistent(
        model,
        maxValidScrollRange: maxValidScrollRange
      ),
      notifyingObservers: false
    )
  }

  private func configureScrollHandler(for model: GalleryViewModel) {
    scrollHandler.direction = model.direction
    scrollHandler.mode = switch model.scrollMode {
    case .default:
      .free
    case let .autoPaging(inertionEnabled):
      .layoutPaging(step: inertionEnabled ? .multiplePages : .singlePage)
    case .fixedPaging:
      .fixedSizePaging
    }
  }

  private func applyScrollRequest(
    _ state: GalleryViewState,
    oldState: GalleryViewState?,
    oldModel: GalleryViewModel?,
    isNewScrollRequest: Bool,
    navigationDirection: ScrollNavigationDirection
  ) {
    let model = self.model!

    updateInfiniteScrollIfNeeded()

    let willHandlePositionChange = isNewScrollRequest
      || (!scrollAnimator.isRunning && oldState != self.state)
    if isNewScrollRequest {
      appliedScrollRequest = state.scrollRequest
      if !bounds.isEmpty {
        state.scrollRequest?.markApplied()
      }
    }
    if willHandlePositionChange {
      configureByNewState(
        oldContentPosition: oldState?.contentPosition,
        newLayout: oldModel?.path != model.path,
        navigationDirection: navigationDirection,
        isNewScrollRequest: isNewScrollRequest
      )
    }
  }

  private func configureByNewModel(
    isLayoutDirectionChanged: Bool,
    isItemsNumberChanged: Bool
  ) {
    let blocks = model.items.map(\.content)
    handleElementsDisappearing(cellIndices: model.removedItemsIndices)

    cellRegistrator.register(blocks: blocks, in: collectionView)
    dataSource.blocks = blocks
    if isLayoutDirectionChanged {
      collectionView.semanticContentAttribute = model
        .layoutDirection == .rightToLeft ? .forceRightToLeft : .forceLeftToRight
    }
    collectionView.decelerationRate = model.scrollMode.decelerationRate
    collectionView.allowTapWhileScroll = model.allowTapWhileScroll
    collectionView.alwaysBounceVertical = model.alwaysBounceVertical
    collectionView.bounces = model.bounces
    collectionView.showsHorizontalScrollIndicator = model.scrollbar.show
    collectionView.showsVerticalScrollIndicator = model.scrollbar.show
    if isItemsNumberChanged {
      withDetachedFocusObserver {
        collectionView.reloadData()
      }
    } else {
      configureVisibleCells(blocks)
    }
  }

  private func configureByNewState(
    oldContentPosition: GalleryViewState.Position?,
    newLayout: Bool,
    navigationDirection: ScrollNavigationDirection,
    isNewScrollRequest: Bool
  ) {
    if frame.size == .zero {
      pendingLayoutState = self.state
    } else {
      scrollHandler.handlePositionChange(
        oldPosition: oldContentPosition,
        newPosition: state.contentPosition,
        newLayout: newLayout,
        animated: state.animated,
        scrollDuration: state.scrollRequest?.duration,
        isNewScrollRequest: isNewScrollRequest,
        direction: navigationDirection
      )
    }
  }

  private func handleElementsDisappearing(cellIndices: Set<Int>) {
    let cells = collectionView.visibleCells.map { $0 as! CellType }
    for (cell, indexPath) in zip(cells, collectionView.indexPathsForVisibleItems) {
      if cellIndices.contains(indexPath.row) {
        cell.onVisibleBoundsChanged(from: cell.frame, to: .zero)
      }
    }
  }

  private func configureVisibleCells(_ blocks: [Block]) {
    let cells = collectionView.visibleCells.map { $0 as! CellType }
    for (cell, indexPath) in zip(cells, collectionView.indexPathsForVisibleItems) {
      cell.configure(
        model: blocks[indexPath.row],
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
    }
  }

  private func setState(_ state: GalleryViewState, notifyingObservers: Bool) {
    guard self.state != state else {
      return
    }

    self.state = state

    if notifyingObservers, let model {
      observer?.elementStateChanged(state, forPath: model.path)
    }
  }

  private func updateLayout(to model: GalleryViewModel) {
    let layout = layoutFactory(model, bounds.size)
    collectionViewLayout.apply(layout)
    self.layout = layout
  }

  private func configureScrollBehavior(for model: GalleryViewModel) {
    switch model.scrollMode {
    case .default:
      scrollHandler.clearPager()
    case .autoPaging, .fixedPaging:
      let initialPagerOffset: CGFloat? = if case let .paging(index) = state.contentPosition {
        layout.contentOffset(pageIndex: index)
      } else {
        nil
      }
      scrollHandler.configurePager(
        pageOrigins: layout.pageOrigins,
        isHorizontal: model.direction.isHorizontal,
        initialContentOffset: initialPagerOffset
      )
    }
  }

  private func resyncPagerContentOffset() {
    guard let model,
          !model.scrollMode.isDefault,
          !collectionView.isTracking,
          !collectionView.isDragging,
          !collectionView.isDecelerating,
          !scrollAnimator.isRunning,
          case .paging = state.contentPosition else { return }

    collectionView.performWithDetachedDelegate {
      updateContentOffset(to: state.contentPosition, animated: false)
    }
  }

  private func updateInfiniteScrollIfNeeded() {
    guard let model else { return }
    scrollHandler.configureInfiniteScroll(
      enabled: model.infiniteScroll,
      bufferSize: model.bufferSize,
      boundsSize: model.direction.isHorizontal ? bounds.width : bounds.height,
      alignment: model.alignment,
      insetMode: model.metrics.axialInsetMode
    )
  }

  private func updateContentOffset(
    to contentPosition: GalleryViewState.Position,
    animated: Bool,
    scrollDuration: TimeInterval? = nil,
    completion: @escaping () -> Void = {}
  ) {
    switch contentPosition {
    case let .offset(value, _):
      setContentOffset(
        value,
        animated: scrollDuration == nil ? false : animated,
        scrollDuration: scrollDuration,
        completion: completion
      )
    case let .paging(pageIndex):
      let offset = layout.contentOffset(pageIndex: pageIndex)
      setContentOffset(
        offset,
        animated: animated,
        scrollDuration: scrollDuration,
        completion: completion
      )
    }
  }

  private func setContentOffset(
    _ offset: CGFloat,
    animated: Bool,
    scrollDuration: TimeInterval? = nil,
    completion: @escaping () -> Void = {}
  ) {
    let contentOffset = switch model.direction {
    case .vertical:
      CGPoint(x: 0, y: offset)
    case .horizontal:
      CGPoint(x: offset, y: 0)
    }
    scrollAnimator.scroll(
      to: contentOffset,
      animated: animated,
      duration: scrollDuration,
      completion: completion
    )
    if scrollAnimator.isRunning {
      scrollHandler.onDidScroll(collectionView)
    }
  }

  private func withDetachedFocusObserver(_ action: () -> Void) {
    observer?.setFocusTrackingEnabled(false)
    action()
    observer?.setFocusTrackingEnabled(true)
  }
}

extension GalleryView: ScrollHandlerDelegate {
  func updateOffsetToPosition(
    _ pos: GalleryViewState.Position,
    animated: Bool,
    scrollDuration: TimeInterval? = nil
  ) {
    scrollTargetPosition = pos
    updateContentOffset(
      to: pos,
      animated: animated,
      scrollDuration: scrollDuration
    ) { [weak self] in
      guard let self else {
        return
      }
      scrollHandler.onDidEndScrollingAnimation(collectionView)
    }

    if animated {
      if !dataSource.isConfiguringCell {
        collectionView.layoutIfNeeded()
      }
    }
  }

  func updateOffsetDetached(_ offset: CGFloat) {
    collectionView.performWithDetachedDelegate {
      setContentOffset(offset, animated: false)
    }
  }

  func updateOffsetDetached(_ pos: GalleryViewState.Position) {
    collectionView.performWithDetachedDelegate {
      updateContentOffset(to: pos, animated: false)
    }
  }

  func updateGalleryState(_ pos: GalleryViewState.Position, offset: CGFloat) {
    let newState = GalleryViewState(
      contentPosition: pos,
      itemsCount: model.items.count,
      isScrolling: true,
      scrollRange: state.scrollRange,
      animated: scrollHandler.isUserInitiatedScroll || state.animated
    )

    setState(
      newState,
      notifyingObservers: observerNotificationMode == .allStates
    )
    updatesDelegate?.onContentOffsetChanged(offset, in: model)
    visibilityDelegate?.onGalleryVisibilityChanged()
  }

  func interruptScrolling() {
    scrollAnimator.cancel()
    collectionView.interruptScroll()
  }

  func finishScrolling(scrollStartOffset: CGFloat?) {
    onDidEndScroll(scrollStartOffset: scrollStartOffset)
  }
}

extension GalleryView {
  private func getOffset(_ scrollView: ScrollView) -> CGFloat {
    model.direction.isHorizontal
      ? scrollView.contentOffset.x
      : scrollView.contentOffset.y
  }

  private func onDidEndScroll(scrollStartOffset: CGFloat? = nil) {
    scrollTargetPosition = nil
    let newState = state.completingScroll(itemsCount: model.items.count, animated: state.animated)
    setState(newState, notifyingObservers: true)
    visibilityDelegate?.onGalleryVisibilityChanged()

    GalleryScrollEvent(
      model: model, layout: layout, bounds: collectionView.bounds,
      scrollStartOffset: scrollStartOffset
    ).sendFrom(self)
  }
}

extension GalleryView: ScrollViewTrackable {
  public var isTracking: Bool { collectionView.isTracking }
  public var isDragging: Bool { collectionView.isDragging }
  public var isDecelerating: Bool { collectionView.isDecelerating }
}

extension GalleryView: VisibleBoundsTrackingContainer {}

extension GalleryView {
  private var scrollRange: CGFloat {
    model.direction.isHorizontal
      ? layout.contentSize.width - bounds.width
      : layout.contentSize.height - bounds.height
  }
}

extension GalleryScrollEvent {
  fileprivate convenience init(
    model: GalleryViewModel,
    layout: GalleryViewLayouting,
    bounds: CGRect,
    scrollStartOffset: CGFloat?
  ) {
    let isHorizontal = model.direction.isHorizontal
    let firstOffset = isHorizontal ? bounds.minX : bounds.minY
    let lastOffset = isHorizontal ? bounds.maxX : bounds.maxY
    let firstIndex = layout.blockFrames.firstIndex {
      (isHorizontal ? $0.maxX : $0.maxY) >= firstOffset
    } ?? -1
    let lastIndex = layout.blockFrames.lastIndex {
      (isHorizontal ? $0.minX : $0.minY) < lastOffset
    } ?? -1
    self.init(
      path: model.path,
      direction: Direction(from: scrollStartOffset ?? 0, to: firstOffset),
      firstVisibleItemIndex: firstIndex, lastVisibleItemIndex: lastIndex,
      itemsCount: model.items.count
    )
  }
}

#endif
