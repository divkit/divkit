import UIKit

import CommonCore
import DivKit
import LayoutKit

final class DivHostView: UICollectionView {
  private let components: DivKitComponents
  private var blocks: [Block] = []

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

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func setCards(_ cards: [DivData]) {
    self.blocks = cards.compactMap { card in
      let context = components.makeContext(
        cardId: DivCardID(rawValue: card.logId),
        cachedImageHolders: []
      )
      return try? card.makeBlock(context: context)
    }
  }

  private class Cell: UICollectionViewCell {
    static let reuseIdentifier = "cell"

    struct State {
      var block: Block
      var view: BlockView
    }

    var state: State?

    func setBlock(_ block: Block) {
      let view = block.reuse(
        state?.view,
        observer: nil,
        overscrollDelegate: nil,
        renderingDelegate: nil,
        superview: self
      )
      state = State(block: block, view: view)
    }

    override func layoutSubviews() {
      super.layoutSubviews()
      if let state = state {
        let height = state.block.heightOfVerticallyNonResizableBlock(forWidth: bounds.width)
        state.view.frame = CGRect(x: 0, y: 0, width: bounds.width, height: height)
      }
    }
  }
}

extension DivHostView: UICollectionViewDataSource {
  func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
    return blocks.count
  }

  func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Cell.reuseIdentifier, for: indexPath) as! Cell
    let block = blocks[indexPath.row]
    cell.setBlock(block)
    return cell
  }
}

extension DivHostView: UICollectionViewDelegateFlowLayout {
  func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
    let block = blocks[indexPath.row]
    let height = block.heightOfVerticallyNonResizableBlock(forWidth: bounds.width)
    return CGSize(width: bounds.width, height: height)
  }
}
