// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

private let assignmentQueue = DispatchQueue(
  label: "Memoization optimization assignment (for classes)",
  attributes: [.concurrent]
)

private func cachedValue<A, B>(
  from cache: Atomic<[A: B]>,
  for key: A,
  fallback: (A) throws -> B
) rethrows -> B {
  if let cachedValue = cache.accessRead({ $0[key] }) {
    return cachedValue
  }
  let value = try fallback(key)
  cache.accessWrite { $0[key] = value }
  return value
}

public func memoize<A: Hashable, B>(_ f: @escaping (A) throws -> B) -> (A) throws -> B {
  let cache = Atomic(initialValue: [A: B]())
  return { (input: A) -> B in
    try cachedValue(from: cache, for: input, fallback: f)
  }
}

public func memoize<A: Hashable, B>(_ f: @escaping (A) -> B) -> (A) -> B {
  let cache = Atomic(initialValue: [A: B]())
  return { (input: A) -> B in
    cachedValue(from: cache, for: input, fallback: f)
  }
}

private struct MemoizeParams2<A: Hashable, B: Hashable>: Hashable {
  let a: A
  let b: B
}

public func memoize<A: Hashable, B: Hashable, C>(_ f: @escaping (A, B) -> C) -> (A, B) -> C {
  let cache = Atomic(initialValue: [MemoizeParams2<A, B>: C]())
  return { (a: A, b: B) -> C in
    cachedValue(from: cache, for: MemoizeParams2(a: a, b: b), fallback: { f($0.a, $0.b) })
  }
}

private struct MemoizeParams3<A: Hashable, B: Hashable, C: Hashable>: Hashable {
  let a: A
  let b: B
  let c: C
}

private class MemoizeParams3AClass<A: Hashable, B: Hashable, C: Hashable>: Hashable
  where A: AnyObject {
  private(set) var a: A
  let b: B
  let c: C

  init(a: A, b: B, c: C) {
    self.a = a
    self.b = b
    self.c = c
  }

  func hash(into hasher: inout Hasher) {
    a.hash(into: &hasher)
    b.hash(into: &hasher)
    c.hash(into: &hasher)
  }

  static func ==(lhs: MemoizeParams3AClass, rhs: MemoizeParams3AClass) -> Bool {
    // This 🦃 performs a very specific optimization for the case when
    // we put the calculations for the specific string (which has reference type) to the cache,
    // but _sometimes_ we re-create the instance of this string. Thus, if we don't modify
    // dictionary key, we'll always miss comparison by reference.
    // Here we rely on the implementation detail of Dictionary, namely we hope that
    // `lhs` corresponds to the key _already contained_ in dictionary and `rhs` corresponds
    // to the key by which we're trying to get the value.
    let aEquals = assignmentQueue.sync { lhs.a === rhs.a || lhs.a == rhs.a }
    if !aEquals {
      return false
    }

    assignmentQueue.sync(flags: .barrier) { lhs.a = rhs.a }

    return lhs.b == rhs.b && lhs.c == rhs.c
  }
}

public func memoize<
  A: Hashable,
  B: Hashable,
  C: Hashable,
  D
>(_ f: @escaping (A, B, C) -> D) -> (A, B, C) -> D {
  let cache = Atomic(initialValue: [MemoizeParams3<A, B, C>: D]())
  return { (a: A, b: B, c: C) -> D in
    cachedValue(
      from: cache,
      for: MemoizeParams3(a: a, b: b, c: c),
      fallback: { f($0.a, $0.b, $0.c) }
    )
  }
}

public func memoizeAClass<
  A: Hashable,
  B: Hashable,
  C: Hashable,
  D
>(_ f: @escaping (A, B, C) -> D) -> (A, B, C) -> D where A: AnyObject {
  let cache = Atomic(initialValue: [MemoizeParams3AClass<A, B, C>: D]())
  return { (a: A, b: B, c: C) -> D in
    cachedValue(
      from: cache,
      for: MemoizeParams3AClass(a: a, b: b, c: c),
      fallback: { f($0.a, $0.b, $0.c) }
    )
  }
}
