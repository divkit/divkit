import Foundation
import VGSLUI

struct InfiniteScroll {
  struct Position: Equatable {
    let offset: CGFloat
    let page: Int
  }

  let origins: [CGFloat]
  let bufferSize: Int
  let boundsSize: CGFloat
  let alignment: Alignment
  let insetMode: InsetMode

  var isPerformed = false

  init(
    origins: [CGFloat],
    bufferSize: Int,
    boundsSize: CGFloat = 0,
    alignment: Alignment = .center,
    insetMode: InsetMode = .fixed(values: .zero)
  ) {
    self.origins = origins
    self.bufferSize = bufferSize
    self.boundsSize = boundsSize
    self.alignment = alignment
    self.insetMode = insetMode
  }

  func getNewPosition(
    currentOffset: CGFloat
  ) -> Position? {
    guard bufferSize > 0 else {
      assertionFailure("Buffer size couldn't be less than one element for infinite scroll")
      return nil
    }

    let itemsCount = origins.count
    let cycleStartIndex = bufferSize
    let cycleEndIndex = itemsCount - bufferSize - 1

    let cycleSize = origins[cycleEndIndex + 1] - origins[cycleStartIndex]
    let correctedOffset = currentOffset + alignment.offset(bounds: boundsSize)

    let insets = insetMode.insets(forSize: boundsSize)
    if correctedOffset < origins[cycleStartIndex] + alignment.correction - insets.leading {
      return Position(offset: currentOffset + cycleSize, page: cycleEndIndex)
    } else if correctedOffset > origins[cycleEndIndex + 1] + alignment.correction - insets
      .trailing {
      return Position(offset: currentOffset - cycleSize, page: cycleStartIndex)
    }
    return nil
  }

  func getNewPositionForState(
    oldPosition: GalleryViewState.Position?,
    newPosition: GalleryViewState.Position,
    updateToPosition: (CGFloat) -> Void
  ) -> GalleryViewState.Position? {
    guard let oldStatePageIndex = oldPosition?.pageIndex,
          let newStatePageIndex = newPosition.pageIndex else {
      return nil
    }

    let firstRealPageIndex = CGFloat(bufferSize)
    let lastRealPageIndex = CGFloat(origins.count - bufferSize) - 1
    let bufferedCopyOfFirstRealPageIndex = lastRealPageIndex + 1
    let bufferedCopyOfLastRealPageIndex = firstRealPageIndex - 1

    switch (oldStatePageIndex.rounded(), newStatePageIndex.rounded()) {
    case (lastRealPageIndex, bufferedCopyOfFirstRealPageIndex):
      updateToPosition(bufferedCopyOfLastRealPageIndex)
      return .paging(index: firstRealPageIndex)
    case (firstRealPageIndex, bufferedCopyOfLastRealPageIndex):
      updateToPosition(bufferedCopyOfFirstRealPageIndex)
      return .paging(index: lastRealPageIndex)
    default:
      return newPosition
    }
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
