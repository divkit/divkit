@testable import DivKit

import XCTest

import LayoutKit

final class DivBlockModelingContextTests: XCTestCase {
  func test_modifying_cardLogId() {
    let context = DivBlockModelingContext()
      .modifying(cardLogId: "new_card_log_id")

    XCTAssertEqual(context.cardLogId, "new_card_log_id")
  }

  func test_modifying_parentPath() {
    let parentPath = UIElementPath("parent_path")
    let context = DivBlockModelingContext()
      .modifying(parentPath: parentPath)

    XCTAssertEqual(context.parentPath, parentPath)

    _ = context.expressionResolver.resolveString(expression("@{invalid}"))

    XCTAssertEqual(context.errorsStorage.errors[0].path, parentPath)
  }

  func test_modifying_parentPath_ProvidesAccessToLocalVariables() {
    let context = DivBlockModelingContext()
    let elementPath = context.parentPath + "element_id"
    context.variablesStorage.initializeIfNeeded(
      path: elementPath,
      variables: ["local_var": .string("value")]
    )

    let elementContext = context.modifying(parentPath: elementPath)

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

  func test_WhenHasNoExtensionHandler_AddsErrorToErrorStorage() throws {
    let context = DivBlockModelingContext()

    _ = try DivDataTemplate.make(
      fromFile: "div-with-extension-handler",
      subdirectory: "div-context",
      context: context
    )

    XCTAssertEqual(context.errorsStorage.errors.count, 1)
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
