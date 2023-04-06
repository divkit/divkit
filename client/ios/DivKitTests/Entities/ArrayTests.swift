import XCTest

final class ArrayTests: XCTestCase {
  func test_Array_WithLink() throws {
    let entity = try readEntity("test_array_with_link")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_Array_WithoutLink() throws {
    let entity = try readEntity("test_array_without_link")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_Array_WithInternalLinks() throws {
    let entity = try readEntity("test_array_with_internal_links")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_ArrayWithComplexItems_WithInternalLinks() throws {
    let entity = try readEntity("test_array_with_complex_items_with_internal_links")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_ArrayWithTemplatedItems() throws {
    let entity = try readEntity("test_array_with_templated_items")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_ArrayWithOverrideInItems() throws {
    let entity = try readEntity("test_array_with_override_in_items")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_ArrayWithOverrideInTemplate() throws {
    let entity = try readEntity("test_array_with_override_in_template")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_Array_WithLinkToInternalLink() throws {
    let entity = try readEntity("test_array_with_link_to_internal_link")

    // DIVKIT-54: link to link is not supported
    // XCTAssertEqual(entity, Expected.withArray)
    XCTAssertNil(entity)
  }

  func test_WhenArrayHasConstraintOnMinNumberOfItems_AndIsEmpty_NoValue() throws {
    let entity = try readEntity("test_array_empty")

    XCTAssertNil(entity)
  }

  func test_WhenArrayHasConstraintOnMinNumberOfItems_AndItemTemplatesAreInvalid_NoValue() throws {
    let entity = try readEntity("test_array_invalid_items")

    XCTAssertNil(entity)
  }

  func test_WhenArrayHasConstraintOnMinNumberOfItems_AndItemsInDataAreInvalid_NoValue() throws {
    let entity = try readEntity("test_array_invalid_items_in_data")

    XCTAssertNil(entity)
  }

  func test_WhenArrayHasConstraintOnMinNumberOfItems_AndDataIsMissing_NoValue() throws {
    let entity = try readEntity("test_array_missing_data")

    XCTAssertNil(entity)
  }

  func test_WhenArrayHasInvalidItems_SkipsThem() throws {
    let entity = try readEntity("test_array_one_invalid_item")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_WhenArrayHasItemWithInternalLinkThatIsMissing_SkipsIt() throws {
    let entity = try readEntity("test_array_missing_one_link")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_Array_NotTemplated() throws {
    let entity = try readEntity("test_array_not_templated")

    XCTAssertEqual(entity, ExpectedEntities.withArray)
  }

  func test_HeterogeneousArray() throws {
    let entity = try readEntity("test_array_with_heterogeneous_items")

    XCTAssertEqual(entity, ExpectedEntities.withHeterogeneousArray)
  }

  func test_HeterogeneousArray_WithInternalLinks() throws {
    let entity = try readEntity("test_array_with_heterogeneous_items_with_internal_links")

    XCTAssertEqual(entity, ExpectedEntities.withHeterogeneousArray)
  }

  func test_NestedArray() throws {
    let entity = try readEntity("test_array_nested")

    XCTAssertEqual(entity, ExpectedEntities.withNestedArray)
  }

  func test_NestedArrayWithInternalLink() throws {
    let entity = try readEntity("test_array_nested_with_internal_link")

    XCTAssertEqual(entity, ExpectedEntities.withNestedArray)
  }

  func test_ArrayWithNestedTemplatedArray() throws {
    let entity = try readEntity("test_array_with_nested_templated_array")

    XCTAssertEqual(entity, ExpectedEntities.withNestedArray)
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithArray? {
  try readEntity(
    EntityWithArrayTemplate.self,
    fileName: "array/\(fileName)"
  )
}
