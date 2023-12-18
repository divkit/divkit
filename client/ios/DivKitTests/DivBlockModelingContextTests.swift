@testable import DivKit

import XCTest

final class DivBlockModelingContextErrorsTests: XCTestCase {
  func test_WhenHasNoExtensionHandler_AddsErrorToErrorStorage() throws {
    let context = DivBlockModelingContext()
    
    _ = try DivDataTemplate.make(
      fromFile: "div-with-extension-handler",
      subdirectory: "div-context",
      context: context
    )

    XCTAssertEqual(context.errorsStorage.errors.count, 1)
  }
}
