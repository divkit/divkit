import Foundation

enum InfiniteScroll {
  struct Position: Equatable {
    let offset: CGFloat
    let page: Int
  }

  static func getNewPosition(currentOffset: CGFloat, itemsCount: Int, size: CGFloat) -> Position? {
    guard itemsCount > 2 else {
      return nil
    }
    let pageSize = size / CGFloat(itemsCount)
    let moveSize = pageSize / 2
    let cycleSize = pageSize * CGFloat(itemsCount - 2)
    if currentOffset < moveSize {
      return Position(offset: currentOffset + cycleSize, page: itemsCount - 2)
    } else if currentOffset > cycleSize + moveSize {
      return Position(offset: currentOffset - cycleSize, page: 1)
    }
    return nil
  }
}
