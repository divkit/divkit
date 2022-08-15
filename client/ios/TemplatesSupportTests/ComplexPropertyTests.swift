import XCTest

final class ComplexPropertyTests: XCTestCase {
  func test_RequiredComplexProperty_WithLink() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_with_link"
    )

    XCTAssertEqual(entity, Expected.withRequiredComplexProperty)
  }

  func test_RequiredComplexProperty_WithoutLink() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_without_link"
    )

    XCTAssertEqual(entity, Expected.withRequiredComplexProperty)
  }

  func test_RequiredComplexProperty_WithInternalLink() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_with_internal_link"
    )

    XCTAssertEqual(entity, Expected.withRequiredComplexProperty)
  }

  func test_ComplexProperty_WithLinkToInternalLink() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_with_link_to_internal_link"
    )

    // DIVKIT-54: link to link is not supported
    // XCTAssertEqual(entity, Expected.withRequiredComplexProperty)
    XCTAssertNil(entity)
  }

  func test_RequiredComplexProperty_WithInternalLinkOverrideInTemplate() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_with_internal_link_override_in_template"
    )

    XCTAssertEqual(entity, Expected.withRequiredComplexProperty)
  }

  func test_WhenRequiredComplexPropertyIsMissing_WithLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_with_link_missing_data"
    )

    XCTAssertNil(entity)
  }

  func test_WhenRequiredComplexPropertyIsMissing_WithoutLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_without_link_missing_data"
    )

    XCTAssertNil(entity)
  }

  func test_WhenRequiredComplexPropertyIsMissing_WithInternalLink_NoValue() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_with_internal_link_missing_data"
    )

    XCTAssertNil(entity)
  }

  func test_RequiredComplexProperty_NotTemplated() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_not_templated"
    )

    XCTAssertEqual(entity, Expected.withRequiredComplexProperty)
  }

  func test_RequiredComplexProperty_WithOverrideInTemplate() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_complex_property_with_override_in_template"
    )

    XCTAssertEqual(entity, Expected.withRequiredComplexProperty)
  }

  func test_InvalidRequiredComplexProperty_NoValue() throws {
    let entity = try readEntity(
      EntityWithComplexPropertyTemplate.self,
      fileName: "test_invalid_complex_property_not_templated"
    )

    XCTAssertNil(entity)
  }
}
