// Copyright 2021 Yandex LLC. All rights reserved.

public struct Zip3Iterator<
  A: IteratorProtocol,
  B: IteratorProtocol,
  C: IteratorProtocol
>: IteratorProtocol {
  private var first: A
  private var second: B
  private var third: C

  fileprivate init(_ first: A, _ second: B, _ third: C) {
    self.first = first
    self.second = second
    self.third = third
  }

  public mutating func next() -> (A.Element, B.Element, C.Element)? {
    if let a = first.next(), let b = second.next(), let c = third.next() {
      return (a, b, c)
    }
    return nil
  }
}

public func zip3<A: Sequence, B: Sequence, C: Sequence>(
  _ a: A,
  _ b: B,
  _ c: C
) -> IteratorSequence<Zip3Iterator<A.Iterator, B.Iterator, C.Iterator>> {
  IteratorSequence(Zip3Iterator(a.makeIterator(), b.makeIterator(), c.makeIterator()))
}

extension Sequence {
  public func group(batchSize: Int) -> [[Element]] {
    var result = [[Element]]()
    var iterator = makeIterator()
    var currentBatch = [Element]()
    while let currentItem = iterator.next() {
      if currentBatch.count == batchSize {
        result.append(currentBatch)
        currentBatch = [Element]()
      }
      currentBatch.append(currentItem)
    }
    if !currentBatch.isEmpty {
      result.append(currentBatch)
    }
    return result
  }

  /// Returns array of unique element based on comparator
  /// Complexity: O(n^2)
  @inlinable
  public func uniqued(comparator: @escaping (Element, Element) -> Bool) -> [Element] {
    var result: [Element] = []
    for element in self {
      if result.contains(where: { comparator(element, $0) }) {
        continue
      }
      result.append(element)
    }
    return result
  }
}
