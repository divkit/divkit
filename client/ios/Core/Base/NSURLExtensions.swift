// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

extension URL {
  public func equalWithoutFragments(_ url: URL) -> Bool {
    if url == self {
      return true
    }

    if var components = URLComponents(url: self, resolvingAgainstBaseURL: false),
       var otherComponents = URLComponents(
         url: url,
         resolvingAgainstBaseURL: false
       ) {
      components.fragment = nil
      otherComponents.fragment = nil
      return components == otherComponents
    } else {
      // https://st.yandex-team.ru/MOBYANDEXIOS-735
      //
      // As specified in RFC 3986, in "Parsing a URI Reference with a Regular Expression" part,
      // this expression could be used for parsing into components:
      // ^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?
      //
      // Which means that symbol (#) could be used only to specify the fragment of the URL.
      // So, we can just crop the string up to this symbol to get the URL without fragment.

      return absoluteString.substringToString("#") == url.absoluteString.substringToString("#")
    }
  }

  public func queryParamValue(forName name: String) -> String? {
    queryItem(forName: name)?.value
  }

  public func queryItem(forName name: String) -> URLQueryItem? {
    queryItems?.first { $0.name == name }
  }

  public var queryParams: URLQueryParams {
    queryItems?.map { ($0.name, $0.value) } ?? []
  }

  private var queryItems: [URLQueryItem]? {
    URLComponents(url: self, resolvingAgainstBaseURL: false)?.queryItems
  }

  public var queryParamsDict: [String: String?] {
    Dictionary(queryParams.map { ($0.name, $0.value) }, uniquingKeysWith: { $1 })
  }

  public var URLByStrippingQuery: URL? {
    var components = URLComponents(url: self, resolvingAgainstBaseURL: false)
    components?.query = nil
    return components?.url
  }

  public func removingQueryParameters(_ names: Set<String>) -> URL {
    var components = URLComponents(url: self, resolvingAgainstBaseURL: false)!
    components.queryItems = components.queryItems?.filter { !names.contains($0.name) }
    if let items = components.queryItems, items.isEmpty {
      components.query = nil
    }
    return components.url!
  }

  public func removingQueryParameter(_ name: String) -> URL {
    removingQueryParameters([name])
  }

  public var isAppStoreURL: Bool {
    let components = URLComponents(url: self, resolvingAgainstBaseURL: false)

    guard let scheme = components?.scheme, let host = components?.host else {
      return false
    }

    return scheme.hasPrefix("itms")
      || host.hasSuffix("itunes.apple.com")
      || host.hasSuffix("apps.apple.com")
  }

  public var isHypertextURL: Bool {
    let lowercased = scheme?.lowercased()
    return lowercased == "http" || lowercased == "https"
  }

  public var isBlobURL: Bool {
    scheme?.lowercased() == "blob"
  }

  public func URLWithDefaultScheme(_ defaultScheme: String) -> URL {
    guard scheme == nil || scheme!.isEmpty else {
      return self
    }

    var components = URLComponents(url: self, resolvingAgainstBaseURL: false)!
    components.scheme = defaultScheme
    return components.url!
  }

  public var domains: [String]? {
    // in file://some/path `some` is also defined as "host"
    // https://tools.ietf.org/html/rfc1738#section-3.10
    guard !isFileURL else { return nil }
    return host?.components(separatedBy: ".")
  }

  public var hostAndPathString: String? {
    guard !isFileURL, let host = host else { return nil }
    return host + path
  }

  public var sanitizedHost: URL? {
    modified(components) {
      $0?.user = nil
      $0?.password = nil
      $0?.host = nil
      $0?.scheme = nil
      $0?.port = nil
    }?.url
  }
}
