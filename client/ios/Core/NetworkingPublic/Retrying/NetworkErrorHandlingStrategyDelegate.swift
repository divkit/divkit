// Copyright 2017 Yandex LLC. All rights reserved.

public protocol NetworkErrorHandlingStrategyDelegate: AnyObject {
  func performRetry()
}
