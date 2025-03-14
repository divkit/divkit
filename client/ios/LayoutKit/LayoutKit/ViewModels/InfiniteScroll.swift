import Foundation
import VGSLUI

enum InfiniteScroll {
  struct Position: Equatable {
    let offset: CGFloat
    let page: Int
  }

  static func getNewPosition(
    currentOffset: CGFloat,
    origins: [CGFloat],
    bufferSize: Int,
    boundsSize: CGFloat = 0,
    alignment: Alignment = .center
  ) -> Position? {
    let itemsCount = origins.count
    let cycleStartIndex = bufferSize
    let cycleEndIndex = itemsCount - bufferSize - 1

    let cycleSize = origins[cycleEndIndex + 1] - origins[cycleStartIndex]
    let correctedOffset = currentOffset + alignment.offset(bounds: boundsSize)

    if correctedOffset < origins[cycleStartIndex] + alignment.correction {
      return Position(offset: currentOffset + cycleSize, page: cycleEndIndex)
    } else if correctedOffset > origins[cycleEndIndex + 1] + alignment.correction {
      return Position(offset: currentOffset - cycleSize, page: cycleStartIndex)
    }
    return nil
  }
}

extension Alignment {
  fileprivate var correction: CGFloat {
    switch self {
    case .leading:
      -1
    case .center:
      0
    case .trailing:
      1
    }
  }

  fileprivate func offset(bounds: CGFloat) -> CGFloat {
    switch self {
    case .leading:
      0
    case .center:
      bounds / 2
    case .trailing:
      bounds
    }
  }
}
