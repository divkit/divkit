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

    let imageURL = div.resolveImageUrl(context.expressionResolver) ?? defaultImageURL
    let placeholder: ImagePlaceholder = .view(viewFactory())

    let imageHolder = context.imageHolderFactory.make(imageURL, placeholder)
    return block.makeCopy(with: imageHolder)
  }
}

private let defaultImageURL = URL(string: "empty://")

