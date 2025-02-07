import VGSL

extension DivBlendMode {
  var tintMode: TintMode {
    switch self {
    case .sourceIn:
      .sourceIn
    case .sourceAtop:
      .sourceAtop
    case .darken:
      .darken
    case .lighten:
      .lighten
    case .multiply:
      .multiply
    case .screen:
      .screen
    }
  }
}
