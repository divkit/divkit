// Copyright 2018 Yandex LLC. All rights reserved.

import XCTest

final class StringEnumPropertyTests: XCTestCase {
  func test_StringEnumProperty_WithLink() throws {
    let entity = try readEntity(
      EntityWithStringEnumPropertyTemplate.self,
      fileName: "test_string_enum_property_with_link"
    )

    XCTAssertEqual(entity, Expected.withStringEnumProperty)
  }

  func test_StringEnumProperty_WithoutLink() throws {
    let entity = try readEntity(
      EntityWithStringEnumPropertyTemplate.self,
      fileName: "test_string_enum_property_without_link"
    )

    XCTAssertEqual(entity, Expected.withStringEnumProperty)
  }

  func test_WhenStringEnumPropertyIsMissing_WithLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithStringEnumPropertyTemplate.self,
      fileName: "test_string_enum_property_with_link_missing_data"
    )

    XCTAssertNil(entity)
  }

  func test_WhenStringEnumPropertyIsMissing_WithoutLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithStringEnumPropertyTemplate.self,
      fileName: "test_string_enum_property_without_link_missing_data"
    )

    XCTAssertNil(entity)
  }

  func test_StringEnumProperty_WithOverride() throws {
    let entity = try readEntity(
      EntityWithStringEnumPropertyTemplate.self,
      fileName: "test_string_enum_property_with_override"
    )

    XCTAssertEqual(entity, Expected.withStringEnumProperty)
  }

  func test_StringEnumProperty_NotTemplated() throws {
    let entity = try readEntity(
      EntityWithStringEnumPropertyTemplate.self,
      fileName: "test_string_enum_property_not_templated"
    )

    XCTAssertEqual(entity, Expected.withStringEnumProperty)
  }
}
