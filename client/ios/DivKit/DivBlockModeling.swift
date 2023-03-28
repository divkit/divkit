import Foundation

import CommonCorePublic
import LayoutKit

public protocol DivBlockModeling {
  func makeBlock(context: DivBlockModelingContext) throws -> Block
}

public struct DivBlockModelingError: Error, CustomStringConvertible, Equatable {
  public let description: String

  init(_ message: String, path: UIElementPath) {
    description = "\(message) [\(path)]"
    DivKitLogger.error(description)
  }
}

public struct DivBlockModelingWarning: CustomStringConvertible {
  public let description: String

  init(_ message: String, path: UIElementPath) {
    description = "\(message) [\(path)]"
    DivKitLogger.warning(description)
  }
}
