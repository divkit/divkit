@testable import DivKit
import DivKitTestsSupport
import LayoutKit
import VGSL
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

  func test_HorizontalGallery_UsesStartEndPaddingsAsAxialInsets() throws {
    let gallery = try makeGallery(
      paddings: DivEdgeInsets(bottom: .value(4), end: .value(8), start: .value(16), top: .value(2))
    )

    XCTAssertEqual(gallery.model.metrics.axialInsetMode, .fixed(values: SideInsets(
      leading: 16,
      trailing: 8
    )))
    XCTAssertEqual(gallery.model.metrics.crossInsetMode, .fixed(values: SideInsets(
      leading: 2,
      trailing: 4
    )))
  }

  func test_HorizontalGallery_PrefersStartEndPaddingsOverLeftRight() throws {
    let gallery = try makeGallery(
      paddings: DivEdgeInsets(left: .value(30), right: .value(40), start: .value(16))
    )

    XCTAssertEqual(gallery.model.metrics.axialInsetMode, .fixed(values: SideInsets(
      leading: 16,
      trailing: 0
    )))
  }

  func test_HorizontalGallery_InRTL_UsesStartEndPaddingsAsAxialInsets() throws {
    let gallery = try makeGallery(
      paddings: DivEdgeInsets(end: .value(8), start: .value(16)),
      layoutDirection: .rightToLeft
    )

    XCTAssertEqual(gallery.model.metrics.axialInsetMode, .fixed(values: SideInsets(
      leading: 16,
      trailing: 8
    )))
  }

  func test_HorizontalGallery_InRTL_KeepsLeftRightPaddingsPhysical() throws {
    let gallery = try makeGallery(
      paddings: DivEdgeInsets(left: .value(30), right: .value(40)),
      layoutDirection: .rightToLeft
    )

    XCTAssertEqual(gallery.model.metrics.axialInsetMode, .fixed(values: SideInsets(
      leading: 40,
      trailing: 30
    )))
  }

  func test_VerticalGallery_UsesStartEndPaddingsAsCrossInsets() throws {
    let paddings = DivEdgeInsets(
      bottom: .value(4),
      end: .value(8),
      start: .value(16),
      top: .value(2)
    )
    let ltrGallery = try makeGallery(orientation: .vertical, paddings: paddings)
    let rtlGallery = try makeGallery(
      orientation: .vertical,
      paddings: paddings,
      layoutDirection: .rightToLeft
    )

    XCTAssertEqual(ltrGallery.model.metrics.axialInsetMode, .fixed(values: SideInsets(
      leading: 2,
      trailing: 4
    )))
    XCTAssertEqual(ltrGallery.model.metrics.crossInsetMode, .fixed(values: SideInsets(
      leading: 16,
      trailing: 8
    )))
    XCTAssertEqual(rtlGallery.model.metrics.crossInsetMode, .fixed(values: SideInsets(
      leading: 16,
      trailing: 8
    )))
  }
}

private func makeGallery(
  orientation: DivGallery.Orientation? = nil,
  paddings: DivEdgeInsets,
  layoutDirection: UserInterfaceLayoutDirection = .leftToRight
) throws -> GalleryBlock {
  let context = DivKitComponents(layoutDirection: layoutDirection)
    .makeContext(cardId: "card", cachedImageHolders: [])
  let block = try divGallery(
    items: [divText(text: "text")],
    orientation: orientation,
    paddings: paddings
  ).value.makeBlock(context: context) as! WrapperBlock
  return block.child as! GalleryBlock
}

private func makeBlock(
  fromFile filename: String,
  context: DivBlockModelingContext = .default
) throws -> Block {
  try DivGallery.make(
    fromFile: filename,
    subdirectory: "div-gallery",
    context: context
  )
}
