import DivKitTestsSupport
@testable import LayoutKit
import VGSL
import XCTest

final class ImageBaseBlockStateTests: XCTestCase {
  // MARK: - hasIntrinsicSize Detection Tests - Width Intrinsic

  func test_hasIntrinsicSize_whenWidthIsIntrinsicStatic_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.fixed(200)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenWidthIsIntrinsicWithConstrainedFalse_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: .infinity),
      height: .trait(.fixed(200)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenWidthIsIntrinsicWithConstrainedTrue_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic(constrained: true, minSize: 50, maxSize: 200),
      height: .trait(.fixed(200)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenWidthIsIntrinsicWithMinSize_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic(constrained: false, minSize: 10, maxSize: .infinity),
      height: .trait(.fixed(200)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenWidthIsIntrinsicWithMaxSize_returnsTrue() {
    let imageSize = CGSize(width: 300, height: 400)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: 328),
      height: .trait(.fixed(100)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenWidthIsIntrinsicWithMinAndMaxSize_returnsTrue() {
    let imageSize = CGSize(width: 300, height: 400)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic(constrained: true, minSize: 50, maxSize: 328),
      height: .trait(.fixed(100)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  // MARK: - hasIntrinsicSize Detection Tests - Height Intrinsic

  func test_hasIntrinsicSize_whenHeightIsIntrinsicStatic_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .fixed(200),
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenHeightIsIntrinsicWithConstrainedFalse_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .fixed(200),
      height: .trait(.intrinsic(constrained: false, minSize: 0, maxSize: .infinity)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenHeightIsIntrinsicWithConstrainedTrue_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .fixed(200),
      height: .trait(.intrinsic(constrained: true, minSize: 50, maxSize: 200)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenHeightIsIntrinsicWithMinSize_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .fixed(100),
      height: .trait(.intrinsic(constrained: false, minSize: 10, maxSize: .infinity)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenHeightIsIntrinsicWithMaxSize_returnsTrue() {
    let imageSize = CGSize(width: 300, height: 400)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .fixed(100),
      height: .trait(.intrinsic(constrained: false, minSize: 0, maxSize: 328)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenHeightIsIntrinsicWithMinAndMaxSize_returnsTrue() {
    let imageSize = CGSize(width: 300, height: 400)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .fixed(100),
      height: .trait(.intrinsic(constrained: true, minSize: 50, maxSize: 328)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  // MARK: - hasIntrinsicSize Detection Tests - Both Intrinsic

  func test_hasIntrinsicSize_whenBothAreIntrinsicStatic_returnsTrue() {
    let imageSize = CGSize(width: 100, height: 100)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenBothAreIntrinsicWithConstraints_returnsTrue() {
    let imageSize = CGSize(width: 300, height: 400)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic(constrained: false, minSize: 0, maxSize: 328),
      height: .trait(.intrinsic(constrained: false, minSize: 0, maxSize: 328)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_hasIntrinsicSize_whenBothAreIntrinsicWithDifferentConstraints_returnsTrue() {
    let imageSize = CGSize(width: 300, height: 400)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic(constrained: true, minSize: 100, maxSize: 500),
      height: .trait(.intrinsic(constrained: false, minSize: 0, maxSize: 328)),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  // MARK: - hasIntrinsicSize Detection Tests - Non-Intrinsic Cases

  func test_hasIntrinsicSize_whenWidthIsFixedAndHeightIsFixed_returnsFalse() {
    let imageHolder = makeColoredImage(size: CGSize(width: 100, height: 100))
    let state = ImageBaseBlockState(
      widthTrait: .fixed(200),
      height: .trait(.fixed(200)),
      imageHolder: imageHolder
    )

    XCTAssertNil(
      state.intrinsicContentSize,
      "intrinsicContentSize should be nil when both dimensions are fixed"
    )
  }

  func test_hasIntrinsicSize_whenWidthIsWeightedAndHeightIsFixed_returnsFalse() {
    let imageHolder = makeColoredImage(size: CGSize(width: 100, height: 100))
    let state = ImageBaseBlockState(
      widthTrait: .weighted(1),
      height: .trait(.fixed(200)),
      imageHolder: imageHolder
    )

    XCTAssertNil(
      state.intrinsicContentSize,
      "intrinsicContentSize should be nil when width is weighted"
    )
  }

  func test_hasIntrinsicSize_whenWidthIsFixedAndHeightIsWeighted_returnsFalse() {
    let imageHolder = makeColoredImage(size: CGSize(width: 100, height: 100))
    let state = ImageBaseBlockState(
      widthTrait: .fixed(200),
      height: .trait(.weighted(1)),
      imageHolder: imageHolder
    )

    XCTAssertNil(
      state.intrinsicContentSize,
      "intrinsicContentSize should be nil when height is weighted"
    )
  }

  func test_hasIntrinsicSize_whenWidthIsFixedAndHeightIsRatio_returnsFalse() {
    let imageHolder = makeColoredImage(size: CGSize(width: 100, height: 100))
    let state = ImageBaseBlockState(
      widthTrait: .fixed(200),
      height: .ratio(2),
      imageHolder: imageHolder
    )

    XCTAssertNil(
      state.intrinsicContentSize,
      "intrinsicContentSize should be nil when height is ratio"
    )
  }

  func test_hasIntrinsicSize_whenWidthIsWeightedAndHeightIsWeighted_returnsFalse() {
    let imageHolder = makeColoredImage(size: CGSize(width: 100, height: 100))
    let state = ImageBaseBlockState(
      widthTrait: .weighted(1),
      height: .trait(.weighted(1)),
      imageHolder: imageHolder
    )

    XCTAssertNil(
      state.intrinsicContentSize,
      "intrinsicContentSize should be nil when both are weighted"
    )
  }

  // MARK: - Image Size Tests

  func test_intrinsicContentSize_usesImageHolderSize() {
    let imageSize = CGSize(width: 300, height: 400)
    let imageHolder = makeColoredImage(size: imageSize)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, imageSize)
  }

  func test_intrinsicContentSize_withPlaceholder_usesPlaceholderSize() {
    let placeholderSize = CGSize(width: 50, height: 50)
    let placeholder = makeColoredImage(size: placeholderSize)
    let imageHolder = FakeImageHolder(image: nil, placeholder: .image(placeholder))

    let state = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, placeholderSize)
  }

  func test_intrinsicContentSize_withLoadedImage_usesLoadedImageSize() {
    let placeholderSize = CGSize(width: 50, height: 50)
    let imageSize = CGSize(width: 300, height: 400)
    let placeholder = makeColoredImage(size: placeholderSize)
    let image = makeColoredImage(size: imageSize)
    let imageHolder = FakeImageHolder(image: image, placeholder: .image(placeholder))

    let state = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )

    XCTAssertEqual(
      state.intrinsicContentSize,
      imageSize,
      "Should use loaded image size, not placeholder"
    )
  }

  // MARK: - State Equality Tests

  func test_equality_whenIntrinsicContentSizesAreEqual_returnsTrue() {
    let imageHolder = makeColoredImage(size: CGSize(width: 100, height: 100))
    let state1 = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )
    let state2 = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state1, state2)
  }

  func test_equality_whenIntrinsicContentSizesAreDifferent_returnsFalse() {
    let imageHolder1 = makeColoredImage(size: CGSize(width: 100, height: 100))
    let imageHolder2 = makeColoredImage(size: CGSize(width: 200, height: 200))
    let state1 = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder1
    )
    let state2 = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder2
    )

    XCTAssertNotEqual(state1, state2)
  }

  func test_equality_whenOneHasIntrinsicSizeAndOtherDoesNot_returnsFalse() {
    let imageHolder = makeColoredImage(size: CGSize(width: 100, height: 100))
    let state1 = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )
    let state2 = ImageBaseBlockState(
      widthTrait: .fixed(100),
      height: .trait(.fixed(100)),
      imageHolder: imageHolder
    )

    XCTAssertNotEqual(state1, state2)
  }

  // MARK: - Edge Cases

  func test_hasIntrinsicSize_withZeroSizeImage_returnsTrue() {
    let imageHolder = makeColoredImage(size: .zero)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, .zero)
  }

  func test_hasIntrinsicSize_withColorPlaceholder_returnsTrue() {
    let imageHolder = ColorHolder(color: .red)
    let state = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: imageHolder
    )

    XCTAssertEqual(state.intrinsicContentSize, .zero, "Color placeholder returns zero size")
  }
}

// MARK: - Helper Functions

private func makeColoredImage(size: CGSize) -> Image {
  Image.imageOfSize(size, opaque: true, scale: 1) { ctx in
    ctx.setFillColor(UIColor.black.cgColor)
    ctx.fill(CGRect(origin: .zero, size: size))
  } ?? Image()
}
