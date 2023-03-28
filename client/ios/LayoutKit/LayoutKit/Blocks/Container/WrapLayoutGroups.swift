import BaseTinyPublic
import Foundation

struct WrapLayoutGroups {
  typealias ChildParametes = (child: ContainerBlock.Child, childSize: CGSize, lineOffset: CGFloat)

  private(set) var childrenWithSeparators: [ContainerBlock.Child] = []
  private(set) var groups: [[ChildParametes]] = [[]]

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
    children: [ContainerBlock.Child],
    separator: ContainerBlock.Separator?,
    lineSeparator: ContainerBlock.Separator?,
    size: CGSize,
    layoutDirection: ContainerBlock.LayoutDirection
  ) {
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
      guard !child.isResizable(for: layoutDirection.opposite) && !child
        .isConstrained(for: layoutDirection.opposite) else { return }
      if child.isResizable(for: layoutDirection) {
        startNewLine()
        var childSize = child.content.size(forResizableBlockSize: size)
        if let separator = separator {
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
          addChild(
            child: child,
            size: childSize
          )
        } else {
          addChild(child: child, size: childSize)
        }
      }
    }
    if line.count > 0 {
      addEndSeparator()
      groups.append(line)
      addEndLineSeparator()
    }
  }

  private var separatorOffset: CGFloat {
    guard let separator = separator else {
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
    childrenWithSeparators.append(child)
    line.append((child: child, childSize: size, lineOffset: offset))
    offset += size[keyPath: keyPath]
    separatorAdded = false
  }

  private mutating func startNewLine() {
    if line.count > 0 {
      addEndSeparator()
      groups.append(line)
      addBetweenLineSeparator()
    }
    line = []
    offset = 0
    separatorAdded = false
  }

  private mutating func addStartLineSeparator() {
    guard let lineSeparator = lineSeparator, lineSeparator.showAtStart else {
      return
    }
    addLineSeparator()
  }

  private mutating func addBetweenLineSeparator() {
    guard let lineSeparator = lineSeparator, lineSeparator.showBetween else {
      return
    }
    addLineSeparator()
  }

  private mutating func addEndLineSeparator() {
    guard let lineSeparator = lineSeparator, lineSeparator.showAtEnd else {
      return
    }
    addLineSeparator()
  }

  private mutating func addLineSeparator() {
    guard let lineSeparator = lineSeparator else {
      return
    }
    childrenWithSeparators.append(lineSeparator.style)
    groups.append([(child: lineSeparator.style, childSize: lineSeparatorSize, lineOffset: 0)])
  }

  private mutating func addStartSeparator() {
    guard let separator = separator, separator.showAtStart else {
      return
    }
    addSeparator()
  }

  private mutating func addEndSeparator() {
    guard let separator = separator, separator.showAtEnd else {
      return
    }
    addSeparator()
  }

  private mutating func addBetweenSeparator() {
    guard let separator = separator, separator.showBetween else {
      return
    }
    addSeparator()
  }

  private mutating func addSeparator() {
    guard let separator = separator, !separatorAdded else {
      return
    }
    separatorAdded = true
    childrenWithSeparators.append(separator.style)
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
