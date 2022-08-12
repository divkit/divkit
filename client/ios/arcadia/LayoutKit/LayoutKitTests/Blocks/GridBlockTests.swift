// Copyright 2019 Yandex LLC. All rights reserved.

import XCTest

import CommonCore
import LayoutKit

final class GridBlockTests: XCTestCase {
  func test_WhenUpdatesState_SendsStatesToChildren() throws {
    let state = GalleryViewState(contentOffset: 1)
    let states = [GalleryBlockTestModels.path: state]
    let block = gridBlock

    let updatedBlock = try block.updated(withStates: states)
    let updatedGalleryBlock = updatedBlock.items.first?.contents as? GalleryBlock
    XCTAssertEqual(updatedGalleryBlock?.state, state)
  }

  func test_IntrinsicHeightOfResizableGridIsZero() {
    let block = try! GridBlock(
      widthTrait: .resizable,
      heightTrait: .resizable,
      contentAlignment: .default,
      items: [.init(contents: SeparatorBlock(direction: .vertical))],
      columnCount: 1,
      path: "/"
    )
    XCTAssertEqual(block.intrinsicContentHeight(forWidth: 100), 0)
  }
}

private let gridBlock = try! GridBlock(
  widthTrait: .intrinsic,
  heightTrait: .intrinsic,
  contentAlignment: .default,
  items: [.init(contents: GalleryBlockTestModels.base)],
  columnCount: 1,
  path: "/"
)
