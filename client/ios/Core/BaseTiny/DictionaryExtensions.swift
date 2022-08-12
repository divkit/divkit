// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

extension Dictionary where Key == String, Value == String? {
  public var queryParams: URLQueryParams {
    map { ($0.key, $0.value) }
  }
}

extension Dictionary where Key == String, Value == String {
  public var queryParams: URLQueryParams {
    map { ($0.key, $0.value) }
  }
}
