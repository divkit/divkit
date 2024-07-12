import XCTest

import LayoutKit
import VGSL

final class ContainerBlockTests: XCTestCase {
  func test_WhenUpdatesState_SendsStatesToChildren() throws {
    let state = GalleryViewState(contentOffset: 1, itemsCount: 2)
    let states = [GalleryBlockTestModels.path: state]
    let block = containerBlock

    let updatedBlock = try block.updated(withStates: states)
    let updatedGalleryBlock = updatedBlock.children.first?.content as? GalleryBlock
    XCTAssertEqual(updatedGalleryBlock?.state, state)
  }
}

private let containerBlock = try! ContainerBlock(
  layoutDirection: .horizontal,
  children: [GalleryBlockTestModels.base]
)
