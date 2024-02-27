import BasePublic

extension DivFontWeight {
  var fontWeight: Font.Weight {
    switch self {
    case .light:
      .light
    case .regular:
      .regular
    case .medium:
      .medium
    case .bold:
      .bold
    }
  }
}
