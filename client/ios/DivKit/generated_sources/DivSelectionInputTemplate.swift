// Generated code. Do not modify.

import CommonCore
import Foundation
import Serialization
import TemplatesSupport

public final class DivSelectionInputTemplate: TemplateValue, TemplateDeserializable {
  public final class ItemTemplate: TemplateValue, TemplateDeserializable {
    public let text: Field<Expression<String>>? // at least 1 char
    public let value: Field<Expression<String>>? // at least 1 char

    public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
      do {
        self.init(
          text: try dictionary.getOptionalExpressionField("text"),
          value: try dictionary.getOptionalExpressionField("value")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "item_template." + field, representation: representation)
      }
    }

    init(
      text: Field<Expression<String>>? = nil,
      value: Field<Expression<String>>? = nil
    ) {
      self.text = text
      self.value = value
    }

    private static func resolveOnlyLinks(context: Context, parent: ItemTemplate?) -> DeserializationResult<DivSelectionInput.Item> {
      let textValue = parent?.text?.resolveValue(context: context, validator: ResolvedValue.textValidator) ?? .noValue
      let valueValue = parent?.value?.resolveOptionalValue(context: context, validator: ResolvedValue.valueValidator) ?? .noValue
      var errors = mergeErrors(
        textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) },
        valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
      )
      if case .noValue = textValue {
        errors.append(.requiredFieldIsMissing(field: "text"))
      }
      guard
        let textNonNil = textValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivSelectionInput.Item(
        text: textNonNil,
        value: valueValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: Context, parent: ItemTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSelectionInput.Item> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var textValue: DeserializationResult<Expression<String>> = parent?.text?.value() ?? .noValue
      var valueValue: DeserializationResult<Expression<String>> = parent?.value?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "text":
          textValue = deserialize(__dictValue, validator: ResolvedValue.textValidator).merged(with: textValue)
        case "value":
          valueValue = deserialize(__dictValue, validator: ResolvedValue.valueValidator).merged(with: valueValue)
        case parent?.text?.link:
          textValue = textValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.textValidator))
        case parent?.value?.link:
          valueValue = valueValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.valueValidator))
        default: break
        }
      }
      var errors = mergeErrors(
        textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) },
        valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
      )
      if case .noValue = textValue {
        errors.append(.requiredFieldIsMissing(field: "text"))
      }
      guard
        let textNonNil = textValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivSelectionInput.Item(
        text: textNonNil,
        value: valueValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: Templates) throws -> ItemTemplate {
      return self
    }

    public func resolveParent(templates: Templates) throws -> ItemTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "selection"
  public let parent: String? // at least 1 char
  public let items: Field<[ItemTemplate]>? // at least 1 elements

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        items: try dictionary.getOptionalArray("items", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-selection-input_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    items: Field<[ItemTemplate]>? = nil
  ) {
    self.parent = parent
    self.items = items
  }

  private static func resolveOnlyLinks(context: Context, parent: DivSelectionInputTemplate?) -> DeserializationResult<DivSelectionInput> {
    let itemsValue = parent?.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) }
    )
    if case .noValue = itemsValue {
      errors.append(.requiredFieldIsMissing(field: "items"))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSelectionInput(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: Context, parent: DivSelectionInputTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivSelectionInput> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var itemsValue: DeserializationResult<[DivSelectionInput.Item]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "items":
        itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivSelectionInputTemplate.ItemTemplate.self).merged(with: itemsValue)
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivSelectionInputTemplate.ItemTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      itemsValue = itemsValue.merged(with: parent.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) }
    )
    if case .noValue = itemsValue {
      errors.append(.requiredFieldIsMissing(field: "items"))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivSelectionInput(
      items: itemsNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: Templates) throws -> DivSelectionInputTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivSelectionInputTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivSelectionInputTemplate(
      parent: nil,
      items: items ?? mergedParent.items
    )
  }

  public func resolveParent(templates: Templates) throws -> DivSelectionInputTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivSelectionInputTemplate(
      parent: nil,
      items: try merged.items?.resolveParent(templates: templates)
    )
  }
}
