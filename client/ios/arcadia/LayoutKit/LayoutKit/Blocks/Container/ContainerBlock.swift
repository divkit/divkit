// Copyright 2016 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import BaseUI
import CommonCore

public final class ContainerBlock: BlockWithLayout {
  typealias Layout = ContainerBlockLayout
  public static let defaultAnchorPoint = AnchorPoint(
    x: .relative(value: 50),
    y: .relative(value: 50)
  )

  /// Determines direction in which child blocks are laid out in a container
  public enum LayoutDirection: CaseIterable {
    /// Child blocks are laid out horizontally one after another
    case horizontal
    /// Child blocks are laid out vertically one after another
    case vertical
  }

  public struct Child: Equatable {
    public var content: Block
    /// Alignment for dimension crossing container direction
    public let crossAlignment: Alignment

    public init(content: Block, crossAlignment: Alignment = .leading) {
      self.content = content
      self.crossAlignment = crossAlignment
    }
  }

  public enum Error: BlockError, Equatable {
    case noChildrenProvided
    case childAndGapCountMismatch
    case inconsistentChildLayoutTraits(details: String)
    case moreThanOneConstrainedChild
  }

  private struct CachedSizes {
    var intrinsicWidth: CGFloat?
    var nonResizableWidth: CGFloat?
    var intrinsicHeight: (width: CGFloat, height: CGFloat)?
  }

  public let layoutDirection: LayoutDirection
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let axialAlignment: Alignment
  public let gaps: [CGFloat]
  public let children: [Child]
  public let contentAnimation: BlockAnimation?
  public let anchorPoint: AnchorPoint
  public let childrenTransform: CGAffineTransform
  public let clipContent: Bool
  public let accessibilityElement: AccessibilityElement?

  private var cached = CachedSizes()

  public init(
    layoutDirection: LayoutDirection,
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    axialAlignment: Alignment = .leading,
    gaps: [CGFloat]? = nil,
    children: [Child],
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
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.axialAlignment = axialAlignment
    self.gaps = gaps
    self.children = children
    self.contentAnimation = contentAnimation
    self.anchorPoint = anchorPoint
    self.childrenTransform = childrenTransform
    self.clipContent = clipContent
    self.accessibilityElement = accessibilityElement

    try validateLayoutTraits()
  }

  private func validateLayoutTraits() throws {
    if widthTrait == .intrinsic {
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

    if heightTrait == .intrinsic {
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

    switch layoutDirection {
    case .horizontal:
      guard (children.filter { $0.content.isHorizontallyConstrained }.count <= 1) else {
        throw Error.moreThanOneConstrainedChild
      }
    case .vertical:
      guard (children.filter { $0.content.isVerticallyConstrained }.count <= 1) else {
        throw Error.moreThanOneConstrainedChild
      }
    }
  }

  public var isVerticallyResizable: Bool { heightTrait.isResizable }
  public var isHorizontallyResizable: Bool { widthTrait.isResizable }

  public var isVerticallyConstrained: Bool { heightTrait.isConstrained }
  public var isHorizontallyConstrained: Bool { widthTrait.isConstrained }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(width):
      return width
    case .intrinsic, .weighted:
      if let cached = cached.intrinsicWidth {
        return cached
      }

      let result: CGFloat
      switch layoutDirection {
      case .horizontal:
        result = (children.map { $0.content.intrinsicContentWidth } + gaps).reduce(0, +)
      case .vertical:
        result = children.map { $0.content.intrinsicContentWidth }.max()!
      }

      cached.intrinsicWidth = result
      return result
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(height):
      return height
    case .intrinsic, .weighted:
      if let cached = cached.intrinsicHeight,
         cached.width.isApproximatelyEqualTo(width) {
        return cached.height
      }

      let result: CGFloat
      switch layoutDirection {
      case .horizontal:
        let childrenContents = children.map { $0.content }
        let widthAvailableForResizableBlocks = width -
          widthsOfHorizontallyNonResizableBlocksIn(childrenContents).reduce(0, +) - gaps
          .reduce(0, +)
        let resizableBlockWeights = childrenContents.filter { $0.isHorizontallyResizable }
          .map { $0.weightOfHorizontallyResizableBlock.rawValue }
        let widthAvailablePerWeightUnit = max(0, widthAvailableForResizableBlocks) /
          resizableBlockWeights.reduce(0, +)
        let verticallyNonResizableBlocks = childrenContents.filter { !$0.isVerticallyResizable }
        let widthsOfVerticallyNonResizableBlocks = verticallyNonResizableBlocks.map {
          $0.isHorizontallyResizable
            ? floor($0.weightOfHorizontallyResizableBlock.rawValue * widthAvailablePerWeightUnit)
            : $0.widthOfHorizontallyNonResizableBlock
        }
        result = zip(verticallyNonResizableBlocks, widthsOfVerticallyNonResizableBlocks).map {
          $0.0.heightOfVerticallyNonResizableBlock(forWidth: $0.1)
        }.max() ?? 0
      case .vertical:
        let childrenHeights = children.map { $0.content }.intrinsicHeights(forWidth: width)
        result = (childrenHeights + gaps).reduce(0, +)
      }

      cached.intrinsicHeight = (width: width, height: result)
      return result
    }
  }

  public var widthOfHorizontallyNonResizableBlock: CGFloat {
    if case let .fixed(value) = widthTrait {
      return value
    }

    guard widthTrait == .intrinsic else {
      fatalError()
    }

    if let cached = cached.nonResizableWidth {
      return cached
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

    cached.nonResizableWidth = result
    return result
  }

  public func heightOfVerticallyNonResizableBlock(forWidth width: CGFloat) -> CGFloat {
    if case let .fixed(value) = heightTrait {
      return value
    }

    guard heightTrait == .intrinsic else {
      fatalError()
    }

    return intrinsicContentHeight(forWidth: width)
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    guard case let .weighted(value) = heightTrait else { fatalError() }
    return value
  }

  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    guard case let .weighted(value) = widthTrait else { fatalError() }
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
      gaps: gaps,
      layoutDirection: layoutDirection,
      axialAlignment: axialAlignment,
      size: size
    )
    let newChildren = zip(self.children, layout.blockFrames).map { child, frame -> Child in
      modified(child) { $0.content = $0.content.laidOut(for: frame.size) }
    }
    let block = try! modifying(children: newChildren)

    return (block, layout)
  }
}

extension ContainerBlock: Equatable {
  public static func ==(lhs: ContainerBlock, rhs: ContainerBlock) -> Bool {
    lhs.layoutDirection == rhs.layoutDirection &&
      lhs.widthTrait == rhs.widthTrait &&
      lhs.heightTrait == rhs.heightTrait &&
      lhs.axialAlignment == rhs.axialAlignment &&
      lhs.gaps == rhs.gaps &&
      lhs.children == rhs.children &&
      lhs.contentAnimation == rhs.contentAnimation &&
      lhs.anchorPoint == rhs.anchorPoint &&
      lhs.childrenTransform == rhs.childrenTransform &&
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
    case .moreThanOneConstrainedChild: return "moreThanOneConstrainedChild"
    case .inconsistentChildLayoutTraits: return "inconsistentChildLayoutTraits"
    }
  }

  public var userInfo: [String: String] {
    switch self {
    case let .inconsistentChildLayoutTraits(details):
      return ["details": details]
    case .noChildrenProvided, .childAndGapCountMismatch, .moreThanOneConstrainedChild:
      return [:]
    }
  }
}
