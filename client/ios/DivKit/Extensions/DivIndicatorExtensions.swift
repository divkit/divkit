import CoreGraphics
import Foundation

import CommonCorePublic
import LayoutKit

extension DivIndicator: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actions: nil,
      actionAnimation: nil,
      doubleTapActions: nil,
      longTapActions: nil
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    guard let rectangle = shape.rectangle else {
      context.addError(level: .error, message: "circle shape unsupported.")
      return EmptyBlock()
    }

    let expressionResolver = context.expressionResolver
    let activeRect = makeRoundedRectangle(with: activeShape, resolver: expressionResolver)
    let inactiveRect = makeRoundedRectangle(with: inactiveShape, resolver: expressionResolver)
    let inactiveMinimumRect = makeRoundedRectangle(
      with: inactiveMinimumShape,
      resolver: expressionResolver
    )

    let pageSize = CGSize(
      width: inactiveRect?.size
        .width ?? CGFloat(rectangle.itemWidth.resolveValue(expressionResolver) ?? 0),
      height: inactiveRect?.size
        .height ?? CGFloat(rectangle.itemHeight.resolveValue(expressionResolver) ?? 0)
    )

    let hightightedScaleX: CGFloat
    let hightightedScaleY: CGFloat
    if let activeRect = activeRect, let inactiveRect = inactiveRect {
      hightightedScaleX = activeRect.size.width / inactiveRect.size.width
      hightightedScaleY = activeRect.size.height / inactiveRect.size.height
    } else {
      let scale = CGFloat(resolveActiveItemSize(expressionResolver))
      hightightedScaleX = scale
      hightightedScaleY = scale
    }

    let disappearingScaleX: CGFloat
    let disappearingScaleY: CGFloat
    if let inactiveRect = inactiveRect, let inactiveMinimumRect = inactiveMinimumRect {
      disappearingScaleX = inactiveMinimumRect.size.width / inactiveRect.size.width
      disappearingScaleY = inactiveMinimumRect.size.height / inactiveRect.size.height
    } else {
      let scale = CGFloat(resolveMinimumItemSize(expressionResolver))
      disappearingScaleX = scale
      disappearingScaleY = scale
    }

    let pagerPath = pagerId.map {
      PagerPath(
        cardId: context.cardId.rawValue,
        pagerId: $0
      )
    }
    let state = context.blockStateStorage.states.pagerViewState(for: pagerPath) ?? .default
    let spaceBetweenCenters = CGFloat(spaceBetweenCenters.resolveValue(expressionResolver) ?? 0)

    let configuration = PageIndicatorConfiguration(
      highlightedColor: activeRect?.backgroundColor ?? resolveActiveItemColor(expressionResolver),
      normalColor: inactiveRect?.backgroundColor ?? resolveInactiveItemColor(expressionResolver),
      highlightedBorder: BlockBorder(
        color: activeRect?.borderColor ?? .clear,
        width: activeRect?.borderWidth ?? 0
      ),
      normalBorder: BlockBorder(
        color: inactiveRect?.borderColor ?? .clear,
        width: inactiveRect?.borderWidth ?? 0
      ),
      highlightedHeightScale: hightightedScaleY,
      highlightedWidthScale: hightightedScaleX,
      disappearingHeightScale: disappearingScaleY,
      disappearingWidthScale: disappearingScaleX,
      pageSize: pageSize,
      highlightedPageCornerRadius: activeRect?.cornerRadius,
      pageCornerRadius: inactiveRect?
        .cornerRadius ?? CGFloat(rectangle.cornerRadius.resolveValue(expressionResolver) ?? 0),
      animation: resolveAnimation(expressionResolver).asBlockAnimation,
      itemPlacement: itemsPlacement?
        .makeItemPlacement(with: expressionResolver) ??
        .fixed(spaceBetweenCenters: spaceBetweenCenters)
    )

    return
      PageControlBlock(
        pagerPath: pagerPath,
        widthTrait: makeContentWidthTrait(with: context),
        heightTrait: makeContentHeightTrait(with: context),
        configuration: configuration,
        state: state
      )
  }

  private func makeRoundedRectangle(
    with shape: DivRoundedRectangleShape?,
    resolver: ExpressionResolver
  ) -> PageIndicatorConfiguration.RoundedRectangleIndicator? {
    guard let shape = shape else {
      return nil
    }
    return PageIndicatorConfiguration.RoundedRectangleIndicator(
      size: CGSize(
        width: shape.itemWidth.resolveValue(resolver) ?? 0,
        height: shape.itemHeight.resolveValue(resolver) ?? 0
      ),
      cornerRadius: CGFloat(shape.cornerRadius.resolveValue(resolver) ?? 0),
      backgroundColor: shape.resolveBackgroundColor(resolver) ?? .clear,
      borderWidth: CGFloat(shape.stroke?.resolveWidth(resolver) ?? 0),
      borderColor: shape.stroke?.resolveColor(resolver) ?? .clear
    )
  }
}

extension DivShape {
  fileprivate var rectangle: DivRoundedRectangleShape? {
    switch self {
    case let .divRoundedRectangleShape(rectangle):
      return rectangle
    case .divCircleShape:
      return nil
    }
  }
}

extension DivIndicator.Animation {
  fileprivate var asBlockAnimation: PageIndicatorConfiguration.Animation {
    switch self {
    case .scale:
      return .scale
    case .worm:
      return .worm
    case .slider:
      return .slider
    }
  }
}

extension DivIndicatorItemPlacement {
  func makeItemPlacement(with expressionResolver: ExpressionResolver) -> PageIndicatorConfiguration
    .ItemPlacement {
    switch self {
    case let .divDefaultIndicatorItemPlacement(defaultPlacement):
      return .fixed(spaceBetweenCenters: CGFloat(
        defaultPlacement.spaceBetweenCenters.resolveValue(expressionResolver) ?? 0
      ))
    case let .divStretchIndicatorItemPlacement(stretchPlacement):
      return .stretch(
        spacing: CGFloat(stretchPlacement.itemSpacing.resolveValue(expressionResolver) ?? 0),
        maxVisibleItems: stretchPlacement.resolveMaxVisibleItems(expressionResolver)
      )
    }
  }
}
