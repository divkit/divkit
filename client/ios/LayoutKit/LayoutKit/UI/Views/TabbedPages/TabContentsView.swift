import UIKit

import BasePublic
import CommonCorePublic
import LayoutKitInterface

internal class TabContentsView: BlockView {
  private enum Appearance {
    static let animationDuration: TimeInterval = 0.2
  }

  private var collectionView: VisibleBoundsTrackingCollectionView!
  private var listViews: [UIView] = []
  private var footerView: UIView? {
    didSet {
      oldValue?.removeFromSuperview()
      if let footerView = footerView {
        addSubview(footerView)
      }
    }
  }

  private var backgroundView: BlockView? {
    didSet {
      oldValue?.removeFromSuperview()
      if let backgroundView = backgroundView {
        insertSubview(backgroundView, at: 0)
      }
    }
  }

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { collectionView.asArray() }
  var effectiveBackgroundColor: UIColor? { backgroundView?.effectiveBackgroundColor }

  var delegate: TabContentsViewDelegate?
  weak var updatesDelegate: TabbedPagesViewModelDelegate?
  private weak var overscrollDelegate: ScrollDelegate? {
    didSet {
      dataSource.overscrollDelegate = overscrollDelegate
    }
  }

  private(set) var selectedPageIndex: CGFloat = 0

  private var selectedPagePath: UIElementPath {
    let index = clamp(
      Int(selectedPageIndex.rounded()),
      min: 0,
      max: max(0, model.pages.count - 1)
    )
    return model.pages.element(at: index)!.path
  }

  private var layout: TabContentsViewLayout! {
    didSet {
      collectionViewLayout.layout = layout.map {
        var contentSize = BasePublic.contentSize(for: $0.pageFrames)
        if bounds.width > 0 {
          contentSize.width = contentSize.width.ceiled(toStep: bounds.width)
        }
        contentSize.height = 0
        return GenericCollectionLayout(
          frames: $0.pageFrames,
          contentSize: contentSize
        )
      }
    }
  }

  private(set) var model: TabContentsViewModel!
  private weak var observer: ElementStateObserver? {
    didSet {
      dataSource.observer = observer
    }
  }

  func configure(
    model: TabContentsViewModel,
    state: TabViewState,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let oldModel: TabContentsViewModel! = self.model
    let oldObserver = self.observer

    self.model = model
    self.observer = observer
    self.overscrollDelegate = overscrollDelegate

    if oldModel == nil || oldModel.pages !== model.pages || oldObserver !== observer {
      dataSource.models = [model.pages.map { $0.block as! CollectionCellModel }]

      if let backgroundView = backgroundView,
         let background = model.background,
         background.canConfigureBlockView(backgroundView) {
        background.configureBlockView(
          backgroundView,
          observer: observer,
          overscrollDelegate: overscrollDelegate,
          renderingDelegate: renderingDelegate
        )
      } else {
        backgroundView = model.background?.makeBlockView(
          observer: observer,
          overscrollDelegate: overscrollDelegate,
          renderingDelegate: renderingDelegate
        )
      }

      footerView = model.footer
        .flatMap { view in
          view.makeBlockView(
            observer: observer,
            overscrollDelegate: overscrollDelegate,
            renderingDelegate: renderingDelegate
          )
        }
      layout = nil
      collectionView.reloadData()
    }

    collectionView.isScrollEnabled = model.scrollingEnabled

    if oldModel != model || layout == nil || state.selectedPageIndex != selectedPageIndex {
      selectedPageIndex = state.selectedPageIndex
      setNeedsLayout()
    }
  }

  private let dataSource = GenericCollectionViewDataSource()
  private let collectionViewLayout = GenericCollectionViewLayout()

  private var scrolledPageIndex: CGFloat = 0

  init() {
    collectionView = VisibleBoundsTrackingCollectionView(
      frame: .zero,
      collectionViewLayout: collectionViewLayout
    )
    collectionView.dataSource = dataSource
    collectionView.backgroundColor = .clear
    collectionView.scrollsToTop = false
    collectionView.register(
      GenericCollectionViewCell.self,
      forCellWithReuseIdentifier: blockReuseID
    )
    collectionView.showsHorizontalScrollIndicator = false
    collectionView.showsVerticalScrollIndicator = false
    collectionView.isPagingEnabled = true

    collectionView.disableContentInsetAdjustmentBehavior()

    super.init(frame: .zero)
    addSubview(collectionView)
    collectionView.delegate = self
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func updateRelativeOffset(_ offset: CGFloat) {
    relativeContentOffset = offset
    selectedPageIndex = relativeContentOffset
  }

  func selectPageAtIndex(_ idx: CGFloat) {
    guard idx.isApproximatelyNotEqualTo(selectedPageIndex) else {
      assertionFailure("Could not scroll to same index")
      delegate?.tabContentsViewDidEndAnimation()
      return
    }
    selectedPageIndex = idx
    collectionView.setContentOffset(selectedPageContentOffset, animated: true)
  }

  private var selectedPageContentOffset: CGPoint {
    CGPoint(x: collectionView.bounds.width * selectedPageIndex, y: 0)
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    collectionView.frame = bounds
    collectionView.contentOffset = selectedPageContentOffset

    let newLayout = TabContentsViewLayout(
      pages: model.pages.map { $0.block },
      footer: model.footer,
      size: collectionView.bounds.size,
      pagesInsets: model.contentInsets.horizontalInsets
    )
    guard newLayout != layout else {
      return
    }

    layout = newLayout
    collectionView.setNeedsLayout()

    footerView?.frame = layout.footerFrame
    backgroundView?.frame = bounds
  }

  private func updateSelectedPageIndexFromRelativeContentOffset(isIntermediate: Bool) {
    notifyDelegateAboutSelectedPageIndexChange(
      selectedPageIndex: relativeContentOffset,
      countOfPages: model.pages.count,
      isIntermediate: isIntermediate
    )
  }

  private func notifyDelegateAboutSelectedPageIndexChange(
    selectedPageIndex: CGFloat,
    countOfPages: Int,
    isIntermediate: Bool
  ) {
    let roundedIndex = selectedPageIndex.rounded(.toNearestOrAwayFromZero)
    if isIntermediate,
       selectedPageIndex.isApproximatelyEqualTo(roundedIndex, withAccuracy: 0.005) {
      return
    }
    let index = isIntermediate ? selectedPageIndex : roundedIndex
    self.selectedPageIndex = index
    updatesDelegate?.onSelectedPageIndexChanged(index, inModel: model)
    let state = TabViewState(selectedPageIndex: index, countOfPages: countOfPages)
    observer?.elementStateChanged(state, forPath: model.path)
  }
}

extension TabContentsView: UICollectionViewDelegate {
  func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
    overscrollDelegate?.onDidEndDragging(scrollView, willDecelerate: decelerate)
    if !decelerate {
      sendScrollEvent()
      updateSelectedPageIndexFromRelativeContentOffset(isIntermediate: false)
    }
  }

  func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
    overscrollDelegate?.onDidEndDecelerating(scrollView)
    updateSelectedPageIndexFromRelativeContentOffset(isIntermediate: false)
    sendScrollEvent()
  }

  func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {
    overscrollDelegate?.onDidEndScrollingAnimation(scrollView)
    delegate?.tabContentsViewDidEndAnimation()
    updateSelectedPageIndexFromRelativeContentOffset(isIntermediate: false)
  }

  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    overscrollDelegate?.onDidScroll(scrollView)
    delegate?.tabContentsViewDidChangeRelativeContentOffsetTo(relativeContentOffset)

    switch model.pagesHeight {
    case .byHighestPage:
      break
    case .bySelectedPage:
      if !scrollView.isAtTop {
        scrollView.contentOffset.y = 0
      }
      updateSelectedPageIndexFromRelativeContentOffset(isIntermediate: true)
    }
  }

  func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
    scrolledPageIndex = selectedPageIndex
    overscrollDelegate?.onWillBeginDragging(scrollView)
  }

  func scrollViewDidScrollToTop(_ scrollView: UIScrollView) {
    overscrollDelegate?.onDidEndScrollingToTop(scrollView)
  }

  func scrollViewWillEndDragging(
    _ scrollView: UIScrollView,
    withVelocity velocity: CGPoint,
    targetContentOffset: UnsafeMutablePointer<CGPoint>
  ) {
    overscrollDelegate?.onWillEndDragging(
      scrollView,
      withVelocity: velocity,
      targetContentOffset: targetContentOffset
    )
  }

  func scrollViewWillBeginDecelerating(_ scrollView: UIScrollView) {
    overscrollDelegate?.onWillBeginDecelerating(scrollView)
  }

  private var relativeContentOffset: CGFloat {
    set {
      collectionView.contentOffset.x = collectionView.bounds.width * newValue
    }
    get {
      collectionView.contentOffset.x / collectionView.bounds.width
    }
  }

  private func sendScrollEvent() {
    let currentPageIndex = Int(selectedPageIndex.rounded())
    GalleryScrollEvent(
      path: selectedPagePath,
      direction: GalleryScrollEvent.Direction(
        from: scrolledPageIndex,
        to: selectedPageIndex
      ),
      firstVisibleItemIndex: currentPageIndex,
      lastVisibleItemIndex: currentPageIndex
    ).sendFrom(self)
  }
}

extension TabContentsView: VisibleBoundsTrackingContainer {}
