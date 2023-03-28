// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

public final class Future<T> {
  public typealias Callback = (T) -> Void

  private enum State {
    case pending([Callback])
    case fulfilled(T)

    static var initial: State {
      .pending([])
    }
  }

  private let state: AllocatedUnfairLock<State>

  public init(payload: T) {
    state = AllocatedUnfairLock(initialState: .fulfilled(payload))
  }

  private init() {
    state = AllocatedUnfairLock(initialState: .initial)
  }

  // swiftlint:disable:next use_make_instead_of_create
  static func create() -> (Future<T>, feed: (T) -> Void) {
    let future = Future<T>()
    return (future, future.accept)
  }

  public func resolved(_ callback: @escaping Callback) {
    let immediateResult = state.withLock { state -> T? in
      switch state {
      case let .pending(callbacks):
        state = .pending(callbacks + [callback])
        return nil
      case let .fulfilled(result):
        // DON'T pass control to the unknown code within the scope
        // It may lead to a recursive lock, which is unsupported
        return result
      }
    }
    if let immediateResult {
      callback(immediateResult)
    }
  }

  public func unwrap() -> T? {
    state.withLock { state in
      switch state {
      case .pending: return nil
      case let .fulfilled(result): return result
      }
    }
  }

  private func accept(_ result: T) {
    let callbacks = state.withLock { state -> [Callback] in
      switch state {
      case let .pending(callbacks):
        state = .fulfilled(result)
        // DON'T pass control to the unknown code within the scope
        // It may lead to a recursive lock, which is unsupported
        return callbacks
      case let .fulfilled(currentResult):
        assertionFailure(
          "Future already has result: \(currentResult). Callstack: \(Thread.callStackSymbols.joined(separator: "\n"))"
        )
        return []
      }
    }
    for callback in callbacks {
      callback(result)
    }
  }
}

extension Future: Cancellable where T: Cancellable {
  public func cancel() {
    self.resolved { $0.cancel() }
  }
}

extension Future where T == Void {
  @inlinable public convenience init() {
    self.init(payload: ())
  }
}

extension Future: ExpressibleByBooleanLiteral where T == Bool {
  @inlinable
  public convenience init(booleanLiteral value: Bool) {
    self.init(payload: value)
  }
}

extension Future {
  public func map<U>(_ transform: @escaping (T) -> U) -> Future<U> {
    let (future, feed) = Future<U>.create()
    resolved { payload in
      feed(transform(payload))
    }
    return future
  }

  public func flatMap<U>(_ transform: @escaping (T) -> Future<U>) -> Future<U> {
    let (future, feed) = Future<U>.create()
    resolved { payload in
      let next = transform(payload)
      next.resolved(feed)
    }
    return future
  }

  @inlinable public func after<U>(_ continuation: @escaping () -> U) -> Future<U> {
    map { _ in continuation() }
  }

  @inlinable public func after<U>(_ continuation: @escaping () -> Future<U>) -> Future<U> {
    flatMap { _ in continuation() }
  }
}

extension Future {
  @inlinable public func dropPayload() -> Future<Void> {
    after { () }
  }

  @inlinable public func forward(to promise: Promise<T>) {
    resolved(promise.resolve)
  }

  @inlinable public func forward(to promise: Promise<T>, on queue: DispatchQueue) {
    resolved { payload in queue.async { promise.resolve(payload) } }
  }
}

extension Future {
  public func after(timeInterval: Double, on queue: DispatchQueue) -> Future {
    let (future, feed) = Future<T>.create()
    func forward(_ result: T) {
      queue.asyncAfter(deadline: .now() + .milliseconds(Int(timeInterval * 1000))) {
        feed(result)
      }
    }
    resolved(forward)
    return future
  }
}

extension Future {
  public var isFulfilled: Bool {
    guard let _ = unwrap() else { return false }
    return true
  }
}

extension Future {
  public func transfer(to queue: DispatchQueue) -> Future<T> {
    let (future, feed) = Future<T>.create()
    resolved { payload in
      queue.async {
        feed(payload)
      }
    }
    return future
  }
}

extension Future {
  @inlinable public static func fromAsyncTask(
    _ task: @escaping () -> T,
    after interval: TimeInterval,
    on queue: DispatchQueue
  ) -> Future {
    Future<Void>().after(timeInterval: interval, on: queue).after(task)
  }

  @inlinable public static func fromAsyncTask(
    _ task: @escaping () -> Future<T>,
    after interval: TimeInterval,
    on queue: DispatchQueue
  ) -> Future {
    Future<Void>().after(timeInterval: interval, on: queue).after(task)
  }
}

extension Future {
  public static func fromAsyncTask(_ task: (@escaping (T) -> Void) -> Void) -> Future {
    let (future, feed) = Future<T>.create()
    task(feed)
    return future
  }

  public static func fromAsyncBackgroundTask(
    _ task: @escaping () -> Future<T>,
    executeOn executeQueue: DispatchQueue,
    resolveOn resolveQueue: DispatchQueue
  ) -> Future<T> {
    let (future, feed) = Future<T>.create()
    executeQueue.async {
      let taskComplete = task()
      taskComplete.resolved { payload in resolveQueue.async { feed(payload) } }
    }
    return future
  }

  public static func fromBackgroundTask(
    _ task: @escaping () -> T,
    executingOn executeQueue: DispatchQueue,
    resolvingOn resolveQueue: DispatchQueue
  ) -> Future<T> {
    let (future, feed) = Future<T>.create()
    executeQueue.async {
      let result = task()
      resolveQueue.async {
        feed(result)
      }
    }
    return future
  }

  public static func fromBackgroundTask<U>(
    _ task: @escaping (U) -> T,
    _ argument: U,
    executingOn executeQueue: DispatchQueue,
    resolvingOn resolveQueue: DispatchQueue
  ) -> Future<T> {
    let (future, feed) = Future<T>.create()
    executeQueue.async {
      let result = task(argument)
      resolveQueue.async {
        feed(result)
      }
    }
    return future
  }

  public static func wrap(_ task: (@escaping (T) -> Void) -> Void) -> Future<T> {
    let (future, feed) = Future<T>.create()
    task { feed($0) }
    return future
  }

  public static func wrap<T1, T2>(
    _ task: (T1, T2, @escaping (T) -> Void) -> Void,
    _ p1: T1,
    _ p2: T2
  ) -> Future<T> {
    let (future, feed) = Future<T>.create()
    task(p1, p2) { feed($0) }
    return future
  }

  public static func resolved(_ payload: T) -> Future<T> {
    let (future, feed) = Future<T>.create()
    feed(payload)
    return future
  }

  public func timingOut(
    after timeout: TimeInterval,
    on queue: DispatchQueue,
    withFallback value: T
  ) -> Future {
    let fallback = Future(payload: value).after(timeInterval: timeout, on: queue)
    return first(self, fallback).map { $0.value }
  }
}

extension Either where T == U {
  fileprivate var value: T {
    switch self {
    case let .left(value):
      return value
    case let .right(value):
      return value
    }
  }
}

extension Future where T == Void {
  public static func fromAsyncTask(_ task: (@escaping () -> Void) -> Void) -> Future {
    let (future, feed) = Future<Void>.create()
    task { feed(()) }
    return future
  }

  public static func after(_ barrier: Operation) -> Future {
    let promise = Promise<Void>()
    let operation = BlockOperation { promise.resolve() }
    operation.addDependency(barrier)
    // swiftlint:disable:next no_direct_use_for_main_queue
    OperationQueue.main.addOperation(operation)
    return promise.future
  }
}

public func all<T, U>(_ f1: Future<T>, _ f2: Future<U>) -> Future<(T, U)> {
  let (future, feed) = Future<(T, U)>.create()
  f1.resolved { p1 in
    f2.resolved { p2 in
      feed((p1, p2))
    }
  }
  return future
}

public func all<T, U, V>(
  _ f1: Future<T>,
  _ f2: Future<U>,
  _ f3: Future<V>
) -> Future<(T, U, V)> {
  let (future, feed) = Future<(T, U, V)>.create()
  f1.resolved { p1 in
    f2.resolved { p2 in
      f3.resolved { p3 in
        feed((p1, p2, p3))
      }
    }
  }
  return future
}

public func first<T, U>(_ a: Future<T>, _ b: Future<U>) -> Future<Either<T, U>> {
  let (future, feed) = Future<Either<T, U>>.create()
  var resolved = false
  func fullfil(_ value: Either<T, U>) {
    if !resolved {
      resolved = true
      feed(value)
    }
  }
  a.resolved {
    fullfil(.left($0))
  }
  b.resolved {
    fullfil(.right($0))
  }
  return future
}

@inlinable public func all<T>(futures: [Future<T>]) -> Future<[T]> {
  guard let future = futures.first else {
    return Future<[T]>(payload: [])
  }

  guard futures.count > 1 else {
    return future.map { [$0] }
  }

  let lastFutures = Array(futures.dropFirst())
  return all(future, all(futures: lastFutures)).flatMap { Future(payload: [$0.0] + $0.1) }
}
