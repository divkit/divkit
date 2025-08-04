import CoreGraphics
import Foundation
import VGSL

final class DecoratingBlock: WrapperBlock {
  static let defaultBoundary: BoundaryTrait = .clips
  static let defaultBackgroundColor: Color = .clear
  static let defaultChildAlpha: CGFloat = 1
  static let defaultHighlightedBackgroundColor: Color? = nil
  static let defaultCaptureFocusOnAction: Bool = true

  let child: Block
  let backgroundColor: Color
  let highlightedBackgroundColor: Color?
  let actions: NonEmptyArray<UserInterfaceAction>?
  let actionAnimation: ActionAnimation?
  let doubleTapActions: NonEmptyArray<UserInterfaceAction>?
  let longTapActions: LongTapActions?
  let pressStartActions: NonEmptyArray<UserInterfaceAction>?
  let pressEndActions: NonEmptyArray<UserInterfaceAction>?
  let hoverStartActions: NonEmptyArray<UserInterfaceAction>?
  let hoverEndActions: NonEmptyArray<UserInterfaceAction>?
  let boundary: BoundaryTrait
  let border: BlockBorder?
  let childAlpha: CGFloat
  let blurEffect: BlurEffect?
  let paddings: EdgeInsets
  let visibilityParams: VisibilityParams?
  let tooltips: [BlockTooltip]
  let accessibilityElement: AccessibilityElement?
  let reuseId: String
  let path: UIElementPath?
  let isEmpty: Bool
  let isFocused: Bool
  let captureFocusOnAction: Bool

  var intrinsicContentWidth: CGFloat {
    child.intrinsicContentWidth.roundedToScreenScale + paddings.horizontal.sum
  }

  var widthOfHorizontallyNonResizableBlock: CGFloat {
    intrinsicContentWidth
  }

  init(
    child: Block,
    backgroundColor: Color = DecoratingBlock.defaultBackgroundColor,
    highlightedBackgroundColor: Color? = DecoratingBlock.defaultHighlightedBackgroundColor,
    actions: NonEmptyArray<UserInterfaceAction>? = nil,
    actionAnimation: ActionAnimation? = nil,
    doubleTapActions: NonEmptyArray<UserInterfaceAction>? = nil,
    longTapActions: LongTapActions? = nil,
    pressStartActions: NonEmptyArray<UserInterfaceAction>? = nil,
    pressEndActions: NonEmptyArray<UserInterfaceAction>? = nil,
    hoverStartActions: NonEmptyArray<UserInterfaceAction>? = nil,
    hoverEndActions: NonEmptyArray<UserInterfaceAction>? = nil,
    boundary: BoundaryTrait = DecoratingBlock.defaultBoundary,
    border: BlockBorder? = nil,
    childAlpha: CGFloat = defaultChildAlpha,
    blurEffect: BlurEffect? = nil,
    paddings: EdgeInsets = .zero,
    visibilityParams: VisibilityParams? = nil,
    tooltips: [BlockTooltip] = [],
    accessibilityElement: AccessibilityElement? = nil,
    reuseId: String? = nil,
    path: UIElementPath? = nil,
    isEmpty: Bool = false,
    isFocused: Bool = false,
    captureFocusOnAction: Bool = DecoratingBlock.defaultCaptureFocusOnAction
  ) {
    self.child = child
    self.backgroundColor = backgroundColor
    self.highlightedBackgroundColor = highlightedBackgroundColor
    self.actions = actions
    self.actionAnimation = actionAnimation
    self.doubleTapActions = doubleTapActions
    self.longTapActions = longTapActions
    self.pressStartActions = pressStartActions
    self.pressEndActions = pressEndActions
    self.hoverStartActions = hoverStartActions
    self.hoverEndActions = hoverEndActions
    self.boundary = boundary
    self.border = border
    self.childAlpha = childAlpha
    self.blurEffect = blurEffect
    self.paddings = paddings
    self.visibilityParams = visibilityParams
    self.tooltips = tooltips
    self.accessibilityElement = accessibilityElement
    self.reuseId = reuseId ?? DecoratingBlock.defaultReuseId
    self.path = path
    self.isEmpty = isEmpty
    self.isFocused = isFocused
    self.captureFocusOnAction = captureFocusOnAction
  }

  public func ascent(forWidth width: CGFloat) -> CGFloat? {
    guard let childAscent = child.ascent(forWidth: width) else {
      return nil
    }
    return childAscent + paddings.vertical.leading
  }

  func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    let childWidth = max(0, width - paddings.horizontal.sum)
    return child.intrinsicContentHeight(forWidth: childWidth).roundedToScreenScale
      + paddings.vertical.sum
  }

  func heightOfVerticallyNonResizableBlock(forWidth width: CGFloat) -> CGFloat {
    intrinsicContentHeight(forWidth: width)
  }

  func laidOut(for width: CGFloat) -> Block {
    let childWidth = width - paddings.horizontal.sum
    return updatingChild(child.laidOut(for: childWidth))
  }

  func laidOut(for size: CGSize) -> Block {
    let childSize = size.inset(by: paddings)
    return updatingChild(child.laidOut(for: childSize))
  }

  func equals(_ other: Block) -> Bool {
    guard let other = other as? DecoratingBlock else { return false }
    return backgroundColor == other.backgroundColor
      && actions == other.actions
      && longTapActions == other.longTapActions
      && doubleTapActions == other.doubleTapActions
      && pressStartActions == other.pressStartActions
      && pressEndActions == other.pressEndActions
      && hoverStartActions == other.hoverStartActions
      && hoverEndActions == other.hoverEndActions
      && actionAnimation == other.actionAnimation
      && boundary == other.boundary
      && border == other.border
      && childAlpha == other.childAlpha
      && blurEffect == other.blurEffect
      && paddings == other.paddings
      && child.equals(other.child)
      && visibilityParams == other.visibilityParams
      && tooltips == other.tooltips
      && accessibilityElement == other.accessibilityElement
      && reuseId == other.reuseId
      && isEmpty == other.isEmpty
      && path == other.path
      && isFocused == other.isFocused
  }

  func makeCopy(wrapping child: Block) -> DecoratingBlock {
    modifying(child: child)
  }

  private func updatingChild(_ newChild: Block) -> DecoratingBlock {
    guard newChild !== child else { return self }
    return modifying(child: newChild)
  }
}

extension DecoratingBlock {
  func modifying(
    child: Block? = nil,
    backgroundColor: Color? = nil,
    highlightedBackgroundColor: Color?? = nil,
    actions: NonEmptyArray<UserInterfaceAction>? = nil,
    actionAnimation: ActionAnimation? = nil,
    doubleTapActions: NonEmptyArray<UserInterfaceAction>? = nil,
    longTapActions: LongTapActions? = nil,
    pressStartActions: NonEmptyArray<UserInterfaceAction>? = nil,
    pressEndActions: NonEmptyArray<UserInterfaceAction>? = nil,
    hoverStartActions: NonEmptyArray<UserInterfaceAction>? = nil,
    hoverEndActions: NonEmptyArray<UserInterfaceAction>? = nil,
    boundary: BoundaryTrait? = nil,
    border: BlockBorder?? = nil,
    childAlpha: CGFloat? = nil,
    blurEffect: BlurEffect? = nil,
    paddings: EdgeInsets? = nil,
    visibilityParams: VisibilityParams? = nil,
    tooltips: [BlockTooltip]? = nil,
    accessibilityElement: AccessibilityElement? = nil,
    reuseId: String? = nil,
    path: UIElementPath? = nil,
    isEmpty: Bool? = nil,
    isFocused: Bool? = nil,
    captureFocusOnAction: Bool? = nil
  ) -> DecoratingBlock {
    DecoratingBlock(
      child: child ?? self.child,
      backgroundColor: backgroundColor ?? self.backgroundColor,
      highlightedBackgroundColor: highlightedBackgroundColor ?? self.highlightedBackgroundColor,
      actions: actions ?? self.actions,
      actionAnimation: actionAnimation ?? self.actionAnimation,
      doubleTapActions: doubleTapActions ?? self.doubleTapActions,
      longTapActions: longTapActions ?? self.longTapActions,
      pressStartActions: pressStartActions ?? self.pressStartActions,
      pressEndActions: pressEndActions ?? self.pressEndActions,
      hoverStartActions: hoverStartActions ?? self.hoverStartActions,
      hoverEndActions: hoverEndActions ?? self.hoverEndActions,
      boundary: boundary ?? self.boundary,
      border: border ?? self.border,
      childAlpha: childAlpha ?? self.childAlpha,
      blurEffect: blurEffect ?? self.blurEffect,
      paddings: paddings ?? self.paddings,
      visibilityParams: visibilityParams ?? self.visibilityParams,
      tooltips: tooltips ?? self.tooltips,
      accessibilityElement: accessibilityElement ?? self.accessibilityElement,
      reuseId: reuseId ?? self.reuseId,
      path: path ?? self.path,
      isEmpty: isEmpty ?? self.isEmpty,
      isFocused: isFocused ?? self.isFocused,
      captureFocusOnAction: captureFocusOnAction ?? self.captureFocusOnAction
    )
  }
}

extension DecoratingBlock: PaddingProvidingBlock {}

extension DecoratingBlock {
  var childPath: UIElementPath? {
    switch child {
    case let child as DecoratingBlock:
      child.childPath

    case let child as PathIdentifiable:
      child.path

    default:
      nil
    }
  }
}
