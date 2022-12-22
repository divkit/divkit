import XCTest

final class ArrayOfNestedItemsTests: XCTestCase {
  func test_ArrayOfNestedItems_WithTemplatedItem() throws {
    let entity = try readEntity("test_array_of_nested_items_with_item_template")

    XCTAssertEqual(entity, Expected.withArrayOfNestedItems)
  }

  func test_ArrayOfNestedItems_WithoutTemplates() throws {
    let entity = try readEntity("test_array_of_nested_items_without_templates")

    XCTAssertEqual(entity, Expected.withArrayOfNestedItems)
  }

  func test_ArrayOfNestedItems_WithLinkForItems() throws {
    let entity = try readEntity("test_array_of_nested_items_with_link_for_items")

    XCTAssertEqual(entity, Expected.withArrayOfNestedItems)
  }

  func test_ArrayOfNestedItems_WithLinkForItems_WithTemplatedItem() throws {
    let entity = try readEntity("test_array_of_nested_items_with_link_for_items_with_item_template")

    XCTAssertEqual(entity, Expected.withArrayOfNestedItems)
  }

  func test_ArrayOfNestedItems_WithoutRequiredItemProperty() throws {
    let entity = try readEntity("test_array_of_nested_items_without_item_required_property")

    XCTAssertNil(entity)
  }

  func test_ArrayOfNestedItems_WithLinkForItems_WithoutRequiredItemProperty() throws {
    let entity =
      try readEntity(
        "test_array_of_nested_items_with_link_for_items_without_item_required_property"
      )

    XCTAssertNil(entity)
  }

  func test_ArrayOfNestedItems_WithTemplatedItem_WithoutRequiredItemProperty() throws {
    let entity =
      try readEntity("test_array_of_nested_items_with_item_template_without_item_required_property")

    XCTAssertNil(entity)
  }

  func test_ArrayOfNestedItems_WithTemplatedItem_WithoutData() throws {
    let entity = try readEntity("test_array_of_nested_items_with_item_template_without_data")

    XCTAssertNil(entity)
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithArrayOfNestedItems? {
  try readEntity(
    EntityWithArrayOfNestedItemsTemplate.self,
    fileName: "array_of_nested_items/\(fileName)"
  )
}
