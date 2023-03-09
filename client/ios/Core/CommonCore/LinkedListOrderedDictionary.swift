// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

public final class LinkedListOrderedDictionary<T>: OrderedDictionary {
  public typealias Item = T

  private enum State {
    case empty
    case nonEmpty(head: Node<T>, tail: Node<T>, keyToNode: [String: Node<T>])
  }

  private var state: State

  public init(items: [(key: String, item: T)]) {
    state = .empty
    items.reversed().forEach {
      insertFirst(key: $0.key, item: $0.item)
    }
  }

  public convenience init() {
    self.init(items: [])
  }

  public func asArray() -> [(key: String, item: T)] {
    switch state {
    case .empty:
      return []
    case let .nonEmpty(head, _, keyToNode):
      var result = [(head.key, head.value)]
      var current = head
      while let next = current.next {
        result.append((next.key, next.value))
        current = next
      }
      assert(keyToNode.count == result.count)
      return result
    }
  }

  public func insertFirst(key: String, item: T) {
    let newNode = node(for: key)
      .flatMap { node in modified(node) { $0.value = item } }
      ?? Node(key: key, value: item)

    switch state {
    case .empty:
      self.state = .nonEmpty(
        head: newNode,
        tail: newNode,
        keyToNode: [key: newNode]
      )
    case let .nonEmpty(head: head, tail: tail, keyToNode: keyToNode):
      let newTail: Node<T>
      if newNode.key == tail.key {
        newTail = tail.previous ?? tail
      } else {
        newTail = tail
      }
      if newNode.key != head.key {
        newNode.previous?.next = newNode.next
        newNode.next?.previous = newNode.previous
        newNode.previous = nil
        newNode.next = head
        head.previous = newNode
      }
      self.state = .nonEmpty(
        head: newNode,
        tail: newTail,
        keyToNode: modified(keyToNode) { $0[key] = newNode }
      )
    }
  }

  public func value(for key: String) -> T? {
    node(for: key)?.value
  }

  private func node(for key: String) -> Node<T>? {
    switch state {
    case .empty:
      return nil
    case let .nonEmpty(head: _, tail: _, keyToNode: keyToNode):
      return keyToNode[key]
    }
  }

  public func removeLast() -> (key: String, item: T)? {
    switch state {
    case .empty:
      return nil
    case let .nonEmpty(head, tail, keyToNode):
      if let previous = tail.previous {
        previous.next = nil
        state = .nonEmpty(
          head: head,
          tail: previous,
          keyToNode: modified(keyToNode) { $0[tail.key] = nil }
        )
      } else {
        state = .empty
      }
      return (tail.key, tail.value)
    }
  }

  public func remove(key: String) -> T? {
    switch state {
    case .empty:
      return nil
    case let .nonEmpty(head, tail, keyToNode):
      guard let node = keyToNode[key] else { return nil }

      if head !== tail {
        node.previous?.next = node.next
        node.next?.previous = node.previous

        state = .nonEmpty(
          head: node !== head ? head : node.next!,
          tail: node !== tail ? tail : tail.previous!,
          keyToNode: modified(keyToNode) { $0[key] = nil }
        )
        return node.value
      } else {
        state = .empty
        return tail.value
      }
    }
  }
}

private final class Node<T> {
  let key: String
  var value: T

  var previous: Node?
  var next: Node?

  init(key: String, value: T) {
    self.key = key
    self.value = value
  }
}
