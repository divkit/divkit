import Foundation

final class ArrayActionsHandler {
  func handle(_ action: DivActionArrayInsertValue, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let variableName = action.resolveName(expressionResolver),
          let value = action.value.resolve(expressionResolver),
          var array = context.getVariableValue(variableName) else {
      return
    }

    if let index = action.resolveIndex(expressionResolver) {
      guard index >= 0, index <= array.count else {
        DivKitLogger.error(
          "Failed to insert value in array \(variableName). Index out of bounds: \(index)."
        )
        return
      }
      array.insert(value, at: index)
    } else {
      array.append(value)
    }

    context.setVariableValue(variableName, array)
  }

  func handle(_ action: DivActionArrayRemoveValue, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let variableName = action.resolveName(expressionResolver),
          let index = action.resolveIndex(expressionResolver),
          var array = context.getVariableValue(variableName) else {
      return
    }

    guard index >= 0, index < array.count else {
      DivKitLogger.error(
        "Failed to remove value from array \(variableName). Index out of bounds: \(index)."
      )
      return
    }

    array.remove(at: index)
    context.setVariableValue(variableName, array)
  }

  func handle(_ action: DivActionArraySetValue, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let variableName = action.resolveName(expressionResolver),
          let index = action.resolveIndex(expressionResolver),
          let value = action.value.resolve(expressionResolver),
          var array = context.getVariableValue(variableName) else {
      return
    }

    guard index >= 0, index < array.count else {
      DivKitLogger.error(
        "Failed to set value in array \(variableName). Index out of bounds: \(index)."
      )
      return
    }

    array[index] = value
    context.setVariableValue(variableName, array)
  }
}

extension DivActionHandlingContext {
  fileprivate func getVariableValue(_ name: DivVariableName) -> DivArray? {
    variablesStorage.getVariableValue(path: path, name: name)
  }

  fileprivate func setVariableValue(_ name: DivVariableName, _ value: DivArray) {
    variablesStorage.update(path: path, name: name, value: .array(value))
  }
}

private protocol ArrayAction {
  func resolveVariableName(_ resolver: ExpressionResolver) -> String?
}

extension ArrayAction {
  func resolveName(_ resolver: ExpressionResolver) -> DivVariableName? {
    if let name = resolveVariableName(resolver) {
      return DivVariableName(rawValue: name)
    }
    return nil
  }
}

extension DivActionArrayInsertValue: ArrayAction {}

extension DivActionArrayRemoveValue: ArrayAction {}

extension DivActionArraySetValue: ArrayAction {}
