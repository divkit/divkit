import Foundation

import CommonCorePublic

public final class DebugInfoBlock: WrapperBlock, LayoutCachingDefaultImpl {
  public static let showOverlayURL = URL(string: "debugInfo://show")!

  public let child: Block
  public let showDebugInfo: Action

  public init(
    child: Block,
    showDebugInfo: @escaping Action
  ) {
    self.child = child
    self.showDebugInfo = showDebugInfo
  }

  public func makeCopy(wrapping block: Block) -> DebugInfoBlock {
    DebugInfoBlock(
      child: block,
      showDebugInfo: showDebugInfo
    )
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? DebugInfoBlock else {
      return false
    }
    return self == other
  }
}

extension DebugInfoBlock: Equatable {
  public static func ==(lhs: DebugInfoBlock, rhs: DebugInfoBlock) -> Bool {
    lhs.child == rhs.child
  }
}

extension DebugInfoBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    "DebugInfoBlock child: \(child)"
  }
}
