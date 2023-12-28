import CoreGraphics

import BasePublic
import BaseUIPublic
import LayoutKit
import NetworkingPublic

extension DivBase {
  func applyBaseProperties(
    to block: () throws -> Block,
    context: DivBlockModelingContext,
    actionsHolder: DivActionsHolder?,
    options: BasePropertiesOptions = [],
    customA11yDescriptionProvider: (() -> String?)? = nil,
    clipToBounds: Bool = true
  ) throws -> Block {
    let expressionResolver = context.expressionResolver
    let statePath = context.parentDivStatePath ?? DivData.rootPath

    let visibility = resolveVisibility(expressionResolver)
    if visibility == .gone {
      context.lastVisibleBoundsCache.dropVisibleBounds(forMatchingPrefix: context.parentPath)
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
      : paddings.resolve(context)
    block = block.addingEdgeInsets(internalInsets, clipsToBounds: clipToBounds)

    let externalInsets = margins.resolve(context)
    if visibility == .invisible {
      context.lastVisibleBoundsCache.dropVisibleBounds(forMatchingPrefix: context.parentPath)
      context.stateManager.setBlockVisibility(statePath: statePath, div: self, isVisible: false)
      block = applyExtensionHandlersAfterBaseProperties(
        to: block.addingEdgeInsets(externalInsets, clipsToBounds: clipToBounds),
        extensionHandlers: extensionHandlers,
        context: context
      )
      return block.addingDecorations(alpha: 0)
    }

    // We can't put container properties into single container.
    // Properties should be applied in specific order.
    // For example, shadow should be applied to block with border
    // and alpha should be applied to block with border and shadow.
    var visibilityActions = makeVisibilityActions(context: context)
    visibilityActions += makeDisappearActions(context: context)
    let hasVisibilityActions = !visibilityActions.isEmpty

    let focusState: FocusViewState = context.blockStateStorage
      .getState(context.parentPath) ?? .default
    let border = getBorder(focusState)

    let boundary: BoundaryTrait?
    if !clipToBounds {
      boundary = .noClip
    } else if let border {
      boundary = .clipCorner(border.resolveCornerRadii(expressionResolver))
    } else {
      boundary = nil
    }

    let shadow = border?.resolveShadow(expressionResolver)
    
    let accessibilityElement = (accessibility ?? DivAccessibility())
      .resolve(
        expressionResolver,
        id: id,
        customDescriptionProvider: customA11yDescriptionProvider
      )

    block = try applyBackground(
      getBackground(focusState),
      to: block,
      context: context
    )
    .addingDecorations(
      boundary: boundary,
      border: border?.resolveBorder(expressionResolver),
      shadow: shadow,
      visibilityActions: hasVisibilityActions ? visibilityActions : nil,
      lastVisibleBounds: hasVisibilityActions ? Property<CGRect>(
        getter: { context.lastVisibleBoundsCache.lastVisibleBounds(for: context.parentPath) },
        setter: {
          context.lastVisibleBoundsCache.updateLastVisibleBounds(
            for: context.parentPath,
            bounds: $0
          )
        }
      ) : nil,
      scheduler: hasVisibilityActions ? context.scheduler : nil,
      tooltips: tooltips.makeTooltips(context: context),
      accessibilityElement: accessibilityElement
    )

    let rotation = transform?.resolveRotation(expressionResolver)

    if let transform {
      block = block.addingTransform(
        transform: rotation
          .flatMap { CGAffineTransform(rotationAngle: CGFloat($0) * .pi / 180) } ?? .identity,
        anchorPoint: transform.resolveAnchorPoint(expressionResolver)
      )
    }

    block = applyTransitioningAnimations(to: block, context: context, statePath: statePath)
      .addActions(context: context, actionsHolder: actionsHolder)
      .addingEdgeInsets(externalInsets, clipsToBounds: false)
      .addingDecorations(
        boundary: rotation != nil || shadow != nil ? .noClip : nil,
        alpha: CGFloat(resolveAlpha(expressionResolver))
      )

    return applyExtensionHandlersAfterBaseProperties(
      to: block,
      extensionHandlers: extensionHandlers,
      context: context
    )
  }

  private func getBackground(_ focusState: FocusViewState) -> [DivBackground]? {
    guard focusState.isFocused else {
      return background
    }
    return focus?.background ?? background
  }

  private func getBorder(_ focusState: FocusViewState) -> DivBorder? {
    guard focusState.isFocused else {
      return border
    }
    return focus?.border ?? border
  }

  func resolveAlignment(
    _ context: DivBlockModelingContext,
    defaultAlignment: BlockAlignment2D
  ) -> BlockAlignment2D {
    let expressionResolver = context.expressionResolver
    return BlockAlignment2D(
      horizontal: resolveAlignmentHorizontal(expressionResolver)?
        .makeContentAlignment(uiLayoutDirection: context.layoutDirection)
        ?? defaultAlignment.horizontal,
      vertical: resolveAlignmentVertical(expressionResolver)?.alignment
        ?? defaultAlignment.vertical
    )
  }

  private func makeVisibilityActions(
    context: DivBlockModelingContext
  ) -> [VisibilityAction] {
    (visibilityActions ?? visibilityAction.asArray())
      .compactMap { $0.makeVisibilityAction(context: context) }
  }

  private func makeDisappearActions(
    context: DivBlockModelingContext
  ) -> [VisibilityAction] {
    disappearActions?
      .compactMap { $0.makeDisappearAction(context: context) } ?? []
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
      animationIn = transitionIn?.resolveAnimations(expressionResolver, type: .appearing)
    } else {
      animationIn = nil
    }

    context.stateManager.setBlockVisibility(
      statePath: statePath,
      div: self,
      isVisible: true
    )

    let animationOut = transitionOut?.resolveAnimations(expressionResolver, type: .disappearing)
    let animationChange = transitionChange?.resolveTransition(expressionResolver)
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
    context: DivBlockModelingContext
  ) -> Block {
    guard let backgrounds = backgrounds else {
      return block
    }

    // optimization for the most common case: saves Array alloc/dealloc
    if backgrounds.count == 1 {
      guard let background = backgrounds[0].makeBlockBackground(context: context) else {
        return block
      }
      if case let .solidColor(color) = background {
        return block.addingDecorations(backgroundColor: color)
      }
      return BackgroundBlock(background: background, child: block)
    }

    let blockBackgrounds = backgrounds.compactMap {
      $0.makeBlockBackground(context: context)
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
  func getTransformedWidth(_ context: DivBlockModelingContext) -> DivSize {
    context.sizeModifier?.transformWidth(width) ?? width
  }

  func resolveWidthTrait(_ context: DivBlockModelingContext) -> LayoutTrait {
    getTransformedWidth(context).resolveLayoutTrait(context.expressionResolver)
  }

  func resolveContentWidthTrait(_ context: DivBlockModelingContext) -> LayoutTrait {
    resolveWidthTrait(context)
      .trim(paddings.resolve(context).horizontalInsets)
  }

  func getTransformedHeight(_ context: DivBlockModelingContext) -> DivSize {
    context.sizeModifier?.transformHeight(height) ?? height
  }

  func resolveHeightTrait(_ context: DivBlockModelingContext) -> LayoutTrait {
    getTransformedHeight(context).resolveLayoutTrait(context.expressionResolver)
  }

  func resolveContentHeightTrait(_ context: DivBlockModelingContext) -> LayoutTrait {
    resolveHeightTrait(context)
      .trim(paddings.resolve(context).verticalInsets)
  }
}

extension LayoutTrait {
  fileprivate func trim(_ insets: SideInsets) -> LayoutTrait {
    switch self {
    case let .fixed(value):
      return .fixed(value - insets.sum)
    case let .intrinsic(constrained, minSize, maxSize):
      return .intrinsic(
        constrained: constrained,
        minSize: minSize - insets.sum,
        maxSize: maxSize - insets.sum
      )
    case .weighted:
      return self
    }
  }
}

extension DivBorder {
  fileprivate func resolveBorder(_ expressionResolver: ExpressionResolver) -> BlockBorder? {
    guard let stroke = stroke else {
      return nil
    }
    return BlockBorder(
      color: stroke.resolveColor(expressionResolver) ?? .black,
      width: stroke.resolveUnit(expressionResolver)
        .makeScaledValue(stroke.resolveWidth(expressionResolver))
    )
  }

  fileprivate func resolveShadow(_ expressionResolver: ExpressionResolver) -> BlockShadow? {
    guard resolveHasShadow(expressionResolver) else {
      return nil
    }

    return BlockShadow(
      cornerRadii: resolveCornerRadii(expressionResolver),
      blurRadius: CGFloat(shadow?.resolveBlur(expressionResolver) ?? 2),
      offset: shadow?.offset.resolve(expressionResolver) ?? .zero,
      opacity: (shadow?.resolveAlpha(expressionResolver)).map(Float.init)
        ?? BlockShadow.Defaults.opacity,
      color: shadow?.resolveColor(expressionResolver) ?? BlockShadow.Defaults.color
    )
  }

  fileprivate func resolveCornerRadii(_ expressionResolver: ExpressionResolver) -> CornerRadii {
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
