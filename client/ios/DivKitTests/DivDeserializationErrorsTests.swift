@testable import DivKit

import XCTest

import Serialization

final class DivDeserializationErrorsTests: XCTestCase {
  func test_WhenRequiredFieldIsMissing_returnErrors() {
    testErrors(
      building: dictWithoutRequiredField,
      withExpectedErrors: missingRequiredFieldErrors
    )
  }

  func test_WhenInvalidField_returnErrors() {
    testErrors(
      building: dictWithInvalidField,
      withExpectedErrors: invalidFieldErrors
    )
  }

  func test_WhenTypeIsMissing_returnErrors() {
    testErrors(
      building: dictWithMissingType,
      withExpectedErrors: missingTypeErrors
    )
  }

  func test_WhenTypeMismatch_returnErrors() {
    testErrors(
      building: dictWithTypeMismatch,
      withExpectedErrors: typeMismatchErrors
    )
  }
}

private func testErrors(
  building dict: [String: Any],
  withTemplates templates: [String: Any] = [:],
  withExpectedErrors expectedErrors: [DeserializationError]
) {
  guard case let .failure(errors) = DivData.resolve(
    card: dict,
    templates: templates
  ) else {
    return XCTFail("Expected failure when building div data")
  }
  XCTAssertEqual(errors.asArray(), expectedErrors)
}

private let dictWithoutRequiredField: [String: Any] = [
  "states": [
    [
      "div": [
        "type": "separator",
      ],
      "state_id": 0,
    ],
  ],
]

private let dictWithInvalidField: [String: Any] = [
  "log_id": "card",
  "states": [
    [
      "div": [
        "type": "container",
        "items": [],
      ],
      "state_id": 0,
    ],
    [
      "div": [
        "type": "separator",
      ],
      "state_id": 1,
    ],
  ],
]

private let dictWithMissingType: [String: Any] = [
  "log_id": "card",
  "states": [
    [
      "div": [
        "type": "unknown_type",
      ],
      "state_id": 0,
    ],
  ],
]

private let dictWithTypeMismatch: [String: Any] = [
  "log_id": 0,
  "states": [
    [
      "div": [
        "type": "separator",
      ],
      "state_id": 0,
    ],
  ],
]

private let missingRequiredFieldErrors: [DeserializationError] = [
  DeserializationError.requiredFieldIsMissing(field: logIdKey),
]

private let invalidFieldErrors: [DeserializationError] = [
  .nestedObjectError(
    field: statesKey,
    error: .nestedObjectError(
      field: "0",
      error: .nestedObjectError(
        field: divKey,
        error: .invalidValue(
          result: nil,
          value: [
            "type": "container",
            "items": [],
          ]
        )
      )
    )
  ),
  .nestedObjectError(
    field: statesKey,
    error: .nestedObjectError(
      field: "0",
      error: .nestedObjectError(
        field: divKey,
        error: .nestedObjectError(
          field: itemsKey,
          error: .invalidValue(
            result: [],
            value: []
          )
        )
      )
    )
  ),
  .nestedObjectError(
    field: statesKey,
    error: .invalidValue(
      result: [],
      value: [
        "state_id": 0,
        "div": [
          "type": "container",
          "items": [],
        ],
      ]
    )
  ),
]

private let typeMismatchErrors: [DeserializationError] = [
  .nestedObjectError(
    field: logIdKey,
    error: .typeMismatch(
      expected: "String",
      representation: 0
    )
  ),
]

private let missingTypeErrors: [DeserializationError] = [
  .nestedObjectError(
    field: statesKey,
    error: .nestedObjectError(
      field: "0",
      error: .nestedObjectError(
        field: divKey,
        error: .invalidValue(
          result: nil,
          value: [
            "type": "unknown_type",
          ]
        )
      )
    )
  ),
  .nestedObjectError(
    field: statesKey,
    error: .nestedObjectError(
      field: "0",
      error: .nestedObjectError(
        field: divKey,
        error: .requiredFieldIsMissing(field: typeKey)
      )
    )
  ),
  .nestedObjectError(
    field: statesKey,
    error: .invalidValue(
      result: [],
      value: [
        "state_id": 0,
        "div": [
          "type": "unknown_type",
        ],
      ]
    )
  ),
]

extension DeserializationError: Equatable {
  public static func ==(lhs: DeserializationError, rhs: DeserializationError) -> Bool {
    switch (lhs, rhs) {
    case (.generic, .generic),
         (.invalidValue, .invalidValue),
         (.missingType, .missingType),
         (.noData, .noData),
         (.requiredFieldIsMissing, .requiredFieldIsMissing),
         (.unexpectedError, .unexpectedError):
      return true
    case let (.nonUTF8String(lhsStr), .nonUTF8String(rhsStr)):
      return lhsStr == rhsStr
    case let (.invalidJSONData(lhsData), .invalidJSONData(rhsData)):
      return lhsData == rhsData
    case let (.unknownType(lhsType), .unknownType(rhsType)):
      return lhsType == rhsType
    case let (.invalidFieldRepresentation(lhsField, _), .invalidFieldRepresentation(rhsField, _)):
      return lhsField == rhsField
    case let (
      .nestedObjectError(lhsFieldName, lhsError),
      .nestedObjectError(rhsFieldName, rhsError)
    ):
      return lhsFieldName == rhsFieldName && lhsError == rhsError
    case let (.typeMismatch(lhsExpected, _), .typeMismatch(rhsExpected, _)):
      return lhsExpected == rhsExpected
    default:
      switch lhs {
      case .generic,
           .nonUTF8String,
           .invalidJSONData,
           .missingType,
           .unknownType,
           .invalidFieldRepresentation,
           .typeMismatch,
           .invalidValue,
           .requiredFieldIsMissing,
           .nestedObjectError,
           .noData,
           .unexpectedError:
        return false
      }
    }
  }
}

private let statesKey = "states"
private let divKey = "div"
private let logIdKey = "log_id"
private let typeKey = "type"
private let itemsKey = "items"
