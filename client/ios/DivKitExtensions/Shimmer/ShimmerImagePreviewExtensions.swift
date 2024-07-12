import Foundation
import UIKit

import DivKit
import LayoutKit
import VGSL

public final class ShimmerImagePreviewExtension: DivExtensionHandler {
  public let id: String = extensionID
  private let effectBeginTime = CACurrentMediaTime()

  public init() {}

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
    let shimmerViewProvider = ShimmerViewProvider(
      style: div.resolveShimmerStyle(expressionResolver) ?? .default,
      effectBeginTime: effectBeginTime,
      path: context.parentPath
    )
    let imageHolder = context.imageHolderFactory.make(
      div.resolveImageUrl(expressionResolver),
      .view(shimmerViewProvider)
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
