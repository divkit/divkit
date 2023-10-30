import LayoutKit

extension BlockError: Equatable {
  public static func ==(lhs: BlockError, rhs: BlockError) -> Bool {
    lhs.message == rhs.message
  }
}
