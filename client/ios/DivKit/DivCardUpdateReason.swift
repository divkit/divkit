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
