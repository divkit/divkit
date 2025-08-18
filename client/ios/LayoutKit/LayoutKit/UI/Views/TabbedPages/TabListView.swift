#if os(iOS)
import UIKit
import VGSL

final class TabListView: UIView {
  var isMovingToSelectedItem = false
  var model: TabTitlesViewModel! {
    didSet {
      if oldValue?.items != model.items || oldValue?.tabTitleDelimiter != model.tabTitleDelimiter {
        lastLayout = nil
        dataSource = TabListViewDataSource(
          tabs: model.items,
          tabTitleDelimiterImageHolder: model.tabTitleDelimiter?.imageHolder
        )
        delegate.tabs = model.items
      }
      if oldValue?.layoutDirection != model.layoutDirection {
        collectionView.semanticContentAttribute = model
          .layoutDirection == .rightToLeft ? .forceRightToLeft : .forceLeftToRight
        delegate.layoutDirection = model.layoutDirection
      }
      collectionViewLayout.tabTitleDelimiter = model.tabTitleDelimiter
      collectionViewLayout.sectionInset = model.listPaddings
      collectionViewLayout.minimumLineSpacing = model.totalSpacing
      selectedItemBackground.backgroundColor = model.selectedBackgroundColor.systemColor
      setNeedsLayout()
    }
  }

  private let collectionView: UICollectionView
  private let delegate: TabListViewDelegate
  private let collectionViewLayout = TabsCollectionViewFlowLayout()

  private var selectedItemBackground: UIView!
  private var setInitialModel = false

  private var dataSource: TabListViewDataSource! {
    didSet {
      collectionView.dataSource = dataSource
    }
  }

  private var lastLayout: (
    bounds: CGRect,
    contentOffset: CGPoint,
    pillOffset: CGFloat,
    contentSize: CGSize,
    selection: CGFloat
  )?

  private var animationInfo: AnimationInfo? {
    didSet {
      setNeedsLayout()
    }
  }

  var selectionDelegate: TabListViewDelegateTabSelection? {
    get { delegate.selectionDelegate }
    set { delegate.selectionDelegate = newValue }
  }

  weak var overscrollDelegate: ScrollDelegate? {
    get { delegate.overscrollDelegate }
    set { delegate.overscrollDelegate = newValue }
  }

  init() {
    collectionView = makeCollectionView(layout: collectionViewLayout)
    delegate = TabListViewDelegate(collectionView: collectionView)

    super.init(frame: .zero)
    clipsToBounds = true

    selectedItemBackground = UIView(frame: .zero)
    addSubview(selectedItemBackground)

    collectionView.delegate = delegate
    addSubview(collectionView)
  }

  @available(*, unavailable)
  required init(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    let contentOffset: CGPoint
    let pillOriginX: CGFloat
    let selection: CGFloat

    collectionView.frame = bounds
    delegate.size = bounds.size

    if let animationInfo {
      let fromLayout = delegate.collectionView(
        collectionView,
        layoutForItemSelection: animationInfo.fromSelection,
        offset: animationInfo.fromOffset
      )
      let toLayout = delegate.collectionView(
        collectionView,
        layoutForItemSelection: animationInfo.toSelection,
        offset: nil
      )
      let progress = animationInfo.progress(withCurrentSelection: model.selection)
      contentOffset = fromLayout.contentOffset.interpolated(
        to: toLayout.contentOffset,
        progress: progress
      )
      pillOriginX = fromLayout.pillOriginX.interpolated(
        to: toLayout.pillOriginX,
        progress: progress
      )
      selection = animationInfo.fromSelection.interpolated(
        to: animationInfo.toSelection,
        progress: progress
      )
    } else {
      let newLayoutForSelection = delegate.collectionView(
        collectionView,
        layoutForItemSelection: model.selection,
        offset: model.offset
      )
      contentOffset = newLayoutForSelection.contentOffset
      pillOriginX = newLayoutForSelection.pillOriginX
      selection = model.selection
    }

    let contentSize = collectionView.contentSize
    let newLayout = (
      bounds: bounds,
      contentOffset: contentOffset,
      pillOffset: pillOriginX,
      contentSize: contentSize,
      selection: selection
    )

    guard lastLayout == nil || lastLayout! != newLayout else {
      return
    }

    lastLayout = newLayout

    collectionView.contentOffset = contentOffset
    collectionView.layoutIfNeeded()

    let pillSize = CGSize(
      width: model.items.map(\.totalSize.width).interim(at: selection),
      height: model.items.first!.totalSize.height
    )
    let halfPill = pillSize.height / 2

    selectedItemBackground.frame = CGRect(
      x: pillOriginX,
      y: bounds.inset(by: model.listPaddings).midY - halfPill,
      width: pillSize.width,
      height: pillSize.height
    ).roundedToScreenScale

    let boundary = model.cornerRadius.map(BoundaryTrait.clipCorner) ??
      BoundaryTrait.cornerRadius(halfPill)
    selectedItemBackground.layer.apply(boundary: boundary)
  }

  func prepareForScrollingAnimation(to idx: CGFloat) {
    assert(animationInfo == nil, "First animation should be finished before starting second one")
    if let offset = model.offset {
      animationInfo = AnimationInfo(
        fromSelection: model.selection,
        fromOffset: offset,
        toSelection: idx
      )
    }
  }

  func endScrollingAnimation() {
    animationInfo = nil
  }

  func setInitialModel(_ model: TabTitlesViewModel) {
    guard !setInitialModel else { return }
    self.model = model
    setInitialModel = true
  }
}

extension CGPoint {
  fileprivate func interpolated(to point: CGPoint, progress: CGFloat) -> CGPoint {
    CGPoint(
      x: x.interpolated(to: point.x, progress: progress),
      y: y.interpolated(to: point.y, progress: progress)
    )
  }
}

private struct AnimationInfo {
  let fromSelection: CGFloat
  let fromOffset: CGFloat
  let toSelection: CGFloat

  init?(
    fromSelection: CGFloat,
    fromOffset: CGFloat,
    toSelection: CGFloat
  ) {
    guard fromSelection.isApproximatelyNotEqualTo(toSelection) else {
      assertionFailure("Could not create object with equal from/to selection")
      return nil
    }
    self.fromSelection = fromSelection
    self.fromOffset = fromOffset
    self.toSelection = toSelection
  }

  func progress(withCurrentSelection selection: CGFloat) -> CGFloat {
    let from = min(fromSelection, toSelection)
    let to = max(fromSelection, toSelection)
    let clamped = clamp(selection, min: from, max: to)
    let progress = (clamped - fromSelection) / (toSelection - fromSelection)
    return progress
  }
}

private func makeCollectionView(layout: UICollectionViewLayout) -> UICollectionView {
  let collectionView = ExclusiveTouchCollectionView(frame: .zero, collectionViewLayout: layout)
  collectionView.register(TabListItemCell.self, forCellWithReuseIdentifier: TabListItemCell.reuseID)
  collectionView.register(
    TabTitleDelimiterReusableView.self,
    forSupplementaryViewOfKind: TabsCollectionViewFlowLayout.delimiterKind,
    withReuseIdentifier: TabTitleDelimiterReusableView.reuseID
  )
  collectionView.showsHorizontalScrollIndicator = false
  collectionView.backgroundColor = .clear
  collectionView.scrollsToTop = false
  collectionView.alwaysBounceHorizontal = true
  collectionView.contentInsetAdjustmentBehavior = .never
  return collectionView
}
#endif
