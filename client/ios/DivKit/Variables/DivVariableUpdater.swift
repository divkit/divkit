import BasePublic
import LayoutKit

/// Deprecated. Use `DivVariablesStorage`.
public protocol DivVariableUpdater {
  func update(cardId: DivCardID, name: DivVariableName, value: String)
  func update(path: UIElementPath, name: DivVariableName, value: String)
}

extension DivVariableUpdater {
  func update(path: UIElementPath, name: DivVariableName, value: String) {
    update(cardId: path.cardId, name: name, value: value)
  }
}
