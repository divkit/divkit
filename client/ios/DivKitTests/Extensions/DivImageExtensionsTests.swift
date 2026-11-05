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
        accessibilityElement: accessibility(traits: .image),
        path: defaultPath
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
        ),
        path: defaultPath
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
        accessibilityElement: accessibility(traits: .image),
        path: defaultPath
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
        accessibilityElement: accessibility(traits: .image),
        path: defaultPath
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithoutImageUrl_IsShown() throws {
    try assertEqual(makeBlock(parsedImage(nil)), shownBlock)
  }

  func test_WithNullImageUrl_IsShown() throws {
    try assertEqual(makeBlock(parsedImage(NSNull())), shownBlock)
  }

  func test_WithEmptySchemeImageUrl_IsShown() throws {
    try assertEqual(makeBlock(parsedImage("empty://")), shownBlock)
  }

  func test_WhenHideImagesWithoutUrl_WithoutImageUrl_IsGone() throws {
    try assertEqual(
      makeBlock(parsedImage(nil, json: marginsAndDisappearActions), context: hidingContext),
      goneBlock
    )
  }

  func test_WhenHideImagesWithoutUrl_WithNullImageUrl_IsGone() throws {
    try assertEqual(
      makeBlock(
        parsedImage(NSNull(), json: marginsAndDisappearActions),
        context: hidingContext
      ),
      goneBlock
    )
  }

  func test_WhenHideImagesWithoutUrl_WithEmptyStringImageUrl_IsGone() throws {
    try assertEqual(
      makeBlock(parsedImage("", json: marginsAndDisappearActions), context: hidingContext),
      goneBlock
    )
  }

  func test_WhenHideImagesWithoutUrl_WithEmptySchemeImageUrl_IsShown() throws {
    try assertEqual(makeBlock(parsedImage("empty://"), context: hidingContext), shownBlock)
  }

  func test_WhenHideImagesWithoutUrl_WithEmptyExpressionImageUrl_IsShown() throws {
    try assertEqual(
      makeBlock(parsedImage("@{''}"), context: hidingContext, ignoreErrors: true),
      shownBlock
    )
  }

  func test_WhenHideImagesWithoutUrl_WithoutImageUrl_ResetsLastVisibleArea() throws {
    let context = hidingContext
    context.lastVisibleBoundsCache.updateLastVisibleArea(for: defaultPath, area: 100)
    _ = try makeBlock(parsedImage(nil), context: context)
    XCTAssertEqual(context.lastVisibleBoundsCache.lastVisibleArea(for: defaultPath), 0)
  }
}

private let defaultPath = UIElementPath("test_card_id") + "0" + "image"

private var shownBlock: StateBlock {
  StateBlock(
    child: DecoratingBlock(
      child: ImageBlock(
        imageHolder: FakeImageHolder(),
        widthTrait: .fixed(100),
        heightTrait: .fixed(200),
        contentMode: ImageContentMode(scale: .aspectFill),
        path: defaultPath
      ),
      accessibilityElement: accessibility(traits: .image),
      path: defaultPath
    ),
    ids: []
  )
}

private var goneBlock: StateBlock {
  StateBlock(child: EmptyBlock.zeroSized, ids: [])
}

private var hidingContext: DivBlockModelingContext {
  DivBlockModelingContext(flagsInfo: DivFlagsInfo(hideImagesWithoutUrl: true))
}

private var marginsAndDisappearActions: [String: Any] {
  [
    "margins": ["left": 8],
    "disappear_actions": [["log_id": "disappear"]],
  ]
}

private func parsedImage(_ url: Any?, json: [String: Any] = [:]) throws -> Div {
  var json = json
  json["width"] = ["type": "fixed", "value": 100]
  json["height"] = ["type": "fixed", "value": 200]
  json["image_url"] = url
  let result = DivTemplates(dictionary: [:]).parseValue(type: DivImage.self, from: json)
  return try .divImage(XCTUnwrap(result.value))
}
