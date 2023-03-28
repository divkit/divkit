// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public final class NilImageHolder: ImageHolder {
  public var image: Image? { nil }
  public var placeholder: ImagePlaceholder? { nil }
  public var debugDescription: String { "NilImageHolder" }

  public init() {}

  public func requestImageWithCompletion(_ completion: @escaping ((Image?) -> Void))
    -> Cancellable? {
    onMainThread {
      completion(nil)
    }
    return nil
  }

  public func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    (placeholder == nil && remoteImageURL == nil) ? self : nil
  }

  public func equals(_ other: ImageHolder) -> Bool {
    other is NilImageHolder
  }
}
