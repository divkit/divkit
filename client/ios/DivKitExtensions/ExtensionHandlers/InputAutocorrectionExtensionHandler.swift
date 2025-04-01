import DivKit
import LayoutKit
import VGSL

public final class InputAutocorrectionExtensionHandler: DivExtensionHandler {
  public let id = "input_autocorrection"

  public init() {}

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    guard let textInputBlock = block as? TextInputBlock else {
      return block
    }

    let params = getExtensionParams(div)

    guard let autocorrectionEnabled = params["enabled"] as? Bool else {
      return block
    }

    return textInputBlock.modifying(
      autocorrection: autocorrectionEnabled
    )
  }
}
