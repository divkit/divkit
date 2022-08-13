// Copyright 2018 Yandex LLC. All rights reserved.

import XCTest

final class PropertyTests: XCTestCase {
  func test_RequiredProperty_WithLink() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_with_link"
    )

    XCTAssertEqual(entity, Expected.withRequiredProperty)
  }

  func test_RequiredProperty_WithoutLink() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_without_link"
    )

    XCTAssertEqual(entity, Expected.withRequiredProperty)
  }

  func test_Property_WithLinkToLink() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_with_link_to_link"
    )

    // DIVKIT-54: link to link is not supported
    // XCTAssertEqual(entity, Expected.withRequiredProperty)
    XCTAssertNil(entity)
  }

  func test_WhenRequiredPropertyIsMissing_WithLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_with_link_missing_data"
    )

    XCTAssertNil(entity)
  }

  func test_WhenRequiredPropertyIsMissing_WithoutLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_without_link_missing_data"
    )

    XCTAssertNil(entity)
  }

  func test_RequiredProperty_WithOverride() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_with_override"
    )

    XCTAssertEqual(entity, Expected.withRequiredProperty)
  }

  func test_RequiredProperty_WithOverrideInTemplate() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_with_override_in_template"
    )

    XCTAssertEqual(entity, Expected.withRequiredProperty)
  }

  func test_RequiredProperty_NotTemplated() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_not_templated"
    )

    XCTAssertEqual(entity, Expected.withRequiredProperty)
  }

  func test_WhenRequiredPropertyIsInvalid_WithLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_with_link_invalid"
    )

    XCTAssertNil(entity)
  }

  func test_WhenRequiredPropertyIsInvalid_WithoutLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithRequiredPropertyTemplate.self,
      fileName: "test_property_without_link_invalid"
    )

    XCTAssertNil(entity)
  }

  func test_WhenRequiredPropertyIsInvalid_NotTemplated_ThrowsError() throws {
    XCTAssertThrowsError(
      try readEntity(
        EntityWithRequiredPropertyTemplate.self,
        fileName: "test_property_not_templated_invalid"
      )
    )
  }

  func test_WhenRequiredPropertyIsInvalid_WithUnknownType_ThrowsError() throws {
    XCTAssertThrowsError(
      try readEntity(
        EntityWithRequiredPropertyTemplate.self,
        fileName: "test_property_unknown_type"
      )
    )
  }

  func test_WhenRequiredPropertyIsInvalid_TemplatedWithUnknownType_ThrowsError() throws {
    XCTAssertThrowsError(
      try readEntity(
        EntityWithRequiredPropertyTemplate.self,
        fileName: "test_property_template_unknown_type"
      )
    )
  }
}
