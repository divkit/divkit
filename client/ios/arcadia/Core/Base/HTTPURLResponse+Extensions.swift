// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

extension HTTPURLResponse {
  @objc public var contentDisposition: String? {
    yb_value(forHTTPHeader: "Content-Disposition")
  }

  public func yb_value(forHTTPHeader headerName: String) -> String? {
    if #available(iOS 13, tvOS 13, macOS 10.15, *) {
      return value(forHTTPHeaderField: headerName)
    } else {
      // https://bugs.swift.org/browse/SR-2429
      // swiftlint:disable:next use_http_headers
      return (allHeaderFields as NSDictionary)[headerName] as? String
    }
  }
}
