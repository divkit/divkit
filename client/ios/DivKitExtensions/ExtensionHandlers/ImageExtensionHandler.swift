import CommonCorePublic
import DivKit
import LayoutKit

public final class ImageExtensionHandler: DivExtensionHandler {
  public let id: String
  private let image: ImageHolder?

  public init(
    id: String,
    image: ImageHolder?
  ) {
    self.id = id
    self.image = image
  }

  public func applyBeforeBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    guard
      let block = block as? ImageBlock,
      let image = image
    else {
      return block
    }

    return ImageBlock(
      imageHolder: image,
      widthTrait: block.widthTrait,
      height: block.height,
      contentMode: block.contentMode,
      tintColor: block.tintColor,
      tintMode: block.tintMode,
      effects: block.effects
    )
  }
}
