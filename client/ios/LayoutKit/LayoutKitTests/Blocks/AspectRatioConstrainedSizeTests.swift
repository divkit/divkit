@testable import LayoutKit
import XCTest

final class AspectRatioConstrainedSizeTests: XCTestCase {
  typealias Constraints = (min: CGFloat, max: CGFloat)

  func test_WhenImageFitsWithinWidthConstraints_ConstrainsByWidth() {
    let imageSize = CGSize(width: 300, height: 400)
    let widthConstraints: Constraints = (min: 0.0, max: 328.0)
    let heightConstraints: Constraints = (min: 0.0, max: 500.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 300, accuracy: 0.01)
    XCTAssertEqual(result.height, 400, accuracy: 0.01)
  }

  func test_WhenImageExceedsWidthConstraints_ClampsByWidth() {
    let imageSize = CGSize(width: 400, height: 300)
    let widthConstraints: Constraints = (min: 0.0, max: 328.0)
    let heightConstraints: Constraints = (min: 0.0, max: 500.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 328, accuracy: 0.01)
    XCTAssertEqual(result.height, 246, accuracy: 0.01)
  }

  func test_WhenHeightExceedsConstraints_ConstrainsByHeight() {
    let imageSize = CGSize(width: 300, height: 400)
    let widthConstraints: Constraints = (min: 0.0, max: 328.0)
    let heightConstraints: Constraints = (min: 0.0, max: 328.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 246, accuracy: 0.01)
    XCTAssertEqual(result.height, 328, accuracy: 0.01)
  }

  func test_WhenBothDimensionsExceedConstraints_ConstrainsByHeight() {
    let imageSize = CGSize(width: 500, height: 600)
    let widthConstraints: Constraints = (min: 0.0, max: 328.0)
    let heightConstraints: Constraints = (min: 0.0, max: 328.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 273.33, accuracy: 0.01)
    XCTAssertEqual(result.height, 328, accuracy: 0.01)
  }

  func test_WhenImageIsSmallerThanMinConstraints_ClampsToMin() {
    let imageSize = CGSize(width: 50, height: 50)
    let widthConstraints: Constraints = (min: 100.0, max: 328.0)
    let heightConstraints: Constraints = (min: 100.0, max: 328.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 100, accuracy: 0.01)
    XCTAssertEqual(result.height, 100, accuracy: 0.01)
  }

  func test_WhenAspectRatioIsWide_PreservesRatio() {
    let imageSize = CGSize(width: 800, height: 200)
    let widthConstraints: Constraints = (min: 0.0, max: 400.0)
    let heightConstraints: Constraints = (min: 0.0, max: 400.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 400, accuracy: 0.01)
    XCTAssertEqual(result.height, 100, accuracy: 0.01)
  }

  func test_WhenAspectRatioIsTall_PreservesRatio() {
    let imageSize = CGSize(width: 200, height: 800)
    let widthConstraints: Constraints = (min: 0.0, max: 400.0)
    let heightConstraints: Constraints = (min: 0.0, max: 400.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 100, accuracy: 0.01)
    XCTAssertEqual(result.height, 400, accuracy: 0.01)
  }

  func test_WhenImageIsSquare_PreservesSquareRatio() {
    let imageSize = CGSize(width: 500, height: 500)
    let widthConstraints: Constraints = (min: 0.0, max: 300.0)
    let heightConstraints: Constraints = (min: 0.0, max: 300.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 300, accuracy: 0.01)
    XCTAssertEqual(result.height, 300, accuracy: 0.01)
  }

  func test_RealWorldCase_300x400ImageWith328MaxConstraints() {
    let imageSize = CGSize(width: 300, height: 400)
    let widthConstraints: Constraints = (min: 0.0, max: 328.0)
    let heightConstraints: Constraints = (min: 0.0, max: 328.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 246, accuracy: 0.01)
    XCTAssertEqual(result.height, 328, accuracy: 0.01)
  }

  func test_WhenAspectRatioIsZero_ReturnsMinHeight() {
    let imageSize = CGSize(width: 300, height: 0)
    let widthConstraints: Constraints = (min: 0.0, max: 328.0)
    let heightConstraints: Constraints = (min: 0.0, max: 500.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 0, accuracy: 0.01)
    XCTAssertEqual(result.height, 0, accuracy: 0.01)
  }

  func test_WhenWidthIsZeroAndHeightIsNonZero_ReturnsMinConstraints() {
    let imageSize = CGSize(width: 0, height: 400)
    let widthConstraints: Constraints = (min: 0.0, max: 328.0)
    let heightConstraints: Constraints = (min: 0.0, max: 500.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 0, accuracy: 0.01)
    XCTAssertEqual(result.height, 0, accuracy: 0.01)
  }

  func test_WhenBothDimensionsAreZero_ReturnsZeroSize() {
    let imageSize = CGSize(width: 0, height: 0)
    let widthConstraints: Constraints = (min: 0.0, max: 328.0)
    let heightConstraints: Constraints = (min: 0.0, max: 500.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 0, accuracy: 0.01)
    XCTAssertEqual(result.height, 0, accuracy: 0.01)
  }

  func test_WhenAspectRatioIsZeroWithMinConstraints_RespectsMinConstraints() {
    let imageSize = CGSize(width: 300, height: 0)
    let widthConstraints: Constraints = (min: 100.0, max: 328.0)
    let heightConstraints: Constraints = (min: 50.0, max: 500.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 100, accuracy: 0.01)
    XCTAssertEqual(result.height, 50, accuracy: 0.01)
  }

  func test_VeryWideImage_WithMinHeightConstraint_UsesMinHeightNotMax() {
    let imageSize = CGSize(width: 7500, height: 2400)
    let widthConstraints: Constraints = (min: 100.0, max: 300.0)
    let heightConstraints: Constraints = (min: 100.0, max: 268.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 312.5, accuracy: 0.01)
    XCTAssertEqual(result.height, 100, accuracy: 0.01)
  }

  func test_WideImage_HeightBelowMin_UsesMinHeight() {
    let imageSize = CGSize(width: 1000, height: 200)
    let widthConstraints: Constraints = (min: 50.0, max: 400.0)
    let heightConstraints: Constraints = (min: 100.0, max: 300.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 500, accuracy: 0.01)
    XCTAssertEqual(result.height, 100, accuracy: 0.01)
  }

  func test_WideImage_HeightAboveMax_UsesMaxHeight() {
    let imageSize = CGSize(width: 1000, height: 800)
    let widthConstraints: Constraints = (min: 50.0, max: 400.0)
    let heightConstraints: Constraints = (min: 100.0, max: 300.0)

    let result = AspectRatioConstrainedSize.calculate(
      imageSize: imageSize,
      widthConstraints: widthConstraints,
      heightConstraints: heightConstraints
    )

    XCTAssertEqual(result.width, 375, accuracy: 0.01)
    XCTAssertEqual(result.height, 300, accuracy: 0.01)
  }
}
