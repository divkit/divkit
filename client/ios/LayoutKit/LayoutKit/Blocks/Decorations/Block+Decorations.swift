import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

extension Block {
  public var wrappingWidthTrait: LayoutTrait {
    isHorizontallyResizable ? .weighted(weightOfHorizontallyResizableBlock) : .intrinsic
  }

  public var wrappingHeightTrait: LayoutTrait {
    isVerticallyResizable ? .weighted(weightOfVerticallyResizableBlock) : .intrinsic
  }

  private func applyDecoratingBlockProperties(
    boundary: BoundaryTrait? = nil,
    border: BlockBorder? = nil,
    backgroundColor: Color? = nil,
    highlightedBackgroundColor: Color?? = nil,
    alpha: CGFloat? = nil,
    blurEffect: BlurEffect? = nil,
    actions: NonEmptyArray<UserInterfaceAction>? = nil,
    actionAnimation: ActionAnimation? = nil,
    doubleTapActions: NonEmptyArray<UserInterfaceAction>? = nil,
    longTapActions: LongTapActions? = nil,
    analyticsURL: URL? = nil,
    visibilityActions: [VisibilityAction]? = nil,
    tooltips: [BlockTooltip]? = nil,
    forceWrapping: Bool,
    accessibilityElement: AccessibilityElement? = nil
  ) -> Block {
    let anythingToApplyExceptBoundary =
      (border != nil && border?.width.isApproximatelyEqualTo(0) != true)
        || (backgroundColor != nil && backgroundColor?.alpha.isApproximatelyEqualTo(0) != true)
        || (alpha != nil && alpha?.isApproximatelyEqualTo(1) != true)
        || blurEffect != nil
        || actions != nil
        || doubleTapActions != nil
        || longTapActions != nil
        || analyticsURL != nil
        || visibilityActions != nil
        || tooltips?.isEmpty == false
        || forceWrapping
        || accessibilityElement != nil

    if !forceWrapping, let block = self as? DecoratingBlock {
      #if INTERNAL_BUILD
      let newBoundary = block.makeNewBoundary(fromModifying: boundary)
      #else
      let newBoundary = boundary
      #endif

      guard (newBoundary != nil && newBoundary != block.boundary) || anythingToApplyExceptBoundary
      else {
        return self
      }

      #if INTERNAL_BUILD
      assert(
        block.backgroundColor.alpha.isApproximatelyEqualTo(0) || backgroundColor == nil,
        "Replacing background color is suspicious, consider revising decorations order"
      )

      assert(
        block.analyticsURL == nil || analyticsURL == nil,
        "Applying analytics URL over another one doesn't make sense"
      )

      assert(
        border == nil || block.border == nil,
        "Applying border over another border doesn't make sense"
      )

      assert(
        block.visibilityActions.isEmpty || visibilityActions == nil,
        "Applying visibility actions over another one doesn't make sense"
      )
      #endif

      return block.modifying(
        backgroundColor: backgroundColor,
        highlightedBackgroundColor: highlightedBackgroundColor ?? block.highlightedBackgroundColor,
        actions: actions ?? block.actions,
        actionAnimation: actionAnimation ?? block.actionAnimation,
        doubleTapActions: doubleTapActions ?? block.doubleTapActions,
        longTapActions: longTapActions ?? block.longTapActions,
        analyticsURL: (analyticsURL ?? block.analyticsURL) as URL?,
        boundary: newBoundary ?? block.boundary,
        border: (border ?? block.border) as BlockBorder?,
        childAlpha: alpha.map { $0 * block.childAlpha },
        blurEffect: blurEffect ?? block.blurEffect,
        visibilityActions: visibilityActions ?? block.visibilityActions,
        tooltips: [tooltips, block.tooltips].compactMap { $0 }.flatMap { $0 },
        accessibilityElement: accessibilityElement
      )
    }

    guard boundary.shouldApplyBoundary || anythingToApplyExceptBoundary else {
      return self
    }

    return DecoratingBlock(
      child: self,
      backgroundColor: backgroundColor ?? DecoratingBlock.defaultBackgroundColor,
      highlightedBackgroundColor: highlightedBackgroundColor ?? DecoratingBlock
        .defaultHighlightedBackgroundColor,
      actions: actions,
      actionAnimation: actionAnimation,
      doubleTapActions: doubleTapActions,
      longTapActions: longTapActions,
      analyticsURL: analyticsURL,
      boundary: boundary ?? DecoratingBlock.defaultBoundary,
      border: border,
      childAlpha: alpha ?? DecoratingBlock.defaultChildAlpha,
      blurEffect: blurEffect,
      visibilityActions: visibilityActions ?? [],
      tooltips: tooltips ?? [],
      accessibilityElement: accessibilityElement
    )
  }

  public func addingDecorations(
    action: UserInterfaceAction?
  ) -> Block {
    addingDecorations(
      actions: action.asNonEmptyArray()
    )
  }

  public func addingDecorations(
    boundary: BoundaryTrait? = nil,
    border: BlockBorder? = nil,
    backgroundColor: Color? = nil,
    highlightedBackgroundColor: Color?? = nil,
    alpha: CGFloat? = nil,
    blurEffect: BlurEffect? = nil,
    actions: NonEmptyArray<UserInterfaceAction>? = nil,
    actionAnimation: ActionAnimation? = nil,
    doubleTapActions: NonEmptyArray<UserInterfaceAction>? = nil,
    longTapActions: LongTapActions? = nil,
    analyticsURL: URL? = nil,
    shadow: BlockShadow? = nil,
    visibilityActions: [VisibilityAction]? = nil,
    tooltips: [BlockTooltip]? = nil,
    forceWrapping: Bool = false,
    accessibilityElement: AccessibilityElement? = nil
  ) -> Block {
    let decoratedBlock = applyDecoratingBlockProperties(
      boundary: boundary,
      border: border,
      backgroundColor: backgroundColor,
      highlightedBackgroundColor: highlightedBackgroundColor,
      alpha: alpha,
      blurEffect: blurEffect,
      actions: actions,
      actionAnimation: actionAnimation,
      doubleTapActions: doubleTapActions,
      longTapActions: longTapActions,
      analyticsURL: analyticsURL,
      visibilityActions: visibilityActions,
      tooltips: tooltips,
      forceWrapping: forceWrapping,
      accessibilityElement: accessibilityElement
    )
    return decoratedBlock.shaded(with: shadow)
  }

  public func addingHorizontalGaps(
    left: CGFloat,
    right: CGFloat,
    clipsToBounds: Bool? = nil,
    forceWrapping: Bool = false
  ) -> Block {
    addingEdgeInsets(
      EdgeInsets(top: 0, left: left, bottom: 0, right: right),
      clipsToBounds: clipsToBounds,
      forceWrapping: forceWrapping
    )
  }

  public func addingHorizontalGaps(_ value: CGFloat) -> Block {
    addingHorizontalGaps(left: value, right: value)
  }

  public func withHorizontalInsets(_ insets: SideInsets) -> Block {
    addingHorizontalGaps(left: insets.leading, right: insets.trailing)
  }

  public func addingVerticalGaps(
    top: CGFloat,
    bottom: CGFloat,
    clipsToBounds: Bool? = nil,
    forceWrapping: Bool = false
  ) -> Block {
    addingEdgeInsets(
      EdgeInsets(top: top, left: 0, bottom: bottom, right: 0),
      clipsToBounds: clipsToBounds,
      forceWrapping: forceWrapping
    )
  }

  public func addingVerticalGaps(_ value: CGFloat) -> Block {
    addingVerticalGaps(top: value, bottom: value)
  }

  public func addingEdgeInsets(
    _ insets: EdgeInsets,
    clipsToBounds: Bool? = nil,
    forceWrapping: Bool = false
  ) -> Block {
    guard insets != .zero || forceWrapping else { return self }

    let newBoundary: BoundaryTrait? = clipsToBounds.map { $0 ? .clips : .noClip }
    if let block = self as? DecoratingBlock, block.canSafelyExtendPaddings {
      return block.modifying(paddings: block.paddings + insets)
    }

    return DecoratingBlock(
      child: self,
      boundary: newBoundary ?? .clips,
      paddings: insets
    )
  }

  public func addingTransform(transform: CGAffineTransform, anchorPoint: AnchorPoint) -> Block {
    guard transform != .identity else {
      return self
    }
    return try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: isHorizontallyResizable ? .resizable : .intrinsic,
      heightTrait: isVerticallyResizable ? .resizable : .intrinsic,
      children: [self],
      anchorPoint: anchorPoint,
      childrenTransform: transform
    )
  }

  public func addingEdgeGaps(_ value: CGFloat) -> Block {
    addingVerticalGaps(value)
      .addingHorizontalGaps(value)
  }

  public func rotated(by angle: CGFloat) -> Block {
    try! ContainerBlock(
      layoutDirection: .vertical,
      widthTrait: .intrinsic,
      children: [self],
      childrenTransform: .init(rotationAngle: angle)
    )
  }
}

extension DecoratingBlock {
  fileprivate var canSafelyExtendPaddings: Bool {
    backgroundColor.alpha.isApproximatelyEqualTo(0)
      && actions == nil
      && longTapActions == nil
      && analyticsURL == nil
      && boundary.allCornersAreApproximatelyEqualToZero()
      && border == nil
  }

  #if INTERNAL_BUILD
  fileprivate func makeNewBoundary(fromModifying newBoundary: BoundaryTrait?) -> BoundaryTrait? {
    guard let newBoundary = newBoundary else {
      return nil
    }
    switch (boundary, newBoundary) {
    case let (.clipCorner(corners), .clipCorner):
      assert(
        corners.allCornersAreApproximatelyEqualToZero(),
        "Applying one corner radius over another doesn't make sense"
      )
      return newBoundary
    case (.clipCorner, .noClip):
      assertionFailure("""
        Changing from .clip to .noClip is suspicious move,
        consider revising decorations order
      """)
      return boundary
    case (.noClip, .clipCorner):
      return newBoundary
    case (.noClip, .noClip):
      return nil // nothing to change
    case (.noClip, .clipPath):
      return newBoundary
    case (.clipPath, _), (.clipCorner, _):
      assertionFailure()
      return newBoundary
    }
  }
  #endif
}

extension Optional where Wrapped == BoundaryTrait {
  fileprivate var shouldApplyBoundary: Bool {
    switch self {
    case .noClip?, .none:
      return false
    case .clipPath?, .clipCorner?:
      return true
    }
  }
}
