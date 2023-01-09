import XCTest

import BaseUI
import CommonCore
import LayoutKit

final class SwitchableContainerBlockTests: XCTestCase {
  func test_WhenUpdatesState_UsesIt() throws {
    let state = SwitchableContainerBlockState(selectedItem: .right)
    let states = [SwitchableContainerBlockTestModels.path: state]
    let block = SwitchableContainerBlockTestModels.base

    let updatedBlock = try block.updated(withStates: states)
    XCTAssertEqual(updatedBlock.selectedItem, state.selectedItem)
    XCTAssertNotEqual(updatedBlock.selectedItem, block.selectedItem)
  }

  func test_WhenUpdatesState_SendsStatesToItems() throws {
    let state = GalleryViewState(contentOffset: 1)
    let states = [GalleryBlockTestModels.path: state]
    let block = SwitchableContainerBlockTestModels.base

    let updatedBlock = try block.updated(withStates: states)
    let updatedGalleryBlock = updatedBlock.items.0.content as? GalleryBlock
    XCTAssertEqual(updatedGalleryBlock?.state, state)
  }
}
