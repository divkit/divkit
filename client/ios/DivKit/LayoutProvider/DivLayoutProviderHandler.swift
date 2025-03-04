import LayoutKit
import VGSL

@_spi(Internal)
public final class DivLayoutProviderHandler {
  private let variablesStorage: DivVariablesStorage
  private let autodisposePool = AutodisposePool()

  // Stores variables that has been updated during the layout cycle to prevent relayout.
  // resetUpdatedVariables() must be called when the card is updated and new layout cycle begins.
  private var updateVariablesCounter = 0
  private var updatedVariables: Set<DivVariableName> = []

  public init(variablesStorage: DivVariablesStorage) {
    self.variablesStorage = variablesStorage

    variablesStorage.changeEvents.addObserver { [weak self] event in
      if let updatedVariables = self?.updatedVariables,
         !updatedVariables.isEmpty,
         !event.changedVariables.isSubset(of: updatedVariables) {
        // If any variables are updated externally (not by DivLayoutProviderHandler) it must be
        // considered as new layout cycle.
        self?.updatedVariables = []
      }
    }.dispose(in: autodisposePool)
  }

  public func apply(
    block: Block,
    path: UIElementPath,
    widthVariableName: DivVariableName?,
    heightVariableName: DivVariableName?
  ) -> Block {
    if widthVariableName == nil, heightVariableName == nil {
      return block
    }
    return SizeProviderBlock(
      child: block,
      widthUpdater: makeValueUpdater(path: path, variableName: widthVariableName),
      heightUpdater: makeValueUpdater(path: path, variableName: heightVariableName)
    )
  }

  public func resetUpdatedVariables() {
    updateVariablesCounter = 0
    updatedVariables = []
  }

  private func update(
    path: UIElementPath,
    variableName: DivVariableName,
    value: Int
  ) {
    let previousValue: Int? = variablesStorage.getVariableValue(path: path, name: variableName)
    if value == previousValue {
      return
    }

    if updatedVariables.contains(variableName) {
      if updateVariablesCounter > 3 {
        DivKitLogger.warning(
          "[DivLayoutProviderHandler] Variable '\(variableName)' was already updated more then 3 times during the layout cycle. It looks like there is a circular dependency in the layout."
        )
        return
      } else {
        updateVariablesCounter += 1
      }
    }

    updatedVariables.insert(variableName)

    variablesStorage.update(
      path: path,
      name: variableName,
      value: String(value)
    )
  }

  private func makeValueUpdater(
    path: UIElementPath,
    variableName: DivVariableName?
  ) -> SizeProviderBlock.ValueUpdater? {
    guard let variableName else {
      return nil
    }
    return { [weak self] in
      self?.update(path: path, variableName: variableName, value: $0)
    }
  }
}

extension DivLayoutProvider {
  func apply(
    block: Block,
    context: DivBlockModelingContext
  ) -> Block {
    guard let handler = context.layoutProviderHandler else {
      return block
    }
    return handler.apply(
      block: block,
      path: context.parentPath,
      widthVariableName: widthVariableName?.toVariableName(),
      heightVariableName: heightVariableName?.toVariableName()
    )
  }
}

extension String {
  fileprivate func toVariableName() -> DivVariableName {
    DivVariableName(rawValue: self)
  }
}
