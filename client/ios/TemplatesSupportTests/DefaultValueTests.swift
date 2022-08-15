import XCTest

final class DefaultValueTests: XCTestCase {
  func test_WhenOptionalPropertyHasInvalidValuePresentedDirectly_ItFallsBackToDefault() throws {
    let entity = try readEntity(
      EntityWithPropertyWithDefaultValueTemplate.self,
      fileName: "test_invalid_value_presented_directly"
    )

    XCTAssertEqual(entity, Expected.withDefaultValue)
  }

  func test_WhenOptionalPropertyHasInvalidValueReferencedByLink_ItFallsBackToDefault() throws {
    let entity = try readEntity(
      EntityWithPropertyWithDefaultValueTemplate.self,
      fileName: "test_invalid_value_referenced_by_link"
    )

    XCTAssertEqual(entity, Expected.withDefaultValue)
  }
}
