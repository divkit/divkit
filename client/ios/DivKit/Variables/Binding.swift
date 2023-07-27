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
    let actionContext = context.actionContext
    let expressionResolver = context.expressionResolver
    self.init(
      name: name,
      getValue: { expressionResolver.getVariableValue($0) ?? .zero },
      userInterfaceActionFactory: { name, value in
        URL(string: "div-action://set_variable?name=\(name)&value=\(value.description)").flatMap {
          DivAction(logId: "binding", url: .value($0))
        }?.uiAction(context: actionContext)
      }
    )
  }
}

extension Binding where T == String {
  init(context: DivBlockModelingContext, name: String) {
    let actionContext = context.actionContext
    let expressionResolver = context.expressionResolver
    self.init(
      name: name,
      getValue: { expressionResolver.getVariableValue($0) ?? "" },
      userInterfaceActionFactory: { name, value in
        URL(string: "div-action://set_variable?name=\(name)&value=\(value.percentEncoded())")
          .flatMap {
            DivAction(logId: "binding", url: .value($0))
          }?.uiAction(context: actionContext)
      }
    )
  }
}
