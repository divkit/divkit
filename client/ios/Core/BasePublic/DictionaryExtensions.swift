// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

extension NSDictionary {
  public func toStringDictionary() -> [String: String] {
    var dictionary: [String: String] = [:]
    for (key, value) in self {
      dictionary[String(describing: key)] = String(describing: value)
    }
    return dictionary
  }
}

extension Dictionary {
  @inlinable
  public init<K: Sequence, E: Sequence>(_ keys: K, _ values: E) where K.Element == Key,
    E.Element == Value {
    self.init(zip(keys, values), uniquingKeysWith: { $1 })
  }

  @inlinable
  public func valuesForKeysMatching(_ includeKey: (Key) -> Bool) -> [Value] {
    compactMap { includeKey($0.0) ? $0.1 : nil }
  }

  public func toJSONData() -> Data? {
    try? JSONSerialization.data(withJSONObject: self, options: [])
  }

  @inlinable
  public func value(forCaseInsensitiveKey key: Key) -> Value? {
    guard let lowercasedKey = (key as? String)?.lowercased() else { return nil }
    return first(where: { ($0.key as? String)?.lowercased() == lowercasedKey })?.value
  }

  @inlinable
  public func transformed<T>(_ transform: (Value) throws -> T) rethrows -> [Key: T] {
    [Key: T](try map { ($0.key, try transform($0.value)) }, uniquingKeysWith: { $1 })
  }

  @inlinable
  public func map<ResultKey, ResultValue>(
    key keyMapper: (Key) throws -> ResultKey,
    value valueMapper: (Value) throws -> ResultValue
  ) rethrows -> [ResultKey: ResultValue] {
    var result = [:] as [ResultKey: ResultValue]
    for (key, value) in self {
      result[try keyMapper(key)] = try valueMapper(value)
    }
    return result
  }

  @inlinable
  public func filteringNilValues<T>() -> [Key: T] where Value == T? {
    [Key: T](
      compactMap {
        switch $0 {
        case (_, .none):
          return nil
        case let (key, .some(value)):
          return (key, value)
        }
      },
      uniquingKeysWith: { $1 }
    )
  }

  @inlinable
  public func mergingRecursively(_ other: [Key: Value]) -> [Key: Value] {
    var result = self
    for (key, otherValue) in other {
      if let existingValue = result[key] {
        if let otherDict = otherValue as? [Key: Value],
           let existingDict = existingValue as? [Key: Value] {
          result[key] = existingDict.mergingRecursively(otherDict) as? Value
        } else {
          result[key] = otherValue
        }
      } else {
        result[key] = otherValue
      }
    }
    return result
  }

  @inlinable
  public mutating func getOrCreate(_ key: Key, factory: () -> Value) -> Value {
    if let value = self[key] {
      return value
    }
    let value = factory()
    self[key] = value
    return value
  }
}

@inlinable
public func + <K, V>(lhs: [K: V], rhs: [K: V]) -> [K: V] {
  lhs.merging(rhs, uniquingKeysWith: { $1 })
}

@inlinable
public func += <K, V>(lhs: inout [K: V], rhs: [K: V]) {
  lhs.merge(rhs, uniquingKeysWith: { $1 })
}

extension Dictionary where Key == String {
  @inlinable
  public var lowercasedKeys: [Key: Value] {
    var result: [String: Value] = [:]
    for key in keys {
      result[key.lowercased()] = self[key]
    }
    return result
  }
}

extension Dictionary {
  public var jsonString: String? {
    guard !self.isEmpty else {
      return nil
    }

    var result = "{"
    for (key, value) in self {
      result.append("\"\(key)\":\"\(value)\",")
    }
    result.removeLast()
    result.append("}")

    return result
  }
}
