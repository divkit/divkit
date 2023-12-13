@testable import DivKit

import XCTest

final class PrototypesValueStorageTests: XCTestCase {
  let prototypesValueStorage = PrototypesValueStorage()

  func test_findValueInPrototypeStorage() throws {
    prototypesValueStorage.insert(prefix: "a.", data: prototypeData)
    prototypesValueStorage.insert(prefix: "b.", data: prototypeData)
    XCTAssertEqual(prototypesValueStorage.findValue(expression: "a.field"), "value")
    XCTAssertEqual(prototypesValueStorage.findValue(expression: "b.field"), "value")
  }

  func test_replacingDataWithSamePrototypePrefix() throws {
    let anotherPrototypeData: [String: AnyHashable] = [
      "anotherField": "anotherValue",
    ]
    prototypesValueStorage.insert(prefix: "a.", data: prototypeData)
    prototypesValueStorage.insert(prefix: "a.", data: anotherPrototypeData)
    XCTAssertEqual(prototypesValueStorage.findValue(expression: "a.anotherField"), "anotherValue")
    XCTAssertNil(prototypesValueStorage.findValue(expression: "a.field"))
  }

  func test_returnValueWithLongestPrototypePrefixInStringCollisionCase() throws {
    let dataA = [
      "bc": "value1",
    ]
    let dataAB = [
      "c": "value2",
    ]
    prototypesValueStorage.insert(prefix: "a", data: dataA)
    prototypesValueStorage.insert(prefix: "ab", data: dataAB)
    XCTAssertEqual(prototypesValueStorage.findValue(expression: "abc"), "value2")
  }
}

let prototypeData: [String: AnyHashable] = [
  "field": "value",
]
