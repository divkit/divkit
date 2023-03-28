// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public protocol URLResourceRequesting: AnyObject {
  typealias CompletionHandlerWithSource = (Result<URLRequestResult, NSError>) -> Void

  func getDataWithSource(from url: URL, completion: @escaping CompletionHandlerWithSource)
    -> Cancellable?
}

extension URLResourceRequesting {
  public typealias CompletionHandler = (Result<Data, NSError>) -> Void

  public func getData(from url: URL, completion: @escaping CompletionHandler) -> Cancellable? {
    getDataWithSource(from: url) { result in
      completion(result.map { $0.data })
    }
  }
}

public struct URLRequestResult {
  public enum Source {
    case network
    case cache
  }

  public let data: Data
  public let source: Source

  public init(data: Data, source: Source) {
    self.data = data
    self.source = source
  }
}
