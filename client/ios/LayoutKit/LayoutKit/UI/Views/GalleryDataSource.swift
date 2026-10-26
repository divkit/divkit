#if os(iOS)
import UIKit
import VGSL

final class GalleryDataSource: NSObject, UICollectionViewDataSource {
  var blocks: [Block] = []
  weak var observer: ElementStateObserver?
  weak var overscrollDelegate: ScrollDelegate?
  weak var renderingDelegate: RenderingDelegate?

  private var configurationDepth = 0

  var isConfiguringCell: Bool { configurationDepth > 0 }

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
    ) as! GenericCollectionViewCell

    configurationDepth += 1
    defer { configurationDepth -= 1 }
    cell.configure(
      model: block,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
    return cell
  }
}

extension GalleryDataSource: CollectionViewAccessibilityElementProviding {
  func collectionView(
    _: UICollectionView,
    accessibilityElementFor cell: UICollectionViewCell,
    at _: IndexPath
  ) -> Any? {
    let elements = topLevelAccessibilityViews(in: cell.contentView)
    return elements.count == 1 ? elements[0] : nil
  }

  private func topLevelAccessibilityViews(in view: UIView) -> [UIView] {
    guard !view.isHidden,
          view.alpha > 0.01,
          !view.accessibilityElementsHidden else {
      return []
    }
    if view.isAccessibilityElement {
      return [view]
    }
    return view.subviews.flatMap(topLevelAccessibilityViews(in:))
  }
}

extension GenericCollectionViewLayout {
  func apply(_ layout: GalleryViewLayouting?) {
    self.layout = layout
      .map { GenericCollectionLayout(
        frames: $0.blockFrames,
        contentSize: $0.contentSize,
        transformation: $0.transformation
      ) }
  }
}

extension UIScrollView {
  func interruptScroll() {
    isScrollEnabled = false
    isScrollEnabled = true
  }
}

extension GalleryViewModel.ScrollMode {
  var decelerationRate: UIScrollView.DecelerationRate {
    switch self {
    case .default:
      .normal
    case .autoPaging, .fixedPaging:
      .fast
    }
  }
}

#endif
