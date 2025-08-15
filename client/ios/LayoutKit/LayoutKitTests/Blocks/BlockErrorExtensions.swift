import LayoutKit

extension BlockError: Swift.Equatable {
  public static func ==(lhs: BlockError, rhs: BlockError) -> Bool {
    lhs.message == rhs.message
  }
}
