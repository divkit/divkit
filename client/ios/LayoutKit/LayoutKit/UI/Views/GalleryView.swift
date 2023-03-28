import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

private typealias CellType = GenericCollectionViewCell

public final class GalleryView: BlockView {
  public typealias LayoutFactory = (GalleryViewModel, CGSize) -> GalleryViewLayouting

  private enum DeferredStateSetting {
    case idle
    case pending(GalleryViewState)
    case firstLayoutPerformed
  }

  private let collectionView: VisibleBoundsTrackingCollectionView
  private let collectionViewLayout: GenericCollectionViewLayout
  private let dataSource = GalleryDataSource()
  private let compoundScrollDelegate = CompoundScrollDelegate()
  private var contentPager: ScrollableContentPager? {
    didSet {
      if let oldDelegate = oldValue {
        compoundScrollDelegate.remove(oldDelegate)
      }
      if let newDelegate = contentPager {
        compoundScrollDelegate.add(newDelegate)
      }
    }
  }

  private var model: GalleryViewModel!
  private var layout: GalleryViewLayouting!
  private var state: GalleryViewState!
  private var layoutFactory: LayoutFactory!
  private var deferredStateSetting = DeferredStateSetting.idle
  private var scrollStartOffset: CGFloat = 0
  public var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { [collectionView] }

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

  public weak var observer: ElementStateObserver? {
    didSet {
      dataSource.observer = observer
    }
  }

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

  public func configure(
    model: GalleryViewModel,
    state: GalleryViewState,
    layoutFactory: @escaping LayoutFactory = GalleryViewLayout.init,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    self.layoutFactory = layoutFactory
    self.observer = observer
    self.overscrollDelegate = overscrollDelegate
    self.renderingDelegate = renderingDelegate

    let oldModel = self.model
    let oldState = self.state

    self.model = model

    setState(state.resetToModelIfInconsistent(model), notifyingObservers: false)
    updateLayoutIfNeeded(to: model)

    let isNewModel = oldModel != model
    if isNewModel {
      let blocks = model.items.map { $0.content }
      dataSource.blocks = blocks
      collectionView.decelerationRate = model.scrollMode.decelerationRate
      collectionView.alwaysBounceVertical = model.alwaysBounceVertical
      collectionView.bounces = model.bounces
      if oldModel?.items.count == model.items.count {
        configureVisibleCells(blocks)
      } else {
        collectionView.reloadData()
      }
    }

    if oldState != self.state {
      switch deferredStateSetting {
      case .idle where frame.size == .zero,
           .pending where frame.size == .zero:
        deferredStateSetting = .pending(self.state)
      case .idle, .pending, .firstLayoutPerformed:
        updateContentOffset(to: self.state.contentPosition, animated: !isNewModel)
      }
    }
  }

  private func configureVisibleCells(_ blocks: [Block]) {
    let cells = collectionView.visibleCells.map { $0 as! CellType }
    zip(cells, collectionView.indexPathsForVisibleItems)
      .forEach { cell, indexPath in
        cell.configure(
          model: blocks[indexPath.row],
          observer: observer,
          overscrollDelegate: overscrollDelegate,
          renderingDelegate: renderingDelegate,
          accessibilityElement: blocks[indexPath.row].accessibilityElement
        )
      }
  }

  private func setState(_ state: GalleryViewState, notifyingObservers: Bool) {
    guard self.state != state else { return }

    self.state = state

    if notifyingObservers, let model = model {
      observer?.elementStateChanged(state, forPath: model.path)
    }
  }

  private func updateLayoutIfNeeded(to model: GalleryViewModel) {
    if self.layout?.isEqual(to: model, boundsSize: bounds.size) == true {
      return
    }

    let layout = layoutFactory(model, bounds.size)
    collectionViewLayout.apply(layout)
    self.layout = layout

    switch model.scrollMode {
    case .default:
      contentPager = nil
    case .autoPaging, .fixedPaging:
      let configure = { (contentPager: ScrollableContentPager) in
        contentPager.setPageOrigins(
          layout.pageOrigins,
          withPagingEnabled: true,
          isHorizontal: model.direction.isHorizontal
        )
        contentPager.setInitialOffset(layout.pageOrigins.first!)
      }
      if let contentPager = contentPager {
        configure(contentPager)
      } else {
        let contentPager = ScrollableContentPager()
        configure(contentPager)
        self.contentPager = contentPager
      }
    }
  }

  public weak var updatesDelegate: GalleryViewModelDelegate?
  public weak var visibilityDelegate: GalleryVisibilityDelegate?

  public override init(frame: CGRect) {
    collectionViewLayout = GenericCollectionViewLayout()
    collectionView = VisibleBoundsTrackingCollectionView(
      frame: frame,
      collectionViewLayout: collectionViewLayout
    )
    collectionView.alwaysBounceVertical = false
    collectionView.clipsToBounds = false
    collectionView.backgroundColor = .clear
    collectionView.showsHorizontalScrollIndicator = false
    collectionView.showsVerticalScrollIndicator = false
    collectionView.scrollsToTop = false
    collectionView.register(CellType.self, forCellWithReuseIdentifier: reuseID)
    collectionView.dataSource = dataSource

    collectionView.disableContentInsetAdjustmentBehavior()

    super.init(frame: frame)
    (collectionView as UIScrollView).delegate = compoundScrollDelegate
    compoundScrollDelegate.add(self)
    addSubview(collectionView)
    mask = UIView(frame: frame)
    mask!.backgroundColor = .black
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
    collectionView.frame = bounds
    mask!.frame = bounds.insetBy(dx: 0, dy: shadowInsetValue)

    if let model = model {
      updateLayoutIfNeeded(to: model)
    }
    if case let .pending(state) = deferredStateSetting {
      collectionView.performWithDetachedDelegate {
        updateContentOffset(to: state.contentPosition, animated: false)
      }
    }
    deferredStateSetting = .idle
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
    let contentOffset: CGPoint
    switch model.direction {
    case .vertical:
      contentOffset = CGPoint(x: 0, y: offset)
    case .horizontal:
      contentOffset = CGPoint(x: offset, y: 0)
    }
    collectionView.setContentOffset(contentOffset, animated: animated)
  }
}

private let shadowInsetValue = -(BlockShadow.maxOffset + 2 * BlockShadow.maxBlurRadius)

extension GalleryView: ScrollDelegate {
  public func onWillBeginDragging(_ scrollView: ScrollView) {
    scrollStartOffset = getOffset(scrollView)
  }

  public func onDidScroll(_ scrollView: ScrollView) {
    let offset = getOffset(scrollView)
    let contentPosition: GalleryViewState.Position
    switch model.scrollMode {
    case .default:
      contentPosition = .offset(
        offset,
        firstVisibleItemIndex: Int(layout.pageIndex(forContentOffset: CGFloat(offset)))
      )
    case .fixedPaging, .autoPaging:
      let pageIndex = layout.pageIndex(forContentOffset: offset)
      contentPosition = .paging(index: pageIndex)
    }

    let newState = GalleryViewState(
      contentPosition: contentPosition,
      itemsCount: model.items.count,
      isScrolling: true
    )
    setState(newState, notifyingObservers: true)
    updatesDelegate?.onContentOffsetChanged(offset, in: model)
    visibilityDelegate?.onGalleryVisibilityChanged()
  }

  public func onDidEndDragging(_ scrollView: ScrollView, willDecelerate decelerate: Bool) {
    if !decelerate {
      onDidEndScroll(scrollView)
    }
  }

  public func onDidEndDecelerating(_ scrollView: ScrollView) {
    onDidEndScroll(scrollView)
  }

  private func getOffset(_ scrollView: ScrollView) -> CGFloat {
    model.direction.isHorizontal
      ? scrollView.contentOffset.x
      : scrollView.contentOffset.y
  }

  private func onDidEndScroll(_ scrollView: ScrollView) {
    let newState = GalleryViewState(
      contentPosition: state.contentPosition,
      itemsCount: model.items.count,
      isScrolling: false
    )
    setState(newState, notifyingObservers: true)
    visibilityDelegate?.onGalleryVisibilityChanged()

    let firstVisibleItemOffset = getOffset(scrollView)
    let firstVisibleItemIndex = layout.blockFrames.firstIndex {
      (model.direction.isHorizontal ? $0.maxX : $0.maxY) >= firstVisibleItemOffset
    } ?? -1

    let lastVisibleItemOffset = model.direction.isHorizontal
      ? firstVisibleItemOffset + scrollView.bounds.width
      : firstVisibleItemOffset + scrollView.bounds.height
    let lastVisibleItemIndex = layout.blockFrames.lastIndex {
      (model.direction.isHorizontal ? $0.minX : $0.minY) < lastVisibleItemOffset
    } ?? -1

    GalleryScrollEvent(
      path: model.path,
      direction: GalleryScrollEvent.Direction(
        from: scrollStartOffset,
        to: firstVisibleItemOffset
      ),
      firstVisibleItemIndex: firstVisibleItemIndex,
      lastVisibleItemIndex: lastVisibleItemIndex
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
    let cell = collectionView.dequeueReusableCell(
      withReuseIdentifier: reuseID,
      for: indexPath
    ) as! CellType
    cell.configure(
      model: blocks[indexPath.item],
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      accessibilityElement: blocks[indexPath.item].accessibilityElement
    )
    return cell
  }
}

private let reuseID = "GalleryCollectionViewPlainCell"

extension GalleryViewModel.ScrollMode {
  fileprivate var decelerationRate: UIScrollView.DecelerationRate {
    switch self {
    case .default:
      return .normal
    case .autoPaging, .fixedPaging:
      return .fast
    }
  }
}

extension GenericCollectionViewLayout {
  fileprivate func apply(_ layout: GalleryViewLayouting?) {
    self.layout = layout
      .map { GenericCollectionLayout(frames: $0.blockFrames, contentSize: $0.contentSize) }
  }
}

extension GalleryView: VisibleBoundsTrackingContainer {}
