import DivKit
import LayoutKit
import VGSL

public final class InputPropertiesExtensionHandler: DivExtensionHandler {
  public let id = "input_properties"

  public init() {}

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let textInputBlock = block as? TextInputBlock else {
      return block
    }

    let params = getExtensionParams(div)

    var newBlock = textInputBlock

    if let enablesReturnKeyAutomatically = try? params.getOptionalBool(
      "enables_return_key_automatically",
      expressionResolver: context.expressionResolver
    ) {
      newBlock = newBlock.modifying(
        enablesReturnKeyAutomatically: enablesReturnKeyAutomatically
      )
    }

    if let spellChecking = try? params.getOptionalBool(
      "spell_checking",
      expressionResolver: context.expressionResolver
    ) {
      newBlock = newBlock.modifying(
        spellChecking: spellChecking
      )
    }

    return newBlock
  }
}
