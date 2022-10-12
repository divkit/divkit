import UIKit

import CommonCore
import DivKit
import LayoutKit

final class DivHostView: UICollectionView {
  private let components: DivKitComponents
  private var items: [Item] = []

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

  func setData(_ items: [DivData]) {
    self.items = items.compactMap { makeItem(data: $0) }
  }
  
  func reloadItem(cardId: String) {
    guard let index = items.firstIndex(where: { $0.data.logId == cardId }) else {
      return
    }
    
    let item = items[index]
    items[index] = makeItem(
      data: item.data,
      cachedImageHolders: item.block.getImageHolders()
    )
    
    reloadItems(at: [IndexPath(item: index, section: 0)])
  }

  private func makeItem(
    data: DivData,
    cachedImageHolders: [ImageHolder] = []
  ) -> Item {
    let context = components.makeContext(
      cardId: DivCardID(rawValue: data.logId),
      cachedImageHolders: cachedImageHolders
    )
    return Item(data: data, block: try! data.makeBlock(context: context))
  }
  
  private struct Item {
    let data: DivData
    let block: Block
  }
  
  private class Cell: UICollectionViewCell {
    static let reuseIdentifier = "cell"

    struct State {
      let block: Block
      let view: BlockView
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
        let blockSize = state.block.size(forResizableBlockSize: bounds.size)
        state.view.frame = CGRect(origin: .zero, size: blockSize)
      }
    }
  }
}

extension DivHostView: UICollectionViewDataSource {
  func collectionView(
    _ collectionView: UICollectionView,
    numberOfItemsInSection section: Int
  ) -> Int {
    return items.count
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Cell.reuseIdentifier, for: indexPath) as! Cell
    let block = items[indexPath.row].block
    cell.setBlock(block)
    return cell
  }
}

extension DivHostView: UICollectionViewDelegateFlowLayout {
  func collectionView(
    _ collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    let block = items[indexPath.row].block
    let height = block.heightOfVerticallyNonResizableBlock(forWidth: bounds.width)
    return CGSize(width: bounds.width, height: height)
  }
}
