import UIKit

import DivKit

final class DivHostView: UICollectionView {
  private let components: DivKitComponents
  private let preloader: DivViewPreloader

  var items: [DivCardID] = [] {
    didSet {
      reloadData()
    }
  }

  init(components: DivKitComponents, preloader: DivViewPreloader) {
    self.components = components
    self.preloader = preloader

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

    func configureCell(
      divKitComponents: DivKitComponents,
      preloader: DivViewPreloader,
      cardID: DivCardID
    ) {
      if divView == nil {
        self.divView = DivView(
          divKitComponents: divKitComponents,
          divViewPreloader: preloader
        )
      }

      divView?.showCardId(cardID)
    }

    override func layoutSubviews() {
      super.layoutSubviews()

      guard let divView else {
        return
      }

      divView.frame = CGRect(
        origin: .zero,
        size: divView.cardSize?.sizeFor(parentViewSize: bounds.size) ?? .zero
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
    cell.configureCell(
      divKitComponents: components,
      preloader: preloader,
      cardID: items[indexPath.row]
    )
    return cell
  }
}

extension DivHostView: UICollectionViewDelegateFlowLayout {
  func collectionView(
    _: UICollectionView,
    layout _: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    let cardId = items[indexPath.row]
    return preloader.expectedSize(for: cardId)?.sizeFor(parentViewSize: bounds.size) ?? .zero
  }
}

extension DivData {
  var cardId: DivCardID {
    DivCardID(rawValue: logId)
  }
}
