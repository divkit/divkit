import Foundation
import VGSL

/// Manages safe area system variables.
///
/// Use ``DivSafeAreaManager`` to provide safe area insets into `DivKit`.
/// The manager converts edge insets into predefined variables
/// (`safe_area_top`, `safe_area_bottom`, `safe_area_left`, `safe_area_right`)
/// and stores them in the associated global storage.
///
/// You can update safe area values at runtime by calling:
/// ```swift
/// divKitComponents.safeAreaManager.setEdgeInsets(insets)
/// ```
///
/// In JSON, you can use these variables to adapt layouts based on
/// the current safe area. For example:
/// ```json
/// "height": "@{safe_area_bottom + 16}"
/// ```
public final class DivSafeAreaManager: DivSystemPropertyManager {
  private let storage: DivVariableStorage

  init(storage: DivVariableStorage) {
    self.storage = storage
  }

  /// Sets or updates safe area variables.
  ///
  /// Call this method to provide initial safe area values to `DivKit`
  /// and whenever the host application's safe area insets change.
  /// The method converts the given insets into system variables and
  /// writes them into the underlying global storage.
  ///
  /// If the variables were not previously set, they will be created.
  /// If they exist, their values will be updated.
  public func setEdgeInsets(_ insets: EdgeInsets) {
    let variables = makeVariables(insets)
    storage.put(variables, notifyObservers: true)
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
