import CoreGraphics

/// A relative request, resolved against the view's position when the request is applied.
public enum ScrollDelta: Equatable, Sendable {
  case items(Int, wraps: Bool)
  case offset(CGFloat, wraps: Bool)

  func destinationIndex(from index: CGFloat, count: Int, wraps forceWrap: Bool = false) -> CGFloat {
    guard case let .items(delta, wraps) = self, count > 0 else {
      return index
    }
    let value = Int(index) + delta
    if wraps || forceWrap {
      let remainder = value % count
      return CGFloat(remainder < 0 ? remainder + count : remainder)
    }
    return CGFloat(max(0, min(value, count - 1)))
  }

  func destinationOffset(from offset: CGFloat, range: CGFloat) -> CGFloat {
    guard case let .offset(delta, wraps) = self, range > 0 else {
      return 0
    }
    let value = offset + delta
    if wraps {
      let remainder = value.truncatingRemainder(dividingBy: range)
      return remainder < 0 ? remainder + range : remainder
    }
    return max(0, min(value, range))
  }
}
