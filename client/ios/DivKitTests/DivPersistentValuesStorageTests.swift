@testable import DivKit

import Foundation
import XCTest

import BasePublic

class DivPersistentValuesStorageTests: XCTestCase {
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

  func test_setValue_GetSameValue() {
    let value = DivStoredValue(name: "var", value: "value", type: .string, lifetimeInSec: 1)
    storage.set(value: value)
    XCTAssertEqual(
      storage.get(name: value.name),
      value.value
    )
  }

  func test_setColorValue_GetSameValue() {
    let value = DivStoredValue(name: "var", value: "#FFFFFF", type: .color, lifetimeInSec: 1)
    storage.set(value: value)
    XCTAssertEqual(
      storage.get(name: value.name),
      Color.color(withHexString: value.value)
    )
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
    currentTimestamp += Milliseconds(days * 86400000)
  }
}
