@testable import DivKit

import XCTest

final class FunctionTests: XCTestCase {
  private var binaryFunction: SimpleFunction!

  private var lastArgs: [Any] = []

  override func setUp() {
    binaryFunction = FunctionBinary<Double, Double, Bool> { [unowned self] in
      lastArgs = [$0, $1]
      return true
    }
  }

  func test_invoke_FunctionBinary_WithoutCast() throws {
    _ = try binaryFunction.invoke(args: [1.2, 3.4])

    checLastArgs([1.2, 3.4])
  }

  func test_invoke_FunctionBinary_WithCast() throws {
    _ = try binaryFunction.invoke(args: [2, 3.4])

    checLastArgs([2.0, 3.4])
  }

  func test_invoke_FunctionBinary_WithInvalidArguments() throws {
    XCTAssertThrowsError(
      _ = try binaryFunction.invoke(args: [1.2, true]),
      AnyCalcExpression.Error.message("Argument couldn't be casted to Double")
    )
  }

  func test_invoke_OverloadedFunction_WithoutCast() throws {
    var success = false

    let function = OverloadedFunction(
      functions: [
        FunctionBinary<Int, Int, Bool> { _, _ in
          XCTFail("Function must not be called")
          return true
        },
        FunctionBinary<Double, Double, Bool> { _, _ in
          success = true
          return true
        }
      ]
    )

    _ = try function.invoke(args: [1.2, 3.4])

    XCTAssertTrue(success)
  }

  func test_invoke_OverloadedFunction_WithCast() throws {
    var success = false

    let function = OverloadedFunction(
      functions: [
        FunctionBinary<Int, Int, Bool> { _, _ in
          XCTFail("Function must not be called")
          return true
        },
        FunctionBinary<Double, Double, Bool> { _, _ in
          success = true
          return true
        }
      ]
    )

    _ = try function.invoke(args: [2, 3.4])

    XCTAssertTrue(success)
  }

  func test_invoke_OverloadedFunction_WithInvalidArguments() throws {
    let function = OverloadedFunction(
      functions: [
        FunctionBinary<Int, Int, Bool> { _, _ in
          XCTFail("Function must not be called")
          return true
        },
        FunctionBinary<Double, Double, Bool> { _, _ in
          XCTFail("Function must not be called")
          return true
        }
      ]
    )

    XCTAssertThrowsError(
      _ = try function.invoke(args: [1.2, true]),
      FunctionSignature.Error.notFound.message
    )
  }

  func test_invoke_OverloadedFunction_WithCast_MultipleMatches() throws {
    let function = OverloadedFunction(
      functions: [
        FunctionBinary<Int, Double, Bool> { _, _ in true },
        FunctionBinary<Double, Int, Bool> { _, _ in true }
      ]
    )

    XCTAssertThrowsError(
      _ = try function.invoke(args: [2, 3]),
      CalcExpression.Error.message("Multiple matching overloads")
    )
  }

  private func checLastArgs(_ args: [AnyHashable]) {
    XCTAssertEqual(args, lastArgs as! [AnyHashable])
  }
}
