import Foundation
import VGSL

final class ImageWithPreviewHolder: ImageHolder {
  private let mainHolder: ImageHolder
  private let previewHolder: ImageHolder

  var image: Image? { mainHolder.image ?? previewHolder.image?.firstFrame }
  var placeholder: ImagePlaceholder? { mainHolder.placeholder }

  var debugDescription: String {
    "ImageWithPreviewHolder(main: \(mainHolder), preview: \(previewHolder))"
  }

  init(mainHolder: ImageHolder, previewHolder: ImageHolder) {
    self.mainHolder = mainHolder
    self.previewHolder = previewHolder
  }

  func requestImageWithCompletion(_ completion: @escaping @MainActor (Image?) -> Void)
    -> Cancellable? {
    var mainLoaded = false

    let previewCancellable = previewHolder.requestImageWithCompletion { image in
      if !mainLoaded, let image {
        completion(image.firstFrame)
      }
    }

    let mainCancellable = mainHolder.requestImageWithCompletion { image in
      mainLoaded = true
      previewCancellable?.cancel()
      completion(image)
    }

    return CompositeCancellable([previewCancellable, mainCancellable])
  }

  func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    mainHolder.reused(with: placeholder, remoteImageURL: remoteImageURL) != nil ? self : nil
  }

  func equals(_ other: ImageHolder) -> Bool {
    guard let other = other as? ImageWithPreviewHolder else { return false }
    return mainHolder.equals(other.mainHolder) && previewHolder.equals(other.previewHolder)
  }
}

#if os(iOS)
extension Image {
  fileprivate var firstFrame: Image {
    images?.first ?? self
  }
}
#else
extension Image {
  fileprivate var firstFrame: Image { self }
}
#endif

private final class CompositeCancellable: Cancellable {
  private let cancellables: [Cancellable]

  init(_ cancellables: [Cancellable?]) {
    self.cancellables = cancellables.compactMap { $0 }
  }

  func cancel() {
    cancellables.forEach { $0.cancel() }
  }
}
