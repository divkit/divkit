// Copyright 2016 Yandex LLC. All rights reserved.

import Foundation

extension URLSessionTask {
  public func httpResponseError(isSuccess: (Int) -> Bool) -> NSError? {
    guard let response = response as? HTTPURLResponse else {
      return NSError(domain: URLRequestErrorDomain, code: NoResponseErrorCode, userInfo: nil)
    }
    guard !isSuccess(response.statusCode) else {
      return nil
    }
    var userInfo = [String: Any]()
    userInfo[URLSessionErrorResponseKey] = response
    userInfo[NSURLErrorFailingURLErrorKey] = response.url
    return NSError(
      domain: HTTPErrorDomain,
      code: response.statusCode,
      userInfo: userInfo
    )
  }

  public var httpResponseError: NSError? {
    httpResponseError(isSuccess: { 200...299 ~= $0 })
  }
}
