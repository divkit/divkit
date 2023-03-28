// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

public final class RWLock {
  private var lock = pthread_rwlock_t()

  public init() {
    pthread_rwlock_init(&lock, nil)
  }

  deinit {
    pthread_rwlock_destroy(&lock)
  }

  public func read<T>(_ block: () throws -> T) rethrows -> T {
    pthread_rwlock_rdlock(&lock)
    defer { pthread_rwlock_unlock(&lock) }
    return try block()
  }

  public func write<T>(_ block: () throws -> T) rethrows -> T {
    pthread_rwlock_wrlock(&lock)
    defer { pthread_rwlock_unlock(&lock) }
    return try block()
  }
}
