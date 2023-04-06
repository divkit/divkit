import Serialization
import XCTest

final class PropertyTests: XCTestCase {
  func test_RequiredProperty_WithLink() throws {
    let entity = try readEntity("test_property_with_link")

    XCTAssertEqual(entity, ExpectedEntities.withRequiredProperty)
  }

  func test_RequiredProperty_WithoutLink() throws {
    let entity = try readEntity("test_property_without_link")

    XCTAssertEqual(entity, ExpectedEntities.withRequiredProperty)
  }

  func test_Property_WithLinkToLink() throws {
    let entity = try readEntity("test_property_with_link_to_link")

    // DIVKIT-54: link to link is not supported
    // XCTAssertEqual(entity, Expected.withRequiredProperty)
    XCTAssertNil(entity)
  }

  func test_Property_WithLinkValueInTemplate() throws {
    let entity = try readEntity("test_property_with_link_value_in_template")

    // DIVKIT-1130: not supported
    // XCTAssertEqual(entity, Expected.withRequiredProperty)
    XCTAssertNil(entity)
  }

  func test_WhenRequiredPropertyIsMissing_WithLink_NoValue() throws {
    let entity = try readEntity("test_property_with_link_missing_data")

    XCTAssertNil(entity)
  }

  func test_WhenRequiredPropertyIsMissing_WithoutLink_NoValue() throws {
    let entity = try readEntity("test_property_without_link_missing_data")

    XCTAssertNil(entity)
  }

  func test_RequiredProperty_WithOverride() throws {
    let entity = try readEntity("test_property_with_override")

    XCTAssertEqual(entity, ExpectedEntities.withRequiredProperty)
  }

  func test_RequiredProperty_WithOverrideInTemplate() throws {
    let entity = try readEntity("test_property_with_override_in_template")

    XCTAssertEqual(entity, ExpectedEntities.withRequiredProperty)
  }

  func test_RequiredProperty_NotTemplated() throws {
    let entity = try readEntity("test_property_not_templated")

    XCTAssertEqual(entity, ExpectedEntities.withRequiredProperty)
  }

  func test_WhenRequiredPropertyIsInvalid_WithLink_NoValue() throws {
    let entity = try readEntity("test_property_with_link_invalid")

    XCTAssertNil(entity)
  }

  func test_WhenRequiredPropertyIsInvalid_WithoutLink_NoValue() throws {
    _ = try readEntity("test_property_without_link_invalid")

    // TODO: template validation is broken (DIVKIT-402)
    // XCTAssertNil(entity)
  }

  func test_RequiredComplexPropertyIsInvalid_ReturnsError() throws {
    let result = try readEntityWithResult(fileName: "property/test_property_not_templated_invalid")

    switch result {
    case let .failure(errors):
      XCTAssertEqual(errors.count, 2)
    case .partialSuccess, .noValue, .success:
      XCTFail("Error expeced")
    }
  }

  func test_EntityTypeIsUnknown_ReturnsError() throws {
    let result = try readEntityWithResult(fileName: "property/test_property_unknown_type")

    switch result {
    case let .failure(errors):
      XCTAssertEqual(errors.count, 1)
    case .partialSuccess, .noValue, .success:
      XCTFail("Error expeced")
    }
  }

  func test_TemplateTypeIsUnknown_ThrowsError() throws {
    XCTAssertThrowsError(
      try readEntity("test_property_template_unknown_type")
    )
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithRequiredProperty? {
  try readEntity(
    EntityWithRequiredPropertyTemplate.self,
    fileName: "property/\(fileName)"
  )
}
