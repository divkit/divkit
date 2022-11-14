// Copyright 2022 Yandex LLC. All rights reserved.

import CoreImage

public enum ImageComposerType: FilterProtocol {
  case sourceAtop
  case sourceIn
  case darken
  case lighten
  case multiply
  case screen

  public var name: String {
    switch self {
    case .sourceAtop:
      return "CISourceAtopCompositing"
    case .sourceIn:
      return "CISourceInCompositing"
    case .darken:
      return "CIDarkenBlendMode"
    case .lighten:
      return "CILightenBlendMode"
    case .multiply:
      return "CIMultiplyCompositing"
    case .screen:
      return "CIScreenBlendMode"
    }
  }

  public var parameters: [String: Any] {
    switch self {
    case .sourceAtop, .sourceIn, .darken, .lighten, .multiply, .screen:
      return [:]
    }
  }

  public var imageComposer: ImageComposer {
    { backgroundImage in
      { image in
        let parameters: [String: Any] = [
          kCIInputImageKey: image,
          kCIInputBackgroundImageKey: backgroundImage,
        ]
        let filter = CIFilter(name: name, parameters: self.parameters.merging(parameters) { $1 })
        return filter?.outputImage
      }
    }
  }
}
