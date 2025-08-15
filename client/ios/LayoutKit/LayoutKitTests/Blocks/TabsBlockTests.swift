import LayoutKit
import VGSL
import XCTest

final class TabsBlockTests: XCTestCase {
  func test_WhenUpdatesState_UsesIt() throws {
    let state = TabViewState(selectedPageIndex: 1, countOfPages: 1)
    let states = [TabsBlockTestModels.path: state]
    let block = TabsBlockTestModels.base

    let updatedBlock = try block.updated(withStates: states)
    XCTAssertEqual(updatedBlock.state, state)
    XCTAssertNotEqual(updatedBlock.state, block.state)
  }

  func test_WhenUpdatesState_SendsStatesToItems() throws {
    let state = GalleryViewState(contentOffset: 1, itemsCount: 2)
    let states = [GalleryBlockTestModels.path: state]
    let block = TabsBlockTestModels.gallery

    let updatedBlock = try block.updated(withStates: states)
    let updatedGalleryBlock = updatedBlock.model.contentsModel.pages.first?.block as? GalleryBlock
    XCTAssertEqual(updatedGalleryBlock?.state, state)
  }

  func test_WhenUpdatesWithoutState_DoesNotTouchBlock() throws {
    let states: BlocksState = [:]
    let block = TabsBlockTestModels.base

    let updatedBlock = try block.updated(withStates: states)
    XCTAssertTrue(updatedBlock === block)
  }

  func test_WhenUpdatesState_DoesNotTouchInitialBlock() throws {
    let state = TabViewState(selectedPageIndex: 1, countOfPages: 1)
    let states = [TabsBlockTestModels.path: state]
    let block = TabsBlockTestModels.base

    _ = try block.updated(withStates: states)
    XCTAssertEqual(block.state, .default)
  }
}
