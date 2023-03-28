import CoreGraphics

import CommonCorePublic

extension GridBlock.Layout {
  init(
    size: CGSize,
    items: [GridBlock.Item],
    grid: Grid,
    contentAlignment: BlockAlignment2D
  ) {
    let columnWeights = calculateWeights(at: .horizontal, for: items, in: grid)
    let horizontallyNonResizableItems = items.map(HorizontallyNonResizableItem.init)
    let columnWidths = calculateSizes(
      for: horizontallyNonResizableItems,
      grid: grid,
      weights: columnWeights,
      direction: .horizontal,
      boundingSize: size.width
    )

    let indexToCoord = grid.calculateItemIndexToCoord()
    let verticallyNonResizableItems: [VerticallyNonResizableItem?] = items.enumerated().map {
      let column = indexToCoord[$0.offset]!.column
      let columnRange = column..<(column + $0.element.span.columns)
      return VerticallyNonResizableItem(
        $0.element,
        width: columnWidths[columnRange].reduce(0, +)
      )
    }
    let rowWeights = calculateWeights(at: .vertical, for: items, in: grid)
    let rowHeights = calculateSizes(
      for: verticallyNonResizableItems,
      grid: grid,
      weights: rowWeights,
      direction: .vertical,
      boundingSize: size.height
    )

    let calculatedSize = CGSize(
      width: columnWidths.reduce(0, +),
      height: rowHeights.reduce(0, +)
    )

    self.size = CGSize(
      width: min(calculatedSize.width, size.width),
      height: min(calculatedSize.height, size.height)
    )
    let offset = contentAlignment.offset(forAvailableSpace: size - calculatedSize)
    let columnsPartialSums = ([offset.x] + columnWidths).partialSums
    let rowsPartialSums = ([offset.y] + rowHeights).partialSums
    itemFrames = items.enumerated().map {
      let (row, column) = indexToCoord[$0.offset]!
      let cellX = columnsPartialSums[column]
      let maxX = columnsPartialSums[column + $0.element.span.columns]
      let cellY = rowsPartialSums[row]
      let maxY = rowsPartialSums[row + $0.element.span.rows]
      let cellFrame = CGRect(x: cellX, y: cellY, width: maxX - cellX, height: maxY - cellY)
      let width = horizontallyNonResizableItems[$0.offset]?.size ?? cellFrame.width
      let height = verticallyNonResizableItems[$0.offset]?.size ?? cellFrame.height

      return CGRect(
        x: cellX + $0.element.alignment.horizontal
          .offset(forAvailableSpace: cellFrame.width - width),
        y: cellY + $0.element.alignment.vertical
          .offset(forAvailableSpace: cellFrame.height - height),
        width: width,
        height: height
      )
    }
  }

  // this works faster than calculateNonResizableColumnWidths
  static func isGrid(
    _ grid: Grid,
    with items: [GridBlock.Item],
    resizableAtDirection direction: GridBlock.Direction
  ) -> Bool {
    let weights = calculateWeights(at: direction, for: items, in: grid)
    // if there are not weighed dimensions, whole grid is not resizable
    if !weights.contains(where: { $0 != nil }) {
      return false
    }

    let nonResizableItems = items.map {
      // unmeasured size is ok here as we only need to know
      // if it is possible to calculate width per weight or not,
      // no matter what result is
      UnmeasuredNonResizableItem($0, direction: direction)
    }

    // if we can't calculate weightToSize, layout is resizable
    return calculateWeightToIntrinsicSize(
      for: nonResizableItems,
      grid: grid,
      weights: weights,
      direction: direction
    ) == nil
  }

  static func calculateIntrinsicColumnWidths(
    items: [GridBlock.Item],
    grid: Grid
  ) -> [CGFloat] {
    let columnWeights = calculateWeights(at: .horizontal, for: items, in: grid)

    // prevent from calculating width more than once per non-resizable item
    let horizontallyNonResizableItems = items.map(HorizontallyNonResizableItem.init)
    let weightToWidth = calculateWeightToIntrinsicSize(
      for: horizontallyNonResizableItems,
      grid: grid,
      weights: columnWeights,
      direction: .horizontal
    ) ?? 0

    return calculateSizes(
      for: horizontallyNonResizableItems,
      in: grid,
      weights: columnWeights,
      weightToSize: weightToWidth,
      direction: .horizontal
    )
  }
}

extension Grid {
  fileprivate func calculateItemIndexToCoord() -> [Int: (row: Int, column: Int)] {
    var itemIndexToCoord: [Int: (row: Int, column: Int)] = [:]
    forEach {
      let idx = itemsIndices[$0]
      itemIndexToCoord[idx] = itemIndexToCoord[idx] ?? $0
    }
    return itemIndexToCoord
  }
}

private func makeEnumeratedItems(
  withSpan span: Int,
  from items: [NonResizableItem?]
) -> [(offset: Int, element: NonResizableItem)] {
  items
    .enumerated()
    .compactMap {
      if let element = $0.element, element.span == span {
        return ($0.offset, element)
      } else {
        return nil
      }
    }
}

private func calculateSizes<T: NonResizableItem>(
  for nonResizableItems: [T?],
  grid: Grid,
  weights: [LayoutTrait.Weight?],
  direction: Direction,
  boundingSize: CGFloat
) -> [CGFloat] {
  let weightToSize = calculateWeightToSize(
    for: nonResizableItems,
    grid: grid,
    weights: weights,
    direction: direction,
    boundingSize: boundingSize
  )

  return calculateSizes(
    for: nonResizableItems,
    in: grid,
    weights: weights,
    weightToSize: weightToSize,
    direction: direction
  )
}

private func calculateSizes<T: NonResizableItem>(
  for items: [T?],
  in grid: Grid,
  weights: [LayoutTrait.Weight?],
  weightToSize: CGFloat,
  direction: Direction
) -> [CGFloat] {
  let resizableSizes = weights.map {
    $0.map { $0.rawValue * weightToSize }
  }
  var sizes = resizableSizes.map { $0 ?? 0 }
  let resizableIndices = Set(
    weights
      .enumerated()
      .filter { $0.element != nil }
      .map { $0.offset }
  )
  let spans = Set(items.compactMap { $0?.span }).sorted(by: <)

  let itemIndexToCoord = grid.calculateItemIndexToCoord()

  for span in spans {
    let items = makeEnumeratedItems(withSpan: span, from: items).map {
      (
        firstIndex: direction.selectDimension(from: itemIndexToCoord[$0.offset]!),
        element: $0.element
      )
    }.sorted(by: { $0.firstIndex > $1.firstIndex })
    for (firstIndex, item) in items {
      let indices = firstIndex..<(firstIndex + span)

      ///
      /// Here is a strategy to disambiuate case which simplest variant
      /// can be described as follows:
      ///
      /// Given a grid with a single cell with intrinsic size for one of the dimensions,
      /// and a span of 2 for direction that corresponds to this dimension,
      /// how do you distribute its size between spanned direction?
      ///
      /// Here we choose a strategy to give all the size
      /// to the first item, and leave zero space for the second,
      /// while iterating with increasing span
      /// and decreasing position in grid (last row/column to first).
      /// Increasing span reduces possibility of using up more space
      /// than we potentially could as there's less ambiguity
      /// for cells with smaller span compared to ones with larger span.
      ///
      /// To understand why the reverse order of rows and columns is used,
      /// consider the following layout:
      ///
      /// |  1:2  |2:1|
      /// |3:1|  4:2  |
      ///
      /// where:
      /// - columnCount is 3;
      /// - all row spans are 1;
      /// - all cells have intrinsic width;
      /// - in X:Y label X means linear cell number and Y is column span.
      ///
      /// Let `w(x)` be intrinsic width of x-th cell,
      /// and `BigWidth == w(1) == w(4) > w(2) == w(3) == SmallWidth`.
      ///
      /// If we went from left to right, we'd get here is:
      /// 1. Span = 1, all items handled, column widths are: `[SmallWidth, 0, SmallWidth]`;
      /// 2. Span = 2, first item handled, column widths are: `[BigWidth, 0, SmallWidth]`;
      /// 3. Span = 2, second item handled, column widths are: `[BigWidth, BigWidth - SmallWidth,
      /// BigWidth]`.
      ///
      /// However, obviously, the optimal widths would be
      /// `[SmallWidth, BigWidth - SmallWidth, BigWidth]`,
      /// and the oversize we've got is equal to `BigWidth - SmallWidth`.
      ///
      /// By going from right to left, this issue is fixed:
      /// on step 2 widths are `[SmallWidth, BigWidth - SmallWidth, BigWidth]`,
      /// and on step 3 they are unchanged.
      ///

      let resizableItemIndices = Set(indices.filter(resizableIndices.contains))
      if resizableItemIndices.count != indices.count {
        let currentSize = indices.map { sizes[$0] }.reduce(0, +)
        let remainingSize = item.size - currentSize
        guard remainingSize > 0 else {
          continue
        }

        let nonResizableIndices = Set(indices.filter { !resizableIndices.contains($0) })
        let notYetSizedIndices = Set(nonResizableIndices.filter {
          sizes[$0].isApproximatelyEqualTo(0)
        })
        /// Here we try to distribute remaining size evenly
        /// through indices with zero size, if possible,
        /// and fallback to distribiting only through indices
        /// without weight (if there is a weight in the index,
        /// all the size for this index is already considered
        /// by weightToSize).
        /// This won't lead to "funny" cases thanks to
        /// iterations order described above.
        let indicesToDistributeSize = notYetSizedIndices.isEmpty
          ? nonResizableIndices
          : notYetSizedIndices
        let remainginWidthPerIndex = remainingSize / CGFloat(indicesToDistributeSize.count)
        indicesToDistributeSize.forEach {
          sizes[$0] += remainginWidthPerIndex
        }
      }
    }
  }

  return sizes
}

private func calculateWeights(
  at direction: Direction,
  for items: [GridBlock.Item],
  in grid: Grid
) -> [LayoutTrait.Weight?] {
  let weightedItems = items.map { WeightedItem($0, direction: direction) }
  let resultCount =
    direction.selectDimension(from: (row: grid.rowCount, column: grid.columnCount))
  var result = [LayoutTrait.Weight?](repeating: nil, times: try! UInt(value: resultCount))
  grid.forEach { coord in
    if let item = weightedItems[grid.itemsIndices[coord]] {
      let resultIndex = direction.selectDimension(from: coord)
      let weightPerSpan = item.weight.rawValue / CGFloat(item.span)
      result[resultIndex] = LayoutTrait.Weight(
        rawValue: max(result[resultIndex]?.rawValue ?? weightPerSpan, weightPerSpan)
      )!
    }
  }

  return result
}

private func calculateWeightToSize<T: NonResizableItem>(
  for nonResizableItems: [T?],
  grid: Grid,
  weights: [LayoutTrait.Weight?],
  direction: Direction,
  boundingSize: CGFloat
) -> CGFloat {
  guard boundingSize.isFinite else {
    return 0
  }

  let weightToIntrinsicSize = calculateWeightToIntrinsicSize(
    for: nonResizableItems,
    grid: grid,
    weights: weights,
    direction: direction
  ) ?? 0

  let weightsSum = weights.compactMap { $0?.rawValue }.reduce(0, +)

  let weightToBoundingSize: CGFloat
  if weightsSum > 0 {
    let intrinsicSizes = calculateSizes(
      for: nonResizableItems,
      in: grid,
      weights: weights,
      weightToSize: 0,
      direction: direction
    )
    weightToBoundingSize = max(0, boundingSize - intrinsicSizes.reduce(0, +)) / weightsSum
  } else {
    weightToBoundingSize = 0
  }

  return max(weightToBoundingSize, weightToIntrinsicSize)
}

private func calculateWeightToIntrinsicSize<T: NonResizableItem>(
  for nonResizableItems: [T?],
  grid: Grid,
  weights: [LayoutTrait.Weight?],
  direction: Direction
) -> CGFloat? {
  var result: CGFloat?
  var checkedItems: Set<Int> = []

  guard let minWeight = weights.compactMap({ $0?.rawValue }).min() else {
    return nil
  }

  let maxSize = nonResizableItems.compactMap { $0?.size }.max() ?? 0
  let maxPossibleWeightToSize = maxSize / minWeight

  let nonResizableSizes = calculateSizes(
    for: nonResizableItems,
    in: grid,
    weights: weights,
    weightToSize: maxPossibleWeightToSize,
    direction: direction
  )

  grid.forEach { coord in
    let idx = grid.itemsIndices[coord]
    guard !checkedItems.contains(idx) else {
      return
    }
    checkedItems.insert(idx)
    if let item = nonResizableItems[idx] {
      let dimension = direction.selectDimension(from: coord)
      let range = dimension..<(dimension + item.span)
      let weight = weights[range]
        .compactMap { $0?.rawValue }.reduce(0, +)
      let usedSize = range.compactMap {
        weights[$0] == nil
          ? nonResizableSizes[$0]
          : nil
      }.reduce(0, +)
      if weight > 0 {
        result = max(result ?? 0, (item.size - usedSize) / weight)
      }
    }
  }

  return result
}

private protocol NonResizableItem {
  var span: Int { get }
  var size: CGFloat { get }
}

private struct UnmeasuredNonResizableItem: NonResizableItem {
  let span: Int
  let size: CGFloat = 0

  init?(_ item: GridBlock.Item, direction: Direction) {
    guard item.weight.value(at: direction) == nil else {
      return nil
    }

    span = item.span.value(at: direction)
  }
}

private struct HorizontallyNonResizableItem: NonResizableItem {
  let span: Int
  let size: CGFloat

  init?(_ item: GridBlock.Item) {
    guard !item.contents.isHorizontallyResizable else {
      return nil
    }

    span = item.span.columns
    size = item.contents.widthOfHorizontallyNonResizableBlock
  }
}

private struct VerticallyNonResizableItem: NonResizableItem {
  let span: Int
  let size: CGFloat

  init?(_ item: GridBlock.Item, width: CGFloat) {
    guard !item.contents.isVerticallyResizable else {
      return nil
    }

    span = item.span.rows
    size = item.contents.heightOfVerticallyNonResizableBlock(forWidth: width)
  }
}

private struct WeightedItem {
  let span: Int
  let weight: LayoutTrait.Weight

  init?(_ item: GridBlock.Item, direction: Direction) {
    guard let value = item.weight.value(at: direction) else {
      return nil
    }

    span = item.span.value(at: direction)
    weight = value
  }
}

extension GridBlock.Item.Weight {
  fileprivate func value(at direction: Direction) -> LayoutTrait.Weight? {
    switch direction {
    case .horizontal: return column
    case .vertical: return row
    }
  }
}

extension GridBlock.Span {
  fileprivate func value(at direction: Direction) -> Int {
    switch direction {
    case .horizontal: return columns
    case .vertical: return rows
    }
  }
}

private typealias Direction = GridBlock.Direction

extension Direction {
  fileprivate func selectDimension(from coords: (row: Int, column: Int)) -> Int {
    switch self {
    case .horizontal: return coords.column
    case .vertical: return coords.row
    }
  }
}
