// Copyright 2018 Yandex LLC. All rights reserved.
public struct SignalPipe<T> {
  private let bag = Bag<Observer<T>>()
  public let signal: Signal<T>

  public init() {
    signal = Signal(addObserver: { [weak bag] observer in
      bag?.add(observer) ?? Disposable()
    })
  }

  public func send(_ value: T) {
    bag.forEach { observer in
      let action = observer.action
      action(value)
    }
  }
}

extension SignalPipe where T == Void {
  public func send() { send(()) }
}

// TODO: - Сделать приватным в рамках тикета https://st.yandex-team.ru/IBRO-28094
public final class Bag<T> {
  private final class Container {
    var payload: T?

    init(payload: T) {
      self.payload = payload
    }
  }

  private typealias Key = UInt64
  private var counter: Key = 0
  private var items: [Key: Container] = [:]

  public init() {}

  public func add(_ item: T) -> Disposable {
    let key = counter
    counter += 1
    items[key] = Container(payload: item)
    return Disposable { [weak self] in
      guard let self = self else { return }
      guard let value = self.items.removeValue(forKey: key) else { return }

      value.payload = nil
    }
  }

  // TODO: - Убрать computed property values в рамках тикета https://st.yandex-team.ru/IBRO-28094
  public var values: [T] {
    items.values.compactMap { $0.payload }
  }

  func forEach(_ block: (T) -> Void) {
    #if INTERNAL_BUILD
    // ensure failure of code relying on the order of observers
    let values = items.values.shuffled()
    #else
    let values = items.values
    #endif

    for item in values {
      if let payload = item.payload {
        block(payload)
      }
    }
  }
}
