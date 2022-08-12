// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

protocol TabContentsViewDelegate: AnyObject {
  func tabContentsViewDidChangeRelativeContentOffsetTo(_ offset: CGFloat)
  func tabContentsViewDidEndAnimation()
}
