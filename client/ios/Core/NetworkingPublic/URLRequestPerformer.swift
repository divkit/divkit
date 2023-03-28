// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public final class URLRequestPerformer: URLRequestPerforming {
  public enum ChallengeHandler {
    case preferred(ChallengeHandling)
    case fallback(ChallengeHandling)
  }

  private let urlSession: URLSession
  private let URLSessionDelegate: URLSessionDelegateImpl
  private let activeRequestsTracker: ActiveRequestsTracker?
  private let challengeHandler: ChallengeHandler?
  private let trustedHosts: [String]
  private let urlTransform: URLTransform?

  private init(
    urlSession: URLSession,
    URLSessionDelegate: URLSessionDelegateImpl,
    activeRequestsTracker: ActiveRequestsTracker? = nil,
    challengeHandler: ChallengeHandler? = nil,
    trustedHosts: [String],
    urlTransform: URLTransform?
  ) {
    self.urlSession = urlSession
    self.URLSessionDelegate = URLSessionDelegate
    self.activeRequestsTracker = activeRequestsTracker
    self.challengeHandler = challengeHandler
    self.trustedHosts = trustedHosts
    self.urlTransform = urlTransform
  }

  public convenience init(
    urlSession: URLSession,
    URLSessionDelegate: URLSessionDelegateImpl,
    activeRequestsTracker: ActiveRequestsTracker? = nil,
    challengeHandler: ChallengeHandler? = nil,
    urlTransform: URLTransform?
  ) {
    self.init(
      urlSession: urlSession,
      URLSessionDelegate: URLSessionDelegate,
      activeRequestsTracker: activeRequestsTracker,
      challengeHandler: challengeHandler,
      trustedHosts: [],
      urlTransform: urlTransform
    )
  }

  public convenience init(urlTransform: URLTransform?) {
    let delegate = URLSessionDelegateImpl()
    let session = URLSession(configuration: .default, delegate: delegate, delegateQueue: .main)
    self.init(urlSession: session, URLSessionDelegate: delegate, urlTransform: urlTransform)
  }

  #if INTERNAL_BUILD
  public convenience init(trustedHosts: [String], urlTransform: URLTransform?) {
    let delegate = URLSessionDelegateImpl()
    let session = URLSession(configuration: .default, delegate: delegate, delegateQueue: .main)
    self.init(
      urlSession: session,
      URLSessionDelegate: delegate,
      trustedHosts: trustedHosts,
      urlTransform: urlTransform
    )
  }
  #endif

  public func performRequest(
    _ request: URLRequest,
    downloadProgressHandler: ((Double) -> Void)?,
    uploadProgressHandler: ((Double) -> Void)?,
    completion: @escaping URLRequestCompletionHandler
  ) -> NetworkTask {
    let request = modified(request) { $0.url = urlTransform?($0.url) ?? $0.url }

    let task = urlSession.dataTask(with: request)

    onMainThread { [activeRequestsTracker] in
      activeRequestsTracker?.addRequest(request)
    }

    let delegateCompletion: URLRequestCompletionHandler = { [activeRequestsTracker] result in
      Thread.assertIsMain()
      completion(result)
      activeRequestsTracker?.removeRequest(request)
    }
    URLSessionDelegate.setHandlers(
      downloadProgressChange: downloadProgressHandler,
      uploadProgressChange: uploadProgressHandler,
      challengeHandler: makeChallengeHandler(
        challengeHandler: challengeHandler,
        trustedHosts: trustedHosts,
        request: request
      ),
      completion: delegateCompletion,
      forTask: task
    )
    task.resume()
    return task
  }
}

extension Optional where Wrapped == URLRequestPerformer.ChallengeHandler {
  public var preferredChallengeHandler: ChallengeHandling? {
    guard let self = self else { return externalURLSessionChallengeHandler }

    switch self {
    case let .preferred(challengeHandler): return challengeHandler
    case let .fallback(challengeHandler):
      return externalURLSessionChallengeHandler ?? challengeHandler
    }
  }
}

private func makeChallengeHandler(
  challengeHandler: URLRequestPerformer.ChallengeHandler?,
  trustedHosts: [String],
  request: URLRequest
) -> ChallengeHandling {
  AuthChallengeHandler(
    nextHandler: challengeHandler.preferredChallengeHandler,
    trustedHosts: trustedHosts,
    request: request
  )
}
