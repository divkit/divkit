import CoreGraphics
import LayoutKit

final class DivBaseBlockBuilder {
  enum Visibility {
    case visible(Block, alpha: CGFloat)
    case invisible(Block)
    case gone

    var isVisible: Bool {
      if case .visible = self { return true }
      return false
    }
  }

  private let context: DivBlockModelingContext
  private let visibility: Visibility
  private let div: DivBase
  private let clipToBounds: Bool
  private let extensionHandlers: [DivExtensionHandler]
  private let identity: String
  private let isFocused: Bool

  private lazy var rotation = self.div.transform?.resolveRotation(self.expressionResolver)

  private lazy var border = isFocused ? div.focus?.border ?? div.border : div.border

  private lazy var boundary: BoundaryTrait? = if !clipToBounds {
    .noClip
  } else if let border {
    .clipCorner(border.resolveCornerRadii(expressionResolver))
  } else {
    nil
  }

  private lazy var shadow: BlockShadow? = border?.resolveShadow(expressionResolver)

  private lazy var finalClipToBounds: Bool = [nil, 0.0].contains(rotation) && shadow == nil

  private lazy var block: Block = switch visibility {
  case let .visible(block, _), let .invisible(block):
    block
  case .gone:
    makeGoneBlock(div: div)
  }

  private var expressionResolver: ExpressionResolver {
    context.expressionResolver
  }

  private var path: UIElementPath {
    context.path
  }

  private var statePath: DivStatePath {
    context.parentDivStatePath ?? DivData.rootPath
  }

  init(
    context: DivBlockModelingContext,
    visibility: Visibility,
    div: DivBase,
    clipToBounds: Bool,
    extensionHandlers: [DivExtensionHandler],
    identity: String,
    isFocused: Bool
  ) throws {
    self.context = context
    self.visibility = visibility
    self.div = div
    self.clipToBounds = clipToBounds
    self.extensionHandlers = extensionHandlers
    self.identity = identity
    self.isFocused = isFocused
  }

  func applyExtensionHandlers(
    stage: ApplyExtensionHandlersStage
  ) -> Self {
    block = block.applyExtensionHandlers(
      order: stage,
      div: div,
      extensionHandlers: extensionHandlers,
      context: context
    )

    return self
  }

  func applyEdgeInsets(
    applyPaddings: Bool
  ) -> Self {
    block = block.addingEdgeInsets(
      applyPaddings ? div.paddings.resolve(context) : .zero,
      clipsToBounds: clipToBounds
    )

    return self
  }

  func applyExternalEdgeInsets(
    externalInsets: EdgeInsets
  ) -> Self {
    block = block.addingEdgeInsets(
      externalInsets,
      clipsToBounds: visibility.isVisible ? false : clipToBounds
    )

    return self
  }

  func applyBlockFinalDecorations() -> Self {
    switch visibility {
    case let .visible(_, alpha):
      block = block
        .addingDecorations(
          boundary: finalClipToBounds ? nil : .noClip,
          alpha: alpha
        )
    case .invisible:
      block = block.addingDecorations(alpha: 0)
    case .gone:
      assertionFailure("This function should not be called for \".gone\" visibility")
    }

    return self
  }

  func applyBackground() -> Self {
    guard let backgrounds = getBackground(isFocused) else {
      return self
    }

    // optimization for the most common case: saves Array alloc/dealloc
    if backgrounds.count == 1 {
      guard let background = backgrounds[0].makeBlockBackground(context: context) else {
        return self
      }
      if case let .solidColor(color) = background {
        block = block.addingDecorations(backgroundColor: color)
        return self
      }
      block = BackgroundBlock(
        background: background,
        child: block
      )

      return self
    }

    let blockBackgrounds = backgrounds.compactMap {
      $0.makeBlockBackground(context: context)
    }
    guard let background = blockBackgrounds.composite() else {
      return self
    }

    block = BackgroundBlock(
      background: background,
      child: block
    )

    return self
  }

  func applyVisibilityActionsAndDecorations(
    customAccessibilityParams: CustomAccessibilityParams
  ) throws -> Self {
    // We can't put container properties into single container.
    // Properties should be applied in specific order.
    // For example, shadow should be applied to block with border
    // and alpha should be applied to block with border and shadow.
    var visibilityActions = div.makeVisibilityActions(
      actionsType: .appear,
      context: context
    )
    visibilityActions += div.makeVisibilityActions(
      actionsType: .disappear,
      context: context
    )

    let visibilityParams = context.makeVisibilityParams(
      actions: visibilityActions,
      isVisible: true
    )

    let accessibilityElement = (div.accessibility ?? DivAccessibility()).resolve(
      expressionResolver,
      id: context.currentDivId,
      customParams: customAccessibilityParams
    )

    block = try block
      .addingDecorations(
        boundary: boundary,
        border: border?.resolveBorder(expressionResolver),
        shadow: shadow,
        visibilityParams: visibilityParams,
        tooltips: div.tooltips.makeTooltips(context: context),
        accessibilityElement: accessibilityElement,
        isFocused: isFocused
      )

    return self
  }

  func applyTransitioningAnimations() -> Self {
    let expressionResolver = context.expressionResolver

    let initialAnimationIn = div.transitionIn?.resolveAnimations(
      expressionResolver,
      type: .appearing
    )

    let animationIn: [TransitioningAnimation]? = if isAppearing() {
      initialAnimationIn
    } else {
      nil
    }

    let animationOut = div.transitionOut?.resolveAnimations(expressionResolver, type: .disappearing)

    let animationChange = div.transitionChange?.resolveTransition(expressionResolver)

    if animationIn != nil || animationOut != nil || animationChange != nil {
      block = DetachableAnimationBlock(
        child: block,
        id: identity,
        animationIn: animationIn,
        animationOut: animationOut,
        animationChange: animationChange
      )

      addAnimationWarningsIfNeeded(
        isPresentAnimationIn: initialAnimationIn != nil
      )
    }

    return self
  }

  func applyActions(
    actionsHolder: DivActionsHolder?
  ) -> Self {
    block = block
      .addActions(
        context: context,
        actionsHolder: actionsHolder,
        clipToBounds: finalClipToBounds
      )
    return self
  }

  func applyTransformations() -> Self {
    block = block
      .addingTransformations(
        transform: div.transform,
        rotation: rotation,
        expressionResolver: expressionResolver
      )

    return self
  }

  func setBlockLayoutProvider(_ layoutProvider: DivLayoutProvider?) -> Self {
    if let layoutProvider {
      block = layoutProvider.apply(block: block, context: context)
    }

    return self
  }

  func setBlockReuseId(_ reuseId: String?) -> Self {
    guard let reuseId else { return self }

    block = block.addingDecorations(
      reuseId: reuseId
    )

    return self
  }

  func setupContextForBlockVisibility() -> Self {
    if visibility.isVisible {
      context.lastVisibleBoundsCache.onBecomeVisible(path)
    } else {
      context.lastVisibleBoundsCache.onBecomeInvisible(path)
    }

    context.stateManager.setBlockVisibility(
      statePath: statePath,
      resolvedId: identity,
      div: div,
      isVisible: visibility.isVisible
    )

    return self
  }

  func build() -> Block {
    block
  }

  private func makeGoneBlock(
    div: DivBase
  ) -> Block {
    context.stateManager.setBlockVisibility(
      statePath: statePath,
      div: div,
      isVisible: false
    )

    if let visibilityParams = context.makeVisibilityParams(
      actions: div.makeVisibilityActions(
        actionsType: .disappear,
        context: context
      ),
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

  private func getBackground(_ isFocused: Bool) -> [DivBackground]? {
    guard isFocused else {
      return div.background
    }
    return div.focus?.background ?? div.background
  }

  private func isAppearing() -> Bool {
    let stateManager = context.stateManager
    if stateManager.shouldBlockAppearWithTransition(path: statePath + identity) {
      return true
    }

    return stateManager.isBlockAdded(identity, stateBlockPath: statePath.stateBlockPath)
  }

  private func addAnimationWarningsIfNeeded(isPresentAnimationIn: Bool) {
    guard context.currentDivId == nil,
          let block = block as? DetachableAnimationBlock else { return }

    func warningMessage(_ animation: String) -> String {
      "The component id with the \(animation) property for state change is missing. Either specify the id, or specify the \"transition_trigger\" property without \"state_change\" value."
    }

    if isPresentAnimationIn,
       div.transitionTriggersOrDefault.contains(.stateChange) {
      context.addWarning(
        message: warningMessage("\"transition_in\"")
      )
    }

    if block.animationOut != nil,
       div.transitionTriggersOrDefault.contains(.stateChange) {
      context.addWarning(
        message: warningMessage("\"transition_out\"")
      )
    }

    if block.animationChange != nil {
      context.addWarning(
        message: warningMessage("\"transition_change\"")
      )
    }
  }
}
