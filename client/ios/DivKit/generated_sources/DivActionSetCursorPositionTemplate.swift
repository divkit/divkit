// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSetCursorPositionTemplate: TemplateValue, Sendable {
  public final class PositionTemplate: TemplateValue, Sendable {
    public static let type: String = "absolute"
    public let parent: String?
    public let end: Field<Expression<Int>>?
    public let start: Field<Expression<Int>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        parent: dictionary["type"] as? String,
        end: dictionary.getOptionalExpressionField("end"),
        start: dictionary.getOptionalExpressionField("start")
      )
    }

    init(
      parent: String?,
      end: Field<Expression<Int>>? = nil,
      start: Field<Expression<Int>>? = nil
    ) {
      self.parent = parent
      self.end = end
      self.start = start
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: PositionTemplate?) -> DeserializationResult<DivActionSetCursorPosition.Position> {
      let endValue = { parent?.end?.resolveOptionalValue(context: context) ?? .noValue }()
      let startValue = { parent?.start?.resolveValue(context: context) ?? .noValue }()
      var errors = mergeErrors(
        endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) }
      )
      if case .noValue = startValue {
        errors.append(.requiredFieldIsMissing(field: "start"))
      }
      guard
        let startNonNil = startValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivActionSetCursorPosition.Position(
        end: { endValue.value }(),
        start: { startNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: PositionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSetCursorPosition.Position> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var endValue: DeserializationResult<Expression<Int>> = { parent?.end?.value() ?? .noValue }()
      var startValue: DeserializationResult<Expression<Int>> = { parent?.start?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "end" {
             endValue = deserialize(__dictValue).merged(with: endValue)
            }
          }()
          _ = {
            if key == "start" {
             startValue = deserialize(__dictValue).merged(with: startValue)
            }
          }()
          _ = {
           if key == parent?.end?.link {
             endValue = endValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.start?.link {
             startValue = startValue.merged(with: { deserialize(__dictValue) })
            }
          }()
        }
      }()
      var errors = mergeErrors(
        endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) }
      )
      if case .noValue = startValue {
        errors.append(.requiredFieldIsMissing(field: "start"))
      }
      guard
        let startNonNil = startValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivActionSetCursorPosition.Position(
        end: { endValue.value }(),
        start: { startNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> PositionTemplate {
      guard let parent = parent, parent != Self.type else { return self }
      guard let parentTemplate = templates[parent] as? PositionTemplate else {
        throw DeserializationError.unknownType(type: parent)
      }
      let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

      return PositionTemplate(
        parent: nil,
        end: end ?? mergedParent.end,
        start: start ?? mergedParent.start
      )
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> PositionTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public static let type: String = "set_cursor_position"
  public let parent: String?
  public let id: Field<Expression<String>>?
  public let position: Field<PositionTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      id: dictionary.getOptionalExpressionField("id"),
      position: dictionary.getOptionalField("position", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    id: Field<Expression<String>>? = nil,
    position: Field<PositionTemplate>? = nil
  ) {
    self.parent = parent
    self.id = id
    self.position = position
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionSetCursorPositionTemplate?) -> DeserializationResult<DivActionSetCursorPosition> {
    let idValue = { parent?.id?.resolveValue(context: context) ?? .noValue }()
    let positionValue = { parent?.position?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    if case .noValue = positionValue {
      errors.append(.requiredFieldIsMissing(field: "position"))
    }
    guard
      let idNonNil = idValue.value,
      let positionNonNil = positionValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSetCursorPosition(
      id: { idNonNil }(),
      position: { positionNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionSetCursorPositionTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSetCursorPosition> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var idValue: DeserializationResult<Expression<String>> = { parent?.id?.value() ?? .noValue }()
    var positionValue: DeserializationResult<DivActionSetCursorPosition.Position> = .noValue
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
          if key == "position" {
           positionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSetCursorPositionTemplate.PositionTemplate.self).merged(with: positionValue)
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.position?.link {
           positionValue = positionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSetCursorPositionTemplate.PositionTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { positionValue = positionValue.merged(with: { parent.position?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) }
    )
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    if case .noValue = positionValue {
      errors.append(.requiredFieldIsMissing(field: "position"))
    }
    guard
      let idNonNil = idValue.value,
      let positionNonNil = positionValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSetCursorPosition(
      id: { idNonNil }(),
      position: { positionNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionSetCursorPositionTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionSetCursorPositionTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionSetCursorPositionTemplate(
      parent: nil,
      id: id ?? mergedParent.id,
      position: position ?? mergedParent.position
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionSetCursorPositionTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionSetCursorPositionTemplate(
      parent: nil,
      id: merged.id,
      position: try merged.position?.resolveParent(templates: templates)
    )
  }
}
