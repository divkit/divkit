import Serialization
import XCTest

final class DictionarySerializationBehaviorTests: XCTestCase {
  func testRequiredField_WhenMissing_ThrowsNoData() {
    let dictionary: [String: Any] = [:]

    XCTAssertThrowsError(try dictionary.getField("value") as String) { error in
      guard case .noData = error as? DeserializationError else {
        return XCTFail("Expected noData, got: \(error)")
      }
    }
  }

  func testOptionalFieldWithoutContext_WhenInvalidType_ReturnsNil() throws {
    let dictionary: [String: Any] = ["value": "text"]

    let parsed: Int? = try dictionary.getOptionalField("value")

    XCTAssertNil(parsed)
  }

  func testOptionalFieldWithContext_WhenInvalidType_ReturnsNilAndAppendsWarning() throws {
    let dictionary: [String: Any] = ["value": "text"]
    let context = ParsingContext()

    let parsed: Int? = try dictionary.getOptionalField("value", context: context)

    XCTAssertNil(parsed)
    XCTAssertEqual(context.errors.count, 0)
    XCTAssertEqual(context.warnings.count, 1)
  }

  func testOptionalArrayWithoutContext_WhenElementTypeInvalid_Throws() {
    let dictionary: [String: Any] = ["items": [1, "bad", 3]]

    XCTAssertThrowsError(
      try dictionary.getOptionalArray(
        "items",
        transform: { (value: Int) throws -> Int in value }
      ) as [Int]?
    ) { error in
      guard case .invalidFieldRepresentation = error as? DeserializationError else {
        return XCTFail("Expected invalidFieldRepresentation, got: \(error)")
      }
    }
  }

  func testOptionalArrayWithoutContext_WhenTransformFails_DropsInvalidElement() throws {
    let dictionary: [String: Any] = ["items": [1, 2, 3]]

    let parsed: [Int]? = try dictionary.getOptionalArray(
      "items",
      transform: { (value: Int) throws -> Int in
        if value == 2 {
          throw DeserializationError.generic
        }
        return value
      }
    )

    XCTAssertEqual(parsed, [1, 3])
  }

  func testOptionalArrayWithContext_WhenElementTypeInvalid_AppendsWarningAndKeepsPartialResult(
  ) throws {
    let dictionary: [String: Any] = ["items": [1, "bad", 3]]
    let context = ParsingContext()

    let parsed: [Int]? = try dictionary.getOptionalArray("items", context: context)

    XCTAssertEqual(parsed, [1, 3])
    XCTAssertEqual(context.errors.count, 0)
    XCTAssertEqual(context.warnings.count, 1)
  }

  func testOptionalFieldValidatorParity_WithAndWithoutContext() throws {
    let dictionary: [String: Any] = ["value": 5]
    let validator = makeValueValidator { (value: Int) in value > 10 }

    let withoutContext: Int? = try dictionary.getOptionalField("value", validator: validator)

    let context = ParsingContext()
    let withContext: Int? = try dictionary.getOptionalField(
      "value",
      validator: validator,
      context: context
    )

    XCTAssertNil(withoutContext)
    XCTAssertNil(withContext)
    XCTAssertEqual(context.errors.count, 0)
    XCTAssertEqual(context.warnings.count, 1)
  }
}
