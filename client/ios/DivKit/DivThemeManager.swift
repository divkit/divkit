import Foundation
import VGSL

/// Manages theme system variables.
///
/// Use ``DivThemeManager`` to provide the current color scheme
/// (light or dark) into `DivKit`.
/// The manager exposes the selected theme and stores it as a system
/// variable in the associated global storage.
///
/// You can change the theme at runtime by calling:
/// ```swift
/// divKitComponents.themeManager.setTheme(.dark)
/// ```
///
/// In JSON, you can use the `theme` variable to adjust styles
/// based on the current color scheme. For example:
/// ```json
/// "text_color": "@{theme == 'light' ? '#000' : '#FFF'}"
/// ```
public final class DivThemeManager: DivSystemPropertyManager {
  public enum Theme: String {
    case dark, light
  }

  public private(set) var theme: Theme?

  private let storage: DivVariableStorage

  init(storage: DivVariableStorage) {
    self.storage = storage
  }

  /// Sets or updates the current theme.
  ///
  /// Call this method to provide the initial theme value to `DivKit`
  /// and whenever the application's color scheme changes.
  /// The method converts the given theme into a system variable and
  /// writes it into the underlying ``DivVariableStorage`` with
  /// notification enabled.
  ///
  /// If the variable was not previously set, it will be created.
  /// If it exists, its value will be updated.
  public func setTheme(theme: Theme) {
    self.theme = theme
    storage.put(["theme": .string(theme.rawValue)], notifyObservers: true)
  }
}
