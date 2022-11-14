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

  public func addXYandexTheme(_ theme: String) -> HTTPHeaders {
    self.addHeader("X-Yandex-Theme", theme)
  }
}

extension HTTPHeaders {
  public static let yandexUID = "X-Yandex-RandomUID"
}

extension HTTPHeaders {
  /**
     Creates a new instance, combining current headers with newHeaders.
     Conflicting keys get a value from newHeaders.
     "Cookies" field is merged separately, all cookies are stored,
     conflicting cookie keys get a value from newHeaders' cookies.
   **/
  public func merged(with newHeaders: HTTPHeaders) -> HTTPHeaders {
    let cookie = mergeCookies(self["Cookie"], newHeaders["Cookie"])
    return self + newHeaders +
      (cookie != "" ? HTTPHeaders(headersDictionary: ["Cookie": cookie]) : HTTPHeaders.empty)
  }

  private func mergeCookies(_ cookies: String?, _ newCookies: String?) -> String {
    var result = parseCookies(cookies ?? "")
    parseCookies(newCookies ?? "").forEach { newCookie in
      if let index = result.firstIndex(where: { $0.name == newCookie.name }) {
        result[index] = newCookie
      } else {
        result.append(newCookie)
      }
    }
    return result.map { "\($0.name)=\($0.value)" }.joined(separator: "; ")
  }

  private func parseCookies(_ cookies: String) -> [(name: String, value: String)] {
    cookies
      .components(separatedBy: "; ")
      .filter { $0 != "" }
      .map {
        let keyVal = $0.components(separatedByFirst: "=")
        return (name: keyVal[0], value: keyVal.count > 1 ? keyVal[1] : "")
      }
  }
}

extension String {
  fileprivate func components(separatedByFirst delimiter: String) -> [String] {
    let comps = components(separatedBy: delimiter)
    guard comps.count > 1 else { return comps }
    return [comps[0], comps.suffix(from: 1).joined(separator: delimiter)]
  }
}
