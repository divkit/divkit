import XCTest

final class OptionalOptionalComplexPropertyTests: XCTestCase {
  func test_OptionalComplexProperty_WithLink() throws {
    let entity = try readEntity("test_optional_complex_property_with_link")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalComplexProperty)
  }

  func test_OptionalComplexProperty_WithoutLink() throws {
    let entity = try readEntity("test_optional_complex_property_without_link")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalComplexProperty)
  }

  func test_OptionalComplexProperty_WithInternalLink() throws {
    let entity = try readEntity("test_optional_complex_property_with_internal_link")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalComplexProperty)
  }

  func test_OptionalComplexProperty_WithInternalLinkOverrideInTemplate() throws {
    let entity =
      try readEntity("test_optional_complex_property_with_internal_link_override_in_template")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalComplexProperty)
  }

  func test_WhenOptionalComplexPropertyIsMissing_WithLink_BuildsEntity() throws {
    let entity = try readEntity("test_optional_complex_property_with_link_missing_data")

    XCTAssertEqual(entity, ExpectedEntities.withMissingOptionalComplexProperty)
  }

  func test_WhenOptionalComplexPropertyIsMissing_WithoutLink_BuildsEntity() throws {
    let entity = try readEntity("test_optional_complex_property_without_link_missing_data")

    XCTAssertEqual(entity, ExpectedEntities.withMissingOptionalComplexProperty)
  }

  func test_WhenOptionalComplexPropertyIsMissing_WithInternalLink_BuildsEntity() throws {
    let entity = try readEntity("test_optional_complex_property_with_internal_link_missing_data")

    XCTAssertEqual(entity, ExpectedEntities.withMissingOptionalComplexProperty)
  }

  func test_OptionalComplexProperty_NotTemplated() throws {
    let entity = try readEntity("test_optional_complex_property_not_templated")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalComplexProperty)
  }

  func test_OptionalComplexProperty_WithOverrideInTemplate() throws {
    let entity = try readEntity("test_optional_complex_property_with_override_in_template")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalComplexProperty)
  }

  func test_InvalidOptionalComplexProperty_NotTemplated() throws {
    let entity = try readEntity("test_optional_invalid_complex_property_not_templated")

    XCTAssertEqual(entity, ExpectedEntities.withMissingOptionalComplexProperty)
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithOptionalComplexProperty? {
  try readEntity(
    EntityWithOptionalComplexPropertyTemplate.self,
    fileName: "optional_complex_property/\(fileName)"
  )
}
