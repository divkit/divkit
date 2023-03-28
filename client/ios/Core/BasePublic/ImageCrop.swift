// Copyright 2022 Yandex LLC. All rights reserved.

import CoreImage

public enum ImageCropType: FilterProtocol {
  case crop(rect: CIVector)

  public var name: String {
    switch self {
    case .crop:
      return "CICrop"
    }
  }

  public var parameters: [String: Any] {
    switch self {
    case let .crop(rect):
      return ["inputRectangle": rect]
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
