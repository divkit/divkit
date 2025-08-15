#if os(iOS)
import UIKit
import VGSL

final class TabsCollectionViewFlowLayout: UICollectionViewFlowLayout {
  static let delimiterKind = "TabDelimiter"

  override var flipsHorizontallyInOppositeLayoutDirection: Bool {
    true
  }

  var tabTitleDelimiter: TabTitleDelimiterStyle? {
    didSet {
      if tabTitleDelimiter != oldValue {
        invalidateLayout()
      }
    }
  }

  private var delimiterAttributes: [UICollectionViewLayoutAttributes] = []

  override init() {
    super.init()
    scrollDirection = .horizontal
    minimumInteritemSpacing = 0
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func prepare() {
    super.prepare()

    delimiterAttributes = []
    guard let collectionView, let tabTitleDelimiter else {
      return
    }

    for section in 0..<collectionView.numberOfSections {
      let itemCount = collectionView.numberOfItems(inSection: section)

      for item in 1..<itemCount {
        let indexPath = IndexPath(item: item, section: section)
        let prevIndexPath = IndexPath(item: item - 1, section: section)

        guard let prevAttributes = layoutAttributesForItem(at: prevIndexPath),
              let currentAttributes = layoutAttributesForItem(at: indexPath) else {
          continue
        }

        let delimiterAttribute = UICollectionViewLayoutAttributes(
          forSupplementaryViewOfKind: TabsCollectionViewFlowLayout.delimiterKind,
          with: indexPath
        )

        let delimiterWidth = tabTitleDelimiter.width
        let delimiterHeight = tabTitleDelimiter.height
        let prevFrame = prevAttributes.frame
        let currentFrame = currentAttributes.frame

        let xPosition = (prevFrame.maxX + currentFrame.minX) / 2 - delimiterWidth / 2

        let cellHeight = max(prevFrame.height, currentFrame.height)
        let yPosition = prevFrame.minY + (cellHeight - delimiterHeight) / 2

        delimiterAttribute.frame = CGRect(
          x: xPosition,
          y: yPosition,
          width: delimiterWidth,
          height: delimiterHeight
        )

        delimiterAttributes.append(delimiterAttribute)
      }
    }
  }

  override func layoutAttributesForElements(in rect: CGRect)
    -> [UICollectionViewLayoutAttributes]? {
    var attributes = super.layoutAttributesForElements(in: rect)?
      .compactMap { $0.copy() as? UICollectionViewLayoutAttributes } ?? []
    attributes.append(contentsOf: delimiterAttributes.filter { $0.frame.intersects(rect) })
    return attributes
  }

  override func layoutAttributesForSupplementaryView(
    ofKind elementKind: String,
    at indexPath: IndexPath
  ) -> UICollectionViewLayoutAttributes? {
    guard elementKind == TabsCollectionViewFlowLayout.delimiterKind else {
      return super.layoutAttributesForSupplementaryView(ofKind: elementKind, at: indexPath)
    }

    return delimiterAttributes.first {
      $0.indexPath == indexPath
    }
  }

  override func invalidateLayout() {
    delimiterAttributes = []
    super.invalidateLayout()
  }
}
#endif
