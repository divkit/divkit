@testable import DivKit
import VGSL
import XCTest

final class DivSystemPropertyManagersTests: XCTestCase {
  private let globalStorage = DivVariableStorage()
  private let disposePool = AutodisposePool()

  private lazy var variablesStorage = DivVariablesStorage(outerStorage: globalStorage)

  private var event: DivVariablesStorage.ChangeEvent?

  override func setUp() {
    event = nil
    variablesStorage.addObserver { [unowned self] in self.event = $0 }
      .dispose(in: disposePool)
  }

  override func tearDown() {
    globalStorage.replaceAll([:])
    disposePool.drain()
  }

  func test_themeManager_setsVariable() {
    let themeManager = makeThemeManager()
    themeManager.setTheme(theme: .dark)

    XCTAssertEqual("dark", getVariable("theme"))
  }

  func test_themeManager_sendUpdateEvent() {
    let themeManager = makeThemeManager()
    XCTAssertNil(event)

    themeManager.setTheme(theme: .dark)
    XCTAssertNotNil(event)
  }

  func test_themeManager_overwritesPreviouslySetVariable() {
    let themeManager = makeThemeManager()
    themeManager.setTheme(theme: .dark)
    themeManager.setTheme(theme: .light)

    XCTAssertEqual("light", getVariable("theme"))
  }

  func test_themeManager_localVariableOverridesManagerSetVariable() {
    let themeManager = makeThemeManager()
    themeManager.setTheme(theme: .light)

    let newVariables: DivVariables = ["theme": .string("system")]
    variablesStorage.set(cardId: cardId, variables: newVariables)

    XCTAssertEqual("system", getVariable("theme"))
  }

  func test_themeManager_localVariableTakesPrecedenceOverManagerVariable() {
    let themeManager = makeThemeManager()

    let newVariables: DivVariables = ["theme": .string("system")]
    variablesStorage.set(cardId: cardId, variables: newVariables)

    themeManager.setTheme(theme: .light)

    XCTAssertEqual("system", getVariable("theme"))
  }

  func test_themeManager_globalVariableOverridesManagerSetVariable() {
    let themeManager = makeThemeManager()
    themeManager.setTheme(theme: .light)

    let newVariables: DivVariables = ["theme": .string("system")]
    globalStorage.put(newVariables)

    XCTAssertEqual("system", getVariable("theme"))
  }

  func test_themeManager_globalVariableTakesPrecedenceOverManagerVariable() {
    let themeManager = makeThemeManager()

    let newVariables: DivVariables = ["theme": .string("system")]
    globalStorage.put(newVariables)

    themeManager.setTheme(theme: .light)
    XCTAssertEqual("system", getVariable("theme"))

    globalStorage.replaceAll([:])
    XCTAssertEqual("light", getVariable("theme"))
  }

  func test_safeAreaManager_setsVariables() {
    let manager = makeSafeAreaManager()
    let insets = EdgeInsets(top: 10, left: 5, bottom: 20, right: 15)

    manager.setEdgeInsets(insets)

    XCTAssertEqual(10.0, getVariable("safe_area_top"))
    XCTAssertEqual(20.0, getVariable("safe_area_bottom"))
    XCTAssertEqual(5.0, getVariable("safe_area_left"))
    XCTAssertEqual(15.0, getVariable("safe_area_right"))
  }

  func test_safeAreaManager_sendUpdateEvent() {
    let manager = makeSafeAreaManager()
    XCTAssertNil(event)

    let insets = EdgeInsets(top: 1, left: 1, bottom: 1, right: 1)
    manager.setEdgeInsets(insets)

    XCTAssertNotNil(event)
  }

  func test_safeAreaManager_overwritesPreviouslySetVariables() {
    let manager = makeSafeAreaManager()

    manager.setEdgeInsets(EdgeInsets(top: 10, left: 5, bottom: 20, right: 15))
    manager.setEdgeInsets(EdgeInsets(top: 7, left: 9, bottom: 8, right: 10))

    XCTAssertEqual(7.0, getVariable("safe_area_top"))
    XCTAssertEqual(8.0, getVariable("safe_area_bottom"))
    XCTAssertEqual(9.0, getVariable("safe_area_left"))
    XCTAssertEqual(10.0, getVariable("safe_area_right"))
  }

  func test_safeAreaManager_globalVariablesTakePrecedenceOverSafeAreaManager() {
    let manager = makeSafeAreaManager()

    let globalVars: DivVariables = [
      "safe_area_top": .number(100),
      "safe_area_bottom": .number(200),
      "safe_area_left": .number(300),
      "safe_area_right": .number(400),
    ]
    globalStorage.put(globalVars)

    manager.setEdgeInsets(EdgeInsets(top: 10, left: 30, bottom: 20, right: 40))

    XCTAssertEqual(100.0, getVariable("safe_area_top"))
    XCTAssertEqual(200.0, getVariable("safe_area_bottom"))
    XCTAssertEqual(300.0, getVariable("safe_area_left"))
    XCTAssertEqual(400.0, getVariable("safe_area_right"))

    globalStorage.replaceAll([:])

    XCTAssertEqual(10.0, getVariable("safe_area_top"))
    XCTAssertEqual(20.0, getVariable("safe_area_bottom"))
    XCTAssertEqual(30.0, getVariable("safe_area_left"))
    XCTAssertEqual(40.0, getVariable("safe_area_right"))
  }

  private func makeThemeManager() -> DivThemeManager {
    variablesStorage.createPropertiesManager()
  }

  private func makeSafeAreaManager() -> DivSafeAreaManager {
    variablesStorage.createPropertiesManager()
  }

  private func getVariable<T>(_ name: DivVariableName) -> T? {
    variablesStorage.getVariableValue(cardId: cardId, name: name)
  }
}

private let cardId = DivCardID(rawValue: "test_card")
