import CoreGraphics
import Foundation
import LayoutKit
import VGSL

extension DivSlider: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let context = modifiedContextParentPath(context)
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: nil
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    let firstThumbValue: Binding<Int> = thumbValueVariable.flatMap {
      context.makeBinding(variableName: $0, defaultValue: 0)
    } ?? .zero
    let firstThumb = SliderModel.ThumbModel(
      block: makeThumbBlock(
        thumb: thumbStyle.makeBlock(context: context, corners: .all),
        textBlock: thumbTextStyle?.makeThumbTextBlock(
          context: context,
          value: firstThumbValue.value
        ),
        textOffset: thumbTextStyle.flatMap {
          CGPoint(
            x: $0.offset?.x.resolveValue(expressionResolver) ?? 0,
            y: $0.offset?.y.resolveValue(expressionResolver) ?? 0
          )
        } ?? .zero
      ),
      value: firstThumbValue,
      size: CGSize(
        width: thumbStyle.resolveWidth(context),
        height: thumbStyle.resolveHeight(context)
      ),
      offset: thumbTextStyle?.offset?.resolve(expressionResolver) ?? .zero
    )
    let secondThumb: SliderModel.ThumbModel?
    if let thumbSecondaryValueVariable {
      let secondThumbValue = context.makeBinding(
        variableName: thumbSecondaryValueVariable,
        defaultValue: 0
      )
      secondThumb = SliderModel.ThumbModel(
        block: makeThumbBlock(
          thumb: thumbSecondaryStyle?.makeBlock(context: context, corners: .all) ?? thumbStyle
            .makeBlock(context: context, corners: .all),
          textBlock: (thumbSecondaryTextStyle ?? thumbTextStyle)?.makeThumbTextBlock(
            context: context,
            value: secondThumbValue.value
          ),
          textOffset: thumbSecondaryTextStyle?.offset?.resolve(expressionResolver) ?? .zero
        ),
        value: secondThumbValue,
        size: CGSize(
          width: thumbSecondaryStyle?.resolveWidth(context)
            ?? thumbStyle.resolveWidth(context),
          height: thumbSecondaryStyle?.resolveHeight(context)
            ?? thumbStyle.resolveHeight(context)
        ),
        offset: thumbSecondaryTextStyle?.offset?.resolve(expressionResolver) ?? .zero
      )
    } else {
      secondThumb = nil
    }

    let activeMark = makeRoundedRectangle(
      drawable: tickMarkActiveStyle,
      resolver: expressionResolver
    )
    let inactiveMark = makeRoundedRectangle(
      drawable: tickMarkInactiveStyle,
      resolver: expressionResolver
    )

    let minValue = resolveMinValue(expressionResolver)
    let maxValue = resolveMaxValue(expressionResolver)

    let marksConfiguration = MarksConfigurationModel(
      minValue: CGFloat(minValue),
      maxValue: CGFloat(maxValue),
      activeMark: activeMark ?? .empty,
      inactiveMark: inactiveMark ?? .empty,
      layoutDirection: context.layoutDirection
    )

    let pressStartActions = pressStartActions?.uiActions(context: context) ?? []
    let pressEndActions = pressEndActions?.uiActions(context: context) ?? []

    let sliderModel = SliderModel(
      firstThumb: firstThumb,
      secondThumb: secondThumb,
      minValue: minValue,
      maxValue: maxValue,
      marksConfiguration: marksConfiguration,
      ranges: makeRanges(ranges, with: context),
      layoutDirection: context.layoutDirection,
      path: context.path,
      isEnabled: resolveIsEnabled(expressionResolver),
      pressStartActions: pressStartActions,
      pressEndActions: pressEndActions
    )
    return SliderBlock(
      sliderModel: sliderModel,
      widthTrait: resolveWidthTrait(context),
      heightTrait: resolveHeightTrait(context)
    )
  }

  private func makeRanges(
    _ ranges: [DivSlider.Range]?,
    with context: DivBlockModelingContext
  ) -> [SliderModel.RangeModel] {
    let expressionResolver = context.expressionResolver

    let minValue = resolveMinValue(expressionResolver)
    let maxValue = resolveMaxValue(expressionResolver)

    var sliderRanges: [SliderModel.RangeModel] = (ranges ?? []).map { range in
      SliderModel.RangeModel(
        start: range.resolveStart(expressionResolver) ?? minValue,
        end: range.resolveEnd(expressionResolver) ?? maxValue,
        margins: range.margins.resolve(context),
        activeTrack: range.trackActiveStyle?.makeBlock(
          context: context,
          widthTrait: .resizable,
          corners: .all
        ) ?? trackActiveStyle.makeBlock(
          context: context,
          widthTrait: .resizable,
          corners: .all
        ),
        inactiveTrack: range.trackInactiveStyle?.makeBlock(
          context: context,
          widthTrait: .resizable,
          corners: .all
        ) ?? trackInactiveStyle.makeBlock(
          context: context,
          widthTrait: .resizable,
          corners: .all
        )
      )
    }

    let makeBasicRange: (Int, Int, CGRect.Corners) -> SliderModel.RangeModel = { [self] in
      SliderModel.RangeModel(
        start: $0,
        end: $1,
        margins: EdgeInsets.zero,
        activeTrack: trackActiveStyle.makeBlock(
          context: context,
          widthTrait: .resizable,
          corners: $2
        ),
        inactiveTrack: trackInactiveStyle.makeBlock(
          context: context,
          widthTrait: .resizable,
          corners: $2
        )
      )
    }

    sliderRanges.sort { $0.start < $1.start }

    var lastRangeEnd = minValue
    for (index, range) in sliderRanges.enumerated() {
      if range.start != lastRangeEnd {
        let corner: CGRect.Corners = index == 0 ? .left : .all
        sliderRanges.append(makeBasicRange(lastRangeEnd, range.start, corner))
      }
      lastRangeEnd = range.end
    }
    if lastRangeEnd != maxValue {
      let corner: CGRect.Corners = sliderRanges.count == 0 ? .all : .right
      sliderRanges.append(makeBasicRange(lastRangeEnd, maxValue, corner))
    }

    sliderRanges.sort { $0.start < $1.start }

    return sliderRanges
  }
}

private func makeThumbBlock(
  thumb: Block,
  textBlock: Block?,
  textOffset: CGPoint
) -> Block {
  let insets = EdgeInsets(
    top: max(textOffset.y, 0),
    left: max(textOffset.x, 0),
    bottom: max(-textOffset.y, 0),
    right: max(-textOffset.x, 0)
  ) * 2
  return LayeredBlock(
    widthTrait: .intrinsic,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [
      thumb,
      textBlock?.addingEdgeInsets(insets),
    ].compactMap { $0 }
  )
}

private func makeRoundedRectangle(
  drawable: DivDrawable?,
  resolver: ExpressionResolver
) -> MarksConfigurationModel.RoundedRectangle? {
  guard let drawable else {
    return nil
  }

  switch drawable {
  case let .divShapeDrawable(drawable):
    let color = drawable.resolveColor(resolver) ?? .clear
    let fallbackBorder = drawable.stroke
    switch drawable.shape {
    case let .divRoundedRectangleShape(shape):
      let border = shape.stroke
      let borderWidth = border?.resolveWidth(resolver)
        ?? fallbackBorder?.resolveWidth(resolver)
        ?? .zero
      let borderColor = border?.resolveColor(resolver)
        ?? fallbackBorder?.resolveColor(resolver)
        ?? .clear
      return MarksConfigurationModel.RoundedRectangle(
        size: CGSize(
          width: shape.itemWidth.resolveValue(resolver) ?? 0,
          height: shape.itemHeight.resolveValue(resolver) ?? 0
        ),
        cornerRadius: CGFloat(shape.cornerRadius.resolveValue(resolver) ?? 0),
        color: color,
        borderWidth: borderWidth,
        borderColor: borderColor
      )
    case let .divCircleShape(shape):
      let cornerRadius = CGFloat(shape.radius.resolveValue(resolver) ?? 0)
      let border = shape.stroke
      let borderWidth = border?.resolveWidth(resolver)
        ?? fallbackBorder?.resolveWidth(resolver)
        ?? .zero
      let borderColor = border?.resolveColor(resolver)
        ?? fallbackBorder?.resolveColor(resolver)
        ?? .clear
      return MarksConfigurationModel.RoundedRectangle(
        size: CGSize(squareDimension: cornerRadius * 2),
        cornerRadius: cornerRadius,
        color: color,
        borderWidth: borderWidth,
        borderColor: borderColor
      )
    }
  }
}

extension DivSlider.TextStyle {
  fileprivate func makeThumbTextBlock(
    context: DivBlockModelingContext,
    value: Int
  ) -> Block {
    let expressionResolver = context.expressionResolver
    let fontParams = FontParams(
      family: "",
      weight: resolveFontWeightValue(expressionResolver)
        ?? resolveFontWeight(expressionResolver)?.toInt(),
      size: resolveFontSize(expressionResolver),
      unit: resolveFontSizeUnit(expressionResolver),
      featureSettings: nil,
      variationSettings: resolveFontVariationSettings(expressionResolver)?
        .mapValues { $0 as? NSNumber }.filteringNilValues()
    )
    let typo = Typo(font: context.font(fontParams))
      .with(color: resolveTextColor(expressionResolver))
      .with(alignment: .center)
    return TextBlock(
      widthTrait: .intrinsic,
      text: Int(value).description.with(typo: typo)
    )
  }
}
