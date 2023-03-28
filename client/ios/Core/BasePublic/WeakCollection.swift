// Copyright 2016 Yandex LLC. All rights reserved.

private final class Weak {
  private(set) weak var value: AnyObject?

  init(value: AnyObject) {
    self.value = value
  }
}

public struct WeakCollection<T> {
  private var array: [Weak] = []

  public var isEmpty: Bool {
    array.isEmpty
  }

  public mutating func append(_ object: T) {
    assert((object as AnyObject) is T)
    compact()
    array.append(Weak(value: object as AnyObject))
  }

  public mutating func append(_ objects: [T]) {
    compact()
    for object in objects {
      assert((object as AnyObject) is T)
      array.append(Weak(value: object as AnyObject))
    }
  }

  public mutating func remove(_ object: T) {
    guard let objectIndex = array.firstIndex(where: { $0.value === (object as AnyObject) })
    else { return }
    array.remove(at: objectIndex)
  }

  public mutating func remove(where predicate: (T) throws -> Bool) {
    let maybeIndex = try? array.firstIndex {
      guard let object = $0.value as? T else {
        return false
      }
      return try predicate(object)
    }
    if let index = maybeIndex {
      array.remove(at: index)
    }
  }

  public func contains(_ object: T) -> Bool {
    array.contains(where: { $0.value === (object as AnyObject) })
  }

  public mutating func removeAll() {
    array.removeAll()
  }

  public mutating func compact() {
    array = array.filter { $0.value != nil }
  }
}

extension WeakCollection: CustomStringConvertible {
  public var description: String {
    array.map { $0.value }.description
  }
}

extension WeakCollection: ExpressibleByArrayLiteral {
  public init(arrayLiteral elements: T...) {
    self.init(elements)
  }
}

extension WeakCollection {
  public init<T: Sequence>(_ other: T) where T.Element == Element {
    array = other.map { Weak(value: $0 as AnyObject) }
  }
}

extension WeakCollection: Sequence {
  public typealias Element = T?
  public func makeIterator() -> AnyIterator<T?> {
    var currentIndex = array.startIndex
    return AnyIterator { [array] in
      guard currentIndex < array.endIndex else { return nil }
      let currentElem = array[currentIndex]
      currentIndex = array.index(after: currentIndex)
      return currentElem.value as? T
    }
  }

  public static func +(
    lhs: WeakCollection<T>,
    rhs: WeakCollection<T>
  ) -> WeakCollection<T> {
    WeakCollection(array: lhs.array + rhs.array)
  }

  public static func +=(
    lhs: inout WeakCollection<T>,
    rhs: WeakCollection<T>
  ) {
    lhs.array += rhs.array
  }
}
