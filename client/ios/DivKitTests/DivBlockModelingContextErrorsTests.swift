@testable import DivKit

import XCTest

import NetworkingPublic

final class DivBlockModelingContextErrorsTests: XCTestCase {
  func test_WhenHasNoExtensionHandler_ThrowsError() throws {
    let context = makeContext()
    _ = try DivDataTemplate.make(
      fromFile: "div-with-extension-handler",
      subdirectory: "div-context",
      context: context
    )

    XCTAssertEqual(context.errorsStorage.errors.count, 1)
  }

  func test_WhenHasExtensionHandler_NoErrors() throws {
    let context = makeContext(
      extensionHandlers: [FakeExtensionHandler(id: "sample_extension_id")]
    )
    _ = try DivDataTemplate.make(
      fromFile: "div-with-extension-handler",
      subdirectory: "div-context",
      context: context
    )

    XCTAssertEqual(context.errorsStorage.errors.count, 0)
  }

  func test_WhenHasStateInterceptorHandler_NoErrors() throws {
    let context = makeContext(
      stateInterceptors: [FakeStateInterceptor(id: "sample_extension_id")]
    )
    _ = try DivDataTemplate.make(
      fromFile: "div-with-extension-handler",
      subdirectory: "div-context",
      context: context
    )

    XCTAssertEqual(context.errorsStorage.errors.count, 0)
  }
}

private struct FakeExtensionHandler: DivExtensionHandler {
  var id: String
}

private struct FakeStateInterceptor: DivStateInterceptor {
  var id: String

  func getAppropriateState(divState _: DivState, context _: DivBlockModelingContext) -> DivState
    .State? {
    nil
  }
}

private func makeContext(
  extensionHandlers: [DivExtensionHandler] = [],
  stateInterceptors: [DivStateInterceptor] = []
) -> DivBlockModelingContext {
  DivBlockModelingContext(
    cardId: DivKitTests.cardId,
    stateManager: DivStateManager(),
    imageHolderFactory: FakeImageHolderFactory(),
    extensionHandlers: extensionHandlers,
    stateInterceptors: stateInterceptors,
    persistentValuesStorage: DivPersistentValuesStorage()
  )
}
