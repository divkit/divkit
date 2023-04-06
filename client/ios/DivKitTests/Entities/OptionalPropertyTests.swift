import XCTest

final class OptionalPropertyTests: XCTestCase {
  func test_OptionalProperty_WithLink() throws {
    let entity = try readEntity("test_optional_property_with_link")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalProperty)
  }

  func test_OptionalProperty_WithoutLink() throws {
    let entity = try readEntity("test_optional_property_without_link")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalProperty)
  }

  func test_WhenOptionalPropertyIsMissing_WithLink_BuildsEntity() throws {
    let entity = try readEntity("test_optional_property_with_link_missing_data")

    XCTAssertEqual(entity, ExpectedEntities.withMissingOptionalProperty)
  }

  func test_WhenOptionalPropertyIsMissing_WithoutLink_BuildsEntity() throws {
    let entity = try readEntity("test_optional_property_without_link_missing_data")

    XCTAssertEqual(entity, ExpectedEntities.withMissingOptionalProperty)
  }

  func test_OptionalProperty_WithOverride() throws {
    let entity = try readEntity("test_optional_property_with_override")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalProperty)
  }

  func test_OptionalProperty_WithOverrideInTemplate() throws {
    let entity = try readEntity("test_optional_property_with_override_in_template")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalProperty)
  }

  func test_OptionalProperty_NotTemplated() throws {
    let entity = try readEntity("test_optional_property_not_templated")

    XCTAssertEqual(entity, ExpectedEntities.withOptionalProperty)
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithOptionalProperty? {
  try readEntity(
    EntityWithOptionalPropertyTemplate.self,
    fileName: "optional_property/\(fileName)"
  )
}
