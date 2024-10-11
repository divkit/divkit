// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivPatchTemplate: TemplateValue {
  public final class ChangeTemplate: TemplateValue {
    public let id: Field<String>?
    public let items: Field<[DivTemplate]>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        id: dictionary.getOptionalField("id"),
        items: dictionary.getOptionalArray("items", templateToType: templateToType)
      )
    }

    init(
      id: Field<String>? = nil,
      items: Field<[DivTemplate]>? = nil
    ) {
      self.id = id
      self.items = items
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ChangeTemplate?) -> DeserializationResult<DivPatch.Change> {
      let idValue = { parent?.id?.resolveValue(context: context) ?? .noValue }()
      let itemsValue = { parent?.items?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
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
        id: { idNonNil }(),
        items: { itemsValue.value }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ChangeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPatch.Change> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
      var itemsValue: DeserializationResult<[Div]> = .noValue
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "id" {
             idValue = deserialize(__dictValue).merged(with: idValue)
            }
          }()
          _ = {
            if key == "items" {
             itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: itemsValue)
            }
          }()
          _ = {
           if key == parent?.id?.link {
             idValue = idValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.items?.link {
             itemsValue = itemsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self) })
            }
          }()
        }
      }()
      if let parent = parent {
        _ = { itemsValue = itemsValue.merged(with: { parent.items?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
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
        id: { idNonNil }(),
        items: { itemsValue.value }()
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
  public let onAppliedActions: Field<[DivActionTemplate]>?
  public let onFailedActions: Field<[DivActionTemplate]>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      changes: dictionary.getOptionalArray("changes", templateToType: templateToType),
      mode: dictionary.getOptionalExpressionField("mode"),
      onAppliedActions: dictionary.getOptionalArray("on_applied_actions", templateToType: templateToType),
      onFailedActions: dictionary.getOptionalArray("on_failed_actions", templateToType: templateToType)
    )
  }

  init(
    changes: Field<[ChangeTemplate]>? = nil,
    mode: Field<Expression<Mode>>? = nil,
    onAppliedActions: Field<[DivActionTemplate]>? = nil,
    onFailedActions: Field<[DivActionTemplate]>? = nil
  ) {
    self.changes = changes
    self.mode = mode
    self.onAppliedActions = onAppliedActions
    self.onFailedActions = onFailedActions
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivPatchTemplate?) -> DeserializationResult<DivPatch> {
    let changesValue = { parent?.changes?.resolveValue(context: context, validator: ResolvedValue.changesValidator, useOnlyLinks: true) ?? .noValue }()
    let modeValue = { parent?.mode?.resolveOptionalValue(context: context) ?? .noValue }()
    let onAppliedActionsValue = { parent?.onAppliedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let onFailedActionsValue = { parent?.onFailedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      changesValue.errorsOrWarnings?.map { .nestedObjectError(field: "changes", error: $0) },
      modeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mode", error: $0) },
      onAppliedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_applied_actions", error: $0) },
      onFailedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_failed_actions", error: $0) }
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
      changes: { changesNonNil }(),
      mode: { modeValue.value }(),
      onAppliedActions: { onAppliedActionsValue.value }(),
      onFailedActions: { onFailedActionsValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivPatchTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivPatch> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var changesValue: DeserializationResult<[DivPatch.Change]> = .noValue
    var modeValue: DeserializationResult<Expression<DivPatch.Mode>> = { parent?.mode?.value() ?? .noValue }()
    var onAppliedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var onFailedActionsValue: DeserializationResult<[DivAction]> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "changes" {
           changesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.changesValidator, type: DivPatchTemplate.ChangeTemplate.self).merged(with: changesValue)
          }
        }()
        _ = {
          if key == "mode" {
           modeValue = deserialize(__dictValue).merged(with: modeValue)
          }
        }()
        _ = {
          if key == "on_applied_actions" {
           onAppliedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: onAppliedActionsValue)
          }
        }()
        _ = {
          if key == "on_failed_actions" {
           onFailedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: onFailedActionsValue)
          }
        }()
        _ = {
         if key == parent?.changes?.link {
           changesValue = changesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.changesValidator, type: DivPatchTemplate.ChangeTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.mode?.link {
           modeValue = modeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.onAppliedActions?.link {
           onAppliedActionsValue = onAppliedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.onFailedActions?.link {
           onFailedActionsValue = onFailedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { changesValue = changesValue.merged(with: { parent.changes?.resolveValue(context: context, validator: ResolvedValue.changesValidator, useOnlyLinks: true) }) }()
      _ = { onAppliedActionsValue = onAppliedActionsValue.merged(with: { parent.onAppliedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { onFailedActionsValue = onFailedActionsValue.merged(with: { parent.onFailedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      changesValue.errorsOrWarnings?.map { .nestedObjectError(field: "changes", error: $0) },
      modeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mode", error: $0) },
      onAppliedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_applied_actions", error: $0) },
      onFailedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_failed_actions", error: $0) }
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
      changes: { changesNonNil }(),
      mode: { modeValue.value }(),
      onAppliedActions: { onAppliedActionsValue.value }(),
      onFailedActions: { onFailedActionsValue.value }()
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
      mode: merged.mode,
      onAppliedActions: merged.onAppliedActions?.tryResolveParent(templates: templates),
      onFailedActions: merged.onFailedActions?.tryResolveParent(templates: templates)
    )
  }
}
