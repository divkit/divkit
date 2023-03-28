// Copyright 2022 Yandex LLC. All rights reserved.

import CoreImage

public enum ImageBlurType: FilterProtocol {
  case gaussian(radius: CGFloat)

  public var name: String {
    switch self {
    case .gaussian:
      return "CIGaussianBlur"
    }
  }

  public var parameters: [String: Any] {
    switch self {
    case let .gaussian(radius):
      return [kCIInputRadiusKey: radius]
    }
  }

  public var imageFilter: ImageFilter {
    { image in
      let parameters: [String: Any] = [
        kCIInputImageKey: image,
      ]
      let filter = CIFilter(name: name, parameters: self.parameters.merging(parameters) { $1 })
      return filter?.outputImage
    }
  }
}
