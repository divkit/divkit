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
    let secondThumb: SliderModel.ThumbModel?
    if let thumbSecondaryStyle = thumbSecondaryStyle,
       let thumbSecondaryValueVariable = thumbSecondaryValueVariable {
      let secondThumbValue = Binding<Int>(context: context, name: thumbSecondaryValueVariable)
      secondThumb = SliderModel.ThumbModel(
        block: makeThumbBlock(
          thumb: try thumbSecondaryStyle
            .makeBlock(context: context, corners: .all),
          textBlock: thumbSecondaryTextStyle?.makeThumbTextBlock(
            context: context,
            value: secondThumbValue.wrappedValue
          ),
          textOffset: thumbSecondaryTextStyle.flatMap {
            CGPoint(
              x: $0.offset?.x.resolveValue(context.expressionResolver) ?? 0,
              y: $0.offset?.y.resolveValue(context.expressionResolver) ?? 0
            )
          } ?? .zero
        ),
        value: secondThumbValue,
        size: CGSize(
          width: thumbSecondaryStyle.getWidth(context: context),
          height: thumbSecondaryStyle.getHeight(context: context)
        ),
        offsetX: thumbSecondaryTextStyle?.offset?.x.resolveValue(context.expressionResolver) ?? 0,
        offsetY: thumbSecondaryTextStyle?.offset?.y.resolveValue(context.expressionResolver) ?? 0
      )
    } else {
      secondThumb = nil
    }

    let firstThumbValue: Binding<Int> = thumbValueVariable.flatMap {
      Binding<Int>(context: context, name: $0)
    } ?? .zero

    let sliderModel = SliderModel(
      firstThumb: SliderModel.ThumbModel(
        block: makeThumbBlock(
          thumb: try thumbStyle
            .makeBlock(context: context, corners: .all),
          textBlock: thumbTextStyle?.makeThumbTextBlock(
            context: context,
            value: firstThumbValue.wrappedValue
          ),
          textOffset: thumbTextStyle.flatMap {
            CGPoint(
              x: $0.offset?.x.resolveValue(context.expressionResolver) ?? 0,
              y: $0.offset?.y.resolveValue(context.expressionResolver) ?? 0
            )
          } ?? .zero
        ),
        value: firstThumbValue,
        size: CGSize(
          width: thumbStyle.getWidth(context: context),
          height: thumbStyle.getHeight(context: context)
        ),
        offsetX: thumbTextStyle?.offset?.x.resolveValue(context.expressionResolver) ?? 0,
        offsetY: thumbTextStyle?.offset?.y.resolveValue(context.expressionResolver) ?? 0
      ),
      secondThumb: secondThumb,
      activeMarkModel: tickMarkActiveStyle.flatMap {
        SliderModel.MarkModel(
          block: (try? $0.makeBlock(context: context, corners: .all)) ?? EmptyBlock.zeroSized,
          size: CGSize(
            width: $0.getWidth(context: context),
            height: $0.getHeight(context: context)
          )
        )
      },
      inactiveMarkModel: tickMarkInactiveStyle.flatMap {
        SliderModel.MarkModel(
          block: (try? $0.makeBlock(context: context, corners: .all)) ?? EmptyBlock.zeroSized,
          size: CGSize(
            width: $0.getWidth(context: context),
            height: $0.getHeight(context: context)
          )
        )
      },
      minValue: resolveMinValue(context.expressionResolver),
      maxValue: resolveMaxValue(context.expressionResolver),
      activeTrack: (try? self.trackActiveStyle.makeBlock(
        context: context,
        widthTrait: .resizable,
        corners: .all
      )) ?? EmptyBlock.zeroSized,
      inactiveTrack: (try? self.trackInactiveStyle.makeBlock(
        context: context,
        widthTrait: .resizable,
        corners: .all
      )) ?? EmptyBlock.zeroSized
    )
    let width = context.override(width: width)
    let height = context.override(height: height)
    return SliderBlock(
      sliderModel: sliderModel,
      widthTrait: width.makeLayoutTrait(with: context.expressionResolver),
      heightTrait: height.makeLayoutTrait(with: context.expressionResolver)
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

extension DivSlider.TextStyle {
  fileprivate func makeThumbTextBlock(
    context: DivBlockModelingContext,
    value: Int
  ) -> Block {
    let typo = Typo(
      size: FontSize(
        rawValue: resolveFontSize(context.expressionResolver)
          .flatMap(CGFloat.init) ?? 16
      ),
      weight: resolveFontWeight(context.expressionResolver).fontWeight
    )
    .with(color: resolveTextColor(context.expressionResolver))
    .with(alignment: .center)

    return TextBlock(
      widthTrait: .intrinsic,
      text: Int(value).description.with(typo: typo)
    )
  }
}
