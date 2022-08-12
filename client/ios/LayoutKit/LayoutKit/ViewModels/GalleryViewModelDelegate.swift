// Copyright 2017 Yandex LLC. All rights reserved.

import CoreGraphics

public protocol GalleryViewModelDelegate: AnyObject {
  func onContentOffsetChanged(_ contentOffset: CGFloat, in model: GalleryViewModel)
}
