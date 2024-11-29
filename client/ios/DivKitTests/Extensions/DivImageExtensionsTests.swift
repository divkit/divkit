@testable import DivKit
@testable import LayoutKit

import XCTest

import DivKitTestsSupport
import VGSL

final class DivImageExtensionsTests: XCTestCase {
  func test_WithImageUrl() {
    let block = makeBlock(
      divImage(
        height: fixedSize(200),
        imageUrl: "https://image.url",
        width: fixedSize(100)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: ImageBlock(
          imageHolder: FakeImageHolder(),
          widthTrait: .fixed(100),
          heightTrait: .fixed(200),
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
      divImage(
        accessibility: DivAccessibility(description: .value("Description")),
        height: fixedSize(200),
        imageUrl: "https://image.url",
        width: fixedSize(100)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: ImageBlock(
          imageHolder: FakeImageHolder(),
          widthTrait: .fixed(100),
          heightTrait: .fixed(200),
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
      try divData(divImage(
        imageUrl: "https://image.url",
        width: wrapContentSize()
      )).makeBlock(context: .default),
      DivBlockModelingError(
        "DivImage has wrap_content width",
        path: .root + "0"
      )
    )
  }

  func test_WhenHeightIsWrapContent_ThrowsError() throws {
    XCTAssertThrowsError(
      try divData(divImage(
        height: wrapContentSize(),
        imageUrl: "https://image.url"
      )).makeBlock(context: .default),
      DivBlockModelingError(
        "DivImage without aspect has wrap_content height",
        path: .root + "0"
      )
    )
  }
}
