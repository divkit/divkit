// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

public protocol NetworkErrorHandlingStrategy: AnyObject {
  // NOTE: beware this delegate is being overwritten in network operation
  // weak
  var delegate: NetworkErrorHandlingStrategyDelegate? { get set }

  func policy(
    for networkError: NSError,
    from url: URL
  ) -> NetworkErrorHandlingPolicy
}
