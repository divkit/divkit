// Generated code. Do not modify.

import CoreFoundation
import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public final class DivTooltipTemplate: TemplateValue, TemplateDeserializable {
  public typealias Position = DivTooltip.Position

  public let animationIn: Field<DivAnimationTemplate>?
  public let animationOut: Field<DivAnimationTemplate>?
  public let div: Field<DivTemplate>?
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 5000
  public let id: Field<String>? // at least 1 char
  public let offset: Field<DivPointTemplate>?
  public let position: Field<Expression<Position>>?

  public convenience init(dictionary: [String: Any], templateToType: TemplateToType) throws {
    do {
      self.init(
        animationIn: try dictionary.getOptionalField(
          "animation_in",
          templateToType: templateToType
        ),
        animationOut: try dictionary.getOptionalField(
          "animation_out",
          templateToType: templateToType
        ),
        div: try dictionary.getOptionalField("div", templateToType: templateToType),
        duration: try dictionary.getOptionalField("duration"),
        id: try dictionary.getOptionalField("id"),
        offset: try dictionary.getOptionalField("offset", templateToType: templateToType),
        position: try dictionary.getOptionalField("position")
      )
    } catch let DeserializationError.invalidFieldRepresentation(
      field: field,
      representation: representation
    ) {
      throw DeserializationError.invalidFieldRepresentation(
        field: "div-tooltip_template." + field,
        representation: representation
      )
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

  private static func resolveOnlyLinks(
    context: Context,
    parent: DivTooltipTemplate?
  ) -> DeserializationResult<DivTooltip> {
    let animationInValue = parent?.animationIn?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.animationInValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let animationOutValue = parent?.animationOut?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.animationOutValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let divValue = parent?.div?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
    let durationValue = parent?.duration?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.durationValidator
    ) ?? .noValue
    let idValue = parent?.id?
      .resolveValue(context: context, validator: ResolvedValue.idValidator) ?? .noValue
    let offsetValue = parent?.offset?.resolveOptionalValue(
      context: context,
      validator: ResolvedValue.offsetValidator,
      useOnlyLinks: true
    ) ?? .noValue
    let positionValue = parent?.position?.resolveValue(context: context) ?? .noValue
    var errors = mergeErrors(
      animationInValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "animation_in", level: .warning)) },
      animationOutValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "animation_out", level: .warning)) },
      divValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "div", level: .error)) },
      durationValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "duration", level: .warning)) },
      idValue.errorsOrWarnings?.map { .right($0.asError(deserializing: "id", level: .error)) },
      offsetValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "offset", level: .warning)) },
      positionValue.errorsOrWarnings?
        .map { .right($0.asError(deserializing: "position", level: .error)) }
    )
    if case .noValue = divValue {
      errors
        .append(.right(FieldError(fieldName: "div", level: .error, error: .requiredFieldIsMissing)))
    }
    if case .noValue = idValue {
      errors
        .append(.right(FieldError(fieldName: "id", level: .error, error: .requiredFieldIsMissing)))
    }
    if case .noValue = positionValue {
      errors
        .append(.right(FieldError(
          fieldName: "position",
          level: .error,
          error: .requiredFieldIsMissing
        )))
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
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(
    context: Context,
    parent: DivTooltipTemplate?,
    useOnlyLinks: Bool
  ) -> DeserializationResult<DivTooltip> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var animationInValue: DeserializationResult<DivAnimation> = .noValue
    var animationOutValue: DeserializationResult<DivAnimation> = .noValue
    var divValue: DeserializationResult<Div> = .noValue
    var durationValue: DeserializationResult<Expression<Int>> = parent?.duration?
      .value() ?? .noValue
    var idValue: DeserializationResult<String> = parent?.id?
      .value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var offsetValue: DeserializationResult<DivPoint> = .noValue
    var positionValue: DeserializationResult<Expression<DivTooltip.Position>> = parent?.position?
      .value() ?? .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "animation_in":
        animationInValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.animationInValidator,
          type: DivAnimationTemplate.self
        ).merged(with: animationInValue)
      case "animation_out":
        animationOutValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.animationOutValidator,
          type: DivAnimationTemplate.self
        ).merged(with: animationOutValue)
      case "div":
        divValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivTemplate.self
        ).merged(with: divValue)
      case "duration":
        durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator)
          .merged(with: durationValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator)
          .merged(with: idValue)
      case "offset":
        offsetValue = deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.offsetValidator,
          type: DivPointTemplate.self
        ).merged(with: offsetValue)
      case "position":
        positionValue = deserialize(__dictValue).merged(with: positionValue)
      case parent?.animationIn?.link:
        animationInValue = animationInValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.animationInValidator,
          type: DivAnimationTemplate.self
        ))
      case parent?.animationOut?.link:
        animationOutValue = animationOutValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.animationOutValidator,
          type: DivAnimationTemplate.self
        ))
      case parent?.div?.link:
        divValue = divValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          type: DivTemplate.self
        ))
      case parent?.duration?.link:
        durationValue = durationValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.durationValidator))
      case parent?.id?.link:
        idValue = idValue
          .merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.offset?.link:
        offsetValue = offsetValue.merged(with: deserialize(
          __dictValue,
          templates: context.templates,
          templateToType: context.templateToType,
          validator: ResolvedValue.offsetValidator,
          type: DivPointTemplate.self
        ))
      case parent?.position?.link:
        positionValue = positionValue.merged(with: deserialize(__dictValue))
      default: break
      }
    }
    if let parent = parent {
      animationInValue = animationInValue.merged(with: parent.animationIn?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.animationInValidator,
        useOnlyLinks: true
      ))
      animationOutValue = animationOutValue.merged(with: parent.animationOut?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.animationOutValidator,
        useOnlyLinks: true
      ))
      divValue = divValue
        .merged(with: parent.div?.resolveValue(context: context, useOnlyLinks: true))
      offsetValue = offsetValue.merged(with: parent.offset?.resolveOptionalValue(
        context: context,
        validator: ResolvedValue.offsetValidator,
        useOnlyLinks: true
      ))
    }
    var errors = mergeErrors(
      animationInValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "animation_in", level: .warning)) },
      animationOutValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "animation_out", level: .warning)) },
      divValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "div", level: .error)) },
      durationValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "duration", level: .warning)) },
      idValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "id", level: .error)) },
      offsetValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "offset", level: .warning)) },
      positionValue.errorsOrWarnings?
        .map { Either.right($0.asError(deserializing: "position", level: .error)) }
    )
    if case .noValue = divValue {
      errors
        .append(.right(FieldError(fieldName: "div", level: .error, error: .requiredFieldIsMissing)))
    }
    if case .noValue = idValue {
      errors
        .append(.right(FieldError(fieldName: "id", level: .error, error: .requiredFieldIsMissing)))
    }
    if case .noValue = positionValue {
      errors
        .append(.right(FieldError(
          fieldName: "position",
          level: .error,
          error: .requiredFieldIsMissing
        )))
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
    return errors
      .isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates _: Templates) throws -> DivTooltipTemplate {
    self
  }

  public func resolveParent(templates: Templates) throws -> DivTooltipTemplate {
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
