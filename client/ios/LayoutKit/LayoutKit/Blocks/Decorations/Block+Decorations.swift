import CoreGraphics
import Foundation
import VGSL

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
    pressStartActions: NonEmptyArray<UserInterfaceAction>? = nil,
    pressEndActions: NonEmptyArray<UserInterfaceAction>? = nil,
    hoverStartActions: NonEmptyArray<UserInterfaceAction>? = nil,
    hoverEndActions: NonEmptyArray<UserInterfaceAction>? = nil,
    visibilityParams: VisibilityParams? = nil,
    tooltips: [BlockTooltip]? = nil,
    forceWrapping: Bool,
    accessibilityElement: AccessibilityElement? = nil,
    reuseId: String?,
    path: UIElementPath?,
    isEmpty: Bool?,
    isFocused: Bool?,
    captureFocusOnAction: Bool?
  ) -> Block {
    let hasAlpha = alpha != nil && alpha?.isApproximatelyEqualTo(1) != true
    let anythingToApplyExceptBoundary =
      (border != nil && border?.width.isApproximatelyEqualTo(0) != true)
        || (backgroundColor != nil && backgroundColor?.alpha.isApproximatelyEqualTo(0) != true)
        || hasAlpha
        || blurEffect != nil
        || actions != nil
        || doubleTapActions != nil
        || longTapActions != nil
        || visibilityParams != nil
        || tooltips?.isEmpty == false
        || forceWrapping
        || accessibilityElement != nil
        || reuseId != nil
        || isEmpty != nil
        || isFocused != nil
        || captureFocusOnAction != nil

    let decoratingSelf = self as? DecoratingBlock
    let selfBackgroundColor = decoratingSelf?.backgroundColor
    let shouldWrapAlpha = hasAlpha
      && selfBackgroundColor != nil
      && selfBackgroundColor != DecoratingBlock.defaultBackgroundColor
    if !forceWrapping, !shouldWrapAlpha, let block = decoratingSelf {
      guard (boundary != nil && boundary != block.boundary) || anythingToApplyExceptBoundary
      else {
        return self
      }

      return block.modifying(
        backgroundColor: backgroundColor,
        highlightedBackgroundColor: highlightedBackgroundColor ?? block.highlightedBackgroundColor,
        actions: actions ?? block.actions,
        actionAnimation: actionAnimation ?? block.actionAnimation,
        doubleTapActions: doubleTapActions ?? block.doubleTapActions,
        longTapActions: longTapActions ?? block.longTapActions,
        pressStartActions: pressStartActions ?? block.pressStartActions,
        pressEndActions: pressEndActions ?? block.pressEndActions,
        hoverStartActions: hoverStartActions ?? block.hoverStartActions,
        hoverEndActions: hoverEndActions ?? block.hoverEndActions,
        boundary: boundary ?? block.boundary,
        border: (border ?? block.border) as BlockBorder?,
        childAlpha: alpha.map { $0 * block.childAlpha },
        blurEffect: blurEffect ?? block.blurEffect,
        visibilityParams: visibilityParams ?? block.visibilityParams,
        tooltips: [tooltips, block.tooltips].compactMap { $0 }.flatMap { $0 },
        accessibilityElement: accessibilityElement,
        reuseId: reuseId,
        path: path,
        isFocused: isFocused,
        captureFocusOnAction: captureFocusOnAction ?? block.captureFocusOnAction
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
      pressStartActions: pressStartActions,
      pressEndActions: pressEndActions,
      hoverStartActions: hoverStartActions,
      hoverEndActions: hoverEndActions,
      boundary: boundary ?? DecoratingBlock.defaultBoundary,
      border: border,
      childAlpha: alpha ?? DecoratingBlock.defaultChildAlpha,
      blurEffect: blurEffect,
      visibilityParams: visibilityParams,
      tooltips: tooltips ?? [],
      accessibilityElement: accessibilityElement,
      reuseId: reuseId,
      path: path,
      isFocused: isFocused ?? false,
      captureFocusOnAction: captureFocusOnAction ?? DecoratingBlock.defaultCaptureFocusOnAction
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
    pressStartActions: NonEmptyArray<UserInterfaceAction>? = nil,
    pressEndActions: NonEmptyArray<UserInterfaceAction>? = nil,
    hoverStartActions: NonEmptyArray<UserInterfaceAction>? = nil,
    hoverEndActions: NonEmptyArray<UserInterfaceAction>? = nil,
    shadow: BlockShadow? = nil,
    visibilityParams: VisibilityParams? = nil,
    tooltips: [BlockTooltip]? = nil,
    forceWrapping: Bool = false,
    accessibilityElement: AccessibilityElement? = nil,
    reuseId: String? = nil,
    path: UIElementPath? = nil,
    isEmpty: Bool? = nil,
    isFocused: Bool? = nil,
    captureFocusOnAction: Bool? = nil
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
      pressStartActions: pressStartActions,
      pressEndActions: pressEndActions,
      hoverStartActions: hoverStartActions,
      hoverEndActions: hoverEndActions,
      visibilityParams: visibilityParams,
      tooltips: tooltips,
      forceWrapping: forceWrapping,
      accessibilityElement: accessibilityElement,
      reuseId: reuseId,
      path: path,
      isEmpty: isEmpty,
      isFocused: isFocused,
      captureFocusOnAction: captureFocusOnAction
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

    if let block = self as? DecoratingBlock, block.canSafelyExtendPaddings {
      return block.modifying(paddings: block.paddings + insets)
    }

    return DecoratingBlock(
      child: self,
      boundary: clipsToBounds == false ? .noClip : .clips,
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
      childrenTransform: transform,
      clipContent: false
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
      && accessibilityElement == nil
      && actions == nil
      && doubleTapActions == nil
      && longTapActions == nil
      && boundary.allCornersAreApproximatelyEqualToZero()
      && border == nil
  }
}

extension BoundaryTrait? {
  fileprivate var shouldApplyBoundary: Bool {
    switch self {
    case .noClip?, .none:
      false
    case .clipPath?, .clipCorner?:
      true
    }
  }
}
