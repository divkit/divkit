#if os(iOS)
import DivKit
import Foundation
import LayoutKit
import VGSL

/// An `ImageHolder` wrapper that crops loaded images to match an expected aspect ratio.
///
/// When the wrapped image holder delivers an image, this holder checks if the image's
/// aspect ratio matches the expected ratio. If they differ, the image is center-cropped
/// to match the expected aspect ratio before being delivered to the completion handler.
final class AspectCorrectionImageHolder: ImageHolder {
  private let wrapped: ImageHolder
  private let expectedAspect: CGFloat
  private let aspectTolerance: CGFloat
  private var croppedImage: Image?

  var image: Image? {
    if let croppedImage {
      return croppedImage
    }
    guard let originalImage = wrapped.image else {
      return nil
    }
    let processed = processImage(originalImage)
    croppedImage = processed
    return processed
  }

  var placeholder: ImagePlaceholder? {
    wrapped.placeholder
  }

  var debugDescription: String {
    "AspectCorrectionImageHolder(expectedAspect: \(expectedAspect), wrapped: \(wrapped.debugDescription))"
  }

  init(
    wrapped: ImageHolder,
    expectedAspect: CGFloat,
    aspectTolerance: CGFloat
  ) {
    self.wrapped = wrapped
    self.expectedAspect = expectedAspect
    self.aspectTolerance = aspectTolerance
  }

  @discardableResult
  func requestImageWithCompletion(
    _ completion: @escaping @MainActor (Image?) -> Void
  ) -> Cancellable? {
    wrapped.requestImageWithCompletion { [weak self] image in
      guard let self else {
        completion(image)
        return
      }
      if let image {
        let processed = self.processImage(image)
        self.croppedImage = processed
        completion(processed)
      } else {
        completion(nil)
      }
    }
  }

  func requestImageWithSource(
    _ completion: @escaping CompletionHandlerWithSource
  ) -> Cancellable? {
    wrapped.requestImageWithSource { [weak self] result in
      guard let self else {
        completion(result)
        return
      }
      if let (image, source) = result {
        let processed = self.processImage(image)
        self.croppedImage = processed
        completion((processed, source))
      } else {
        completion(nil)
      }
    }
  }

  func reused(
    with placeholder: ImagePlaceholder?,
    remoteImageURL: URL?
  ) -> ImageHolder? {
    guard let reusedWrapped = wrapped.reused(
      with: placeholder,
      remoteImageURL: remoteImageURL
    ) else {
      return nil
    }
    return AspectCorrectionImageHolder(
      wrapped: reusedWrapped,
      expectedAspect: expectedAspect,
      aspectTolerance: aspectTolerance
    )
  }

  func equals(_ other: ImageHolder) -> Bool {
    guard let other = other as? AspectCorrectionImageHolder else {
      return false
    }
    return
      expectedAspect == other.expectedAspect &&
      aspectTolerance == other.aspectTolerance &&
      wrapped.equals(other.wrapped)
  }

  private func processImage(_ image: Image) -> Image {
    guard let actualAspect = image.size.aspectRatio,
          abs(expectedAspect - actualAspect) > aspectTolerance,
          let cgImage = image.cgImage else {
      return image
    }

    let imageWidth = CGFloat(cgImage.width)
    let imageHeight = CGFloat(cgImage.height)

    let cropWidth: CGFloat
    let cropHeight: CGFloat

    if actualAspect < expectedAspect {
      cropWidth = imageWidth
      cropHeight = imageWidth / expectedAspect
    } else {
      cropHeight = imageHeight
      cropWidth = imageHeight * expectedAspect
    }

    let cropRect = CGRect(x: 0, y: 0, width: cropWidth, height: cropHeight)

    guard let croppedCGImage = cgImage.cropping(to: cropRect) else {
      return image
    }

    return Image(cgImage: croppedCGImage, scale: image.scale, orientation: image.imageOrientation)
  }
}
#endif
