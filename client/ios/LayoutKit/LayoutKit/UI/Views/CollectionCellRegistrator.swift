#if os(iOS)
import UIKit

final class CollectionCellRegistrator {
  private var registeredCellIds = Set<String>()

  func register(blocks: [Block], in collectionView: UICollectionView) {
    let ids = Set(blocks.map(\.reuseId))
    let unregisteredIds = ids.subtracting(registeredCellIds)

    for reuseId in unregisteredIds {
      registeredCellIds.insert(reuseId)
      collectionView.register(GenericCollectionViewCell.self, forCellWithReuseIdentifier: reuseId)
    }
  }
}
#endif
