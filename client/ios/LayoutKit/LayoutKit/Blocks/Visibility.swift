public enum Visibility {
  case visible
  case notVisible
}

extension Visibility {
  public var isVisible: Bool {
    switch self {
    case .visible: return true
    case .notVisible: return false
    }
  }
}
