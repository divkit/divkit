import Foundation

public protocol DivVariableUpdater {
  func update(cardId: DivCardID, name: DivVariableName, value: String)
}
