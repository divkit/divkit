import Foundation

import VGSL

public struct ActionLimiter {
  let canSend: () -> Bool
  let markSent: Action

  public init(
    canSend: @escaping () -> Bool,
    markSent: @escaping Action
  ) {
    self.canSend = canSend
    self.markSent = markSent
  }
}
