import CommonCorePublic

protocol ReferenceEquatable: Equatable {
  var source: Variable<AnyObject?> { get }
}

extension ReferenceEquatable {
  static func ==(lhs: Self, rhs: Self) -> Bool {
    // This code is used to compare blocks when configuring UIViews.
    // So, there must be at least one living block.
    assert(lhs.source.value != nil || rhs.source.value != nil)
    return lhs.source.value === rhs.source.value
  }
}
