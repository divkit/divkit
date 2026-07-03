import Foundation
import VGSL

public struct TooltipInfo: Equatable {
  public let id: String
  public let showsOnStart: Bool
  public let multiple: Bool
  public let scopePath: UIElementPath?
  public let onError: ((String) -> Void)?

  public var identity: TooltipIdentity {
    TooltipIdentity(id: id, scopePath: scopePath)
  }

  public init(
    id: String,
    showsOnStart: Bool,
    multiple: Bool,
    scopePath: UIElementPath? = nil,
    onError: ((String) -> Void)? = nil
  ) {
    self.id = id
    self.showsOnStart = showsOnStart
    self.multiple = multiple
    self.scopePath = scopePath
    self.onError = onError
  }

  public static func ==(lhs: TooltipInfo, rhs: TooltipInfo) -> Bool {
    lhs.id == rhs.id &&
      lhs.showsOnStart == rhs.showsOnStart &&
      lhs.multiple == rhs.multiple &&
      lhs.scopePath == rhs.scopePath
  }
}
