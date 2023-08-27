import Foundation

import CommonCorePublic
import LayoutKit

public protocol DivBlockModeling {
  func makeBlock(context: DivBlockModelingContext) throws -> Block
}

struct DivBlockModelingError: Error, DivError {
  public let kind: DivErrorKind = .blockModeling
  public let message: String
  public let path: UIElementPath
  public let causes: [DivError]
  public let level: DivErrorLevel = .error

  init(_ message: String, path: UIElementPath, causes: [DivError] = []) {
    self.message = message
    self.path = path
    self.causes = causes
    DivKitLogger.error(description)
  }
}

struct DivBlockModelingWarning: DivError {
  public let kind = DivErrorKind.blockModeling
  public let message: String
  public let path: UIElementPath
  public let level: DivErrorLevel = .warning

  init(_ message: String, path: UIElementPath) {
    self.message = message
    self.path = path
    DivKitLogger.warning(description)
  }
}

struct DivExpressionError: Error, DivError {
  public let kind = DivErrorKind.expression
  public let message: String
  public let path: UIElementPath
  public let level: DivErrorLevel = .error

  init(_ error: ExpressionError, path: UIElementPath) {
    self.message = error.description
    self.path = path
    DivKitLogger.error(description)
  }
}

struct DivUnknownError: Error, DivError {
  public let kind = DivErrorKind.unknown
  public let message: String
  public let path: UIElementPath
  public let level: DivErrorLevel = .error

  init(_ error: Error, path: UIElementPath) {
    self.message = (error as CustomStringConvertible).description
    self.path = path
    DivKitLogger.error(description)
  }
}
