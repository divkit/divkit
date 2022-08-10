import Foundation

public struct TooltipInfo: Equatable {
  let id: String
  let showsOnStart: Bool
  let multiple: Bool

  public init(id: String, showsOnStart: Bool, multiple: Bool) {
    self.id = id
    self.showsOnStart = showsOnStart
    self.multiple = multiple
  }
}
