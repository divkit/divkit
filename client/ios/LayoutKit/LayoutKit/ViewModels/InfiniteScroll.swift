import Foundation

enum InfiniteScroll {
  struct Position: Equatable {
    let offset: CGFloat
    let page: Int
  }

  static func getNewPosition(currentOffset: CGFloat, origins: [CGFloat]) -> Position? {
    guard origins.count > 2 else {
      return nil
    }
    let itemsCount = origins.count
    let cycleSize = origins[itemsCount - 1] - origins[1]
    let jumpOffset = cycleSize / CGFloat((itemsCount - 2) * 2) + origins[0]
    if currentOffset < jumpOffset {
      return Position(offset: currentOffset + cycleSize, page: itemsCount - 2)
    } else if currentOffset > cycleSize + jumpOffset {
      return Position(offset: currentOffset - cycleSize, page: 1)
    }
    return nil
  }
}
