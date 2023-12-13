import Foundation

final class PrototypesValueStorage {
  private final class Node {
    let value: Character?
    var children: [Character: Node] = [:]
    var data: [String: AnyHashable] = [:]
    var isEndOfWord: Bool = false

    init(value: Character? = nil) {
      self.value = value
    }
  }

  private let root = Node()

  func insert(prefix: String, data: [String: AnyHashable]) {
    var node = root

    for char in prefix {
      if let child = node.children[char] {
        node = child
      } else {
        let newNode = Node(value: char)
        node.children[char] = newNode
        node = newNode
      }
    }

    node.data = data
    node.isEndOfWord = true
  }

  func findValue<T>(expression: String) -> T? {
    var node = root
    var index = expression.startIndex
    var result: T?
    while index != expression.endIndex {
      if node.isEndOfWord, let value = node.data[String(expression.suffix(from: index))] {
        result = value as? T
      }

      guard let child = node.children[expression[index]] else {
        return result
      }

      node = child
      index = expression.index(after: index)
    }

    return result
  }
}
