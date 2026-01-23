extension DivVisibility {
  static func min(_ lhs: DivVisibility, _ rhs: DivVisibility) -> DivVisibility {
    guard lhs != rhs else { return lhs }

    return switch (lhs, rhs) {
    case (.gone, _): lhs
    case (.invisible, .visible): lhs
    default: rhs
    }
  }
}
