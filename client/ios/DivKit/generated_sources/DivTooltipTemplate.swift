// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTooltipTemplate: TemplateValue, Sendable {
  public typealias Position = DivTooltip.Position

  public let animationIn: Field<DivAnimationTemplate>?
  public let animationOut: Field<DivAnimationTemplate>?
  public let closeByTapOutside: Field<Expression<Bool>>? // default value: true
  public let div: Field<DivTemplate>?
  public let duration: Field<Expression<Int>>? // constraint: number >= 0; default value: 5000
  public let id: Field<String>?
  public let mode: Field<DivTooltipModeTemplate>? // default value: .divTooltipModeModal(DivTooltipModeModal())
  public let offset: Field<DivPointTemplate>?
  public let position: Field<Expression<Position>>?
  public let tapOutsideActions: Field<[DivActionTemplate]>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      animationIn: dictionary.getOptionalField("animation_in", templateToType: templateToType),
      animationOut: dictionary.getOptionalField("animation_out", templateToType: templateToType),
      closeByTapOutside: dictionary.getOptionalExpressionField("close_by_tap_outside"),
      div: dictionary.getOptionalField("div", templateToType: templateToType),
      duration: dictionary.getOptionalExpressionField("duration"),
      id: dictionary.getOptionalField("id"),
      mode: dictionary.getOptionalField("mode", templateToType: templateToType),
      offset: dictionary.getOptionalField("offset", templateToType: templateToType),
      position: dictionary.getOptionalExpressionField("position"),
      tapOutsideActions: dictionary.getOptionalArray("tap_outside_actions", templateToType: templateToType)
    )
  }

  init(
    animationIn: Field<DivAnimationTemplate>? = nil,
    animationOut: Field<DivAnimationTemplate>? = nil,
    closeByTapOutside: Field<Expression<Bool>>? = nil,
    div: Field<DivTemplate>? = nil,
    duration: Field<Expression<Int>>? = nil,
    id: Field<String>? = nil,
    mode: Field<DivTooltipModeTemplate>? = nil,
    offset: Field<DivPointTemplate>? = nil,
    position: Field<Expression<Position>>? = nil,
    tapOutsideActions: Field<[DivActionTemplate]>? = nil
  ) {
    self.animationIn = animationIn
    self.animationOut = animationOut
    self.closeByTapOutside = closeByTapOutside
    self.div = div
    self.duration = duration
    self.id = id
    self.mode = mode
    self.offset = offset
    self.position = position
    self.tapOutsideActions = tapOutsideActions
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTooltipTemplate?) -> DeserializationResult<DivTooltip> {
    let animationInValue = { parent?.animationIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let animationOutValue = { parent?.animationOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let closeByTapOutsideValue = { parent?.closeByTapOutside?.resolveOptionalValue(context: context) ?? .noValue }()
    let divValue = { parent?.div?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let durationValue = { parent?.duration?.resolveOptionalValue(context: context, validator: ResolvedValue.durationValidator) ?? .noValue }()
    let idValue = { parent?.id?.resolveValue(context: context) ?? .noValue }()
    let modeValue = { parent?.mode?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let offsetValue = { parent?.offset?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let positionValue = { parent?.position?.resolveValue(context: context) ?? .noValue }()
    let tapOutsideActionsValue = { parent?.tapOutsideActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      animationInValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_in", error: $0) },
      animationOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_out", error: $0) },
      closeByTapOutsideValue.errorsOrWarnings?.map { .nestedObjectError(field: "close_by_tap_outside", error: $0) },
      divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      modeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mode", error: $0) },
      offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) },
      positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) },
      tapOutsideActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tap_outside_actions", error: $0) }
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
      animationIn: { animationInValue.value }(),
      animationOut: { animationOutValue.value }(),
      closeByTapOutside: { closeByTapOutsideValue.value }(),
      div: { divNonNil }(),
      duration: { durationValue.value }(),
      id: { idNonNil }(),
      mode: { modeValue.value }(),
      offset: { offsetValue.value }(),
      position: { positionNonNil }(),
      tapOutsideActions: { tapOutsideActionsValue.value }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTooltipTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTooltip> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var animationInValue: DeserializationResult<DivAnimation> = .noValue
    var animationOutValue: DeserializationResult<DivAnimation> = .noValue
    var closeByTapOutsideValue: DeserializationResult<Expression<Bool>> = { parent?.closeByTapOutside?.value() ?? .noValue }()
    var divValue: DeserializationResult<Div> = .noValue
    var durationValue: DeserializationResult<Expression<Int>> = { parent?.duration?.value() ?? .noValue }()
    var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
    var modeValue: DeserializationResult<DivTooltipMode> = .noValue
    var offsetValue: DeserializationResult<DivPoint> = .noValue
    var positionValue: DeserializationResult<Expression<DivTooltip.Position>> = { parent?.position?.value() ?? .noValue }()
    var tapOutsideActionsValue: DeserializationResult<[DivAction]> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "animation_in" {
           animationInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self).merged(with: animationInValue)
          }
        }()
        _ = {
          if key == "animation_out" {
           animationOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self).merged(with: animationOutValue)
          }
        }()
        _ = {
          if key == "close_by_tap_outside" {
           closeByTapOutsideValue = deserialize(__dictValue).merged(with: closeByTapOutsideValue)
          }
        }()
        _ = {
          if key == "div" {
           divValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: divValue)
          }
        }()
        _ = {
          if key == "duration" {
           durationValue = deserialize(__dictValue, validator: ResolvedValue.durationValidator).merged(with: durationValue)
          }
        }()
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
          }
        }()
        _ = {
          if key == "mode" {
           modeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipModeTemplate.self).merged(with: modeValue)
          }
        }()
        _ = {
          if key == "offset" {
           offsetValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPointTemplate.self).merged(with: offsetValue)
          }
        }()
        _ = {
          if key == "position" {
           positionValue = deserialize(__dictValue).merged(with: positionValue)
          }
        }()
        _ = {
          if key == "tap_outside_actions" {
           tapOutsideActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: tapOutsideActionsValue)
          }
        }()
        _ = {
         if key == parent?.animationIn?.link {
           animationInValue = animationInValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.animationOut?.link {
           animationOutValue = animationOutValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.closeByTapOutside?.link {
           closeByTapOutsideValue = closeByTapOutsideValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.div?.link {
           divValue = divValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.duration?.link {
           durationValue = durationValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.durationValidator) })
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.mode?.link {
           modeValue = modeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipModeTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.offset?.link {
           offsetValue = offsetValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivPointTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.position?.link {
           positionValue = positionValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.tapOutsideActions?.link {
           tapOutsideActionsValue = tapOutsideActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { animationInValue = animationInValue.merged(with: { parent.animationIn?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { animationOutValue = animationOutValue.merged(with: { parent.animationOut?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { divValue = divValue.merged(with: { parent.div?.resolveValue(context: context, useOnlyLinks: true) }) }()
      _ = { modeValue = modeValue.merged(with: { parent.mode?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { offsetValue = offsetValue.merged(with: { parent.offset?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { tapOutsideActionsValue = tapOutsideActionsValue.merged(with: { parent.tapOutsideActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      animationInValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_in", error: $0) },
      animationOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_out", error: $0) },
      closeByTapOutsideValue.errorsOrWarnings?.map { .nestedObjectError(field: "close_by_tap_outside", error: $0) },
      divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
      durationValue.errorsOrWarnings?.map { .nestedObjectError(field: "duration", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      modeValue.errorsOrWarnings?.map { .nestedObjectError(field: "mode", error: $0) },
      offsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "offset", error: $0) },
      positionValue.errorsOrWarnings?.map { .nestedObjectError(field: "position", error: $0) },
      tapOutsideActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tap_outside_actions", error: $0) }
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
      animationIn: { animationInValue.value }(),
      animationOut: { animationOutValue.value }(),
      closeByTapOutside: { closeByTapOutsideValue.value }(),
      div: { divNonNil }(),
      duration: { durationValue.value }(),
      id: { idNonNil }(),
      mode: { modeValue.value }(),
      offset: { offsetValue.value }(),
      position: { positionNonNil }(),
      tapOutsideActions: { tapOutsideActionsValue.value }()
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
      closeByTapOutside: merged.closeByTapOutside,
      div: try merged.div?.resolveParent(templates: templates),
      duration: merged.duration,
      id: merged.id,
      mode: merged.mode?.tryResolveParent(templates: templates),
      offset: merged.offset?.tryResolveParent(templates: templates),
      position: merged.position,
      tapOutsideActions: merged.tapOutsideActions?.tryResolveParent(templates: templates)
    )
  }
}
