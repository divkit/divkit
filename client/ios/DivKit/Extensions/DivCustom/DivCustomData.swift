import LayoutKit

public struct DivCustomData {
  public let name: String
  public let data: [String: Any]
  public let children: [Block]

  init(name: String, data: [String: Any], children: [Block] = []) {
    self.name = name
    self.data = data
    self.children = children
  }
}
