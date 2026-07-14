import CoreGraphics
import VGSL

struct ConstrainedBlockSize {
  var size: CGFloat
  var minSize: CGFloat
  var maxSize: CGFloat = .infinity
}

func decreaseConstrainedBlockSizes(
  blockSizes: [ConstrainedBlockSize],
  lengthToDecrease: CGFloat
) -> IndexingIterator<[CGFloat]> {
  guard lengthToDecrease > 0 else {
    return blockSizes.map { min($0.size, $0.maxSize) }.makeIterator()
  }

  var result = blockSizes.map(\.size)
  var remainingLength = result.reduce(0, +)
  var remainingDecrease = lengthToDecrease
  let processingOrder = blockSizes.indices.sorted {
    let lhs = blockSizes[$0].minSize.safeDivide(blockSizes[$0].size)
    let rhs = blockSizes[$1].minSize.safeDivide(blockSizes[$1].size)
    return lhs == rhs ? $0 < $1 : lhs > rhs
  }

  for index in processingOrder {
    let block = blockSizes[index]
    let share = block.size.safeDivide(remainingLength) * remainingDecrease
    let newSize = clamp(block.size - share, min: block.minSize, max: block.maxSize)
    result[index] = newSize
    remainingDecrease -= block.size - newSize
    remainingLength -= block.size
  }

  return result.makeIterator()
}

extension CGFloat {
  fileprivate func safeDivide(_ divider: CGFloat) -> CGFloat {
    divider.isZero ? 0 : (self / divider)
  }
}
