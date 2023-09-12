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
      actions: nil,
      actionAnimation: nil,
      doubleTapActions: nil,
      longTapActions: nil
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver
    let secondThumb: SliderModel.ThumbModel?
    if let thumbSecondaryStyle = thumbSecondaryStyle,
       let thumbSecondaryValueVariable = thumbSecondaryValueVariable {
      let secondThumbValue = context.makeBinding(
        variableName: thumbSecondaryValueVariable,
        defaultValue: 0
      )
      secondThumb = SliderModel.ThumbModel(
        block: makeThumbBlock(
          thumb: thumbSecondaryStyle
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
          width: thumbSecondaryStyle.getWidth(context: context),
          height: thumbSecondaryStyle.getHeight(context: context)
        ),
        offsetX: thumbSecondaryTextStyle?.offset?.x.resolveValue(expressionResolver) ?? 0,
        offsetY: thumbSecondaryTextStyle?.offset?.y.resolveValue(expressionResolver) ?? 0
      )
    } else {
      secondThumb = nil
    }

    let firstThumbValue: Binding<Int> = thumbValueVariable.flatMap {
      context.makeBinding(variableName: $0, defaultValue: 0)
    } ?? .zero

    let activeMark = makeRoundedRectangle(with: tickMarkActiveStyle, resolver: expressionResolver)
    let inactiveMark = makeRoundedRectangle(
      with: tickMarkInactiveStyle,
      resolver: expressionResolver
    )

    let marksConfiguration = MarksConfiguration(
      minValue: CGFloat(resolveMinValue(expressionResolver)),
      maxValue: CGFloat(resolveMaxValue(expressionResolver)),
      activeMark: activeMark ?? .empty,
      inactiveMark: inactiveMark ?? .empty,
      layoutDirection: context.layoutDirection
    )

    let sliderModel = SliderModel(
      firstThumb: SliderModel.ThumbModel(
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
      ),
      secondThumb: secondThumb,
      activeMarkModel: tickMarkActiveStyle.flatMap {
        SliderModel.MarkModel(
          block: $0.makeBlock(context: context, corners: .all),
          size: CGSize(
            width: $0.getWidth(context: context),
            height: $0.getHeight(context: context)
          )
        )
      },
      inactiveMarkModel: tickMarkInactiveStyle.flatMap {
        SliderModel.MarkModel(
          block: $0.makeBlock(context: context, corners: .all),
          size: CGSize(
            width: $0.getWidth(context: context),
            height: $0.getHeight(context: context)
          )
        )
      },
      minValue: resolveMinValue(expressionResolver),
      maxValue: resolveMaxValue(expressionResolver),
      activeTrack: self.trackActiveStyle.makeBlock(
        context: context,
        widthTrait: .resizable,
        corners: .all
      ),
      inactiveTrack: self.trackInactiveStyle.makeBlock(
        context: context,
        widthTrait: .resizable,
        corners: .all
      ),
      marksConfiguration: marksConfiguration,
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
