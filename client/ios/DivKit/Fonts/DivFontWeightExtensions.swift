extension DivFontWeight {
  func toInt() -> Int {
    switch self {
    case .light:
      300
    case .regular:
      400
    case .medium:
      500
    case .bold:
      700
    }
  }
}
