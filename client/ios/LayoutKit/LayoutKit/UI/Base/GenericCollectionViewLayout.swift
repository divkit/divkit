import Foundation
import UIKit
import VGSL

public final class GenericCollectionViewLayout: UICollectionViewLayout {
  public var layout: GenericCollectionLayout? {
    didSet {
      invalidateLayout()
    }
  }

  public override var collectionViewContentSize: CGSize {
    layout?.contentSize ?? .zero
  }

  public override func layoutAttributesForItem(at indexPath: IndexPath)
    -> UICollectionViewLayoutAttributes? {
    guard let layout else {
      return nil
    }
    return UICollectionViewLayoutAttributes(
      index: indexPath.item,
      frame: layout.frames[indexPath.item]
    )
  }

  public override func shouldInvalidateLayout(forBoundsChange _: CGRect) -> Bool {
    layout?.transformation != nil
  }

  public override func layoutAttributesForElements(in rect: CGRect)
    -> [UICollectionViewLayoutAttributes]? {
    guard let frames = layout?.frames else {
      return nil
    }
    return filter(frames: frames, intersecting: rect)
      .map { UICollectionViewLayoutAttributes(index: $0.index, frame: $0.frame) }
      .map { self.transformLayoutAttributes($0) }
  }

  fileprivate func transformLayoutAttributes(_ attributes: UICollectionViewLayoutAttributes)
    -> UICollectionViewLayoutAttributes {
    guard let collectionView = self.collectionView,
          let transformation = layout?.transformation else { return attributes }

    let originalCenter = attributes.center

    if transformation.style == .overlap {
      let contentOffset = collectionView.contentOffset
      let offset = layout?.frames.first?.origin ?? .zero
      attributes.frame.origin.x = max(attributes.frame.origin.x, contentOffset.x + offset.x)
      attributes.zIndex = attributes.indexPath.item
    }

    let collectionCenter = collectionView.frame.size.width / 2

    let normalizedCenter = if transformation.scrollDirection == .horizontal {
      originalCenter.x - collectionView.contentOffset.x
    } else {
      originalCenter.y - collectionView.contentOffset.y
    }

    let maxDistance = if transformation.scrollDirection == .horizontal {
      attributes.frame.width
    } else {
      attributes.frame.height
    }
    let sign = (normalizedCenter - collectionCenter) > 0 ? 1 : -1
    let distance = min(abs(collectionCenter - normalizedCenter), maxDistance)
    let ratio = (maxDistance - distance) / maxDistance

    let alpha: CGFloat
    let scale: CGFloat
    if sign > 0 {
      alpha = transformation.nextElementAlpa + ratio * (1 - transformation.nextElementAlpa)
      scale = transformation.nextElementScale + ratio * (1 - transformation.nextElementScale)
    } else {
      alpha = transformation
        .previousElementAlpha + ratio * (1 - transformation.previousElementAlpha)
      scale = transformation
        .previousElementScale + ratio * (1 - transformation.previousElementScale)
    }
    attributes.alpha = alpha
    attributes.transform = CGAffineTransform(scale: scale)
    return attributes
  }

  public override var flipsHorizontallyInOppositeLayoutDirection: Bool {
    true
  }
}

extension UICollectionViewLayoutAttributes {
  fileprivate convenience init(index: Int, frame: CGRect) {
    self.init(forCellWith: IndexPath(indexes: [0, index]))
    self.frame = frame
  }
}
