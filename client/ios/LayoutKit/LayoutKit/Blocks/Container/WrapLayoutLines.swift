import Foundation
import VGSL

struct WrapLayoutLines {
  struct SeparatorSize {
    let start: CGSize?
    let end: CGSize?
    let between: CGSize?
  }

  typealias ChildParameters = (child: ContainerBlock.Child, childSize: CGSize, lineOffset: CGFloat)

  enum Line {
    case content([ChildParameters])
    /// A `line_separator`, or the `line_spacing` block stored in `lineSeparator.between`.
    case separator(ChildParameters)

    var children: [ChildParameters] {
      switch self {
      case let .content(children):
        children
      case let .separator(child):
        [child]
      }
    }
  }

  private(set) var childrenWithSeparators: [ContainerBlock.Child] = []
  private(set) var lines: [Line] = []

  private let blockLayoutDirection: UserInterfaceLayoutDirection
  private let children: [ContainerBlock.Child]
  private let separator: ContainerBlock.Separator?
  private let lineSeparator: ContainerBlock.Separator?
  private let size: CGSize
  private let layoutDirection: ContainerBlock.LayoutDirection
  private let keyPath: WritableKeyPath<CGSize, CGFloat>
  private let separatorSize: SeparatorSize
  private let lineSeparatorSize: SeparatorSize
  private var currentLineItems: [ChildParameters] = []
  private var offset: CGFloat = 0
  private var separatorAdded = false

  private var separatorOffset: CGFloat {
    if separator == nil {
      return 0
    }
    var offset: CGFloat = 0
    if currentLineItems.count > 0, let between = separatorSize.between {
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

    makeLines()
  }

  private mutating func makeLines() {
    addStartLineSeparator()
    for child in children {
      guard !child.isResizable(for: layoutDirection.opposite) else { continue }
      if child.isResizable(for: layoutDirection) {
        let resizableSize = child.content.size(forResizableBlockSize: size)
        if resizableSize[keyPath: keyPath] < size[keyPath: keyPath] {
          // A match_parent item bounded by max_size (smaller than the line) does not fill the
          // line, so it packs onto the current line like a fixed-size item, keeping its clamped
          // size. This matches Android, where such items share a line instead of each taking one.
          if offset + resizableSize[keyPath: keyPath] + separatorOffset > size[keyPath: keyPath] {
            startNewLine()
          }
          addChild(child: child, size: resizableSize)
        } else {
          // The item fills or overflows the line, so it gets its own line.
          startNewLine()
          var childSize = resizableSize
          if let start = separatorSize.start {
            childSize[keyPath: keyPath] = childSize[keyPath: keyPath] -
              start[keyPath: keyPath]
          }
          if let end = separatorSize.end {
            childSize[keyPath: keyPath] = childSize[keyPath: keyPath] -
              end[keyPath: keyPath]
          }
          addChild(child: child, size: childSize)
        }
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
    if currentLineItems.count > 0 {
      addEndSeparator()
      addLine(currentLineItems)
      addEndLineSeparator()
    }

    if layoutDirection == .vertical, blockLayoutDirection == .rightToLeft {
      lines.reverse()
    }

    childrenWithSeparators = lines.flatMap(\.children).map(\.child)
  }

  private mutating func addChild(child: ContainerBlock.Child, size: CGSize) {
    if currentLineItems.count > 0 {
      addBetweenSeparator()
    } else {
      addStartSeparator()
    }
    currentLineItems.append((child: child, childSize: size, lineOffset: offset))
    offset += size[keyPath: keyPath]
    separatorAdded = false
  }

  private mutating func addLine(_ items: [ChildParameters]) {
    switch (layoutDirection, blockLayoutDirection) {
    case (.vertical, _):
      lines.append(.content(items))
    case (.horizontal, .leftToRight):
      lines.append(.content(items))
    case (.horizontal, .rightToLeft):
      lines.append(.content(reverseChildren(in: items)))
    @unknown default:
      assertionFailure(
        "Unknown blockLayoutDirection (UserInterfaceLayoutDirection) or layoutDirection (ContainerBlock.LayoutDirection)"
      )
    }
  }

  private func reverseChildren(in items: [ChildParameters]) -> [ChildParameters] {
    var reversedLine = [ChildParameters]()
    var offset: CGFloat = 0
    for childParameter in items.reversed() {
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
    if currentLineItems.count > 0 {
      addEndSeparator()
      addLine(currentLineItems)
      addBetweenLineSeparator()
    }
    currentLineItems = []
    offset = 0
    separatorAdded = false
  }

  private mutating func addStartLineSeparator() {
    guard let lineSeparator, let start = lineSeparator.start,
          let size = lineSeparatorSize.start else {
      return
    }
    addLineSeparator(start, size: size)
  }

  private mutating func addBetweenLineSeparator() {
    guard let lineSeparator, let between = lineSeparator.between,
          let size = lineSeparatorSize.between else {
      return
    }
    addLineSeparator(between, size: size)
  }

  private mutating func addEndLineSeparator() {
    guard let lineSeparator, let end = lineSeparator.end, let size = lineSeparatorSize.end else {
      return
    }
    addLineSeparator(end, size: size)
  }

  private mutating func addLineSeparator(_ child: ContainerBlock.Child, size: CGSize) {
    lines.append(.separator((child: child, childSize: size, lineOffset: 0)))
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
    currentLineItems.append((child: separator, childSize: size, lineOffset: offset))
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
