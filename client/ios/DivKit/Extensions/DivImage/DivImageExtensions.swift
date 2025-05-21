import Foundation
import LayoutKit
import VGSL

extension DivImage: DivBlockModeling, DivImageProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let context = modifiedContextParentPath(context)
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: self,
      customAccessibilityParams: CustomAccessibilityParams(defaultTraits: .image)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    let highPriority = resolveHighPriorityPreviewShow(expressionResolver)
    let widthTrait = resolveContentWidthTrait(context)
    let height = resolveHeight(context)
    return ImageBlock(
      imageHolder: context.imageHolderFactory.make(
        resolveImageUrl(expressionResolver),
        resolvePlaceholder(expressionResolver, highPriority: highPriority)
      ),
      widthTrait: widthTrait,
      height: height,
      contentMode: resolveContentMode(context),
      tintColor: resolveTintColor(expressionResolver),
      tintMode: resolveTintMode(expressionResolver).tintMode,
      effects: resolveEffects(expressionResolver),
      appearanceAnimation: appearanceAnimation?.resolve(expressionResolver),
      blurUsingMetal: context.flagsInfo.imageBlurPreferMetal,
      tintUsingMetal: context.flagsInfo.imageTintPreferMetal,
      path: context.path
    )
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
      duration: TimeInterval(milliseconds: resolveDuration(expressionResolver)),
      delay: TimeInterval(milliseconds: resolveStartDelay(expressionResolver)),
      timingFunction: resolveInterpolator(expressionResolver).asTimingFunction()
    )
  }
}
