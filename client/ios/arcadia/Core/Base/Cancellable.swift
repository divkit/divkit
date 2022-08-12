// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public protocol Cancellable {
  func cancel()
}

public final class EmptyCancellable: Cancellable {
  public init() {}
  @objc
  public func cancel() {}
}

public struct CallbackCancellable: Cancellable {
  private let onCancel: Action

  public init(onCancel: @escaping Action) {
    self.onCancel = onCancel
  }

  public func cancel() {
    self.onCancel()
  }
}
