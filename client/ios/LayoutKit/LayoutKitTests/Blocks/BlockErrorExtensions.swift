import LayoutKit

extension BlockError: Equatable {
  public static func == (lhs: BlockError, rhs: BlockError) -> Bool {
    return lhs.message == rhs.message
  }
}
