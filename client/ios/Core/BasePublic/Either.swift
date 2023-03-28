// Copyright 2020 Yandex LLC. All rights reserved.

import Foundation

@frozen
public enum Either<T, U> {
  case left(T)
  case right(U)

  @inlinable
  public func mapLeft<T1>(_ transform: (T) throws -> T1) rethrows -> Either<T1, U> {
    switch self {
    case let .left(t):
      return try .left(transform(t))
    case let .right(u):
      return .right(u)
    }
  }

  @inlinable
  public func mapRight<U1>(_ transform: (U) throws -> U1) rethrows -> Either<T, U1> {
    switch self {
    case let .left(t):
      return .left(t)
    case let .right(u):
      return try .right(transform(u))
    }
  }
}

extension Either: Equatable where T: Equatable, U: Equatable {}
