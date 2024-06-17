// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivCollectionItemBuilderTemplate: TemplateValue {
  public final class PrototypeTemplate: TemplateValue {
    public let div: Field<DivTemplate>?
    public let id: Field<Expression<String>>?
    public let selector: Field<Expression<Bool>>? // default value: true

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        div: dictionary.getOptionalField("div", templateToType: templateToType),
        id: dictionary.getOptionalExpressionField("id"),
        selector: dictionary.getOptionalExpressionField("selector")
      )
    }

    init(
      div: Field<DivTemplate>? = nil,
      id: Field<Expression<String>>? = nil,
      selector: Field<Expression<Bool>>? = nil
    ) {
      self.div = div
      self.id = id
      self.selector = selector
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: PrototypeTemplate?) -> DeserializationResult<DivCollectionItemBuilder.Prototype> {
      let divValue = parent?.div?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
      let idValue = parent?.id?.resolveOptionalValue(context: context) ?? .noValue
      let selectorValue = parent?.selector?.resolveOptionalValue(context: context) ?? .noValue
      var errors = mergeErrors(
        divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
        idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
        selectorValue.errorsOrWarnings?.map { .nestedObjectError(field: "selector", error: $0) }
      )
      if case .noValue = divValue {
        errors.append(.requiredFieldIsMissing(field: "div"))
      }
      guard
        let divNonNil = divValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivCollectionItemBuilder.Prototype(
        div: divNonNil,
        id: idValue.value,
        selector: selectorValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: PrototypeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCollectionItemBuilder.Prototype> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var divValue: DeserializationResult<Div> = .noValue
      var idValue: DeserializationResult<Expression<String>> = parent?.id?.value() ?? .noValue
      var selectorValue: DeserializationResult<Expression<Bool>> = parent?.selector?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "div":
          divValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: divValue)
        case "id":
          idValue = deserialize(__dictValue).merged(with: idValue)
        case "selector":
          selectorValue = deserialize(__dictValue).merged(with: selectorValue)
        case parent?.div?.link:
          divValue = divValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self) })
        case parent?.id?.link:
          idValue = idValue.merged(with: { deserialize(__dictValue) })
        case parent?.selector?.link:
          selectorValue = selectorValue.merged(with: { deserialize(__dictValue) })
        default: break
        }
      }
      if let parent = parent {
        divValue = divValue.merged(with: { parent.div?.resolveValue(context: context, useOnlyLinks: true) })
      }
      var errors = mergeErrors(
        divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
        idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
        selectorValue.errorsOrWarnings?.map { .nestedObjectError(field: "selector", error: $0) }
      )
      if case .noValue = divValue {
        errors.append(.requiredFieldIsMissing(field: "div"))
      }
      guard
        let divNonNil = divValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivCollectionItemBuilder.Prototype(
        div: divNonNil,
        id: idValue.value,
        selector: selectorValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> PrototypeTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> PrototypeTemplate {
      let merged = try mergedWithParent(templates: templates)

      return PrototypeTemplate(
        div: try merged.div?.resolveParent(templates: templates),
        id: merged.id,
        selector: merged.selector
      )
    }
  }

  public let data: Field<Expression<[Any]>>?
  public let dataElementName: Field<String>? // default value: it
  public let prototypes: Field<[PrototypeTemplate]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      data: dictionary.getOptionalExpressionField("data"),
      dataElementName: dictionary.getOptionalField("data_element_name"),
      prototypes: dictionary.getOptionalArray("prototypes", templateToType: templateToType)
    )
  }

  init(
    data: Field<Expression<[Any]>>? = nil,
    dataElementName: Field<String>? = nil,
    prototypes: Field<[PrototypeTemplate]>? = nil
  ) {
    self.data = data
    self.dataElementName = dataElementName
    self.prototypes = prototypes
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivCollectionItemBuilderTemplate?) -> DeserializationResult<DivCollectionItemBuilder> {
    let dataValue = parent?.data?.resolveValue(context: context) ?? .noValue
    let dataElementNameValue = parent?.dataElementName?.resolveOptionalValue(context: context) ?? .noValue
    let prototypesValue = parent?.prototypes?.resolveValue(context: context, validator: ResolvedValue.prototypesValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      dataValue.errorsOrWarnings?.map { .nestedObjectError(field: "data", error: $0) },
      dataElementNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "data_element_name", error: $0) },
      prototypesValue.errorsOrWarnings?.map { .nestedObjectError(field: "prototypes", error: $0) }
    )
    if case .noValue = dataValue {
      errors.append(.requiredFieldIsMissing(field: "data"))
    }
    if case .noValue = prototypesValue {
      errors.append(.requiredFieldIsMissing(field: "prototypes"))
    }
    guard
      let dataNonNil = dataValue.value,
      let prototypesNonNil = prototypesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivCollectionItemBuilder(
      data: dataNonNil,
      dataElementName: dataElementNameValue.value,
      prototypes: prototypesNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCollectionItemBuilderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCollectionItemBuilder> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var dataValue: DeserializationResult<Expression<[Any]>> = parent?.data?.value() ?? .noValue
    var dataElementNameValue: DeserializationResult<String> = parent?.dataElementName?.value() ?? .noValue
    var prototypesValue: DeserializationResult<[DivCollectionItemBuilder.Prototype]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "data":
        dataValue = deserialize(__dictValue).merged(with: dataValue)
      case "data_element_name":
        dataElementNameValue = deserialize(__dictValue).merged(with: dataElementNameValue)
      case "prototypes":
        prototypesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.prototypesValidator, type: DivCollectionItemBuilderTemplate.PrototypeTemplate.self).merged(with: prototypesValue)
      case parent?.data?.link:
        dataValue = dataValue.merged(with: { deserialize(__dictValue) })
      case parent?.dataElementName?.link:
        dataElementNameValue = dataElementNameValue.merged(with: { deserialize(__dictValue) })
      case parent?.prototypes?.link:
        prototypesValue = prototypesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.prototypesValidator, type: DivCollectionItemBuilderTemplate.PrototypeTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      prototypesValue = prototypesValue.merged(with: { parent.prototypes?.resolveValue(context: context, validator: ResolvedValue.prototypesValidator, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      dataValue.errorsOrWarnings?.map { .nestedObjectError(field: "data", error: $0) },
      dataElementNameValue.errorsOrWarnings?.map { .nestedObjectError(field: "data_element_name", error: $0) },
      prototypesValue.errorsOrWarnings?.map { .nestedObjectError(field: "prototypes", error: $0) }
    )
    if case .noValue = dataValue {
      errors.append(.requiredFieldIsMissing(field: "data"))
    }
    if case .noValue = prototypesValue {
      errors.append(.requiredFieldIsMissing(field: "prototypes"))
    }
    guard
      let dataNonNil = dataValue.value,
      let prototypesNonNil = prototypesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivCollectionItemBuilder(
      data: dataNonNil,
      dataElementName: dataElementNameValue.value,
      prototypes: prototypesNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivCollectionItemBuilderTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivCollectionItemBuilderTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivCollectionItemBuilderTemplate(
      data: merged.data,
      dataElementName: merged.dataElementName,
      prototypes: try merged.prototypes?.resolveParent(templates: templates)
    )
  }
}
