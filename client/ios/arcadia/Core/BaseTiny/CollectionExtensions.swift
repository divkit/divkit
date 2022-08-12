// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

extension Collection {
  public func element(at index: Index) -> Element? {
    indices.contains(index) ? self[index] : nil
  }
}
