@testable @_spi(Internal) import DivKit
import LayoutKit
import XCTest

final class DivActionHandlerTests: XCTestCase {
  private var actionHandler: DivActionHandler!
  private let reporter = MockReporter()
  private let variablesStorage = DivVariablesStorage()

  private var handledUrl: URL?

  override func setUp() {
    let idToPath = IdToPath()
    idToPath[cardId.path + "element_id"] = cardId.path + "element_id"
    actionHandler = DivActionHandler(
      idToPath: idToPath,
      reporter: reporter,
      urlHandler: DivUrlHandlerDelegate { url, _ in
        self.handledUrl = url
      },
      variablesStorage: variablesStorage
    )
  }

  func test_UrlPassedToUrlHandler() {
    handle(
      divAction(
        logId: "test_log_id",
        url: "https://some.url"
      )
    )

    XCTAssertEqual(url("https://some.url"), handledUrl)
  }

  func test_UrlNotPassedToUrlHandler_IfTypedActionHandled() {
    handle(
      divAction(
        logId: "test_log_id",
        typed: .divActionSetVariable(
          DivActionSetVariable(
            value: stringValue("new value"),
            variableName: .value("string_var")
          )
        ),
        url: "https://some.url"
      )
    )

    XCTAssertNil(handledUrl)
  }

  func test_UrlWithExpression() {
    variablesStorage.set(cardId: cardId, variables: ["host": .string("test.url")])

    handle(
      divAction(
        logId: "test_log_id",
        urlExpression: "https://@{host}"
      )
    )

    XCTAssertEqual(url("https://test.url"), handledUrl)
  }

  func test_UrlWithExpressionWithLocalValues() {
    variablesStorage.set(cardId: cardId, variables: ["host": .string("test.url")])

    handle(
      divAction(
        logId: "test_log_id",
        urlExpression: "https://@{host}"
      ),
      localValues: ["host": "localhost"]
    )

    XCTAssertEqual(url("https://localhost"), handledUrl)
  }

  func test_ArrayInsertValueAction_AppendsValue() {
    setVariableValue("array_var", .array([1, "two"]))

    handle(.divActionArrayInsertValue(
      DivActionArrayInsertValue(
        value: stringValue("new value"),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual([1, "two", "new value"] as DivArray, getVariableValue("array_var"))
  }

  func test_ArrayInsertValueAction_InsertsValue() {
    setVariableValue("array_var", .array([1, "two"]))

    handle(.divActionArrayInsertValue(
      DivActionArrayInsertValue(
        index: .value(1),
        value: stringValue("new value"),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual([1, "new value", "two"] as DivArray, getVariableValue("array_var"))
  }

  func test_ArrayInsertValueAction_WithIndexEqualLength_InsertsValue() {
    setVariableValue("array_var", .array(["one", "two"]))

    handle(.divActionArrayInsertValue(
      DivActionArrayInsertValue(
        index: .value(2),
        value: stringValue("new value"),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual(["one", "two", "new value"], getVariableValue("array_var"))
  }

  func test_ArrayInsertValueAction_DoesNothingForInvalidIndex() {
    setVariableValue("array_var", .array([1, "two"]))

    handle(.divActionArrayInsertValue(
      DivActionArrayInsertValue(
        index: .value(10),
        value: stringValue("new value"),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual([1, "two"] as DivArray, getVariableValue("array_var"))
  }

  func test_ArrayInsertValueAction_DoesNothingForNotArrayVar() {
    setVariableValue("string_var", .string("value"))

    handle(.divActionArrayInsertValue(
      DivActionArrayInsertValue(
        value: stringValue("new value"),
        variableName: .value("string_var")
      )
    ))

    XCTAssertEqual("value", getVariableValue("string_var"))
  }

  func test_ArrayRemoveValueAction_RemovesValue() {
    setVariableValue("array_var", .array([1, "two"]))

    handle(.divActionArrayRemoveValue(
      DivActionArrayRemoveValue(
        index: .value(1),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual([1] as DivArray, getVariableValue("array_var"))
  }

  func test_ArrayRemoveValueAction_DoesNothingForInvalidIndex() {
    setVariableValue("array_var", .array([1, "two"]))

    handle(.divActionArrayRemoveValue(
      DivActionArrayRemoveValue(
        index: .value(10),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual([1, "two"] as DivArray, getVariableValue("array_var"))
  }

  func test_ArrayRemoveValueAction_DoesNothingForNotArrayVar() {
    setVariableValue("string_var", .string("value"))

    handle(.divActionArrayRemoveValue(
      DivActionArrayRemoveValue(
        index: .value(0),
        variableName: .value("string_var")
      )
    ))

    XCTAssertEqual("value", getVariableValue("string_var"))
  }

  func test_ArraySetValueAction_SetsValue() {
    setVariableValue("array_var", .array(["one", "two"]))

    handle(.divActionArraySetValue(
      DivActionArraySetValue(
        index: .value(1),
        value: stringValue("new value"),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual(["one", "new value"], getVariableValue("array_var"))
  }

  func test_ArraySetValueAction_DoesNothingForInvalidIndex() {
    setVariableValue("array_var", .array(["one", "two"]))

    handle(.divActionArraySetValue(
      DivActionArraySetValue(
        index: .value(2),
        value: stringValue("new value"),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual(["one", "two"], getVariableValue("array_var"))
  }

  func test_ArraySetValueAction_DoesNothingForNotArrayVar() {
    setVariableValue("string_var", .string("value"))

    handle(.divActionArraySetValue(
      DivActionArraySetValue(
        index: .value(0),
        value: stringValue("new value"),
        variableName: .value("string_var")
      )
    ))

    XCTAssertEqual("value", getVariableValue("string_var"))
  }

  func test_DictSetValueAction_AddsValue() {
    setVariableValue("dict_var", .dict([:]))

    handle(.divActionDictSetValue(
      DivActionDictSetValue(
        key: .value("key"),
        value: stringValue("new value"),
        variableName: .value("dict_var")
      )
    ))

    XCTAssertEqual(["key": "new value"], getVariableValue("dict_var"))
  }

  func test_DictSetValueAction_UpdatesValue() {
    setVariableValue("dict_var", .dict(["key": "value"]))

    handle(.divActionDictSetValue(
      DivActionDictSetValue(
        key: .value("key"),
        value: .dictValue(DictValue(value: ["new_key": "new value"])),
        variableName: .value("dict_var")
      )
    ))

    XCTAssertEqual(["key": ["new_key": "new value"]], getVariableValue("dict_var"))
  }

  func test_DictSetValueAction_UpdatesParentLocalValue() {
    let parentPath = cardId.path + "parent_id"
    variablesStorage.initializeIfNeeded(
      path: parentPath,
      variables: ["dict_var": .dict(["key": "value"])]
    )

    actionHandler.handle(
      divAction(
        logId: "log_id",
        typed: .divActionDictSetValue(DivActionDictSetValue(
          key: .value("key"),
          value: .dictValue(DictValue(value: ["new_key": "new value"])),
          variableName: .value("dict_var")
        ))
      ),
      path: parentPath + "element_id",
      source: .tap,
      sender: nil
    )

    XCTAssertEqual(
      ["key": ["new_key": "new value"]],
      variablesStorage.getVariableValue(path: parentPath, name: "dict_var")
    )
  }

  func test_DictSetValueAction_RemovesValue() {
    setVariableValue("dict_var", .dict(["key": "value"]))

    handle(.divActionDictSetValue(
      DivActionDictSetValue(
        key: .value("key"),
        variableName: .value("dict_var")
      )
    ))

    XCTAssertEqual(DivDictionary(), getVariableValue("dict_var"))
  }

  func test_DictSetValueAction_DoesNothingForNotDictVar() {
    setVariableValue("array_var", .array(["one", "two"]))

    handle(.divActionDictSetValue(
      DivActionDictSetValue(
        key: .value("key"),
        value: stringValue("new value"),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual(["one", "two"], getVariableValue("array_var"))
  }

  func test_SetVariableAction_SetsStringVariable() {
    setVariableValue("string_var", .string("default"))

    handle(.divActionSetVariable(
      DivActionSetVariable(
        value: stringValue("new value"),
        variableName: .value("string_var")
      )
    ))

    XCTAssertEqual("new value", getVariableValue("string_var"))
  }

  func test_SetVariableAction_SetsArrayVariable() {
    setVariableValue("array_var", .array([]))

    handle(.divActionSetVariable(
      DivActionSetVariable(
        value: .arrayValue(ArrayValue(value: .value(["value 1", "value 2"]))),
        variableName: .value("array_var")
      )
    ))

    XCTAssertEqual(["value 1", "value 2"], getVariableValue("array_var"))
  }

  func test_SetVariableAction_SetsDictVariable() {
    setVariableValue("dict_var", .dict([:]))

    handle(.divActionSetVariable(
      DivActionSetVariable(
        value: .dictValue(DictValue(
          value: ["key_1": "value_1", "nested": ["key_2": "value_2"]]
        )),
        variableName: .value("dict_var")
      )
    ))

    XCTAssertEqual(
      ["key_1": "value_1", "nested": ["key_2": "value_2"]] as DivDictionary,
      getVariableValue("dict_var")
    )
  }

  func test_SetVariableAction_SetsLocalVariable() {
    let path = cardId.path + "element_id"
    variablesStorage.initializeIfNeeded(
      path: path,
      variables: ["local_var": .string("value")]
    )

    actionHandler.handle(
      divAction(
        logId: "log_id",
        typed: .divActionSetVariable(DivActionSetVariable(
          value: stringValue("new value"),
          variableName: .value("local_var")
        ))
      ),
      path: path,
      source: .tap,
      sender: nil
    )

    XCTAssertEqual(
      "new value",
      variablesStorage.getVariableValue(path: path, name: "local_var")
    )
  }

  func test_ActionWithScopeId() {
    let actionPath = cardId.path + "element_with_action_id"
    let path = cardId.path + "element_id"
    variablesStorage.initializeIfNeeded(
      path: path,
      variables: ["local_var": .string("value")]
    )

    actionHandler.handle(
      divAction(
        logId: "log_id",
        scopeId: "element_id",
        typed: .divActionSetVariable(DivActionSetVariable(
          value: stringValue("new value"),
          variableName: .value("local_var")
        ))
      ),
      path: actionPath,
      source: .tap,
      sender: nil
    )

    XCTAssertEqual(
      "new value",
      variablesStorage.getVariableValue(path: path, name: "local_var")
    )
  }

  func test_ActionIsReported() {
    handle(
      DivAction(
        logId: .value("test_log_id"),
        logUrl: .value(url("https://some.log.url")),
        payload: ["key": "value"],
        referer: .value(url("https://some.referer.url")),
        url: .value(url("https://some.url"))
      )
    )

    XCTAssertEqual(cardId, reporter.lastCardId)
    XCTAssertEqual("test_log_id", reporter.lastActionInfo?.logId)
    XCTAssertEqual(url("https://some.log.url"), reporter.lastActionInfo?.logUrl)
    XCTAssertEqual(url("https://some.referer.url"), reporter.lastActionInfo?.referer)
    XCTAssertEqual(["key": "value"], reporter.lastActionInfo?.payload as! [String: AnyHashable])
  }

  private func handle(
    _ action: DivActionBase,
    localValues: [String: AnyHashable] = [:]
  ) {
    actionHandler.handle(
      action,
      path: cardId.path,
      source: .tap,
      localValues: localValues,
      sender: nil
    )
  }

  private func handle(_ action: DivActionTyped) {
    actionHandler.handle(
      divAction(logId: "log_id", typed: action),
      path: cardId.path,
      source: .tap,
      sender: nil
    )
  }

  private func getVariableValue<T>(_ name: DivVariableName) -> T? {
    variablesStorage.getVariableValue(cardId: cardId, name: name)
  }

  private func setVariableValue(_ name: DivVariableName, _ value: DivVariableValue) {
    variablesStorage.set(cardId: cardId, variables: [name: value])
  }
}

private func stringValue(_ value: String) -> DivTypedValue {
  .stringValue(StringValue(value: .value(value)))
}

private let cardId: DivCardID = "test_card"

private final class MockReporter: DivReporter {
  private(set) var lastCardId: DivCardID?
  private(set) var lastActionInfo: DivActionInfo?
  private(set) var lastError: DivError?

  func reportAction(cardId: DivCardID, info: DivActionInfo) {
    lastCardId = cardId
    lastActionInfo = info
  }

  func reportError(cardId: DivCardID, error: DivError) {
    lastCardId = cardId
    lastError = error
  }
}
