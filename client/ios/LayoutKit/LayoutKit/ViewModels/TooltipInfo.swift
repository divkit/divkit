import Foundation

public struct TooltipInfo: Equatable {
  public let id: String
  public let showsOnStart: Bool
  public let multiple: Bool

  public init(id: String, showsOnStart: Bool, multiple: Bool) {
    self.id = id
    self.showsOnStart = showsOnStart
    self.multiple = multiple
  }
}
