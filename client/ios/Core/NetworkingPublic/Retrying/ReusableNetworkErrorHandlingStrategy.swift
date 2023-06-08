// Copyright 2017 Yandex LLC. All rights reserved.

public protocol ReusableNetworkErrorHandlingStrategy: NetworkErrorHandlingStrategy {
  func resetRetryInterval()
}
