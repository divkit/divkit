// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivFixedLengthInputMaskTemplate: TemplateValue {
  public final class PatternElementTemplate: TemplateValue {
    public let key: Field<Expression<String>>? // at least 1 char
    public let placeholder: Field<Expression<String>>? // at least 1 char; default value: _
    public let regex: Field<Expression<String>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          key: try dictionary.getOptionalExpressionField("key"),
          placeholder: try dictionary.getOptionalExpressionField("placeholder"),
          regex: try dictionary.getOptionalExpressionField("regex")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "pattern_element_template." + field, representation: representation)
      }
    }

    init(
      key: Field<Expression<String>>? = nil,
      placeholder: Field<Expression<String>>? = nil,
      regex: Field<Expression<String>>? = nil
    ) {
      self.key = key
      self.placeholder = placeholder
      self.regex = regex
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: PatternElementTemplate?) -> DeserializationResult<DivFixedLengthInputMask.PatternElement> {
      let keyValue = parent?.key?.resolveValue(context: context, validator: ResolvedValue.keyValidator) ?? .noValue
      let placeholderValue = parent?.placeholder?.resolveOptionalValue(context: context, validator: ResolvedValue.placeholderValidator) ?? .noValue
      let regexValue = parent?.regex?.resolveOptionalValue(context: context) ?? .noValue
      var errors = mergeErrors(
        keyValue.errorsOrWarnings?.map { .nestedObjectError(field: "key", error: $0) },
        placeholderValue.errorsOrWarnings?.map { .nestedObjectError(field: "placeholder", error: $0) },
        regexValue.errorsOrWarnings?.map { .nestedObjectError(field: "regex", error: $0) }
      )
      if case .noValue = keyValue {
        errors.append(.requiredFieldIsMissing(field: "key"))
      }
      guard
        let keyNonNil = keyValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivFixedLengthInputMask.PatternElement(
        key: keyNonNil,
        placeholder: placeholderValue.value,
        regex: regexValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: PatternElementTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFixedLengthInputMask.PatternElement> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var keyValue: DeserializationResult<Expression<String>> = parent?.key?.value() ?? .noValue
      var placeholderValue: DeserializationResult<Expression<String>> = parent?.placeholder?.value() ?? .noValue
      var regexValue: DeserializationResult<Expression<String>> = parent?.regex?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "key":
          keyValue = deserialize(__dictValue, validator: ResolvedValue.keyValidator).merged(with: keyValue)
        case "placeholder":
          placeholderValue = deserialize(__dictValue, validator: ResolvedValue.placeholderValidator).merged(with: placeholderValue)
        case "regex":
          regexValue = deserialize(__dictValue).merged(with: regexValue)
        case parent?.key?.link:
          keyValue = keyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.keyValidator))
        case parent?.placeholder?.link:
          placeholderValue = placeholderValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.placeholderValidator))
        case parent?.regex?.link:
          regexValue = regexValue.merged(with: deserialize(__dictValue))
        default: break
        }
      }
      var errors = mergeErrors(
        keyValue.errorsOrWarnings?.map { .nestedObjectError(field: "key", error: $0) },
        placeholderValue.errorsOrWarnings?.map { .nestedObjectError(field: "placeholder", error: $0) },
        regexValue.errorsOrWarnings?.map { .nestedObjectError(field: "regex", error: $0) }
      )
      if case .noValue = keyValue {
        errors.append(.requiredFieldIsMissing(field: "key"))
      }
      guard
        let keyNonNil = keyValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivFixedLengthInputMask.PatternElement(
        key: keyNonNil,
        placeholder: placeholderValue.value,
        regex: regexValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> PatternElementTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> PatternElementTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "fixed_length"
  public let parent: String?
  public let alwaysVisible: Field<Expression<Bool>>? // default value: false
  public let pattern: Field<Expression<String>>?
  public let patternElements: Field<[PatternElementTemplate]>? // at least 1 elements
  public let rawTextVariable: Field<String>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type"),
        alwaysVisible: try dictionary.getOptionalExpressionField("always_visible"),
        pattern: try dictionary.getOptionalExpressionField("pattern"),
        patternElements: try dictionary.getOptionalArray("pattern_elements", templateToType: templateToType),
        rawTextVariable: try dictionary.getOptionalField("raw_text_variable")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-fixed-length-input-mask_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    alwaysVisible: Field<Expression<Bool>>? = nil,
    pattern: Field<Expression<String>>? = nil,
    patternElements: Field<[PatternElementTemplate]>? = nil,
    rawTextVariable: Field<String>? = nil
  ) {
    self.parent = parent
    self.alwaysVisible = alwaysVisible
    self.pattern = pattern
    self.patternElements = patternElements
    self.rawTextVariable = rawTextVariable
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivFixedLengthInputMaskTemplate?) -> DeserializationResult<DivFixedLengthInputMask> {
    let alwaysVisibleValue = parent?.alwaysVisible?.resolveOptionalValue(context: context) ?? .noValue
    let patternValue = parent?.pattern?.resolveValue(context: context) ?? .noValue
    let patternElementsValue = parent?.patternElements?.resolveValue(context: context, validator: ResolvedValue.patternElementsValidator, useOnlyLinks: true) ?? .noValue
    let rawTextVariableValue = parent?.rawTextVariable?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      alwaysVisibleValue.errorsOrWarnings?.map { .nestedObjectError(field: "always_visible", error: $0) },
      patternValue.errorsOrWarnings?.map { .nestedObjectError(field: "pattern", error: $0) },
      patternElementsValue.errorsOrWarnings?.map { .nestedObjectError(field: "pattern_elements", error: $0) },
      rawTextVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "raw_text_variable", error: $0) }
    )
    if case .noValue = patternValue {
      errors.append(.requiredFieldIsMissing(field: "pattern"))
    }
    if case .noValue = patternElementsValue {
      errors.append(.requiredFieldIsMissing(field: "pattern_elements"))
    }
    if case .noValue = rawTextVariableValue {
      errors.append(.requiredFieldIsMissing(field: "raw_text_variable"))
    }
    guard
      let patternNonNil = patternValue.value,
      let patternElementsNonNil = patternElementsValue.value,
      let rawTextVariableNonNil = rawTextVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivFixedLengthInputMask(
      alwaysVisible: alwaysVisibleValue.value,
      pattern: patternNonNil,
      patternElements: patternElementsNonNil,
      rawTextVariable: rawTextVariableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivFixedLengthInputMaskTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivFixedLengthInputMask> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var alwaysVisibleValue: DeserializationResult<Expression<Bool>> = parent?.alwaysVisible?.value() ?? .noValue
    var patternValue: DeserializationResult<Expression<String>> = parent?.pattern?.value() ?? .noValue
    var patternElementsValue: DeserializationResult<[DivFixedLengthInputMask.PatternElement]> = .noValue
    var rawTextVariableValue: DeserializationResult<String> = parent?.rawTextVariable?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "always_visible":
        alwaysVisibleValue = deserialize(__dictValue).merged(with: alwaysVisibleValue)
      case "pattern":
        patternValue = deserialize(__dictValue).merged(with: patternValue)
      case "pattern_elements":
        patternElementsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.patternElementsValidator, type: DivFixedLengthInputMaskTemplate.PatternElementTemplate.self).merged(with: patternElementsValue)
      case "raw_text_variable":
        rawTextVariableValue = deserialize(__dictValue).merged(with: rawTextVariableValue)
      case parent?.alwaysVisible?.link:
        alwaysVisibleValue = alwaysVisibleValue.merged(with: deserialize(__dictValue))
      case parent?.pattern?.link:
        patternValue = patternValue.merged(with: deserialize(__dictValue))
      case parent?.patternElements?.link:
        patternElementsValue = patternElementsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.patternElementsValidator, type: DivFixedLengthInputMaskTemplate.PatternElementTemplate.self))
      case parent?.rawTextVariable?.link:
        rawTextVariableValue = rawTextVariableValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    if let parent = parent {
      patternElementsValue = patternElementsValue.merged(with: parent.patternElements?.resolveValue(context: context, validator: ResolvedValue.patternElementsValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      alwaysVisibleValue.errorsOrWarnings?.map { .nestedObjectError(field: "always_visible", error: $0) },
      patternValue.errorsOrWarnings?.map { .nestedObjectError(field: "pattern", error: $0) },
      patternElementsValue.errorsOrWarnings?.map { .nestedObjectError(field: "pattern_elements", error: $0) },
      rawTextVariableValue.errorsOrWarnings?.map { .nestedObjectError(field: "raw_text_variable", error: $0) }
    )
    if case .noValue = patternValue {
      errors.append(.requiredFieldIsMissing(field: "pattern"))
    }
    if case .noValue = patternElementsValue {
      errors.append(.requiredFieldIsMissing(field: "pattern_elements"))
    }
    if case .noValue = rawTextVariableValue {
      errors.append(.requiredFieldIsMissing(field: "raw_text_variable"))
    }
    guard
      let patternNonNil = patternValue.value,
      let patternElementsNonNil = patternElementsValue.value,
      let rawTextVariableNonNil = rawTextVariableValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivFixedLengthInputMask(
      alwaysVisible: alwaysVisibleValue.value,
      pattern: patternNonNil,
      patternElements: patternElementsNonNil,
      rawTextVariable: rawTextVariableNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivFixedLengthInputMaskTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivFixedLengthInputMaskTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivFixedLengthInputMaskTemplate(
      parent: nil,
      alwaysVisible: alwaysVisible ?? mergedParent.alwaysVisible,
      pattern: pattern ?? mergedParent.pattern,
      patternElements: patternElements ?? mergedParent.patternElements,
      rawTextVariable: rawTextVariable ?? mergedParent.rawTextVariable
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivFixedLengthInputMaskTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivFixedLengthInputMaskTemplate(
      parent: nil,
      alwaysVisible: merged.alwaysVisible,
      pattern: merged.pattern,
      patternElements: try merged.patternElements?.resolveParent(templates: templates),
      rawTextVariable: merged.rawTextVariable
    )
  }
}
