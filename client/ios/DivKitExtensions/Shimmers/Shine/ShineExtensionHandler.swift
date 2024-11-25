import UIKit

import DivKit
import LayoutKit

public final class ShineExtensionHandler: DivExtensionHandler {
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
    let shineStyle: ShineStyle
    do {
      shineStyle = try div.resolveExtensionParams(for: extensionID).flatMap {
        try ShineStyle(dictionary: $0, expressionResolver: expressionResolver)
      } ?? .default
    } catch {
      DivKitLogger.error("Failed to resolve ShineExtension params: \(error)")
      return block
    }

    guard shineStyle.isEnabled else {
      return block
    }

    return LayeredBlock(
      widthTrait: block.widthTrait,
      children: [
        block,
        ShineBlock(
          style: shineStyle,
          effectBeginTime: effectBeginTime,
          maskImageHolder: block.imageHolder
        ),
      ]
    )
  }
}

private let extensionID = "shine"
