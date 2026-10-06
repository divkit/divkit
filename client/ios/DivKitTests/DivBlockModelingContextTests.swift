@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import XCTest

final class DivBlockModelingContextTests: XCTestCase {
  func test_parentPath_InitiallyEqualsToCardId() {
    let context = DivBlockModelingContext(cardId: "card_id")

    XCTAssertEqual("card_id", context.path)
  }

  func test_parentPath_ContainsAdditionalId() {
    let cardId = DivCardID(rawValue: "card_id")
    let context = DivKitComponents().makeContext(
      cardId: cardId,
      additionalId: "additional_id",
      cachedImageHolders: []
    )

    XCTAssertEqual(
      context.path,
      UIElementPath("card_id") + "tooltip#additional_id"
    )
  }

  func test_modifying_cardLogId() {
    let context = DivBlockModelingContext()
      .modifying(cardLogId: "new_card_log_id")

    XCTAssertEqual(context.cardLogId, "new_card_log_id")
  }

  func test_modifying_pathSuffix() {
    let cardId = "custom_card_id"
    let pathSuffix = "path_suffix"
    let context = DivBlockModelingContext(
      cardId: DivCardID(rawValue: cardId)
    ).modifying(pathSuffix: pathSuffix)

    let expectedPath = UIElementPath(cardId) + pathSuffix
    XCTAssertEqual(context.path, expectedPath)

    _ = context.expressionResolver.resolveString(expression("@{invalid}"))

    XCTAssertEqual(context.errorsStorage.errors[0].path, expectedPath)
  }

  func test_modifying_parentPath_ProvidesAccessToLocalVariables() {
    let context = DivBlockModelingContext()
    let elementSuffix = "element_id"
    let elementPath = context.path + "element_id"
    context.variablesStorage.initializeIfNeeded(
      path: elementPath,
      variables: ["local_var": .string("value")]
    )

    let elementContext = context.modifying(pathSuffix: elementSuffix)

    XCTAssertEqual(
      "value",
      elementContext.expressionResolver.resolveString(expression("@{local_var}"))
    )
  }

  func test_modifying_parentDivStatePath() {
    let divStatePath = DivStatePath.makeDivStatePath(from: "0/div_state")
    let context = DivBlockModelingContext()
      .modifying(parentDivStatePath: divStatePath)

    XCTAssertEqual(context.parentDivStatePath, divStatePath)
  }

  func test_modifying_sizeModifier() {
    let sizeModifier = MockSizeModifier()
    let context = DivBlockModelingContext()
      .modifying(sizeModifier: sizeModifier)

    XCTAssertIdentical(context.sizeModifier as! MockSizeModifier, sizeModifier)
  }

  func test_modifying_errorsStorage() {
    let errorsStorage = DivErrorsStorage(errors: [])
    let context = DivBlockModelingContext()
      .modifying(errorsStorage: errorsStorage)

    XCTAssertIdentical(context.errorsStorage, errorsStorage)

    _ = context.expressionResolver.resolveString(expression("@{invalid}"))

    XCTAssertEqual(errorsStorage.errors.count, 1)
  }

  func test_modifying_prototypeParams_AddsVariableToExpressionResolver() {
    let context = DivBlockModelingContext().modifying(
      prototypeParams: PrototypeParams(
        index: 1,
        variableName: "it",
        value: ["key": "value"]
      )
    )

    XCTAssertEqual(
      context.expressionResolver.resolveString(expression("@{it.getString('key')}")),
      "value"
    )
  }

  func test_modifying_prototypeParams_AddsIndexVariableToExpressionResolver() {
    let context = DivBlockModelingContext().modifying(
      prototypeParams: PrototypeParams(
        index: 1,
        variableName: "it",
        value: ["key": "value"]
      )
    )

    XCTAssertEqual(
      context.expressionResolver.resolveNumeric(expression("@{index}")),
      1
    )
  }

  func test_cloneForTooltip() {
    let anchorPath = UIElementPath("card_id") + "0" + "element_id"
    let context = DivBlockModelingContext(cardId: "card_id")
      .modifying(pathSuffix: "0")
      .modifying(pathSuffix: "element_id")

    let tooltipContext = context.cloneForTooltip(tooltipId: "tooltip_id")

    // The anchor is part of the identity, not just of the path: two elements may declare
    // tooltips sharing an id, and only the anchor tells those views apart.
    XCTAssertEqual(
      DivViewId(
        cardId: "card_id",
        tooltip: DivViewId.Tooltip(id: "tooltip_id", anchorPath: anchorPath)
      ),
      tooltipContext.viewId
    )

    XCTAssertEqual(
      anchorPath + "tooltip#tooltip_id",
      tooltipContext.path
    )
  }

  func test_cloneForTooltip_SameIdOnDifferentAnchors_GivesDistinctViewIds() {
    let context = DivBlockModelingContext(cardId: "card_id").modifying(pathSuffix: "0")

    let first = context.modifying(pathSuffix: "anchor_a").cloneForTooltip(tooltipId: "hint")
    let second = context.modifying(pathSuffix: "anchor_b").cloneForTooltip(tooltipId: "hint")

    // div_tooltip_id is only required to be unique among one element's tooltips, so this is a
    // layout the contract allows - the two views must not collapse into one identity.
    XCTAssertNotEqual(first.viewId, second.viewId)
    XCTAssertNotEqual(first.path, second.path)
  }

  func test_WhenHasNoExtensionHandler_AddsErrorToErrorStorage() throws {
    let context = DivBlockModelingContext()

    _ = try DivData.make(
      fromFile: "div-with-extension-handler",
      subdirectory: "div-context",
      context: context
    )

    XCTAssertEqual(context.errorsStorage.errors.count, 1)
  }

  func testWhenModifyOtherPropertiesToNil_contextIsNotChanged() throws {
    let parentContext = DivBlockModelingContext().modifying(
      cardLogId: "card_log_id",
      pathSuffix: "path_suffix",
      parentDivStatePath: .makeDivStatePath(from: "div_id")
    )

    let childContext = parentContext.modifying(
      cardLogId: nil,
      pathSuffix: nil,
      parentDivStatePath: nil
    )

    XCTAssertEqual(childContext.cardLogId, parentContext.cardLogId)
    XCTAssertEqual(childContext.path, parentContext.path)
    XCTAssertEqual(childContext.parentDivStatePath, parentContext.parentDivStatePath)
  }

  func test_WhenModifyIdPropertiesToNil_propertiesAreNil() throws {
    let parentContext = DivBlockModelingContext().modifying(
      overridenId: "overriden_id",
      currentDivId: "current_div_id",
      pathSuffix: "arbitrary_parent_path_suffix"
    )

    let childContext = parentContext.modifying(
      overridenId: nil,
      currentDivId: nil,
      pathSuffix: "arbitrary_child_path_suffix"
    )

    XCTAssertNil(childContext.overridenId)
    XCTAssertNil(childContext.currentDivId)
  }

  func test_elementIdWithTooltipPrefix_addsWarning() {
    let context = DivBlockModelingContext()
    _ = makeBlock(
      divSeparator(id: "\(DivViewId.tooltipMarker)button"),
      context: context,
      ignoreErrors: true
    )

    let warnings = context.errorsStorage.errors.filter { $0.level == .warning }
    XCTAssertEqual(warnings.count, 1)
    XCTAssertTrue(warnings[0].message.contains(DivViewId.tooltipMarker))
  }
}

private final class MockSizeModifier: DivSizeModifier {
  func transformWidth(_ width: DivSize) -> DivSize {
    width
  }

  func transformHeight(_ height: DivSize) -> DivSize {
    height
  }
}
