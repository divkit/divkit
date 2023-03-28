import CoreGraphics

import CommonCorePublic

struct ConstrainedBlockSize {
  var size: CGFloat
  var minSize: CGFloat
  var coefficient: CGFloat = 1
}

extension ConstrainedBlockSize {
  var normalizedDecrease: CGFloat { blockDecrease / coefficient }
  var blockDecrease: CGFloat { size - minSize }
}

func decreaseConstrainedBlockSizes(
  blockSizes: [ConstrainedBlockSize],
  lengthToDecrease: CGFloat
) -> IndexingIterator<[CGFloat]> {
  ///
  /// Here is the algorithm for compressing wrap content constrained blocks.
  ///
  /// Given array of n constrained blocks along one direction.
  /// It is necessary to compress the blocks proportionally to the base size
  /// so that as much as possible fit on the screen.
  ///
  /// ```
  /// struct ConstrainedBlock {
  ///  let size: CGFloat
  ///  let minSize: CGFloat
  /// }
  /// ```
  ///
  /// The maximum size by which we can compress block is:
  /// `constrainedBlock.size - constrainedBlock.minSize`
  /// We can sort array by this value and then give the sizes to the blocks in `O(nlogn)`
  /// But an additional condition is to compress proportionally.
  /// Let's look at 2 constrained blocks with sizes `s1`, `s2`.
  /// If we descreased `s1` by `x` point, `s2` descreased by `s2.size / s1.size * x` points.
  /// Let's add a coefficient that normalizes blocks relative to the minimum block size.
  /// Let `smin`  be the block with the minimum size.
  /// If we descreased `s1` by `x` point, `s2` descreased by
  /// `x / (s1.size / smin.size) * (s2.size / smin.size)` points,
  /// which equal `s2.size / s1.size * x` points.
  /// Let `coefficient` is `block.size / blockmin.size` value.
  ///
  /// So how do we find the block that will shrink to a minimum the fastest?
  /// This is the block with the minimum value of
  /// `(constrainedBlock.size - constrainedBlock.minSize) / coef`
  ///
  /// Algorithm:
  /// ```
  /// 1) Let's sort the blocks in ascending order of the possibility of compression.
  /// 2) At the i-th iteration, we will try to compress the i-th block.
  /// We can compress i-th block by
  /// min(block.size - block.minSize - currentBlockDecrease * block.coefficient,
  ///     lengthToDecrease / sum(sortedBlocks[i..n].coefficient) * block.coefficient)
  /// ```
  ///

  guard !blockSizes.isEmpty else { return [].makeIterator() }
  var blockSizes = blockSizes
  var lengthToDecrease = lengthToDecrease
  var summaryBlockDecrease: CGFloat = 0
  var (sumCoefficients, sortedBlockSizes) = sort(blockSizes: blockSizes)
  for (index, blockSize) in sortedBlockSizes {
    let decrease = min(
      blockSize.normalizedDecrease - summaryBlockDecrease,
      lengthToDecrease / sumCoefficients
    )
    lengthToDecrease -= decrease * sumCoefficients
    summaryBlockDecrease += decrease
    blockSizes[index].size -= summaryBlockDecrease * blockSize.coefficient
    sumCoefficients -= blockSize.coefficient
  }
  return blockSizes.map(\.size).makeIterator()
}

private func sort(blockSizes: [ConstrainedBlockSize])
  -> (CGFloat, [(Int, ConstrainedBlockSize)]) {
  let minBlockSize = blockSizes.map(\.size).min()!
  let blocksWithCoefficients = blockSizes.map {
    ConstrainedBlockSize(
      size: $0.size,
      minSize: $0.minSize,
      coefficient: $0.size / minBlockSize
    )
  }
  return (
    blocksWithCoefficients.reduce(0) { $0 + $1.coefficient },
    blocksWithCoefficients.enumerated().sorted {
      $0.element.normalizedDecrease < $1.element.normalizedDecrease
    }
  )
}
