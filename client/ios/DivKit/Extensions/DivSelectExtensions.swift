import CoreFoundation
import CoreGraphics
import Foundation

import LayoutKit
import VGSL

extension DivSelect: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: nil
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    let expressionResolver = context.expressionResolver

    let font = context.fontProvider.font(resolveFontParams(expressionResolver))
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

    let textValue: Binding<String> = context
      .makeBinding(variableName: valueVariable, defaultValue: "")

    let onFocusActions = focus?.onFocus?.uiActions(context: context) ?? []
    let onBlurActions = focus?.onBlur?.uiActions(context: context) ?? []

    let selectPath = context.parentPath + (id ?? DivSelect.type)
    let isFocused = context.blockStateStorage.isFocused(path: selectPath)

    return TextInputBlock(
      widthTrait: resolveContentWidthTrait(context),
      heightTrait: resolveContentHeightTrait(context),
      hint: hintValue.with(typo: hintTypo),
      textValue: textValue,
      rawTextValue: nil,
      textTypo: textTypo,
      inputType: makeInputType(expressionResolver),
      path: selectPath,
      isFocused: isFocused,
      onFocusActions: onFocusActions,
      onBlurActions: onBlurActions,
      parentScrollView: context.parentScrollView,
      layoutDirection: context.layoutDirection
    )
  }

  private func makeInputType(_ resolver: ExpressionResolver) -> TextInputBlock.InputType {
    .selection(options.map { $0.makeSelectionItem(resolver) })
  }
}

extension DivSelect: FontParamsProvider {}

extension DivSelect.Option {
  fileprivate func makeSelectionItem(_ resolver: ExpressionResolver) -> TextInputBlock.InputType
    .SelectionItem {
    let value = resolveValue(resolver) ?? ""
    let text = resolveText(resolver) ?? value
    return TextInputBlock.InputType.SelectionItem(value: value, text: text)
  }
}
