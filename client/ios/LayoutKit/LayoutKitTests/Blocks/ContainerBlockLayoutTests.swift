@testable import LayoutKit

import XCTest

import BaseUIPublic
import CommonCorePublic

extension ContainerBlockLayout {
  fileprivate init(
    blocks: [Block],
    gaps: [CGFloat],
    layoutDirection: ContainerBlock.LayoutDirection,
    layoutMode: ContainerBlock.LayoutMode = .noWrap,
    crossAlignment: ContainerBlock.CrossAlignment = .leading,
    axialAlignment: ContainerBlock.AxialAlignment = .leading,
    size: CGSize
  ) {
    self.init(
      children: blocks.map { .init(content: $0, crossAlignment: crossAlignment) },
      gaps: gaps,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode,
      axialAlignment: axialAlignment,
      crossAlignment: crossAlignment,
      size: size
    )
  }
}

final class ContainerBlockLayoutTests: XCTestCase {
  func test_WidthOfHorizontallyResizableBlockInVerticalContainer_EqualsWidthOfContainer() {
    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .vertical,
      size: imageSize
    )

    let textBlockFrame = layout.blockFrames[1]
    XCTAssertEqual(textBlockFrame.width, imageSize.width)
  }

  func test_InVerticalContainerWithLeadingChildrenAlignment_BlocksAreLaidOutOneBelowAnotherStartingFromTopShiftedByGaps(
  ) {
    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .vertical,
      size: imageSize
    )

    let blockOriginYs = layout.blockFrames.map(\.origin.y)
    XCTAssertEqual(
      blockOriginYs,
      [
        ContainerBlockTestModels.threeGaps[0],
        ContainerBlockTestModels.threeGaps[0] + imageSize.height + ContainerBlockTestModels
          .threeGaps[1],
      ]
    )
  }

  func test_InVerticalContainerWithCenterChildrenAlignment_BlocksAreLaidOutOneBelowAnotherInCenter(
  ) {
    let containerSize = CGSize(width: imageSize.width, height: 200)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .vertical,
      axialAlignment: .center,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map(\.origin.y)
    let firstBlockOrigin =
      floor(
        ContainerBlockTestModels.threeGaps[0] +
          (
            containerSize
              .height -
              (imageSize.height + multilineTextSize.height + ContainerBlockTestModels.threeGapsSize)
          ) /
          2
      )
    let expectedBlockOrigins = [
      firstBlockOrigin,
      firstBlockOrigin + imageSize.height + ContainerBlockTestModels.threeGaps[1],
    ]
    zip(blockOriginYs, expectedBlockOrigins).forEach { XCTAssertEqual($0.0, $0.1, accuracy: 1e-4) }
  }

  func test_InVerticalContainerWithTrailingChildrenAlignment_BlocksAreLaidOutOneBelowAnotherAtBottom(
  ) {
    let containerSize = CGSize(width: imageSize.width, height: 200)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .vertical,
      axialAlignment: .trailing,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map(\.origin.y)
    let firstBlockOrigin = floor(
      ContainerBlockTestModels.threeGaps[0] + containerSize
        .height -
        (imageSize.height + multilineTextSize.height + ContainerBlockTestModels.threeGapsSize)
    )
    let expectedBlockOrigins = [
      firstBlockOrigin,
      firstBlockOrigin + imageSize.height + ContainerBlockTestModels.threeGaps[1],
    ]
    zip(blockOriginYs, expectedBlockOrigins).forEach { XCTAssertEqual($0.0, $0.1, accuracy: 1e-4) }
  }

  func test_InHorizontalContainerWithLeadingChildrenAlignment_BlocksAreLaidOutStartingFromTop() {
    let containerSize = CGSize(width: imageSize.width, height: 200)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map(\.origin.y)
    XCTAssertEqual(blockOriginYs, [0, 0])
  }

  func test_InHorizontalContainerWithCenterChidlrenAlignment_BlocksAreCeneteredVertically() {
    let containerSize = CGSize(width: 300, height: 200)
    let textBlock = TextBlock(widthTrait: .resizable, text: text)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), textBlock],
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .horizontal,
      crossAlignment: .center,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map(\.origin.y)
    let textBlockWidth = containerSize
      .width - (imageSize.width + ContainerBlockTestModels.threeGapsSize)
    let textBlockHeight = textBlock.heightOfVerticallyNonResizableBlock(forWidth: textBlockWidth)
    let expectedBlockOrigins = [
      (containerSize.height - imageSize.height) / 2,
      (containerSize.height - textBlockHeight) / 2,
    ].map { floor($0) }
    zip(blockOriginYs, expectedBlockOrigins).forEach { XCTAssertEqual($0.0, $0.1, accuracy: 1e-4) }
  }

  func test_InHorizontalContainerWithTrailingChidlrenAlignment_BlocksAreLaidOutAtBottom() {
    let containerSize = CGSize(width: 300, height: 200)
    let textBlock = TextBlock(widthTrait: .resizable, text: text)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), textBlock],
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .horizontal,
      crossAlignment: .trailing,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map(\.origin.y)
    let textBlockWidth = containerSize
      .width - (imageSize.width + ContainerBlockTestModels.threeGapsSize)
    let textBlockHeight = textBlock.heightOfVerticallyNonResizableBlock(forWidth: textBlockWidth)
    let expectedBlockOrigins = [
      containerSize.height - imageSize.height,
      containerSize.height - textBlockHeight,
    ].map { floor($0) }
    zip(blockOriginYs, expectedBlockOrigins).forEach { XCTAssertEqual($0.0, $0.1, accuracy: 1e-4) }
  }

  func test_InVerticalContainerVerticallyNonResizableChildrenHaveIntrinsicHeight() {
    let containerSize = CGSize(width: imageSize.width, height: 200)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .vertical,
      size: containerSize
    )

    let blockHeights = layout.blockFrames.map(\.height)
    XCTAssertEqual(blockHeights, [imageSize.height, multilineTextSize.height])
  }

  func test_InVerticalContainerHorizontallyNonResizableChildrenHaveIntrinsicWidth() {
    let containerSize = CGSize(width: 100, height: imageSize.height)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image)],
      gaps: [0, 20],
      layoutDirection: .vertical,
      size: containerSize
    )

    XCTAssertEqual(layout.blockFrames[0].width, imageSize.width)
  }

  func test_InHorizontalContainer_WithHorizontalChildrenAlignmentInCenter_HorizontallyNonResizableChildrenAreLaidOutInCenter(
  ) {
    let containerSize = CGSize(width: 200, height: imageSize.height)
    let gaps = SideInsets(leading: 10, trailing: 20)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image)],
      gaps: gaps.asArray(),
      layoutDirection: .horizontal,
      axialAlignment: .center,
      size: containerSize
    )

    XCTAssertEqual(
      layout.blockFrames[0].origin.x,
      gaps.leading + (containerSize.width - imageSize.width - gaps.sum) / 2
    )
  }

  func test_InHorizontalContainer_WithLeadingHorizontalChildrenAlignment_HorizontallyNonResizableChildrenAreLaidOutAtLeftShiftedByLeftGap(
  ) {
    let containerSize = CGSize(width: 200, height: imageSize.height)
    let gaps = SideInsets(leading: 10, trailing: 20)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image)],
      gaps: gaps.asArray(),
      layoutDirection: .horizontal,
      size: containerSize
    )

    XCTAssertEqual(layout.blockFrames[0].origin.x, gaps.leading)
  }

  func test_InHorizontalContainer_WithTrailingHorizontalChildrenAlignment_HorizontallyNonResizableChildrenAreLaidOutAtRightShiftedByRightGap(
  ) {
    let containerSize = CGSize(width: 200, height: imageSize.height)
    let gaps = SideInsets(leading: 10, trailing: 20)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image)],
      gaps: gaps.asArray(),
      layoutDirection: .horizontal,
      axialAlignment: .trailing,
      size: containerSize
    )

    XCTAssertEqual(
      layout.blockFrames[0].origin.x,
      containerSize.width - imageSize.width - gaps.trailing
    )
  }

  func test_InVerticalContainer_WithHorizontalChildrenAlignmentInCenter_HorizontallyNonResizableChildrenAreLaidOutInCenter(
  ) {
    let containerSize = CGSize(width: 100, height: imageSize.height)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image)],
      gaps: [10, 20],
      layoutDirection: .vertical,
      crossAlignment: .center,
      size: containerSize
    )

    XCTAssertEqual(layout.blockFrames[0].origin.x, (containerSize.width - imageSize.width) / 2)
  }

  func test_InVerticalContainer_WithLeadingHorizontalChildrenAlignment_HorizontallyNonResizableChildrenAreLaidOutAtLeft(
  ) {
    let containerSize = CGSize(width: 100, height: imageSize.height)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image)],
      gaps: [10, 20],
      layoutDirection: .vertical,
      size: containerSize
    )

    XCTAssertEqual(layout.blockFrames[0].origin.x, 0)
  }

  func test_InVerticalContainer_WithTrailingHorizontalChildrenAlignment_HorizontallyNonResizableChildrenAreLaidOutAtRight(
  ) {
    let containerSize = CGSize(width: 100, height: imageSize.height)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image)],
      gaps: [10, 20],
      layoutDirection: .vertical,
      crossAlignment: .trailing,
      size: containerSize
    )

    XCTAssertEqual(layout.blockFrames[0].origin.x, containerSize.width - imageSize.width)
  }

  func test_InVerticalContainerWithVerticallyResizableChildren_AllResizableChildrenOccupyAllRemainingSpaceAndHaveHeightsAccordingToWeights(
  ) {
    let containerSize = CGSize(width: imageSize.width, height: 1000)

    let verticallyResizableChildren: [Block] = [
      try! ContainerBlock(
        layoutDirection: .vertical,
        widthTrait: .intrinsic,
        heightTrait: .resizable,
        children: [ImageBlock(imageHolder: image)]
      ),
      try! ContainerBlock(
        layoutDirection: .vertical,
        widthTrait: .resizable,
        heightTrait: .weighted(3),
        children: [ImageBlock(imageHolder: image)]
      ),
    ]
    let children: [Block] = [ImageBlock(imageHolder: image)] + verticallyResizableChildren
    let layout = ContainerBlockLayout(
      blocks: children,
      gaps: [0, 0, 0, 0],
      layoutDirection: .vertical,
      axialAlignment: .center,
      size: containerSize
    )

    let expectedWeightUnit = (containerSize.height - imageSize.height) / 4
    XCTAssertEqual(layout.blockFrames[1].height, expectedWeightUnit)
    XCTAssertEqual(
      layout.blockFrames[2].height,
      containerSize.height - imageSize.height - layout.blockFrames[1].height
    )
  }

  func test_InHorizontalContainerWithHorizontallyResizableChildren_AllResizableChildrenOccupyAllRemainingSpaceAndHaveWidthsAccordingToWeights(
  ) {
    let containerSize = CGSize(width: 1000, height: imageSize.height)

    let horizontallyResizableChildren: [Block] = [
      try! ContainerBlock(
        layoutDirection: .horizontal,
        widthTrait: .weighted(3),
        heightTrait: .intrinsic,
        children: [ImageBlock(imageHolder: image)]
      ),
      try! ContainerBlock(
        layoutDirection: .horizontal,
        widthTrait: .resizable,
        heightTrait: .resizable,
        children: [ImageBlock(imageHolder: image)]
      ),
    ]
    let children: [Block] = [ImageBlock(imageHolder: image)] + horizontallyResizableChildren
    let layout = ContainerBlockLayout(
      blocks: children,
      gaps: [0, 0, 0, 0],
      layoutDirection: .horizontal,
      crossAlignment: .center,
      size: containerSize
    )

    let expectedWeightUnit = (containerSize.width - imageSize.width) / 4
    XCTAssertEqual(layout.blockFrames[1].width, expectedWeightUnit * 3)
    XCTAssertEqual(
      layout.blockFrames[2].width,
      containerSize.width - imageSize.width - layout.blockFrames[1].width
    )
  }

  func test_InHorizontalContainer_ResizableBlockOccupiesAllRemainingWidthWithoutGapsAfterLayingOutNonResizableBlocks(
  ) {
    let containerSize = CGSize(width: 200, height: imageSize.height)
    let children: [Block] = [
      ImageBlock(imageHolder: image),
      try! ContainerBlock(
        layoutDirection: .vertical,
        gaps: [0, 0],
        children: [ImageBlock(imageHolder: image)]
      ),
    ]

    let layout = ContainerBlockLayout(
      blocks: children,
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )

    let resizableBlockFrame = layout.blockFrames[1]
    XCTAssertEqual(
      resizableBlockFrame.width,
      containerSize.width - imageSize.width - ContainerBlockTestModels.threeGapsSize
    )
  }

  func test_InVerticalContainer_ResizableBlockOccupiesAllRemainingHeightWithoutGapsAfterLayingOutNonResizableBlocks(
  ) {
    let containerSize = CGSize(width: imageSize.width, height: 1000)
    let children: [Block] = [
      ImageBlock(imageHolder: image),
      try! ContainerBlock(
        layoutDirection: .horizontal,
        widthTrait: .intrinsic,
        heightTrait: .resizable,
        gaps: [0, 0],
        children: [ImageBlock(imageHolder: image)]
      ),
    ]

    let layout = ContainerBlockLayout(
      blocks: children,
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .vertical,
      size: containerSize
    )

    let resizableBlockFrame = layout.blockFrames[1]
    XCTAssertEqual(
      resizableBlockFrame.height,
      containerSize.height - imageSize.height - ContainerBlockTestModels.threeGapsSize
    )
  }

  func test_InHorizontalContainer_BlocksAreLaidOutOneAfterAnotherShiftedByGaps() {
    let containerSize = CGSize(width: 100, height: imageSize.height)
    let children: [Block] = [
      ImageBlock(imageHolder: image),
      try! ContainerBlock(
        layoutDirection: .vertical,
        gaps: [0, 0],
        children: [ImageBlock(imageHolder: image)]
      ),
    ]

    let layout = ContainerBlockLayout(
      blocks: children,
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )

    let blockOriginXs = layout.blockFrames.map(\.origin.x)
    XCTAssertEqual(
      blockOriginXs,
      [
        ContainerBlockTestModels.threeGaps[0],
        ContainerBlockTestModels.threeGaps[0] + imageSize.width + ContainerBlockTestModels
          .threeGaps[1],
      ]
    )
  }

  func test_WhenThereIsNoSpaceForResizableChildInHorizontalContainer_FrameWidthIsZero() {
    let containerSize = CGSize(width: 100, height: imageSize.height)
    // free space: 100, used space: gaps 10 + 20 + 30 + image 80 = 140
    let children: [Block] = [
      ImageBlock(imageHolder: image),
      try! ContainerBlock(
        layoutDirection: .vertical,
        gaps: [0, 0],
        children: [ImageBlock(imageHolder: image)]
      ),
    ]

    let layout = ContainerBlockLayout(
      blocks: children,
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )
    let resizableChildWidth = layout.blockFrames.last!.width
    XCTAssertEqual(resizableChildWidth, 0)
  }

  func test_HeightOfVerticallyResizableBlockInHorizontalContainer_EqualsHeightOfContainer() {
    let containerSize = CGSize(width: 100, height: imageSize.height)
    let children: [Block] = [
      ImageBlock(imageHolder: image),
      try! ContainerBlock(
        layoutDirection: .vertical,
        heightTrait: .resizable,
        gaps: [0, 0],
        children: [ImageBlock(imageHolder: image)]
      ),
    ]

    let layout = ContainerBlockLayout(
      blocks: children,
      gaps: ContainerBlockTestModels.threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )

    let resizableBlockFrame = layout.blockFrames[1]
    XCTAssertEqual(resizableBlockFrame.height, containerSize.height)
  }

  func test_WhenItemDoesNotFitHeightInVerticalContainer_TopInsetEqualsNonFittingSizePlusFirstGap() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 5, height: 102))],
      gaps: [10, 10],
      layoutDirection: .vertical,
      axialAlignment: .center,
      size: CGSize(width: 100, height: 100)
    )

    // 1 from non-fitting content and 10 from top gap
    XCTAssertEqual(layout.topInset, 11)
  }

  func test_WhenItemDoesNotFitHeightInHorizontalContainer_TopInsetEqualsNonFittingSize() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 5, height: 102))],
      gaps: [10, 10],
      layoutDirection: .horizontal,
      crossAlignment: .center,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.topInset, 1)
  }

  func test_WhenItemFitsHeightContainer_TopInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 5, height: 40))],
      gaps: [10, 10],
      layoutDirection: .vertical,
      axialAlignment: .center,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.topInset, 0)
  }

  func test_WhenItemDoesNotFitHeightInContainerWithVerticallyLeadingChildren_TopInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 5, height: 102))],
      gaps: [10, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.topInset, 0)
  }

  func test_WhenItemDoesNotFitWidthInVerticalContainer_LeftInsetEqualsNonFittingSize() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 102, height: 5))],
      gaps: [10, 10],
      layoutDirection: .vertical,
      crossAlignment: .center,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.leftInset, 1)
  }

  func test_WhenItemsDoNotFit_InHorizontalContainer_WithHorizontalChildrenAlignmentInCenter_HorizontallyNonResizableChildrenAreLaidOutInCenter(
  ) {
    let containerWidth: CGFloat = 100
    let gap: CGFloat = 8
    let insets = SideInsets(leading: 10, trailing: 20)

    let layout = ContainerBlockLayout(
      blocks: [
        ImageBlock(imageHolder: image),
        ImageBlock(imageHolder: image),
      ],
      gaps: [insets.leading, gap, insets.trailing],
      layoutDirection: .horizontal,
      axialAlignment: .center,
      size: CGSize(width: containerWidth, height: imageSize.height)
    )

    let totalContentWidth = imageSize.width * 2 + gap
    let expectedContentOrigin = insets
      .leading + (containerWidth - insets.sum - totalContentWidth) / 2
    XCTAssertEqual(layout.blockFrames.first!.origin.x, expectedContentOrigin)
  }

  func test_WhenItemsDoNotFit_InHorizontalContainer_WithLeadingHorizontalChildrenAlignment_HorizontallyNonResizableChildrenAreLaidOutAtLeftShiftedByLeftGap(
  ) {
    let containerWidth: CGFloat = 100
    let gap: CGFloat = 8
    let insets = SideInsets(leading: 10, trailing: 20)

    let layout = ContainerBlockLayout(
      blocks: [
        ImageBlock(imageHolder: image),
        ImageBlock(imageHolder: image),
      ],
      gaps: [insets.leading, gap, insets.trailing],
      layoutDirection: .horizontal,
      size: CGSize(width: containerWidth, height: imageSize.width)
    )

    XCTAssertEqual(layout.blockFrames.first!.origin.x, insets.leading)
  }

  func test_WhenItemsDoNotFit_InHorizontalContainer_WithTrailingHorizontalChildrenAlignment_HorizontallyNonResizableChildrenAreLaidOutAtRightShiftedByRightGap(
  ) {
    let containerWidth: CGFloat = 100
    let gap: CGFloat = 8
    let insets = SideInsets(leading: 10, trailing: 20)

    let layout = ContainerBlockLayout(
      blocks: [
        ImageBlock(imageHolder: image),
        ImageBlock(imageHolder: image),
      ],
      gaps: [insets.leading, gap, insets.trailing],
      layoutDirection: .horizontal,
      axialAlignment: .trailing,
      size: CGSize(width: containerWidth, height: imageSize.width)
    )

    let totalContentWidth = imageSize.width * 2 + gap
    let expectedContentOrigin = containerWidth - totalContentWidth - insets.trailing
    XCTAssertEqual(layout.blockFrames.first!.origin.x, expectedContentOrigin)
  }

  func test_WhenItemFitsWidth_LeftInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 10, height: 5))],
      gaps: [10, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.leftInset, 0)
  }

  func test_InVerticalContainer_BottomInsetEqualsLastGap() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 10, height: 5))],
      gaps: [5, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.bottomInset, 10)
  }

  func test_InVerticalContainer_RightInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 10, height: 5))],
      gaps: [5, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.rightInset, 0)
  }

  func test_InHorizontalContainer_BottomInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 10, height: 5))],
      gaps: [5, 10],
      layoutDirection: .horizontal,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.bottomInset, 0)
  }

  func test_InHorizontalContainer_RightInsetEqualsLastGap() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 10, height: 5))],
      gaps: [5, 10],
      layoutDirection: .horizontal,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.rightInset, 10)
  }

  func test_HorizontalWrapContainer_OneLine() {
    let block = BlockTestsModels.block(withSize: blockSize)
    let layout = ContainerBlockLayout(
      blocks: [block, block],
      gaps: [0, 0, 0],
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      size: CGSize(width: blockSize.width * 3, height: blockSize.height * 3)
    )

    XCTAssertEqual(layout.contentSize.height, blockSize.height)
  }

  func test_HorizontalWrapContainer_TwoLine() {
    let block = BlockTestsModels.block(withSize: blockSize)
    let layout = ContainerBlockLayout(
      blocks: [block, block],
      gaps: [0, 0, 0],
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      size: CGSize(width: blockSize.width, height: blockSize.height * 3)
    )

    XCTAssertEqual(layout.contentSize.height, blockSize.height * 2)
  }

  func test_VerticalWrapContainer_OneLine() {
    let block = BlockTestsModels.block(withSize: blockSize)
    let layout = ContainerBlockLayout(
      blocks: [block, block],
      gaps: [0, 0, 0],
      layoutDirection: .vertical,
      layoutMode: .wrap,
      size: CGSize(width: blockSize.width * 3, height: blockSize.height * 3)
    )

    XCTAssertEqual(layout.contentSize.width, blockSize.width)
  }

  func test_VerticalWrapContainer_TwoLine() {
    let block = BlockTestsModels.block(withSize: blockSize)
    let layout = ContainerBlockLayout(
      blocks: [block, block],
      gaps: [0, 0, 0],
      layoutDirection: .vertical,
      layoutMode: .wrap,
      size: CGSize(width: blockSize.width * 3, height: blockSize.height)
    )

    XCTAssertEqual(layout.contentSize.width, blockSize.width * 2)
  }

  func test_ContentWidthEqualsRightmostMaxXOfAllFrames() {
    let layout = ContainerBlockLayout(
      blocks: [
        BlockTestsModels.block(withSize: CGSize(width: 10, height: 15)),
        BlockTestsModels.block(withSize: CGSize(width: 10, height: 5)),
      ],
      gaps: [5, 0, 10],
      layoutDirection: .horizontal,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.contentSize.width, 25)
  }

  func test_InVerticalContainer_ContentHeightEqualsMaxOfMaxXOfAllFrames() {
    let layout = ContainerBlockLayout(
      blocks: [
        BlockTestsModels.block(withSize: CGSize(width: 10, height: 15)),
        BlockTestsModels.block(withSize: CGSize(width: 10, height: 5)),
      ],
      gaps: [5, 0, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.contentSize.height, 25)
  }

  func test_InHorizontalContainer_LeftInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [BlockTestsModels.block(withSize: CGSize(width: 110, height: 15))],
      gaps: [5, 10],
      layoutDirection: .horizontal,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.leftInset, 0)
  }

  func test_CenteredConstrainedElementLayout() {
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      text: text
    )
    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, intrinsicTextSize.width)

    let layout = ContainerBlockLayout(
      blocks: [
        block,
      ],
      gaps: [0, 0],
      layoutDirection: .horizontal,
      axialAlignment: .center,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.blockFrames[0].minX, 0)
  }

  func test_CenteredIntrinsicElementWithFixedBlockLayout() {
    let block1 = TextBlock(
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      text: text
    )
    let block2 = TextBlock(widthTrait: .fixed(40), text: text)

    let layout = ContainerBlockLayout(
      blocks: [
        block1,
        block2,
      ],
      gaps: [0, 0, 0],
      layoutDirection: .horizontal,
      axialAlignment: .center,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.blockFrames[0].minX, 0)
    XCTAssertEqual(layout.blockFrames[1].minX, 60)
  }

  func test_TrailingConstrainedElementLayout() {
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      text: text
    )

    let layout = ContainerBlockLayout(
      blocks: [
        block,
      ],
      gaps: [0, 0],
      layoutDirection: .horizontal,
      axialAlignment: .trailing,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.blockFrames[0].minX, 0)
  }

  func test_TrailingIntrinsicElementWithFixedBlockLayout() {
    let block1 = TextBlock(
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      text: text
    )
    let block2 = TextBlock(widthTrait: .fixed(40), text: text)

    let layout = ContainerBlockLayout(
      blocks: [
        block1,
        block2,
      ],
      gaps: [0, 0, 0],
      layoutDirection: .horizontal,
      axialAlignment: .trailing,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.blockFrames[0].minX, 0)
    XCTAssertEqual(layout.blockFrames[1].minX, 60)
  }

  func test_VerticallyConstrainedElementInHorizontalLayout() {
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      heightTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      text: text
    )

    let layout = ContainerBlockLayout(
      blocks: [
        block,
      ],
      gaps: [0, 0],
      layoutDirection: .horizontal,
      axialAlignment: .center,
      size: CGSize(width: 10, height: 10)
    )

    XCTAssertEqual(layout.blockFrames[0], CGRect(x: 0, y: 0, width: 10, height: 10))
  }

  func test_HorizontallyConstrainedElementInVerticalLayout() {
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      heightTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      text: text
    )

    let layout = ContainerBlockLayout(
      blocks: [
        block,
      ],
      gaps: [0, 0],
      layoutDirection: .vertical,
      axialAlignment: .center,
      size: CGSize(width: 10, height: 10)
    )

    XCTAssertEqual(layout.blockFrames[0], CGRect(x: 0, y: 0, width: 10, height: 10))
  }

  func test_HorizontallyConstrainedElementInVerticalTrailingLayout() {
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      heightTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      text: text
    )

    let layout = ContainerBlockLayout(
      blocks: [
        block,
      ],
      gaps: [0, 0],
      layoutDirection: .vertical,
      axialAlignment: .trailing,
      size: CGSize(width: 10, height: 10)
    )

    XCTAssertEqual(layout.blockFrames[0], CGRect(x: 0, y: 0, width: 10, height: 10))
  }

  // MARK: Constrained elements layout tests

  func test_WhenConstrainedItemsWithZeroSize_ProducedZeroSize() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 0, minSize: 0),
          ConstrainedBlockSize(size: 0, minSize: 0),
        ],
        lengthToDecrease: 10000
      ))

    XCTAssertEqual(blockSizes, [0, 0])
  }

  func test_WhenTwoConstrainedElementsWithEqualMinSizesDecreasedToMinSizes() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 100, minSize: 50),
          ConstrainedBlockSize(
            size: 100,
            minSize: 50
          ),
        ],
        lengthToDecrease: 10000
      ))

    XCTAssertEqual(blockSizes, [50, 50])
  }

  func test_WhenTwoConstrainedElementsWithEqualMinSizesDecreasedProportionally() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 100, minSize: 50),
          ConstrainedBlockSize(
            size: 100,
            minSize: 50
          ),
        ],
        lengthToDecrease: 50
      ))

    XCTAssertEqual(blockSizes, [75, 75])
  }

  func test_WhenTwoConstrainedElementsWithDifferentMinSizesDecreasedToMinSizes() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 100, minSize: 75),
          ConstrainedBlockSize(
            size: 100,
            minSize: 50
          ),
        ],
        lengthToDecrease: 10000
      ))

    XCTAssertEqual(blockSizes, [75, 50])
  }

  func test_WhenFirstConstrainedElementDecreasedToMinSize() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 100, minSize: 75),
          ConstrainedBlockSize(
            size: 100,
            minSize: 50
          ),
        ],
        lengthToDecrease: 60
      ))

    XCTAssertEqual(blockSizes, [75, 65])
  }

  func test_WhenFirstConstrainedElementDecreasedToMinSizeAndOtherDecreasedProportionally() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 100, minSize: 75),
          ConstrainedBlockSize(
            size: 100,
            minSize: 50
          ),
          ConstrainedBlockSize(
            size: 100,
            minSize: 60
          ),
        ],
        lengthToDecrease: 85
      ))

    XCTAssertEqual(blockSizes, [75, 70, 70])
  }

  func test_WhenElementsUnsortedByDecreaseSize() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 100, minSize: 60),
          ConstrainedBlockSize(
            size: 100,
            minSize: 50
          ),
          ConstrainedBlockSize(
            size: 100,
            minSize: 75
          ),
        ],
        lengthToDecrease: 85
      ))

    XCTAssertEqual(blockSizes, [70, 70, 75])
  }

  func test_WhenConstrainedElementsHaveDifferentSizes() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 100, minSize: 0),
          ConstrainedBlockSize(size: 200, minSize: 0),
        ],
        lengthToDecrease: 60
      ))

    XCTAssertEqual(blockSizes, [80, 160])
  }

  func test_WhenConstrainedElementsHaveDifferentSizesAndFirstElementDecreasedToMinSize() {
    let blockSizes =
      Array(decreaseConstrainedBlockSizes(
        blockSizes: [
          ConstrainedBlockSize(size: 100, minSize: 99),
          ConstrainedBlockSize(size: 200, minSize: 0),
          ConstrainedBlockSize(size: 400, minSize: 0),
        ],
        lengthToDecrease: 7 + 30
      ))
    // at first iteration block sizes: [100 - 1, 198 - 2, 396 - 4]
    XCTAssertEqual(blockSizes, [99, 188, 376])
  }

  func test_TwoHorizontallyConstrainedElementsInHorizontalLayout() {
    let blockWidth: CGFloat = 100
    let block = BlockWithFixedWrapContent(
      width: blockWidth,
      constrainedHorizontally: true
    )

    let layout = ContainerBlockLayout(
      blocks: [
        block,
        block,
      ],
      gaps: [0, 0, 0],
      layoutDirection: .horizontal,
      size: CGSize(width: 150, height: 10)
    )

    XCTAssertEqual(layout.blockFrames.map(\.width), [75, 75])
  }

  func test_TwoHorizontallyConstrainedElementsInHorizontalLayoutDecreasedProportionally() {
    let blockA = BlockWithFixedWrapContent(
      width: 100,
      constrainedHorizontally: true
    )

    let blockB = BlockWithFixedWrapContent(
      width: 200,
      constrainedHorizontally: true
    )

    let layout = ContainerBlockLayout(
      blocks: [
        blockA,
        blockB,
      ],
      gaps: [0, 0, 0],
      layoutDirection: .horizontal,
      size: CGSize(width: 270, height: 0)
    )
    XCTAssertEqual(layout.blockFrames[0].width / layout.blockFrames[1].width, 0.5, accuracy: 1e-5)
  }

  func test_TwoVerticallyConstrainedElementsInVerticalLayout() {
    let blockHeight: CGFloat = 100
    let block = BlockWithFixedWrapContent(
      height: blockHeight,
      constrainedVertically: true
    )

    let layout = ContainerBlockLayout(
      blocks: [
        block,
        block,
      ],
      gaps: [0, 0, 0],
      layoutDirection: .vertical,
      size: CGSize(width: 0, height: 150)
    )

    XCTAssertEqual(layout.blockFrames.map(\.height), [75, 75])
  }

  func test_TwoVerticallyConstrainedElementsInVerticalLayoutDecreasedProportionally() {
    let blockA = BlockWithFixedWrapContent(
      height: 100,
      constrainedVertically: true
    )

    let blockB = BlockWithFixedWrapContent(
      height: 200,
      constrainedVertically: true
    )

    let layout = ContainerBlockLayout(
      blocks: [
        blockA,
        blockB,
      ],
      gaps: [0, 0, 0],
      layoutDirection: .vertical,
      size: CGSize(width: 0, height: 270)
    )
    XCTAssertEqual(layout.blockFrames[0].height / layout.blockFrames[1].height, 0.5, accuracy: 1e-5)
  }
}

private let containerBlock = try! ContainerBlock(
  layoutDirection: .horizontal,
  children: [GalleryBlockTestModels.base]
)

private let blockSize = CGSize(width: 10, height: 5)
private let imageSize = ImageBlockTestModels.imageSize
private let image = ImageBlockTestModels.image
private let text = TextBlockTestModels.text
private let intrinsicTextSize = TextBlockTestModels.intrinsicTextSize
private let multilineTextSize = text.sizeForWidth(imageSize.width)
