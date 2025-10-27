@testable import DivKit
import DivKitTestsSupport
import LayoutKit
import XCTest

final class DivGalleryExtensionsTests: XCTestCase {
  func test_WhenContextContainsGalleryState_RestoresIt() throws {
    let expectedState = GalleryViewState(contentOffset: 16, itemsCount: 1)
    let blockStateStorage = DivBlockStateStorage()
    blockStateStorage.setState(path: .root + DivGallery.type, state: expectedState)
    let context = DivBlockModelingContext(
      blockStateStorage: blockStateStorage
    )

    let block = try makeBlock(
      fromFile: "gallery",
      context: context
    ) as? WrapperBlock

    let gallery = block?.child as? GalleryBlock

    XCTAssertEqual(gallery?.state, expectedState)
  }

  func test_WhenContextContainsGalleryStateWithInconsistentScrollMode_IgnoresIt() throws {
    let blockStateStorage = DivBlockStateStorage()
    blockStateStorage.setState(
      path: .root + DivGallery.type,
      state: GalleryViewState(contentOffset: 100, itemsCount: 1)
    )
    let context = DivBlockModelingContext(
      blockStateStorage: blockStateStorage
    )
    let block = try makeBlock(
      fromFile: "paging",
      context: context
    ) as? WrapperBlock

    let gallery = block?.child as? GalleryBlock
    XCTAssertEqual(gallery?.model.scrollMode, .autoPaging(inertionEnabled: true))
    XCTAssertEqual(gallery?.state.contentPosition, .paging(index: 0))
  }

  func test_WhenScrollModeIsPaging_AutoPagingEnabled() throws {
    let block = try makeBlock(fromFile: "paging") as? WrapperBlock

    let gallery = block?.child as? GalleryBlock
    XCTAssertEqual(gallery?.model.scrollMode, .autoPaging(inertionEnabled: true))
  }

  func test_HorizontalGallery_WhenHasInstrinsicWidth_BuildsBlock() throws {
    _ = try makeBlock(fromFile: "horizontal_gallery_wrap_content_width")
  }

  func test_VerticalGallery_WhenHasInstrinsicHeight_BuildsBlock() throws {
    _ = try makeBlock(fromFile: "vertical_gallery_wrap_content_height")
  }

  func test_HorizontalGallery_WhenHasHorizontallyResizableItem_BuildsBlock() throws {
    _ = try makeBlock(fromFile: "horizontal_gallery_match_parent_width_items")
  }

  func test_VerticalGallery_WhenHasVerticallyResizableItem_BuildsBlock() throws {
    _ = try makeBlock(fromFile: "vertical_gallery_match_parent_height_items")
  }

  func test_HorizontalGallery_WhenVerticallyInrinsic_AndHasNonResizableVerticallyItems_BuildsBlocks(
  ) throws {
    _ = try makeBlock(fromFile: "horizontal_gallery_mixed_height_items")
  }

  func test_VerticalGallery_WhenHorizontallyInrinsic_AndHasNonResizableHorizontallyItems_BuildsBlocks(
  ) throws {
    _ = try makeBlock(fromFile: "vertical_gallery_mixed_width_items")
  }

  func test_EmptyGallery_WithPagingMode_DoesNotCrash() throws {
    let blockStateStorage = DivBlockStateStorage()
    blockStateStorage.setState(
      path: .root + DivGallery.type,
      state: GalleryViewState(
        contentPosition: .paging(index: 1),
        itemsCount: 1,
        isScrolling: false,
        animated: true
      )
    )
    let context = DivBlockModelingContext(
      blockStateStorage: blockStateStorage
    )
    let block = try makeBlock(
      fromFile: "empty_gallery",
      context: context
    ) as? WrapperBlock

    let gallery = block?.child as? GalleryBlock
    XCTAssertEqual(gallery?.state.contentPosition, .paging(index: 0))
    XCTAssertEqual(gallery?.model.items.count, 0)
  }
}

private func makeBlock(
  fromFile filename: String,
  context: DivBlockModelingContext = .default
) throws -> Block {
  try DivGalleryTemplate.make(
    fromFile: filename,
    subdirectory: "div-gallery",
    context: context
  )
}
