import Foundation
import UIKit

import CommonCorePublic

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
    guard let layout = layout else {
      return nil
    }
    return UICollectionViewLayoutAttributes(
      index: indexPath.item,
      frame: layout.frames[indexPath.item]
    )
  }

  public override func layoutAttributesForElements(in rect: CGRect)
    -> [UICollectionViewLayoutAttributes]? {
    guard let frames = layout?.frames else {
      return nil
    }
    return filter(frames: frames, intersecting: rect)
      .map { UICollectionViewLayoutAttributes(index: $0.index, frame: $0.frame) }
  }
}

extension UICollectionViewLayoutAttributes {
  fileprivate convenience init(index: Int, frame: CGRect) {
    self.init(forCellWith: IndexPath(indexes: [0, index]))
    self.frame = frame
  }
}
