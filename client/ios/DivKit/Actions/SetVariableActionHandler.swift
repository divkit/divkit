import Foundation

final class SetVariableActionHandler {
  func handle(_ action: DivActionSetVariable, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let variableName = action.resolveVariableName(expressionResolver) else {
      return
    }

    guard let variableValue = action.value.resolveVariableValue(expressionResolver) else {
      return
    }

    context.variablesStorage.update(
      path: context.path,
      name: DivVariableName(rawValue: variableName),
      value: variableValue
    )
  }
}
