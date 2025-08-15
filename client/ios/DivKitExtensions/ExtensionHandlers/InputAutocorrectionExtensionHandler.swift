import DivKit
import LayoutKit
import VGSL

public final class InputAutocorrectionExtensionHandler: DivExtensionHandler {
  public let id = "input_autocorrection"

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

    guard let autocorrectionEnabled = try? params.getOptionalBool(
      "enabled",
      expressionResolver: context.expressionResolver
    ) else {
      return block
    }

    return textInputBlock.modifying(
      autocorrection: autocorrectionEnabled
    )
  }
}
