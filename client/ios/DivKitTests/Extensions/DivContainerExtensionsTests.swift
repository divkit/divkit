@testable import DivKit
@testable import LayoutKit

import XCTest

import BaseUIPublic
import CommonCorePublic

final class DivContainerExtensionsTests: XCTestCase {
  func test_NilItems() throws {
    let block = makeBlock(
      divContainer(items: nil)
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: []
        ),
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_EmptyItems() throws {
    let block = makeBlock(
      divContainer(items: [])
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: []
        ),
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithItems() throws {
    let block = makeBlock(
      divContainer(
        items: [
          divSeparator(),
          divText(text: "Hello!"),
        ]
      )
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: [
            DecoratingBlock(
              child: SeparatorBlock(
                color: color("#14000000")
              ),
              accessibilityElement: .default
            ),
            DecoratingBlock(
              child: textBlock(text: "Hello!"),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Hello!"
              )
            ),
          ]
        ),
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithItemBuilder() throws {
    let block = makeBlock(
      divContainer(
        itemBuilder: DivCollectionItemBuilder(
          data: .value([
            ["text": "Item 1"],
            ["text": "Item 2"],
          ]),
          prototypes: [
            DivCollectionItemBuilder.Prototype(
              div: divText(textExpression: "@{getStringFromDict(it, 'text')}")
            ),
          ]
        )
      )
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: [
            DecoratingBlock(
              child: textBlock(text: "Item 1"),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Item 1"
              )
            ),
            DecoratingBlock(
              child: textBlock(text: "Item 2"),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Item 2"
              )
            ),
          ]
        ),
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_ItemBuilder_HasHigherPriorityThanItems() throws {
    let block = makeBlock(
      divContainer(
        itemBuilder: DivCollectionItemBuilder(
          data: .value([[]]),
          prototypes: [
            DivCollectionItemBuilder.Prototype(
              div: divText(text: "itemBuilder")
            ),
          ]
        ),
        items: [
          divText(text: "items"),
        ]
      )
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: [
            DecoratingBlock(
              child: textBlock(text: "itemBuilder"),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "itemBuilder"
              )
            ),
          ]
        ),
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithMergeAccessibility() throws {
    let block = makeBlock(
      divContainer(
        accessibility: DivAccessibility(
          mode: .value(.merge),
          type: .button
        ),
        items: [
          divText(text: "Hello!"),
          divText(
            accessibility: DivAccessibility(mode: .value(.exclude)),
            text: "Excluded item"
          ),
          divContainer(
            items: [
              divText(text: "Nested item"),
            ]
          ),
        ]
      )
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: [
            DecoratingBlock(
              child: textBlock(text: "Hello!"),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Hello!"
              )
            ),
            DecoratingBlock(
              child: textBlock(text: "Excluded item"),
              accessibilityElement: accessibility(hideElementWithChildren: true)
            ),
            DecoratingBlock(
              child: ContainerBlock(
                layoutDirection: .vertical,
                children: [
                  DecoratingBlock(
                    child: textBlock(text: "Nested item"),
                    accessibilityElement: accessibility(
                      traits: .staticText,
                      label: "Nested item"
                    )
                  ),
                ]
              ),
              accessibilityElement: .default
            ),
          ]
        ),
        accessibilityElement: accessibility(
          traits: .button,
          label: "Hello! Nested item"
        )
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_AddsIndexedParentPathToItems() throws {
    let block = try makeBlock(
      fromFile: "item_with_action"
    ) as! WrapperBlock

    let container = block.child as! ContainerBlock
    let wrappedContainer = container.children[0].content as! WrapperBlock
    let gallery = wrappedContainer.child as! GalleryBlock

    // We are using "container" const instead of DivContainer.type to emphasise its importance for
    // analytics.
    // DivContainer.type changes can brake analytic reports.
    XCTAssertEqual(
      gallery.model.path,
      UIElementPath.root + "container" + 0 + "gallery"
    )
  }

  func test_HorizontalWrapContainer_HasVerticallyResizableItem_withoutFallbackHeight(
  ) throws {
    do {
      let _ = try makeBlock(
        fromFile: "horizontal_wrap_container_match_parent_height_item"
      ) as? WrapperBlock
    } catch {
      XCTAssertTrue(error is DivBlockModelingError)
    }
  }

  func test_VerticalWrapContainer_HasHorizontallyResizableItem_withoutFallbackWidth(
  ) throws {
    do {
      let _ = try makeBlock(
        fromFile: "vertical_wrap_container_match_parent_width_item"
      ) as? WrapperBlock
    } catch {
      XCTAssertTrue(error is DivBlockModelingError)
    }
  }

  func test_HorizontalContainer_WithIntrinsicWidth_AndHasSingleHorizontallyResizableItem_FallbackWidth(
  ) throws {
    try assertBlocksAreEqual(
      in: "wrap_content_width_match_parent_items",
      "wrap_content_width_wrap_content_constrained_items"
    )
  }

  func test_HorizontalContainer_WithIntrinsicWidth_AndHasHorizontallyResizableItem_FallbackWidth(
  ) throws {
    try assertBlocksAreEqual(
      in: "horizontal_wrap_content_width_match_parent_item",
      "horizontal_wrap_content_width_wrap_content_constrained_item"
    )
  }

  func test_HorizontalContainer_WithIntrinsicHeight_AndAllItemsAreVerticallyResizable_FallbackHeight(
  ) throws {
    try assertBlocksAreEqual(
      in: "horizontal_wrap_content_height_match_parent_items",
      "horizontal_wrap_content_height_wrap_content_constrained_items"
    )
  }

  func test_VerticalContainer_WithIntrinsicWidth_AndAllItemsAreHorizontallyResizable_FallbackWidth(
  ) throws {
    try assertBlocksAreEqual(
      in: "vertical_wrap_content_width_match_parent_items",
      "vertical_wrap_content_width_wrap_content_constrained_items"
    )
  }

  func test_VerticalContainer_WithIntrinsicHeight_AndHasVerticallyResizableItem_FallbackHeight(
  ) throws {
    try assertBlocksAreEqual(
      in: "vertical_wrap_content_height_match_parent_item",
      "vertical_wrap_content_height_wrap_content_constrained_item"
    )
  }

  func test_OverlapContainer_WithIntrinsicWidth_AndAllItemsAreHorizontallyResizable_FallbackWidth(
  ) throws {
    try assertBlocksAreEqual(
      in: "overlap_wrap_content_width_match_parent_items",
      "overlap_wrap_content_width_wrap_content_constrained_items"
    )
  }

  func test_OverlapContainer_WithIntrinsicHeight_AndAllItemsAreVerticallyResizable_FallbackHeight(
  ) throws {
    try assertBlocksAreEqual(
      in: "overlap_wrap_content_height_match_parent_items",
      "overlap_wrap_content_height_wrap_content_constrained_items"
    )
  }

  func test_AxialItemsAlignmentsAreIgnoredInHorizontalContainer() throws {
    try assertBlocksAreEqual(
      in: "horizontal_orientation_axial_alignments",
      "horizontal_orientation_no_alignments"
    )
  }

  func test_AxialItemsAlignmentsAreIgnoredInVerticalContainer() throws {
    try assertBlocksAreEqual(
      in: "vertical_orientation_axial_alignments",
      "vertical_orientation_no_alignments"
    )
  }

  private func assertBlocksAreEqual(in files: String...) throws {
    let first = try makeBlock(fromFile: files[0])
    for file in files.dropFirst() {
      XCTAssertTrue(try first == makeBlock(fromFile: file))
    }
  }
}

private func makeBlock(fromFile filename: String) throws -> Block {
  try DivContainerTemplate.make(
    fromFile: filename,
    subdirectory: "div-container",
    context: .default
  )
}

extension DivBlockModelingError: Equatable {
  public static func ==(lhs: DivBlockModelingError, rhs: DivBlockModelingError) -> Bool {
    lhs.message == rhs.message
      && lhs.path == rhs.path
      && lhs.causes.map(\.description) == rhs.causes.map(\.description)
  }
}
