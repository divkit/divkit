import DivKit
import UIKit
import VGSL

final class CollectionViewController: UIViewController {
  private lazy var divKitComponents = DivKitComponents()
  private lazy var preloader = DivViewPreloader(divKitComponents: divKitComponents)
  private var cardIds: [DivCardID] = []

  private lazy var collectionView: UICollectionView = {
    let layout = UICollectionViewFlowLayout()
    let collectionView = UICollectionView(
      frame: .zero,
      collectionViewLayout: layout
    )
    collectionView.register(
      DivCollectionViewCell.self,
      forCellWithReuseIdentifier: DivCollectionViewCell.reuseIdentifier
    )
    collectionView.dataSource = self
    collectionView.delegate = self
    return collectionView
  }()

  override func viewDidLoad() {
    super.viewDidLoad()
    title = "DivKit Collection"
    view.addSubview(collectionView)

    Task {
      await loadCards()
      collectionView.reloadData()
    }
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    collectionView.frame = view.bounds.inset(by: view.safeAreaInsets)
    collectionView.collectionViewLayout.invalidateLayout()
  }

  private func loadCards() async {
    guard let url = Bundle.main.url(forResource: "Cards", withExtension: "json") else {
      return
    }
    do {
      let data = try Data(contentsOf: url)
      let cards = try JSONSerialization.jsonObject(with: data) as! [[String: Any]]
      var sources: [DivViewSource] = []
      for (index, card) in cards.enumerated() {
        let cardId = DivCardID(rawValue: "card_\(index)")
        let cardData = try JSONSerialization.data(withJSONObject: card)
        sources.append(DivViewSource(kind: .data(cardData), cardId: cardId))
        cardIds.append(cardId)
      }
      await preloader.setSources(sources)
    } catch {
      print("Error while loading data")
    }
  }
}

extension CollectionViewController: UICollectionViewDataSource {
  func collectionView(
    _: UICollectionView,
    numberOfItemsInSection _: Int
  ) -> Int {
    cardIds.count
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(
      withReuseIdentifier: DivCollectionViewCell.reuseIdentifier,
      for: indexPath
    ) as! DivCollectionViewCell
    cell.configure(
      cardId: cardIds[indexPath.item],
      divKitComponents: divKitComponents,
      preloader: preloader
    )
    return cell
  }
}

extension CollectionViewController: UICollectionViewDelegateFlowLayout {
  func collectionView(
    _ collectionView: UICollectionView,
    layout _: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    let width = collectionView.bounds.width
    let cardId = cardIds[indexPath.item]
    if let expectedSize = preloader.expectedSize(for: cardId) {
      let size = expectedSize.sizeFor(
        parentViewSize: CGSize(width: width, height: .greatestFiniteMagnitude)
      )
      return CGSize(width: width, height: size.height)
    }
    return CGSize(width: width, height: 200)
  }
}
