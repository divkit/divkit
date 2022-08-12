// Copyright 2018 Yandex LLC. All rights reserved.

extension Range {
  public func extended(toContain other: Range<Bound>) -> Range<Bound> {
    Swift.min(lowerBound, other.lowerBound)..<Swift.max(upperBound, other.upperBound)
  }
}
