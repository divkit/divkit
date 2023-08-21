// Copyright 2019 Yandex LLC. All rights reserved.
import Foundation

import BaseUIPublic

public enum ImagePlaceholder: Equatable, CustomDebugStringConvertible {
  case image(Image)
  case imageData(ImageData)
  case color(Color)
  case view(ViewType)
}

extension ImagePlaceholder {
  public func toImageHolder() -> ImageHolder {
    switch self {
    case let .image(image):
      return image
    case let .imageData(imageData):
      return ImageDataHolder(imageData: imageData)
    case let .color(color):
      return ColorHolder(color: color)
    case let .view(view):
      return ViewImageHolder(view: view)
    }
  }

  public var debugDescription: String {
    switch self {
    case let .image(image):
      return "Image(\(image.size.width) x \(image.size.height))"
    case .imageData:
      return "Image data"
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
    case let (.imageData(lData), .imageData(rData)):
      return lData == rData
    case let (.color(lColor), .color(rColor)):
      return lColor == rColor
    case let (.view(lView), .view(rView)):
      #if canImport(UIKit)
      return lView == rView
      #else
      return lView === rView
      #endif
    case (.image, _), (.color, _), (.view, _), (.imageData, _):
      return false
    }
  }
}

extension Optional where Wrapped == ImagePlaceholder {
  public static func ===(lhs: ImagePlaceholder?, rhs: ImagePlaceholder?) -> Bool {
    switch (lhs, rhs) {
    case let (.image(lImage)?, .image(rImage)?):
      return lImage === rImage
    case let (.imageData(lData), .imageData(rData)):
      return lData == rData
    case let (.color(lColor)?, .color(rColor)?):
      return lColor == rColor
    case let (.view(lView)?, .view(rView)?):
      return lView === rView
    case (.none, .none):
      return true
    case (.image?, _), (.color?, _), (.view?, _), (.imageData?, _), (.none, _):
      return false
    }
  }
}

public struct ImageData: Equatable {
  private let base64: String
  private let highPriority: Bool

  public init(base64: String, highPriority: Bool = false) {
    self.base64 = base64
    self.highPriority = highPriority
  }

  public func makeImage(queue: OperationQueueType, completion: @escaping (Image) -> Void) {
    let action = {
      if let image = makeImage() {
        onMainThread {
          completion(image)
        }
      }
    }
    if highPriority {
      action()
    } else {
      queue.addOperation(action)
    }
  }
  public func makeImage() -> Image? {
    decode(base64: base64).flatMap(Image.init(data:))
  }
}

fileprivate func decode(base64: String) -> Data? {
  if let data = Data(base64Encoded: base64) {
    return data
  }
  if let url = URL(string: base64),
     let data = try? Data(contentsOf: url) {
    return data
  }
  return nil
}
