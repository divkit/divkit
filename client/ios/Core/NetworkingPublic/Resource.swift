// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public enum HTTPMethod: String {
  case GET
  case POST
  case PUT
  case DELETE
}

public struct Resource<T> {
  public typealias Parser = (Data, HTTPURLResponse) throws -> T

  public let path: String?
  public let method: HTTPMethod
  public let GETParameters: URLQueryParams
  public let headers: HTTPHeaders?
  public let body: Data?
  public let parser: Parser
  public let timeout: TimeInterval?

  public init(
    path: String?,
    method: HTTPMethod = .GET,
    GETParameters: [String: String] = [:],
    headers: HTTPHeaders? = nil,
    body: Data? = nil,
    timeout: TimeInterval? = nil,
    parser: @escaping Parser
  ) {
    self.init(
      path: path,
      method: method,
      GETParameters: GETParameters.queryParams,
      headers: headers,
      body: body,
      timeout: timeout,
      parser: parser
    )
  }

  public init(
    path: String?,
    GETParameters: [String: String],
    headers: HTTPHeaders,
    parser: @escaping Parser
  ) {
    self.init(
      path: path,
      method: .GET,
      GETParameters: GETParameters.queryParams,
      headers: headers,
      body: nil,
      parser: parser
    )
  }

  public init(
    path: String?,
    method: HTTPMethod = .GET,
    GETParameters: URLQueryParams,
    headers: HTTPHeaders? = nil,
    body: Data? = nil,
    timeout: TimeInterval? = nil,
    parser: @escaping Parser
  ) {
    self.path = path
    self.method = method
    self.GETParameters = GETParameters
    self.headers = headers
    self.body = body
    self.parser = parser
    self.timeout = timeout
  }
}

@inlinable
public func URLForResource<T>(_ resource: Resource<T>, withBaseURL baseURL: URL) -> URL {
  var absoluteURL = baseURL
  if let path = resource.path {
    absoluteURL = baseURL.appendingPathComponent(path)
  }
  var components = URLComponents(url: absoluteURL, resolvingAgainstBaseURL: false)!

  let encodedQueryItems = resource.GETParameters.map { name, value -> String in
    String(forQueryItemWithName: name, value: value)
  }
  let query = (components.percentEncodedQuery.asArray() + encodedQueryItems.sorted())
    .joined(separator: "&")
  if !query.isEmpty {
    components.percentEncodedQuery = query
  }

  return components.url!
}

@inlinable
public func URLRequestForResource<T>(
  _ resource: Resource<T>,
  withBaseURL baseURL: URL
) -> URLRequest {
  var request = URLRequest(url: URLForResource(resource, withBaseURL: baseURL))
  if let headers = resource.headers {
    for (field, value) in headers.headersDictionary {
      request.setValue(value, forHTTPHeaderField: field)
    }
  }
  request.httpMethod = resource.method.rawValue
  request.httpBody = resource.body
  if let timeout = resource.timeout {
    request.timeoutInterval = timeout
  }
  return request
}

public func noOpParser<T>(_: Data, _: HTTPURLResponse) throws -> T {
  throw UnexpectedParserInvocation()
}

extension Resource {
  public func addingGetParams(_ params: [String: String]) -> Self {
    Self(
      path: path,
      method: method,
      GETParameters: GETParameters + params.queryParams,
      headers: headers,
      body: body,
      timeout: timeout,
      parser: parser
    )
  }

  public func mergingDefaultHeaders(_ defaultHeaders: HTTPHeaders) -> Self {
    Self(
      path: path,
      method: method,
      GETParameters: GETParameters,
      headers: defaultHeaders + (headers ?? HTTPHeaders.empty),
      body: body,
      timeout: timeout,
      parser: parser
    )
  }
}

private struct UnexpectedParserInvocation: Error {}
