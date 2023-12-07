// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivPatchTemplate: TemplateValue {
  public final class ChangeTemplate: TemplateValue {
    public let id: Field<String>?
    public let items: Field<[DivTemplate]>? // at least 1 elements

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          id: try dictionary.getOptionalField("id"),
          items: try dictionary.getOptionalArray("items", templateToType: templateToType)
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "change_template." + field, representation: representation)
      }
    }

    init(
      id: Field<String>? = nil,
      items: Field<[DivTemplate]>? = nil
    ) {
      self.id = id
      self.items = items
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ChangeTemplate?) -> DeserializationResult<DivPatch.Change> {
      let idValue = parent?.id?.resolveValue(context: context) ?? .noValue
      let itemsValue = parent?.items?.resolveOptionalValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true) ?? .noValue
      var errors = mergeErrors(
        idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
        itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) }
      )
      if case .noValue = idValue {
        errors.append(.requiredFieldIsMissing(field: "id"))
      }
      guard
        let idNonNil = idValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivPatch.Change(
        id: idNonNil,
        items: itemsValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ChangeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPatch.Change> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var idValue: DeserializationResult<String> = parent?.id?.value() ?? .noValue
      var itemsValue: DeserializationResult<[Div]> = .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "id":
          idValue = deserialize(__dictValue).merged(with: idValue)
        case "items":
          itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivTemplate.self).merged(with: itemsValue)
        case parent?.id?.link:
          idValue = idValue.merged(with: deserialize(__dictValue))
        case parent?.items?.link:
          itemsValue = itemsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivTemplate.self))
        default: break
        }
      }
      if let parent = parent {
        itemsValue = itemsValue.merged(with: parent.items?.resolveOptionalValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true))
      }
      var errors = mergeErrors(
        idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
        itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) }
      )
      if case .noValue = idValue {
        errors.append(.requiredFieldIsMissing(field: "id"))
      }
      guard
        let idNonNil = idValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivPatch.Change(
        id: idNonNil,
        items: itemsValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ChangeTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ChangeTemplate {
      let merged = try mergedWithParent(templates: templates)

      return ChangeTemplate(
        id: merged.id,
        items: merged.items?.tryResolveParent(templates: templates)
      )
    }
  }

  public typealias Mode = DivPatch.Mode

  public let changes: Field<[ChangeTemplate]>? // at least 1 elements
  public let mode: Field<Expression<Mode>>? // default value: partial

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        changes: try dictionary.getOptionalArray("changes", templateToType: templateToType),
        mode: try dictionary.getOptionalExpressionField("mode")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-patch_template." + field, representation: representation)
    }
  }

  init(
    changes: Field<[ChangeTemplate]>? = nil,
    mode: Field<Expression<Mode>>? = nil
  ) {
    self.changes = changes
    self.mode = mode
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivPatchTemplate?) -> DeserializationResult<DivPatch> {
    let changesValue = parent?.changes?.resolveValue(context: context, validator: ResolvedValue.changesValidator, useOnlyLinks: true) ?? .noValue
    let modeValue = parent?.mode?.resolveOptionalValue(context: context, validator: ResolvedValue.modeValidator) ?? .noValue
    var errors = mergeErrors(
      changesValue.errorsOrWarnings?.map { .nestedObjectError(field: "changes", error: $0) },
      modeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mode", error: $0) }
    )
    if case .noValue = changesValue {
      errors.append(.requiredFieldIsMissing(field: "changes"))
    }
    guard
      let changesNonNil = changesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPatch(
      changes: changesNonNil,
      mode: modeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPatchTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPatch> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var changesValue: DeserializationResult<[DivPatch.Change]> = .noValue
    var modeValue: DeserializationResult<Expression<DivPatch.Mode>> = parent?.mode?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "changes":
        changesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.changesValidator, type: DivPatchTemplate.ChangeTemplate.self).merged(with: changesValue)
      case "mode":
        modeValue = deserialize(__dictValue, validator: ResolvedValue.modeValidator).merged(with: modeValue)
      case parent?.changes?.link:
        changesValue = changesValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.changesValidator, type: DivPatchTemplate.ChangeTemplate.self))
      case parent?.mode?.link:
        modeValue = modeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.modeValidator))
      default: break
      }
    }
    if let parent = parent {
      changesValue = changesValue.merged(with: parent.changes?.resolveValue(context: context, validator: ResolvedValue.changesValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      changesValue.errorsOrWarnings?.map { .nestedObjectError(field: "changes", error: $0) },
      modeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mode", error: $0) }
    )
    if case .noValue = changesValue {
      errors.append(.requiredFieldIsMissing(field: "changes"))
    }
    guard
      let changesNonNil = changesValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivPatch(
      changes: changesNonNil,
      mode: modeValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivPatchTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivPatchTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivPatchTemplate(
      changes: try merged.changes?.resolveParent(templates: templates),
      mode: merged.mode
    )
  }
}
