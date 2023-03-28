// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

// Base is exported with minimal iOS version 9.0.
// It's needed by RealTimeAnalytics pod, which is integrated in YXMobileMetrica.
@available(iOS 10, tvOS 10, *)
extension URLSessionTaskMetrics {
  var networkSessionMetrics: [NetworkSessionMetrics] {
    transactionMetrics
  }
}

// Base is exported with minimal iOS version 9.0.
// It's needed by RealTimeAnalytics pod, which is integrated in YXMobileMetrica.
@available(iOS 10, tvOS 10, *)
extension URLSessionTaskTransactionMetrics: NetworkSessionMetrics {}
