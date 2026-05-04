import DivKit
import LayoutKit
import VGSL

public final class TextExtensionHandler: DivExtensionHandler {
  public let id: String

  private let text: String?

  public init(
    id: String,
    text: String?
  ) {
    self.id = id
    self.text = text
  }

  public func applyBeforeBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    guard
      let textBlock = block as? TextBlock,
      let text,
      let newTextBlock = TextBlock(copyingAttributesFrom: textBlock, text: text)
    else {
      return block
    }

    return newTextBlock
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    guard div is DivText, let text else { return block }
    return block.addingDecorations(
      accessibilityElement: .none(label: text)
    )
  }
}
