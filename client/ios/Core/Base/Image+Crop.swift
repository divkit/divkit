// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

extension Image {
  public func crop(rect cropRect: RelativeRect, preserveScale: Bool = false) -> Image? {
    let newScale = preserveScale ? scale : 1
    let absoluteCropRect = cropRect.absolute(in: CGRect(origin: .zero, size: size))
    let cropRect = absoluteCropRect.applying(orientationTransform.scaled(by: newScale))
    guard let imgRef = cgImg?.cropping(to: cropRect) else {
      return nil
    }
    return Image(cgImage: imgRef, scale: newScale, orientation: imageOrientation)
  }
}
