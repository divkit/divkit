import LayoutKit

public protocol DivCustomBlockFactory {
  func makeBlock(data: DivCustomData, context: DivBlockModelingContext) -> Block
}

public struct EmptyDivCustomBlockFactory: DivCustomBlockFactory {
  public init() {}

  public func makeBlock(data: DivCustomData, context: DivBlockModelingContext) -> Block {
    context.warningsStorage.add(
      DivBlockModelingWarning(
        "No block factory for DivCustom: \(data.name)",
        path: context.parentPath
      )
    )
    return EmptyBlock.zeroSized
  }
}
