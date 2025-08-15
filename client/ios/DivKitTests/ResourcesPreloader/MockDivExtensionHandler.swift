import DivKit
import Foundation
import LayoutKit

final class MockDivExtensionHandler: DivExtensionHandler {
  var id: String = "mock_extension"
  var preloadURL = URL(string: "https://example.com/mock.json")!

  public func getPreloadURLs(div _: DivBase, expressionResolver _: ExpressionResolver) -> [URL] {
    [preloadURL]
  }

  func accept(div _: DivBase, context _: DivBlockModelingContext) {}

  func applyBeforeBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    block
  }

  func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    block
  }
}
