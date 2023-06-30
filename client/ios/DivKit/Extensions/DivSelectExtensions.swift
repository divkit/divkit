import CoreFoundation
import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension DivSelect: DivBlockModeling {
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

    let font = context.fontProvider.font(
      family: resolveFontFamily(expressionResolver) ?? "",
      weight: resolveFontWeight(expressionResolver),
      size: resolveFontSizeUnit(expressionResolver)
        .makeScaledValue(resolveFontSize(expressionResolver))
    )
    var typo = Typo(font: font).allowHeightOverrun

    let kern = CGFloat(resolveLetterSpacing(expressionResolver))
    if !kern.isApproximatelyEqualTo(0) {
      typo = typo.kerned(kern)
    }

    if let lineHeight = resolveLineHeight(expressionResolver) {
      typo = typo.with(height: CGFloat(lineHeight))
    }

    let resolvedHintColor: Color = resolveHintColor(expressionResolver)
    let hintTypo = typo.with(color: resolvedHintColor)
    let hintValue = resolveHintText(expressionResolver) ?? ""

    let resolvedColor: Color = resolveTextColor(expressionResolver)
    let textTypo = typo.with(color: resolvedColor)

    let textValue = Binding<String>(context: context, name: valueVariable)

    let onFocusActions = (focus?.onFocus ?? []).map {
      $0.uiAction(context: context.actionContext)
    }
    let onBlurActions = (focus?.onBlur ?? []).map {
      $0.uiAction(context: context.actionContext)
    }

    return TextInputBlock(
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeContentHeightTrait(with: context),
      hint: hintValue.with(typo: hintTypo),
      textValue: textValue,
      rawTextValue: nil,
      textTypo: textTypo,
      inputType: makeInputType(expressionResolver),
      path: context.parentPath,
      onFocusActions: onFocusActions,
      onBlurActions: onBlurActions,
      parentScrollView: context.parentScrollView
    )
  }
}

extension DivSelect {
  fileprivate func makeInputType(_ resolver: ExpressionResolver) -> TextInputBlock.InputType {
    .selection(options.map { $0.makeSelectionItem(resolver) })
  }
}

extension DivSelect.Option {
  fileprivate func makeSelectionItem(_ resolver: ExpressionResolver) -> TextInputBlock.InputType
    .SelectionItem {
    let value = resolveValue(resolver) ?? ""
    let text = resolveText(resolver) ?? value
    return TextInputBlock.InputType.SelectionItem(value: value, text: text)
  }
}
