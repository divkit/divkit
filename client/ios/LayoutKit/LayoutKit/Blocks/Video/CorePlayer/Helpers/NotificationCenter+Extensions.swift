import Foundation
import VGSL

extension NotificationCenter {
  func observe(
    name: Notification.Name,
    queue: OperationQueue? = nil,
    block: @escaping (Notification) -> Void
  ) -> Disposable {
    let observer = addObserver(
      forName: name,
      object: nil,
      queue: queue,
      using: { notification in
        block(notification)
      }
    )

    return Disposable { [weak self] in
      self?.removeObserver(observer)
    }
  }

  func observe<Target>(
    _ target: Target,
    name: Notification.Name,
    queue: OperationQueue? = nil,
    block: @escaping (Target, Notification) -> Void
  ) -> Disposable {
    let observer = addObserver(
      forName: name,
      object: target,
      queue: queue,
      using: { notification in
        guard let target = notification.object as? Target else { return }
        block(target, notification)
      }
    )

    return Disposable { [weak self] in
      self?.removeObserver(observer)
    }
  }
}
