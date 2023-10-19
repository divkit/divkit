import Foundation

import BasePublic
import DivKit
import LayoutKit

public final class CustomImagePreviewExtensionHandler: DivExtensionHandler {
  public let id: String
  private let viewFactory: () -> ViewType

  public init(id: String, viewFactory: @escaping () -> ViewType) {
    self.id = id
    self.viewFactory = viewFactory
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
      .view(viewFactory())
    )
    return block.makeCopy(with: imageHolder)
  }
}

private let defaultImageURL = URL(string: "empty://")
