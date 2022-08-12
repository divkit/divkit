// Copyright 2018 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUI
import CommonCore

extension ContainerBlock {
  public func modifying(
    layoutDirection: LayoutDirection? = nil,
    widthTrait: LayoutTrait? = nil,
    heightTrait: LayoutTrait? = nil,
    axialAlignment: Alignment? = nil,
    gaps: [CGFloat]? = nil,
    children: [Child]? = nil,
    contentAnimation: BlockAnimation?? = nil,
    anchorPoint: AnchorPoint? = nil,
    childrenTransform: CGAffineTransform? = nil,
    clipContent: Bool? = nil,
    accessibilityElement: AccessibilityElement? = nil
  ) throws -> ContainerBlock {
    try ContainerBlock(
      layoutDirection: layoutDirection ?? self.layoutDirection,
      widthTrait: widthTrait ?? self.widthTrait,
      heightTrait: heightTrait ?? self.heightTrait,
      axialAlignment: axialAlignment ?? self.axialAlignment,
      gaps: gaps ?? self.gaps,
      children: children ?? self.children,
      contentAnimation: contentAnimation ?? self.contentAnimation,
      anchorPoint: anchorPoint ?? self.anchorPoint,
      childrenTransform: childrenTransform ?? self.childrenTransform,
      clipContent: clipContent ?? self.clipContent,
      accessibilityElement: accessibilityElement ?? self.accessibilityElement
    )
  }

  public convenience init(
    layoutDirection: LayoutDirection,
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    horizontalChildrenAlignment: Alignment = .leading,
    verticalChildrenAlignment: Alignment = .leading,
    gaps: [CGFloat]? = nil,
    children: [Block],
    contentAnimation: BlockAnimation? = nil,
    anchorPoint: AnchorPoint = ContainerBlock.defaultAnchorPoint,
    childrenTransform: CGAffineTransform = .identity,
    clipContent: Bool = true,
    accessibilityElement: AccessibilityElement? = nil
  ) throws {
    let axialAlignment: Alignment
    let crossAlignment: Alignment

    switch layoutDirection {
    case .horizontal:
      axialAlignment = horizontalChildrenAlignment
      crossAlignment = verticalChildrenAlignment
    case .vertical:
      axialAlignment = verticalChildrenAlignment
      crossAlignment = horizontalChildrenAlignment
    }

    let alignedChildren = children.map {
      Child(content: $0, crossAlignment: crossAlignment)
    }

    try self.init(
      layoutDirection: layoutDirection,
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      axialAlignment: axialAlignment,
      gaps: gaps,
      children: alignedChildren,
      contentAnimation: contentAnimation,
      anchorPoint: anchorPoint,
      childrenTransform: childrenTransform,
      clipContent: clipContent,
      accessibilityElement: accessibilityElement
    )
  }
}
