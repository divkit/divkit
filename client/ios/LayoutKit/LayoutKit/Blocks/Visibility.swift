public enum Visibility {
  case visible
  case notVisible
}

extension Visibility {
  public var isVisible: Bool {
    switch self {
    case .visible: true
    case .notVisible: false
    }
  }
}
