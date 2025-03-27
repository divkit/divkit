import CoreGraphics
import LayoutKit
import VGSL

extension DivBase {
  func applyBaseProperties(
    to makeBlock: () throws -> Block,
    context: DivBlockModelingContext,
    actionsHolder: DivActionsHolder?,
    customAccessibilityParams: CustomAccessibilityParams = .default,
    applyPaddings: Bool = true,
    clipToBounds: Bool = true,
    isFocused: Bool = false
  ) throws -> Block {
    let identity = context.currentDivId
      ?? context.path.description
    setupContext(context: context, identity: identity)

    let extensionHandlers = setupExtensionsHandlers(context: context)
    let expressionResolver = context.expressionResolver
    let externalInsets = margins.resolve(context)
    let alpha = CGFloat(resolveAlpha(expressionResolver))
    let reuseId = resolveReuseId(expressionResolver)

    let visibility: DivBaseBlockBuilder.Visibility = try {
      let visibility = resolveVisibility(expressionResolver)

      switch visibility {
      case .gone:
        return .gone
      case .invisible:
        return .invisible(try makeBlock())
      case .visible:
        return .visible(try makeBlock(), alpha: alpha)
      }
    }()

    let blockBuilder = try DivBaseBlockBuilder(
      context: context,
      visibility: visibility,
      div: self,
      clipToBounds: clipToBounds,
      extensionHandlers: extensionHandlers,
      identity: identity,
      isFocused: isFocused || context.blockStateStorage.isFocused(path: context.path)
    )

    switch visibility {
    case .gone:
      return blockBuilder
        .build()
    case .invisible:
      return blockBuilder
        .applyExtensionHandlers(stage: .beforeBaseProperties)
        .applyEdgeInsets(
          applyPaddings: applyPaddings
        )
        .setupContextForBlockVisibility()
        .applyExternalEdgeInsets(
          externalInsets: externalInsets
        )
        .applyExtensionHandlers(stage: .afterBaseProperties)
        .applyBlockFinalDecorations()
        .build()
    case .visible:
      return try blockBuilder
        .applyExtensionHandlers(stage: .beforeBaseProperties)
        .applyEdgeInsets(
          applyPaddings: applyPaddings
        )
        .applyBackground()
        .applyVisibilityActionsAndDecorations(
          customAccessibilityParams: customAccessibilityParams
        )
        .applyTransformations()
        .applyTransitioningAnimations()
        .setupContextForBlockVisibility() // should be after apply animations
        .applyActions(
          actionsHolder: actionsHolder
        )
        .applyExternalEdgeInsets(
          externalInsets: externalInsets
        )
        .applyBlockFinalDecorations()
        .setBlockLayoutProvider(layoutProvider)
        .setBlockReuseId(reuseId)
        .applyExtensionHandlers(stage: .afterBaseProperties)
        .build()
    }
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

  func makeVisibilityActions(
    actionsType: VisibilityActionType,
    context: DivBlockModelingContext
  ) -> [VisibilityAction] {
    let divActions: [DivVisibilityActionBase]? = switch actionsType {
    case .appear:
      visibilityActions ?? visibilityAction.asArray()
    case .disappear:
      disappearActions
    }

    let blockActions: [VisibilityAction] = divActions?.compactMap {
      $0.makeVisibilityAction(
        context: context
      )
    } ?? []

    let logIds = blockActions.map(\.logId)
    let nonUniqueLogIds = logIds.nonUniqueElements
    if !nonUniqueLogIds.isEmpty {
      context.addWarning(
        message: "\(actionsType.rawValue) actions array contains non-unique log_id values: \(nonUniqueLogIds)"
      )
    }

    return blockActions
  }

  private func setupContext(
    context: DivBlockModelingContext,
    identity: String
  ) {
    let path = context.path

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

    context.idToPath[path.cardId.path + identity] = path

    animators?.forEach { animator in
      context.animatorController?.initializeIfNeeded(
        path: path,
        id: animator.id,
        animator: Variable { animator.resolve(context) }
      )
    }

    let expressionResolver = context.expressionResolver
    if let forwardId = focus?.nextFocusIds?.resolveForward(expressionResolver),
       let currentId = self.id {
      context.accessibilityElementsStorage.put(id: currentId, nextId: forwardId)
    }
  }

  private func setupExtensionsHandlers(
    context: DivBlockModelingContext
  ) -> [DivExtensionHandler] {
    let extensionHandlers = context.getExtensionHandlers(for: self)
    for extensionHandler in extensionHandlers {
      extensionHandler.accept(div: self, context: context)
    }

    return extensionHandlers
  }
}

enum ApplyExtensionHandlersStage {
  case beforeBaseProperties, afterBaseProperties
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

extension Block {
  func applyExtensionHandlers(
    order: ApplyExtensionHandlersStage,
    div: DivBase,
    extensionHandlers: [DivExtensionHandler],
    context: DivBlockModelingContext
  ) -> Block {
    var newBlock: Block = self
    for extensionHandler in extensionHandlers {
      switch order {
      case .beforeBaseProperties:
        newBlock = extensionHandler.applyBeforeBaseProperties(to: self, div: div, context: context)
      case .afterBaseProperties:
        newBlock = extensionHandler.applyAfterBaseProperties(to: self, div: div, context: context)
      }
    }
    return newBlock
  }

  func addingTransformations(
    transform: DivTransform?,
    rotation: Double?,
    expressionResolver: ExpressionResolver
  ) -> Block {
    guard let transform else { return self }

    return self
      .addingTransform(
        transform: rotation
          .flatMap { CGAffineTransform(rotationAngle: CGFloat($0) * .pi / 180) } ?? .identity,
        anchorPoint: transform.resolveAnchorPoint(expressionResolver)
      )
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
  func resolveBorder(_ expressionResolver: ExpressionResolver) -> BlockBorder? {
    guard let stroke else {
      return nil
    }
    return BlockBorder(
      style: stroke.resolveStyle(expressionResolver),
      color: stroke.resolveColor(expressionResolver) ?? .black,
      width: stroke.resolveUnit(expressionResolver)
        .makeScaledValue(stroke.resolveWidth(expressionResolver))
    )
  }

  func resolveShadow(_ expressionResolver: ExpressionResolver) -> BlockShadow? {
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

  func resolveCornerRadii(_ expressionResolver: ExpressionResolver) -> CornerRadii {
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

extension DivStroke {
  fileprivate func resolveStyle(_: ExpressionResolver) -> BlockBorder.Style {
    switch style {
    case .divStrokeStyleDashed: BlockBorder.Style.dashed
    case .divStrokeStyleSolid: BlockBorder.Style.solid
    }
  }
}

extension DivBlockModelingContext {
  func makeVisibilityParams(
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
