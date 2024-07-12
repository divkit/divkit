import XCTest

import LayoutKit
import VGSL

final class GalleryBlockTests: XCTestCase {
  func test_WhenUpdatesState_UsesIt() throws {
    let state = GalleryViewState(contentOffset: 1, itemsCount: 2)
    let states = [GalleryBlockTestModels.path: state]
    let block = GalleryBlockTestModels.base

    let updatedBlock = try block.updated(withStates: states)
    XCTAssertEqual(updatedBlock.state, state)
    XCTAssertNotEqual(updatedBlock.state, block.state)
  }

  func test_WhenUpdatesState_SendsStatesToItems() throws {
    let state = TabViewState(selectedPageIndex: 1, countOfPages: 1)
    let states = [TabsBlockTestModels.path: state]
    let block = GalleryBlockTestModels.tabs

    let updatedBlock = try block.updated(withStates: states)
    let updatedTabsBlock = updatedBlock.model.items.first?.content as? TabsBlock
    XCTAssertEqual(updatedTabsBlock?.state, state)
  }

  func test_WhenUpdatesWithoutState_DoesNotTouchBlock() throws {
    let states: BlocksState = [:]
    let block = GalleryBlockTestModels.base

    let updatedBlock = try block.updated(withStates: states)
    XCTAssertTrue(updatedBlock === block)
  }

  func test_WhenUpdatesState_DoesNotTouchInitialBlock() throws {
    let state = GalleryViewState(contentOffset: 1, itemsCount: 1)
    let states = [GalleryBlockTestModels.path: state]
    let block = GalleryBlockTestModels.base

    _ = try block.updated(withStates: states)
    XCTAssertEqual(block.state, GalleryViewState(contentOffset: 0, itemsCount: 2))
  }
}

extension GalleryBlockTestModels {
  fileprivate static let tabs = make(blocks: [TabsBlockTestModels.base])
}
