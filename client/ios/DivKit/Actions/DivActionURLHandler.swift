import Foundation
import LayoutKit
import VGSL

public enum DivActionURLHandler {
  @frozen
  public enum UpdateReason {
    case patch(DivCardID, DivPatch)
    case timer(DivCardID)
    case state(DivCardID)
    case variable([DivCardID: Set<DivVariableName>])
    case external
  }
}
