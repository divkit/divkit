// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public final class NetworkURLResourceRequester: URLResourceRequesting {
  private let performer: URLRequestPerforming

  public init(performer: URLRequestPerforming) {
    self.performer = performer
  }

  public func getDataWithSource(
    from url: URL,
    completion: @escaping CompletionHandlerWithSource
  ) -> Cancellable? {
    performer.performRequest(URLRequest(url: url)) { result in
      Thread.assertIsMain()
      switch result {
      case let .success((data, _)):
        completion(.success(URLRequestResult(data: data, source: .network)))
      case let .failure(error):
        completion(.failure(error))
      }
    }
  }
}
