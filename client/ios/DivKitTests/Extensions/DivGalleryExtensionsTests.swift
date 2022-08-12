// Copyright 2018 Yandex LLC. All rights reserved.

import XCTest

import BaseUI
import CommonCore
import DivKit
import LayoutKit

final class DivGalleryExtensionsTests: XCTestCase {
  func test_WhenCantBuildBlockForItems_ThrowsError() {
    XCTAssertThrowsError(
      try makeBlock(fromFile: "invalid_items"),
      DivBlockModelingError(
        "DivGallery has no items",
        path: .root + "gallery"
      )
    )
  }

  func test_WhenContextContainsGalleryState_RestoresIt() throws {
    let expectedState = GalleryViewState(contentOffset: 16)
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
      state: GalleryViewState(contentOffset: 100)
    )
    let context = DivBlockModelingContext(
      blockStateStorage: blockStateStorage
    )
    let block = try makeBlock(
      fromFile: "paging",
      context: context
    ) as? WrapperBlock

    let gallery = block?.child as? GalleryBlock
    XCTAssertEqual(gallery?.model.scrollMode, .autoPaging)
    XCTAssertEqual(gallery?.state.contentPosition, .paging(index: 0))
  }

  func test_WhenScrollModeIsPaging_AutoPagingEnabled() throws {
    let block = try makeBlock(fromFile: "paging") as? WrapperBlock

    let gallery = block?.child as? GalleryBlock
    XCTAssertEqual(gallery?.model.scrollMode, .autoPaging)
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

  func test_HorizontalGallery_WhenVerticallyInrinsic_AndAllItemsAreVerticallyResizable_ThrowsError(
  ) {
    XCTAssertThrowsError(
      try makeBlock(fromFile: "horizontal_gallery_match_parent_height_items"),
      DivBlockModelingError(
        "All items in horizontal DivGallery with wrap_content height has match_parent height",
        path: .root + "gallery"
      )
    )
  }

  func test_VerticalGallery_WhenHorizontallyInrinsic_AndAllItemsAreHorizontallyResizable_ThrowsError(
  ) {
    XCTAssertThrowsError(
      try makeBlock(fromFile: "vertical_gallery_match_parent_width_items"),
      DivBlockModelingError(
        "All items in vertical DivGallery with wrap_content width has match_parent width",
        path: .root + "gallery"
      )
    )
  }

  func test_HorizontalGallery_WhenVerticallyInrinsic_AndHasNonResizableVerticallyItems_BuildsBlocks(
  ) throws {
    _ = try makeBlock(fromFile: "horizontal_gallery_mixed_height_items")
  }

  func test_VerticalGallery_WhenHorizontallyInrinsic_AndHasNonResizableHorizontallyItems_BuildsBlocks(
  ) throws {
    _ = try makeBlock(fromFile: "vertical_gallery_mixed_width_items")
  }

  func test_WhenContextContainsGalleryResizableInsets_UsesThemToBuildBlocks() throws {
    let insets = InsetMode.Resizable(minValue: 12, maxViewportSize: 240)
    let context = DivBlockModelingContext(galleryResizableInsets: insets)

    let block = try makeBlock(
      fromFile: "gallery",
      context: context
    ) as? WrapperBlock
    let gallery = block?.child as? GalleryBlock

    XCTAssertEqual(gallery?.model.metrics.axialInsetMode, .resizable(params: insets))
  }

  func test_WhenContextContainsGalleryResizableInsets_DiscardsThemForNestedBlocks() throws {
    let context = DivBlockModelingContext(
      galleryResizableInsets: .init(minValue: 12, maxViewportSize: 240)
    )

    let block = try makeBlock(
      fromFile: "nested_gallery",
      context: context
    ) as? WrapperBlock

    let gallery = block?.child as? GalleryBlock

    let nestedGalleryBlock = gallery?.model.items.first?.content as? WrapperBlock
    let nestedGallery = nestedGalleryBlock?.child as? GalleryBlock
    let expectedInsets = SideInsets.zero
    XCTAssertEqual(nestedGallery?.model.metrics.axialInsetMode, .fixed(values: expectedInsets))
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

private let expectedInsets = SideInsets(leading: 12, trailing: 24)
