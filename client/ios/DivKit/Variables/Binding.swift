import CoreGraphics
import Foundation

import LayoutKit

extension Binding where T: AdditiveArithmetic & CustomStringConvertible {
  static var zero: Binding<T> {
    self.init(
      name: "zero",
      getValue: { _ in .zero },
      userInterfaceActionFactory: { _, _ in nil }
    )
  }

  init(context: DivBlockModelingContext, name: String) {
    self = makeBinding(
      context: context,
      name: name,
      valueConverter: { $0.description },
      defaultValue: .zero
    )
  }
}

extension Binding where T == String {
  init(context: DivBlockModelingContext, name: String) {
    self = makeBinding(
      context: context,
      name: name,
      valueConverter: { $0.percentEncoded() },
      defaultValue: ""
    )
  }
}

extension Binding where T == Bool {
  init(context: DivBlockModelingContext, name: String) {
    self = makeBinding(
      context: context,
      name: name,
      valueConverter: { $0.description },
      defaultValue: false
    )
  }
}

private func makeBinding<T>(
  context: DivBlockModelingContext,
  name: String,
  valueConverter: @escaping (T) -> String,
  defaultValue: T
) -> Binding<T> {
  context.variableTracker?.onVariablesUsed(
    cardId: context.cardId,
    variables: [DivVariableName(rawValue: name)]
  )

  let expressionResolver = context.expressionResolver
  return Binding(
    name: name,
    getValue: { expressionResolver.getVariableValue($0) ?? defaultValue },
    userInterfaceActionFactory: { name, value in
      URL(string: "div-action://set_variable?name=\(name)&value=\(valueConverter(value))")
        .flatMap { DivAction(logId: "binding", url: .value($0)) }?
        .uiAction(context: context)
    }
  )
}
