import CoreFoundation
import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic
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

    let maskValidator = mask?.makeMaskValidator(context.expressionResolver)
    let rawTextValue: Binding<String>? = mask?.makeRawVariable(context)

    return TextInputBlock(
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeContentHeightTrait(with: context),
      hint: hintValue.with(typo: hintTypo),
      textValue: textValue,
      rawTextValue: rawTextValue,
      textTypo: textTypo,
      multiLineMode: keyboardType == .multiLineText,
      inputType: keyboardType.system,
      highlightColor: highlightColor,
      maxVisibleLines: maxVisibleLines,
      selectAllOnFocus: selectAllOnFocus,
      maskValidator: maskValidator,
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
  fileprivate var system: TextInputBlock.InputType {
    switch self {
    case .singleLineText, .multiLineText:
      return .default
    case .phone:
      return .keyboard(.phonePad)
    case .number:
      return .keyboard(.decimalPad)
    case .email:
      return .keyboard(.emailAddress)
    case .uri:
      return .keyboard(.URL)
    }
  }
}

extension DivInputMask {
  fileprivate func makeMaskValidator(_ resolver: ExpressionResolver) -> MaskValidator? {
    switch self {
    case let .divFixedLengthInputMask(divFixedLengthInputMask):
      return MaskValidator(
        pattern: divFixedLengthInputMask.resolvePattern(resolver) ?? "",
        alwaysVisible: divFixedLengthInputMask.resolveAlwaysVisible(resolver),
        patternElements: divFixedLengthInputMask.patternElements
          .map { $0.makePatternElement(resolver) }
      )
    case .divCurrencyInputMask:
      return nil
    }
  }

  fileprivate func makeRawVariable(_ context: DivBlockModelingContext) -> Binding<String>? {
    switch self {
    case let .divFixedLengthInputMask(divFixedLengthInputMask):
      return .init(context: context, name: divFixedLengthInputMask.rawTextVariable)
    case .divCurrencyInputMask:
      return nil
    }
  }
}

extension DivFixedLengthInputMask.PatternElement {
  fileprivate func makePatternElement(_ resolver: ExpressionResolver) -> PatternElement {
    PatternElement(
      key: (resolveKey(resolver) ?? "").first!,
      regex: try! NSRegularExpression(pattern: resolveRegex(resolver) ?? ""),
      placeholder: resolvePlaceholder(resolver).first!
    )
  }
}
