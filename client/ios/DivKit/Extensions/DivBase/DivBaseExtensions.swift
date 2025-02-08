import CoreGraphics
import LayoutKit
import VGSL

extension DivBase {
  func applyBaseProperties(
    to block: () throws -> Block,
    context: DivBlockModelingContext,
    actionsHolder: DivActionsHolder?,
    customAccessibilityParams: CustomAccessibilityParams = .default,
    applyPaddings: Bool = true,
    clipToBounds: Bool = true
  ) throws -> Block {
    let path = context.parentPath

    context.functionsStorage?.setIfNeeded(
      path: path,
      functions: functions ?? []
    )
    context.variablesStorage.initializeIfNeeded(
      path: path,
      variables: variables?.extractDivVariableValues() ?? [:]
    )
    context.triggersStorage?.setIfNeeded(
      path: path,
      triggers: variableTriggers ?? []
    )

    if let id = context.elementId ?? id {
      context.idToPath[path.cardId.path + id] = path
    }

    animators?.forEach { animator in
      context.animatorController?.initializeIfNeeded(
        path: path,
        id: animator.id,
        animator: Variable { animator.resolve(context) }
      )
    }

    let extensionHandlers = context.getExtensionHandlers(for: self)
    for extensionHandler in extensionHandlers {
      extensionHandler.accept(div: self, context: context)
    }

    let expressionResolver = context.expressionResolver
    if let forwardId = focus?.nextFocusIds?.resolveForward(expressionResolver),
       let currentId = self.id {
      context.accessibilityElementsStorage.put(id: currentId, nextId: forwardId)
    }

    let statePath = context.parentDivStatePath ?? DivData.rootPath
    let visibility = resolveVisibility(expressionResolver)
    if visibility == .gone {
      context.stateManager.setBlockVisibility(statePath: statePath, div: self, isVisible: false)
      if let visibilityParams = context.makeVisibilityParams(
        actions: makeDisappearActions(context: context),
        isVisible: false
      ) {
        return EmptyBlock.zeroSized.addingDecorations(
          visibilityParams: visibilityParams,
          isEmpty: true
        )
      }
      context.lastVisibleBoundsCache.onBecomeInvisible(path)
      return EmptyBlock.zeroSized
    }

    var block = try block()

    for extensionHandler in extensionHandlers {
      block = extensionHandler.applyBeforeBaseProperties(to: block, div: self, context: context)
    }

    block = block.addingEdgeInsets(
      applyPaddings ? paddings.resolve(context) : .zero,
      clipsToBounds: clipToBounds
    )

    let externalInsets = margins.resolve(context)
    if visibility == .invisible {
      context.lastVisibleBoundsCache.onBecomeInvisible(path)
      context.stateManager.setBlockVisibility(statePath: statePath, div: self, isVisible: false)
      block = applyExtensionHandlersAfterBaseProperties(
        to: block.addingEdgeInsets(externalInsets, clipsToBounds: clipToBounds),
        extensionHandlers: extensionHandlers,
        context: context
      )
      return block.addingDecorations(alpha: 0)
    }

    context.lastVisibleBoundsCache.onBecomeVisible(path)

    // We can't put container properties into single container.
    // Properties should be applied in specific order.
    // For example, shadow should be applied to block with border
    // and alpha should be applied to block with border and shadow.
    var visibilityActions = makeVisibilityActions(context: context)
    visibilityActions += makeDisappearActions(context: context)
    let visibilityParams = context.makeVisibilityParams(
      actions: visibilityActions,
      isVisible: true
    )

    let isFocused = context.blockStateStorage.isFocused(path: path)
    let border = getBorder(isFocused)

    let boundary: BoundaryTrait? = if !clipToBounds {
      .noClip
    } else if let border {
      .clipCorner(border.resolveCornerRadii(expressionResolver))
    } else {
      nil
    }

    let shadow = border?.resolveShadow(expressionResolver)

    let accessibilityElement = (accessibility ?? DivAccessibility()).resolve(
      expressionResolver,
      id: context.elementId ?? id,
      customParams: customAccessibilityParams
    )

    block = try applyBackground(
      getBackground(isFocused),
      to: block,
      context: context
    )
    .addingDecorations(
      boundary: boundary,
      border: border?.resolveBorder(expressionResolver),
      shadow: shadow,
      visibilityParams: visibilityParams,
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

    let clipToBounds = [nil, 0.0].contains(rotation) && shadow == nil
    block = applyTransitioningAnimations(to: block, context: context, statePath: statePath)
      .addActions(context: context, actionsHolder: actionsHolder, clipToBounds: clipToBounds)
      .addingEdgeInsets(externalInsets, clipsToBounds: false)
      .addingDecorations(
        boundary: clipToBounds ? nil : .noClip,
        alpha: CGFloat(resolveAlpha(expressionResolver))
      )

    if let layoutProvider {
      block = layoutProvider.apply(block: block, context: context)
    }

    if let reuseId = resolveReuseId(expressionResolver) {
      block = block.addingDecorations(
        reuseId: reuseId
      )
    }

    return applyExtensionHandlersAfterBaseProperties(
      to: block,
      extensionHandlers: extensionHandlers,
      context: context
    )
  }

  private func getBackground(_ isFocused: Bool) -> [DivBackground]? {
    guard isFocused else {
      return background
    }
    return focus?.background ?? background
  }

  private func getBorder(_ isFocused: Bool) -> DivBorder? {
    guard isFocused else {
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
        .makeContentAlignment(layoutDirection: context.layoutDirection)
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
    guard let id = context.elementId ?? id else {
      return block
    }

    let expressionResolver = context.expressionResolver
    let animationIn: [TransitioningAnimation]? = if isAppearing(
      statePath: statePath,
      id: id,
      context: context
    ) {
      transitionIn?.resolveAnimations(expressionResolver, type: .appearing)
    } else {
      nil
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
    guard let backgrounds else {
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
    for extensionHandler in extensionHandlers {
      block = extensionHandler.applyAfterBaseProperties(to: block, div: self, context: context)
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
    resolveContentWidthTrait(context, paddings: paddings.resolve(context))
  }

  func resolveContentWidthTrait(
    _ context: DivBlockModelingContext,
    paddings: EdgeInsets
  ) -> LayoutTrait {
    resolveWidthTrait(context).trim(paddings.horizontalInsets)
  }

  func getTransformedHeight(_ context: DivBlockModelingContext) -> DivSize {
    context.sizeModifier?.transformHeight(height) ?? height
  }

  func resolveHeightTrait(_ context: DivBlockModelingContext) -> LayoutTrait {
    getTransformedHeight(context).resolveLayoutTrait(context.expressionResolver)
  }

  func resolveContentHeightTrait(_ context: DivBlockModelingContext) -> LayoutTrait {
    resolveContentHeightTrait(context, paddings: paddings.resolve(context))
  }

  func resolveContentHeightTrait(
    _ context: DivBlockModelingContext,
    paddings: EdgeInsets
  ) -> LayoutTrait {
    resolveHeightTrait(context).trim(paddings.verticalInsets)
  }
}

extension LayoutTrait {
  fileprivate func trim(_ insets: SideInsets) -> LayoutTrait {
    switch self {
    case let .fixed(value):
      .fixed(value - insets.sum)
    case let .intrinsic(constrained, minSize, maxSize):
      .intrinsic(
        constrained: constrained,
        minSize: minSize - insets.sum,
        maxSize: maxSize - insets.sum
      )
    case .weighted:
      self
    }
  }
}

extension DivAlignmentHorizontal {
  fileprivate func makeContentAlignment(
    layoutDirection: UserInterfaceLayoutDirection
  ) -> Alignment {
    switch self {
    case .left:
      .leading
    case .right:
      .trailing
    case .start:
      layoutDirection == .leftToRight ? .leading : .trailing
    case .center:
      .center
    case .end:
      layoutDirection == .rightToLeft ? .leading : .trailing
    }
  }
}

extension DivBorder {
  fileprivate func resolveBorder(_ expressionResolver: ExpressionResolver) -> BlockBorder? {
    guard let stroke else {
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

    let cornerRadii = resolveCornerRadii(expressionResolver)

    guard let shadow else {
      return BlockShadow(
        cornerRadii: cornerRadii,
        blurRadius: 2,
        offset: .zero,
        opacity: BlockShadow.Defaults.opacity,
        color: BlockShadow.Defaults.color
      )
    }

    return shadow.resolve(expressionResolver, cornerRadii: cornerRadii)
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

extension DivBlockModelingContext {
  fileprivate func makeVisibilityParams(
    actions: [VisibilityAction],
    isVisible: Bool
  ) -> VisibilityParams? {
    if actions.isEmpty {
      return nil
    }
    let path = parentPath
    return VisibilityParams(
      actions: actions,
      isVisible: isVisible,
      lastVisibleArea: Property<Int>(
        getter: { lastVisibleBoundsCache.lastVisibleArea(for: path) },
        setter: { lastVisibleBoundsCache.updateLastVisibleArea(for: path, area: $0) }
      ),
      scheduler: scheduler
    )
  }
}
