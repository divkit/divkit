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

private func makeColoredImage(size: CGSize) -> Image {
  Image.imageOfSize(size, opaque: true, scale: 1) { ctx in
    ctx.setFillColor(UIColor.black.cgColor)
    ctx.fill(CGRect(origin: .zero, size: size))
  }!
}
