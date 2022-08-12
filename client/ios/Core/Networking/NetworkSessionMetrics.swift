// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

protocol NetworkSessionMetrics {
  var connectStartDate: Date? { get }
  var connectEndDate: Date? { get }
  // Networking is exported with minimal iOS version 9.0.
  // It's needed by RealTimeAnalytics pod, which is integrated in YXMobileMetrica.
  @available(iOS 10, tvOS 10, *)
  var resourceFetchType: URLSessionTaskMetrics.ResourceFetchType { get }
}
