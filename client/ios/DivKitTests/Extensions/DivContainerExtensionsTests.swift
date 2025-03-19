@testable import DivKit
@testable import LayoutKit
import XCTest

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

  func test_EmptyItems_WrapContent() throws {
    let block = makeBlock(
      divContainer(
        items: [],
        width: wrapContentSize()
      )
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          widthTrait: .intrinsic,
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
              child: textBlock(
                text: "Hello!",
                path: defaultContainerPath + 1 + "text"
              ),
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

  func test_NoClipping() throws {
    let block = makeBlock(
      divContainer(
        clipToBounds: false,
        items: [
          divSeparator(),
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
          ],
          clipContent: false
        ),
        boundary: .noClip,
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
              child: textBlock(
                text: "Item 1",
                path: defaultContainerPath + 0 + "text"
              ),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Item 1"
              )
            ),
            DecoratingBlock(
              child: textBlock(
                text: "Item 2",
                path: defaultContainerPath + 1 + "text"
              ),
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

  func test_WithItemBuilderAndPrototypeId() throws {
    let block = makeBlock(
      divContainer(
        itemBuilder: DivCollectionItemBuilder(
          data: .value([
            [],
            [],
          ]),
          prototypes: [
            DivCollectionItemBuilder.Prototype(
              div: divText(
                id: "must_be_overloaded",
                textExpression: "Index = @{index}"
              ),
              id: expression("item_@{index}")
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
              child: textBlock(
                text: "Index = 0",
                path: defaultContainerPath + 0 + "item_0"
              ),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Index = 0",
                identifier: "item_0"
              )
            ),
            DecoratingBlock(
              child: textBlock(
                text: "Index = 1",
                path: defaultContainerPath + 1 + "item_1"
              ),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Index = 1",
                identifier: "item_1"
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
  
  func test_ItemBuilder_PathsWithPrototypeId() throws {
    let context = DivBlockModelingContext(
      cardId: "custom_card_id"
    )

    let block = makeBlock(
      divContainer(
        itemBuilder: DivCollectionItemBuilder(
          data: .value([
            [],
            [],
          ]),
          prototypes: [
            DivCollectionItemBuilder.Prototype(
              div: divText(
                textExpression: "Index = @{index}"
              ),
              id: expression("item_@{index}")
            ),
          ]
        )
      ),
      context: context,
      stateId: 777
    )
    let customContainerPath = UIElementPath("custom_card_id") + 777 + "container"

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: [
            DecoratingBlock(
              child: textBlock(
                text: "Index = 0",
                path: customContainerPath + 0 + "item_0"
              ),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Index = 0",
                identifier: "item_0"
              )
            ),
            DecoratingBlock(
              child: textBlock(
                text: "Index = 1",
                path: customContainerPath + 1 + "item_1"
              ),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Index = 1",
                identifier: "item_1"
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
              child: textBlock(
                text: "itemBuilder",
                path: defaultContainerPath + 0 + "text"
              ),
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

    let nestedTextPath = UIElementPath.root + 0 + "container" + 2 + "container" + 0 + "text"
    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: [
            DecoratingBlock(
              child: textBlock(
                text: "Hello!",
                path: defaultContainerPath + 0 + "text"
              ),
              accessibilityElement: accessibility(
                traits: .staticText,
                label: "Hello!"
              )
            ),
            DecoratingBlock(
              child: textBlock(
                text: "Excluded item",
                path: defaultContainerPath + 1 + "text"
              ),
              accessibilityElement: accessibility(hideElementWithChildren: true)
            ),
            DecoratingBlock(
              child: ContainerBlock(
                layoutDirection: .vertical,
                children: [
                  DecoratingBlock(
                    child: textBlock(
                      text: "Nested item",
                      path: nestedTextPath
                    ),
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

  func test_WrapContent_MatchParentItem() throws {
    let context = DivBlockModelingContext()
    let block = makeBlock(
      divContainer(
        items: [
          divText(
            text: "Hello!",
            width: matchParentSize()
          ),
        ],
        width: wrapContentSize()
      ),
      context: context,
      ignoreErrors: true
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          widthTrait: .intrinsic,
          children: [
            DecoratingBlock(
              child: textBlock(
                widthTrait: .intrinsic(
                  constrained: true,
                  minSize: 0,
                  maxSize: .infinity
                ),
                text: "Hello!",
                path: defaultContainerPath + 0 + "text"
              ),
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

    assertEqual(context.errorsStorage.errors.count, 1)
    assertEqual(
      context.errorsStorage.errors.first?.message,
      "All items in DivContainer with wrap_content width has match_parent width"
    )
  }

  func test_AddsIndexedParentPathToItems() throws {
    let block = try makeBlock(
      fromFile: "item_with_action"
    ) as! WrapperBlock

    let container = block.child as! ContainerBlock
    let wrappedContainer = container.children[0].content as! WrapperBlock
    let gallery = wrappedContainer.child as! GalleryBlock

    XCTAssertEqual(
      gallery.model.path,
      UIElementPath.root + "container" + 0 + "gallery"
    )
  }

  func test_PathForMultipleItems_withId() throws {
    let textWithIdDiv = divText(
      id: "text_id"
    )
    let textWithoutIdDiv = divText(
      id: nil
    )

    let block = makeBlock(
      divContainer(
        id: "container_id",
        items: [
          textWithIdDiv,
          textWithoutIdDiv,
          divGallery(
            items: [textWithIdDiv, textWithoutIdDiv],
            id: "gallery_id"
          ),
        ]
      ),
      stateId: 777
    )

    let container: ContainerBlock = try block.child.unwrap()
    let items = container.children.map(\.content)
    let textWithIdBlock: TextBlock = try items[0].unwrap()
    let textWithoutIdBlock: TextBlock = try items[1].unwrap()
    let gallery: GalleryBlock = try items[2].unwrap()

    let containerPath = UIElementPath.root + 777 + "container_id"
    XCTAssertEqual(
      textWithIdBlock.path,
      containerPath + 0 + "text_id"
    )

    XCTAssertEqual(
      textWithoutIdBlock.path,
      containerPath + 1 + "text"
    )

    XCTAssertEqual(
      gallery.model.path,
      containerPath + 2 + "gallery_id"
    )
  }

  func test_PathForMultipleItems_noId() throws {
    let textWithIdDiv = divText(
      id: "text_id"
    )
    let textWithoutIdDiv = divText(
      id: nil
    )

    let block = makeBlock(
      divContainer(
        id: nil,
        items: [
          textWithIdDiv,
          textWithoutIdDiv,
          divGallery(
            items: [textWithIdDiv, textWithoutIdDiv]
          ),
        ]
      ),
      stateId: 777
    )

    let container: ContainerBlock = try block.child.unwrap()
    let items = container.children.map(\.content)
    let textWithIdBlock: TextBlock = try items[0].unwrap()
    let textWithoutIdBlock: TextBlock = try items[1].unwrap()
    let gallery: GalleryBlock = try items[2].unwrap()

    let containerPath = UIElementPath.root + 777 + "container"
    XCTAssertEqual(
      textWithIdBlock.path,
      containerPath + 0 + "text_id"
    )

    XCTAssertEqual(
      textWithoutIdBlock.path,
      containerPath + 1 + "text"
    )

    XCTAssertEqual(
      gallery.model.path,
      containerPath + 2 + "gallery"
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

extension DivBlockModelingError: Swift.Equatable {
  public static func ==(lhs: DivBlockModelingError, rhs: DivBlockModelingError) -> Bool {
    lhs.message == rhs.message
      && lhs.path == rhs.path
      && lhs.causes.map(\.description) == rhs.causes.map(\.description)
  }
}

private let defaultContainerPath = UIElementPath.root + 0 + "container"
