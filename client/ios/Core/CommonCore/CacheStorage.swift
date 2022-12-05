// Copyright 2019 Yandex LLC. All rights reserved.

public protocol CacheStorage {
  associatedtype Item

  typealias EvictedKeys = Tagged<Item, [String]>

  func add(key: String, item: Item) -> EvictedKeys
  func value(for key: String) -> Item?
}
