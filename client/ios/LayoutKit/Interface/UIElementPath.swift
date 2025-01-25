import Foundation
import VGSL

public struct UIElementPath: CustomStringConvertible, ExpressibleByStringLiteral, Codable {
  private let address: ListNode

  private init(address: ListNode) {
    self.address = address
  }

  public init(_ root: String) {
    address = ListNode(value: root)
  }

  public init(parent: UIElementPath, child: String) {
    address = ListNode(value: child, next: parent.address)
  }

  public init(stringLiteral value: StringLiteralType) {
    self.init(value)
  }

  public var description: String {
    address.joined(separator: "/")
  }

  public var parent: UIElementPath? {
    if let parent = address.next {
      return UIElementPath(address: parent)
    }
    return nil
  }

  public var root: String {
    address.root
  }

  public var leaf: String {
    address.value
  }

  public func starts(with path: UIElementPath) -> Bool {
    if path == self {
      return true
    }
    return parent?.starts(with: path) == true
  }

  public static func parse(_ path: String) -> UIElementPath {
    let split = path.split(separator: "/")
    if let first = split.first {
      return UIElementPath(String(first)) + split.dropFirst().map(String.init)
    }
    return UIElementPath(path)
  }
}

extension UIElementPath: Hashable {
  public func hash(into hasher: inout Hasher) {
    address.hash(into: &hasher)
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

extension UIElementPath? {
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

private final class ListNode: Codable {
  let value: String
  let next: ListNode?
  private var _root: String?

  private lazy var cachedHash: Int = {
    var hasher = Hasher()
    hasher.combine(value)
    hasher.combine(next)

    return hasher.finalize()
  }()

  init(value: String, next: ListNode? = nil) {
    self.value = value
    self.next = next
  }

  var root: String {
    if let _root {
      return _root
    }
    guard let next else {
      return value
    }
    let result = next.root
    _root = result
    return result
  }

  enum CodingKeys: CodingKey {
    case value, next
  }
}

extension ListNode: Hashable {
  func hash(into hasher: inout Hasher) {
    hasher.combine(cachedHash)
  }
}

extension ListNode: Equatable {
  static func ==(lhs: ListNode, rhs: ListNode) -> Bool {
    if lhs === rhs {
      return true
    }
    return lhs.cachedHash == rhs.cachedHash && lhs.value == rhs.value && lhs.next == rhs.next
  }
}

extension ListNode {
  fileprivate func joined(separator: String) -> String {
    var result = String()
    let (depth, countSum) = calcContentsParams()
    result.reserveCapacity(countSum + separator.count * depth)
    appendValue(to: &result)
    return result
  }

  private func calcContentsParams() -> (depth: Int, countSum: Int) {
    var current: ListNode? = self
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
}
