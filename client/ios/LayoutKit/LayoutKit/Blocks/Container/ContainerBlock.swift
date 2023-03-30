import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic

public final class ContainerBlock: BlockWithLayout {
  typealias Layout = ContainerBlockLayout
  public static let defaultAnchorPoint = AnchorPoint(
    x: .relative(value: 50),
    y: .relative(value: 50)
  )

  /// Determines direction in which child blocks are laid out in a container
  @frozen
  public enum LayoutDirection: CaseIterable {
    /// Child blocks are laid out horizontally one after another
    case horizontal
    /// Child blocks are laid out vertically one after another
    case vertical

    /// Returns opposite direction
    var opposite: LayoutDirection {
      self == .horizontal ? .vertical : .horizontal
    }
  }

  public enum LayoutMode {
    case wrap
    case noWrap
  }

  public struct Child: Equatable {
    public var content: Block
    /// Alignment for dimension crossing container direction
    public let crossAlignment: CrossAlignment

    public init(content: Block, crossAlignment: CrossAlignment = .leading) {
      self.content = content
      self.crossAlignment = crossAlignment
    }
  }

  public enum CrossAlignment {
    case leading
    case center
    case trailing
    case baseline
  }

  public struct Separator: Equatable {
    public let style: Child
    public let showAtEnd: Bool
    public let showAtStart: Bool
    public let showBetween: Bool

    public init(
      style: Child,
      showAtEnd: Bool = false,
      showAtStart: Bool = false,
      showBetween: Bool = false
    ) {
      self.style = style
      self.showAtEnd = showAtEnd
      self.showAtStart = showAtStart
      self.showBetween = showBetween
    }
  }

  public enum Error: BlockError, Equatable {
    case noChildrenProvided
    case childAndGapCountMismatch
    case inconsistentChildLayoutTraits(details: String)
  }

  private struct CachedSizes {
    var intrinsicWidth: CGFloat?
    var intrinsicHeight: (width: CGFloat, height: CGFloat)?
    var nonResizableSize: (width: CGFloat, height: CGFloat?)?
  }

  public let layoutDirection: LayoutDirection
  public let layoutMode: LayoutMode
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let axialAlignment: Alignment
  public let crossAlignment: CrossAlignment
  public let gaps: [CGFloat]
  public let children: [Child]
  public let separator: Separator?
  public let lineSeparator: Separator?
  public let contentAnimation: BlockAnimation?
  public let anchorPoint: AnchorPoint
  public let childrenTransform: CGAffineTransform
  public let clipContent: Bool
  public let accessibilityElement: AccessibilityElement?

  private var cached = CachedSizes()

  public init(
    layoutDirection: LayoutDirection,
    layoutMode: LayoutMode = .noWrap,
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    axialAlignment: Alignment = .leading,
    crossAlignment: CrossAlignment = .leading,
    gaps: [CGFloat]? = nil,
    children: [Child],
    separator: Separator? = nil,
    lineSeparator: Separator? = nil,
    contentAnimation: BlockAnimation? = nil,
    anchorPoint: AnchorPoint = ContainerBlock.defaultAnchorPoint,
    childrenTransform: CGAffineTransform = .identity,
    clipContent: Bool = true,
    accessibilityElement: AccessibilityElement? = nil
  ) throws {
    if children.isEmpty {
      throw Error.noChildrenProvided
    }
    let gaps = gaps ?? Array(repeating: 0, times: try! UInt(value: children.count + 1))

    if gaps.count != children.count + 1 {
      throw Error.childAndGapCountMismatch
    }

    self.layoutDirection = layoutDirection
    self.layoutMode = layoutMode
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.axialAlignment = axialAlignment
    self.crossAlignment = crossAlignment
    self.gaps = makeGapsWithSeparators(
      gaps: gaps,
      separator: separator,
      layoutMode: layoutMode
    )
    self.children = makeChildrenWithSeparators(
      children: children,
      separator: separator,
      layoutMode: layoutMode
    )
    self.separator = separator
    self.lineSeparator = lineSeparator
    self.contentAnimation = contentAnimation
    self.anchorPoint = anchorPoint
    self.childrenTransform = childrenTransform
    self.clipContent = clipContent
    self.accessibilityElement = accessibilityElement

    try validateLayoutTraits()
  }

  public func ascent(forWidth width: CGFloat) -> CGFloat? {
    guard layoutDirection == .horizontal else {
      return nil
    }
    let layout = ContainerBlockLayout(
      children: children,
      separator: separator,
      lineSeparator: lineSeparator,
      gaps: gaps,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode,
      axialAlignment: axialAlignment,
      crossAlignment: crossAlignment,
      size: CGSize(width: width, height: .zero)
    )
    return layout.ascent
  }

  private func validateLayoutTraits() throws {
    if layoutMode == .wrap {
      switch layoutDirection {
      case .horizontal:
        guard (children.map { $0.content }.allVerticallyNonResizable) else {
          throw Error
            .inconsistentChildLayoutTraits(
              details: "failed to build horizontal wrap container with vertically resizable children"
            )
        }
      case .vertical:
        guard (children.map { $0.content }.allHorizontallyNonResizable) else {
          throw Error
            .inconsistentChildLayoutTraits(
              details: "failed to build vertical wrap container with horizontally resizable children"
            )
        }
      }
    }

    if case .intrinsic = widthTrait {
      switch layoutDirection {
      case .horizontal:
        guard (children.map { $0.content }.allHorizontallyNonResizable) else {
          throw Error
            .inconsistentChildLayoutTraits(
              details: "failed to build intrinsic-width horizontal container with horizontally resizable children"
            )
        }
      case .vertical:
        guard (children.map { $0.content }.hasHorizontallyNonResizable) else {
          throw Error
            .inconsistentChildLayoutTraits(
              details: "failed to build intrinsic-width vertical container wihtout any horizontally nonresizable children"
            )
        }
      }
    }

    if case .intrinsic = heightTrait {
      switch layoutDirection {
      case .horizontal:
        break // this is currently a valid case, see `.max() ?? 0` on line 163
      case .vertical:
        guard (children.map { $0.content }.allVerticallyNonResizable) else {
          throw Error
            .inconsistentChildLayoutTraits(
              details: "failed to build intrinsic-height vertical container with vertically resizable children"
            )
        }
      }
    }
  }

  public var isVerticallyResizable: Bool { heightTrait.isResizable }
  public var isHorizontallyResizable: Bool { widthTrait.isResizable }

  public var calculateWidthFirst: Bool {
    switch widthTrait {
    case .fixed, .weighted:
      return true
    case .intrinsic:
      return !(layoutDirection == .vertical && layoutMode == .wrap)
    }
  }

  public var isVerticallyConstrained: Bool { heightTrait.isConstrained }
  public var isHorizontallyConstrained: Bool { widthTrait.isConstrained }

  public var intrinsicContentWidth: CGFloat {
    if case let .fixed(width) = widthTrait {
      return width
    }

    if let cached = cached.intrinsicWidth {
      return cached
    }

    var result: CGFloat
    switch layoutDirection {
    case .horizontal:
      result = (children.map { $0.content.intrinsicContentWidth } + gaps).reduce(0, +)
    case .vertical:
      result = children.map { $0.content.intrinsicContentWidth }.max()!
    }

    if case let .intrinsic(_, minSize, maxSize) = widthTrait {
      result = clamp(result, min: minSize, max: maxSize)
    }

    cached.intrinsicWidth = result
    return result
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    if case let .fixed(height) = heightTrait {
      return height
    }

    if let cached = cached.intrinsicHeight,
       cached.width.isApproximatelyEqualTo(width) {
      return cached.height
    }

    var result: CGFloat
    switch layoutDirection {
    case .horizontal:
      let preparedChildren = children.map {
        ContainerBlock.Child(
          content: $0.content,
          crossAlignment: $0.crossAlignmentForCalculatingHeight
        )
      }
      let layout = ContainerBlockLayout(
        children: preparedChildren,
        separator: separator,
        lineSeparator: lineSeparator,
        gaps: gaps,
        layoutDirection: layoutDirection,
        layoutMode: layoutMode,
        axialAlignment: axialAlignment,
        crossAlignment: crossAlignment,
        size: CGSize(width: width, height: .zero)
      )
      result = layout.blockFrames.map { $0.maxY }.max() ?? 0
    case .vertical:
      let childrenHeights = children.map { $0.content }.intrinsicHeights(forWidth: width)
      result = (childrenHeights + gaps).reduce(0, +)
    }

    if case let .intrinsic(_, minSize, maxSize) = heightTrait {
      result = clamp(result, min: minSize, max: maxSize)
    }

    cached.intrinsicHeight = (width: width, height: result)
    return result
  }

  public var widthOfHorizontallyNonResizableBlock: CGFloat {
    if case let .fixed(value) = widthTrait {
      return value
    }

    guard case .intrinsic = widthTrait else {
      assertionFailure("cannot get widthOfHorizontallyNonResizableBlock for resizable block")
      return 0
    }

    if let cached = cached.nonResizableSize, cached.height == nil {
      return cached.width
    }

    let result: CGFloat

    switch layoutDirection {
    case .horizontal:
      result = (children.map { $0.content.widthOfHorizontallyNonResizableBlock } + gaps)
        .reduce(0, +)
    case .vertical:
      // MOBYANDEXIOS-1092: Only non-resizable children can influence the width of a container
      // because the widths of resizable children depend on the width of container itself
      result = children.filter { !$0.content.isHorizontallyResizable }
        .map { $0.content.widthOfHorizontallyNonResizableBlock }.max()!
    }

    cached.nonResizableSize = (width: result, height: nil)
    return result
  }

  public var heightOfVerticallyNonResizableBlock: CGFloat {
    assert(
      layoutMode == .wrap && layoutDirection == .vertical,
      "First height calculation should only be used for vertical container with wrap layout mode"
    )
    return heightOfVerticallyNonResizableBlock(forWidth: .zero)
  }

  public func widthOfHorizontallyNonResizableBlock(forHeight height: CGFloat) -> CGFloat {
    assert(
      layoutMode == .wrap && layoutDirection == .vertical,
      "First height calculation should only be used for vertical container with wrap layout mode"
    )
    if case let .fixed(value) = widthTrait {
      return value
    }

    guard case .intrinsic = widthTrait else {
      assertionFailure("cannot get widthOfHorizontallyNonResizableBlock for resizable block")
      return 0
    }

    if let cached = cached.nonResizableSize, let cachedHeight = cached.height,
       cachedHeight.isApproximatelyEqualTo(height) {
      return cached.width
    }

    let result: CGFloat
    let layout = ContainerBlockLayout(
      children: children,
      separator: separator,
      lineSeparator: lineSeparator,
      gaps: gaps,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode,
      axialAlignment: axialAlignment,
      crossAlignment: crossAlignment,
      size: CGSize(width: .zero, height: height)
    )
    result = layout.blockFrames.map { $0.maxX }.max()!
    cached.nonResizableSize = (width: result, height: height)
    return result
  }

  public func heightOfVerticallyNonResizableBlock(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case .intrinsic:
      return intrinsicContentHeight(forWidth: width)
    case .weighted:
      assertionFailure("cannot get heightOfVerticallyNonResizableBlock for resizable block")
      return 0
    }
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    guard case let .weighted(value) = heightTrait else {
      assertionFailure("try to get weight for non resizable block")
      return LayoutTrait.Weight.default
    }
    return value
  }

  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    guard case let .weighted(value) = widthTrait else {
      assertionFailure("try to get weight for non resizable block")
      return LayoutTrait.Weight.default
    }
    return value
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? ContainerBlock else {
      return false
    }

    return self == other
  }

  func laidOutHierarchy(for size: CGSize) -> (ContainerBlock, Layout) {
    let layout = ContainerBlockLayout(
      children: children,
      separator: separator,
      lineSeparator: lineSeparator,
      gaps: gaps,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode,
      axialAlignment: axialAlignment,
      crossAlignment: crossAlignment,
      size: size
    )
    let newChildren = zip(self.children, layout.blockFrames).map { child, frame -> Child in
      modified(child) { $0.content = $0.content.laidOut(for: frame.size) }
    }
    let block = try! modifying(children: newChildren)

    return (block, layout)
  }
}

private func makeGapsWithSeparators(
  gaps: [CGFloat],
  separator: ContainerBlock.Separator?,
  layoutMode: ContainerBlock.LayoutMode
) -> [CGFloat] {
  guard layoutMode == .noWrap, let separator = separator else {
    return gaps
  }
  return Array<CGFloat>.build {
    for (index, gap) in gaps.enumerated() {
      switch index {
      case 0:
        gap
        if separator.showAtStart {
          0
        }
      case gaps.count - 1:
        if separator.showAtEnd {
          0
        }
        gap
      default:
        if separator.showBetween {
          gap / 2
          gap / 2
        } else {
          gap
        }
      }
    }
  }
}

private func makeChildrenWithSeparators(
  children: [ContainerBlock.Child],
  separator: ContainerBlock.Separator?,
  layoutMode: ContainerBlock.LayoutMode
) -> [ContainerBlock.Child] {
  guard layoutMode == .noWrap, let separator = separator else {
    return children
  }
  return Array<ContainerBlock.Child>.build {
    if separator.showAtStart {
      separator.style
    }
    for (index, child) in children.enumerated() {
      if separator.showBetween, index > 0 {
        separator.style
      }
      child
    }
    if separator.showAtEnd {
      separator.style
    }
  }
}

extension ContainerBlock: Equatable {
  public static func ==(lhs: ContainerBlock, rhs: ContainerBlock) -> Bool {
    lhs.layoutDirection == rhs.layoutDirection &&
      lhs.widthTrait == rhs.widthTrait &&
      lhs.heightTrait == rhs.heightTrait &&
      lhs.axialAlignment == rhs.axialAlignment &&
      lhs.crossAlignment == rhs.crossAlignment &&
      lhs.gaps == rhs.gaps &&
      lhs.children == rhs.children &&
      lhs.contentAnimation == rhs.contentAnimation &&
      lhs.anchorPoint == rhs.anchorPoint &&
      lhs.childrenTransform == rhs.childrenTransform &&
      lhs.separator == rhs.separator &&
      lhs.lineSeparator == rhs.lineSeparator &&
      lhs.accessibilityElement == rhs.accessibilityElement
  }
}

extension ContainerBlock: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    children.flatMap { $0.content.getImageHolders() }
  }
}

extension ContainerBlock: ElementStateUpdating {
  public func updated(withStates states: BlocksState) throws -> ContainerBlock {
    let newChildren = try children.map {
      ContainerBlock.Child(
        content: try $0.content.updated(withStates: states),
        crossAlignment: $0.crossAlignment
      )
    }
    let childrenChanged = zip(children, newChildren).contains { $1.content !== $0.content }

    return childrenChanged ? try modifying(children: newChildren) : self
  }
}

extension ContainerBlock.Child {
  public static func ==(_ lhs: ContainerBlock.Child, _ rhs: ContainerBlock.Child) -> Bool {
    lhs.content == rhs.content && lhs.crossAlignment == rhs.crossAlignment
  }
}

extension ContainerBlock.CrossAlignment {
  public func offset(
    forAvailableSpace availableSpace: CGFloat,
    contentSize: CGFloat = 0
  ) -> CGFloat {
    switch self {
    case .leading, .baseline:
      return 0
    case .center:
      return ((availableSpace - contentSize) * 0.5).roundedToScreenScale
    case .trailing:
      return availableSpace - contentSize
    }
  }
}

extension Sequence where Element == ContainerBlock.Child {
  fileprivate func applyingContents<S: Sequence>(_ contents: S) -> [ContainerBlock.Child]
    where S.Element == Block {
    zip(self, contents).map { child, newContent in
      modified(child) { $0.content = newContent }
    }
  }
}

extension ContainerBlock.Error {
  public var errorMessage: NonEmptyString {
    switch self {
    case .noChildrenProvided: return "noChildrenProvided"
    case .childAndGapCountMismatch: return "childAndGapCountMismatch"
    case .inconsistentChildLayoutTraits: return "inconsistentChildLayoutTraits"
    }
  }

  public var userInfo: [String: String] {
    switch self {
    case let .inconsistentChildLayoutTraits(details):
      return ["details": details]
    case .noChildrenProvided, .childAndGapCountMismatch:
      return [:]
    }
  }
}

extension ContainerBlock.Child {
  fileprivate var crossAlignmentForCalculatingHeight: ContainerBlock.CrossAlignment {
    guard self.crossAlignment != .center,
          self.crossAlignment != .trailing else {
      return .leading
    }
    return self.crossAlignment
  }
}
