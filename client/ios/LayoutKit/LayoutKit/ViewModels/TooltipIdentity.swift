import VGSL

public struct TooltipIdentity: Hashable {
  public let id: String
  public let scopePath: UIElementPath?

  public init(id: String, scopePath: UIElementPath?) {
    self.id = id
    self.scopePath = scopePath
  }
}
