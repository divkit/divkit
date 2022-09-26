// Copyright 2019 Yandex LLC. All rights reserved.

public struct NonEmpty<C: Collection>: Collection {
  public struct Index: Comparable {
    fileprivate let value: InternalIndex

    public static func <(lhs: Index, rhs: Index) -> Bool {
      lhs.value < rhs.value
    }

    fileprivate static var head: Index {
      Index(value: .head)
    }

    fileprivate static func tail(_ underlyingIndex: C.Index) -> Index {
      Index(value: .tail(underlyingIndex))
    }
  }

  fileprivate enum InternalIndex: Comparable {
    case head
    case tail(C.Index)

    @inlinable
    public static func <(lhs: InternalIndex, rhs: InternalIndex) -> Bool {
      switch (lhs, rhs) {
      case let (.tail(l), .tail(r)):
        return l < r
      case (.head, .tail):
        return true
      case (.tail, .head), (.head, .head):
        return false
      }
    }
  }

  public typealias Element = C.Element

  private(set) var head: Element
  private(set) var tail: C

  public init(_ head: Element, _ tail: C) {
    self.head = head
    self.tail = tail
  }

  public var startIndex: Index {
    .head
  }

  public var endIndex: Index {
    .tail(tail.endIndex)
  }

  public var count: Int {
    tail.count + 1
  }

  public subscript(position: Index) -> Element {
    switch position.value {
    case .head:
      return head
    case let .tail(index):
      return tail[index]
    }
  }

  public func index(after i: Index) -> Index {
    switch i.value {
    case .head:
      return .tail(tail.startIndex)
    case let .tail(index):
      return .tail(tail.index(after: index))
    }
  }
}

extension NonEmpty {
  public var first: Element {
    head
  }

  public func flatMap<T>(_ transform: (Element) throws -> NonEmpty<[T]>) rethrows -> NonEmpty<[T]> {
    var result = try transform(head)
    for element in tail {
      try result.append(contentsOf: transform(element))
    }
    return result
  }

  public func map<T>(_ transform: (Element) throws -> T) rethrows -> NonEmpty<[T]> {
    try NonEmpty<[T]>(transform(head), tail.map(transform))
  }

  public func max(by areInIncreasingOrder: (Element, Element) throws -> Bool) rethrows -> Element {
    try tail
      .max(by: areInIncreasingOrder)
      .map { try areInIncreasingOrder(head, $0) ? $0 : head } ?? head
  }

  public func min(by areInIncreasingOrder: (Element, Element) throws -> Bool) rethrows -> Element {
    try tail
      .min(by: areInIncreasingOrder)
      .map { try areInIncreasingOrder(head, $0) ? head : $0 } ?? head
  }

  public func sorted(by areInIncreasingOrder: (Element, Element) throws -> Bool) rethrows
    -> NonEmpty<[Element]> {
    var result = ContiguousArray(self)
    try result.sort(by: areInIncreasingOrder)
    return NonEmpty<[Element]>(result.first ?? head, Array(result.dropFirst()))
  }
}

extension NonEmpty: CustomStringConvertible {
  public var description: String {
    "\(head)\(tail)"
  }
}

extension NonEmpty: Equatable where C: Equatable, C.Element: Equatable {}

extension NonEmpty: Hashable where C: Hashable, C.Element: Hashable {}

extension NonEmpty: Decodable where C: Decodable, C.Element: Decodable {}

extension NonEmpty: Encodable where C: Encodable, C.Element: Encodable {}

extension NonEmpty: Comparable where C: Comparable, C.Element: Comparable {
  public static func <(lhs: NonEmpty, rhs: NonEmpty) -> Bool {
    lhs.head < rhs.head && lhs.tail < rhs.tail
  }
}

extension NonEmpty where C.Element: Comparable {
  public func max() -> Element {
    Swift.max(head, tail.max() ?? head)
  }

  public func min() -> Element {
    Swift.min(head, tail.min() ?? head)
  }

  public func sorted() -> NonEmpty<[Element]> {
    var result = ContiguousArray(self)
    result.sort()
    return NonEmpty<[Element]>(result.first ?? head, Array(result.dropFirst()))
  }
}

extension NonEmpty: BidirectionalCollection where C: BidirectionalCollection {
  public func index(before i: Index) -> Index {
    switch i.value {
    case .head:
      return .tail(tail.index(before: tail.startIndex))
    case let .tail(index):
      return index == tail.startIndex ? .head : .tail(tail.index(before: index))
    }
  }

  public var last: Element {
    tail.last ?? head
  }
}

extension NonEmpty where C: RangeReplaceableCollection {
  @inlinable
  public init(_ head: Element, _ tail: Element...) {
    self.init(head, C(tail))
  }

  public mutating func append(_ newElement: Element) {
    tail.append(newElement)
  }

  public mutating func append<S: Sequence>(contentsOf newElements: S) where Element == S.Element {
    tail.append(contentsOf: newElements)
  }

  public mutating func insert(_ newElement: Element, at i: Index) {
    switch i.value {
    case .head:
      tail.insert(head, at: tail.startIndex)
      head = newElement
    case let .tail(index):
      tail.insert(newElement, at: tail.index(after: index))
    }
  }

  public mutating func insert<S>(contentsOf newElements: S, at i: Index)
    where S: Collection, Element == S.Element {
    switch i.value {
    case .head:
      guard let first = newElements.first else { return }
      var tail = C(newElements.dropFirst())
      tail.append(head)
      self.tail.insert(contentsOf: tail, at: self.tail.startIndex)
      head = first
    case let .tail(index):
      tail.insert(contentsOf: newElements, at: tail.index(after: index))
    }
  }

  public static func + <S: Sequence>(lhs: NonEmpty, rhs: S) -> NonEmpty where Element == S.Element {
    var tail = lhs.tail
    tail.append(contentsOf: rhs)
    return NonEmpty(lhs.head, tail)
  }

  @inlinable
  public static func += <S: Sequence>(lhs: inout NonEmpty, rhs: S) where Element == S.Element {
    lhs.append(contentsOf: rhs)
  }
}

extension NonEmpty where C: RangeReplaceableCollection & BidirectionalCollection {
  public mutating func insert(_ newElement: Element, at i: C.Index) {
    insert(newElement, at: i == tail.startIndex ? .head : .tail(tail.index(before: i)))
  }

  public mutating func insert<S>(contentsOf newElements: S, at i: C.Index)
    where S: Collection, Element == S.Element {
    insert(contentsOf: newElements, at: i == tail.startIndex ? .head : .tail(tail.index(before: i)))
  }
}

extension NonEmpty {
  public func joined<S: Sequence, RRC: RangeReplaceableCollection>(
    separator: S
  ) -> NonEmpty<RRC> where Element == NonEmpty<RRC>, S.Element == RRC.Element {
    NonEmpty<RRC>(
      head.head, head.tail + RRC(separator) + RRC(tail.joined(separator: separator))
    )
  }

  @inlinable
  public func joined<RRC: RangeReplaceableCollection>() -> NonEmpty<RRC>
    where Element == NonEmpty<RRC> {
    joined(separator: RRC())
  }
}

extension NonEmpty {
  public func randomElement<T: RandomNumberGenerator>(using generator: inout T) -> Element {
    ContiguousArray(self).randomElement(using: &generator) ?? head
  }

  @inlinable
  public func randomElement() -> Element {
    var generator = SystemRandomNumberGenerator()
    return randomElement(using: &generator)
  }
}

extension NonEmpty where C: RangeReplaceableCollection {
  public mutating func shuffle<T: RandomNumberGenerator>(using generator: inout T) {
    let result = ContiguousArray(self).shuffled(using: &generator)
    head = result.first ?? head
    tail = C(result.dropFirst())
  }

  public mutating func shuffle() {
    var generator = SystemRandomNumberGenerator()
    shuffle(using: &generator)
  }

  @inlinable
  public func shuffled<T: RandomNumberGenerator>(using generator: inout T) -> NonEmpty {
    var copy = self
    copy.shuffle(using: &generator)
    return copy
  }

  @inlinable
  public func shuffled() -> NonEmpty {
    var generator = SystemRandomNumberGenerator()
    return shuffled(using: &generator)
  }
}

extension NonEmpty: MutableCollection where C: MutableCollection {
  public subscript(position: Index) -> Element {
    get {
      switch position.value {
      case .head:
        return head
      case let .tail(index):
        return tail[index]
      }
    }
    set {
      switch position.value {
      case .head:
        head = newValue
      case let .tail(index):
        tail[index] = newValue
      }
    }
  }
}

extension NonEmpty where C: BidirectionalCollection {
  private func index(byCIndex cIndex: C.Index) -> Index {
    cIndex == tail.startIndex ? .head : .tail(tail.index(before: cIndex))
  }

  public subscript(position: C.Index) -> Element {
    self[index(byCIndex: position)]
  }
}

extension NonEmpty where C: MutableCollection & BidirectionalCollection {
  public subscript(position: C.Index) -> Element {
    get {
      self[index(byCIndex: position)]
    }
    set {
      self[index(byCIndex: position)] = newValue
    }
    _modify {
      yield &self[index(byCIndex: position)]
    }
  }
}

extension NonEmpty {
  public func prefix(_ maxLength: Int) -> NonEmpty<[Element]>? {
    guard maxLength > 0 else {
      return nil
    }
    return NonEmpty<[Element]>(head, Array(tail.prefix(maxLength - 1)))
  }
}
