@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import VGSL
import XCTest

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
      divGifImage(
        gifUrl: "https://image.url",
        height: fixedSize(100),
        width: wrapContentSize()
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: AnimatableImageBlock(
          imageHolder: FakeImageHolder(),
          widthTrait: .intrinsic,
          height: .trait(.fixed(100)),
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
      divGifImage(
        gifUrl: "https://image.url",
        height: wrapContentSize(),
        width: fixedSize(100)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: AnimatableImageBlock(
          imageHolder: FakeImageHolder(),
          widthTrait: .fixed(100),
          height: .trait(.intrinsic),
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

private let defaultPath = UIElementPath("test_card_id") + "0" + "gif"
