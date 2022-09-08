import XCTest
import Serialization

final class SimplePropertiesTests: XCTestCase {
  func test_BooleanAsInt() throws {
    let entity = try readEntity("boolean_as_int")

    // TODO: boolean property represented by integer value must fail
    // XCTAssertNil(entity!.boolean!.rawValue!)

    XCTAssertTrue(entity!.boolean!.rawValue!)
  }

  func test_BooleanIntAsBoolean() throws {
    let entity = try readEntity("boolean_int_as_boolean")

    XCTAssertTrue(entity!.booleanInt!.rawValue!)
  }

  func test_BooleanIntAsInt() throws {
    let entity = try readEntity("boolean_int_as_int")

    XCTAssertTrue(entity!.booleanInt!.rawValue!)
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithSimpleProperties? {
  try readEntity(
    EntityWithSimplePropertiesTemplate.self,
    fileName: "simple_properties/\(fileName)"
  )
}
