import CoreGraphics

import BasePublic
import BaseUIPublic
import LayoutKit
import NetworkingPublic

extension DivBase {
  func applyBaseProperties(
    to block: () throws -> Block,
    context: DivBlockModelingContext,
    actions: NonEmptyArray<UserInterfaceAction>?,
    actionAnimation: ActionAnimation?,
    doubleTapActions: NonEmptyArray<UserInterfaceAction>?,
    longTapActions: NonEmptyArray<UserInterfaceAction>?,
    options: BasePropertiesOptions = [],
    customA11yElement: AccessibilityElement? = nil
  ) throws -> Block {
    let expressionResolver = context.expressionResolver
    let statePath = context.parentDivStatePath ?? DivData.rootPath

    let visibility = resolveVisibility(expressionResolver)
    if visibility == .gone {
      context.stateManager.setBlockVisibility(statePath: statePath, div: self, isVisible: false)
      return EmptyBlock.zeroSized
    }

    var block = try block()

    let extensionHandlers = context.getExtensionHandlers(for: self)
    extensionHandlers.forEach {
      block = $0.applyBeforeBaseProperties(to: block, div: self, context: context)
    }

    let internalInsets = options.contains(.noPaddings)
      ? .zero
      : paddings.makeEdgeInsets(with: expressionResolver)
    block = block.addingEdgeInsets(internalInsets)

    let externalInsets = margins.makeEdgeInsets(with: expressionResolver)
    if visibility == .invisible {
      context.stateManager.setBlockVisibility(statePath: statePath, div: self, isVisible: false)
      block = applyExtensionHandlersAfterBaseProperties(
        to: block.addingEdgeInsets(externalInsets),
        extensionHandlers: extensionHandlers,
        context: context
      )
      return block.addingDecorations(alpha: 0)
    }

    // We can't put container properties into single container.
    // Properties should be applied in specific order.
    // For example, shadow should be applied to block with border
    // and alpha should be applied to block with border and shadow.
    let visibilityActions = makeVisibilityActions(context: context)

    let focusState: FocusViewState = context.blockStateStorage
      .getState(context.parentPath) ?? .default
    let background = getBackground(focusState)
    let border = getBorder(focusState)

    block = applyBackground(
      background,
      to: block,
      imageHolderFactory: context.imageHolderFactory,
      expressionResolver: expressionResolver,
      metalImageRenderingEnabled: context.flagsInfo.metalImageRenderingEnabled
    )
    .addingDecorations(
      boundary: border.makeBoundaryTrait(with: expressionResolver),
      border: border.makeBlockBorder(with: expressionResolver),
      shadow: border.makeBlockShadow(with: expressionResolver),
      visibilityActions: visibilityActions.isEmpty ? nil : visibilityActions,
      tooltips: try makeTooltips(context: context)
    )

    block = applyTransitioningAnimations(to: block, context: context, statePath: statePath)
      .addingEdgeInsets(externalInsets, clipsToBounds: false)
      .addingDecorations(
        alpha: CGFloat(resolveAlpha(expressionResolver)),
        actions: actions,
        actionAnimation: actionAnimation,
        doubleTapActions: doubleTapActions,
        longTapActions: longTapActions.map(LongTapActions.actions),
        accessibilityElement: customA11yElement ?? makeAccessibilityElement(
          accessibility,
          expressionResolver: expressionResolver,
          childrenA11yDescription: context.childrenA11yDescription
        )
      )

    let anchorPoint = transform.makeAnchorPoint(expressionResolver: expressionResolver)

    return applyExtensionHandlersAfterBaseProperties(
      to: block,
      extensionHandlers: extensionHandlers,
      context: context
    )
    .addingTransform(
      transform: transform.resolveRotation(expressionResolver)
        .flatMap { CGAffineTransform(rotationAngle: CGFloat($0) * .pi / 180) } ?? .identity,
      anchorPoint: anchorPoint
    )
  }

  private func getBackground(_ focusState: FocusViewState) -> [DivBackground]? {
    guard focusState.isFocused else {
      return background
    }
    let focusedBackground = focus?.background ?? background
    return focusedBackground
  }

  private func getBorder(_ focusState: FocusViewState) -> DivBorder {
    guard focusState.isFocused else {
      return border
    }
    let focusedBorder = focus?.border ?? border
    return focusedBorder
  }

  func alignment2D(
    withDefault defaultAlignment: BlockAlignment2D,
    expressionResolver: ExpressionResolver
  ) -> BlockAlignment2D {
    BlockAlignment2D(
      horizontal: resolveAlignmentHorizontal(expressionResolver)?.alignment
        ?? defaultAlignment.horizontal,
      vertical: resolveAlignmentVertical(expressionResolver)?.alignment
        ?? defaultAlignment.vertical
    )
  }

  private func makeVisibilityActions(
    context: DivBlockModelingContext
  ) -> [VisibilityAction] {
    (visibilityActions ?? visibilityAction.asArray())
      .enumerated()
      .compactMap { index, action in
        action.makeVisibilityAction(context: context, index: index)
      }
  }

  private func makeAccessibilityElement(
    _ accessibility: DivAccessibility?,
    expressionResolver: ExpressionResolver,
    childrenA11yDescription: String?
  ) -> AccessibilityElement? {
    if let accessibility = accessibility {
      return accessibility.accessibilityElement(
        divId: id,
        expressionResolver: expressionResolver,
        childrenA11yDescription: childrenA11yDescription
      )
    }

    return nil
  }

  private func makeTooltips(context: DivBlockModelingContext) throws -> [BlockTooltip] {
    try tooltips?.iterativeFlatMap { div, index in
      let tooltipContext = modified(context) {
        $0.parentPath = $0.parentPath + "tooltip" + index
      }
      return try div.makeTooltip(context: tooltipContext)
    } ?? []
  }

  private func applyTransitioningAnimations(
    to block: Block,
    context: DivBlockModelingContext,
    statePath: DivStatePath
  ) -> Block {
    guard let id = self.id else {
      return block
    }

    let expressionResolver = context.expressionResolver
    let animationIn: [TransitioningAnimation]?
    if isAppearing(statePath: statePath, id: id, context: context) {
      animationIn = transitionIn?
        .makeTransitioningAnimations(for: .appearing, with: expressionResolver)
    } else {
      animationIn = nil
    }

    context.stateManager.setBlockVisibility(
      statePath: statePath,
      div: self,
      isVisible: true
    )

    let animationOut = transitionOut?
      .makeTransitioningAnimations(for: .disappearing, with: expressionResolver)
    let animationChange = transitionChange?
      .makeChangeBoundsTransition(with: expressionResolver)
    if animationIn != nil || animationOut != nil || animationChange != nil {
      return DetachableAnimationBlock(
        child: block,
        id: id,
        animationIn: animationIn,
        animationOut: animationOut,
        animationChange: animationChange
      )
    }

    return block
  }

  private func isAppearing(
    statePath: DivStatePath,
    id: String,
    context: DivBlockModelingContext
  ) -> Bool {
    let stateManager = context.stateManager
    if stateManager.shouldBlockAppearWithTransition(path: statePath + id) {
      return true
    }

    return stateManager.isBlockAdded(id, stateBlockPath: statePath.stateBlockPath)
  }

  private func applyBackground(
    _ backgrounds: [DivBackground]?,
    to block: Block,
    imageHolderFactory: ImageHolderFactory,
    expressionResolver: ExpressionResolver,
    metalImageRenderingEnabled: Bool
  ) -> Block {
    guard let backgrounds = backgrounds else {
      return block
    }

    // optimization for the most common case: saves Array alloc/dealloc
    if backgrounds.count == 1 {
      guard let background = backgrounds[0].makeBlockBackground(
        with: imageHolderFactory,
        expressionResolver: expressionResolver,
        metalImageRenderingEnabled: metalImageRenderingEnabled
      ) else {
        return block
      }
      if case let .solidColor(color) = background {
        return block.addingDecorations(backgroundColor: color)
      }
      return BackgroundBlock(background: background, child: block)
    }

    let blockBackgrounds = backgrounds.compactMap {
      $0.makeBlockBackground(
        with: imageHolderFactory,
        expressionResolver: expressionResolver,
        metalImageRenderingEnabled: metalImageRenderingEnabled
      )
    }
    guard let background = blockBackgrounds.composite() else {
      return block
    }

    return BackgroundBlock(
      background: background,
      child: block
    )
  }

  private func applyExtensionHandlersAfterBaseProperties(
    to block: Block,
    extensionHandlers: [DivExtensionHandler],
    context: DivBlockModelingContext
  ) -> Block {
    var block = block
    extensionHandlers.forEach {
      block = $0.applyAfterBaseProperties(to: block, div: self, context: context)
    }
    return block
  }
}

extension DivBase {
  func makeContentWidthTrait(with context: DivBlockModelingContext) -> LayoutTrait {
    let overridenWidth = context.override(width: width)
    return overridenWidth.makeLayoutTrait(with: context.expressionResolver).contentTrait(
      consideringInsets: paddings.makeEdgeInsets(with: context.expressionResolver).horizontalInsets
    )
  }

  func makeContentHeightTrait(with context: DivBlockModelingContext) -> LayoutTrait {
    let overridenHeight = context.override(height: height)
    return overridenHeight.makeLayoutTrait(with: context.expressionResolver).contentTrait(
      consideringInsets: paddings.makeEdgeInsets(with: context.expressionResolver).verticalInsets
    )
  }
}

extension LayoutTrait {
  fileprivate func contentTrait(consideringInsets insets: SideInsets) -> LayoutTrait {
    switch self {
    case let .fixed(value):
      return .fixed(value - insets.sum)
    case .intrinsic,
         .weighted:
      return self
    }
  }
}

extension DivBorder {
  fileprivate func makeBlockBorder(with expressionResolver: ExpressionResolver) -> BlockBorder? {
    guard let stroke = stroke else { return nil }
    return BlockBorder(
      color: stroke.resolveColor(expressionResolver) ?? .black,
      width: stroke.resolveUnit(expressionResolver)
        .makeScaledValue(stroke.resolveWidth(expressionResolver))
    )
  }

  fileprivate func makeBlockShadow(with expressionResolver: ExpressionResolver) -> BlockShadow? {
    guard resolveHasShadow(expressionResolver) else { return nil }

    return BlockShadow(
      cornerRadii: makeCornerRadii(with: expressionResolver),
      blurRadius: CGFloat(shadow?.resolveBlur(expressionResolver) ?? 2),
      offset: shadow?.offset.cast(with: expressionResolver) ?? .zero,
      opacity: (shadow?.resolveAlpha(expressionResolver)).map(Float.init)
        ?? BlockShadow.Defaults.opacity,
      color: shadow?.resolveColor(expressionResolver) ?? BlockShadow.Defaults.color
    )
  }

  fileprivate func makeBoundaryTrait(with expressionResolver: ExpressionResolver) -> BoundaryTrait {
    .clipCorner(makeCornerRadii(with: expressionResolver))
  }

  private func makeCornerRadii(with expressionResolver: ExpressionResolver) -> CornerRadii {
    let cornerRadius = resolveCornerRadius(expressionResolver)
    let topLeft = cornersRadius?.resolveTopLeft(expressionResolver)
      ?? cornerRadius ?? 0
    let topRight = cornersRadius?.resolveTopRight(expressionResolver)
      ?? cornerRadius ?? 0
    let bottomLeft = cornersRadius?.resolveBottomLeft(expressionResolver)
      ?? cornerRadius ?? 0
    let bottomRight = cornersRadius?.resolveBottomRight(expressionResolver)
      ?? cornerRadius ?? 0

    return CornerRadii(
      topLeft: CGFloat(topLeft),
      topRight: CGFloat(topRight),
      bottomLeft: CGFloat(bottomLeft),
      bottomRight: CGFloat(bottomRight)
    )
  }
}

extension DivTooltip {
  fileprivate func makeTooltip(context: DivBlockModelingContext) throws
    -> BlockTooltip? {
    let expressionResolver = context.expressionResolver
    guard let position = resolvePosition(expressionResolver)?.cast() else {
      return nil
    }
    let block = try div.value.makeBlock(context: context)
    return BlockTooltip(
      id: id,
      block: block,
      duration: Duration(milliseconds: resolveDuration(expressionResolver)),
      offset: offset?.cast(with: expressionResolver) ?? .zero,
      position: position
    )
  }
}

extension DivTooltip.Position {
  fileprivate func cast() -> BlockTooltip.Position {
    switch self {
    case .left: return .left
    case .topLeft: return .topLeft
    case .top: return .top
    case .topRight: return .topRight
    case .right: return .right
    case .bottomRight: return .bottomRight
    case .bottom: return .bottom
    case .bottomLeft: return .bottomLeft
    }
  }
}

extension DivPoint {
  fileprivate func cast(with expressionResolver: ExpressionResolver) -> CGPoint {
    CGPoint(
      x: x.resolveUnit(expressionResolver).makeScaledValue(x.resolveValue(expressionResolver) ?? 0),
      y: y.resolveUnit(expressionResolver).makeScaledValue(y.resolveValue(expressionResolver) ?? 0)
    )
  }
}
