import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivSlider: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
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
        thumb: thumbStyle
          .makeBlock(context: context, corners: .all),
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
        width: thumbStyle.getWidth(context: context),
        height: thumbStyle.getHeight(context: context)
      ),
      offsetX: thumbTextStyle?.offset?.x.resolveValue(expressionResolver) ?? 0,
      offsetY: thumbTextStyle?.offset?.y.resolveValue(expressionResolver) ?? 0
    )
    let secondThumb: SliderModel.ThumbModel?
    if let thumbSecondaryValueVariable = thumbSecondaryValueVariable {
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
          textOffset: thumbSecondaryTextStyle.flatMap {
            CGPoint(
              x: $0.offset?.x.resolveValue(expressionResolver) ?? 0,
              y: $0.offset?.y.resolveValue(expressionResolver) ?? 0
            )
          } ?? .zero
        ),
        value: secondThumbValue,
        size: CGSize(
          width: thumbSecondaryStyle?.getWidth(context: context) ?? thumbStyle
            .getWidth(context: context),
          height: thumbSecondaryStyle?.getHeight(context: context) ?? thumbStyle
            .getHeight(context: context)
        ),
        offsetX: thumbSecondaryTextStyle?.offset?.x.resolveValue(expressionResolver) ?? 0,
        offsetY: thumbSecondaryTextStyle?.offset?.y.resolveValue(expressionResolver) ?? 0
      )
    } else {
      secondThumb = nil
    }

    let activeMark = makeRoundedRectangle(with: tickMarkActiveStyle, resolver: expressionResolver)
    let inactiveMark = makeRoundedRectangle(
      with: tickMarkInactiveStyle,
      resolver: expressionResolver
    )

    let minValue = resolveMinValue(expressionResolver)
    let maxValue = resolveMaxValue(expressionResolver)

    let marksConfiguration = MarksConfiguration(
      minValue: CGFloat(minValue),
      maxValue: CGFloat(maxValue),
      activeMark: activeMark ?? .empty,
      inactiveMark: inactiveMark ?? .empty,
      layoutDirection: context.layoutDirection
    )

    let sliderRanges = makeRanges(ranges, with: context)

    let sliderModel = SliderModel(
      firstThumb: firstThumb,
      secondThumb: secondThumb,
      minValue: minValue,
      maxValue: maxValue,
      marksConfiguration: marksConfiguration,
      ranges: sliderRanges,
      layoutDirection: context.layoutDirection
    )

    let width = context.override(width: width)
    let height = context.override(height: height)

    return SliderBlock(
      sliderModel: sliderModel,
      widthTrait: width.makeLayoutTrait(with: expressionResolver),
      heightTrait: height.makeLayoutTrait(with: expressionResolver)
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
        margins: range.margins.makeEdgeInsets(context: context),
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
  with mark: DivDrawable?,
  resolver: ExpressionResolver
) -> MarksConfiguration
  .RoundedRectangle? {
  guard let mark = mark,
        let divShapeDrawable = mark.value as? DivShapeDrawable,
        let shape = divShapeDrawable.shape.value as? DivRoundedRectangleShape else {
    return nil
  }
  return MarksConfiguration.RoundedRectangle(
    size: CGSize(
      width: shape.itemWidth.resolveValue(resolver) ?? 0,
      height: shape.itemHeight.resolveValue(resolver) ?? 0
    ),
    cornerRadius: CGFloat(shape.cornerRadius.resolveValue(resolver) ?? 0),
    color: divShapeDrawable.resolveColor(resolver) ?? .clear,
    borderWidth: CGFloat(divShapeDrawable.stroke?.resolveWidth(resolver) ?? 0),
    borderColor: divShapeDrawable.stroke?.resolveColor(resolver) ?? .clear
  )
}

extension DivSlider.TextStyle {
  fileprivate func makeThumbTextBlock(
    context: DivBlockModelingContext,
    value: Int
  ) -> Block {
    let expressionResolver = context.expressionResolver

    let typo = Typo(
      font: context.fontProvider.font(
        weight: resolveFontWeight(expressionResolver),
        size: resolveFontSize(expressionResolver).map(CGFloat.init) ?? 16
      )
    )
    .with(color: resolveTextColor(expressionResolver))
    .with(alignment: .center)

    return TextBlock(
      widthTrait: .intrinsic,
      text: Int(value).description.with(typo: typo)
    )
  }
}
