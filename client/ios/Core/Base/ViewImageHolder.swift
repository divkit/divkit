// Copyright 2023 Yandex LLC. All rights reserved.

import Foundation

public final class ViewImageHolder: ImageHolder {
  private let view: ViewType
  public var image: Image? { nil }
  public var placeholder: ImagePlaceholder? { .view(view) }

  public init(view: ViewType) {
    self.view = view
  }

  public func requestImageWithCompletion(_: @escaping ((Image?) -> Void)) -> Cancellable? {
    nil
  }

  public func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    placeholder === self.placeholder && remoteImageURL == nil ? self : nil
  }

  public func equals(_ other: ImageHolder) -> Bool {
    (other as? ViewImageHolder)?.view === view
  }

  public var debugDescription: String {
    "ViewImageHolder(" + view.debugDescription + ")"
  }
}
