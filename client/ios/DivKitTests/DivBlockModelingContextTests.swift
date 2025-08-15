@testable import DivKit
import DivKitTestsSupport
import LayoutKit
import XCTest

final class DivBlockModelingContextTests: XCTestCase {
  func test_parentPath_InitiallyEqualsToCardId() {
    let context = DivBlockModelingContext(cardId: "card_id")

    XCTAssertEqual("card_id", context.path)
  }

  func test_parentPath_ContainsAdditionalId() {
    let context = DivBlockModelingContext(
      cardId: "card_id",
      additionalId: "additional_id"
    )

    XCTAssertEqual(UIElementPath("card_id") + "additional_id", context.path)
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
    let context = DivBlockModelingContext(cardId: "card_id")
      .modifying(pathSuffix: "0")
      .modifying(pathSuffix: "element_id")

    let tooltipContext = context.cloneForTooltip(tooltipId: "tooltip_id")

    XCTAssertEqual(
      DivViewId(cardId: "card_id", additionalId: "tooltip_id"),
      tooltipContext.viewId
    )

    XCTAssertEqual(
      UIElementPath("card_id") + "tooltip_id",
      tooltipContext.path
    )
  }

  func test_WhenHasNoExtensionHandler_AddsErrorToErrorStorage() throws {
    let context = DivBlockModelingContext()

    _ = try DivDataTemplate.make(
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
}

private final class MockSizeModifier: DivSizeModifier {
  func transformWidth(_ width: DivSize) -> DivSize {
    width
  }

  func transformHeight(_ height: DivSize) -> DivSize {
    height
  }
}
