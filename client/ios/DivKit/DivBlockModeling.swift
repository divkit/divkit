import Foundation

import CommonCore
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
