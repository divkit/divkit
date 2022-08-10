import BaseUI
import CommonCore
import DivKit
import LayoutKit

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

  public var accessibilityElement: AccessibilityElement? {
    .none(label: text)
  }

  public func applyBeforeBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    guard
      let textBlock = block as? TextBlock,
      let text = text,
      let newTextBlock = TextBlock(copyingAttributesFrom: textBlock, text: text)
    else {
      return block
    }

    return newTextBlock
  }
}
