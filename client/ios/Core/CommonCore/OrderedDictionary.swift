// Copyright 2019 Yandex LLC. All rights reserved.

public protocol OrderedDictionary {
  associatedtype Item

  init()

  func insertFirst(key: String, item: Item)
  func value(for key: String) -> Item?
  func removeLast() -> (key: String, item: Item)?
  func remove(key: String) -> Item?

  func asArray() -> [(key: String, item: Item)]
}
