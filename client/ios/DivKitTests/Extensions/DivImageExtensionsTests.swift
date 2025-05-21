@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import VGSL
import XCTest

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
          contentMode: ImageContentMode(scale: .aspectFill),
          path: defaultPath
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
          contentMode: ImageContentMode(scale: .aspectFill),
          path: defaultPath
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

  func test_WhenWidthIsWrapContent_UsesIntrinsicContentSize() {
    let block = makeBlock(
      divImage(
        height: fixedSize(100),
        imageUrl: "https://image.url",
        width: wrapContentSize()
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: ImageBlock(
          imageHolder: FakeImageHolder(),
          widthTrait: .intrinsic,
          heightTrait: .fixed(100),
          contentMode: ImageContentMode(scale: .aspectFill),
          path: defaultPath
        ),
        accessibilityElement: accessibility(traits: .image)
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WhenHeightIsWrapContent_UsesIntrinsicContentSize() {
    let block = makeBlock(
      divImage(
        height: wrapContentSize(),
        imageUrl: "https://image.url",
        width: fixedSize(100)
      )
    )
    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: ImageBlock(
          imageHolder: FakeImageHolder(),
          widthTrait: .fixed(100),
          heightTrait: .intrinsic,
          contentMode: ImageContentMode(scale: .aspectFill),
          path: defaultPath
        ),
        accessibilityElement: accessibility(traits: .image)
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }
}

private let defaultPath = UIElementPath("test_card_id") + "0" + "image"
