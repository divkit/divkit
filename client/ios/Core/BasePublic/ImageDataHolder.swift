import Foundation

final class ImageDataHolder: ImageHolder {
  private let imageData: ImageData
  public let image: Image?
  public var placeholder: ImagePlaceholder? { .imageData(imageData) }

  public init(imageData: ImageData) {
    self.imageData = imageData
    self.image = imageData.makeImage()
  }

  public func requestImageWithCompletion(_ completion: @escaping ((Image?) -> Void))
    -> Cancellable? {
    onMainThread {
      completion(self.image)
    }
    return nil
  }

  public func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    (placeholder === .imageData(imageData) && remoteImageURL == nil) ? self : nil
  }

  public func equals(_ other: ImageHolder) -> Bool {
    (other as? ImageDataHolder)?.imageData == imageData
  }

  public var debugDescription: String {
    "ImageDataHolder"
  }
}
