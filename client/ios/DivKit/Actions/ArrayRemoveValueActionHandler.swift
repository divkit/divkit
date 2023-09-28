import Foundation

final class ArrayRemoveValueActionHandler {
  func handle(_ action: DivActionArrayRemoveValue, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let variableName = action.resolveVariableName(expressionResolver),
          let index = action.resolveIndex(expressionResolver),
          var array: [AnyHashable] = context.variablesStorage.getVariableValue(
            cardId: context.cardId,
            name: DivVariableName(rawValue: variableName)
          ) else {
      return
    }

    guard index >= 0 && index < array.count else {
      DivKitLogger
        .error(
          "Index out of bound \(index) for removing value of \(variableName) with length \(array.count)"
        )
      return
    }

    array.remove(at: index)

    context.variablesStorage.update(
      cardId: context.cardId,
      name: DivVariableName(rawValue: variableName),
      value: .array(array)
    )
  }
}
