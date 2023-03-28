import UIKit

import CommonCorePublic
import LayoutKitInterface

final class TabListViewDelegate: NSObject, UICollectionViewDelegateFlowLayout {
  private let collectionView: UICollectionView

  private var itemSizes: [CGSize] = [] {
    didSet {
      if itemSizes != oldValue {
        collectionView.collectionViewLayout.invalidateLayout()
      }
    }
  }

  var tabs: [TabTitleViewModel] = [] {
    didSet {
      itemSizes = tabs.map { TabListItemCell.sizeForModel($0) }
    }
  }

  var size: CGSize = .zero {
    didSet {
      if oldValue != size {
        collectionView.collectionViewLayout.invalidateLayout()
      }
    }
  }

  weak var selectionDelegate: TabListViewDelegateTabSelection?
  weak var overscrollDelegate: ScrollDelegate?

  private var scrollInfo: (startOffset: CGFloat, itemPath: UIElementPath)?

  init(collectionView: UICollectionView) {
    self.collectionView = collectionView
  }

  func collectionView(
    _: UICollectionView,
    layout _: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    itemSizes[indexPath.item]
  }

  struct Layout {
    let contentOffset: CGPoint
    let pillOriginX: CGFloat
  }

  func collectionView(
    _ collectionView: UICollectionView,
    layoutForItemSelection itemSelection: CGFloat,
    offset: CGFloat?
  ) -> Layout {
    guard tabs.count > 0 else {
      return Layout(contentOffset: .zero, pillOriginX: 0)
    }
    let baseItemIndex = clamp(Int(floor(itemSelection)), min: 0, max: tabs.count - 1)
    let itemContentOffsetXs: [CGFloat] = tabs.indices.map {
      contentOffsetForItemAtIndex($0, inCollectionView: collectionView).x
    }

    let nextItemVisibilityFraction = itemSelection - CGFloat(baseItemIndex)

    let contentOffsetX: CGFloat
    let pillOriginX: CGFloat

    let bouncingLeft = itemSelection <= 0
    let bouncingRight = itemSelection >= CGFloat(tabs.count - 1)
    if bouncingLeft || bouncingRight {
      let itemWidth = size.width
      contentOffsetX = itemContentOffsetXs[baseItemIndex] + itemWidth * nextItemVisibilityFraction

      let itemIndex = bouncingLeft ? 0 : (tabs.count - 1)
      let indexPath = IndexPath(item: itemIndex, section: 0)
      let itemFrame = collectionView.safeFrameForItem(at: indexPath)
      let currentOffset = itemFrame.minX - itemContentOffsetXs[itemIndex]
      pillOriginX = currentOffset - itemWidth * nextItemVisibilityFraction
    } else {
      let indexPath = IndexPath(item: baseItemIndex, section: 0)
      let itemFrame = collectionView.safeFrameForItem(at: indexPath)
      let originX = itemFrame.minX - itemContentOffsetXs[baseItemIndex]
      let offset = itemContentOffsetXs[baseItemIndex]

      let nextIndexPath = IndexPath(item: baseItemIndex + 1, section: 0)
      let nextItemFrame = collectionView.safeFrameForItem(at: nextIndexPath)
      let nextOriginX = nextItemFrame.minX - itemContentOffsetXs[baseItemIndex + 1]
      let nextOffset = itemContentOffsetXs[baseItemIndex + 1]

      contentOffsetX = offset.interpolated(to: nextOffset, progress: nextItemVisibilityFraction)
      pillOriginX = originX.interpolated(to: nextOriginX, progress: nextItemVisibilityFraction)
    }

    if let offset = offset {
      return Layout(
        contentOffset: CGPoint(x: offset, y: 0),
        pillOriginX: pillOriginX - (offset - contentOffsetX)
      )
    } else {
      return Layout(contentOffset: CGPoint(x: contentOffsetX, y: 0), pillOriginX: pillOriginX)
    }
  }

  func collectionView(_: UICollectionView, didSelectItemAt indexPath: IndexPath) {
    let selectedItem = tabs[indexPath.item]
    selectionDelegate?.tabListViewDidSelectItemAt(
      indexPath.item,
      withUrl: selectedItem.url,
      path: selectedItem.path
    )
  }

  func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
    overscrollDelegate?.onWillBeginDragging(scrollView)

    let location = scrollView.panGestureRecognizer.location(in: scrollView)
    guard let itemIndex = collectionView.indexPathForItem(at: location)?.item else {
      assertionFailure("No item found at \(location)")
      return
    }
    let index = clamp(
      itemIndex,
      min: 0, max: max(0, tabs.count - 1)
    )
    scrollInfo = (
      startOffset: scrollView.contentOffset.x,
      itemPath: tabs[index].path
    )
  }

  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    overscrollDelegate?.onDidScroll(scrollView)
    let shouldNotify = scrollView.isChangingContentOffsetDueToUserActions
    guard shouldNotify else {
      return
    }
    selectionDelegate?.tabListViewDidScrollTo(scrollView.contentOffset.x)
  }

  func scrollViewDidScrollToTop(_ scrollView: UIScrollView) {
    overscrollDelegate?.onDidEndScrollingToTop(scrollView)
  }

  func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
    overscrollDelegate?.onDidEndDragging(scrollView, willDecelerate: decelerate)
    if !decelerate {
      sendScrollEvent(scrollView)
    }
  }

  func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
    overscrollDelegate?.onDidEndDecelerating(scrollView)
    sendScrollEvent(scrollView)
  }

  func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {
    overscrollDelegate?.onDidEndScrollingAnimation(scrollView)
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

  private func sendScrollEvent(_ scrollView: UIScrollView) {
    guard let scrollInfo = scrollInfo else {
      return
    }

    let bounds = collectionView.bounds
    let firstItemIndex = collectionView
      .indexPathForItem(at: CGPoint(x: bounds.minX, y: bounds.minY))?
      .item ?? -1
    let lastItemIndex = collectionView
      .indexPathForItem(at: CGPoint(x: bounds.maxX, y: bounds.minY))?
      .item ?? -1

    GalleryScrollEvent(
      path: scrollInfo.itemPath,
      direction: GalleryScrollEvent.Direction(
        from: scrollInfo.startOffset,
        to: scrollView.contentOffset.x
      ),
      firstVisibleItemIndex: firstItemIndex,
      lastVisibleItemIndex: lastItemIndex
    ).sendFrom(collectionView)

    self.scrollInfo = nil
  }

  private func contentOffsetForItemAtIndex(
    _ index: Int,
    inCollectionView collectionView: UICollectionView
  ) -> CGPoint {
    let collectionViewWidth = collectionView.bounds.width
    let contentSizeWidth = collectionView.contentSize.width
    guard contentSizeWidth > collectionViewWidth else {
      return .zero
    }
    let indexPath = IndexPath(item: index, section: 0)
    guard let item = collectionView.layoutAttributesForItem(at: indexPath) else {
      return .zero
    }
    let itemWidth = item.size.width
    let x = item.frame.minX - (collectionViewWidth - itemWidth) / 2.0
    let maxX = contentSizeWidth - collectionViewWidth
    return CGPoint(x: clamp(x, min: 0, max: maxX), y: 0)
  }
}

extension UICollectionView {
  fileprivate func safeFrameForItem(at indexPath: IndexPath) -> CGRect {
    layoutAttributesForItem(at: indexPath)?.frame ?? .zero
  }

  fileprivate func indexPathForItem(at location: CGPoint) -> IndexPath? {
    (indexPathAndFrames.last(where: {
      location.x >= $0.frame.minX
    }) ?? indexPathAndFrames.first)?.indexPath
  }

  private var indexPathAndFrames: [(indexPath: IndexPath, frame: CGRect)] {
    indexPaths.map {
      ($0, layoutAttributesForItem(at: $0)?.frame)
    }.compactMap { indexPath, frame in
      if let frame = frame {
        return (indexPath, frame)
      } else {
        return nil
      }
    }
  }

  private var indexPaths: [IndexPath] {
    var result: [IndexPath] = []
    for section in 0..<numberOfSections {
      for row in 0..<numberOfItems(inSection: section) {
        result.append(IndexPath(row: row, section: section))
      }
    }
    return result
  }
}
