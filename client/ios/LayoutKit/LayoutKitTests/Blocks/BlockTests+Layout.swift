@testable import LayoutKit

import XCTest

import BaseUI
import CommonCore

extension ContainerBlockLayout {
  fileprivate init(
    blocks: [Block],
    gaps: [CGFloat],
    layoutDirection: ContainerBlock.LayoutDirection,
    layoutMode: ContainerBlock.LayoutMode = .noWrap,
    crossAlignment: ContainerBlock.CrossAlignment = .leading,
    axialAlignment: Alignment = .leading,
    size: CGSize
  ) {
    self.init(
      children: blocks.map { .init(content: $0, crossAlignment: crossAlignment) },
      gaps: gaps,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode,
      axialAlignment: axialAlignment,
      size: size
    )
  }
}

final class BlockTests_Layout: XCTestCase {
  func test_IntrinsicWidthOfTextBlock_EqualsTextWidth() {
    let block = TextBlock(widthTrait: .intrinsic, text: text)

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, intrinsicTextSize.width)
  }

  func test_FixedWidthOfTextBlock_IsEqualToAssociatedValue() {
    let anyWidth = intrinsicTextSize.width / 2
    let block = TextBlock(widthTrait: .fixed(anyWidth), text: text)

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, anyWidth)
  }

  func test_IntrinsicWidthOfResizableTextBlock_EqualsToZero() {
    let block = TextBlock(widthTrait: .resizable, text: text)

    XCTAssertEqual(block.intrinsicContentWidth, 0)
  }

  func test_IntrinsicWidthOfFixedTextBlock_EqualsToAssociatedValue() {
    let fixedWidth: CGFloat = 42
    let block = TextBlock(widthTrait: .fixed(fixedWidth), text: text)

    XCTAssertEqual(block.intrinsicContentWidth, fixedWidth)
  }

  func test_IntrinsicWidthOfIntrinsicTextBlock_EqualsToTextWidth() {
    let block = TextBlock(widthTrait: .intrinsic, text: text)

    XCTAssertEqual(block.intrinsicContentWidth, intrinsicTextSize.width)
  }

  func test_WidthOfImageBlock_EqualsPlaceholderImageWidth() {
    let block = ImageBlock(imageHolder: image)

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, imageSize.width)
  }

  func test_IntrinsicWidthOfResizableImageBlock_EqualsToZero() {
    let block = ImageBlock(imageHolder: image, widthTrait: .resizable)

    XCTAssertEqual(block.intrinsicContentWidth, 0)
  }

  func test_IntrinsicWidthOfFixedImageBlock_EqualsToAssociatedValue() {
    let fixedWidth: CGFloat = 42
    let block = ImageBlock(imageHolder: image, widthTrait: .fixed(fixedWidth))

    XCTAssertEqual(block.intrinsicContentWidth, fixedWidth)
  }

  func test_IntrinsicWidthOfIntrinsicImageBlock_EqualsPlaceholderImageWidth() {
    let block = ImageBlock(imageHolder: image, widthTrait: .intrinsic)

    XCTAssertEqual(block.intrinsicContentWidth, imageSize.width)
  }

  func test_WidthOfSwitchBlock_EqualsUISwitchWidth() {
    let block = SwitchBlock(on: true, enabled: true, action: nil)

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, uiSwitchSize.width)
  }

  func test_IntrinsicWidthOfSwitchBlock_EqualsToUISwitchWidth() {
    let block = SwitchBlock(on: true, enabled: true, action: nil)

    XCTAssertEqual(block.intrinsicContentWidth, uiSwitchSize.width)
  }

  func test_calculateWidthFirstForHorizontalWrapContainer_EqualsTrue() {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      children: [TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.calculateWidthFirst, true)
  }

  func test_calculateWidthFirstForVerticalWrapContainer_EqualsFalse() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      children: [TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.calculateWidthFirst, false)
  }

  func test_HeightOfVerticalWrapContainer_EqualsSumHeightOfChildrenWithGaps() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      heightTrait: .intrinsic,
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    let expectedHeight = imageSize.height + intrinsicTextSize.height + threeGapsSize
    XCTAssertEqual(block.heightOfVerticallyNonResizableBlock, expectedHeight)
  }

  func test_FixedHeightOfVerticalWrapContainer_IsEqualToAssociatedValue() {
    let anyHeight = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      heightTrait: .fixed(anyHeight),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.heightOfVerticallyNonResizableBlock, anyHeight)
  }

  func test_FixedWidthOfHorizontalWrapContainer_IsEqualToAssociatedValue() {
    let anyWidth = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      widthTrait: .fixed(anyWidth),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, anyWidth)
  }

  func test_WidthOfHorizontalWrapContainerWithHorizontallyNonResizableBlocks_EqualsSumOfChildrenWidthsPlusGaps(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      widthTrait: .intrinsic,
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    let expectedWidth = imageSize.width + intrinsicTextSize.width + threeGapsSize
    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, expectedWidth)
  }

  func test_WidthOfHorizontalWrapContainerWithHorizontallyResizableBlocks_EqualsToSumOfGaps() {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      widthTrait: .resizable,
      gaps: threeGaps,
      children: [
        ImageBlock(imageHolder: image, widthTrait: .resizable),
        TextBlock(widthTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentWidth, threeGapsSize)
  }

  func test_WidthOfVerticalContainer_EqualsMaxWidthOfChildren() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .intrinsic,
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, intrinsicTextSize.width)
  }

  func test_FixedWidthOfVerticalContainer_IsEqualToAssociatedValue() {
    let anyWidth = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .fixed(anyWidth),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, anyWidth)
  }

  func test_IntrinsicWidthOfVerticalContainerWithHorizontallyResizableBlocks_EqualsToZero() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .resizable,
      gaps: threeGaps,
      children: [
        ImageBlock(imageHolder: image, widthTrait: .resizable),
        TextBlock(widthTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentWidth, 0)
  }

  func test_IntrinsicWidthOfFixedHorizontalContainer_EqualsToAssociatedValue() {
    let anyWidth = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .fixed(anyWidth),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.intrinsicContentWidth, anyWidth)
  }

  func test_FixedWidthOfHorizontalContainer_IsEqualToAssociatedValue() {
    let anyWidth = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .fixed(anyWidth),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, anyWidth)
  }

  func test_WidthOfHorizontalContainerWithHorizontallyNonResizableBlocks_EqualsSumOfChildrenWidthsPlusGaps(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .intrinsic,
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    let expectedWidth = imageSize.width + intrinsicTextSize.width + threeGapsSize
    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, expectedWidth)
  }

  func test_WidthOfHorizontalContainerWithHorizontallyResizableBlocks_EqualsToSumOfGaps() {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .resizable,
      gaps: threeGaps,
      children: [
        ImageBlock(imageHolder: image, widthTrait: .resizable),
        TextBlock(widthTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentWidth, threeGapsSize)
  }

  func test_WidthOfVerticalContainerWithHorizontallyResizableBlock_EqualsMaxWidthOfNonResizableChildren(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .intrinsic,
      gaps: [10, 20, 30, 40],
      children: [
        ImageBlock(imageHolder: image),
        TextBlock(widthTrait: .resizable, text: text),
        TextBlock(widthTrait: .intrinsic, text: text),
      ]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, intrinsicTextSize.width)
  }

  func test_WidthOfVerticalContainerWithHorizontallyResizableBlock_EqualsToZero() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .resizable,
      children: [
        ImageBlock(imageHolder: image, widthTrait: .resizable),
        TextBlock(widthTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentWidth, 0)
  }

  func test_WhenMakingContainerWithNoChildren_Throws() {
    XCTAssertThrowsError(
      try ContainerBlock(layoutDirection: .horizontal, children: []),
      ContainerBlock.Error.noChildrenProvided
    )
  }

  func test_WhenMakingContainerWithWrongNumberOfGaps_Throws() {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .horizontal,
        gaps: [12],
        children: [TextBlock(widthTrait: .resizable, text: text)]
      ),
      ContainerBlock.Error.childAndGapCountMismatch
    )
  }

  func test_WhenMakingHorizontalWrapContainer_WithVerticallyResizableChildren_Throws(
  ) {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .horizontal,
        layoutMode: .wrap,
        children: [
          TextBlock(widthTrait: .intrinsic, heightTrait: .resizable, text: text),
        ]
      ),
      ContainerBlock.Error.inconsistentChildLayoutTraits(
        details: "failed to build horizontal wrap container with vertically resizable children"
      )
    )
  }

  func test_WhenMakingVerticalWrapContainer_WithHorizontallyResizableChildren_Throws(
  ) {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .vertical,
        layoutMode: .wrap,
        children: [
          TextBlock(widthTrait: .resizable, text: text),
        ]
      ),
      ContainerBlock.Error.inconsistentChildLayoutTraits(
        details: "failed to build vertical wrap container with horizontally resizable children"
      )
    )
  }

  func test_WhenMakingHorizontalContainerWithIntrinsicWidth_AndWithHorizontallyResizableChildren_Throws(
  ) {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .horizontal,
        widthTrait: .intrinsic,
        children: [
          ImageBlock(imageHolder: image, widthTrait: .fixed(10)),
          TextBlock(widthTrait: .weighted(0.5), text: text),
        ]
      ),
      ContainerBlock.Error.inconsistentChildLayoutTraits(
        details: "failed to build intrinsic-width horizontal container with horizontally resizable children"
      )
    )
  }

  func test_WhenMakingVerticalContainerWithIntrinsicWidth_AndWithAllHorizontallyResizableChildren_Throws(
  ) {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .vertical,
        widthTrait: .intrinsic,
        children: [
          ImageBlock(imageHolder: image, widthTrait: .resizable),
          TextBlock(widthTrait: .resizable, text: text),
        ]
      ),
      ContainerBlock.Error.inconsistentChildLayoutTraits(
        details: "failed to build intrinsic-width vertical container wihtout any horizontally nonresizable children"
      )
    )
  }

  func test_WhenMakingVerticalContainerWithIntrinsicHeight_AndWithVerticallyResizableChildren_Throws(
  ) {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .vertical,
        heightTrait: .intrinsic,
        children: [
          ImageBlock(imageHolder: image, heightTrait: .weighted(0.5)),
          TextBlock(widthTrait: .resizable, text: text),
        ]
      ),
      ContainerBlock.Error.inconsistentChildLayoutTraits(
        details: "failed to build intrinsic-height vertical container with vertically resizable children"
      )
    )
  }

  func test_WhenMakingHorizontalContainerWithTwoConstrainedChildren_Throws(
  ) {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .horizontal,
        widthTrait: .intrinsic,
        children: [
          ImageBlock(imageHolder: image, widthTrait: .intrinsic(constrained: true)),
          TextBlock(widthTrait: .intrinsic(constrained: true), text: text),
        ]
      ),
      ContainerBlock.Error.moreThanOneConstrainedChild
    )
  }

  func test_WhenMakingVerticalContainerWithTwoConstrainedChildren_Throws(
  ) {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .vertical,
        heightTrait: .intrinsic,
        children: [
          ImageBlock(imageHolder: image, heightTrait: .intrinsic(constrained: true)),
          TextBlock(
            widthTrait: .intrinsic,
            heightTrait: .intrinsic(constrained: true),
            text: text
          ),
        ]
      ),
      ContainerBlock.Error.moreThanOneConstrainedChild
    )
  }

  func test_WidthOfHorizontallyResizableBlockInVerticalContainer_EqualsWidthOfContainer() {
    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: threeGaps,
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
      gaps: threeGaps,
      layoutDirection: .vertical,
      size: imageSize
    )

    let blockOriginYs = layout.blockFrames.map { $0.origin.y }
    XCTAssertEqual(blockOriginYs, [threeGaps[0], threeGaps[0] + imageSize.height + threeGaps[1]])
  }

  func test_InVerticalContainerWithCenterChildrenAlignment_BlocksAreLaidOutOneBelowAnotherInCenter(
  ) {
    let containerSize = CGSize(width: imageSize.width, height: 200)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: threeGaps,
      layoutDirection: .vertical,
      axialAlignment: .center,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map { $0.origin.y }
    let firstBlockOrigin =
      floor(
        threeGaps[0] +
          (containerSize.height - (imageSize.height + multilineTextSize.height + threeGapsSize)) / 2
      )
    let expectedBlockOrigins = [
      firstBlockOrigin,
      firstBlockOrigin + imageSize.height + threeGaps[1],
    ]
    zip(blockOriginYs, expectedBlockOrigins).forEach { XCTAssertEqual($0.0, $0.1, accuracy: 1e-4) }
  }

  func test_InVerticalContainerWithTrailingChildrenAlignment_BlocksAreLaidOutOneBelowAnotherAtBottom(
  ) {
    let containerSize = CGSize(width: imageSize.width, height: 200)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: threeGaps,
      layoutDirection: .vertical,
      axialAlignment: .trailing,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map { $0.origin.y }
    let firstBlockOrigin = floor(
      threeGaps[0] + containerSize
        .height - (imageSize.height + multilineTextSize.height + threeGapsSize)
    )
    let expectedBlockOrigins = [
      firstBlockOrigin,
      firstBlockOrigin + imageSize.height + threeGaps[1],
    ]
    zip(blockOriginYs, expectedBlockOrigins).forEach { XCTAssertEqual($0.0, $0.1, accuracy: 1e-4) }
  }

  func test_InHorizontalContainerWithLeadingChildrenAlignment_BlocksAreLaidOutStartingFromTop() {
    let containerSize = CGSize(width: imageSize.width, height: 200)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)],
      gaps: threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map { $0.origin.y }
    XCTAssertEqual(blockOriginYs, [0, 0])
  }

  func test_InHorizontalContainerWithCenterChidlrenAlignment_BlocksAreCeneteredVertically() {
    let containerSize = CGSize(width: 300, height: 200)
    let textBlock = TextBlock(widthTrait: .resizable, text: text)

    let layout = ContainerBlockLayout(
      blocks: [ImageBlock(imageHolder: image), textBlock],
      gaps: threeGaps,
      layoutDirection: .horizontal,
      crossAlignment: .center,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map { $0.origin.y }
    let textBlockWidth = containerSize.width - (imageSize.width + threeGapsSize)
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
      gaps: threeGaps,
      layoutDirection: .horizontal,
      crossAlignment: .trailing,
      size: containerSize
    )

    let blockOriginYs = layout.blockFrames.map { $0.origin.y }
    let textBlockWidth = containerSize.width - (imageSize.width + threeGapsSize)
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
      gaps: threeGaps,
      layoutDirection: .vertical,
      size: containerSize
    )

    let blockHeights = layout.blockFrames.map { $0.height }
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
        heightTrait: .weighted(2),
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

    let expectedWeightUnit = (containerSize.height - imageSize.height) / 3
    XCTAssertEqual(layout.blockFrames[1].height, floor(expectedWeightUnit))
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
        widthTrait: .weighted(2),
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

    let expectedWeightUnit = (containerSize.width - imageSize.width) / 3
    XCTAssertEqual(layout.blockFrames[1].width, floor(expectedWeightUnit * 2))
    XCTAssertEqual(
      layout.blockFrames[2].width,
      containerSize.width - imageSize.width - layout.blockFrames[1].width
    )
  }

  func test_HeightOfTextBlock_EqualsTextHeight() {
    let block = TextBlock(widthTrait: .intrinsic, text: text)

    XCTAssertEqual(
      block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width),
      multilineTextSize.height
    )
  }

  func test_HeightOfTextBlock_IsIntrinsicByDefault() {
    let implicitlyIntrinsicHeightBlock = TextBlock(widthTrait: .intrinsic, text: text)
    let explicitlyIntrinsicHeightBlock = TextBlock(
      widthTrait: .intrinsic,
      heightTrait: .intrinsic,
      text: text
    )

    XCTAssertTrue(explicitlyIntrinsicHeightBlock.equals(implicitlyIntrinsicHeightBlock))
  }

  func test_FixedHeightOfTextBlock_IsEqualToAssociatedValue() {
    let anyHeight: CGFloat = 20
    let block = TextBlock(widthTrait: .intrinsic, heightTrait: .fixed(anyHeight), text: text)

    XCTAssertEqual(block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfResizableTextBlock_EqualsToZero() {
    let anyWidth: CGFloat = 42
    let block = TextBlock(widthTrait: .intrinsic, heightTrait: .resizable, text: text)

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: anyWidth), 0)
  }

  func test_HeightOfImageBlock_EqualsPlaceholderImageHeight() {
    let block = ImageBlock(imageHolder: image)

    XCTAssertEqual(
      block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width),
      imageSize.height
    )
  }

  func test_IntrinsicHeightOfResizalbeImageBlock_EqualsToZero() {
    let anyWidth: CGFloat = 42
    let block = ImageBlock(imageHolder: image, heightTrait: .resizable)

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: anyWidth), 0)
  }

  func test_HeightOfSwitchBlock_EqualsUISwitchHeight() {
    let block = SwitchBlock(on: true, enabled: true, action: nil)

    XCTAssertEqual(
      block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width),
      uiSwitchSize.height
    )
  }

  func test_IntrinsicHeightOfSwitchBlock_EqualsUISwitchHeight() {
    let anyWidth: CGFloat = 42
    let block = SwitchBlock(on: true, enabled: true, action: nil)

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: anyWidth), uiSwitchSize.height)
  }

  func test_HeightOfVerticalContainerWithoutVerticallyResizableBlocks_EqualsTotalHeightOfChildrenPlusGaps(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .intrinsic,
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    let expectedHeight = imageSize.height + intrinsicTextSize.height + threeGapsSize
    XCTAssertEqual(
      block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width),
      expectedHeight
    )
  }

  func test_IntrinsicHeightOfResizableVerticalContainerWithoutVerticallyResizableBlocks_EqualsTotalHeightOfChildrenPlusGaps(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      heightTrait: .resizable,
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)]
    )

    let expectedHeight = imageSize.height + multilineTextSize.height + threeGapsSize
    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), expectedHeight)
  }

  func test_FixedHeightOfVerticalContainer_IsEqualToAssociatedValue() {
    let anyHeight = imageSize.height
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      heightTrait: .fixed(anyHeight),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfFixedVerticalContainer_IsEqualToAssociatedValue() {
    let anyHeight = imageSize.height
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      heightTrait: .fixed(anyHeight),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfResizableVerticalContainerWithOnlyVerticallyResizableChildren_IsEqualToSumOfGaps(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      heightTrait: .resizable,
      gaps: threeGaps,
      children: [
        ImageBlock(imageHolder: image, heightTrait: .resizable),
        TextBlock(widthTrait: .intrinsic, heightTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), threeGapsSize)
  }

  func test_FixedHeightOfHorizontalContainer_IsEqualToAssociatedValue() {
    let anyHeight = imageSize.height / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .fixed(anyHeight),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfFixedHorizontalContainer_IsEqualToAssociatedValue() {
    let anyHeight = imageSize.height / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .fixed(anyHeight),
      gaps: threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfIntrinsicHorizontalContainerWithoutVerticallyResizableBlocks_EqualsToMaxHeightOfChildren(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .intrinsic,
      gaps: threeGaps,
      children: [
        TextBlock(widthTrait: .resizable, text: text),
        TextBlock(widthTrait: .intrinsic, text: text),
      ]
    )

    XCTAssertEqual(
      block.intrinsicContentHeight(forWidth: intrinsicTextSize.width * 2),
      multilineTextSize.height
    )
  }

  func test_IntrinsicHeightOfResizableHorizontalContainerWithoutVerticallyResizableBlocks_EqualsToMaxHeightOfChildren(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .resizable,
      gaps: threeGaps,
      children: [
        TextBlock(widthTrait: .resizable, text: text),
        TextBlock(widthTrait: .intrinsic, text: text),
      ]
    )

    XCTAssertEqual(
      block.intrinsicContentHeight(forWidth: intrinsicTextSize.width * 2),
      multilineTextSize.height
    )
  }

  func test_IntrinsicHeightOfIntrinsicHorizontalContainerWithOnlyVerticallyResizableBlocks_EqualsToZero(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .intrinsic,
      gaps: threeGaps,
      children: [
        ImageBlock(imageHolder: image, heightTrait: .resizable),
        TextBlock(widthTrait: .intrinsic, heightTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: intrinsicTextSize.width * 2), 0)
  }

  func test_IntrinsicHeightOfResizableHorizontalContainerWithOnlyVerticallyResizableBlocks_EqualsToZero(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .resizable,
      gaps: threeGaps,
      children: [
        ImageBlock(imageHolder: image, heightTrait: .resizable),
        TextBlock(widthTrait: .intrinsic, heightTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: intrinsicTextSize.width * 2), 0)
  }

  func test_HeightOfHorizontalContainerWithoutVerticallyResizableBlocks_EqualsMaxHeightOfChildren() {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .resizable,
      gaps: threeGaps,
      children: [
        TextBlock(widthTrait: .resizable, text: text),
        TextBlock(widthTrait: .intrinsic, text: text),
      ]
    )

    XCTAssertEqual(
      block.heightOfVerticallyNonResizableBlock(forWidth: intrinsicTextSize.width * 2),
      multilineTextSize.height
    )
  }

  func test_HeightOfHorizontalContainerWithVerticallyResizableBlock_EqualsMaxHeightOfChildren() {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .intrinsic,
      gaps: threeGaps + [40],
      children: [
        ImageBlock(imageHolder: image),
        TextBlock(widthTrait: .intrinsic, text: text),
        try! ContainerBlock(
          layoutDirection: .vertical,
          widthTrait: .intrinsic,
          heightTrait: .resizable,
          verticalChildrenAlignment: .center,
          gaps: [10, 20],
          children: [TextBlock(widthTrait: .intrinsic, text: text)]
        ),
      ]
    )

    XCTAssertEqual(
      block.heightOfVerticallyNonResizableBlock(forWidth: intrinsicTextSize.width * 2),
      imageSize.height
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
      gaps: threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )

    let resizableBlockFrame = layout.blockFrames[1]
    XCTAssertEqual(resizableBlockFrame.width, containerSize.width - imageSize.width - threeGapsSize)
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
      gaps: threeGaps,
      layoutDirection: .vertical,
      size: containerSize
    )

    let resizableBlockFrame = layout.blockFrames[1]
    XCTAssertEqual(
      resizableBlockFrame.height,
      containerSize.height - imageSize.height - threeGapsSize
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
      gaps: threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )

    let blockOriginXs = layout.blockFrames.map { $0.origin.x }
    XCTAssertEqual(blockOriginXs, [threeGaps[0], threeGaps[0] + imageSize.width + threeGaps[1]])
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
      gaps: threeGaps,
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
      gaps: threeGaps,
      layoutDirection: .horizontal,
      size: containerSize
    )

    let resizableBlockFrame = layout.blockFrames[1]
    XCTAssertEqual(resizableBlockFrame.height, containerSize.height)
  }

  func test_WhenItemDoesNotFitHeightInVerticalContainer_TopInsetEqualsNonFittingSizePlusFirstGap() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 5, height: 102))],
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
      blocks: [block(withSize: CGSize(width: 5, height: 102))],
      gaps: [10, 10],
      layoutDirection: .horizontal,
      crossAlignment: .center,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.topInset, 1)
  }

  func test_WhenItemFitsHeightContainer_TopInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 5, height: 40))],
      gaps: [10, 10],
      layoutDirection: .vertical,
      axialAlignment: .center,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.topInset, 0)
  }

  func test_WhenItemDoesNotFitHeightInContainerWithVerticallyLeadingChildren_TopInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 5, height: 102))],
      gaps: [10, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.topInset, 0)
  }

  func test_WhenItemDoesNotFitWidthInVerticalContainer_LeftInsetEqualsNonFittingSize() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 102, height: 5))],
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
      blocks: [block(withSize: CGSize(width: 10, height: 5))],
      gaps: [10, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.leftInset, 0)
  }

  func test_InVerticalContainer_BottomInsetEqualsLastGap() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 10, height: 5))],
      gaps: [5, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.bottomInset, 10)
  }

  func test_InVerticalContainer_RightInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 10, height: 5))],
      gaps: [5, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.rightInset, 0)
  }

  func test_InHorizontalContainer_BottomInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 10, height: 5))],
      gaps: [5, 10],
      layoutDirection: .horizontal,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.bottomInset, 0)
  }

  func test_InHorizontalContainer_RightInsetEqualsLastGap() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 10, height: 5))],
      gaps: [5, 10],
      layoutDirection: .horizontal,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.rightInset, 10)
  }

  func test_HorizontalWrapContainer_OneLine() {
    let block = block(withSize: blockSize)
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
    let block = block(withSize: blockSize)
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
    let block = block(withSize: blockSize)
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
    let block = block(withSize: blockSize)
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
        block(withSize: CGSize(width: 10, height: 15)),
        block(withSize: CGSize(width: 10, height: 5)),
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
        block(withSize: CGSize(width: 10, height: 15)),
        block(withSize: CGSize(width: 10, height: 5)),
      ],
      gaps: [5, 0, 10],
      layoutDirection: .vertical,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.contentSize.height, 25)
  }

  func test_InHorizontalContainer_LeftInsetEqualsZero() {
    let layout = ContainerBlockLayout(
      blocks: [block(withSize: CGSize(width: 110, height: 15))],
      gaps: [5, 10],
      layoutDirection: .horizontal,
      size: CGSize(width: 100, height: 100)
    )

    XCTAssertEqual(layout.leftInset, 0)
  }

  func test_CenteredConstrainedElementLayout() {
    let block = TextBlock(widthTrait: .intrinsic(constrained: true), text: text)
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
    let block1 = TextBlock(widthTrait: .intrinsic(constrained: true), text: text)
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
    let block = TextBlock(widthTrait: .intrinsic(constrained: true), text: text)

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
    let block1 = TextBlock(widthTrait: .intrinsic(constrained: true), text: text)
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
      widthTrait: .intrinsic(constrained: true),
      heightTrait: .intrinsic(constrained: true),
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
      widthTrait: .intrinsic(constrained: true),
      heightTrait: .intrinsic(constrained: true),
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
      widthTrait: .intrinsic(constrained: true),
      heightTrait: .intrinsic(constrained: true),
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
}

private func block(withSize size: CGSize) -> Block {
  try! ContainerBlock(
    layoutDirection: .vertical,
    widthTrait: .fixed(size.width),
    heightTrait: .fixed(size.height),
    children: [TextBlock(widthTrait: .resizable, text: "".with(typo: Typo()))]
  )
}

private let threeGaps: [CGFloat] = [10, 20, 30]
private let threeGapsSize = threeGaps.reduce(0, +)
private let textTypo = Typo(size: .textM, weight: .regular).with(height: .textM).kerned(.textM)
private let text = "Северная Ирландия".with(typo: textTypo)
private let intrinsicTextSize = text.sizeForWidth(.infinity)
private let multilineTextSize = text.sizeForWidth(imageSize.width)
private let imageSize = CGSize(width: 80, height: 60)
private let image = Image.imageWithSolidColor(.black, size: imageSize)!
private let blockSize = CGSize(width: 10, height: 5)
