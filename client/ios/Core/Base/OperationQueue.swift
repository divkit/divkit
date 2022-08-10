// Copyright 2016 Yandex LLC. All rights reserved.

import Foundation

public typealias MainQueueScheduler = (Operation) -> Void

public protocol OperationQueueType: AnyObject {
  func addOperation(_ operation: Operation)
  func addOperation(_ block: @escaping () -> Void)
  func cancelAllOperations()
}

public protocol SerialOperationQueue: OperationQueueType {}

extension OperationQueueType {
  public func addOperation(_ block: @escaping () -> Void) {
    addOperation(BlockOperation(block: block))
  }
}

extension OperationQueue: OperationQueueType {
  public convenience init(name: String, qos: QualityOfService) {
    self.init()
    self.name = name
    qualityOfService = qos
  }

  public static func serialQueue(name: String, qos: QualityOfService) -> SerialOperationQueue {
    let queue = SerialOperationQueueImpl()
    queue.name = name
    queue.qualityOfService = qos
    return queue
  }
}

private final class SerialOperationQueueImpl: OperationQueue, SerialOperationQueue {
  override init() {
    super.init()
    maxConcurrentOperationCount = 1
  }
}

public final class OperationQueueStub: OperationQueueType {
  public init() {}
  public func addOperation(_: Operation) {}
  public func cancelAllOperations() {}
}
