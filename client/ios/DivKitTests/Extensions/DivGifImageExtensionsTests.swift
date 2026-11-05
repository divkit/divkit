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
        accessibilityElement: accessibility(traits: .image),
        path: defaultPath
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
        ),
        path: defaultPath
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithPreviewUrl() {
    let block = makeBlock(
      divGifImage(
        gifUrl: "https://image.url",
        height: fixedSize(200),
        previewUrl: "https://preview.url",
        width: fixedSize(100)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: AnimatableImageBlock(
          imageHolder: ImageWithPreviewHolder(
            mainHolder: FakeImageHolder(),
            previewHolder: FakeImageHolder()
          ),
          widthTrait: .fixed(100),
          height: .trait(.fixed(200)),
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
        accessibilityElement: accessibility(traits: .image),
        path: defaultPath
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
        accessibilityElement: accessibility(traits: .image),
        path: defaultPath
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithoutGifUrl_IsShown() throws {
    try assertEqual(makeBlock(parsedGif(nil)), shownBlock)
  }

  func test_WithNullGifUrl_IsShown() throws {
    try assertEqual(makeBlock(parsedGif(NSNull())), shownBlock)
  }

  func test_WithEmptySchemeGifUrl_IsShown() throws {
    try assertEqual(makeBlock(parsedGif("empty://")), shownBlock)
  }

  func test_WhenHideImagesWithoutUrl_WithoutGifUrl_IsGone() throws {
    try assertEqual(
      makeBlock(parsedGif(nil, json: marginsAndDisappearActions), context: hidingContext),
      goneBlock
    )
  }

  func test_WhenHideImagesWithoutUrl_WithNullGifUrl_IsGone() throws {
    try assertEqual(
      makeBlock(parsedGif(NSNull(), json: marginsAndDisappearActions), context: hidingContext),
      goneBlock
    )
  }

  func test_WhenHideImagesWithoutUrl_WithEmptyStringGifUrl_IsGone() throws {
    try assertEqual(
      makeBlock(parsedGif("", json: marginsAndDisappearActions), context: hidingContext),
      goneBlock
    )
  }

  func test_WhenHideImagesWithoutUrl_WithEmptySchemeGifUrl_IsShown() throws {
    try assertEqual(makeBlock(parsedGif("empty://"), context: hidingContext), shownBlock)
  }

  func test_WhenHideImagesWithoutUrl_WithEmptyExpressionGifUrl_IsShown() throws {
    try assertEqual(
      makeBlock(parsedGif("@{''}"), context: hidingContext, ignoreErrors: true),
      shownBlock
    )
  }

  func test_WhenHideImagesWithoutUrl_WithoutGifUrl_ResetsLastVisibleArea() throws {
    let context = hidingContext
    context.lastVisibleBoundsCache.updateLastVisibleArea(for: defaultPath, area: 100)
    _ = try makeBlock(parsedGif(nil), context: context)
    XCTAssertEqual(context.lastVisibleBoundsCache.lastVisibleArea(for: defaultPath), 0)
  }
}

private let defaultPath = UIElementPath("test_card_id") + "0" + "gif"

private var shownBlock: StateBlock {
  StateBlock(
    child: DecoratingBlock(
      child: AnimatableImageBlock(
        imageHolder: FakeImageHolder(),
        widthTrait: .fixed(100),
        height: .trait(.fixed(200)),
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

private func parsedGif(_ url: Any?, json: [String: Any] = [:]) throws -> Div {
  var json = json
  json["width"] = ["type": "fixed", "value": 100]
  json["height"] = ["type": "fixed", "value": 200]
  json["gif_url"] = url
  let result = DivTemplates(dictionary: [:]).parseValue(type: DivGifImage.self, from: json)
  return try .divGifImage(XCTUnwrap(result.value))
}
