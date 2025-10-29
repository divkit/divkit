@testable import LayoutKit
import VGSL
import XCTest

final class ImageBaseBlockStateUpdateTests: XCTestCase {
  func test_updatedWithStates_whenStateChanges_returnsNewBlockWithUpdatedState() {
    let initialImage = makeColoredImage(size: CGSize(width: 100, height: 50))
    let path = UIElementPath("test")
    let block = ImageBlock(
      imageHolder: initialImage,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn,
      path: path
    )

    let newImageSize = CGSize(width: 200, height: 100)
    let newState = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: makeColoredImage(size: newImageSize)
    )

    let states: BlocksState = [path: newState]
    let updatedBlock = try! block.updated(withStates: states)

    XCTAssertEqual(updatedBlock.state, newState)
    XCTAssertEqual(updatedBlock.state.intrinsicContentSize, newImageSize)
    XCTAssertFalse(updatedBlock === block, "Should return new block instance")
  }

  func test_updatedWithStates_whenStateDoesNotChange_returnsSameBlock() {
    let image = makeColoredImage(size: CGSize(width: 100, height: 50))
    let path = UIElementPath("test")
    let block = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn,
      path: path
    )

    let states: BlocksState = [path: block.state]
    let updatedBlock = try! block.updated(withStates: states)

    XCTAssertTrue(updatedBlock === block, "Should return same block instance when state unchanged")
  }

  func test_updatedWithStates_whenNoStateInStates_returnsSameBlock() {
    let image = makeColoredImage(size: CGSize(width: 100, height: 50))
    let path = UIElementPath("test")
    let block = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn,
      path: path
    )

    let states: BlocksState = [:]
    let updatedBlock = try! block.updated(withStates: states)

    XCTAssertTrue(updatedBlock === block, "Should return same block when no state in BlocksState")
  }

  func test_updatedWithStates_whenNoPath_returnsSameBlock() {
    let image = makeColoredImage(size: CGSize(width: 100, height: 50))
    let block = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn,
      path: nil
    )

    let newState = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: makeColoredImage(size: CGSize(width: 200, height: 100))
    )
    let states: BlocksState = [UIElementPath("test"): newState]
    let updatedBlock = try! block.updated(withStates: states)

    XCTAssertTrue(updatedBlock === block, "Should return same block when block has no path")
  }

  func test_makeCopyWithState_preservesAllProperties() {
    let image = makeColoredImage(size: CGSize(width: 100, height: 50))
    let animation = TransitioningAnimation(
      kind: .fade,
      start: 0,
      end: 1,
      duration: 0.3,
      delay: 0,
      timingFunction: .linear
    )
    let path = UIElementPath("test")

    let block = ImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic,
      height: .trait(.fixed(100)),
      contentMode: ImageContentMode(scale: .aspectFill),
      tintColor: .red,
      tintMode: .sourceIn,
      effects: [.blur(radius: 5)],
      filter: nil,
      accessibilityElement: AccessibilityElement(
        traits: .image,
        strings: .init(label: "Test")
      ),
      appearanceAnimation: animation,
      blurUsingMetal: true,
      tintUsingMetal: false,
      path: path
    )

    let newState = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.fixed(100)),
      imageHolder: makeColoredImage(size: CGSize(width: 200, height: 100))
    )

    let copy = block.makeCopy(withState: newState)

    XCTAssertEqual(copy.state, newState, "State should be updated")
    XCTAssertTrue(compare(copy.imageHolder, block.imageHolder), "ImageHolder should be preserved")
    XCTAssertEqual(copy.widthTrait, block.widthTrait, "WidthTrait should be preserved")
    XCTAssertEqual(copy.height, block.height, "Height should be preserved")
    XCTAssertEqual(copy.contentMode, block.contentMode, "ContentMode should be preserved")
    XCTAssertEqual(copy.tintColor, block.tintColor, "TintColor should be preserved")
    XCTAssertEqual(copy.tintMode, block.tintMode, "TintMode should be preserved")
    XCTAssertEqual(copy.effects.count, block.effects.count, "Effects should be preserved")
    XCTAssertEqual(copy.filter, block.filter, "Filter should be preserved")
    XCTAssertEqual(
      copy.accessibilityElement,
      block.accessibilityElement,
      "AccessibilityElement should be preserved"
    )
    XCTAssertEqual(
      copy.appearanceAnimation,
      block.appearanceAnimation,
      "AppearanceAnimation should be preserved"
    )
    XCTAssertEqual(copy.blurUsingMetal, block.blurUsingMetal, "BlurUsingMetal should be preserved")
    XCTAssertEqual(copy.tintUsingMetal, block.tintUsingMetal, "TintUsingMetal should be preserved")
    XCTAssertEqual(copy.path, block.path, "Path should be preserved")
  }

  func test_animatableImageBlock_updatedWithStates_preservesAllProperties() {
    let image = makeColoredImage(size: CGSize(width: 100, height: 50))
    let path = UIElementPath("test")

    let block = AnimatableImageBlock(
      imageHolder: image,
      widthTrait: .intrinsic,
      height: .trait(.fixed(100)),
      contentMode: ImageContentMode(scale: .aspectFill),
      accessibilityElement: AccessibilityElement(
        traits: .image,
        strings: .init(label: "Test")
      ),
      path: path
    )

    let newState = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.fixed(100)),
      imageHolder: makeColoredImage(size: CGSize(width: 200, height: 100))
    )

    let copy = block.makeCopy(withState: newState)

    XCTAssertEqual(copy.state, newState, "State should be updated")
    XCTAssertTrue(compare(copy.imageHolder, block.imageHolder), "ImageHolder should be preserved")
    XCTAssertEqual(copy.widthTrait, block.widthTrait, "WidthTrait should be preserved")
    XCTAssertEqual(copy.height, block.height, "Height should be preserved")
    XCTAssertEqual(copy.contentMode, block.contentMode, "ContentMode should be preserved")
    XCTAssertEqual(
      copy.accessibilityElement,
      block.accessibilityElement,
      "AccessibilityElement should be preserved"
    )
    XCTAssertEqual(copy.path, block.path, "Path should be preserved")
  }

  func test_stateNotRecalculatedDuringUpdate() {
    let initialImage = makeColoredImage(size: CGSize(width: 100, height: 50))
    let path = UIElementPath("test")
    let block = ImageBlock(
      imageHolder: initialImage,
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      contentMode: .default,
      tintColor: nil,
      tintMode: .sourceIn,
      path: path
    )

    let differentImageSize = CGSize(width: 300, height: 150)
    let providedState = ImageBaseBlockState(
      widthTrait: .intrinsic,
      height: .trait(.intrinsic),
      imageHolder: makeColoredImage(size: differentImageSize)
    )

    let states: BlocksState = [path: providedState]
    let updatedBlock = try! block.updated(withStates: states)

    XCTAssertEqual(
      updatedBlock.state.intrinsicContentSize,
      differentImageSize,
      "State should use provided size, not recalculate from imageHolder"
    )
    XCTAssertNotEqual(
      updatedBlock.state.intrinsicContentSize,
      initialImage.size,
      "State should not be recalculated from block's imageHolder"
    )
  }
}

private func makeColoredImage(size: CGSize) -> Image {
  Image.imageOfSize(size, opaque: true, scale: 1) { ctx in
    ctx.setFillColor(UIColor.black.cgColor)
    ctx.fill(CGRect(origin: .zero, size: size))
  }!
}
