// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public protocol ScrollViewTrackable {
  var isTracking: Bool { get }
  var isDragging: Bool { get }
  var isDecelerating: Bool { get }
}

extension ScrollViewTrackable {
  public var isChangingContentOffsetDueToUserActions: Bool {
    isTracking || isDecelerating
  }
}
