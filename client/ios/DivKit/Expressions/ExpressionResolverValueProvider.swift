// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

public protocol ExpressionResolverValueProvider {
  func typedValue<T>() -> T?
}
