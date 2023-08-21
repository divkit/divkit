// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public protocol NetworkRetryIntervalSource {
  func determineInterval(from: NSError, since: Date) -> TimeInterval?
}

public final class CompositeRetryIntervalSource: NetworkRetryIntervalSource {
  private let sources: [NetworkRetryIntervalSource]

  public init(_ sources: [NetworkRetryIntervalSource]) {
    self.sources = sources
  }

  public func determineInterval(from error: NSError, since refDate: Date) -> TimeInterval? {
    for source in sources {
      if let interval = source.determineInterval(from: error, since: refDate) {
        return interval
      }
    }
    return nil
  }
}

public final class HTTPHeaderRetryIntervalSource: NetworkRetryIntervalSource {
  public init() {}

  public func determineInterval(from error: NSError, since refDate: Date) -> TimeInterval? {
    guard !error.isNonRecoverableServerError else {
      return nil
    }
    guard let httpResponse = error.userInfo[URLSessionErrorResponseKey] as? HTTPURLResponse else {
      return nil
    }
    guard let retryAfterHeader = findRetryAfterHeader(in: httpResponse) else {
      return nil
    }
    let interval = retryAfterHeader.timeInterval(since: refDate)
    return max(interval, 0)
  }

  private func findRetryAfterHeader(in response: HTTPURLResponse) -> HTTPRetryAfterHeader? {
    guard let value = response.httpHeaders["Retry-After"] else {
      return nil
    }
    if let date = Date.fromHTTPDate(value) {
      return .date(date)
    } else if let seconds = TimeInterval(value) {
      return .seconds(seconds)
    }
    return nil
  }
}

public final class LocalRetryIntervalSource: NetworkRetryIntervalSource {
  // Starting value of 1 sec should be a reasonable compromise for retries.
  private var retryingTimeout = RetryingTimeout(value: 1)

  public init() {}

  public func determineInterval(from error: NSError, since _: Date) -> TimeInterval? {
    if error.isNonRecoverableServerError { return nil }
    return retryingTimeout.getValueAndIncrement()
  }
}

extension NSError {
  var isNonRecoverableServerError: Bool {
    guard domain == HTTPErrorDomain else {
      return false
    }
    switch code {
    case 408:
      return false // HTTP 408 Request Timeout - recoverable
    case 429:
      return false // HTTP 429 Too Many Requests - recoverable
    case 500..<600:
      return false // HTTP 5xx Server Error - recoverable
    default:
      return true // e.g. HTTP 4xx Client Error - not recoverable
    }
  }
}

extension HTTPRetryAfterHeader {
  fileprivate func timeInterval(since refDate: Date) -> TimeInterval {
    switch self {
    case let .date(date):
      return date.timeIntervalSince(refDate)
    case let .seconds(seconds):
      return seconds
    }
  }
}
