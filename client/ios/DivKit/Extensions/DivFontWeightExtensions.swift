import BasePublic

extension DivFontWeight {
  var fontWeight: Font.Weight {
    switch self {
    case .light:
      return .light
    case .regular:
      return .regular
    case .medium:
      return .medium
    case .bold:
      return .bold
    }
  }
}
