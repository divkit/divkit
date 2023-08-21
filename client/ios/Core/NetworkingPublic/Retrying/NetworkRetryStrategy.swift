// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public class NetworkRetryStrategy: NetworkErrorHandlingStrategy {
  public typealias NetworkReachabilityObserverFactory = (URL) -> NetworkReachabilityObserving?
  public typealias NetworkErrorClassifier = (NSError) -> NetworkErrorType

  public weak var delegate: NetworkErrorHandlingStrategyDelegate?

  private var timer: TimerType? {
    didSet {
      oldValue?.invalidate()
    }
  }

  private let timerScheduler: Scheduling
  private let errorClassifier: NetworkErrorClassifier
  private let log: (String) -> Void
  private let reachabilityObserverFactory: NetworkReachabilityObserverFactory
  private let reachabilityTimeout: TimeInterval
  private let retryIntervalSource: NetworkRetryIntervalSource
  private let maxRetryAttempts: Int
  private let extendedRetryingLogic: Bool
  private var reachabilityObserver: NetworkReachabilityObserving!
  private var retryCount = 0

  public init(
    logger: @escaping (String) -> Void,
    timerScheduler: Scheduling = TimerScheduler(),
    errorClassifier: @escaping NetworkErrorClassifier = defaultNetworkErrorClassifier,
    reachabilityObserverFactory: @escaping NetworkReachabilityObserverFactory = { url in
      guard let host = url.host else { return nil }
      return NetworkReachabilityObserver(hostname: host)
    },
    reachabilityTimeout: TimeInterval = .greatestFiniteMagnitude,
    retryIntervalSource: NetworkRetryIntervalSource =
      CompositeRetryIntervalSource([HTTPHeaderRetryIntervalSource(), LocalRetryIntervalSource()]),
    maxRetryAttempts: Int = .max,
    extendedRetryingLogic: Bool = false
  ) {
    self.timerScheduler = timerScheduler
    self.errorClassifier = errorClassifier
    self.log = logger
    self.reachabilityObserverFactory = reachabilityObserverFactory
    self.reachabilityTimeout = reachabilityTimeout
    self.retryIntervalSource = retryIntervalSource
    self.maxRetryAttempts = maxRetryAttempts
    self.extendedRetryingLogic = extendedRetryingLogic
  }

  public func policy(
    for networkError: NSError,
    from url: URL
  ) -> NetworkErrorHandlingPolicy {
    if extendedRetryingLogic {
      if retryCount >= maxRetryAttempts {
        log("Max retry attempts count reached for error \(networkError.code), from URL \(url)")
        return .complete
      }
      retryCount += 1
    }

    guard errorClassifier(networkError) == .recoverable else {
      log("Got non-recoverable HTTP error \(networkError.code), from URL \(url), giving up")
      return .complete
    }

    guard var timeoutValue = retryIntervalSource
      .determineInterval(from: networkError, since: Date()) else {
      log(
        "Failed to determine required timeout value for network error " +
          "with code \(networkError.code), domain \(networkError.domain), " +
          "userInfo \(networkError.userInfo) from URL \(url)"
      )
      return .complete
    }

    log(
      "Got recoverable network error with code \(networkError.code), " +
        "domain \(networkError.domain), userInfo \(networkError.userInfo) from URL \(url), " +
        "will retry after delay of \(timeoutValue)"
    )

    let isNetworkError = networkError.domain == NSURLErrorDomain
    let delegateAction = { [weak self] in
      guard let strongSelf = self else { return }

      if strongSelf.extendedRetryingLogic {
        // When timeout timer fires while we are waiting for network conditions - abort waiting
        // and give a change to retry. Reachability info is not 100% correct, and new attempt
        // may succeed in some cases.
        // In other cases it should be better to reach max attempts count and give up with error
        // than waiting for indefinite time.
        if let reachabilityObserver = strongSelf.reachabilityObserver {
          reachabilityObserver.stopObserving()
          strongSelf.reachabilityObserver = nil
          strongSelf.retryCount = .max
        }
        strongSelf.delegate?.performRetry()
      } else {
        if isNetworkError, let reachabilityObserver = strongSelf.reachabilityObserverFactory(url) {
          strongSelf.reachabilityObserver = reachabilityObserver
          strongSelf.reachabilityObserver.delegate = strongSelf
          strongSelf.reachabilityObserver.startObserving()
        } else {
          strongSelf.delegate?.performRetry()
        }
      }
    }

    if extendedRetryingLogic {
      // On network error we start observing reachability, meanwhile scheduling timeout timer,
      // so that waiting never continues for indefinite time.
      if isNetworkError, let reachabilityObserver = reachabilityObserverFactory(url) {
        self.reachabilityObserver = reachabilityObserver
        reachabilityObserver.delegate = self
        reachabilityObserver.startObserving()
        timeoutValue = reachabilityTimeout
      }
    }

    if timer?.isValid != true {
      timer = timerScheduler.makeTimer(delay: timeoutValue, handler: delegateAction)
    }

    return .waitForRetry
  }
}

extension NetworkRetryStrategy: NetworkReachabilityObserverDelegate {
  public func reachabilityObserver(
    _ observer: NetworkReachabilityObserving,
    targetDidBecomeReachable reachable: Bool
  ) {
    guard observer === reachabilityObserver else {
      return
    }

    if reachable {
      observer.stopObserving()
      reachabilityObserver = nil

      if extendedRetryingLogic {
        // Retrying on network errors may occur before timeout timer fires.
        if let timer = timer, timer.isValid {
          timer.invalidate()
        }
      }

      delegate?.performRetry()
    }
  }
}

public func defaultNetworkErrorClassifier(_ error: NSError) -> NetworkErrorType {
  error.isNonRecoverableServerError ? .nonRecoverable : .recoverable
}
