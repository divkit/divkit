import Foundation
import VGSL

@preconcurrency @MainActor
final class AsyncDataImageHolder: ImageHolder {
  private(set) weak var image: Image?

  let placeholder: ImagePlaceholder?

  private let imageData: ImageData
  private let imageProcessingQueue: OperationQueueType
  private let imageDecoder: (@Sendable (_ data: Data) -> Image?)?

  nonisolated var debugDescription: String {
    onMainThreadSync {
      let imagePart = if let image {
        String(format: "%.0fx%.0f", image.size.width, image.size.height)
      } else {
        "nil"
      }
      return "AsyncDataImageHolder(image=\(imagePart), placeholder=\(dbgStr(placeholder?.debugDescription)), customDecoder=\(imageDecoder != nil), queue=\(imageProcessingQueue), imageData=\(String(describing: imageData)))"
    }
  }

  init(
    data: ImageData,
    imageProcessingQueue: OperationQueueType,
    imageDecoder: (@Sendable (_ data: Data) -> Image?)? = nil,
    placeholder: ImagePlaceholder? = nil
  ) {
    self.imageData = data
    self.imageProcessingQueue = imageProcessingQueue
    self.imageDecoder = imageDecoder
    self.placeholder = placeholder
  }

  func requestImageWithCompletion(
    _ completion: @escaping @MainActor (Image?) -> Void
  ) -> Cancellable? {
    imageData.makeImage(
      queue: imageProcessingQueue,
      decoder: imageDecoder
    ) { [weak self] image in
      guard let self else { return }
      completion(image)
      self.image = image
    }

    return nil
  }

  func reused(
    with placeholder: ImagePlaceholder?,
    remoteImageURL url: URL?
  ) -> (any ImageHolder)? {
    (self.placeholder === placeholder && url == nil) ? self : nil
  }

  func equals(_ other: any ImageHolder) -> Bool {
    guard let other = other as? Self else {
      return false
    }

    return self.imageData == other.imageData && self.imageProcessingQueue === other
      .imageProcessingQueue
  }
}

extension ImagePlaceholder {
  public func toAsyncDataImageHolder(
    imageProcessingQueue: OperationQueueType,
    decoder: (@Sendable (Data) -> Image?)? = nil
  ) -> ImageHolder {
    switch self {
    case .image, .color, .view:
      toImageHolder()
    case let .imageData(imageData):
      AsyncDataImageHolder(
        data: imageData,
        imageProcessingQueue: imageProcessingQueue,
        imageDecoder: decoder
      )
    }
  }
}
