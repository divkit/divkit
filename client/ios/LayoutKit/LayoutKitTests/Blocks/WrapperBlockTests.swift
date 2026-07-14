import LayoutKit
import VGSL
import XCTest

final class WrapperBlockTests: XCTestCase {
  func test_WhenUpdatesState_SendsStatesToChildren() throws {
    let state = GalleryViewState(contentOffset: 1, itemsCount: 2)
    let states = [GalleryBlockTestModels.path: state]
    let block = wrapperBlock

    let updatedBlock = try block.updated(withStates: states)
    let updatedGalleryBlock = updatedBlock.child as? GalleryBlock
    XCTAssertEqual(updatedGalleryBlock?.state, state)
  }

  func test_DecoratingBlock_addsPaddingsToMinMaxBounds() {
    let child = TextBlock(
      widthTrait: .weighted(.default, minSize: -16, maxSize: 134),
      heightTrait: .fixed(50),
      text: NSAttributedString(string: "x"),
      accessibilityElement: nil
    )
    let decorated = child.addingEdgeInsets(
      EdgeInsets(top: 0, left: 8, bottom: 0, right: 8)
    )

    XCTAssertEqual(decorated.minWidth, 0)
    XCTAssertEqual(decorated.maxWidth, 150)
    XCTAssertEqual(
      decorated.size(forResizableBlockSize: CGSize(width: 360, height: 100)).width,
      150
    )
  }

  func test_whenChildHasReuseId_hasSameReuseId() throws {
    let reuseId = "testReuseId"
    let childBlock = GalleryBlockTestModels.base.addingDecorations(
      reuseId: reuseId
    )

    let wrapperBlock: WrapperBlock = BackgroundBlock(
      background: .solidColor(.clear),
      child: childBlock
    )

    XCTAssertEqual(wrapperBlock.reuseId, reuseId)
  }
}

private let wrapperBlock: WrapperBlock = BackgroundBlock(
  background: .solidColor(.clear),
  child: GalleryBlockTestModels.base
)
