import Foundation
import VGSL

struct WrapLayoutGroups {
  struct SeparatorSize {
    let start: CGSize?
    let end: CGSize?
    let between: CGSize?
  }

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
  private let separatorSize: SeparatorSize
  private let lineSeparatorSize: SeparatorSize
  private var line: [ChildParametes] = []
  private var offset: CGFloat = 0
  private var separatorAdded = false

  private var separatorOffset: CGFloat {
    if separator == nil {
      return 0
    }
    var offset: CGFloat = 0
    if line.count > 0, let between = separatorSize.between {
      offset = between[keyPath: keyPath]
    } else if let start = separatorSize.start {
      offset = start[keyPath: keyPath]
    }
    if let end = separatorSize.end {
      offset = offset + end[keyPath: keyPath]
    }
    return offset
  }

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

    separatorSize = SeparatorSize(
      start: separator?.start?.content.size(forResizableBlockSize: size) ?? .zero,
      end: separator?.end?.content.size(forResizableBlockSize: size) ?? .zero,
      between: separator?.between?.content.size(forResizableBlockSize: size) ?? .zero
    )
    lineSeparatorSize = SeparatorSize(
      start: lineSeparator?.start?.content.size(forResizableBlockSize: size) ?? .zero,
      end: lineSeparator?.end?.content.size(forResizableBlockSize: size) ?? .zero,
      between: lineSeparator?.between?.content.size(forResizableBlockSize: size) ?? .zero
    )

    makeGroups()
  }

  private mutating func makeGroups() {
    addStartLineSeparator()
    for child in children {
      guard !child.isResizable(for: layoutDirection.opposite) else { continue }
      if child.isResizable(for: layoutDirection) {
        startNewLine()
        var childSize = child.content.size(forResizableBlockSize: size)
        if let start = separatorSize.start {
          childSize[keyPath: keyPath] = childSize[keyPath: keyPath] -
            start[keyPath: keyPath]
        }
        if let end = separatorSize.end {
          childSize[keyPath: keyPath] = childSize[keyPath: keyPath] -
            end[keyPath: keyPath]
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
          continue
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
    guard let lineSeparator, let start = lineSeparator.start,
          let size = lineSeparatorSize.start else {
      return
    }
    groups.append([(child: start, childSize: size, lineOffset: 0)])
  }

  private mutating func addBetweenLineSeparator() {
    guard let lineSeparator, let between = lineSeparator.between,
          let size = lineSeparatorSize.between else {
      return
    }
    groups.append([(child: between, childSize: size, lineOffset: 0)])
  }

  private mutating func addEndLineSeparator() {
    guard let lineSeparator, let end = lineSeparator.end, let size = lineSeparatorSize.end else {
      return
    }
    groups.append([(child: end, childSize: size, lineOffset: 0)])
  }

  private mutating func addStartSeparator() {
    guard let separator, let start = separator.start, let size = separatorSize.start else {
      return
    }
    addSeparator(start, size: size)
  }

  private mutating func addEndSeparator() {
    guard let separator, let end = separator.end, let size = separatorSize.end else {
      return
    }
    addSeparator(end, size: size)
  }

  private mutating func addBetweenSeparator() {
    guard let separator, let between = separator.between, let size = separatorSize.between else {
      return
    }
    addSeparator(between, size: size)
  }

  private mutating func addSeparator(_ separator: ContainerBlock.Child, size: CGSize) {
    guard !separatorAdded else { return }
    separatorAdded = true
    line.append((child: separator, childSize: size, lineOffset: offset))
    offset += size[keyPath: keyPath]
  }
}

extension ContainerBlock.LayoutDirection {
  var buildingDirectionKeyPath: WritableKeyPath<CGSize, CGFloat> {
    switch self {
    case .horizontal:
      \.width
    case .vertical:
      \.height
    }
  }

  var transferDirectionKeyPath: KeyPath<CGSize, CGFloat> {
    switch self {
    case .horizontal:
      \.height
    case .vertical:
      \.width
    }
  }
}

extension ContainerBlock.Child {
  fileprivate func isResizable(for layoutDirection: ContainerBlock.LayoutDirection) -> Bool {
    switch layoutDirection {
    case .horizontal:
      content.isHorizontallyResizable
    case .vertical:
      content.isVerticallyResizable
    }
  }

  fileprivate func isConstrained(for layoutDirection: ContainerBlock.LayoutDirection) -> Bool {
    switch layoutDirection {
    case .horizontal:
      content.isHorizontallyConstrained
    case .vertical:
      content.isVerticallyConstrained
    }
  }
}
