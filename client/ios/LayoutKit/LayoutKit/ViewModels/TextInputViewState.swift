import Foundation

/// Carries a pending cursor-position command from a `set_cursor_position` action.
/// Only action-driven; the view never writes back, so user caret moves do NOT update storage.
public struct TextInputViewState: ElementState, Equatable {
  public struct Selection: Equatable {
    public let start: Int
    public let end: Int

    public init(start: Int, end: Int) {
      self.start = start
      self.end = end
    }
  }

  public let pendingSelection: Selection?

  public init(pendingSelection: Selection?) {
    self.pendingSelection = pendingSelection
  }
}
