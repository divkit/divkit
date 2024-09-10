import Foundation

import LayoutKit
import VGSL

extension DivImage: DivBlockModeling, DivImageProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: self,
      customAccessibilityParams: CustomAccessibilityParams(defaultTraits: .image)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    try checkLayoutTraits(context: context)

    let expressionResolver = context.expressionResolver
    let highPriority = resolveHighPriorityPreviewShow(expressionResolver)
    let imageHolderFactory: DivImageHolderFactory = if highPriority,
                                                       let highPriorityImageHolderFactory = context
                                                       .highPriorityImageHolderFactory {
      highPriorityImageHolderFactory
    } else {
      context.imageHolderFactory
    }

    return ImageBlock(
      imageHolder: imageHolderFactory.make(
        resolveImageUrl(expressionResolver),
        resolvePlaceholder(expressionResolver, highPriority: highPriority)
      ),
      widthTrait: resolveContentWidthTrait(context),
      height: resolveHeight(context),
      contentMode: resolveContentMode(context),
      tintColor: resolveTintColor(expressionResolver),
      tintMode: resolveTintMode(expressionResolver).tintMode,
      effects: resolveEffects(expressionResolver),
      appearanceAnimation: appearanceAnimation?.resolve(expressionResolver),
      blurUsingMetal: context.flagsInfo.imageBlurPreferMetal,
      tintUsingMetal: context.flagsInfo.imageTintPreferMetal
    )
  }
}

extension DivBlendMode {
  fileprivate var tintMode: TintMode {
    switch self {
    case .sourceIn:
      .sourceIn
    case .sourceAtop:
      .sourceAtop
    case .darken:
      .darken
    case .lighten:
      .lighten
    case .multiply:
      .multiply
    case .screen:
      .screen
    }
  }
}

extension DivImage {
  fileprivate func resolveEffects(_ expressionResolver: ExpressionResolver) -> [ImageEffect] {
    filters?.compactMap { $0.resolveEffect(expressionResolver) } ?? []
  }
}

extension DivFadeTransition {
  fileprivate func resolve(
    _ expressionResolver: ExpressionResolver
  ) -> TransitioningAnimation {
    TransitioningAnimation(
      kind: .fade,
      start: resolveAlpha(expressionResolver),
      end: 1,
      duration: Duration(milliseconds: resolveDuration(expressionResolver)),
      delay: Delay(milliseconds: resolveStartDelay(expressionResolver)),
      timingFunction: resolveInterpolator(expressionResolver).asTimingFunction()
    )
  }
}
