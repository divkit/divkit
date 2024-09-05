import DivKit
import LayoutKit
import VGSL

public final class MarkdownExtensionHandler: DivExtensionHandler {
  public var id: String = "markdown"

  public init() {}

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let block = block as? TextBlock, let div = div as? DivText else {
      return block
    }
    let fontParams = div.resolveFontParams(context.expressionResolver)
    let typo = Typo(font: context.fontProvider.font(fontParams)).allowHeightOverrun
    var builder = MarkdownStringBuilder(typo: typo, path: context.parentPath)
    let string = builder.attributedString(from: block.text.string)
    return TextBlock(
      widthTrait: block.widthTrait,
      heightTrait: block.heightTrait,
      text: string,
      accessibilityElement: nil
    )
  }
}
