import Foundation
import UIKit

import CommonCorePublic

public protocol CollectionCellModel: UIViewRenderable, AccessibilityContaining {
  var reuseID: String { get }
}

public typealias CollectionHeaderModel = CollectionCellModel
public typealias CollectionFooterModel = CollectionCellModel

public final class GenericCollectionViewDataSource: NSObject, UICollectionViewDataSource {
  public var models: [[CollectionCellModel]] = []
  public var headerModels: [CollectionHeaderModel] = []
  public var footerModels: [CollectionFooterModel] = []
  public weak var observer: ElementStateObserver?
  public weak var overscrollDelegate: ScrollDelegate?
  public weak var renderingDelegate: RenderingDelegate?

  public func numberOfSections(in _: UICollectionView) -> Int {
    models.count
  }

  public func collectionView(_: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    models[section].count
  }

  public func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let model = models[indexPath.section][indexPath.item]
    let cell = collectionView.dequeueReusableCell(
      withReuseIdentifier: model.reuseID,
      for: indexPath
    ) as! GenericCollectionViewCell
    configureCell(cell, with: model)
    return cell
  }

  public func collectionView(
    _ collectionView: UICollectionView,
    viewForSupplementaryElementOfKind kind: String,
    at indexPath: IndexPath
  ) -> UICollectionReusableView {
    // The method is not triggered if header and footer size is zero or not specified
    if kind == UICollectionView.elementKindSectionHeader {
      let model = headerModels[indexPath.section]
      let headerView = collectionView.dequeueReusableSupplementaryView(
        ofKind: kind,
        withReuseIdentifier: model.reuseID,
        for: indexPath
      ) as! GenericCollectionReusableView
      headerView.configure(model: model)
      return headerView
    } else {
      assert(kind == UICollectionView.elementKindSectionFooter)
      let model = footerModels[indexPath.section]
      let footerView = collectionView.dequeueReusableSupplementaryView(
        ofKind: kind,
        withReuseIdentifier: model.reuseID,
        for: indexPath
      ) as! GenericCollectionReusableView
      footerView.configure(model: model)
      return footerView
    }
  }

  public func configureCell(_ cell: UICollectionViewCell?, at path: IndexPath) {
    guard let genericCell = cell as? GenericCollectionViewCell else {
      assertionFailure()
      return
    }
    let model = models[path.section][path.item]
    guard genericCell.reuseIdentifier == model.reuseID else {
      assertionFailure()
      return
    }

    configureCell(genericCell, with: model)
  }

  public func configureCell(_ cell: GenericCollectionViewCell, with model: CollectionCellModel) {
    cell.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      accessibilityElement: model.accessibilityElement
    )
  }
}
