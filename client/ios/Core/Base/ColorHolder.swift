// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

public final class ColorHolder: ImageHolder {
  private let color: Color
  public var image: Image? { nil }
  public var placeholder: ImagePlaceholder? { .color(color) }

  public init(color: Color) {
    self.color = color
  }

  public func requestImageWithCompletion(_: @escaping ((Image?) -> Void)) -> Cancellable? {
    nil
  }

  public func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    placeholder === self.placeholder && remoteImageURL == nil ? self : nil
  }

  public func equals(_ other: ImageHolder) -> Bool {
    (other as? ColorHolder)?.color == color
  }

  public var debugDescription: String {
    "ColorHolder(" + color.debugDescription + ")"
  }
}
