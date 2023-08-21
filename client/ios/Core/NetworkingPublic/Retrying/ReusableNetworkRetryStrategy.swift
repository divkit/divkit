// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

public class ReusableNetworkRetryStrategy: ReusableNetworkErrorHandlingStrategy {
  public weak var delegate: NetworkErrorHandlingStrategyDelegate? {
    didSet {
      _strategy?.delegate = delegate
    }
  }

  private let strategyFactory: () -> NetworkErrorHandlingStrategy
  private var _strategy: NetworkErrorHandlingStrategy?
  private var strategy: NetworkErrorHandlingStrategy {
    if _strategy == nil {
      _strategy = strategyFactory()
      _strategy?.delegate = delegate
    }

    return _strategy!
  }

  public convenience init(logger: @escaping (String) -> Void) {
    self.init(strategyFactory: { NetworkRetryStrategy(logger: logger) })
  }

  public init(strategyFactory: @escaping () -> NetworkErrorHandlingStrategy) {
    self.strategyFactory = strategyFactory
  }

  public func policy(
    for networkError: NSError,
    from url: URL
  ) -> NetworkErrorHandlingPolicy {
    strategy.policy(for: networkError, from: url)
  }

  public func resetRetryInterval() {
    _strategy = nil
  }
}
