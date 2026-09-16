#if os(iOS)
import UIKit
import VGSL

protocol CollectionViewAccessibilityElementProviding: AnyObject {
  func collectionView(
    _ collectionView: UICollectionView,
    accessibilityElementFor cell: UICollectionViewCell,
    at indexPath: IndexPath
  ) -> Any?
}

public final class VisibleBoundsTrackingCollectionView: NoContentTouchDelaysCollectionView,
  VisibleBoundsTrackingContainer, TapControlScrollView {
  public override func accessibilityElementCount() -> Int {
    guard accessibilityElementProvider != nil else {
      return super.accessibilityElementCount()
    }

    return (0..<numberOfSections).reduce(0) { count, section in
      count + numberOfItems(inSection: section)
    }
  }

  public override func accessibilityElement(at index: Int) -> Any? {
    guard let accessibilityElementProvider,
          let indexPath = indexPath(forAccessibilityIndex: index) else {
      return super.accessibilityElement(at: index)
    }

    guard let cell = cellForItem(at: indexPath),
          let element = accessibilityElementProvider.collectionView(
            self,
            accessibilityElementFor: cell,
            at: indexPath
          ) else {
      return super.accessibilityElement(at: index)
    }
    return element
  }

  public override func index(ofAccessibilityElement element: Any) -> Int {
    guard accessibilityElementProvider != nil,
          let indexPath = indexPath(containingAccessibilityElement: element) else {
      return super.index(ofAccessibilityElement: element)
    }
    return accessibilityIndex(for: indexPath)
  }

  public override var bounds: CGRect {
    didSet {
      if bounds.size == oldValue.size {
        let newOrigin = currentVisibleBounds.origin + bounds.origin - oldValue.origin
        currentVisibleBounds = CGRect(origin: newOrigin, size: currentVisibleBounds.size)
      }
      passVisibleBoundsChanged(
        from: oldValue.intersection(previousVisibleBounds),
        to: bounds.intersection(currentVisibleBounds)
      )
      previousVisibleBounds = currentVisibleBounds
    }
  }

  var allowTapWhileScroll = false

  private var previousVisibleBounds = CGRect.zero
  private var currentVisibleBounds = CGRect.zero

  public var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    subviews.compactMap { $0 as? VisibleBoundsTrackingView }
  }
}

private extension VisibleBoundsTrackingCollectionView {
  var accessibilityElementProvider: CollectionViewAccessibilityElementProviding? {
    dataSource as? CollectionViewAccessibilityElementProviding
  }

  func indexPath(forAccessibilityIndex index: Int) -> IndexPath? {
    guard index >= 0 else { return nil }

    var remainingIndex = index
    for section in 0..<numberOfSections {
      let itemCount = numberOfItems(inSection: section)
      if remainingIndex < itemCount {
        return IndexPath(item: remainingIndex, section: section)
      }
      remainingIndex -= itemCount
    }
    return nil
  }

  func accessibilityIndex(for indexPath: IndexPath) -> Int {
    let precedingItemsCount = (0..<indexPath.section).reduce(0) { count, section in
      count + numberOfItems(inSection: section)
    }
    return precedingItemsCount + indexPath.item
  }

  func indexPath(containingAccessibilityElement element: Any) -> IndexPath? {
    var currentView = enclosingView(of: element)
    while let view = currentView {
      if let cell = view as? UICollectionViewCell,
         let indexPath = indexPath(for: cell) {
        return indexPath
      }
      if view === self {
        break
      }
      currentView = view.superview
    }
    return nil
  }

  func enclosingView(of element: Any) -> UIView? {
    if let view = element as? UIView {
      return view
    }

    var container = (element as? UIAccessibilityElement)?.accessibilityContainer
    while let currentContainer = container {
      if let view = currentContainer as? UIView {
        return view
      }
      container = (currentContainer as? UIAccessibilityElement)?.accessibilityContainer
    }
    return nil
  }
}

extension VisibleBoundsTrackingCollectionView: VisibleBoundsTracking {
  public func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    currentVisibleBounds = to
    passVisibleBoundsChanged(from: from, to: to)
  }
}
#endif
