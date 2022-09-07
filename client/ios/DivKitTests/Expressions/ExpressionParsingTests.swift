import XCTest

@testable import DivKit

final class ExpressionParsingTests: XCTestCase {
  func test_ParseStringExpression() {
    testData.forEach { rawValue, expectedItems in
      let link = ExpressionLink<String>(rawValue: rawValue, validator: nil)
      XCTAssertEqual(link?.items, expectedItems, rawValue)
    }
  }
}

private var testData: [(String, [ExpressionLink<String>.Item]?)] {
  [
    ("@{}", [.string("")]),
    ("a", nil),
    (
      "@{x}",
      [.calcExpression(CalcExpression.parse("x"))]
    ),
    (
      "a@{x}",
      [
        .string("a"),
        .calcExpression(CalcExpression.parse("x")),
      ]
    ),
    (
      "@{x}a",
      [
        .calcExpression(CalcExpression.parse("x")),
        .string("a"),
      ]
    ),
    (
      "a@{x}a",
      [
        .string("a"),
        .calcExpression(CalcExpression.parse("x")),
        .string("a"),
      ]
    ),
    (
      "@{xx}",
      [
        .calcExpression(CalcExpression.parse("xx")),
      ]
    ),
    (
      "aa@{xx}",
      [
        .string("aa"),
        .calcExpression(CalcExpression.parse("xx")),
      ]
    ),
    (
      "@{xx}aa",
      [
        .calcExpression(CalcExpression.parse("xx")),
        .string("aa"),
      ]
    ),
    (
      "aa@{xx}aa",
      [
        .string("aa"),
        .calcExpression(CalcExpression.parse("xx")),
        .string("aa"),
      ]
    ),
    (
      "@{xx}a@{xx}",
      [
        .calcExpression(CalcExpression.parse("xx")),
        .string("a"),
        .calcExpression(CalcExpression.parse("xx")),
      ]
    ),
  ]
}
