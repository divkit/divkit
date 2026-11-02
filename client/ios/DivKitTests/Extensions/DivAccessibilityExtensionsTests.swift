@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import VGSL
import XCTest

final class DivAccessibilityExtensionsTests: XCTestCase {
  func test_resolve_Empty() {
    let element = DivAccessibility()
      .resolve()

    let expectedElement = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(label: nil)
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithId() {
    let element = DivAccessibility()
      .resolve(id: "id")

    let expectedElement = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(
        label: nil,
        identifier: "id"
      )
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithTypeDescriptionAndHint() {
    let element = DivAccessibility(
      description: .value("Description"),
      hint: .value("hint"),
      type: .button
    ).resolve()

    let expectedElement = AccessibilityElement(
      traits: .button,
      strings: AccessibilityElement.Strings(
        label: "Description",
        hint: "hint"
      )
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithType_MakesEmptyDescription() {
    let element = DivAccessibility(
      type: .image
    ).resolve()

    let expectedElement = AccessibilityElement(
      traits: .image,
      strings: AccessibilityElement.Strings(label: "")
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithTypeAndNilCustomDescription_MakesEmptyDescription() {
    let element = DivAccessibility(
      type: .button
    ).resolve(customDescriptionProvider: { nil })

    let expectedElement = AccessibilityElement(
      traits: .button,
      strings: AccessibilityElement.Strings(label: "")
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithModeExclude_IgnoresParams() {
    let element = DivAccessibility(
      description: .value("Description"),
      hint: .value("hint"),
      mode: .value(.exclude),
      type: .button
    ).resolve(id: "id")

    let expectedElement = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(label: nil),
      hideElementWithChildren: true
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithTypeList() {
    let element = DivAccessibility(
      description: .value("Description"),
      type: .list
    ).resolve()

    let expectedElement = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(label: "Description"),
      isContainer: true
    )

    assertEqual(element, expectedElement)
  }

  func test_defaultTraits_UsedForAutoType() {
    let element = DivAccessibility(
      description: .value("Description"),
      type: .auto
    ).resolve(defaultTraits: .header)

    let expectedElement = AccessibilityElement(
      traits: .header,
      strings: AccessibilityElement.Strings(label: "Description")
    )

    assertEqual(element, expectedElement)
  }

  func test_defaultTraits_HasLowerPriorityThanType() {
    let element = DivAccessibility(
      description: .value("Description"),
      type: .button
    ).resolve(defaultTraits: .header)

    let expectedElement = AccessibilityElement(
      traits: .button,
      strings: AccessibilityElement.Strings(label: "Description")
    )

    assertEqual(element, expectedElement)
  }

  func test_customDescriptionProvider_HasHigherPriorityThanDescription() {
    let element = DivAccessibility(
      description: .value("Description"),
      type: .text
    ).resolve(customDescriptionProvider: { "Custom description" })

    let expectedElement = AccessibilityElement(
      traits: .staticText,
      strings: AccessibilityElement.Strings(label: "Custom description")
    )

    assertEqual(element, expectedElement)
  }

  func test_resolveMenu_KeepsOrderAndLabels() {
    let actions = resolveMenu([
      menuItem("First"),
      menuItem("Second"),
    ])

    XCTAssertEqual(actions.map(\.name), ["First", "Second"])
  }

  func test_resolveMenu_SkipsItemWithAllActionsDisabled() {
    let actions = resolveMenu([
      menuItem("Item", actions: [divAction(isEnabled: false, logId: "disabled")]),
    ])

    XCTAssertTrue(actions.isEmpty)
  }

  func test_resolveMenu_DropsDisabledActions() {
    let actions = resolveMenu([
      menuItem("Item", actions: [
        divAction(isEnabled: false, logId: "disabled"),
        divAction(logId: "enabled"),
      ]),
    ])

    XCTAssertEqual(actions.first?.actions.asArray().count, 1)
  }

  func test_resolveMenu_SkipsItemWithEmptyActions() {
    let actions = resolveMenu([
      menuItem("Item", actions: []),
    ])

    XCTAssertTrue(actions.isEmpty)
  }

  func test_resolveMenu_WithModeExclude_ReturnsEmpty() {
    let actions = DivAccessibility(
      menu: [menuItem("Item")],
      mode: .value(.exclude)
    ).resolveMenu(context: .default)

    XCTAssertTrue(actions.isEmpty)
  }

  func test_resolveMenu_KeepsOnlyMenuAction() {
    let actions = resolveMenu([
      menuItem("Item", actions: [divAction(logId: "other"), menuAction()]),
    ])

    let commandActions = actions.first?.actions.asArray() ?? []
    XCTAssertEqual(commandActions.count, 1)
    guard case .menu = commandActions.first?.payload else {
      return XCTFail("Expected the menu action")
    }
  }

  func test_makeBlock_MenuReachesDecoratingBlock() throws {
    let block = try divContainer(
      accessibility: DivAccessibility(
        description: .value("Card"),
        menu: [menuItem("Item")]
      ),
      items: [divText(text: "Text")]
    ).value.makeBlock(context: .default)

    XCTAssertEqual(
      (block as? DecoratingBlock)?.accessibilityCustomActions.map(\.name),
      ["Item"]
    )
  }

  func test_makeBlock_MenuUsesCaptureFocus_WithoutActions() throws {
    let blocks = try decoratingBlocks(divSeparator(
      accessibility: DivAccessibility(menu: [menuItem("Item")]),
      captureFocusOnAction: false
    ))

    let menuBlock = try XCTUnwrap(blocks.first { !$0.accessibilityCustomActions.isEmpty })
    XCTAssertFalse(menuBlock.captureFocusOnAction)
  }

  func test_makeBlock_MenuUsesCaptureFocus_WithShadow() throws {
    let blocks = try decoratingBlocks(divSeparator(
      accessibility: DivAccessibility(menu: [menuItem("Item")]),
      actions: [divAction(logId: "tap")],
      border: DivBorder(hasShadow: .value(true)),
      captureFocusOnAction: false
    ))

    let menuBlock = try XCTUnwrap(blocks.first { !$0.accessibilityCustomActions.isEmpty })
    let tapBlock = try XCTUnwrap(blocks.first { $0.actions != nil })
    XCTAssertFalse(menuBlock === tapBlock, "Shadow should split menu and tap actions")
    XCTAssertFalse(menuBlock.captureFocusOnAction)
    XCTAssertFalse(tapBlock.captureFocusOnAction)
  }

  func test_makeBlock_TapKeepsOnlyMenuAction() throws {
    let blocks = try decoratingBlocks(divSeparator(
      actions: [divAction(logId: "other"), menuAction()]
    ))

    let tapActions = try XCTUnwrap(blocks.compactMap(\.actions).first).asArray()
    XCTAssertEqual(tapActions.count, 1)
    guard case .menu = tapActions.first?.payload else {
      return XCTFail("Expected the menu action")
    }
  }
}

extension DivAccessibility {
  fileprivate func resolve(
    id: String? = nil,
    defaultTraits: AccessibilityElement.Traits = .none,
    customDescriptionProvider: (() -> String?)? = nil
  ) -> AccessibilityElement {
    resolve(
      DivBlockModelingContext.default.expressionResolver,
      id: id,
      block: EmptyBlock.zeroSized,
      customParams: CustomAccessibilityParams(
        defaultTraits: defaultTraits,
        descriptionProvider: customDescriptionProvider
      )
    )
  }
}

private func menuAction() -> DivAction {
  DivAction(
    logId: .value("menu"),
    menuItems: [DivAction.MenuItem(action: divAction(logId: "item"), text: .value("Item"))]
  )
}

private func decoratingBlocks(_ div: Div) throws -> [DecoratingBlock] {
  var blocks: [DecoratingBlock] = []
  var block: Block? = try div.value.makeBlock(context: .default)
  while let current = block {
    if let decoratingBlock = current as? DecoratingBlock {
      blocks.append(decoratingBlock)
    }
    block = (current as? WrapperBlock)?.child
  }
  return blocks
}

private func menuItem(
  _ label: String,
  actions: [DivAction] = [divAction(logId: "action")]
) -> DivAccessibilityMenuItem {
  DivAccessibilityMenuItem(actions: actions, label: .value(label))
}

private func resolveMenu(_ menu: [DivAccessibilityMenuItem]) -> [AccessibilityCustomAction] {
  DivAccessibility(menu: menu).resolveMenu(context: .default)
}
