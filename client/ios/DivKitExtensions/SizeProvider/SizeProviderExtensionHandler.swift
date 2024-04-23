import BasePublic
import DivKit
import LayoutKit
import Serialization

public final class SizeProviderExtensionHandler: DivExtensionHandler {
  public let id = "size_provider"

  private let variablesStorage: DivVariablesStorage
  private let autodisposePool = AutodisposePool()

  // Stores variables that has been updated during the layout cycle to prevent relayout.
  // onCardUpdated() must be called when the card is updated and new layout cycle begins.
  private var updatedVariables: Set<DivVariableName> = []

  public init(variablesStorage: DivVariablesStorage) {
    self.variablesStorage = variablesStorage

    variablesStorage.changeEvents.addObserver { [weak self] event in
      if let updatedVariables = self?.updatedVariables,
         !event.changedVariables.isSubset(of: updatedVariables) {
        // If any variables are updated externally (not by SizeProviderExtensionHandler) it must be
        // considered as new layout cycle.
        self?.updatedVariables = []
      }
    }.dispose(in: autodisposePool)
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

    return SizeProviderBlock(
      child: block,
      widthUpdater: makeValueUpdater(context: context, variableName: widthVariableName),
      heightUpdater: makeValueUpdater(context: context, variableName: heightVariableName)
    )
  }

  public func onCardUpdated(reasons: [DivActionURLHandler.UpdateReason]) {
    let hasNotVariableReason = reasons.isEmpty || reasons.contains {
      switch $0 {
      case .variable:
        false
      default:
        true
      }
    }
    if hasNotVariableReason {
      updatedVariables = []
    }
  }

  private func update(
    cardId: DivCardID,
    variableName: DivVariableName,
    value: Int
  ) {
    let previousValue: Int? = variablesStorage.getVariableValue(cardId: cardId, name: variableName)
    if value == previousValue {
      return
    }

    if updatedVariables.contains(variableName) {
      DivKitLogger.warning(
        "[SizeProviderExtensionHandler] Variable '\(variableName)' was already updated during the layout cycle."
      )
      return
    }

    updatedVariables.insert(variableName)

    variablesStorage.update(
      cardId: cardId,
      name: variableName,
      value: String(value)
    )
  }

  private func makeValueUpdater(
    context: DivBlockModelingContext,
    variableName: DivVariableName?
  ) -> ValueUpdater? {
    guard let variableName else {
      return nil
    }
    return { [weak self] in
      self?.update(cardId: context.cardId, variableName: variableName, value: $0)
    }
  }
}
