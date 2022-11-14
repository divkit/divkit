// Copyright 2022 Yandex LLC. All rights reserved.

import CoreImage

public enum ImageGeneratorType: FilterProtocol {
  case constantColor(color: CIColor)

  public var name: String {
    switch self {
    case .constantColor:
      return "CIConstantColorGenerator"
    }
  }

  public var parameters: [String: Any] {
    switch self {
    case let .constantColor(color):
      return [kCIInputColorKey: color]
    }
  }

  private var filter: CIFilter? {
    CIFilter(name: name, parameters: parameters)
  }

  public var imageGenerator: ImageGenerator {
    { filter?.outputImage }
  }
}
