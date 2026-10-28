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

  func test_VerticalGallery_WhenHorizontallyIntrinsic_AndAllItemsMatchParent_ItemsFillCrossAxis(
  ) throws {
    let gallery = try makeGalleryBlock(
      orientation: .vertical,
      width: wrapContentSize(),
      height: fixedSize(300),
      items: [
        divText(text: "first", width: matchParentSize(), height: fixedSize(40)),
        divText(text: "second", width: matchParentSize(), height: fixedSize(40)),
      ]
    )

    XCTAssertEqual(gallery.model.items.map(\.fillsCrossAxis), [true, true])
    XCTAssertTrue(gallery.model.items.allSatisfy { !$0.content.isHorizontallyResizable })
  }

  func test_HorizontalGallery_WhenVerticallyIntrinsic_AndAllItemsMatchParent_ItemsFillCrossAxis(
  ) throws {
    let gallery = try makeGalleryBlock(
      orientation: .horizontal,
      width: fixedSize(300),
      height: wrapContentSize(),
      items: [
        divText(text: "first", width: fixedSize(40), height: matchParentSize()),
        divText(text: "second", width: fixedSize(40), height: matchParentSize()),
      ]
    )

    XCTAssertEqual(gallery.model.items.map(\.fillsCrossAxis), [true, true])
    XCTAssertTrue(gallery.model.items.allSatisfy { !$0.content.isVerticallyResizable })
  }

  func test_VerticalGallery_WhenHorizontallyIntrinsic_AndItemsAreMixed_DoesNotRewriteMatchParent(
  ) throws {
    let gallery = try makeGalleryBlock(
      orientation: .vertical,
      width: wrapContentSize(),
      height: fixedSize(300),
      items: [
        divText(text: "match_parent", width: matchParentSize(), height: fixedSize(40)),
        divText(text: "fixed", width: fixedSize(100), height: fixedSize(40)),
      ]
    )

    XCTAssertEqual(gallery.model.items.map(\.fillsCrossAxis), [false, false])
    XCTAssertTrue(gallery.model.items[0].content.isHorizontallyResizable)
  }

  func test_HorizontalGallery_WhenVerticallyIntrinsic_AndItemsAreMixed_DoesNotRewriteMatchParent(
  ) throws {
    let gallery = try makeGalleryBlock(
      orientation: .horizontal,
      width: fixedSize(300),
      height: wrapContentSize(),
      items: [
        divText(text: "match_parent", width: fixedSize(40), height: matchParentSize()),
        divText(text: "fixed", width: fixedSize(40), height: fixedSize(100)),
      ]
    )

    XCTAssertEqual(gallery.model.items.map(\.fillsCrossAxis), [false, false])
    XCTAssertTrue(gallery.model.items[0].content.isVerticallyResizable)
  }

  func test_HorizontalGallery_WhenItemsAreMixed_MatchParentItemTallerThanSiblingDoesNotGrowRow(
  ) throws {
    let gallery = try makeGalleryBlock(
      orientation: .horizontal,
      width: fixedSize(300),
      height: wrapContentSize(),
      items: [
        divText(
          text: (1...9).map { "line \($0)" }.joined(separator: "\n"),
          width: fixedSize(80),
          height: matchParentSize()
        ),
        divText(
          text: (1...3).map { "line \($0)" }.joined(separator: "\n"),
          width: fixedSize(80)
        ),
      ]
    )
    let siblingHeight = gallery.model.items[1].content.intrinsicContentHeight(forWidth: 80)
    let layout = GalleryViewLayout(model: gallery.model)

    XCTAssertFalse(gallery.model.items[0].fillsCrossAxis)
    XCTAssertGreaterThan(siblingHeight, 0)
    XCTAssertEqual(layout.contentSize.height, siblingHeight)
    XCTAssertEqual(layout.blockFrames[0].height, siblingHeight)
  }

  func test_VerticalGallery_WhenHorizontallyIntrinsic_AndAllItemsMatchParent_KeepsItsConstraints(
  ) throws {
    let gallery = try makeGalleryBlock(
      orientation: .vertical,
      width: wrapContentSize(),
      height: fixedSize(300),
      items: [
        divText(text: "first", width: matchParentSize(minSize: 50, maxSize: 120)),
        divText(text: "second", width: matchParentSize()),
      ]
    )
    let item = gallery.model.items[0].content

    XCTAssertEqual(item.minWidth, 50)
    XCTAssertEqual(item.maxWidth, 120)
  }

  func test_HorizontalGallery_WhenHasHorizontallyResizableItem_DoesNotFillCrossAxis() throws {
    let gallery = try makeGalleryBlock(fromFile: "horizontal_gallery_match_parent_width_items")

    XCTAssertTrue(gallery.model.items.allSatisfy { !$0.fillsCrossAxis })
  }

  func test_ItemBuilderGallery_InsideGalleryWithAllItemsMatchParent_DoesNotTakeFillsCrossAxisFromOuterGallery(
  ) throws {
    let inner = divGallery(
      items: [],
      orientation: .vertical,
      width: fixedSize(200),
      height: matchParentSize(),
      itemBuilder: DivCollectionItemBuilder(
        data: .value([["text": "item"]]),
        prototypes: [
          DivCollectionItemBuilder.Prototype(
            div: divText(text: "item", width: wrapContentSize(), height: matchParentSize())
          ),
        ]
      )
    )
    let outer = try XCTUnwrap(findGalleryBlock(in: makeBlock(
      divGallery(
        items: [inner],
        orientation: .horizontal,
        width: fixedSize(300),
        height: wrapContentSize()
      )
    )))
    let innerGallery = try XCTUnwrap(findGalleryBlock(in: outer.model.items[0].content))

    // The outer modifier is active, otherwise the test would pass trivially.
    XCTAssertEqual(outer.model.items.map(\.fillsCrossAxis), [true])
    // The inner vertical gallery must not take the flag from the outer horizontal one.
    XCTAssertEqual(innerGallery.model.items.map(\.fillsCrossAxis), [false])
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

private func makeGalleryBlock(fromFile filename: String) throws -> GalleryBlock {
  let block = try makeBlock(fromFile: filename)
  return try XCTUnwrap((block as? WrapperBlock)?.child as? GalleryBlock ?? block as? GalleryBlock)
}

private func makeGalleryBlock(
  orientation: DivGallery.Orientation,
  width: DivSize,
  height: DivSize,
  items: [Div]
) throws -> GalleryBlock {
  let block = makeBlock(
    divGallery(items: items, orientation: orientation, width: width, height: height)
  )
  return try XCTUnwrap(findGalleryBlock(in: block))
}

private func findGalleryBlock(in block: Block) -> GalleryBlock? {
  if let gallery = block as? GalleryBlock {
    return gallery
  }
  return (block as? WrapperBlock).flatMap { findGalleryBlock(in: $0.child) }
}
