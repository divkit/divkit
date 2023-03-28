// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

extension Array where Element: AnyObject {
  @inlinable
  public func isEqualByReferences(_ rhs: [Element]) -> Bool {
    guard count == rhs.count else { return false }

    for pair in zip(self, rhs) {
      guard pair.0 === pair.1 else {
        return false
      }
    }

    return true
  }
}

@inlinable
public func == <T: Equatable>(lhs: [T], rhs: [T?]) -> Bool {
  let nonOptionalRhs = rhs.compactMap { $0 }
  guard nonOptionalRhs.count == rhs.count else { return false }

  return lhs == nonOptionalRhs
}

@inlinable
public func == <T: Equatable>(lhs: [T?], rhs: [T]) -> Bool {
  let nonOptionalLhs = lhs.compactMap { $0 }
  guard nonOptionalLhs.count == lhs.count else { return false }

  return rhs == nonOptionalLhs
}

@inlinable
public func reverseIndex<T: Collection>(_ index: T.Index, inCollection collection: T) -> T.Index {
  let distanceToIndex = collection.distance(from: collection.startIndex, to: index)
  return collection.index(collection.endIndex, offsetBy: -(1 + distanceToIndex))
}

@inlinable
public func += <T: RangeReplaceableCollection>(
  collection: inout T, object: T.Element
) {
  collection.append(object)
}

extension Collection {
  @inlinable
  public func firstMatchWithIndex(
    _ predicate: @escaping (Element) -> Bool
  ) -> (Index, Element)? {
    zip(indices, self).first { predicate($0.1) }
  }

  @inlinable
  public func floorIndex<T: BinaryFloatingPoint>(
    _ index: T
  ) -> Int where Index == Int {
    guard !isEmpty else {
      return startIndex
    }

    let integral = Int(floor(index))
    return clamp(integral, min: startIndex, max: endIndex.advanced(by: -1))
  }

  @inlinable
  public func ceilIndex<T: BinaryFloatingPoint>(
    _ index: T
  ) -> Int where Index == Int {
    guard !isEmpty else {
      return startIndex
    }

    let integral = Int(ceil(index))
    return clamp(integral, min: startIndex, max: endIndex.advanced(by: -1))
  }
}

extension MutableCollection where Element: MutableCollection {
  @inlinable
  public subscript(coords: (Index, Element.Index)) -> Element.Element {
    get {
      self[coords.0][coords.1]
    }
    set {
      self[coords.0][coords.1] = newValue
    }
  }
}

extension RangeReplaceableCollection where Index == Int {
  @inlinable
  public func stableSort(isLessOrEqual: (Element, Element) -> Bool) -> Self {
    var result = self
    var aux: [Element] = []
    aux.reserveCapacity(Int(count))

    func merge(_ lo: Index, _ mid: Index, _ hi: Index, isLessOrEqual: (Element, Element) -> Bool) {
      aux.removeAll(keepingCapacity: true)

      var i = lo, j = mid
      while i < mid, j < hi {
        if isLessOrEqual(result[i], result[j]) {
          aux.append(result[i])
          i = result.index(after: i)
        } else {
          aux.append(result[j])
          j = result.index(after: j)
        }
      }
      aux.append(contentsOf: result[i..<mid])
      aux.append(contentsOf: result[j..<hi])
      result.replaceSubrange(lo..<hi, with: aux)
    }

    var size = 1
    while size < count {
      for lo in stride(from: startIndex, to: index(endIndex, offsetBy: -size), by: size * 2) {
        merge(
          lo,
          lo.advanced(by: size),
          result.index(lo, offsetBy: size * 2, limitedBy: endIndex) ?? endIndex,
          isLessOrEqual: isLessOrEqual
        )
      }
      size *= 2
    }

    return result
  }
}

extension NSArray {
  public func toJSONData() -> Data? {
    try? JSONSerialization.data(withJSONObject: self, options: [])
  }
}

@inlinable
public func == <A: Equatable, B: Equatable>(lhs: [(A, B)], rhs: [(A, B)]) -> Bool {
  guard lhs.count == rhs.count else {
    return false
  }

  for pair in zip(lhs, rhs) {
    guard pair.0 == pair.1 else {
      return false
    }
  }

  return true
}

@inlinable
public func != <A: Equatable, B: Equatable>(lhs: [(A, B)], rhs: [(A, B)]) -> Bool {
  !(lhs == rhs)
}

extension Collection where Element: Equatable {
  @inlinable
  public func element(after item: Element) -> Element? {
    guard let itemIndex = firstIndex(of: item) else {
      return nil
    }
    let afterIndex = index(after: itemIndex)
    guard afterIndex != endIndex else {
      return nil
    }
    return self[afterIndex]
  }
}

extension RandomAccessCollection {
  public var lastElementIndex: Index? {
    isEmpty ? nil : index(before: endIndex)
  }
}

@inlinable
public func arraysEqual<T>(_ lhs: [T], _ rhs: [T], equalityTest: (T, T) -> Bool) -> Bool {
  guard lhs.count == rhs.count else { return false }
  return zip(lhs, rhs).first { equalityTest($0.0, $0.1) == false } == nil ? true : false
}

@inlinable
public func == <A: Equatable, B: Equatable, C: Equatable>(
  _ lhs: [(A, B, C)],
  _ rhs: [(A, B, C)]
) -> Bool {
  arraysEqual(lhs, rhs, equalityTest: ==)
}

extension Array {
  @inlinable
  public func iterativeFlatMap<T>(_ transform: (Element, Index) throws -> T?) rethrows -> [T] {
    var result = [T]()
    result.reserveCapacity(count)
    for item in self {
      if let transformedItem = try transform(item, result.count) {
        result.append(transformedItem)
      }
    }
    return result
  }

  @inlinable
  public func toDictionary<K, V>() -> [K: V] where Element == (K, V?) {
    var dict = [K: V]()
    forEach { key, value in
      dict[key] = value
    }
    return dict
  }

  public func find<T>(_ transform: (Element) -> T?) -> T? {
    for item in self {
      if let r = transform(item) {
        return r
      }
    }
    return nil
  }

  public func findAll<T>(_ transform: (Element) -> T?) -> [T] {
    var result = [T]()
    forEach {
      if let found = transform($0) {
        result += found
      }
    }
    return result
  }

  @inlinable
  public var randomElement: Element? {
    guard !isEmpty else { return nil }
    return self[Int(arc4random_uniform(UInt32(count)))]
  }
}

extension Array where Element: Hashable {
  @inlinable
  public var uniqueElements: [Element] {
    Array(Set(self))
  }
}
