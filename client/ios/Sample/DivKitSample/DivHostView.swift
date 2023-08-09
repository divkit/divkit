import UIKit

import BasePublic
import CommonCorePublic
import DivKit
import LayoutKit

final class DivHostView: UICollectionView {
  private let components: DivKitComponents
  var items: [DivData] = []

  init(components: DivKitComponents) {
    self.components = components
    super.init(frame: .zero, collectionViewLayout: UICollectionViewFlowLayout())
    setupCollectionView()
  }

  private func setupCollectionView() {
    register(Cell.self, forCellWithReuseIdentifier: Cell.reuseIdentifier)
    dataSource = self
    delegate = self
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  private class Cell: UICollectionViewCell {
    static let reuseIdentifier = "cell"

    var divView: DivView? {
      didSet {
        guard let divView else { return }
        addSubview(divView)
      }
    }

    func configureCell(divKitComponents: DivKitComponents, divData: DivData) {
      if divView == nil {
        self.divView = DivView(divKitComponents: divKitComponents)
      }
      divView?.setSource(.init(kind: .divData(divData), cardId: DivCardID(rawValue: divData.logId)))
    }

    override func layoutSubviews() {
      super.layoutSubviews()
      divView?.frame = CGRect(
        origin: .zero,
        size: divView?.intrinsicContentSize(for: bounds.size) ?? .zero
      )
    }
  }
}

extension DivHostView: UICollectionViewDataSource {
  func collectionView(
    _: UICollectionView,
    numberOfItemsInSection _: Int
  ) -> Int {
    items.count
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(
      withReuseIdentifier: Cell.reuseIdentifier,
      for: indexPath
    ) as! Cell
    cell.configureCell(divKitComponents: components, divData: items[indexPath.row])
    return cell
  }
}

extension DivHostView: UICollectionViewDelegateFlowLayout {
  func collectionView(
    _: UICollectionView,
    layout _: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    let view = DivView(divKitComponents: components)
    view.setSource(.init(
      kind: .divData(items[indexPath.row]),
      cardId: DivCardID(rawValue: items[indexPath.row].logId)
    ))
    return view.intrinsicContentSize(for: bounds.size)
  }
}
