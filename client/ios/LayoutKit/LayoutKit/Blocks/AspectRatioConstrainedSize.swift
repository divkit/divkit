import CoreGraphics
import Foundation
import VGSL

struct AspectRatioConstrainedSize {
  let width: CGFloat
  let height: CGFloat

  static func calculate(
    imageSize: CGSize,
    widthConstraints: (min: CGFloat, max: CGFloat),
    heightConstraints: (min: CGFloat, max: CGFloat)
  ) -> AspectRatioConstrainedSize {
    let aspectRatio = imageSize.aspectRatio

    guard let aspectRatio, aspectRatio != 0 else {
      return AspectRatioConstrainedSize(
        width: widthConstraints.min,
        height: heightConstraints.min
      )
    }

    let widthConstrainedByWidth = clamp(
      imageSize.width,
      min: widthConstraints.min,
      max: widthConstraints.max
    )

    let heightForConstrainedWidth = widthConstrainedByWidth / aspectRatio

    if heightForConstrainedWidth <= heightConstraints.max,
       heightForConstrainedWidth >= heightConstraints.min {
      return AspectRatioConstrainedSize(
        width: widthConstrainedByWidth,
        height: heightForConstrainedWidth
      )
    }

    let heightConstrainedByHeight = clamp(
      imageSize.height,
      min: heightConstraints.min,
      max: heightConstraints.max
    )
    let widthForConstrainedHeight = heightConstrainedByHeight * aspectRatio

    return AspectRatioConstrainedSize(
      width: widthForConstrainedHeight,
      height: heightConstrainedByHeight
    )
  }
}
