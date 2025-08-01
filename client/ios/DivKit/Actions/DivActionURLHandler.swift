import Foundation
import VGSL

public enum DivActionURLHandler {
  //  Deprecated. Use DivCardUpdateReason
  @frozen
  public enum UpdateReason {
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
}
