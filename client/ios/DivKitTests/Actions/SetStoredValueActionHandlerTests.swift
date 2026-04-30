@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import XCTest

final class SetStoredValueActionHandlerTests: XCTestCase {
  private var handler: DivActionHandler!
  private let persistentValuesStorage = DivPersistentValuesStorage()

  override func setUp() {
    handler = DivActionHandler(
      persistentValuesStorage: persistentValuesStorage
    )
  }

  override func tearDown() {
    persistentValuesStorage.clear()
  }

  func test_SetStringValue() {
    handle(
      action(
        name: "name",
        value: .value("string value")
      )
    )

    XCTAssertEqual(persistentValuesStorage.get(name: "name"), "string value")
  }

  func test_SetValue_WithZeroLifetime_RemovesValue() {
    handle(
      action(
        name: "name",
        value: .value("new value"),
        lifetime: 0
      )
    )

    XCTAssertNil(persistentValuesStorage.get(name: "name"))
  }

  func test_SetValue_WithExpression() {
    handle(
      action(
        name: "name",
        value: expression("@{'string value'}")
      )
    )

    XCTAssertEqual(persistentValuesStorage.get(name: "name"), "string value")
  }

  func test_SetBooleanValue() {
    handle(
      action(
        name: "name",
        value: .booleanValue(BooleanValue(value: .value(true)))
      )
    )

    XCTAssertEqual(persistentValuesStorage.get(name: "name"), true)
  }

  func test_SetColorValue() {
    handle(
      action(
        name: "name",
        value: .colorValue(ColorValue(value: .value(color("#112233"))))
      )
    )

    XCTAssertEqual(persistentValuesStorage.get(name: "name"), color("#112233"))
  }

  func test_SetIntegerValue() {
    handle(
      action(
        name: "name",
        value: .integerValue(IntegerValue(value: .value(123)))
      )
    )

    XCTAssertEqual(persistentValuesStorage.get(name: "name"), 123)
  }

  func test_SetNumberValue() {
    handle(
      action(
        name: "name",
        value: .numberValue(NumberValue(value: .value(123.45)))
      )
    )

    XCTAssertEqual(persistentValuesStorage.get(name: "name"), 123.45)
  }

  func test_SetUrlValue() {
    handle(
      action(
        name: "name",
        value: .urlValue(UrlValue(value: .value(url("https://some.url"))))
      )
    )

    XCTAssertEqual(persistentValuesStorage.get(name: "name"), url("https://some.url"))
  }

  func test_SetArrayValue() {
    handle(
      action(
        name: "name",
        value: .arrayValue(ArrayValue(value: .value(["some value", 123])))
      )
    )

    XCTAssertEqual(
      persistentValuesStorage.get(name: "name"),
      ["some value", 123] as DivArray
    )
  }

  func test_SetDictValue() {
    handle(
      action(
        name: "name",
        value: .dictValue(DictValue(value: .value(["key": "value"])))
      )
    )

    XCTAssertEqual(
      persistentValuesStorage.get(name: "name"),
      ["key": "value"] as DivDictionary
    )
  }

  func test_SetCardScopedValue() {
    handle(
      action(
        name: "name",
        value: .stringValue(StringValue(value: .value("card value"))),
        scope: .card
      )
    )

    XCTAssertEqual(
      persistentValuesStorage.get(name: "name", cardId: cardId),
      "card value"
    )
    XCTAssertNil(persistentValuesStorage.get(name: "name", cardId: nil))
  }

  func test_SetCardScopedValue_IsolatedPerPathCardId() {
    let otherCard = DivCardID(rawValue: "other_stored_card")
    let setName = "per_path_key"
    handle(
      action(
        name: setName,
        value: .stringValue(StringValue(value: .value("from_default"))),
        scope: .card
      ),
      path: cardId.path
    )
    handle(
      action(
        name: setName,
        value: .stringValue(StringValue(value: .value("from_other"))),
        scope: .card
      ),
      path: otherCard.path
    )

    XCTAssertEqual(
      persistentValuesStorage.get(name: setName, cardId: cardId) as String?,
      "from_default"
    )
    XCTAssertEqual(
      persistentValuesStorage.get(name: setName, cardId: otherCard) as String?,
      "from_other"
    )
    XCTAssertNil(persistentValuesStorage.get(name: setName, cardId: nil) as String?)
  }

  private func handle(_ action: DivActionSetStoredValue, path: UIElementPath = cardId.path) {
    handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionSetStoredValue(action)
      ),
      path: path,
      source: .callback,
      sender: nil
    )
  }
}

private let cardId = DivBlockModelingContext.testCardId

private func action(
  name: String,
  value: DivTypedValue,
  lifetime: Int = 1000,
  scope: DivStoredValueScope? = nil
) -> DivActionSetStoredValue {
  DivActionSetStoredValue(
    lifetime: .value(lifetime),
    name: .value(name),
    value: value,
    scope: scope.map {
      switch $0 {
      case .global:
        return .value("global")
      case .card:
        return .value("card")
      }
    }
  )
}

private func action(
  name: String,
  value: DivKit.Expression<String>,
  lifetime: Int = 1000
) -> DivActionSetStoredValue {
  action(
    name: name,
    value: .stringValue(StringValue(value: value)),
    lifetime: lifetime
  )
}
