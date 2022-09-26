// Copyright 2018 Yandex LLC. All rights reserved.

public final class Disposable {
  private var _dispose: (() -> Void)?

  public init(_ dispose: (() -> Void)? = nil) {
    _dispose = dispose
  }

  public func dispose() {
    _dispose?()
    _dispose = nil
  }

  deinit {
    dispose()
  }

  public static let empty = Disposable()
}

extension Disposable {
  @inlinable
  public convenience init<S: Sequence>(_ disposables: S) where S.Element == Disposable {
    self.init {
      for disposable in disposables {
        disposable.dispose()
      }
    }
  }
}
