// Copyright 2019 Yandex LLC. All rights reserved.

import BaseUIPublic

public enum ImagePlaceholder: Equatable, CustomDebugStringConvertible {
  case image(Image)
  case color(Color)
  case view(ViewType)
}

extension ImagePlaceholder {
  public var debugDescription: String {
    switch self {
    case let .image(image):
      return "Image(\(image.size.width) x \(image.size.height))"
    case let .color(color):
      return "Color(" + color.debugDescription + ")"
    case let .view(view):
      #if canImport(UIKit)
      return "View(" + view.debugDescription + ")"
      #else
      return "View()"
      #endif
    }
  }

  public static func ==(lhs: ImagePlaceholder, rhs: ImagePlaceholder) -> Bool {
    switch (lhs, rhs) {
    case let (.image(lImage), .image(rImage)):
      return imagesDataAreEqual(lImage, rImage)
    case let (.color(lColor), .color(rColor)):
      return lColor == rColor
    case let (.view(lView), .view(rView)):
      #if canImport(UIKit)
      return lView == rView
      #else
      return lView === rView
      #endif
    case (.image, _), (.color, _), (.view, _):
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
    case let (.view(lView)?, .view(rView)?):
      return lView === rView
    case (.none, .none):
      return true
    case (.image?, _), (.color?, _), (.view?, _), (.none, _):
      return false
    }
  }
}
