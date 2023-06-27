import Foundation

import CommonCorePublic
import LayoutKit

public protocol DivBlockModeling {
  func makeBlock(context: DivBlockModelingContext) throws -> Block
}

public struct DivBlockModelingError: Error, CustomStringConvertible, Equatable {
  public let description: String

  init(_ message: String, path: UIElementPath) {
    description = "[\(path)]: \(message)"
    DivKitLogger.error(description)
  }
}

public struct DivBlockModelingWarning: CustomStringConvertible {
  public let description: String

  init(_ message: String, path: UIElementPath) {
    description = "[\(path)]: \(message) "
    DivKitLogger.warning(description)
  }
}

public struct DivBlockModelingRuntimeError: Error, CustomStringConvertible {
  public let description: String

  init(_ payload: ExpressionError, path: UIElementPath) {
    description = "[\(path)]: \(payload.description) "
    DivKitLogger.warning(description)
  }
}
