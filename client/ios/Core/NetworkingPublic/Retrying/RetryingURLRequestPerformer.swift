// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public final class RetryingURLRequestPerformer: URLRequestPerforming {
  public typealias ChallengeHandler = URLRequestPerformer.ChallengeHandler
  public typealias FailureHandler = (NSError) -> Void
  private let urlSession: URLSession
  private let URLSessionDelegate: URLSessionDelegateImpl
  private let activeRequestsTracker: ActiveRequestsTracker?
  private let challengeHandler: ChallengeHandler?
  private let failureHandler: FailureHandler
  private let urlTransform: URLTransform?

  public init(
    urlSession: URLSession,
    URLSessionDelegate: URLSessionDelegateImpl,
    activeRequestsTracker: ActiveRequestsTracker? = nil,
    challengeHandler: ChallengeHandler? = nil,
    failureHandler: @escaping FailureHandler,
    urlTransform: URLTransform?
  ) {
    self.urlSession = urlSession
    self.URLSessionDelegate = URLSessionDelegate
    self.activeRequestsTracker = activeRequestsTracker
    self.challengeHandler = challengeHandler
    self.failureHandler = failureHandler
    self.urlTransform = urlTransform
  }

  public func performRequest(
    _ request: URLRequest,
    downloadProgressHandler _: ((Double) -> Void)?,
    uploadProgressHandler _: ((Double) -> Void)?,
    completion: @escaping URLRequestCompletionHandler
  ) -> NetworkTask {
    performRequest(
      request,
      retryStrategy: NetworkRetryStrategy(),
      completion: completion
    )
  }

  public func performRequest(
    _ request: URLRequest,
    retryStrategy: NetworkRetryStrategy,
    completion: @escaping URLRequestCompletionHandler
  ) -> NetworkTask {
    let retryingTask = RetryingNetworkTask(
      request: modified(request) { $0.url = urlTransform?($0.url) ?? $0.url },
      dataTaskFactory: { [urlSession] in urlSession.dataTask(with: $0) },
      retryStrategy: retryStrategy,
      retryAction: { [unowned self] in self.startRetryingTask($0, completion: completion) }
    )
    startRetryingTask(retryingTask, completion: completion)
    return retryingTask
  }

  private func startRetryingTask(
    _ task: RetryingNetworkTask,
    completion: @escaping URLRequestCompletionHandler
  ) {
    Thread.assertIsMain()

    self.activeRequestsTracker?.addRequest(task.request)

    URLSessionDelegate.setHandlers(
      challengeHandler: challengeHandler.preferredChallengeHandler,
      completion: { [weak self] result in
        Thread.assertIsMain()
        switch result {
        case .success:
          completion(result)
        case let .failure(error):
          let policy = task.handleError(error)
          self?.failureHandler(error)

          switch policy {
          case .complete:
            completion(.failure(error))
          case .waitForRetry:
            break
          }
        }
        self?.activeRequestsTracker?.removeRequest(task.request)
      },
      forTask: task.currentTask
    )
    task.resume()
  }
}
