import BaseTinyPublic
import Foundation

public final class DivSafeAreaManager {
  private let storage: DivVariablesStorage

  public init(storage: DivVariablesStorage) {
    self.storage = storage
  }

  public func setEdgeInsets(_ insets: EdgeInsets) {
    let variables = makeVariables(insets)
    storage.append(variables: variables, triggerUpdate: true)
  }

  private func makeVariables(_ insets: EdgeInsets) -> DivVariables {
    [
      "safe_area_top": .number(insets.top),
      "safe_area_bottom": .number(insets.bottom),
      "safe_area_left": .number(insets.left),
      "safe_area_right": .number(insets.right),
    ]
  }
}
