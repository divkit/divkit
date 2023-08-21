// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

extension HTTPURLResponse {
  public var httpHeaders: HTTPHeaders {
    guard let headers = allHeaderFields as? [String: String] else {
      return .empty
    }
    return HTTPHeaders(headersDictionary: headers)
  }

  public static var empty: HTTPURLResponse { HTTPURLResponse() }
}
