import Foundation
import UIKit

import DivKit
import LayoutKit
import BasePublic

public final class ShimmerImagePreviewExtension: DivExtensionHandler {
  public let id: String = extensionID
  private let viewFactory: (ShimmerStyle) -> UIView

  public init(viewFactory: ((ShimmerStyle) -> UIView)? = nil) {
    self.viewFactory = viewFactory ?? DefaultShimmerViewFactory().makeView(style:)
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

    let expressionResolver = context.expressionResolver
    let imageURL = div.resolveImageUrl(expressionResolver)
    let view = viewFactory(div.getShimmerStyle(expressionResolver: expressionResolver) ?? .default)
    let placeholder: ImagePlaceholder = .view(view)

    let imageHolder = context.imageHolderFactory.make(imageURL, placeholder)
    return block.makeCopy(with: imageHolder)
  }
}

extension DivBase {
  func getShimmerStyle(expressionResolver: ExpressionResolver) -> ShimmerStyle? {
    guard let shimmerExtensionsParams = extensions?.first(where: { $0.id == extensionID })?.params,
          let style = try? ShimmerStyle(dictionary: shimmerExtensionsParams, expressionResolver: expressionResolver) else {
      return nil
    }
    return style
  }
}

private let extensionID = "shimmer"
