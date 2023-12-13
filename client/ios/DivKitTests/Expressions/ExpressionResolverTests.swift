import XCTest

@testable import DivKit

import CommonCorePublic

final class ExpressionResolverTests: XCTestCase {
  private lazy var expressionResolver = ExpressionResolver(
    variables: [
      "string_var": .string("string value"),
      "color_var": .color(Color.color(withHexString: "#AABBCC")!),
      "enum_var": .string("first"),
      "url_var": .url(URL(string: "https://some.url")!),
      "array_var": .array([1]),
    ],
    persistentValuesStorage: DivPersistentValuesStorage(),
    variableTracker: { [unowned self] in
      self.usedVariables = self.usedVariables.union($0)
    }
  )

  private var usedVariables: Set<DivVariableName> = []

  func test_ResolveString_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveString(.value("Some string")),
      "Some string"
    )
  }

  func test_ResolveString_WithVariable() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{string_var}")),
      "string value"
    )
  }

  func test_ResolveString_WithNestedExpression() {
    XCTAssertEqual(
      expressionResolver.resolveString(expression("@{'Value: @{string_var}'}")),
      "Value: string value"
    )
  }

  func test_ResolveColor_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveColor(.value(color("#CCBBAA"))),
      color("#CCBBAA")
    )
  }

  func test_ResolveColor_Expression() {
    XCTAssertEqual(
      expressionResolver.resolveColor(expression("@{'#CCBBAA'}")),
      color("#CCBBAA")
    )
  }

  func test_ResolveColor_Invalid() {
    XCTAssertNil(
      expressionResolver.resolveColor(expression("@{'invalid'}"))
    )
  }

  func test_ResolveUrl_Constant() {
    XCTAssertEqual(
      expressionResolver.resolveUrl(.value(url("https://some.url"))),
      url("https://some.url")
    )
  }

  func test_ResolveUrl_Expression() {
    XCTAssertEqual(
      expressionResolver.resolveUrl(expression("@{'https://some.url'}")),
      url("https://some.url")
    )
  }

  func test_ResolveEnum_WithInvalidValue() {
    let value: TestEnum? = expressionResolver.resolveEnum(expression("@{'invalid'}"))
    XCTAssertNil(value)
  }

  func test_ResolveEnum_WithConstant() {
    XCTAssertEqual(
      expressionResolver.resolveEnum(.value(TestEnum.second)),
      TestEnum.second
    )
  }

  func test_ResolveEnum_WithVariable() {
    XCTAssertEqual(
      expressionResolver.resolveEnum(expression("@{enum_var}")),
      TestEnum.first
    )
  }

  func test_ResolveArray_WithVariable() throws {
    XCTAssertEqual(
      expressionResolver.resolveArray(expression("@{array_var}")) as! [AnyHashable],
      [1]
    )
  }

  func test_TracksVariable_InSimpleExpression() {
    let _ = expressionResolver.resolveString(expression("@{string_var}"))

    XCTAssertEqual(usedVariables, ["string_var"])
  }

  func test_TracksVariable_InNestedExpression() {
    let _ = expressionResolver.resolveString(expression("@{'@{string_var}'}"))

    XCTAssertEqual(usedVariables, ["string_var"])
  }

  func test_TracksVariable_InGetValueFunction() {
    let _ = expressionResolver.resolveString(
      expression("@{getStringValue('string_var', 'default')}")
    )

    XCTAssertTrue(usedVariables.contains("string_var"))
  }
}

private func expression<T>(_ expression: String) -> Expression<T> {
  .link(try! ExpressionLink<T>(rawValue: expression)!)
}

private func color(_ color: String) -> Color {
  Color.color(withHexString: color)!
}

private func url(_ url: String) -> URL {
  URL(string: url)!
}

private enum TestEnum: String {
  case first
  case second
}
