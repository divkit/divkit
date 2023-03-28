import Foundation

import BaseTinyPublic

public struct UIElementPath: CustomStringConvertible, Hashable, ExpressibleByStringLiteral,
  Codable {
  private let address: ListNode<String>

  private init(address: ListNode<String>) {
    self.address = address
  }

  public init(_ root: String) {
    #if INTERNAL_BUILD
    precondition(!root.isEmpty)
    #endif
    address = ListNode(value: root)
  }

  public init(parent: UIElementPath, child: String) {
    #if INTERNAL_BUILD
    precondition(!child.isEmpty)
    #endif
    address = ListNode(value: child, next: parent.address)
  }

  public init(stringLiteral value: StringLiteralType) {
    self.init(value)
  }

  public var description: String {
    address.joined(separator: "/")
  }

  public var parent: UIElementPath? {
    address.next.map(UIElementPath.init)
  }

  public var root: String {
    var current = address
    while let next = current.next { current = next }
    return current.value
  }

  public var leaf: String {
    address.value
  }
}

extension UIElementPath {
  public func hash(into hasher: inout Hasher) {
    address.joined().hash(into: &hasher)
  }
}

extension UIElementPath {
  public static func +(parent: UIElementPath, child: String) -> UIElementPath {
    UIElementPath(parent: parent, child: child)
  }

  public static func +(parent: UIElementPath, child: Int) -> UIElementPath {
    UIElementPath(parent: parent, child: String(child))
  }

  public static func +(parent: UIElementPath, child: [String]) -> UIElementPath {
    child.reduce(parent, +)
  }

  public static func +=(path: inout UIElementPath, child: String) {
    path = path + child
  }

  public static func +=(path: inout UIElementPath, child: Int) {
    path = path + child
  }
}

extension Optional where Wrapped == UIElementPath {
  public static func +(parent: Self, child: String) -> UIElementPath {
    parent.map { $0 + child } ?? UIElementPath(child)
  }
}

extension UIElementPath {
  public typealias ItemIndex = Tagged<UIElementPath, Int>

  public func withItem(at index: ItemIndex) -> UIElementPath {
    self + "item" + index.rawValue
  }

  public var itemIndex: ItemIndex? {
    Int(address.value).map(ItemIndex.init(rawValue:))
  }
}

private final class ListNode<T: Codable>: Codable {
  let value: T
  let next: ListNode<T>?

  init(value: T, next: ListNode<T>? = nil) {
    self.value = value
    self.next = next
  }
}

extension ListNode: Equatable where T: Equatable {
  static func ==(lhs: ListNode<T>, rhs: ListNode<T>) -> Bool {
    if lhs === rhs {
      return true
    }

    return lhs.value == rhs.value && lhs.next == rhs.next
  }
}

extension ListNode: Hashable where T: Hashable {
  func hash(into hasher: inout Hasher) {
    value.hash(into: &hasher)
    next?.hash(into: &hasher)
  }
}

extension ListNode where T == String {
  private func calcContentsParams() -> (depth: Int, countSum: Int) {
    var current: ListNode<T>? = self
    var depth = 0
    var countSum = 0
    while let currentNonNull = current {
      depth += 1
      countSum += currentNonNull.value.count
      current = currentNonNull.next
    }
    return (depth, countSum)
  }

  private func appendValue(to string: inout String) {
    next?.appendValue(to: &string)
    if next != nil {
      string.append("/")
    }
    string.append(value)
  }

  func joined(separator: T = "") -> T {
    var result = String()
    let (depth, countSum) = calcContentsParams()
    result.reserveCapacity(countSum + separator.count * depth)
    appendValue(to: &result)
    return result
  }
}
