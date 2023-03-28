// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

public typealias URLQueryParam = (name: String, value: String?)
public typealias URLQueryParams = [URLQueryParam]

extension URL {
  public var components: URLComponents? {
    URLComponents(url: self, resolvingAgainstBaseURL: false)
  }

  public static func mailToURL(_ email: String, subject: String?, body: String?) -> URL? {
    var params = [String: String]()

    if let subject = subject {
      params["subject"] = subject
    }

    if let body = body {
      params["body"] = body
    }

    return URL(string: "mailto:\(email)")?.URLByAddingGETParameters(params)
  }

  public func URLByAddingGETParameters(_ params: URLQueryParams) -> URL {
    var components = URLComponents(url: self, resolvingAgainstBaseURL: false)!
    var currentParams: URLQueryParams = components.queryItems?.map { ($0.name, $0.value) } ?? []
    let overriddenNames = params.map { $0.name }
    currentParams = currentParams.filter { !overriddenNames.contains($0.name) }
    let encodedQueryItems = (currentParams + params).map { name, value -> String in
      String(forQueryItemWithName: name, value: value)
    }
    components.percentEncodedQuery = encodedQueryItems.isEmpty ? nil : encodedQueryItems.sorted()
      .joined(separator: "&")
    return components.url!
  }

  public func URLByAddingGETParameters(_ params: [String: String]) -> URL {
    URLByAddingGETParameters(params.queryParams)
  }

  public func URLByReplacingScheme(_ scheme: String) -> URL {
    var components = URLComponents(url: self, resolvingAgainstBaseURL: false)!
    components.scheme = scheme
    return components.url!
  }

  public var basePartOnly: URL {
    guard var components = URLComponents(url: self, resolvingAgainstBaseURL: false) else {
      assertionFailure("Failed to parse URL: \(absoluteString)")
      return self
    }
    components.path = ""
    components.query = nil
    components.fragment = nil
    guard let url = components.url else {
      assertionFailure("Failed to compose URL: \(absoluteString)")
      return self
    }
    return url
  }
}

extension URLQueryParams {
  public func filteringNilValues() -> URLQueryParams {
    filter { $0.value != nil }
  }
}
