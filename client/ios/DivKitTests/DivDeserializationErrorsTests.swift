@testable import DivKit
import CommonCorePublic

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
    ] as [String: Any],
  ],
]

private let dictWithInvalidField: [String: Any] = [
  "log_id": "card",
  "states": [
    [
      "div": [
        "type": "container",
        "items": [] as [Any],
      ] as [String: Any],
      "state_id": 0,
    ] as [String: Any],
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
    ] as [String: Any],
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
    ] as [String: Any],
  ],
]

private let missingRequiredFieldErrors: [DeserializationError] = [
  DeserializationError.requiredFieldIsMissing(field: logIdKey),
]

private let invalidFieldErrors: [DeserializationError] = [
  .nestedObjectError(
    field: statesKey,
    error: .composite(
      error: .invalidValue(
        result: [] as [Any],
        from: [
          "state_id": 0,
          "div": [
            "type": "container",
            "items": [] as [Any],
          ] as [String: Any],
        ] as [String: Any]),
      causes: NonEmptyArray(
        .nestedObjectError(
          field: "0",
          error: .nestedObjectError(
            field: divKey,
            error: .composite(
              error: .invalidValue(
                result: nil,
                from: [
                  "type": "container",
                  "items": [] as [Any],
                ] as [String: Any]),
              causes: NonEmptyArray(
                .nestedObjectError(
                  field: itemsKey,
                  error: .invalidValue(
                    result: [] as [Any],
                    value: [] as [Any]
                  )
                )
              )
            )
          )
        )
      )
    )
  )
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
    error: .composite(
      error: .invalidValue(
        result: [] as [Any],
        from: [
          "state_id": 0,
          "div": [
            "type": "unknown_type",
          ],
        ] as [String: Any]
      ),
      causes: NonEmptyArray(
        .nestedObjectError(
          field: "0",
          error: .nestedObjectError(
            field: divKey,
            error: .composite(
              error: .invalidValue(
                result: nil,
                from: [
                  "type": "unknown_type",
                ]
              ),
              causes: NonEmptyArray(
                .requiredFieldIsMissing(field: typeKey)
              )
            )
          )
        )
      )
    )
  )
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
    case let (.composite(lhsError, lhsCauses), .composite(rhsError, rhsCauses)):
      return lhsError == rhsError && lhsCauses == rhsCauses
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
           .composite,
           .noData,
           .unexpectedError:
        return false
      }
    }
  }
}

extension DeserializationError.DerivedError: Equatable {
  public static func == (lhs: Serialization.DeserializationError.DerivedError, rhs: Serialization.DeserializationError.DerivedError) -> Bool {
    switch (lhs, rhs) {
    case (.invalidValue, .invalidValue):
      return true
    }
  }
}

private let statesKey = "states"
private let divKey = "div"
private let logIdKey = "log_id"
private let typeKey = "type"
private let itemsKey = "items"
