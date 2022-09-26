// Copyright 2018 Yandex LLC. All rights reserved.

@inlinable
public func + <T: Numeric, U: Numeric>(_ lhs: (T, U), _ rhs: (T, U)) -> (T, U) {
  return (lhs.0 + rhs.0, lhs.1 + rhs.1)
}
