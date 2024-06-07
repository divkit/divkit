import Foundation

final class DictSetValueActionHandler {
  func handle(_ action: DivActionDictSetValue, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let variableName = action.resolveVariableName(expressionResolver),
          let key = action.resolveKey(expressionResolver),
          var dict: DivDictionary = context.variablesStorage.getVariableValue(
            cardId: context.cardId,
            name: DivVariableName(rawValue: variableName)
          ) else {
      return
    }

    if let value = action.value?.resolve(expressionResolver) {
      dict[key] = value
    } else {
      dict.removeValue(forKey: key)
    }

    context.variablesStorage.update(
      cardId: context.cardId,
      name: DivVariableName(rawValue: variableName),
      value: .dict(dict)
    )
  }
}
