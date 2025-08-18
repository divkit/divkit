import Serialization
import XCTest

final class SimplePropertiesTests: XCTestCase {
  func test_BooleanAsInt() throws {
    let entity = try readEntity("boolean_as_int")

    // TODO: boolean property represented by integer value must fail
    // XCTAssertNil(entity!.boolean!.rawValue!)

    XCTAssertEqual(entity!.boolean, .value(true))
  }

  func test_BooleanIntAsBoolean() throws {
    let entity = try readEntity("boolean_int_as_boolean")

    XCTAssertEqual(entity!.booleanInt, .value(true))
  }

  func test_BooleanIntAsInt() throws {
    let entity = try readEntity("boolean_int_as_int")

    XCTAssertEqual(entity!.booleanInt, .value(true))
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithSimpleProperties? {
  try readEntity(
    EntityWithSimplePropertiesTemplate.self,
    fileName: "simple_properties/\(fileName)"
  )
}
