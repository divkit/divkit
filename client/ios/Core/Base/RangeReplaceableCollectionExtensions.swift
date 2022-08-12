// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

extension RangeReplaceableCollection {
  public init(repeating element: Element, times: UInt) {
    // swiftlint:disable no_direct_use_of_repeating_count_initializer
    self.init(repeating: element, count: Int(times))
    // swiftlint:disable no_direct_use_of_repeating_count_initializer
  }

  /// throws `InvalidArgumentError` when `desiredCount` is less than `count`
  public func paddedAtBeginning(upTo desiredCount: Int, with value: Element) throws -> Self {
    var result = self
    result.insert(
      contentsOf: Array(repeating: value, times: try UInt(value: desiredCount - count)),
      at: startIndex
    )
    return result
  }
}
