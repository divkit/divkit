import Foundation

import DivKit
import LayoutKit
import VGSL

public final class CustomImagePreviewExtensionHandler: DivExtensionHandler {
  public let id: String
  private let viewProvider: ViewProvider

  public init(id: String, viewProvider: ViewProvider) {
    self.id = id
    self.viewProvider = viewProvider
  }

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard
      let block = block as? ImageBlock,
      let div = div as? DivImage
    else {
      return block
    }

    let imageHolder = context.imageHolderFactory.make(
      div.resolveImageUrl(context.expressionResolver) ?? defaultImageURL,
      .view(viewProvider)
    )
    return block.makeCopy(with: imageHolder)
  }
}

private let defaultImageURL = URL(string: "empty://")
