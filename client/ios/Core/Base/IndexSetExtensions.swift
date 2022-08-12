// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

extension IndexSet {
  public func shifting(by delta: Int) -> IndexSet {
    var result = self
    result.shift(startingAt: 0, by: delta)
    return result
  }
}
