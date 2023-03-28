// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

public typealias MainThreadRunner = (@escaping () -> Void) -> Void
public typealias MainThreadAsyncRunner = (@escaping () -> Void) -> Void
public typealias BackgroundRunner = (@escaping () -> Void) -> Void
public typealias SyncQueueRunner = (@escaping () -> Void) -> Void
public typealias DelayedExecution = (TimeInterval, @escaping () -> Void) -> Void
public typealias DelayedRunner = (_ delay: TimeInterval, _ block: @escaping () -> Void)
  -> Cancellable

public func onMainThread(_ block: @escaping () -> Void) {
  if Thread.isMainThread {
    block()
  } else {
    DispatchQueue.main.async(execute: block)
  }
}

public func onMainThreadResult<T>(_ function: @escaping () -> T) -> Future<T> {
  let (future, feed) = Future<T>.create()
  onMainThread {
    feed(function())
  }
  return future
}

public func onMainThreadResult<T>(_ function: @escaping () -> Future<T>) -> Future<T> {
  let (future, feed) = Future<T>.create()
  onMainThread {
    function().resolved { result in
      onMainThread {
        feed(result)
      }
    }
  }
  return future
}

public func onMainThreadAsync(_ block: @escaping () -> Void) {
  DispatchQueue.main.async(execute: block)
}

@inlinable
public func onMainThreadSync<T>(_ block: () -> T) -> T {
  if Thread.isMainThread {
    return block()
  } else {
    var result: T!
    DispatchQueue.main.sync {
      result = block()
    }
    return result
  }
}

public func onBackgroundThread(_ block: @escaping () -> Void) {
  onBackgroundThread(qos: .default)(block)
}

public func onBackgroundThread(qos: DispatchQoS.QoSClass) -> (@escaping () -> Void) -> Void {
  {
    DispatchQueue.global(qos: qos).async(execute: $0)
  }
}

public func invokeImmediately(_ block: @escaping () -> Void) {
  block()
}

@discardableResult
public func dispatchAfter(
  _ delay: TimeInterval,
  block: @escaping () -> Void
) -> Cancellable {
  after(delay, onQueue: .main, block: block)
}

public func after(_ delay: TimeInterval, block: @escaping () -> Void) {
  _ = after(delay, onQueue: .main, block: block)
}

@discardableResult
public func after(
  _ delay: TimeInterval,
  onQueue queue: DispatchQueue,
  block: @escaping () -> Void
) -> Cancellable {
  let fireTime = DispatchTime.now() + delay
  let workItem = DispatchWorkItem(block: block)
  queue.asyncAfter(deadline: fireTime, execute: workItem)
  return workItem
}

@inlinable
public func performAsyncAction<T>(
  _ action: (@escaping (T) -> Void) -> Void,
  withMinimumDelay minimumDelay: TimeInterval,
  skipDelayPredicate: @escaping (T) -> Bool,
  completion: @escaping (T) -> Void,
  delayedExecutor: DelayedExecution
) {
  var minimumDelayPassed = false
  var value: T?
  var completion: ((T) -> Void)? = completion

  func tryComplete() {
    if let value = value, minimumDelayPassed || skipDelayPredicate(value) {
      completion?(value)
      completion = nil
    }
  }

  delayedExecutor(minimumDelay) {
    minimumDelayPassed = true
    tryComplete()
  }

  action {
    value = $0
    tryComplete()
  }
}

extension DispatchWorkItem: Cancellable {}
