import Foundation

import BaseUIPublic
import CommonCorePublic
import LayoutKit
import NetworkingPublic

extension DivImage: DivBlockModeling, DivImageProtocol {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: makeActions(context: context),
      actionAnimation: actionAnimation.makeActionAnimation(with: context.expressionResolver),
      doubleTapActions: makeDoubleTapActions(context: context),
      longTapActions: makeLongTapActions(context: context)
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    try checkLayoutTraits(context: context)

    let expressionResolver = context.expressionResolver
    let highPriority = resolveHighPriorityPreviewShow(expressionResolver)
    let imageHolderFactory: DivImageHolderFactory
    if highPriority, let highPriorityImageHolderFactory = context.highPriorityImageHolderFactory {
      imageHolderFactory = highPriorityImageHolderFactory
    } else {
      imageHolderFactory = context.imageHolderFactory
    }

    return ImageBlock(
      imageHolder: imageHolderFactory.make(
        resolveImageUrl(expressionResolver),
        resolvePlaceholder(expressionResolver, highPriority: highPriority)
      ),
      widthTrait: makeContentWidthTrait(with: context),
      height: resolveHeight(context),
      contentMode: contentMode(context: context),
      tintColor: resolveTintColor(expressionResolver),
      tintMode: resolveTintMode(expressionResolver).tintMode,
      effects: makeEffects(with: expressionResolver),
      appearanceAnimation: appearanceAnimation?.makeAppearanceAnimation(with: expressionResolver)
    )
  }
}

extension DivBlendMode {
  fileprivate var tintMode: TintMode {
    switch self {
    case .sourceIn:
      return .sourceIn
    case .sourceAtop:
      return .sourceAtop
    case .darken:
      return .darken
    case .lighten:
      return .lighten
    case .multiply:
      return .multiply
    case .screen:
      return .screen
    }
  }
}

extension DivImage {
  fileprivate func makeEffects(with resolver: ExpressionResolver) -> [ImageEffect] {
    filters?.compactMap { $0.makeImageEffect(with: resolver) } ?? []
  }
}
