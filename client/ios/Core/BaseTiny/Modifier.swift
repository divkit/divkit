// Copyright 2021 Yandex LLC. All rights reserved.

@inlinable
public func modified<T>(_ value: T, _ modificator: (inout T) throws -> Void) rethrows -> T {
  var result = value
  try modificator(&result)
  return result
}

@inlinable
public func modifyError<T: Error, U: Error, R>(
  _ modificator: (T) -> U,
  _ block: () throws -> R
) throws -> R {
  do {
    return try block()
  } catch let e as T {
    throw modificator(e)
  }
}
