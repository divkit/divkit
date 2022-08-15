import XCTest

final class StrictArrayTests: XCTestCase {
  func test_WhenReceivesStrictArrayWithUnparseableItemInData_FailsParsing() throws {
    let entity = try readEntity(
      EntityWithStrictArrayTemplate.self,
      fileName: "test_strict_array_with_non_object_item"
    )

    XCTAssertNil(entity)
  }

  func test_WhenReceivesStrictArrayWithUnparseableItemInLink_FailsParsing() throws {
    let entity = try readEntity(
      EntityWithStrictArrayTemplate.self,
      fileName: "test_referenced_strict_array_with_non_object_item"
    )

    XCTAssertNil(entity)
  }

  func test_WhenReceivesStrictArrayWithUnresolvableItemInData_FailsParsing() throws {
    let entity = try readEntity(
      EntityWithStrictArrayTemplate.self,
      fileName: "test_strict_array_with_non_resolvable_item"
    )

    XCTAssertNil(entity)
  }

  func test_WhenReceivesStrictArrayWithUnresolvableTemplatedItemInData_FailsParsing() throws {
    let entity = try readEntity(
      EntityWithStrictArrayTemplate.self,
      fileName: "test_strict_array_with_non_object_item_ref"
    )

    XCTAssertNil(entity)
  }

  func test_WhenReceivesStrictArrayWithUnresolvableItemInLink_FailsParsing() throws {
    let entity = try readEntity(
      EntityWithStrictArrayTemplate.self,
      fileName: "test_referenced_strict_array_with_non_resolvable_item"
    )

    XCTAssertNil(entity)
  }

  func test_WhenReceivesStrictArrayWithUnresolvableTemplatedItemInLink_FailsParsing() throws {
    let entity = try readEntity(
      EntityWithStrictArrayTemplate.self,
      fileName: "test_strict_array_with_non_resolvable_item_ref"
    )

    XCTAssertNil(entity)
  }

  func test_WhenReceivesStrictArrayWithValidItemsInData_ParsesSuccessfully() throws {
    let entity = try readEntity(
      EntityWithStrictArrayTemplate.self,
      fileName: "test_strict_array_happy_case"
    )

    XCTAssertEqual(entity, Expected.withStrictArray)
  }

  func test_WhenReceivesStrictArrayWithValidItemsInLink_ParsesSuccessfully() throws {
    let entity = try readEntity(
      EntityWithStrictArrayTemplate.self,
      fileName: "test_strict_array_happy_case_referenced"
    )

    XCTAssertEqual(entity, Expected.withStrictArray)
  }
}
