@testable import DivKit
@testable import LayoutKit

import XCTest

import DivKitTestsSupport
import VGSL

final class DivGifImageExtensionsTests: XCTestCase {
  func test_WithGifUrl() {
    let block = makeBlock(
      divGifImage(
        gifUrl: "https://image.url",
        height: fixedSize(200),
        width: fixedSize(100)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: AnimatableImageBlock(
          imageHolder: FakeImageHolder(),
          widthTrait: .fixed(100),
          height: .trait(.fixed(200)),
          contentMode: ImageContentMode(scale: .aspectFill)
        ),
        accessibilityElement: accessibility(traits: .image)
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAccessibilityAuto() {
    let block = makeBlock(
      divGifImage(
        accessibility: DivAccessibility(description: .value("Description")),
        gifUrl: "https://image.url",
        height: fixedSize(200),
        width: fixedSize(100)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: AnimatableImageBlock(
          imageHolder: FakeImageHolder(),
          widthTrait: .fixed(100),
          height: .trait(.fixed(200)),
          contentMode: ImageContentMode(scale: .aspectFill)
        ),
        accessibilityElement: accessibility(
          traits: .image,
          label: "Description"
        )
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WhenWidthIsWrapContent_ThrowsError() {
    XCTAssertThrowsError(
      try divData(divGifImage(
        gifUrl: "https://image.url",
        width: wrapContentSize()
      )).makeBlock(context: .default),
      DivBlockModelingError(
        "DivGifImage has wrap_content width",
        path: .root + "0"
      )
    )
  }

  func test_WhenHeightIsWrapContent_ThrowsError() throws {
    XCTAssertThrowsError(
      try divData(divGifImage(
        gifUrl: "https://image.url",
        height: wrapContentSize()
      )).makeBlock(context: .default),
      DivBlockModelingError(
        "DivGifImage without aspect has wrap_content height",
        path: .root + "0"
      )
    )
  }
}
