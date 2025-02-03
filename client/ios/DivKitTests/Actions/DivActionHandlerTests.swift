@testable @_spi(Internal) import DivKit
import LayoutKit
import XCTest

final class DivActionHandlerTests: XCTestCase {
  private var flags: DivFlagsInfo = .default
  private var patchProvider = MockPatchProvider()
  private let reporter = MockReporter()
  private let stateManagement = DefaultDivStateManagement()
  private let variablesStorage = DivVariablesStorage()

  private lazy var actionHandler: DivActionHandler! = {
    let idToPath = IdToPath()
    idToPath[cardId.path + "element_id"] = cardId.path + "element_id"
    return DivActionHandler(
      flags: flags,
      idToPath: idToPath,
      patchProvider: patchProvider,
      reporter: reporter,
      stateManagement: stateManagement,
      updateCard: { self.lastUpdateReason = $0 },
      urlHandler: DivUrlHandlerDelegate { url, _ in
        self.handledUrl = url
      },
      variablesStorage: variablesStorage
    )
  }()

  private var handledUrl: URL?
  private var lastUpdateReason: DivCardUpdateReason?

  func test_UrlPassedToUrlHandler() {
    handle(
      divAction(url: "https://some.url")
    )

    XCTAssertEqual(url("https://some.url"), handledUrl)
  }

  func test_UrlNotPassedToUrlHandler_VisibilityAction() {
    handle(
      divAction(url: "https://some.url"),
      source: .visibility
    )

    XCTAssertNil(handledUrl)
  }

  func test_UrlNotPassedToUrlHandler_DisappearAction() {
    handle(
      divAction(url: "https://some.url"),
      source: .disappear
    )

    XCTAssertNil(handledUrl)
  }

  func test_UrlPassedToUrlHandler_VisibilityAction_UseUrlHandlerFlagEnabled() {
    flags = DivFlagsInfo(useUrlHandlerForVisibilityActions: true)

    handle(
      divAction(url: "https://some.url"),
      source: .visibility
    )

    XCTAssertEqual(url("https://some.url"), handledUrl)
  }

  func test_UrlNotPassedToUrlHandler_IfTypedActionHandled() {
    handle(
      divAction(
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
      divAction(urlExpression: "https://@{host}")
    )

    XCTAssertEqual(url("https://test.url"), handledUrl)
  }

  func test_UrlWithExpressionWithLocalValues() {
    variablesStorage.set(cardId: cardId, variables: ["host": .string("test.url")])

    handle(
      divAction(urlExpression: "https://@{host}"),
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

    handle(
      divAction(
        typed: .divActionDictSetValue(DivActionDictSetValue(
          key: .value("key"),
          value: .dictValue(DictValue(value: ["new_key": "new value"])),
          variableName: .value("dict_var")
        ))
      ),
      path: parentPath + "element_id"
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

  func test_DownloadAction_updateCardIsCalled() {
    let expectedPatch = DivPatch(
      changes: [
        DivPatch.Change(id: "id", items: [ divText() ])
      ]
    )
    patchProvider = MockPatchProvider {
      $0(.success(expectedPatch))
    }

    handle(.divActionDownload(
      DivActionDownload(
        onSuccessActions: [
          divAction(url: "result://success"),
        ],
        url: .value(url("https://download.url"))
      )
    ))

    switch lastUpdateReason {
    case let .patch(cardId, patch):
      XCTAssertEqual("test_card", cardId)
      XCTAssertEqual(expectedPatch, patch)
    default:
      XCTFail("UpdateReason.patch expected")
    }
  }

  func test_DownloadAction_onSuccessActionIsCalled() {
    patchProvider = MockPatchProvider {
      $0(.success(DivPatch(changes: [])))
    }

    handle(.divActionDownload(
      DivActionDownload(
        onSuccessActions: [
          divAction(url: "result://success"),
        ],
        url: .value(url("https://download.url"))
      )
    ))

    XCTAssertEqual(url("result://success"), handledUrl)
  }

  func test_DownloadAction_onFailActionIsCalled() {
    patchProvider = MockPatchProvider {
      $0(.failure(ExpressionError("Error")))
    }

    handle(.divActionDownload(
      DivActionDownload(
        onFailActions: [
          divAction(url: "result://error"),
        ],
        url: .value(url("https://download.url"))
      )
    ))

    XCTAssertEqual(url("result://error"), handledUrl)
  }

  func test_SetStateAction_SetsState() {
    handle(
      divAction(
        typed: .divActionSetState(DivActionSetState(
          stateId: .value("0/div_state/state1")
        ))
      )
    )

    let item = stateManagement
      .getStateManagerForCard(cardId: cardId)
      .get(stateBlockPath: .makeDivStatePath(from: "0/div_state"))
    XCTAssertEqual("state1", item?.currentStateID)
  }

  func test_SetStateAction_InTooltip_SetsMainCardState() {
    handle(
      divAction(
        typed: .divActionSetState(DivActionSetState(
          stateId: .value("0/div_state/state1")
        ))
      ),
      path: cardId.path + "tooltip" + "element_id"
    )

    let item = stateManagement
      .getStateManagerForCard(cardId: cardId)
      .get(stateBlockPath: .makeDivStatePath(from: "0/div_state"))
    XCTAssertEqual("state1", item?.currentStateID)
  }

  func test_SetStateAction_InTooltip_SetsTooltipState() {
    handle(
      divAction(
        typed: .divActionSetState(DivActionSetState(
          stateId: .value("div_state_in_tooltip/state1")
        ))
      ),
      path: cardId.path + "tooltip" + "element_id"
    )

    let item = stateManagement
      .getStateManagerForCard(cardId: cardId)
      .get(stateBlockPath: .makeDivStatePath(from: "tooltip/0/div_state_in_tooltip"))
    XCTAssertEqual("state1", item?.currentStateID)
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

    handle(
      divAction(
        typed: .divActionSetVariable(DivActionSetVariable(
          value: stringValue("new value"),
          variableName: .value("local_var")
        ))
      ),
      path: path
    )

    XCTAssertEqual(
      "new value",
      variablesStorage.getVariableValue(path: path, name: "local_var")
    )
  }

  func test_ActionWithScopeId() {
    let path = cardId.path + "element_id"
    variablesStorage.initializeIfNeeded(
      path: path,
      variables: ["local_var": .string("value")]
    )

    handle(
      divAction(
        scopeId: "element_id",
        typed: .divActionSetVariable(DivActionSetVariable(
          value: stringValue("new value"),
          variableName: .value("local_var")
        ))
      ),
      path: cardId.path + "element_with_action_id"
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
    path: UIElementPath = cardId.path,
    source: UserInterfaceAction.DivActionSource = .tap,
    localValues: [String: AnyHashable] = [:]
  ) {
    actionHandler.handle(
      action,
      path: path,
      source: source,
      localValues: localValues,
      sender: nil
    )
  }

  private func handle(_ action: DivActionTyped) {
    actionHandler.handle(
      divAction(typed: action),
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
