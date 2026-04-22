#if os(iOS)
import UIKit
import VGSL

private typealias CellType = GenericCollectionViewCell

public final class GalleryView: BlockView {
  public typealias LayoutFactory = (GalleryViewModel, CGSize) -> GalleryViewLayouting

  private enum DeferredStateSetting {
    case idle
    case pending(GalleryViewState)
    case firstLayoutPerformed
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

  private var deferredStateSetting = DeferredStateSetting.idle
  private var configurationInProgress = false
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

  public override init(frame: CGRect) {
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
    (collectionView as UIScrollView).delegate = compoundScrollDelegate
    addSubview(collectionView)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
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
    collectionView.frame = bounds

    var shouldResync = false
    if let model, layout?.isEqual(to: model, boundsSize: bounds.size) != true {
      updateLayout(to: model)
      updateInfiniteScrollIfNeeded()
      setState(stateWithScrollRange, notifyingObservers: true)
      shouldResync = true
    }
    if case let .pending(state) = deferredStateSetting {
      collectionView.performWithDetachedDelegate {
        updateContentOffset(to: state.contentPosition, animated: false)
      }
    } else if shouldResync {
      resyncPagerContentOffset()
    }
    deferredStateSetting = .idle
    layoutReporter?.didLayoutSubviews()
  }

  public func configure(
    model: GalleryViewModel,
    state: GalleryViewState,
    layoutFactory: @escaping LayoutFactory = GalleryViewLayout.init,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    guard !configurationInProgress else { return }

    configurationInProgress = true
    defer {
      configurationInProgress = false
    }

    self.layoutFactory = layoutFactory
    self.observer = observer
    self.overscrollDelegate = overscrollDelegate
    self.renderingDelegate = renderingDelegate

    let oldModel = self.model
    let oldState = self.state

    self.model = model

    setState(state.resetToModelIfInconsistent(model), notifyingObservers: false)
    updateLayout(to: model)

    if oldModel != model {
      configureByNewModel(
        isLayoutDirectionChanged: oldModel?.layoutDirection != model.layoutDirection,
        isItemsNumberChanged: oldModel?.items.count != model.items.count
      )
    }

    scrollHandler.direction = model.direction
    scrollHandler.mode = switch model.scrollMode {
    case .default:
      .free
    case let .autoPaging(inertionEnabled):
      .layoutPaging(step: inertionEnabled ? .multiplePages : .singlePage)
    case .fixedPaging:
      .fixedSizePaging
    }

    updateInfiniteScrollIfNeeded()

    let willHandlePositionChange = oldState != self.state
    if willHandlePositionChange {
      configureByNewState(
        oldContentPosition: oldState?.contentPosition,
        newLayout: oldModel?.path != model.path
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
    newLayout: Bool
  ) {
    switch deferredStateSetting {
    case .idle where frame.size == .zero,
         .pending where frame.size == .zero:
      deferredStateSetting = .pending(self.state)
    case .idle, .pending, .firstLayoutPerformed:
      scrollHandler.handlePositionChange(
        oldPosition: oldContentPosition,
        newPosition: state.contentPosition,
        newLayout: newLayout,
        animated: state.animated
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
    guard self.state != state else { return }

    self.state = state

    if notifyingObservers, let model {
      observer?.elementStateChanged(state, forPath: model.path)
    }
  }

  private func updateLayout(to model: GalleryViewModel) {
    let layout = layoutFactory(model, bounds.size)
    collectionViewLayout.apply(layout)
    self.layout = layout

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
    animated: Bool
  ) {
    switch contentPosition {
    case let .offset(value, _):
      setContentOffset(value, animated: false)
    case let .paging(pageIndex):
      let offset = layout.contentOffset(pageIndex: pageIndex)
      setContentOffset(offset, animated: animated)
    }
  }

  private func setContentOffset(_ offset: CGFloat, animated: Bool) {
    let contentOffset = switch model.direction {
    case .vertical:
      CGPoint(x: 0, y: offset)
    case .horizontal:
      CGPoint(x: offset, y: 0)
    }
    if collectionView.contentOffset != contentOffset {
      collectionView.setContentOffset(contentOffset, animated: animated)
    }
  }

  private func withDetachedFocusObserver(_ action: () -> Void) {
    observer?.setFocusTrackingEnabled(false)
    action()
    observer?.setFocusTrackingEnabled(true)
  }
}

extension GalleryView: ScrollHandlerDelegate {
  func updateOffsetToPosition(_ pos: GalleryViewState.Position, animated: Bool) {
    updateContentOffset(to: pos, animated: animated)

    if animated {
      collectionView.layoutIfNeeded()
    } else {
      onDidEndScroll()
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
      animated: true
    )
    setState(newState, notifyingObservers: true)
    updatesDelegate?.onContentOffsetChanged(offset, in: model)
    visibilityDelegate?.onGalleryVisibilityChanged()
  }

  func interruptScrolling() {
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
    let newState = GalleryViewState(
      contentPosition: state.contentPosition,
      itemsCount: model.items.count,
      isScrolling: false,
      scrollRange: state.scrollRange,
      animated: true
    )
    setState(newState, notifyingObservers: true)
    visibilityDelegate?.onGalleryVisibilityChanged()

    let firstVisibleItemOffset = getOffset(collectionView)
    let firstVisibleItemIndex = layout.blockFrames.firstIndex {
      (model.direction.isHorizontal ? $0.maxX : $0.maxY) >= firstVisibleItemOffset
    } ?? -1

    let lastVisibleItemOffset = model.direction.isHorizontal
      ? firstVisibleItemOffset + collectionView.bounds.width
      : firstVisibleItemOffset + collectionView.bounds.height
    let lastVisibleItemIndex = layout.blockFrames.lastIndex {
      (model.direction.isHorizontal ? $0.minX : $0.minY) < lastVisibleItemOffset
    } ?? -1

    GalleryScrollEvent(
      path: model.path,
      direction: GalleryScrollEvent.Direction(
        from: scrollStartOffset ?? 0,
        to: firstVisibleItemOffset
      ),
      firstVisibleItemIndex: firstVisibleItemIndex,
      lastVisibleItemIndex: lastVisibleItemIndex,
      itemsCount: model.items.count
    ).sendFrom(self)
  }
}

extension GalleryView: ScrollViewTrackable {
  public var isTracking: Bool { collectionView.isTracking }
  public var isDragging: Bool { collectionView.isDragging }
  public var isDecelerating: Bool { collectionView.isDecelerating }
}

private final class GalleryDataSource: NSObject, UICollectionViewDataSource {
  var blocks: [Block] = []
  weak var observer: ElementStateObserver?
  weak var overscrollDelegate: ScrollDelegate?
  weak var renderingDelegate: RenderingDelegate?

  func collectionView(_: UICollectionView, numberOfItemsInSection _: Int) -> Int {
    blocks.count
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let block = blocks[indexPath.item]
    let cell = collectionView.dequeueReusableCell(
      withReuseIdentifier: block.reuseId,
      for: indexPath
    ) as! CellType

    cell.configure(
      model: block,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
    return cell
  }
}

extension GalleryViewModel.ScrollMode {
  fileprivate var decelerationRate: UIScrollView.DecelerationRate {
    switch self {
    case .default:
      .normal
    case .autoPaging, .fixedPaging:
      .fast
    }
  }
}

extension GenericCollectionViewLayout {
  fileprivate func apply(_ layout: GalleryViewLayouting?) {
    self.layout = layout
      .map { GenericCollectionLayout(
        frames: $0.blockFrames,
        contentSize: $0.contentSize,
        transformation: $0.transformation
      ) }
  }
}

extension GalleryView: VisibleBoundsTrackingContainer {}

extension GalleryView {
  var stateWithScrollRange: GalleryViewState {
    GalleryViewState(
      contentPosition: state.contentPosition,
      itemsCount: state.itemsCount,
      isScrolling: state.isScrolling,
      scrollRange: model.direction.isHorizontal ?
        layout.contentSize.width - bounds.width :
        layout.contentSize.height - bounds.height,
      animated: state.animated
    )
  }
}

extension UIScrollView {
  fileprivate func interruptScroll() {
    isScrollEnabled = false
    isScrollEnabled = true
  }
}
#endif
