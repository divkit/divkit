import BaseUIPublic

extension DivFontWeight {
  var fontWeight: FontWeight {
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

extension DivFontFamily {
  var fontFamily: FontFamily {
    switch self {
    case .display:
      return .YSDisplay
    case .text:
      return .YSText
    }
  }
}
