// Copyright 2018 Yandex LLC. All rights reserved.

import XCTest

final class ArrayWithTransformTests: XCTestCase {
  func test_WhenArrayWithTransformHasInvalidItems_SkipsThem() throws {
    let entity = try readEntity(
      EntityWithArrayWithTransformTemplate.self,
      fileName: "test_array_with_transform_one_invalid_item"
    )

    XCTAssertEqual(entity, Expected.withArrayWithTransform)
  }

  func test_WhenArrayWithTransformHasInvalidItems_NotTemplated_SkipsThem() throws {
    let entity = try readEntity(
      EntityWithArrayWithTransformTemplate.self,
      fileName: "test_array_with_transform_not_templated_one_invalid_item"
    )

    XCTAssertEqual(entity, Expected.withArrayWithTransform)
  }

  func test_ArrayWithTransform_AllItemsAreInvalid_NotTemplated_NoValue() throws {
    let entity = try readEntity(
      EntityWithArrayWithTransformTemplate.self,
      fileName: "test_array_with_transform_not_templated_invalid_items"
    )

    XCTAssertNil(entity)
  }

  func test_ArrayWithTransform_AllItemsAreInvalid_NoValue() throws {
    let entity = try readEntity(
      EntityWithArrayWithTransformTemplate.self,
      fileName: "test_array_with_transform_invalid_items"
    )

    XCTAssertNil(entity)
  }
}
