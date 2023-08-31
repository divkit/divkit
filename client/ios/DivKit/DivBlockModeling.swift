import Foundation

import LayoutKit

public protocol DivBlockModeling {
  func makeBlock(context: DivBlockModelingContext) throws -> Block
}
