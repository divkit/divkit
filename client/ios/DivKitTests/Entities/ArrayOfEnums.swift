import XCTest

final class ArrayOfEnumsTests: XCTestCase {
  func test_Empty() throws {
    let entity = try readEntity("empty")

    XCTAssertEqual(entity, nil)
  }

  func test_Simple() throws {
    let entity = try readEntity("simple")

    XCTAssertEqual(entity, entityWithTwoItems)
  }

  func test_SimpleTemplate() throws {
    let entity = try readEntity("simple_template")

    XCTAssertEqual(entity, entityWithTwoItems)
  }

  func test_InvalidItem() throws {
    let entity = try readEntity("invalid_item")

    XCTAssertEqual(entity, entityWithTwoItems)
  }

  func test_InvalidItemsType() throws {
    let entity = try readEntity("invalid_items_type")

    XCTAssertEqual(entity, nil)
  }

  func test_RecurringItems() throws {
    let entity = try readEntity("recurring_items")

    XCTAssertEqual(
      entity,
      EntityWithArrayOfEnums(
        items: [ .first, .second, .first ]
      )
    )
  }
}

private func readEntity(_ fileName: String) throws -> EntityWithArrayOfEnums? {
  try readEntity(
    EntityWithArrayOfEnumsTemplate.self,
    fileName: "array_of_enums/\(fileName)"
  )
}

private let entityWithTwoItems = EntityWithArrayOfEnums(
  items: [ .first, .second ]
)
