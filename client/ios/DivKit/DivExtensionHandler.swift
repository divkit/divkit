import Foundation

import BaseUIPublic
import CommonCorePublic
import LayoutKit

public protocol DivExtensionHandler: AccessibilityContaining {
  var id: String { get }

  func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block

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
