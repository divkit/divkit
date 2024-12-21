import DivKit
import LayoutKit
import UIKit

public final class ShineExtensionHandler: DivExtensionHandler {
  public let id: String = extensionID

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

    let extensionParams: ShineExtensionParams
    do {
      extensionParams = try div.resolveExtensionParams(for: extensionID).flatMap {
        try ShineExtensionParams(dictionary: $0, context: context)
      } ?? .default
    } catch {
      DivKitLogger.error("Failed to resolve ShineExtension params: \(error)")
      return block
    }

    return ShineBlock(
      child: block,
      params: extensionParams,
      maskImageHolder: block.imageHolder
    )
  }
}

private let extensionID = "shine"
