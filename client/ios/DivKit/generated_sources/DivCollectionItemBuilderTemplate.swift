// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivCollectionItemBuilderTemplate: TemplateValue {
  public final class PrototypeTemplate: TemplateValue {
    public let div: Field<DivTemplate>?
    public let selector: Field<Expression<Bool>>? // default value: true

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          div: try dictionary.getOptionalField("div", templateToType: templateToType),
          selector: try dictionary.getOptionalExpressionField("selector")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "prototype_template." + field, representation: representation)
      }
    }

    init(
      div: Field<DivTemplate>? = nil,
      selector: Field<Expression<Bool>>? = nil
    ) {
      self.div = div
      self.selector = selector
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: PrototypeTemplate?) -> DeserializationResult<DivCollectionItemBuilder.Prototype> {
      let divValue = parent?.div?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
      let selectorValue = parent?.selector?.resolveOptionalValue(context: context, validator: ResolvedValue.selectorValidator) ?? .noValue
      var errors = mergeErrors(
        divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
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
        selector: selectorValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: PrototypeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCollectionItemBuilder.Prototype> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var divValue: DeserializationResult<Div> = .noValue
      var selectorValue: DeserializationResult<Expression<Bool>> = parent?.selector?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "div":
          divValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: divValue)
        case "selector":
          selectorValue = deserialize(__dictValue, validator: ResolvedValue.selectorValidator).merged(with: selectorValue)
        case parent?.div?.link:
          divValue = divValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self))
        case parent?.selector?.link:
          selectorValue = selectorValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.selectorValidator))
        default: break
        }
      }
      if let parent = parent {
        divValue = divValue.merged(with: parent.div?.resolveValue(context: context, useOnlyLinks: true))
      }
      var errors = mergeErrors(
        divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
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
        selector: merged.selector
      )
    }
  }

  public let data: Field<Expression<[Any]>>?
  public let dataElementPrefix: Field<String>? // default value: it.
  public let prototypes: Field<[PrototypeTemplate]>? // at least 1 elements

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        data: try dictionary.getOptionalExpressionField("data"),
        dataElementPrefix: try dictionary.getOptionalField("data_element_prefix"),
        prototypes: try dictionary.getOptionalArray("prototypes", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-collection-item-builder_template." + field, representation: representation)
    }
  }

  init(
    data: Field<Expression<[Any]>>? = nil,
    dataElementPrefix: Field<String>? = nil,
    prototypes: Field<[PrototypeTemplate]>? = nil
  ) {
    self.data = data
    self.dataElementPrefix = dataElementPrefix
    self.prototypes = prototypes
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivCollectionItemBuilderTemplate?) -> DeserializationResult<DivCollectionItemBuilder> {
    let dataValue = parent?.data?.resolveValue(context: context) ?? .noValue
    let dataElementPrefixValue = parent?.dataElementPrefix?.resolveOptionalValue(context: context, validator: ResolvedValue.dataElementPrefixValidator) ?? .noValue
    let prototypesValue = parent?.prototypes?.resolveValue(context: context, validator: ResolvedValue.prototypesValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      dataValue.errorsOrWarnings?.map { .nestedObjectError(field: "data", error: $0) },
      dataElementPrefixValue.errorsOrWarnings?.map { .nestedObjectError(field: "data_element_prefix", error: $0) },
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
      dataElementPrefix: dataElementPrefixValue.value,
      prototypes: prototypesNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivCollectionItemBuilderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivCollectionItemBuilder> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var dataValue: DeserializationResult<Expression<[Any]>> = parent?.data?.value() ?? .noValue
    var dataElementPrefixValue: DeserializationResult<String> = parent?.dataElementPrefix?.value(validatedBy: ResolvedValue.dataElementPrefixValidator) ?? .noValue
    var prototypesValue: DeserializationResult<[DivCollectionItemBuilder.Prototype]> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "data":
        dataValue = deserialize(__dictValue).merged(with: dataValue)
      case "data_element_prefix":
        dataElementPrefixValue = deserialize(__dictValue, validator: ResolvedValue.dataElementPrefixValidator).merged(with: dataElementPrefixValue)
      case "prototypes":
        prototypesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.prototypesValidator, type: DivCollectionItemBuilderTemplate.PrototypeTemplate.self).merged(with: prototypesValue)
      case parent?.data?.link:
        dataValue = dataValue.merged(with: deserialize(__dictValue))
      case parent?.dataElementPrefix?.link:
        dataElementPrefixValue = dataElementPrefixValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.dataElementPrefixValidator))
      case parent?.prototypes?.link:
        prototypesValue = prototypesValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.prototypesValidator, type: DivCollectionItemBuilderTemplate.PrototypeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      prototypesValue = prototypesValue.merged(with: parent.prototypes?.resolveValue(context: context, validator: ResolvedValue.prototypesValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      dataValue.errorsOrWarnings?.map { .nestedObjectError(field: "data", error: $0) },
      dataElementPrefixValue.errorsOrWarnings?.map { .nestedObjectError(field: "data_element_prefix", error: $0) },
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
      dataElementPrefix: dataElementPrefixValue.value,
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
      dataElementPrefix: merged.dataElementPrefix,
      prototypes: try merged.prototypes?.resolveParent(templates: templates)
    )
  }
}
