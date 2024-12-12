@testable @_spi(Internal) import DivKit
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
    persistentValuesStorage.reset()
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

  func test_SetArrayValue_DoesNothing() {
    handle(
      action(
        name: "name",
        value: .arrayValue(ArrayValue(value: .value(["some value", 123])))
      )
    )

    XCTAssertNil(persistentValuesStorage.get(name: "name"))
  }

  func test_SetDictValue_DoesNothing() {
    handle(
      action(
        name: "name",
        value: .dictValue(DictValue(value: ["key": "value"]))
      )
    )

    XCTAssertNil(persistentValuesStorage.get(name: "name"))
  }

  private func handle(_ action: DivActionSetStoredValue) {
    handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionSetStoredValue(action)
      ),
      path: cardId.path,
      source: .callback,
      sender: nil
    )
  }
}

private let cardId = DivBlockModelingContext.testCardId

private func action(
  name: String,
  value: DivTypedValue,
  lifetime: Int = 1000
) -> DivActionSetStoredValue {
  DivActionSetStoredValue(
    lifetime: .value(lifetime),
    name: .value(name),
    value: value
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
