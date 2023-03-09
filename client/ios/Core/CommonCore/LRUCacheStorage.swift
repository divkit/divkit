// Copyright 2019 Yandex LLC. All rights reserved.

public final class LRUCacheStorage<Storage: OrderedDictionary>: CacheStorage
  where Storage.Item: SizedItem {
  public typealias Item = Storage.Item

  private let storage: Storage
  private let maxCapacity: Int
  private var currentCapacity: Int

  public init(
    storage: Storage,
    maxCapacity: UInt
  ) {
    self.storage = storage
    self.maxCapacity = Int(maxCapacity)
    currentCapacity = storage.asArray().reduce(0) { $0 + $1.item.size }
  }

  public convenience init(maxCapacity: UInt) {
    self.init(storage: Storage(), maxCapacity: maxCapacity)
  }

  @discardableResult
  public func add(key: String, item: Item) -> EvictedKeys {
    let sizeDiff = item.size - (storage.value(for: key)?.size ?? 0)
    currentCapacity += sizeDiff
    storage.insertFirst(key: key, item: item)

    var evictedKeys: [String] = []
    while currentCapacity > maxCapacity, let (key, item) = storage.removeLast() {
      evictedKeys.append(key)
      currentCapacity -= item.size
    }
    return .init(rawValue: evictedKeys)
  }

  @discardableResult
  public func remove(key: String) -> Item? {
    currentCapacity -= storage.value(for: key)?.size ?? 0
    return storage.remove(key: key)
  }

  public func value(for key: String) -> Item? {
    if let value = storage.value(for: key) {
      storage.insertFirst(key: key, item: value)
      return value
    } else {
      return nil
    }
  }

  public func asArray() -> [CacheRecord] {
    storage.asArray().map { CacheRecord(key: $0.key, size: $0.item.size) }
  }
}
