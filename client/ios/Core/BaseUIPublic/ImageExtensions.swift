// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import BaseTinyPublic

extension Image {
  public var orientationTransform: CGAffineTransform {
    #if os(iOS)
    return imageOrientationTransform(for: imageOrientation, imageSize: size)
    #else
    // orientation not supported for NSImage
    return .identity
    #endif
  }
}

#if os(iOS)
func imageOrientationTransform(
  for imageOrientation: Image.Orientation,
  imageSize size: CGSize
) -> CGAffineTransform {
  switch imageOrientation {
  case .up:
    return .identity
  case .upMirrored:
    return CGAffineTransform(translationX: size.width, y: 0)
      .scaledBy(x: -1, y: 1)
  case .down:
    return CGAffineTransform(translationX: size.width, y: size.height)
      .rotated(by: .pi)
  case .downMirrored:
    return CGAffineTransform(translationX: 0, y: size.height)
      .scaledBy(x: 1, y: -1)
  case .left:
    return CGAffineTransform(translationX: size.height, y: 0)
      .rotated(by: .pi / 2)
  case .leftMirrored:
    return CGAffineTransform(translationX: 0, y: size.width)
      .rotated(by: -.pi / 2)
      .translatedBy(x: size.width, y: 0)
      .scaledBy(x: -1, y: 1)
  case .right:
    return CGAffineTransform(translationX: 0, y: size.width)
      .rotated(by: -.pi / 2)
  case .rightMirrored:
    return CGAffineTransform(translationX: size.height, y: 0)
      .rotated(by: .pi / 2)
      .translatedBy(x: size.width, y: 0)
      .scaledBy(x: -1, y: 1)
  @unknown default:
    return .identity
  }
}
#endif
