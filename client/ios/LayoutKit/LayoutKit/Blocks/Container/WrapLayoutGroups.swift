import BaseTinyPublic
import Foundation

struct WrapLayoutGroups {
  typealias ChildParametes = (child: ContainerBlock.Child, childSize: CGSize, lineOffset: CGFloat)

  private(set) var childrenWithSeparators: [ContainerBlock.Child] = []
  private(set) var groups: [[ChildParametes]] = [[]]

  private let blockLayoutDirection: UserInterfaceLayoutDirection
  private let children: [ContainerBlock.Child]
  private let separator: ContainerBlock.Separator?
  private let lineSeparator: ContainerBlock.Separator?
  private let size: CGSize
  private let layoutDirection: ContainerBlock.LayoutDirection
  private let keyPath: WritableKeyPath<CGSize, CGFloat>
  private let separatorSize: CGSize
  private let lineSeparatorSize: CGSize
  private var line: [ChildParametes] = []
  private var offset: CGFloat = 0
  private var separatorAdded = false

  init(
    blockLayoutDirection: UserInterfaceLayoutDirection,
    children: [ContainerBlock.Child],
    separator: ContainerBlock.Separator?,
    lineSeparator: ContainerBlock.Separator?,
    size: CGSize,
    layoutDirection: ContainerBlock.LayoutDirection
  ) {
    self.blockLayoutDirection = blockLayoutDirection
    self.children = children
    self.separator = separator
    self.lineSeparator = lineSeparator
    self.size = size
    self.layoutDirection = layoutDirection
    keyPath = layoutDirection.buildingDirectionKeyPath

    separatorSize = separator?.style.content.size(forResizableBlockSize: size) ?? .zero
    lineSeparatorSize = lineSeparator?.style.content.size(forResizableBlockSize: size) ?? .zero

    makeGroups()
  }

  private mutating func makeGroups() {
    addStartLineSeparator()
    children.forEach { child in
      guard !child.isResizable(for: layoutDirection.opposite) else { return }
      if child.isResizable(for: layoutDirection) {
        startNewLine()
        var childSize = child.content.size(forResizableBlockSize: size)
        if let separator {
          if separator.showAtStart {
            childSize[keyPath: keyPath] = childSize[keyPath: keyPath] -
              separatorSize[keyPath: keyPath]
          }
          if separator.showAtEnd {
            childSize[keyPath: keyPath] = childSize[keyPath: keyPath] -
              separatorSize[keyPath: keyPath]
          }
        }
        addChild(child: child, size: childSize)
      } else {
        let childSize = child.content.sizeFor(
          widthOfHorizontallyResizableBlock: .zero,
          heightOfVerticallyResizableBlock: .zero,
          constrainedWidth: keyPath == \.width ?
            size[keyPath: keyPath] : .infinity,
          constrainedHeight: keyPath == \.height ?
            size[keyPath: keyPath] : .infinity
        )
        if childSize.isEmpty {
          return
        }
        if offset + childSize[keyPath: keyPath] + separatorOffset > size[keyPath: keyPath] {
          startNewLine()
          addChild(child: child, size: childSize)
        } else {
          addChild(child: child, size: childSize)
        }
      }
    }
    if line.count > 0 {
      addEndSeparator()
      addLine(line)
      addEndLineSeparator()
    }

    if layoutDirection == .vertical, blockLayoutDirection == .rightToLeft {
      groups.reverse()
    }

    childrenWithSeparators = groups.flatMap { $0 }.map(\.child)
  }

  private var separatorOffset: CGFloat {
    guard let separator else {
      return 0
    }
    let separatorSize = separatorSize[keyPath: keyPath]
    var offset: CGFloat = 0
    if line.count > 0, separator.showBetween {
      offset = separatorSize
    } else if separator.showAtStart {
      offset = separatorSize
    }
    if separator.showAtEnd {
      offset = offset + separatorSize
    }
    return offset
  }

  private mutating func addChild(child: ContainerBlock.Child, size: CGSize) {
    if line.count > 0 {
      addBetweenSeparator()
    } else {
      addStartSeparator()
    }
    line.append((child: child, childSize: size, lineOffset: offset))
    offset += size[keyPath: keyPath]
    separatorAdded = false
  }

  private mutating func addLine(_ line: [ChildParametes]) {
    switch (layoutDirection, blockLayoutDirection) {
    case (.vertical, _):
      groups.append(line)
    case (.horizontal, .leftToRight):
      groups.append(line)
    case (.horizontal, .rightToLeft):
      groups.append(reverseChildren(in: line))
    @unknown default:
      assertionFailure(
        "Unknown blockLayoutDirection (UserInterfaceLayoutDirection) or layoutDirection (ContainerBlock.LayoutDirection)"
      )
    }
  }

  private func reverseChildren(in line: [ChildParametes]) -> [ChildParametes] {
    var reversedLine = [ChildParametes]()
    var offset: CGFloat = 0
    for childParameter in line.reversed() {
      reversedLine
        .append((
          child: childParameter.child,
          childSize: childParameter.childSize,
          lineOffset: offset
        ))
      offset += childParameter.childSize[keyPath: keyPath]
    }
    return reversedLine
  }

  private mutating func startNewLine() {
    if line.count > 0 {
      addEndSeparator()
      addLine(line)
      addBetweenLineSeparator()
    }
    line = []
    offset = 0
    separatorAdded = false
  }

  private mutating func addStartLineSeparator() {
    guard let lineSeparator, lineSeparator.showAtStart else {
      return
    }
    addLineSeparator()
  }

  private mutating func addBetweenLineSeparator() {
    guard let lineSeparator, lineSeparator.showBetween else {
      return
    }
    addLineSeparator()
  }

  private mutating func addEndLineSeparator() {
    guard let lineSeparator, lineSeparator.showAtEnd else {
      return
    }
    addLineSeparator()
  }

  private mutating func addLineSeparator() {
    guard let lineSeparator else {
      return
    }
    groups.append([(child: lineSeparator.style, childSize: lineSeparatorSize, lineOffset: 0)])
  }

  private mutating func addStartSeparator() {
    guard let separator, separator.showAtStart else {
      return
    }
    addSeparator()
  }

  private mutating func addEndSeparator() {
    guard let separator, separator.showAtEnd else {
      return
    }
    addSeparator()
  }

  private mutating func addBetweenSeparator() {
    guard let separator, separator.showBetween else {
      return
    }
    addSeparator()
  }

  private mutating func addSeparator() {
    guard let separator, !separatorAdded else {
      return
    }
    separatorAdded = true
    line.append((child: separator.style, childSize: separatorSize, lineOffset: offset))
    offset += separatorSize[keyPath: keyPath]
  }
}

extension ContainerBlock.LayoutDirection {
  var buildingDirectionKeyPath: WritableKeyPath<CGSize, CGFloat> {
    switch self {
    case .horizontal:
      return \.width
    case .vertical:
      return \.height
    }
  }

  var transferDirectionKeyPath: KeyPath<CGSize, CGFloat> {
    switch self {
    case .horizontal:
      return \.height
    case .vertical:
      return \.width
    }
  }
}

extension ContainerBlock.Child {
  fileprivate func isResizable(for layoutDirection: ContainerBlock.LayoutDirection) -> Bool {
    switch layoutDirection {
    case .horizontal:
      return content.isHorizontallyResizable
    case .vertical:
      return content.isVerticallyResizable
    }
  }

  fileprivate func isConstrained(for layoutDirection: ContainerBlock.LayoutDirection) -> Bool {
    switch layoutDirection {
    case .horizontal:
      return content.isHorizontallyConstrained
    case .vertical:
      return content.isVerticallyConstrained
    }
  }
}
