// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

public struct HTTPHeaders: Equatable {
  // TODO: remove public headersDictionary after https://st.yandex-team.ru/SSDK-1104
  public let headersDictionary: [String: String]
  private let lowercasedKeysHeadersDictionary: [String: String]

  public init(headersDictionary: [String: String]) {
    self.headersDictionary = headersDictionary
    lowercasedKeysHeadersDictionary = headersDictionary.lowercasedKeys
  }

  public subscript(_ headerName: String) -> String? {
    lowercasedKeysHeadersDictionary[headerName.lowercased()]
  }
}

extension HTTPHeaders {
  public static let empty = HTTPHeaders(headersDictionary: [:])
}

extension HTTPHeaders {
  public static func +(lhs: HTTPHeaders, rhs: HTTPHeaders) -> HTTPHeaders {
    HTTPHeaders(
      headersDictionary: lhs.headersDictionary.merging(
        rhs.headersDictionary,
        uniquingKeysWith: { $1 }
      )
    )
  }
}

extension HTTPHeaders {
  public init(cookies: [HTTPCookie]) {
    let headersDict = HTTPCookie.requestHeaderFields(with: cookies)
    self.init(headersDictionary: headersDict)
  }
}

extension HTTPHeaders {
  private func addHeader(_ header: String, _ value: String?) -> HTTPHeaders {
    var headers = self.headersDictionary
    headers[header] = value
    return HTTPHeaders(headersDictionary: headers)
  }

  public func addContentTypeJson() -> HTTPHeaders {
    self.addHeader("Content-Type", "application/json")
  }

  public func addOAuthToken(_ token: String?) -> HTTPHeaders {
    guard let token = token else { return self }
    return self.addHeader("Authorization", "OAuth " + token)
  }

  public func addXUUID(_ uuid: String?) -> HTTPHeaders {
    self.addHeader("X-UUID", uuid)
  }
}

extension HTTPHeaders {
  public static let yandexUID = "X-Yandex-RandomUID"
}
