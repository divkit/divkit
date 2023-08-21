import LayoutKit

/// A protocol that allows you to handle situations where you require a complex layout that cannot
/// be fully expressed using standard components in DivKit.
///
/// By implementing `DivCustomBlockFactory`, you can define your custom block factory, responsible
/// for creating blocks based on custom data and context.
///
/// To conform to this protocol, create a type that provides an implementation for the
/// ``makeBlock(data:context:)`` method. This method should return a ``LayoutKit.Block`` that
/// represents your custom layout.
///
/// For more information and usage examples, you can refer to the [example implementation](https://github.com/divkit/divkit/blob/main/client/ios/DivKitPlayground/DivKit/PlaygroundDivCustomBlockFactory.swift).
public protocol DivCustomBlockFactory {
  /// Creates a custom block based on the provided custom data and context.
  ///
  /// - Parameters:
  ///   - data: The custom data used to generate the block layout.
  ///   - context: The context in which the block is being modeled.
  ///
  /// - Returns: A `Block` that represents the custom layout defined by this factory.
  func makeBlock(data: DivCustomData, context: DivBlockModelingContext) -> Block
}

public struct EmptyDivCustomBlockFactory: DivCustomBlockFactory {
  public init() {}

  public func makeBlock(data: DivCustomData, context: DivBlockModelingContext) -> Block {
    context.addError(
      level: .error,
      message: "No block factory for DivCustom: \(data.name)"
    )
    return EmptyBlock.zeroSized
  }
}
