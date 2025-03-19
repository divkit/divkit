import DivKit
import LayoutKit
import UIKit
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
    let style = div.resolveExtensionParams(for: extensionID).flatMap {
      try? ShimmerImagePreviewStyle(dictionary: $0, expressionResolver: expressionResolver)
    } ?? .default
    let ShimmerImagePreviewViewProvider = ShimmerImagePreviewViewProvider(
      style: style,
      effectBeginTime: effectBeginTime,
      path: context.path
    )
    let imageHolder = context.imageHolderFactory.make(
      div.resolveImageUrl(expressionResolver),
      .view(ShimmerImagePreviewViewProvider)
    )
    return block.makeCopy(with: imageHolder)
  }
}

private let extensionID = "shimmer"
