import Foundation
import VGSL

@frozen
public enum DivCardUpdateReason {
  case patch(DivCardID, DivPatch)
  case timer(DivCardID)
  case state(DivCardID)
  case variable([DivCardID: Set<DivVariableName>])
  case external

  var isVariable: Bool {
    switch self {
    case .variable:
      true
    default:
      false
    }
  }
}

extension [DivCardUpdateReason] {
  /// Returns `true` when the batch contains at least one reason that is not `.variable`.
  /// An empty batch (initial layout) also returns `true` so the counter resets on first render.
  /// Variable-only batches originate from `layout_provider` itself and must NOT reset the counter,
  /// allowing the circular-update guard to accumulate across them.
  @_spi(Internal)
  public var hasNonVariableReason: Bool {
    isEmpty || contains(where: { !$0.isVariable })
  }
}
