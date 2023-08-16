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

    if let lineHeightInt = resolveLineHeight(expressionResolver) {
      let lineHeight = CGFloat(lineHeightInt)
      typo = typo.with(height: lineHeight)
      if lineHeight > font.lineHeight {
        typo = typo.with(baseline: (lineHeight - font.lineHeight) / 2)
      }
    }

    let hintValue = resolveHintText(expressionResolver) ?? ""
    let keyboardType = resolveKeyboardType(expressionResolver)

    let onFocusActions = (focus?.onFocus ?? []).map {
      $0.uiAction(context: context)
    }
    let onBlurActions = (focus?.onBlur ?? []).map {
      $0.uiAction(context: context)
    }

    return TextInputBlock(
      widthTrait: makeContentWidthTrait(with: context),
      heightTrait: makeContentHeightTrait(with: context),
      hint: hintValue.with(typo: typo.with(color: resolveHintColor(expressionResolver))),
      textValue: Binding<String>(context: context, name: textVariable),
      rawTextValue: mask?.makeRawVariable(context),
      textTypo: typo.with(color: resolveTextColor(expressionResolver)),
      multiLineMode: keyboardType == .multiLineText,
      inputType: keyboardType.system,
      highlightColor: resolveHighlightColor(expressionResolver),
      maxVisibleLines: resolveMaxVisibleLines(expressionResolver),
      selectAllOnFocus: resolveSelectAllOnFocus(expressionResolver),
      maskValidator: mask?.makeMaskValidator(expressionResolver),
      path: context.parentPath,
      onFocusActions: onFocusActions,
      onBlurActions: onBlurActions,
      parentScrollView: context.parentScrollView,
      validators: makeValidators(context),
      layoutDirection: context.layoutDirection,
      textAlignmentHorizontal: resolveTextAlignmentHorizontal(expressionResolver).textAlignment,
      textAlignmentVertical: resolveTextAlignmentVertical(expressionResolver).textAlignment
    )
  }
}

extension DivInput {
  fileprivate func makeValidators(_ context: DivBlockModelingContext) -> [TextInputValidator]? {
    let expressionResolver = context.expressionResolver
    return validators?.compactMap { validator -> TextInputValidator? in
      switch validator {
      case let .divInputValidatorRegex(regexValidator):
        let pattern = regexValidator.resolvePattern(expressionResolver) ?? ""
        guard let regex = try? NSRegularExpression(pattern: pattern) else {
          DivKitLogger.error("Invalid regex pattern '\(pattern)'")
          return nil
        }
        return TextInputValidator(
          isValid: .init(context: context, name: regexValidator.variable),
          allowEmpty: regexValidator.resolveAllowEmpty(expressionResolver),
          validator: { $0.matchesRegex(regex) },
          message: makeMessage(
            from: regexValidator.resolveLabelId(expressionResolver),
            storage: context.blockStateStorage,
            cardId: context.cardId
          )
        )
      case let .divInputValidatorExpression(expressionValidator):
        return TextInputValidator(
          isValid: .init(context: context, name: expressionValidator.variable),
          allowEmpty: expressionValidator.resolveAllowEmpty(expressionResolver),
          validator: { _ in expressionValidator.resolveCondition(expressionResolver) ?? true },
          message: makeMessage(
            from: expressionValidator.resolveLabelId(expressionResolver),
            storage: context.blockStateStorage,
            cardId: context.cardId
          )
        )
      }
    }
  }

  private func makeMessage(
    from labelId: String?,
    storage: DivBlockStateStorage,
    cardId: DivCardID
  ) -> () -> String? {
    {
      guard let labelId else {
        return nil
      }
      guard let state: TextBlockViewState = storage.getState(labelId, cardId: cardId) else {
        DivKitLogger.error("Can't find text with id '\(labelId)'")
        return nil
      }
      return state.text
    }
  }
}

extension DivAlignmentHorizontal {
  fileprivate var textAlignment: TextInputBlock.TextAlignmentHorizontal {
    switch self {
    case .left:
      return .left
    case .center:
      return .center
    case .right:
      return .right
    case .start:
      return .start
    case .end:
      return .end
    }
  }
}

extension DivAlignmentVertical {
  fileprivate var textAlignment: TextInputBlock.TextAlignmentVertical {
    switch self {
    case .top:
      return .top
    case .center:
      return .center
    case .bottom:
      return .bottom
    case .baseline:
      DivKitLogger.warning("Baseline alignment is not supported.")
      return .center
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
    case .divPhoneInputMask(_):
      return nil
    }
  }

  fileprivate func makeRawVariable(_ context: DivBlockModelingContext) -> Binding<String>? {
    switch self {
    case let .divFixedLengthInputMask(divFixedLengthInputMask):
      return .init(context: context, name: divFixedLengthInputMask.rawTextVariable)
    case .divCurrencyInputMask:
      return nil
    case .divPhoneInputMask(_):
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
