// Copyright 2020 Yandex LLC. All rights reserved.

public func !==(lhs: AnyObject??, rhs: AnyObject?) -> Bool {
  guard let left = lhs else { return true }
  return left !== rhs
}

public func !==(lhs: AnyObject?, rhs: AnyObject??) -> Bool {
  guard let right = rhs else { return true }
  return lhs !== right
}
