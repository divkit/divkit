import Foundation

import VGSL

public struct VisibilityParams: Equatable {
  public let actions: [VisibilityAction]
  public let isVisible: Bool
  public let lastVisibleArea: Property<Int>
  public let scheduler: Scheduling

  public init(
    actions: [VisibilityAction],
    isVisible: Bool,
    lastVisibleArea: Property<Int>,
    scheduler: Scheduling
  ) {
    self.actions = actions
    self.isVisible = isVisible
    self.lastVisibleArea = lastVisibleArea
    self.scheduler = scheduler
  }

  public static func ==(lhs: VisibilityParams, rhs: VisibilityParams) -> Bool {
    lhs.actions == rhs.actions
      && lhs.isVisible == rhs.isVisible
  }
}
