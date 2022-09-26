// Copyright 2021 Yandex LLC. All rights reserved.

public typealias NonEmptySet<Element: Hashable> = NonEmpty<Set<Element>>

extension NonEmpty {
  @inlinable
  public init?<E: Hashable>(set: C) where C == Set<E> {
    guard let head = set.first else {
      return nil
    }
    self.init(head, Set(set.dropFirst()))
  }

  @inlinable
  public init<E: Hashable>(set head: E, _ tail: E...) where C == Set<E> {
    self.init(head, Set(tail))
  }
}

extension NonEmpty where Element: Hashable {
  @inlinable
  public func asSet() -> Set<Element> {
    Set(self)
  }
}
