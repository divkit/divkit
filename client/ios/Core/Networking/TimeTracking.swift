// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

public protocol TimeTracking: AnyObject {
  func track(_ time: TimeInterval, timestamp: Date)
}
