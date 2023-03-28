// Copyright 2016 Yandex LLC. All rights reserved.

import Foundation

public final class ActiveRequestsTracker {
  public private(set) var activeNetworkRequests = [URLRequest]()

  public init() {}

  public func addRequest(_ request: URLRequest) {
    activeNetworkRequests.append(request)
  }

  public func removeRequest(_ request: URLRequest) {
    if let removeIndex = activeNetworkRequests.firstIndex(of: request) {
      activeNetworkRequests.remove(at: removeIndex)
    }
  }
}
