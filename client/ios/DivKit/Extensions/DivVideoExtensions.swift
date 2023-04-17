import LayoutKit

extension DivVideo: DivBlockModeling {
  public func makeBlock(context _: DivBlockModelingContext) throws -> Block {
    assertionFailure("Not implemented")
    return SeparatorBlock()
  }
}
