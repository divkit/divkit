#if os(iOS)
@_spi(Internal) import DivKit
import LayoutKit
import Serialization
import VGSL

/// Use `div-base.layout_provider`.
@_spi(Legacy)
public final class SizeProviderExtensionHandler: DivExtensionHandler {
  public let id = "size_provider"

  private let handler: DivLayoutProviderHandler

  public init(variablesStorage: DivVariablesStorage) {
    handler = DivLayoutProviderHandler(variablesStorage: variablesStorage)
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    let extensionParams = getExtensionParams(div)
    let widthVariableName: DivVariableName? = try? extensionParams
      .getOptionalField("width_variable_name")
    let heightVariableName: DivVariableName? = try? extensionParams
      .getOptionalField("height_variable_name")
    if widthVariableName == nil, heightVariableName == nil {
      DivKitLogger.error("No valid params for SizeProviderExtensionHandler")
      return block
    }
    return handler.apply(
      block: block,
      path: context.path,
      widthVariableName: widthVariableName,
      heightVariableName: heightVariableName
    )
  }

  public func onCardUpdated(reasons: [DivCardUpdateReason]) {
    let hasNotVariableReason = reasons.isEmpty || reasons.contains {
      switch $0 {
      case .variable:
        false
      default:
        true
      }
    }
    if hasNotVariableReason {
      handler.resetUpdatedVariables()
    }
  }
}
#endif
