import CoreGraphics
import Foundation
import VGSL

struct ContainerBlockLayout {
  private enum ContentFitting {
    case fits
    case doesNotFit(notFittingPartSize: CGFloat)

    init(offsets: [CGFloat], margin: CGFloat) {
      let minOffset = (offsets.min() ?? 0) - margin
      if minOffset.isApproximatelyLessThan(0) {
        self = .doesNotFit(notFittingPartSize: -minOffset)
      } else {
        self = .fits
      }
    }

    var insetValue: CGFloat {
      switch self {
      case .fits:
        0
      case let .doesNotFit(notFittingPartSize: inset):
        inset
      }
    }
  }

  public private(set) var childrenWithSeparators: [ContainerBlock.Child] = []
  public private(set) var blockFrames: [CGRect] = []
  public private(set) var ascent: CGFloat?
  let gaps: [CGFloat]
  let blockLayoutDirection: UserInterfaceLayoutDirection
  let layoutDirection: ContainerBlock.LayoutDirection
  let layoutMode: ContainerBlock.LayoutMode
  let crossAlignment: ContainerBlock.CrossAlignment
  let size: CGSize
  let needCompressConstrainedBlocks: Bool
  let axialAlignmentManager: AxialAlignmentManager

  private var sizeInDirection: CGFloat {
    switch layoutDirection {
    case .horizontal:
      size.width
    case .vertical:
      size.height
    }
  }

  public init(
    children: [ContainerBlock.Child],
    separator: ContainerBlock.Separator? = nil,
    lineSeparator: ContainerBlock.Separator? = nil,
    gaps: [CGFloat],
    blockLayoutDirection: UserInterfaceLayoutDirection = .leftToRight,
    layoutDirection: ContainerBlock.LayoutDirection,
    layoutMode: ContainerBlock.LayoutMode,
    axialAlignment: ContainerBlock.AxialAlignment,
    crossAlignment: ContainerBlock.CrossAlignment,
    size: CGSize,
    needCompressConstrainedBlocks: Bool = true
  ) {
    precondition(gaps.count == children.count + 1)
    self.blockLayoutDirection = blockLayoutDirection
    self.gaps = gaps
    self.layoutDirection = layoutDirection
    self.layoutMode = layoutMode
    self.crossAlignment = crossAlignment
    self.size = size
    self.axialAlignmentManager = AxialAlignmentManager(
      layoutDirection: layoutDirection,
      axialAlignment: axialAlignment
    )
    self.needCompressConstrainedBlocks = needCompressConstrainedBlocks
    (self.childrenWithSeparators, self.blockFrames, self.ascent) = calculateBlockFrames(
      children: children,
      separator: separator,
      lineSeparator: lineSeparator
    )
  }

  private func calculateBlockFrames(
    children: [ContainerBlock.Child],
    separator: ContainerBlock.Separator? = nil,
    lineSeparator: ContainerBlock.Separator? = nil
  ) -> ([ContainerBlock.Child], [CGRect], CGFloat?) {
    switch layoutMode {
    case .noWrap:
      calculateNoWrapLayoutFrames(
        children: children
      )
    case .wrap:
      calculateWrapLayoutFrames(
        children: children,
        separator: separator,
        lineSeparator: lineSeparator
      )
    }
  }

  private func calculateNoWrapLayoutFrames(
    children: [ContainerBlock.Child]
  ) -> ([ContainerBlock.Child], [CGRect], CGFloat?) {
    typealias ResizableBlocksData = (
      blocksCount: Int,
      totalWeight: CGFloat,
      marginsSize: CGFloat
    )

    let children =
      (layoutDirection == .horizontal && blockLayoutDirection == .rightToLeft) ?
      children.reversed() : children

    let gapsSize = gaps.reduce(0, +)
    let blocks = children.map(\.content)

    let resizableBlocksData = blocks
      .filter { $0.isResizable(in: layoutDirection) }
      .reduce(into: ResizableBlocksData(0, 0.0, 0.0)) {
        $0.blocksCount += 1
        $0.totalWeight += $1.weightOfResizableBlock(in: layoutDirection)
        $0.marginsSize += ($1 as? PaddingProviding)?.margins(direction: layoutDirection) ?? 0
      }

    let sizeOfNonResizableBlocks: CGFloat = switch layoutDirection {
    case .horizontal:
      widthsOfHorizontallyNonResizableBlocksIn(blocks).reduce(0, +)
    case .vertical:
      heightsOfVerticallyNonResizableBlocksIn(
        blocks, forSize: size
      ).reduce(0, +)
    }

    let spaceAvailableForResizableBlocks = (
      sizeInDirection
        - sizeOfNonResizableBlocks
        - gapsSize
        - resizableBlocksData.marginsSize
    )

    let spaceAvailablePerWeightUnit = max(0, spaceAvailableForResizableBlocks) / resizableBlocksData
      .totalWeight

    let blockMeasure = ResizableBlockMeasure(
      resizableBlockCount: resizableBlocksData.blocksCount,
      lengthAvailablePerWeightUnit: spaceAvailablePerWeightUnit,
      lengthAvailableForResizableBlocks: spaceAvailableForResizableBlocks
    )

    let constrainedBlocks = blocks
      .filter { $0.isConstrained(in: layoutDirection) }
    let constrainedBlockSizesIterator = decreaseConstrainedBlockSizes(
      blockSizes: constrainedBlocks
        .map { .init(
          size: $0.sizeOfNonResizableBlock(
            in: layoutDirection,
            layoutSize: size
          ),
          minSize: $0.minSize(in: layoutDirection)
        ) },
      lengthToDecrease: spaceAvailableForResizableBlocks < 0 ? -spaceAvailableForResizableBlocks : 0
    )

    switch layoutDirection {
    case .horizontal:
      return calculateHorizontalNoWrapLayoutFrames(
        children: children,
        blockMeasure: blockMeasure,
        constrainedBlockSizesIterator: constrainedBlockSizesIterator
      )
    case .vertical:
      return calculateVerticalNoWrapLayoutFrames(
        children: children,
        blockMeasure: blockMeasure,
        constrainedBlockSizesIterator: constrainedBlockSizesIterator
      )
    }
  }

  private func calculateWrapLayoutFrames(
    children: [ContainerBlock.Child],
    separator: ContainerBlock.Separator?,
    lineSeparator: ContainerBlock.Separator?
  ) -> ([ContainerBlock.Child], [CGRect], CGFloat?) {
    var frames = [CGRect]()
    var containerAscent: CGFloat?
    let buildingDirectionKeyPath = layoutDirection.buildingDirectionKeyPath
    let transferDirectionKeyPath = layoutDirection.transferDirectionKeyPath

    let wrapLayoutGroups = WrapLayoutGroups(
      blockLayoutDirection: blockLayoutDirection,
      children: children,
      separator: separator,
      lineSeparator: lineSeparator,
      size: size,
      layoutDirection: layoutDirection
    )

    let contentCrossSize = wrapLayoutGroups.groups.map {
      getGroupHeight(group: $0, lineAscent: getLineAscent(group: $0))
    }.reduce(0, +)

    var currentLineOffset = crossAlignment.offset(
      forAvailableSpace: size[keyPath: transferDirectionKeyPath],
      contentSize: contentCrossSize
    )
    if currentLineOffset < 0 {
      currentLineOffset = 0
    }

    for group in wrapLayoutGroups.groups {
      let lineAscent = getLineAscent(group: group)
      if containerAscent == nil {
        containerAscent = lineAscent
      }

      let groupHeight = getGroupHeight(group: group, lineAscent: lineAscent)

      let contentLength = group.map(\.childSize).map(to: buildingDirectionKeyPath).reduce(0, +)

      var groupFrames: [CGRect] = []
      for item in group {
        let alignmentSpace = groupHeight - item.childSize[keyPath: transferDirectionKeyPath]
        let alignedLineOffset = currentLineOffset + item.child.crossAlignment
          .offset(forAvailableSpace: alignmentSpace)

        switch layoutDirection {
        case .horizontal:
          let baselineOffset = item.child.baselineOffset(
            ascent: lineAscent,
            width: item.childSize.width
          )
          let origin = CGPoint(
            x: item.lineOffset,
            y: alignedLineOffset + baselineOffset
          )
          groupFrames.append(CGRect(origin: origin, size: item.childSize))
        case .vertical:
          let origin = CGPoint(
            x: alignedLineOffset,
            y: item.lineOffset
          )
          groupFrames.append(CGRect(origin: origin, size: item.childSize))
        }
      }

      frames += axialAlignmentManager.applyOffset(
        to: groupFrames,
        forAvailableSpace: size[keyPath: buildingDirectionKeyPath],
        contentSize: contentLength
      )
      currentLineOffset += groupHeight
    }
    return (wrapLayoutGroups.childrenWithSeparators, frames, containerAscent)
  }

  private func calculateHorizontalNoWrapLayoutFrames(
    children: [ContainerBlock.Child],
    blockMeasure: ResizableBlockMeasure,
    constrainedBlockSizesIterator: IndexingIterator<[CGFloat]>
  ) -> ([ContainerBlock.Child], [CGRect], CGFloat?) {
    var x = gaps[0]
    var contentSize: CGFloat = 0
    var frames = [CGRect]()
    var containerAscent: CGFloat?
    var blockMeasure = blockMeasure
    var constrainedBlockSizesIterator = constrainedBlockSizesIterator

    for (child, gapAfterBlock) in zip(children, gaps.dropFirst()) {
      let block = child.content
      let widthIfResizable = blockMeasure.measureNext(block.horizontalMeasure)

      let widthIfConstrained = block
        .isHorizontallyConstrained ? (constrainedBlockSizesIterator.next() ?? 0) : 0

      let blockSize = block.sizeFor(
        widthOfHorizontallyResizableBlock: widthIfResizable,
        heightOfVerticallyResizableBlock: size.height,
        constrainedWidth: widthIfConstrained,
        constrainedHeight: needCompressConstrainedBlocks ? size.height : .infinity
      )

      containerAscent = getMaxAscent(
        current: containerAscent, child: child, childSize: blockSize
      )
      let alignmentSpace = size.height - blockSize.height
      let y = child.crossAlignment.offset(forAvailableSpace: alignmentSpace)
      frames.append(CGRect(origin: CGPoint(x: x, y: y), size: blockSize))
      x += blockSize.width + gapAfterBlock
    }
    contentSize = x
    frames.addBaselineOffset(children: children, ascent: containerAscent)

    return (
      children,
      axialAlignmentManager.applyOffset(
        to: frames,
        forAvailableSpace: sizeInDirection,
        contentSize: contentSize
      ),
      containerAscent
    )
  }

  private func calculateVerticalNoWrapLayoutFrames(
    children: [ContainerBlock.Child],
    blockMeasure: ResizableBlockMeasure,
    constrainedBlockSizesIterator: IndexingIterator<[CGFloat]>
  ) -> ([ContainerBlock.Child], [CGRect], CGFloat?) {
    var y = gaps[0]
    var contentSize: CGFloat = 0
    var frames = [CGRect]()
    var blockMeasure = blockMeasure
    var constrainedBlockSizesIterator = constrainedBlockSizesIterator

    for (child, gapAfterBlock) in zip(children, gaps.dropFirst()) {
      let block = child.content
      let width: CGFloat
      if block.isHorizontallyResizable {
        width = size.width
      } else {
        let intrinsicWidth = block.widthOfHorizontallyNonResizableBlock
        width = block.isHorizontallyConstrained ? min(intrinsicWidth, size.width) : intrinsicWidth
      }

      let height: CGFloat = if block.isVerticallyResizable {
        blockMeasure.measureNext(block.verticalMeasure)
      } else if block.isVerticallyConstrained {
        constrainedBlockSizesIterator.next() ?? 0
      } else {
        block.heightOfVerticallyNonResizableBlock(forWidth: width)
      }

      let alignmentSpace = size.width - width
      let x = child.crossAlignment.offset(forAvailableSpace: alignmentSpace)
      frames.append(CGRect(x: x, y: y, width: width, height: height))
      y += height + gapAfterBlock
    }
    contentSize = y

    return (
      children,
      axialAlignmentManager.applyOffset(
        to: frames,
        forAvailableSpace: sizeInDirection,
        contentSize: contentSize
      ),
      nil
    )
  }

  private func getLineAscent(group: [WrapLayoutGroups.ChildParametes]) -> CGFloat? {
    guard layoutDirection == .horizontal else {
      return nil
    }
    var lineAscent: CGFloat?
    for item in group {
      lineAscent = getMaxAscent(
        current: lineAscent, child: item.child, childSize: item.childSize
      )
    }
    return lineAscent
  }

  private func getGroupHeight(
    group: [WrapLayoutGroups.ChildParametes],
    lineAscent: CGFloat?
  ) -> CGFloat {
    switch layoutDirection {
    case .horizontal:
      group
        .map { item in item.childSize.height + item.child.baselineOffset(
          ascent: lineAscent,
          width: item.childSize.width
        ) }
        .max() ?? 0
    case .vertical:
      group.map { item in item.childSize.width }.max() ?? 0
    }
  }

  public var leftInset: CGFloat {
    let leftMargin = layoutDirection == .horizontal ? gaps.first! : 0
    return ContentFitting(
      offsets: blockFrames.map(\.minX),
      margin: leftMargin
    ).insetValue
  }

  public var topInset: CGFloat {
    let topMargin = layoutDirection == .vertical ? gaps.first! : 0
    return ContentFitting(
      offsets: blockFrames.map(\.minY),
      margin: topMargin
    ).insetValue
  }

  public var bottomInset: CGFloat { layoutDirection == .vertical ? gaps.last! : 0 }
  public var rightInset: CGFloat { layoutDirection == .horizontal ? gaps.last! : 0 }

  public var contentSize: CGSize {
    CGSize(
      width: blockFrames.map(\.maxX).max() ?? 0,
      height: blockFrames.map(\.maxY).max() ?? 0
    )
  }

  private func getMaxAscent(
    current containerAscent: CGFloat?,
    child: ContainerBlock.Child,
    childSize: CGSize
  ) -> CGFloat? {
    let childAscent = child.contentAscent(forWidth: childSize.width)
    guard let containerAscent else {
      return childAscent
    }
    guard let childAscent else {
      return containerAscent
    }
    return max(containerAscent, childAscent)
  }
}

private func heightsOfVerticallyNonResizableBlocksIn(
  _ blocks: [Block],
  forSize size: CGSize
) -> [CGFloat] {
  blocks.filter { !$0.isVerticallyResizable }
    .map {
      $0.sizeFor(
        widthOfHorizontallyResizableBlock: size.width,
        heightOfVerticallyResizableBlock: size.height,
        constrainedWidth: size.width,
        constrainedHeight: .infinity
      ).height
    }
}

func widthsOfHorizontallyNonResizableBlocksIn(_ blocks: [Block]) -> [CGFloat] {
  blocks.filter { !$0.isHorizontallyResizable }.map(\.widthOfHorizontallyNonResizableBlock)
}

extension Block {
  fileprivate var horizontalMeasure: ResizableBlockMeasure.Measure {
    isHorizontallyResizable ? .resizable(
      weightOfHorizontallyResizableBlock,
      reservedSpace: (self as? PaddingProviding)?.margins(direction: .horizontal) ?? 0.0
    ) : .nonResizable
  }

  fileprivate var verticalMeasure: ResizableBlockMeasure.Measure {
    isVerticallyResizable ? .resizable(
      weightOfVerticallyResizableBlock,
      reservedSpace: (self as? PaddingProviding)?.margins(direction: .vertical) ?? 0.0
    ) : .nonResizable
  }
}

extension Block {
  fileprivate typealias Direction = ContainerBlock.LayoutDirection
  fileprivate func isResizable(in direction: Direction) -> Bool {
    direction == .horizontal ? isHorizontallyResizable : isVerticallyResizable
  }

  fileprivate func isConstrained(in direction: Direction) -> Bool {
    direction == .horizontal ? isHorizontallyConstrained : isVerticallyConstrained
  }

  fileprivate func weightOfResizableBlock(in direction: Direction) -> CGFloat {
    direction == .horizontal ?
      weightOfHorizontallyResizableBlock.rawValue :
      weightOfVerticallyResizableBlock.rawValue
  }

  fileprivate func sizeOfNonResizableBlock(
    in direction: Direction,
    layoutSize: CGSize
  ) -> CGFloat {
    direction == .horizontal ?
      widthOfHorizontallyNonResizableBlock :
      sizeFor(
        widthOfHorizontallyResizableBlock: layoutSize.width,
        heightOfVerticallyResizableBlock: layoutSize.height,
        constrainedWidth: layoutSize.width,
        constrainedHeight: .infinity
      ).height
  }

  fileprivate func minSize(in direction: Direction) -> CGFloat {
    direction == .horizontal ?
      minWidth :
      minHeight
  }
}

extension [CGRect] {
  fileprivate mutating func addBaselineOffset(
    children: [ContainerBlock.Child],
    ascent: CGFloat?
  ) {
    guard let ascent, ascent > 0 else {
      return
    }
    var framesWithOffset = [CGRect]()
    for (child, frame) in zip(children, self) {
      let baselineOffset = child.baselineOffset(ascent: ascent, width: frame.size.width)
      let y = frame.origin.y + baselineOffset
      framesWithOffset.append(CGRect(origin: CGPoint(x: frame.origin.x, y: y), size: frame.size))
    }
    self = framesWithOffset
  }
}

extension ContainerBlock.Child {
  fileprivate func contentAscent(forWidth width: CGFloat) -> CGFloat? {
    guard crossAlignment == .baseline else {
      return nil
    }
    return content.ascent(forWidth: width)
  }

  fileprivate func baselineOffset(ascent: CGFloat?, width: CGFloat) -> CGFloat {
    guard let ascent, ascent > 0,
          let childAscent = contentAscent(forWidth: width) else {
      return 0
    }
    return ascent - childAscent
  }
}

fileprivate protocol PaddingProviding {
  var paddings: EdgeInsets { get }
  var child: Block { get }
}

extension PaddingProviding {
  func margins(direction: ContainerBlock.LayoutDirection) -> CGFloat {
    guard child is PaddingProviding else { return 0 }

    switch direction {
    case .horizontal:
      return paddings.horizontal.sum
    case .vertical:
      return paddings.vertical.sum
    }
  }
}

extension DecoratingBlock: PaddingProviding {}
