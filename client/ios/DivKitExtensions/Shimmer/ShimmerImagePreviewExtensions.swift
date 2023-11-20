import Foundation
import UIKit

import BasePublic
import DivKit
import LayoutKit

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
    let placeholderView = viewFactory(
      div.resolveShimmerStyle(expressionResolver) ?? .default
    )
    let imageHolder = context.imageHolderFactory.make(
      div.resolveImageUrl(expressionResolver),
      .view(placeholderView)
    )
    return block.makeCopy(with: imageHolder)
  }
}

extension DivBase {
  fileprivate func resolveShimmerStyle(
    _ expressionResolver: ExpressionResolver
  ) -> ShimmerStyle? {
    guard let shimmerExtensionsParams = extensions?.first(where: { $0.id == extensionID })?.params,
          let style = try? ShimmerStyle(
            dictionary: shimmerExtensionsParams,
            expressionResolver: expressionResolver
          ) else {
      return nil
    }
    return style
  }
}

private let extensionID = "shimmer"
