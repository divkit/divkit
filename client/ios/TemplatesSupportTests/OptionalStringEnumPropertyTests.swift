import XCTest

import TemplatesSupport

final class OptionalOptionalStringEnumPropertyTests: XCTestCase {
  func test_OptionalStringEnumProperty_WithLink() throws {
    let entity = try readEntity("test_optional_string_enum_property_with_link")

    XCTAssertEqual(entity, Expected.withOptionalStringEnumProperty)
  }

  func test_OptionalStringEnumProperty_WithoutLink() throws {
    let entity = try readEntity("test_optional_string_enum_property_without_link")

    XCTAssertEqual(entity, Expected.withOptionalStringEnumProperty)
  }

  func test_OptionalStringEnumPropertyIsMissing_WithLink() throws {
    let entity = try readEntity("test_optional_string_enum_property_with_link_missing_data")

    XCTAssertEqual(entity, Expected.withMissingStringEnumProperty)
  }

  func test_OptionalStringEnumPropertyIsMissing_WithoutLink() throws {
    let entity = try readEntity("test_optional_string_enum_property_without_link_missing_data")

    XCTAssertEqual(entity, Expected.withMissingStringEnumProperty)
  }

  func test_OptionalStringEnumProperty_WithOverride() throws {
    let entity = try readEntity("test_optional_string_enum_property_with_override")

    XCTAssertEqual(entity, Expected.withOptionalStringEnumProperty)
  }

  func test_OptionalStringEnumProperty_NotTemplated() throws {
    let entity = try readEntity("test_optional_string_enum_property_not_templated")

    XCTAssertEqual(entity, Expected.withOptionalStringEnumProperty)
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithOptionalStringEnumProperty? {
  try readEntity(
    EntityWithOptionalStringEnumPropertyTemplate.self,
    fileName: "optional_string_enum_property/\(fileName)"
  )
}
