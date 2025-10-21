import DivKit
import Foundation
import VGSL

public final class FakeImageHolderFactory: DivImageHolderFactory {
  public init() {}

  public func make(_: URL?, _: ImagePlaceholder?) -> ImageHolder {
    FakeImageHolder()
  }
}

public final class FakeImageHolder: ImageHolder {
  public var image: Image?
  public var placeholder: ImagePlaceholder?

  public var debugDescription: String {
    "FakeImageHolder(image: \(String(describing: image)), placeholder: \(String(describing: placeholder)))"
  }

  public init(image: Image? = nil, placeholder: ImagePlaceholder? = nil) {
    self.image = image
    self.placeholder = placeholder
  }

  public func requestImageWithCompletion(_ completion: @escaping @MainActor (Image?) -> Void)
    -> Cancellable? {
    completion(image)
    return nil
  }

  public func reused(with _: ImagePlaceholder?, remoteImageURL _: URL?) -> ImageHolder? {
    nil
  }

  public func equals(_ other: ImageHolder) -> Bool {
    guard let other = other as? FakeImageHolder else { return false }
    return self.image === other.image
  }
}
