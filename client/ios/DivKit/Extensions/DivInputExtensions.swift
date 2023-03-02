import CoreFoundation
import CoreGraphics
import Foundation

import BaseUI
import CommonCore
import LayoutKit

extension DivInput: DivBlockModeling {
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
    let font = context.fontSpecifiers.font(
      family: resolveFontFamily(context.expressionResolver).fontFamily,
      weight: resolveFontWeight(context.expressionResolver).fontWeight,
      size: resolveFontSizeUnit(context.expressionResolver)
        .makeScaledValue(resolveFontSize(context.expressionResolver))
    )
    var typo = Typo(font: font).allowHeightOverrun

    let kern = CGFloat(resolveLetterSpacing(context.expressionResolver))
    if !kern.isApproximatelyEqualTo(0) {
      typo = typo.kerned(kern)
    }

    if let lineHeight = resolveLineHeight(context.expressionResolver) {
      typo = typo.with(height: CGFloat(lineHeight))
    }

    let resolvedHintColor: Color = resolveHintColor(context.expressionResolver)
    let hintTypo = typo.with(color: resolvedHintColor)
    let hintValue = resolveHintText(context.expressionResolver) ?? ""

    let resolvedColor: Color = resolveTextColor(context.expressionResolver)
    let textTypo = typo.with(color: resolvedColor)

    let textValue = Binding<String>(context: context, name: textVariable)

    let highlightColor = resolveHighlightColor(context.expressionResolver)

    let keyboardType = resolveKeyboardType(context.expressionResolver)

    let maxVisibleLines = resolveMaxVisibleLines(context.expressionResolver)

    let selectAllOnFocus = resolveSelectAllOnFocus(context.expressionResolver)

    let onFocusActions = (focus?.onFocus ?? []).map {
      $0.uiAction(context: context.actionContext)
    }
    let onBlurActions = (focus?.onBlur ?? []).map {
      $0.uiAction(context: context.actionContext)
    }

    let inputType: TextInputBlock.InputType
    let multiLineMode: Bool

    if let inputMethod = inputMethod {
      inputType = inputMethod.makeInputType(context.expressionResolver)
      multiLineMode = inputMethod.isMultiLineMode(context.expressionResolver)
    } else {
      inputType = .keyboard(keyboardType.system)
      multiLineMode = keyboardType == .multiLineText
    }

    return TextInputBlock(
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeContentHeightTrait(with: context),
      hint: hintValue.with(typo: hintTypo),
      textValue: textValue,
      textTypo: textTypo,
      multiLineMode: multiLineMode,
      inputType: inputType,
      highlightColor: highlightColor,
      maxVisibleLines: maxVisibleLines,
      selectAllOnFocus: selectAllOnFocus,
      path: context.parentPath,
      onFocusActions: onFocusActions,
      onBlurActions: onBlurActions,
      parentScrollView: context.parentScrollView
    )
  }
}

extension DivAlignmentHorizontal {
  fileprivate var system: TextAlignment {
    switch self {
    case .left:
      return .left
    case .center:
      return .center
    case .right:
      return .right
    }
  }
}

extension DivInput.KeyboardType {
  fileprivate var system: TextInputBlock.InputType.KeyboardType {
    switch self {
    case .singleLineText, .multiLineText:
      return .default
    case .phone:
      return .phonePad
    case .number:
      return .numbersAndPunctuation
    case .email:
      return .emailAddress
    case .uri:
      return .URL
    }
  }
}

extension DivKeyboardInput.KeyboardType {
  fileprivate var system: TextInputBlock.InputType.KeyboardType {
    switch self {
    case .singleLineText, .multiLineText:
      return .default
    case .phone:
      return .phonePad
    case .number:
      return .numbersAndPunctuation
    case .email:
      return .emailAddress
    case .uri:
      return .URL
    }
  }
}

extension DivInputMethod {
  fileprivate func isMultiLineMode(_ resolver: ExpressionResolver) -> Bool {
    switch self {
    case let .divKeyboardInput(divKeyboardInput):
      return divKeyboardInput.resolveKeyboardType(resolver) == .multiLineText
    case .divSelectionInput:
      return true
    }
  }

  fileprivate func makeInputType(_ resolver: ExpressionResolver) -> TextInputBlock.InputType {
    switch self {
    case let .divKeyboardInput(divKeyboardInput):
      return .keyboard(divKeyboardInput.resolveKeyboardType(resolver).system)
    case let .divSelectionInput(divSelectionInput):
      return .selection(divSelectionInput.items.map { $0.makeSelectionItem(resolver) })
    }
  }
}

extension DivSelectionInput.Item {
  fileprivate func makeSelectionItem(_ resolver: ExpressionResolver) -> TextInputBlock.InputType
    .SelectionItem {
    let text = resolveText(resolver) ?? ""
    let value = resolveValue(resolver) ?? text
    return TextInputBlock.InputType.SelectionItem(value: value, text: text)
  }
}
