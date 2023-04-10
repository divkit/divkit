// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTooltipTemplate: TemplateValue {
  public typealias Position = DivTooltip.Position

  public let animationIn: Field<DivAnimationTemplate>?
  public let animationOut: Field<DivAnimationTemplate>?
  public let div: Field<DivTemplate>?
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 5000
  public let id: Field<String>? // at least 1 char
  public let offset: Field<DivPointTemplate>?
  public let position: Field<Expression<Position>>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        animationIn: try dictionary.getOptionalField("animation_in", templateToType: templateToType),
        animationOut: try dictionary.getOptionalField("animation_out", templateToType: templateToType),
        div: try dictionary.getOptionalField("div", templateToType: templateToType),
        duration: try dictionary.getOptionalExpressionField("duration"),
        id: try dictionary.getOptionalField("id"),
        offset: try dictionary.getOptionalField("offset", templateToType: templateToType),
        position: try dictionary.getOptionalExpressionField("position")
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-tooltip_template." + field, representation: representation)
    }
  }

  init(
    animationIn: Field<DivAnimationTemplate>? = nil,
    animationOut: Field<DivAnimationTemplate>? = nil,
    div: Field<DivTemplate>? = nil,
    duration: Field<Expression<Int>>? = nil,
    id: Field<String>? = nil,
    offset: Field<DivPointTemplate>? = nil,
    position: Field<Expression<Position>>? = nil
  ) {
    self.animationIn = animationIn
    self.animationOut = animationOut
    self.div = div
    self.duration = duration
    self.id = id
    self.offset = offset
    self.position = position
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTooltipTemplate?) -> DeserializationResult<DivTooltip> {
    let animationInValue = parent?.animationIn?.resolveOptionalValue(context: context, validator: ResolvedValue.animationInValidator, useOnlyLinks: true) ?? .noValue
    let animationOutValue = parent?.animationOut?.resolveOptionalValue(context: context, validator: ResolvedValue.animationOutValidator, useOnlyLinks: true) ?? .noValue
    let divValue = parent?.div?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let durationValue = parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue
    let idValue = parent?.id?.resolveValue(context: context, validator: ResolvedValue.idValidator) ?? .noValue
    let offsetValue = parent?.offset?.resolveOptionalValue(context: context, validator: ResolvedValue.offsetValidator, useOnlyLinks: true) ?? .noValue
    let positionValue = parent?.position?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      animationInValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_in", error: $0) },
      animationOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_out", error: $0) },
      divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) },
      positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) }
    )
    if case .noValue = divValue {
      errors.append(.requiredFieldIsMissing(field: "div"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    if case .noValue = positionValue {
      errors.append(.requiredFieldIsMissing(field: "position"))
    }
    guard
      let divNonNil = divValue.value,
      let idNonNil = idValue.value,
      let positionNonNil = positionValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTooltip(
      animationIn: animationInValue.value,
      animationOut: animationOutValue.value,
      div: divNonNil,
      duration: durationValue.value,
      id: idNonNil,
      offset: offsetValue.value,
      position: positionNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTooltipTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTooltip> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var animationInValue: DeserializationResult<DivAnimation> = .noValue
    var animationOutValue: DeserializationResult<DivAnimation> = .noValue
    var divValue: DeserializationResult<Div> = .noValue
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?.value() ?? .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var offsetValue: DeserializationResult<DivPoint> = .noValue
    var positionValue: DeserializationResult<Expression<DivTooltip.Position>> = parent?.position?.value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "animation_in":
        animationInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.animationInValidator, type: DivAnimationTemplate.self).merged(with: animationInValue)
      case "animation_out":
        animationOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.animationOutValidator, type: DivAnimationTemplate.self).merged(with: animationOutValue)
      case "div":
        divValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: divValue)
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator).merged(with: idValue)
      case "offset":
        offsetValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.offsetValidator, type: DivPointTemplate.self).merged(with: offsetValue)
      case "position":
        positionValue = deserialize(__dictValue).merged(with: positionValue)
      case parent?.animationIn?.link:
        animationInValue = animationInValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.animationInValidator, type: DivAnimationTemplate.self))
      case parent?.animationOut?.link:
        animationOutValue = animationOutValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.animationOutValidator, type: DivAnimationTemplate.self))
      case parent?.div?.link:
        divValue = divValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self))
      case parent?.duration?.link:
        durationValue = durationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.durationValidator))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.offset?.link:
        offsetValue = offsetValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.offsetValidator, type: DivPointTemplate.self))
      case parent?.position?.link:
        positionValue = positionValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    if let parent = parent {
      animationInValue = animationInValue.merged(with: parent.animationIn?.resolveOptionalValue(context: context, validator: ResolvedValue.animationInValidator, useOnlyLinks: true))
      animationOutValue = animationOutValue.merged(with: parent.animationOut?.resolveOptionalValue(context: context, validator: ResolvedValue.animationOutValidator, useOnlyLinks: true))
      divValue = divValue.merged(with: parent.div?.resolveValue(context: context, useOnlyLinks: true))
      offsetValue = offsetValue.merged(with: parent.offset?.resolveOptionalValue(context: context, validator: ResolvedValue.offsetValidator, useOnlyLinks: true))
    }
    var errors = mergeErrors(
      animationInValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_in", error: $0) },
      animationOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_out", error: $0) },
      divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) },
      positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) }
    )
    if case .noValue = divValue {
      errors.append(.requiredFieldIsMissing(field: "div"))
    }
    if case .noValue = idValue {
      errors.append(.requiredFieldIsMissing(field: "id"))
    }
    if case .noValue = positionValue {
      errors.append(.requiredFieldIsMissing(field: "position"))
    }
    guard
      let divNonNil = divValue.value,
      let idNonNil = idValue.value,
      let positionNonNil = positionValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTooltip(
      animationIn: animationInValue.value,
      animationOut: animationOutValue.value,
      div: divNonNil,
      duration: durationValue.value,
      id: idNonNil,
      offset: offsetValue.value,
      position: positionNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTooltipTemplate {
    return self
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTooltipTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTooltipTemplate(
      animationIn: merged.animationIn?.tryResolveParent(templates: templates),
      animationOut: merged.animationOut?.tryResolveParent(templates: templates),
      div: try merged.div?.resolveParent(templates: templates),
      duration: merged.duration,
      id: merged.id,
      offset: merged.offset?.tryResolveParent(templates: templates),
      position: merged.position
    )
  }
}
