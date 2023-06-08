// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

extension NetworkRetryStrategy {
  public convenience init(
    observeNetworkReachability: Bool = true,
    reachabilityTimeout: TimeInterval = .greatestFiniteMagnitude,
    maxRetryAttempts: Int = .max,
    extendedRetryingLogic: Bool = false,
    logger: @escaping (String) -> Void = { _ in }
  ) {
    if observeNetworkReachability {
      self.init(
        logger: logger,
        reachabilityTimeout: reachabilityTimeout,
        maxRetryAttempts: maxRetryAttempts,
        extendedRetryingLogic: extendedRetryingLogic
      )
    } else {
      self.init(
        logger: logger,
        reachabilityObserverFactory: { _ in nil },
        reachabilityTimeout: reachabilityTimeout,
        maxRetryAttempts: maxRetryAttempts,
        extendedRetryingLogic: extendedRetryingLogic
      )
    }
  }
}

extension ReusableNetworkRetryStrategy {
  public convenience init() {
    self.init(strategyFactory: { NetworkRetryStrategy() })
  }
}
