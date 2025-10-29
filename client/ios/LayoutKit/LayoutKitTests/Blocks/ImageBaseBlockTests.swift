@testable import LayoutKit
import VGSL
import XCTest

final class ImageBaseBlockTests: XCTestCase {
  func test_intrinsicContentWidth_whenWidthTraitIsFixed_returnsFixedValue() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .fixed(100),
      height: .trait(.intrinsic)
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, 100)
  }

  func test_intrinsicContentWidth_whenWidthTraitIsWeighted_returnsZero() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .weighted(1),
      height: .trait(.intrinsic)
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, 0)
  }

  func test_intrinsicContentWidth_whenWidthTraitIsIntrinsicAndHeightIsFixed_calculatesFromAspectRatio(
  ) {
    let fixedHeight: CGFloat = 50
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .intrinsic,
      height: .trait(.fixed(fixedHeight))
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, 100)
  }

  func test_intrinsicContentWidth_whenWidthTraitIsIntrinsicAndHeightIsIntrinsic_returnsImageWidth() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic)
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, defaultImageSize.width)
  }

  func test_intrinsicContentWidth_whenWidthTraitIsIntrinsicAndHeightIsWeighted_returnsImageWidth() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .intrinsic,
      height: .trait(.weighted(1))
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, defaultImageSize.width)
  }

  func test_intrinsicContentWidth_whenWidthTraitIsIntrinsicAndHeightIsRatio_returnsImageWidth() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .intrinsic,
      height: .ratio(2)
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, defaultImageSize.width)
  }

  func test_intrinsicContentWidth_whenWidthTraitIsIntrinsicWithConstraints_respectsMinWidth() {
    let smallImage = makeColoredImage(size: CGSize(width: 50, height: 100))
    let block = makeImageBlock(
      imageHolder: smallImage,
      widthTrait: .intrinsic(constrained: false, minSize: 100, maxSize: 300),
      height: .trait(.intrinsic)
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, 100)
  }

  func test_intrinsicContentWidth_whenWidthTraitIsIntrinsicWithConstraints_respectsMaxWidth() {
    let largeImage = makeColoredImage(size: CGSize(width: 400, height: 100))
    let block = makeImageBlock(
      imageHolder: largeImage,
      widthTrait: .intrinsic(constrained: false, minSize: 100, maxSize: 300),
      height: .trait(.intrinsic)
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, 300)
  }

  func test_intrinsicContentWidth_whenHeightIsFixedAndImageHasNoAspectRatio_returnsZero() {
    let emptyImage = UIImage()
    let block = makeImageBlock(
      imageHolder: emptyImage,
      widthTrait: .intrinsic,
      height: .trait(.fixed(100))
    )

    let width = block.intrinsicContentWidth

    XCTAssertEqual(width, 0)
  }

  func test_intrinsicContentHeight_whenHeightTraitIsFixed_returnsFixedValue() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .intrinsic,
      height: .trait(.fixed(100))
    )

    let height = block.intrinsicContentHeight(forWidth: 200)

    XCTAssertEqual(height, 100)
  }

  func test_intrinsicContentHeight_whenHeightTraitIsWeighted_returnsZero() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .intrinsic,
      height: .trait(.weighted(1))
    )

    let height = block.intrinsicContentHeight(forWidth: 200)

    XCTAssertEqual(height, 0)
  }

  func test_intrinsicContentHeight_whenHeightIsRatio_calculatesFromWidth() {
    let ratio: Double = 2
    let width: CGFloat = 200
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .intrinsic,
      height: .ratio(ratio)
    )

    let height = block.intrinsicContentHeight(forWidth: width)

    XCTAssertEqual(height, 100)
  }

  func test_intrinsicContentHeight_whenHeightIsIntrinsicAndWidthIsFixed_calculatesFromAspectRatio() {
    let fixedWidth: CGFloat = 100
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .fixed(fixedWidth),
      height: .trait(.intrinsic)
    )

    let height = block.intrinsicContentHeight(forWidth: fixedWidth)

    XCTAssertEqual(height, 50)
  }

  func test_intrinsicContentHeight_whenHeightIsIntrinsicAndWidthIsFixedButNoAspectRatio_returnsZero(
  ) {
    let emptyImage = UIImage()
    let block = makeImageBlock(
      imageHolder: emptyImage,
      widthTrait: .fixed(100),
      height: .trait(.intrinsic)
    )

    let height = block.intrinsicContentHeight(forWidth: 100)

    XCTAssertEqual(height, 0)
  }

  func test_intrinsicContentHeight_whenHeightIsIntrinsicAndWidthIsIntrinsic_returnsImageHeight() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic)
    )

    let height = block.intrinsicContentHeight(forWidth: 0)

    XCTAssertEqual(height, defaultImageSize.height)
  }

  func test_intrinsicContentHeight_whenHeightIsIntrinsicAndWidthIsWeighted_returnsImageHeight() {
    let block = makeImageBlock(
      imageHolder: defaultImage,
      widthTrait: .weighted(1),
      height: .trait(.intrinsic)
    )

    let height = block.intrinsicContentHeight(forWidth: 0)

    XCTAssertEqual(height, defaultImageSize.height)
  }

  func test_intrinsicContentHeight_whenHeightIsIntrinsicWithConstraints_respectsMinHeight() {
    let shortImage = makeColoredImage(size: CGSize(width: 200, height: 10))
    let block = makeImageBlock(
      imageHolder: shortImage,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic(constrained: false, minSize: 80, maxSize: 200))
    )

    let height = block.intrinsicContentHeight(forWidth: 0)

    XCTAssertEqual(height, 80)
  }

  func test_intrinsicContentHeight_whenHeightIsIntrinsicWithConstraints_respectsMaxHeight() {
    let tallImage = makeColoredImage(size: CGSize(width: 200, height: 250))
    let block = makeImageBlock(
      imageHolder: tallImage,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic(constrained: false, minSize: 80, maxSize: 200))
    )

    let height = block.intrinsicContentHeight(forWidth: 0)

    XCTAssertEqual(height, 200)
  }

  func test_imageInContainer_withConstrainedWrapContent_respectsContainerBounds() throws {
    let image = makeColoredImage(size: CGSize(width: 400, height: 300))
    let imageBlock = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic(constrained: true, minSize: 100, maxSize: 200),
      height: .trait(.intrinsic(constrained: true, minSize: 50, maxSize: 150)),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn
    )

    let container = try makeContainer(
      layoutDirection: .vertical,
      widthTrait: .fixed(300),
      heightTrait: .fixed(200),
      children: [imageBlock]
    )

    let containerWidth = container.widthOfHorizontallyNonResizableBlock
    let containerHeight = container.heightOfVerticallyNonResizableBlock(forWidth: 300)

    XCTAssertEqual(containerWidth, 300)
    XCTAssertEqual(containerHeight, 200)
  }

  func test_multipleConstrainedImages_inHorizontalContainer_shareSpaceCorrectly() throws {
    let image1 = makeColoredImage(size: CGSize(width: 300, height: 200))
    let image2 = makeColoredImage(size: CGSize(width: 200, height: 300))

    let imageBlock1 = ImageBlock(
      imageHolder: image1,
      widthTrait: .intrinsic(constrained: true, minSize: 50, maxSize: 150),
      height: .trait(.fixed(100)),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn
    )

    let imageBlock2 = ImageBlock(
      imageHolder: image2,
      widthTrait: .intrinsic(constrained: true, minSize: 50, maxSize: 150),
      height: .trait(.fixed(100)),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn
    )

    let container = try makeContainer(
      layoutDirection: .horizontal,
      widthTrait: .fixed(300),
      heightTrait: .fixed(100),
      children: [imageBlock1, imageBlock2],
      gap: 10
    )

    let containerWidth = container.widthOfHorizontallyNonResizableBlock
    let containerHeight = container.heightOfVerticallyNonResizableBlock(forWidth: 300)

    XCTAssertEqual(containerWidth, 300)
    XCTAssertEqual(containerHeight, 100)
  }

  func test_constrainedImage_withAspectRatio_maintainsRatioWithinBounds() throws {
    let wideImage = makeColoredImage(size: CGSize(width: 800, height: 200))

    let imageBlock = ImageBlock(
      imageHolder: wideImage,
      widthTrait: .intrinsic(constrained: true, minSize: 100, maxSize: 400),
      height: .trait(.intrinsic(constrained: true, minSize: 50, maxSize: 150)),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn
    )

    let container = try makeContainer(
      layoutDirection: .vertical,
      widthTrait: .fixed(500),
      heightTrait: .fixed(200),
      children: [imageBlock]
    )

    let containerWidth = container.widthOfHorizontallyNonResizableBlock
    let containerHeight = container.heightOfVerticallyNonResizableBlock(forWidth: 500)

    XCTAssertEqual(containerWidth, 500)
    XCTAssertEqual(containerHeight, 200)
  }

  func test_constrainedImage_inConstrainedContainer_properlyLimitsSize() throws {
    let squareImage = makeColoredImage(size: CGSize(width: 500, height: 500))

    let imageBlock = ImageBlock(
      imageHolder: squareImage,
      widthTrait: .intrinsic(constrained: true, minSize: 0, maxSize: .infinity),
      height: .trait(.intrinsic(constrained: true, minSize: 0, maxSize: .infinity)),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn
    )

    let container = try makeContainer(
      layoutDirection: .vertical,
      widthTrait: .fixed(200),
      heightTrait: .fixed(200),
      children: [imageBlock]
    )

    let containerWidth = container.widthOfHorizontallyNonResizableBlock
    let containerHeight = container.heightOfVerticallyNonResizableBlock(forWidth: 200)

    XCTAssertEqual(containerWidth, 200)
    XCTAssertEqual(containerHeight, 200)
  }

  func test_constrainedImages_inVerticalGallery_maintainConsistentSizes() throws {
    let images = [
      CGSize(width: 300, height: 400),
      CGSize(width: 400, height: 300),
      CGSize(width: 200, height: 200),
    ]

    let imageBlocks = images.map { size in
      ImageBlock(
        imageHolder: makeColoredImage(size: size),
        widthTrait: .intrinsic(constrained: true, minSize: 100, maxSize: 250),
        height: .trait(.intrinsic(constrained: true, minSize: 100, maxSize: 250)),
        contentMode: .default,
        tintColor: nil,
        tintMode: .sourceIn
      )
    }

    let container = try makeContainer(
      layoutDirection: .vertical,
      widthTrait: .fixed(300),
      heightTrait: .intrinsic,
      children: imageBlocks,
      gap: 10
    )

    let containerWidth = container.widthOfHorizontallyNonResizableBlock
    let containerHeight = container.heightOfVerticallyNonResizableBlock(forWidth: 300)

    XCTAssertEqual(containerWidth, 300)
    XCTAssertEqual(containerHeight, 657.5)
  }

  func test_constrainedImage_stateUpdate_maintainsConstraints() throws {
    let initialImage = makeColoredImage(size: CGSize(width: 200, height: 100))
    let path = UIElementPath("test_image")

    let block = ImageBlock(
      imageHolder: initialImage,
      widthTrait: .intrinsic(constrained: true, minSize: 100, maxSize: 300),
      height: .trait(.intrinsic(constrained: true, minSize: 50, maxSize: 200)),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn,
      path: path
    )

    let newImageSize = CGSize(width: 400, height: 200)
    let newState = ImageBaseBlockState(
      widthTrait: .intrinsic(constrained: true, minSize: 100, maxSize: 300),
      height: .trait(.intrinsic(constrained: true, minSize: 50, maxSize: 200)),
      imageHolder: makeColoredImage(size: newImageSize)
    )

    let states: BlocksState = [path: newState]
    let updatedBlock = try block.updated(withStates: states)

    let width = updatedBlock.intrinsicContentWidth
    let height = updatedBlock.intrinsicContentHeight(forWidth: 0)

    XCTAssertEqual(width, 300)
    XCTAssertEqual(height, 150)
  }

  func test_veryWideImage_withMinHeightConstraint_usesMinHeightNotMax() throws {
    let veryWideImage = makeColoredImage(size: CGSize(width: 7500, height: 2400))

    let imageBlock = ImageBlock(
      imageHolder: veryWideImage,
      widthTrait: .intrinsic(constrained: true, minSize: 100, maxSize: 300),
      height: .trait(.intrinsic(constrained: true, minSize: 100, maxSize: 268)),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn
    )

    let width = imageBlock.intrinsicContentWidth
    let height = imageBlock.intrinsicContentHeight(forWidth: width)

    XCTAssertEqual(width, 300, accuracy: 0.1)
    XCTAssertEqual(
      height,
      100,
      accuracy: 0.1,
      "Height should use min constraint (100), not max (268)"
    )
  }

  func test_veryWideImage_inContainer_respectsMinHeight() throws {
    let veryWideImage = makeColoredImage(size: CGSize(width: 2500, height: 800))

    let imageBlock = ImageBlock(
      imageHolder: veryWideImage,
      widthTrait: .intrinsic(constrained: true, minSize: 100, maxSize: 300),
      height: .trait(.intrinsic(constrained: true, minSize: 100, maxSize: 268)),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn
    )

    let container = try makeContainer(
      layoutDirection: .vertical,
      widthTrait: .fixed(402),
      heightTrait: .fixed(628),
      children: [imageBlock]
    )

    let containerWidth = container.widthOfHorizontallyNonResizableBlock
    let containerHeight = container.heightOfVerticallyNonResizableBlock(forWidth: 402)

    XCTAssertEqual(containerWidth, 402)
    XCTAssertEqual(containerHeight, 628)
  }
}

private let defaultImageSize = CGSize(width: 200, height: 100)
private let defaultImage = makeColoredImage(size: defaultImageSize)

private func makeImageBlock(
  imageHolder: ImageHolder,
  widthTrait: LayoutTrait,
  height: ImageBlockHeight
) -> ImageBlock {
  ImageBlock(
    imageHolder: imageHolder,
    widthTrait: widthTrait,
    height: height,
    contentMode: .default,
    tintColor: nil,
    tintMode: .sourceIn
  )
}

private func makeContainer(
  layoutDirection: ContainerBlock.LayoutDirection,
  widthTrait: LayoutTrait,
  heightTrait: LayoutTrait,
  children: [Block],
  gap: CGFloat = 0
) throws -> ContainerBlock {
  var gaps = [CGFloat(0)]
  if children.count > 1 {
    gaps += Array(repeating: gap, count: children.count - 1)
  }
  gaps.append(0)
  return try ContainerBlock(
    layoutDirection: layoutDirection,
    layoutMode: .wrap,
    widthTrait: widthTrait,
    heightTrait: heightTrait,
    gaps: gaps,
    children: children
  )
}

private func makeColoredImage(size: CGSize) -> Image {
  Image.imageOfSize(size, opaque: true, scale: 1) { ctx in
    ctx.setFillColor(UIColor.black.cgColor)
    ctx.fill(CGRect(origin: .zero, size: size))
  }!
}
