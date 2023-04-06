import LayoutKit

extension DivSelect: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    assertionFailure("Not implemented")
    return SeparatorBlock()
  }
}
