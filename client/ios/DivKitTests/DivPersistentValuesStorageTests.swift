@testable import DivKit
import DivKitTestsSupport
import Foundation
import VGSL
import XCTest

final class DivPersistentValuesStorageTests: XCTestCase {
  private var storage: DivPersistentValuesStorage!
  private var currentTimestamp: Milliseconds = 0

  override func setUp() {
    let storageFileUrl = FileManager.default
      .urls(for: .applicationSupportDirectory, in: .userDomainMask)
      .first!
      .appendingPathComponent(DivPersistentValuesStorage.storageFileName)
    try? FileManager.default.removeItem(at: storageFileUrl)

    storage = makeStorage()
  }

  func test_get_UnknownValue_ReturnsNil() {
    XCTAssertNil(storage.get(name: "unknown_var"))
  }

  func test_set_StringValue() {
    storage.set(
      value: DivStoredValue(name: "var", value: "value", type: .string, lifetimeInSec: 1)
    )

    XCTAssertEqual(storage.get(name: "var"), "value")
  }

  func test_set_ColorValue() {
    storage.set(
      value: DivStoredValue(name: "var", value: "#FFFFFF", type: .color, lifetimeInSec: 1)
    )

    XCTAssertEqual(storage.get(name: "var"), color("#FFFFFF"))
  }

  func test_set_BooleanValue_false() {
    storage.set(
      value: DivStoredValue(name: "var", value: "false", type: .boolean, lifetimeInSec: 1)
    )

    XCTAssertEqual(storage.get(name: "var"), false)
  }

  func test_set_BooleanValue_true() {
    storage.set(
      value: DivStoredValue(name: "var", value: "true", type: .boolean, lifetimeInSec: 1)
    )

    XCTAssertEqual(storage.get(name: "var"), true)
  }

  func test_set_BooleanValue_1() {
    storage.set(
      value: DivStoredValue(name: "var", value: "1", type: .boolean, lifetimeInSec: 1)
    )

    XCTAssertEqual(storage.get(name: "var"), true)
  }

  func test_getSameValueInDay() {
    let value = DivStoredValue(name: "var", value: "value", type: .string, lifetimeInSec: 86400)
    storage.set(value: value)
    shiftTime(days: 0.99)
    XCTAssertEqual(
      storage.get(name: value.name),
      value.value
    )
  }

  func test_expiresStoredValue() {
    let value = DivStoredValue(name: "var", value: "value", type: .string, lifetimeInSec: 86400)
    storage.set(value: value)
    shiftTime(days: 1.01)
    let stored: String? = storage.get(name: value.name)
    XCTAssertNil(stored)
  }

  func test_getDifferentType_GetNil() {
    let value = DivStoredValue(name: "var", value: "value", type: .string, lifetimeInSec: 86400)
    storage.set(value: value)
    let stored: Int? = storage.get(name: value.name)
    XCTAssertNil(stored)
  }

  func test_getDoubleFromStoredInt_GetNil() {
    let value = DivStoredValue(name: "var", value: "10", type: .integer, lifetimeInSec: 86400)
    storage.set(value: value)
    let stored: Double? = storage.get(name: value.name)
    XCTAssertNil(stored)
  }

  private func makeStorage() -> DivPersistentValuesStorage {
    DivPersistentValuesStorage(
      timestampProvider: Variable { self.currentTimestamp }
    )
  }

  private func shiftTime(days: Float) {
    currentTimestamp += Milliseconds(days * 86_400_000)
  }
}
