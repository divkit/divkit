// Copyright 2019 Yandex LLC. All rights reserved.

import BaseUI

@frozen
public enum ImagePlaceholder: Equatable, CustomDebugStringConvertible {
  case image(Image)
  case color(Color)
}

extension ImagePlaceholder {
  public var debugDescription: String {
    switch self {
    case let .image(image):
      return "Image(\(image.size.width) x \(image.size.height))"
    case let .color(color):
      return "Color(" + color.debugDescription + ")"
    }
  }

  public static func ==(lhs: ImagePlaceholder, rhs: ImagePlaceholder) -> Bool {
    switch (lhs, rhs) {
    case let (.image(lImage), .image(rImage)):
      return imagesDataAreEqual(lImage, rImage)
    case let (.color(lColor), .color(rColor)):
      return lColor == rColor
    case (.image, _), (.color, _):
      return false
    }
  }
}

extension Optional where Wrapped == ImagePlaceholder {
  public static func ===(lhs: ImagePlaceholder?, rhs: ImagePlaceholder?) -> Bool {
    switch (lhs, rhs) {
    case let (.image(lImage)?, .image(rImage)?):
      return lImage === rImage
    case let (.color(lColor)?, .color(rColor)?):
      return lColor == rColor
    case (.none, .none):
      return true
    case (.image?, _), (.color?, _), (.none, _):
      return false
    }
  }
}
