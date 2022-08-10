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

private enum SwitchableContainerBlockTestModels {
  static let path = UIElementPath("switchableContainerBlock")

  static let base = SwitchableContainerBlock(
    selectedItem: .left,
    items: (
      .init(
        title: .init(text: "A", selectedTypo: Typo(), deselectedTypo: Typo()),
        content: GalleryBlockTestModels.base
      ),
      .init(
        title: .init(text: "A", selectedTypo: Typo(), deselectedTypo: Typo()),
        content: GalleryBlockTestModels.base
      )
    ),
    backgroundColor: .clear,
    selectedBackgroundColor: .clear,
    titleGaps: 0,
    titleContentGap: 0,
    selectorSideGaps: 0,
    switchAction: nil,
    path: path
  )
}
