@testable import LayoutKit
import VGSL
import XCTest

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

  func test_IntrinsicWidthOfNotConstrainedTextBlockWithMinSize_LessThanTextWidth_EqualsTextWidth() {
    let block = TextBlock(
      widthTrait: .intrinsic(
        constrained: false,
        minSize: intrinsicTextSize.width / 2,
        maxSize: .infinity
      ),
      text: text
    )

    XCTAssertEqual(block.intrinsicContentWidth, intrinsicTextSize.width)
  }

  func test_IntrinsicWidthOfNotConstrainedTextBlockWithMinSize_MoreThanTextWidth_EqualsToAssociatedValue(
  ) {
    let minWidth = intrinsicTextSize.width * 2
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: false, minSize: minWidth, maxSize: .infinity),
      text: text
    )

    XCTAssertEqual(block.intrinsicContentWidth, minWidth)
  }

  func test_IntrinsicWidthOfNotConstrainedTextBlockWithMaxSize_LessThanTextWidth_EqualsToAssociatedValue(
  ) {
    let maxWidth = intrinsicTextSize.width / 2
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: maxWidth),
      text: text
    )

    XCTAssertEqual(block.intrinsicContentWidth, maxWidth)
  }

  func test_IntrinsicWidthOfNotConstrainedTextBlockWithMaxSize_LessThanTextWidth_EqualsToMultilineTextWidth(
  ) {
    let maxWidth = intrinsicTextSize.width / 2
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: maxWidth),
      text: text,
      tightenWidth: true
    )

    let result = block.intrinsicContentWidth
    XCTAssertTrue(result <= maxWidth)
    XCTAssertEqual(result, multilineTextSize.width)
  }

  func test_IntrinsicWidthOfNotConstrainedTextBlockWithMaxSize_MoreThanTextWidth_EqualsToTextWidth(
  ) {
    let block = TextBlock(
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: intrinsicTextSize.width * 2),
      text: text
    )

    XCTAssertEqual(block.intrinsicContentWidth, intrinsicTextSize.width)
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

  func test_IntrinsicWidthOfNotConstrainedImageBlockWithMinSize_MoreThanPlaceholderImageWidth_EqualsToAssociatedValue(
  ) {
    let minWidth = imageSize.width * 2
    let block = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic(constrained: false, minSize: minWidth, maxSize: .infinity)
    )

    XCTAssertEqual(block.intrinsicContentWidth, minWidth)
  }

  func test_IntrinsicWidthOfNotConstrainedImageBlockWithMaxSize_LessThanPlaceholderImageWidth_EqualsToAssociatedValue(
  ) {
    let maxWidth = imageSize.width / 2
    let block = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: maxWidth)
    )

    XCTAssertEqual(block.intrinsicContentWidth, maxWidth)
  }

  func test_IntrinsicHeightOfNotConstrainedImageBlockWithMinSize_LessThanPlaceholderImageWidth_EqualsToPlaceholderImageWidth(
  ) {
    let minHeight = imageSize.height / 2
    let block = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic,
      heightTrait: .intrinsic(constrained: false, minSize: minHeight, maxSize: .infinity)
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), imageSize.height)
  }

  func test_IntrinsicHeightOfNotConstrainedImageBlockWithMaxSize_MoreThanPlaceholderImageWidth_EqualsToPlaceholderImageWidth(
  ) {
    let maxHeight = imageSize.height * 2
    let block = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic,
      heightTrait: .intrinsic(constrained: false, minSize: 0, maxSize: maxHeight)
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), imageSize.height)
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

  func test_calculateWidthFirstForVerticalWrapContainerWithFixedWidth_EqualsTrue() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      widthTrait: .fixed(100),
      children: [TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.calculateWidthFirst, true)
  }

  func test_calculateWidthFirstForVerticalWrapContainerWithIntrinsicWidth_EqualsFalse() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      widthTrait: .intrinsic,
      children: [TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.calculateWidthFirst, false)
  }

  func test_HeightOfVerticalWrapContainer_EqualsSumHeightOfChildrenWithGaps() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      heightTrait: .intrinsic,
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    let expectedHeight = imageSize.height + intrinsicTextSize.height + ContainerBlockTestModels
      .threeGapsSize
    XCTAssertEqual(block.heightOfVerticallyNonResizableBlock, expectedHeight)
  }

  func test_FixedHeightOfVerticalWrapContainer_IsEqualToAssociatedValue() {
    let anyHeight = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      heightTrait: .fixed(anyHeight),
      gaps: ContainerBlockTestModels.threeGaps,
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
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, anyWidth)
  }

  func test_IntrinsicWidthOfNotConstrainedVerticalContainerWithMinSize_MoreThanMaxChildWidth_EqualsToAssociatedValue(
  ) {
    let minWidth = intrinsicTextSize.width * 2
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      widthTrait: .intrinsic(constrained: false, minSize: minWidth, maxSize: .infinity),
      gaps: [0, 0, 0],
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.intrinsicContentWidth, minWidth)
  }

  func test_IntrinsicWidthOfNotConstrainedHorizontalContainerWithMaxSize_LessThanSumOfChildrenWidths_EqualsToAssociatedValue(
  ) {
    let maxWidth = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: maxWidth),
      gaps: [0, 0, 0],
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.intrinsicContentWidth, maxWidth)
  }

  func test_IntrinsicHeightOfNotConstrainedVerticalContainedWithMinSize_MoreThanSumOfChildrenHeights_EqualsToAssociatedValue(
  ) {
    let minHeight = imageSize.height * 2
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      widthTrait: .intrinsic,
      heightTrait: .intrinsic(constrained: false, minSize: minHeight, maxSize: .infinity),
      gaps: [0, 0, 0],
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(
      block.intrinsicContentHeight(forWidth: imageSize.width + intrinsicTextSize.width),
      minHeight
    )
  }

  func test_IntrinsicHeightOfNotConstrainedVerticalContainerWithMaxSize_LessThanSumOfChildrenHeights_EqualsToAssociatedValue(
  ) {
    let maxHeight = imageSize.height / 2
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      layoutMode: .wrap,
      widthTrait: .intrinsic,
      heightTrait: .intrinsic(constrained: false, minSize: 0, maxSize: maxHeight),
      gaps: [0, 0, 0],
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(
      block.intrinsicContentHeight(forWidth: imageSize.width + intrinsicTextSize.width),
      maxHeight
    )
  }

  func test_WidthOfHorizontalWrapContainerWithHorizontallyNonResizableBlocks_EqualsSumOfChildrenWidthsPlusGaps(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      widthTrait: .intrinsic,
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    let expectedWidth = imageSize.width + intrinsicTextSize.width + ContainerBlockTestModels
      .threeGapsSize
    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, expectedWidth)
  }

  func test_WidthOfHorizontalWrapContainerWithHorizontallyResizableBlocks_EqualsToSumOfGaps() {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      layoutMode: .wrap,
      widthTrait: .resizable,
      gaps: ContainerBlockTestModels.threeGaps,
      children: [
        ImageBlock(imageHolder: image, widthTrait: .resizable),
        TextBlock(widthTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentWidth, ContainerBlockTestModels.threeGapsSize)
  }

  func test_WidthOfVerticalContainer_EqualsMaxWidthOfChildren() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .intrinsic,
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, intrinsicTextSize.width)
  }

  func test_FixedWidthOfVerticalContainer_IsEqualToAssociatedValue() {
    let anyWidth = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .fixed(anyWidth),
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, anyWidth)
  }

  func test_IntrinsicWidthOfVerticalContainerWithHorizontallyResizableBlocks_EqualsToZero() {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .resizable,
      gaps: ContainerBlockTestModels.threeGaps,
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
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.intrinsicContentWidth, anyWidth)
  }

  func test_FixedWidthOfHorizontalContainer_IsEqualToAssociatedValue() {
    let anyWidth = intrinsicTextSize.width / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .fixed(anyWidth),
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, anyWidth)
  }

  func test_WidthOfHorizontalContainerWithHorizontallyNonResizableBlocks_EqualsSumOfChildrenWidthsPlusGaps(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .intrinsic,
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    let expectedWidth = imageSize.width + intrinsicTextSize.width + ContainerBlockTestModels
      .threeGapsSize
    XCTAssertEqual(block.widthOfHorizontallyNonResizableBlock, expectedWidth)
  }

  func test_WidthOfHorizontalContainerWithHorizontallyResizableBlocks_EqualsToSumOfGaps() {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      widthTrait: .resizable,
      gaps: ContainerBlockTestModels.threeGaps,
      children: [
        ImageBlock(imageHolder: image, widthTrait: .resizable),
        TextBlock(widthTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(block.intrinsicContentWidth, ContainerBlockTestModels.threeGapsSize)
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

  func test_WhenMakingContainerWithWrongNumberOfGaps_Throws() {
    XCTAssertThrowsError(
      try ContainerBlock(
        layoutDirection: .horizontal,
        gaps: [12],
        children: [TextBlock(widthTrait: .resizable, text: text)]
      ),
      BlockError(
        "Container block error: " +
          "gaps count is not equal to children count plus 1 (1 != 1 + 1)"
      )
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
      BlockError(
        "Container block error: horizontal wrap container has children with resizable height"
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
      BlockError(
        "Container block error: vertical wrap container has children with resizable width"
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
      BlockError(
        "Container block error: horizontal intrinsic-width container has children with resizable width"
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
      BlockError(
        "Container block error: in vertical intrinsic-width container all children have resizable width"
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
      BlockError(
        "Container block error: vertical intrinsic-height container has children with resizable height"
      )
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
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    let expectedHeight = imageSize.height + intrinsicTextSize.height + ContainerBlockTestModels
      .threeGapsSize
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
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .resizable, text: text)]
    )

    let expectedHeight = imageSize.height + multilineTextSize.height + ContainerBlockTestModels
      .threeGapsSize
    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), expectedHeight)
  }

  func test_FixedHeightOfVerticalContainer_IsEqualToAssociatedValue() {
    let anyHeight = imageSize.height
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      heightTrait: .fixed(anyHeight),
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfFixedVerticalContainer_IsEqualToAssociatedValue() {
    let anyHeight = imageSize.height
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      heightTrait: .fixed(anyHeight),
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfResizableVerticalContainerWithOnlyVerticallyResizableChildren_IsEqualToSumOfGaps(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .vertical,
      heightTrait: .resizable,
      gaps: ContainerBlockTestModels.threeGaps,
      children: [
        ImageBlock(imageHolder: image, heightTrait: .resizable),
        TextBlock(widthTrait: .intrinsic, heightTrait: .resizable, text: text),
      ]
    )

    XCTAssertEqual(
      block.intrinsicContentHeight(forWidth: imageSize.width),
      ContainerBlockTestModels.threeGapsSize
    )
  }

  func test_FixedHeightOfHorizontalContainer_IsEqualToAssociatedValue() {
    let anyHeight = imageSize.height / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .fixed(anyHeight),
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.heightOfVerticallyNonResizableBlock(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfFixedHorizontalContainer_IsEqualToAssociatedValue() {
    let anyHeight = imageSize.height / 2
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .fixed(anyHeight),
      gaps: ContainerBlockTestModels.threeGaps,
      children: [ImageBlock(imageHolder: image), TextBlock(widthTrait: .intrinsic, text: text)]
    )

    XCTAssertEqual(block.intrinsicContentHeight(forWidth: imageSize.width), anyHeight)
  }

  func test_IntrinsicHeightOfIntrinsicHorizontalContainerWithoutVerticallyResizableBlocks_EqualsToMaxHeightOfChildren(
  ) {
    let block = try! ContainerBlock(
      layoutDirection: .horizontal,
      heightTrait: .intrinsic,
      gaps: ContainerBlockTestModels.threeGaps,
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
      gaps: ContainerBlockTestModels.threeGaps,
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
      gaps: ContainerBlockTestModels.threeGaps,
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
      gaps: ContainerBlockTestModels.threeGaps,
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
      gaps: ContainerBlockTestModels.threeGaps,
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
      gaps: ContainerBlockTestModels.threeGaps + [40],
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
}

private let text = TextBlockTestModels.text
private let intrinsicTextSize = TextBlockTestModels.intrinsicTextSize
private let multilineTextSize = text.sizeForWidth(imageSize.width)

private let imageSize = ImageBlockTestModels.imageSize
private let image = ImageBlockTestModels.image
