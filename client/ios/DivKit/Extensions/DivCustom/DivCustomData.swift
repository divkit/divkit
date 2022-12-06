import LayoutKit

public struct DivCustomData {
  public let name: String
  public let data: [String: Any]
  public let children: [Block]
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  init(
    name: String,
    data: [String: Any],
    children: [Block] = [],
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) {
    self.name = name
    self.data = data
    self.children = children
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
  }
}
