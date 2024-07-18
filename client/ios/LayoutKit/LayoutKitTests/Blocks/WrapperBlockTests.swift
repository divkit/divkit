import XCTest

import LayoutKit
import VGSL

final class WrapperBlockTests: XCTestCase {
  func test_WhenUpdatesState_SendsStatesToChildren() throws {
    let state = GalleryViewState(contentOffset: 1, itemsCount: 2)
    let states = [GalleryBlockTestModels.path: state]
    let block = wrapperBlock

    let updatedBlock = try block.updated(withStates: states)
    let updatedGalleryBlock = updatedBlock.child as? GalleryBlock
    XCTAssertEqual(updatedGalleryBlock?.state, state)
  }
}

private let wrapperBlock: WrapperBlock = BackgroundBlock(
  background: .solidColor(.clear),
  child: GalleryBlockTestModels.base
)
