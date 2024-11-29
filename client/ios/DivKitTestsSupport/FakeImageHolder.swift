import Foundation

import DivKit
import VGSL

public final class FakeImageHolderFactory: DivImageHolderFactory {
  public init() {}

  public func make(_: URL?, _: ImagePlaceholder?) -> ImageHolder {
    FakeImageHolder()
  }
}

public final class FakeImageHolder: ImageHolder {
  public init() {}

  public var image: Image? {
    nil
  }

  public var placeholder: ImagePlaceholder? {
    nil
  }

  public func requestImageWithCompletion(_: @escaping ((Image?) -> Void)) -> Cancellable? {
    nil
  }

  public func reused(with _: ImagePlaceholder?, remoteImageURL _: URL?) -> ImageHolder? {
    nil
  }

  public func equals(_: ImageHolder) -> Bool {
    true
  }

  public var debugDescription: String {
    "FakeImageHolder"
  }
}
