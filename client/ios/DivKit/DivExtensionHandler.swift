import Foundation

import CommonCorePublic
import LayoutKit

/// The `DivExtensionHandler` protocol enables you to extend the functionality of an existing block
/// in `DivKit`.
///
/// This extension can involve wrapping the block in another one or adding basic properties to
/// enhance its behavior.
/// To implement this protocol, you need to provide a unique identifier (`id`) for your extension
/// and define two methods: `applyBeforeBaseProperties(to:div:context:)` and
/// `applyAfterBaseProperties(to:div:context:)`.
/// These methods allow you to apply additional properties before and after the base properties of
/// the block, respectively.
/// The basic properties include properties from
/// [`DivBase`](https://github.com/divkit/divkit/blob/main/schema/div-base.json).
public protocol DivExtensionHandler: AccessibilityContaining {
  /// A unique identifier for the extension.
  var id: String { get }

  /// Applies additional properties to the block before the base properties.
  ///
  /// - Parameters:
  ///   - block: The block to apply properties to.
  ///   - div: The `DivBase` associated with the block.
  ///   - context: The context in which the block is being modeled.
  ///
  /// - Returns: The modified block with additional properties applied.
  func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block

  /// Applies additional properties to the block after the base properties.
  ///
  /// - Parameters:
  ///   - block: The block to apply properties to.
  ///   - div: The `DivBase` associated with the block.
  ///   - context: The context in which the block is being modeled.
  ///
  /// - Returns: The modified block with additional properties applied.
  func applyAfterBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block
}

extension DivExtensionHandler {
  public func applyBeforeBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    block
  }

  public func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    block
  }

  public func getExtensionParams(_ div: DivBase) -> [String: Any] {
    guard let extensionData = div.extensions?.first(where: { $0.id == id }) else {
      DivKitLogger.error("Extension data not found: \(id)")
      return [:]
    }
    return extensionData.params ?? [:]
  }
}
