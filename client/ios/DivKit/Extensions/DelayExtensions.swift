// Copyright 2021 Yandex LLC. All rights reserved.

import LayoutKit

extension Delay {
  init?(milliseconds: Int) {
    guard milliseconds >= 0 else { return nil }
    self.init(Double(milliseconds) / 1000)
  }
}
