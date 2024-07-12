// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextTemplate: TemplateValue {
  public final class EllipsisTemplate: TemplateValue {
    public let actions: Field<[DivActionTemplate]>?
    public let images: Field<[ImageTemplate]>?
    public let ranges: Field<[RangeTemplate]>?
    public let text: Field<Expression<String>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        actions: dictionary.getOptionalArray("actions", templateToType: templateToType),
        images: dictionary.getOptionalArray("images", templateToType: templateToType),
        ranges: dictionary.getOptionalArray("ranges", templateToType: templateToType),
        text: dictionary.getOptionalExpressionField("text")
      )
    }

    init(
      actions: Field<[DivActionTemplate]>? = nil,
      images: Field<[ImageTemplate]>? = nil,
      ranges: Field<[RangeTemplate]>? = nil,
      text: Field<Expression<String>>? = nil
    ) {
      self.actions = actions
      self.images = images
      self.ranges = ranges
      self.text = text
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: EllipsisTemplate?) -> DeserializationResult<DivText.Ellipsis> {
      let actionsValue = parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let imagesValue = parent?.images?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let rangesValue = parent?.ranges?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let textValue = parent?.text?.resolveValue(context: context) ?? .noValue
      var errors = mergeErrors(
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        imagesValue.errorsOrWarnings?.map { .nestedObjectError(field: "images", error: $0) },
        rangesValue.errorsOrWarnings?.map { .nestedObjectError(field: "ranges", error: $0) },
        textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) }
      )
      if case .noValue = textValue {
        errors.append(.requiredFieldIsMissing(field: "text"))
      }
      guard
        let textNonNil = textValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivText.Ellipsis(
        actions: actionsValue.value,
        images: imagesValue.value,
        ranges: rangesValue.value,
        text: textNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: EllipsisTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivText.Ellipsis> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var actionsValue: DeserializationResult<[DivAction]> = .noValue
      var imagesValue: DeserializationResult<[DivText.Image]> = .noValue
      var rangesValue: DeserializationResult<[DivText.Range]> = .noValue
      var textValue: DeserializationResult<Expression<String>> = parent?.text?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "actions":
          actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
        case "images":
          imagesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.self).merged(with: imagesValue)
        case "ranges":
          rangesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.RangeTemplate.self).merged(with: rangesValue)
        case "text":
          textValue = deserialize(__dictValue).merged(with: textValue)
        case parent?.actions?.link:
          actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
        case parent?.images?.link:
          imagesValue = imagesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.self) })
        case parent?.ranges?.link:
          rangesValue = rangesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.RangeTemplate.self) })
        case parent?.text?.link:
          textValue = textValue.merged(with: { deserialize(__dictValue) })
        default: break
        }
      }
      if let parent = parent {
        actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        imagesValue = imagesValue.merged(with: { parent.images?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        rangesValue = rangesValue.merged(with: { parent.ranges?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      }
      var errors = mergeErrors(
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        imagesValue.errorsOrWarnings?.map { .nestedObjectError(field: "images", error: $0) },
        rangesValue.errorsOrWarnings?.map { .nestedObjectError(field: "ranges", error: $0) },
        textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) }
      )
      if case .noValue = textValue {
        errors.append(.requiredFieldIsMissing(field: "text"))
      }
      guard
        let textNonNil = textValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivText.Ellipsis(
        actions: actionsValue.value,
        images: imagesValue.value,
        ranges: rangesValue.value,
        text: textNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> EllipsisTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> EllipsisTemplate {
      let merged = try mergedWithParent(templates: templates)

      return EllipsisTemplate(
        actions: merged.actions?.tryResolveParent(templates: templates),
        images: merged.images?.tryResolveParent(templates: templates),
        ranges: merged.ranges?.tryResolveParent(templates: templates),
        text: merged.text
      )
    }
  }

  public final class ImageTemplate: TemplateValue {
    public let height: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(20))
    public let preloadRequired: Field<Expression<Bool>>? // default value: false
    public let start: Field<Expression<Int>>? // constraint: number >= 0
    public let tintColor: Field<Expression<Color>>?
    public let tintMode: Field<Expression<DivBlendMode>>? // default value: source_in
    public let url: Field<Expression<URL>>?
    public let width: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(20))

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        height: dictionary.getOptionalField("height", templateToType: templateToType),
        preloadRequired: dictionary.getOptionalExpressionField("preload_required"),
        start: dictionary.getOptionalExpressionField("start"),
        tintColor: dictionary.getOptionalExpressionField("tint_color", transform: Color.color(withHexString:)),
        tintMode: dictionary.getOptionalExpressionField("tint_mode"),
        url: dictionary.getOptionalExpressionField("url", transform: URL.init(string:)),
        width: dictionary.getOptionalField("width", templateToType: templateToType)
      )
    }

    init(
      height: Field<DivFixedSizeTemplate>? = nil,
      preloadRequired: Field<Expression<Bool>>? = nil,
      start: Field<Expression<Int>>? = nil,
      tintColor: Field<Expression<Color>>? = nil,
      tintMode: Field<Expression<DivBlendMode>>? = nil,
      url: Field<Expression<URL>>? = nil,
      width: Field<DivFixedSizeTemplate>? = nil
    ) {
      self.height = height
      self.preloadRequired = preloadRequired
      self.start = start
      self.tintColor = tintColor
      self.tintMode = tintMode
      self.url = url
      self.width = width
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ImageTemplate?) -> DeserializationResult<DivText.Image> {
      let heightValue = parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let preloadRequiredValue = parent?.preloadRequired?.resolveOptionalValue(context: context) ?? .noValue
      let startValue = parent?.start?.resolveValue(context: context, validator: ResolvedValue.startValidator) ?? .noValue
      let tintColorValue = parent?.tintColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
      let tintModeValue = parent?.tintMode?.resolveOptionalValue(context: context) ?? .noValue
      let urlValue = parent?.url?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
      let widthValue = parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      var errors = mergeErrors(
        heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
        preloadRequiredValue.errorsOrWarnings?.map { .nestedObjectError(field: "preload_required", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
        tintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "tint_color", error: $0) },
        tintModeValue.errorsOrWarnings?.map { .nestedObjectError(field: "tint_mode", error: $0) },
        urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) },
        widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
      )
      if case .noValue = startValue {
        errors.append(.requiredFieldIsMissing(field: "start"))
      }
      if case .noValue = urlValue {
        errors.append(.requiredFieldIsMissing(field: "url"))
      }
      guard
        let startNonNil = startValue.value,
        let urlNonNil = urlValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivText.Image(
        height: heightValue.value,
        preloadRequired: preloadRequiredValue.value,
        start: startNonNil,
        tintColor: tintColorValue.value,
        tintMode: tintModeValue.value,
        url: urlNonNil,
        width: widthValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ImageTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivText.Image> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var heightValue: DeserializationResult<DivFixedSize> = .noValue
      var preloadRequiredValue: DeserializationResult<Expression<Bool>> = parent?.preloadRequired?.value() ?? .noValue
      var startValue: DeserializationResult<Expression<Int>> = parent?.start?.value() ?? .noValue
      var tintColorValue: DeserializationResult<Expression<Color>> = parent?.tintColor?.value() ?? .noValue
      var tintModeValue: DeserializationResult<Expression<DivBlendMode>> = parent?.tintMode?.value() ?? .noValue
      var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
      var widthValue: DeserializationResult<DivFixedSize> = .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "height":
          heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: heightValue)
        case "preload_required":
          preloadRequiredValue = deserialize(__dictValue).merged(with: preloadRequiredValue)
        case "start":
          startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).merged(with: startValue)
        case "tint_color":
          tintColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: tintColorValue)
        case "tint_mode":
          tintModeValue = deserialize(__dictValue).merged(with: tintModeValue)
        case "url":
          urlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: urlValue)
        case "width":
          widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: widthValue)
        case parent?.height?.link:
          heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
        case parent?.preloadRequired?.link:
          preloadRequiredValue = preloadRequiredValue.merged(with: { deserialize(__dictValue) })
        case parent?.start?.link:
          startValue = startValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startValidator) })
        case parent?.tintColor?.link:
          tintColorValue = tintColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
        case parent?.tintMode?.link:
          tintModeValue = tintModeValue.merged(with: { deserialize(__dictValue) })
        case parent?.url?.link:
          urlValue = urlValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
        case parent?.width?.link:
          widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
        default: break
        }
      }
      if let parent = parent {
        heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      }
      var errors = mergeErrors(
        heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
        preloadRequiredValue.errorsOrWarnings?.map { .nestedObjectError(field: "preload_required", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
        tintColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "tint_color", error: $0) },
        tintModeValue.errorsOrWarnings?.map { .nestedObjectError(field: "tint_mode", error: $0) },
        urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) },
        widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
      )
      if case .noValue = startValue {
        errors.append(.requiredFieldIsMissing(field: "start"))
      }
      if case .noValue = urlValue {
        errors.append(.requiredFieldIsMissing(field: "url"))
      }
      guard
        let startNonNil = startValue.value,
        let urlNonNil = urlValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivText.Image(
        height: heightValue.value,
        preloadRequired: preloadRequiredValue.value,
        start: startNonNil,
        tintColor: tintColorValue.value,
        tintMode: tintModeValue.value,
        url: urlNonNil,
        width: widthValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ImageTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ImageTemplate {
      let merged = try mergedWithParent(templates: templates)

      return ImageTemplate(
        height: merged.height?.tryResolveParent(templates: templates),
        preloadRequired: merged.preloadRequired,
        start: merged.start,
        tintColor: merged.tintColor,
        tintMode: merged.tintMode,
        url: merged.url,
        width: merged.width?.tryResolveParent(templates: templates)
      )
    }
  }

  public final class RangeTemplate: TemplateValue {
    public let actions: Field<[DivActionTemplate]>?
    public let background: Field<DivTextRangeBackgroundTemplate>?
    public let border: Field<DivTextRangeBorderTemplate>?
    public let end: Field<Expression<Int>>? // constraint: number > 0
    public let fontFamily: Field<Expression<String>>?
    public let fontFeatureSettings: Field<Expression<String>>?
    public let fontSize: Field<Expression<Int>>? // constraint: number >= 0
    public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
    public let fontWeight: Field<Expression<DivFontWeight>>?
    public let fontWeightValue: Field<Expression<Int>>? // constraint: number > 0
    public let letterSpacing: Field<Expression<Double>>?
    public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
    public let start: Field<Expression<Int>>? // constraint: number >= 0
    public let strike: Field<Expression<DivLineStyle>>?
    public let textColor: Field<Expression<Color>>?
    public let textShadow: Field<DivShadowTemplate>?
    public let topOffset: Field<Expression<Int>>? // constraint: number >= 0
    public let underline: Field<Expression<DivLineStyle>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        actions: dictionary.getOptionalArray("actions", templateToType: templateToType),
        background: dictionary.getOptionalField("background", templateToType: templateToType),
        border: dictionary.getOptionalField("border", templateToType: templateToType),
        end: dictionary.getOptionalExpressionField("end"),
        fontFamily: dictionary.getOptionalExpressionField("font_family"),
        fontFeatureSettings: dictionary.getOptionalExpressionField("font_feature_settings"),
        fontSize: dictionary.getOptionalExpressionField("font_size"),
        fontSizeUnit: dictionary.getOptionalExpressionField("font_size_unit"),
        fontWeight: dictionary.getOptionalExpressionField("font_weight"),
        fontWeightValue: dictionary.getOptionalExpressionField("font_weight_value"),
        letterSpacing: dictionary.getOptionalExpressionField("letter_spacing"),
        lineHeight: dictionary.getOptionalExpressionField("line_height"),
        start: dictionary.getOptionalExpressionField("start"),
        strike: dictionary.getOptionalExpressionField("strike"),
        textColor: dictionary.getOptionalExpressionField("text_color", transform: Color.color(withHexString:)),
        textShadow: dictionary.getOptionalField("text_shadow", templateToType: templateToType),
        topOffset: dictionary.getOptionalExpressionField("top_offset"),
        underline: dictionary.getOptionalExpressionField("underline")
      )
    }

    init(
      actions: Field<[DivActionTemplate]>? = nil,
      background: Field<DivTextRangeBackgroundTemplate>? = nil,
      border: Field<DivTextRangeBorderTemplate>? = nil,
      end: Field<Expression<Int>>? = nil,
      fontFamily: Field<Expression<String>>? = nil,
      fontFeatureSettings: Field<Expression<String>>? = nil,
      fontSize: Field<Expression<Int>>? = nil,
      fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
      fontWeight: Field<Expression<DivFontWeight>>? = nil,
      fontWeightValue: Field<Expression<Int>>? = nil,
      letterSpacing: Field<Expression<Double>>? = nil,
      lineHeight: Field<Expression<Int>>? = nil,
      start: Field<Expression<Int>>? = nil,
      strike: Field<Expression<DivLineStyle>>? = nil,
      textColor: Field<Expression<Color>>? = nil,
      textShadow: Field<DivShadowTemplate>? = nil,
      topOffset: Field<Expression<Int>>? = nil,
      underline: Field<Expression<DivLineStyle>>? = nil
    ) {
      self.actions = actions
      self.background = background
      self.border = border
      self.end = end
      self.fontFamily = fontFamily
      self.fontFeatureSettings = fontFeatureSettings
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit
      self.fontWeight = fontWeight
      self.fontWeightValue = fontWeightValue
      self.letterSpacing = letterSpacing
      self.lineHeight = lineHeight
      self.start = start
      self.strike = strike
      self.textColor = textColor
      self.textShadow = textShadow
      self.topOffset = topOffset
      self.underline = underline
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: RangeTemplate?) -> DeserializationResult<DivText.Range> {
      let actionsValue = parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let backgroundValue = parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let borderValue = parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let endValue = parent?.end?.resolveValue(context: context, validator: ResolvedValue.endValidator) ?? .noValue
      let fontFamilyValue = parent?.fontFamily?.resolveOptionalValue(context: context) ?? .noValue
      let fontFeatureSettingsValue = parent?.fontFeatureSettings?.resolveOptionalValue(context: context) ?? .noValue
      let fontSizeValue = parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue
      let fontSizeUnitValue = parent?.fontSizeUnit?.resolveOptionalValue(context: context) ?? .noValue
      let fontWeightValue = parent?.fontWeight?.resolveOptionalValue(context: context) ?? .noValue
      let fontWeightValueValue = parent?.fontWeightValue?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValueValidator) ?? .noValue
      let letterSpacingValue = parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue
      let lineHeightValue = parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue
      let startValue = parent?.start?.resolveValue(context: context, validator: ResolvedValue.startValidator) ?? .noValue
      let strikeValue = parent?.strike?.resolveOptionalValue(context: context) ?? .noValue
      let textColorValue = parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
      let textShadowValue = parent?.textShadow?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let topOffsetValue = parent?.topOffset?.resolveOptionalValue(context: context, validator: ResolvedValue.topOffsetValidator) ?? .noValue
      let underlineValue = parent?.underline?.resolveOptionalValue(context: context) ?? .noValue
      var errors = mergeErrors(
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
        borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
        endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
        fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
        fontFeatureSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_feature_settings", error: $0) },
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
        letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
        lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
        strikeValue.errorsOrWarnings?.map { .nestedObjectError(field: "strike", error: $0) },
        textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
        textShadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_shadow", error: $0) },
        topOffsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "top_offset", error: $0) },
        underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) }
      )
      if case .noValue = endValue {
        errors.append(.requiredFieldIsMissing(field: "end"))
      }
      if case .noValue = startValue {
        errors.append(.requiredFieldIsMissing(field: "start"))
      }
      guard
        let endNonNil = endValue.value,
        let startNonNil = startValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivText.Range(
        actions: actionsValue.value,
        background: backgroundValue.value,
        border: borderValue.value,
        end: endNonNil,
        fontFamily: fontFamilyValue.value,
        fontFeatureSettings: fontFeatureSettingsValue.value,
        fontSize: fontSizeValue.value,
        fontSizeUnit: fontSizeUnitValue.value,
        fontWeight: fontWeightValue.value,
        fontWeightValue: fontWeightValueValue.value,
        letterSpacing: letterSpacingValue.value,
        lineHeight: lineHeightValue.value,
        start: startNonNil,
        strike: strikeValue.value,
        textColor: textColorValue.value,
        textShadow: textShadowValue.value,
        topOffset: topOffsetValue.value,
        underline: underlineValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: RangeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivText.Range> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var actionsValue: DeserializationResult<[DivAction]> = .noValue
      var backgroundValue: DeserializationResult<DivTextRangeBackground> = .noValue
      var borderValue: DeserializationResult<DivTextRangeBorder> = .noValue
      var endValue: DeserializationResult<Expression<Int>> = parent?.end?.value() ?? .noValue
      var fontFamilyValue: DeserializationResult<Expression<String>> = parent?.fontFamily?.value() ?? .noValue
      var fontFeatureSettingsValue: DeserializationResult<Expression<String>> = parent?.fontFeatureSettings?.value() ?? .noValue
      var fontSizeValue: DeserializationResult<Expression<Int>> = parent?.fontSize?.value() ?? .noValue
      var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.fontSizeUnit?.value() ?? .noValue
      var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.fontWeight?.value() ?? .noValue
      var fontWeightValueValue: DeserializationResult<Expression<Int>> = parent?.fontWeightValue?.value() ?? .noValue
      var letterSpacingValue: DeserializationResult<Expression<Double>> = parent?.letterSpacing?.value() ?? .noValue
      var lineHeightValue: DeserializationResult<Expression<Int>> = parent?.lineHeight?.value() ?? .noValue
      var startValue: DeserializationResult<Expression<Int>> = parent?.start?.value() ?? .noValue
      var strikeValue: DeserializationResult<Expression<DivLineStyle>> = parent?.strike?.value() ?? .noValue
      var textColorValue: DeserializationResult<Expression<Color>> = parent?.textColor?.value() ?? .noValue
      var textShadowValue: DeserializationResult<DivShadow> = .noValue
      var topOffsetValue: DeserializationResult<Expression<Int>> = parent?.topOffset?.value() ?? .noValue
      var underlineValue: DeserializationResult<Expression<DivLineStyle>> = parent?.underline?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "actions":
          actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
        case "background":
          backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeBackgroundTemplate.self).merged(with: backgroundValue)
        case "border":
          borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeBorderTemplate.self).merged(with: borderValue)
        case "end":
          endValue = deserialize(__dictValue, validator: ResolvedValue.endValidator).merged(with: endValue)
        case "font_family":
          fontFamilyValue = deserialize(__dictValue).merged(with: fontFamilyValue)
        case "font_feature_settings":
          fontFeatureSettingsValue = deserialize(__dictValue).merged(with: fontFeatureSettingsValue)
        case "font_size":
          fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
        case "font_size_unit":
          fontSizeUnitValue = deserialize(__dictValue).merged(with: fontSizeUnitValue)
        case "font_weight":
          fontWeightValue = deserialize(__dictValue).merged(with: fontWeightValue)
        case "font_weight_value":
          fontWeightValueValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator).merged(with: fontWeightValueValue)
        case "letter_spacing":
          letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
        case "line_height":
          lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
        case "start":
          startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).merged(with: startValue)
        case "strike":
          strikeValue = deserialize(__dictValue).merged(with: strikeValue)
        case "text_color":
          textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: textColorValue)
        case "text_shadow":
          textShadowValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self).merged(with: textShadowValue)
        case "top_offset":
          topOffsetValue = deserialize(__dictValue, validator: ResolvedValue.topOffsetValidator).merged(with: topOffsetValue)
        case "underline":
          underlineValue = deserialize(__dictValue).merged(with: underlineValue)
        case parent?.actions?.link:
          actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
        case parent?.background?.link:
          backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeBackgroundTemplate.self) })
        case parent?.border?.link:
          borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeBorderTemplate.self) })
        case parent?.end?.link:
          endValue = endValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.endValidator) })
        case parent?.fontFamily?.link:
          fontFamilyValue = fontFamilyValue.merged(with: { deserialize(__dictValue) })
        case parent?.fontFeatureSettings?.link:
          fontFeatureSettingsValue = fontFeatureSettingsValue.merged(with: { deserialize(__dictValue) })
        case parent?.fontSize?.link:
          fontSizeValue = fontSizeValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator) })
        case parent?.fontSizeUnit?.link:
          fontSizeUnitValue = fontSizeUnitValue.merged(with: { deserialize(__dictValue) })
        case parent?.fontWeight?.link:
          fontWeightValue = fontWeightValue.merged(with: { deserialize(__dictValue) })
        case parent?.fontWeightValue?.link:
          fontWeightValueValue = fontWeightValueValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator) })
        case parent?.letterSpacing?.link:
          letterSpacingValue = letterSpacingValue.merged(with: { deserialize(__dictValue) })
        case parent?.lineHeight?.link:
          lineHeightValue = lineHeightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator) })
        case parent?.start?.link:
          startValue = startValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startValidator) })
        case parent?.strike?.link:
          strikeValue = strikeValue.merged(with: { deserialize(__dictValue) })
        case parent?.textColor?.link:
          textColorValue = textColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
        case parent?.textShadow?.link:
          textShadowValue = textShadowValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self) })
        case parent?.topOffset?.link:
          topOffsetValue = topOffsetValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.topOffsetValidator) })
        case parent?.underline?.link:
          underlineValue = underlineValue.merged(with: { deserialize(__dictValue) })
        default: break
        }
      }
      if let parent = parent {
        actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        textShadowValue = textShadowValue.merged(with: { parent.textShadow?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      }
      var errors = mergeErrors(
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
        borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
        endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
        fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
        fontFeatureSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_feature_settings", error: $0) },
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
        letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
        lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
        strikeValue.errorsOrWarnings?.map { .nestedObjectError(field: "strike", error: $0) },
        textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
        textShadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_shadow", error: $0) },
        topOffsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "top_offset", error: $0) },
        underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) }
      )
      if case .noValue = endValue {
        errors.append(.requiredFieldIsMissing(field: "end"))
      }
      if case .noValue = startValue {
        errors.append(.requiredFieldIsMissing(field: "start"))
      }
      guard
        let endNonNil = endValue.value,
        let startNonNil = startValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivText.Range(
        actions: actionsValue.value,
        background: backgroundValue.value,
        border: borderValue.value,
        end: endNonNil,
        fontFamily: fontFamilyValue.value,
        fontFeatureSettings: fontFeatureSettingsValue.value,
        fontSize: fontSizeValue.value,
        fontSizeUnit: fontSizeUnitValue.value,
        fontWeight: fontWeightValue.value,
        fontWeightValue: fontWeightValueValue.value,
        letterSpacing: letterSpacingValue.value,
        lineHeight: lineHeightValue.value,
        start: startNonNil,
        strike: strikeValue.value,
        textColor: textColorValue.value,
        textShadow: textShadowValue.value,
        topOffset: topOffsetValue.value,
        underline: underlineValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> RangeTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> RangeTemplate {
      let merged = try mergedWithParent(templates: templates)

      return RangeTemplate(
        actions: merged.actions?.tryResolveParent(templates: templates),
        background: merged.background?.tryResolveParent(templates: templates),
        border: merged.border?.tryResolveParent(templates: templates),
        end: merged.end,
        fontFamily: merged.fontFamily,
        fontFeatureSettings: merged.fontFeatureSettings,
        fontSize: merged.fontSize,
        fontSizeUnit: merged.fontSizeUnit,
        fontWeight: merged.fontWeight,
        fontWeightValue: merged.fontWeightValue,
        letterSpacing: merged.letterSpacing,
        lineHeight: merged.lineHeight,
        start: merged.start,
        strike: merged.strike,
        textColor: merged.textColor,
        textShadow: merged.textShadow?.tryResolveParent(templates: templates),
        topOffset: merged.topOffset,
        underline: merged.underline
      )
    }
  }

  public static let type: String = "text"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let action: Field<DivActionTemplate>?
  public let actionAnimation: Field<DivAnimationTemplate>? // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: Field<[DivActionTemplate]>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let autoEllipsize: Field<Expression<Bool>>?
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let disappearActions: Field<[DivDisappearActionTemplate]>?
  public let doubletapActions: Field<[DivActionTemplate]>?
  public let ellipsis: Field<EllipsisTemplate>?
  public let extensions: Field<[DivExtensionTemplate]>?
  public let focus: Field<DivFocusTemplate>?
  public let focusedTextColor: Field<Expression<Color>>?
  public let fontFamily: Field<Expression<String>>?
  public let fontFeatureSettings: Field<Expression<String>>?
  public let fontSize: Field<Expression<Int>>? // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
  public let fontWeight: Field<Expression<DivFontWeight>>? // default value: regular
  public let fontWeightValue: Field<Expression<Int>>? // constraint: number > 0
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>?
  public let images: Field<[ImageTemplate]>?
  public let layoutProvider: Field<DivLayoutProviderTemplate>?
  public let letterSpacing: Field<Expression<Double>>? // default value: 0
  public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
  public let longtapActions: Field<[DivActionTemplate]>?
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let maxLines: Field<Expression<Int>>? // constraint: number >= 0
  public let minHiddenLines: Field<Expression<Int>>? // constraint: number >= 0
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let ranges: Field<[RangeTemplate]>?
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectable: Field<Expression<Bool>>? // default value: false
  public let selectedActions: Field<[DivActionTemplate]>?
  public let strike: Field<Expression<DivLineStyle>>? // default value: none
  public let text: Field<Expression<String>>?
  public let textAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? // default value: start
  public let textAlignmentVertical: Field<Expression<DivAlignmentVertical>>? // default value: top
  public let textColor: Field<Expression<Color>>? // default value: #FF000000
  public let textGradient: Field<DivTextGradientTemplate>?
  public let textShadow: Field<DivShadowTemplate>?
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let underline: Field<Expression<DivLineStyle>>? // default value: none
  public let variables: Field<[DivVariableTemplate]>?
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>?
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      accessibility: dictionary.getOptionalField("accessibility", templateToType: templateToType),
      action: dictionary.getOptionalField("action", templateToType: templateToType),
      actionAnimation: dictionary.getOptionalField("action_animation", templateToType: templateToType),
      actions: dictionary.getOptionalArray("actions", templateToType: templateToType),
      alignmentHorizontal: dictionary.getOptionalExpressionField("alignment_horizontal"),
      alignmentVertical: dictionary.getOptionalExpressionField("alignment_vertical"),
      alpha: dictionary.getOptionalExpressionField("alpha"),
      autoEllipsize: dictionary.getOptionalExpressionField("auto_ellipsize"),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      columnSpan: dictionary.getOptionalExpressionField("column_span"),
      disappearActions: dictionary.getOptionalArray("disappear_actions", templateToType: templateToType),
      doubletapActions: dictionary.getOptionalArray("doubletap_actions", templateToType: templateToType),
      ellipsis: dictionary.getOptionalField("ellipsis", templateToType: templateToType),
      extensions: dictionary.getOptionalArray("extensions", templateToType: templateToType),
      focus: dictionary.getOptionalField("focus", templateToType: templateToType),
      focusedTextColor: dictionary.getOptionalExpressionField("focused_text_color", transform: Color.color(withHexString:)),
      fontFamily: dictionary.getOptionalExpressionField("font_family"),
      fontFeatureSettings: dictionary.getOptionalExpressionField("font_feature_settings"),
      fontSize: dictionary.getOptionalExpressionField("font_size"),
      fontSizeUnit: dictionary.getOptionalExpressionField("font_size_unit"),
      fontWeight: dictionary.getOptionalExpressionField("font_weight"),
      fontWeightValue: dictionary.getOptionalExpressionField("font_weight_value"),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      id: dictionary.getOptionalField("id"),
      images: dictionary.getOptionalArray("images", templateToType: templateToType),
      layoutProvider: dictionary.getOptionalField("layout_provider", templateToType: templateToType),
      letterSpacing: dictionary.getOptionalExpressionField("letter_spacing"),
      lineHeight: dictionary.getOptionalExpressionField("line_height"),
      longtapActions: dictionary.getOptionalArray("longtap_actions", templateToType: templateToType),
      margins: dictionary.getOptionalField("margins", templateToType: templateToType),
      maxLines: dictionary.getOptionalExpressionField("max_lines"),
      minHiddenLines: dictionary.getOptionalExpressionField("min_hidden_lines"),
      paddings: dictionary.getOptionalField("paddings", templateToType: templateToType),
      ranges: dictionary.getOptionalArray("ranges", templateToType: templateToType),
      rowSpan: dictionary.getOptionalExpressionField("row_span"),
      selectable: dictionary.getOptionalExpressionField("selectable"),
      selectedActions: dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
      strike: dictionary.getOptionalExpressionField("strike"),
      text: dictionary.getOptionalExpressionField("text"),
      textAlignmentHorizontal: dictionary.getOptionalExpressionField("text_alignment_horizontal"),
      textAlignmentVertical: dictionary.getOptionalExpressionField("text_alignment_vertical"),
      textColor: dictionary.getOptionalExpressionField("text_color", transform: Color.color(withHexString:)),
      textGradient: dictionary.getOptionalField("text_gradient", templateToType: templateToType),
      textShadow: dictionary.getOptionalField("text_shadow", templateToType: templateToType),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      underline: dictionary.getOptionalExpressionField("underline"),
      variables: dictionary.getOptionalArray("variables", templateToType: templateToType),
      visibility: dictionary.getOptionalExpressionField("visibility"),
      visibilityAction: dictionary.getOptionalField("visibility_action", templateToType: templateToType),
      visibilityActions: dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
      width: dictionary.getOptionalField("width", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    action: Field<DivActionTemplate>? = nil,
    actionAnimation: Field<DivAnimationTemplate>? = nil,
    actions: Field<[DivActionTemplate]>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    autoEllipsize: Field<Expression<Bool>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    disappearActions: Field<[DivDisappearActionTemplate]>? = nil,
    doubletapActions: Field<[DivActionTemplate]>? = nil,
    ellipsis: Field<EllipsisTemplate>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    focusedTextColor: Field<Expression<Color>>? = nil,
    fontFamily: Field<Expression<String>>? = nil,
    fontFeatureSettings: Field<Expression<String>>? = nil,
    fontSize: Field<Expression<Int>>? = nil,
    fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
    fontWeight: Field<Expression<DivFontWeight>>? = nil,
    fontWeightValue: Field<Expression<Int>>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    images: Field<[ImageTemplate]>? = nil,
    layoutProvider: Field<DivLayoutProviderTemplate>? = nil,
    letterSpacing: Field<Expression<Double>>? = nil,
    lineHeight: Field<Expression<Int>>? = nil,
    longtapActions: Field<[DivActionTemplate]>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    maxLines: Field<Expression<Int>>? = nil,
    minHiddenLines: Field<Expression<Int>>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    ranges: Field<[RangeTemplate]>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectable: Field<Expression<Bool>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    strike: Field<Expression<DivLineStyle>>? = nil,
    text: Field<Expression<String>>? = nil,
    textAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    textAlignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    textColor: Field<Expression<Color>>? = nil,
    textGradient: Field<DivTextGradientTemplate>? = nil,
    textShadow: Field<DivShadowTemplate>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    underline: Field<Expression<DivLineStyle>>? = nil,
    variables: Field<[DivVariableTemplate]>? = nil,
    visibility: Field<Expression<DivVisibility>>? = nil,
    visibilityAction: Field<DivVisibilityActionTemplate>? = nil,
    visibilityActions: Field<[DivVisibilityActionTemplate]>? = nil,
    width: Field<DivSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.accessibility = accessibility
    self.action = action
    self.actionAnimation = actionAnimation
    self.actions = actions
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.autoEllipsize = autoEllipsize
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.disappearActions = disappearActions
    self.doubletapActions = doubletapActions
    self.ellipsis = ellipsis
    self.extensions = extensions
    self.focus = focus
    self.focusedTextColor = focusedTextColor
    self.fontFamily = fontFamily
    self.fontFeatureSettings = fontFeatureSettings
    self.fontSize = fontSize
    self.fontSizeUnit = fontSizeUnit
    self.fontWeight = fontWeight
    self.fontWeightValue = fontWeightValue
    self.height = height
    self.id = id
    self.images = images
    self.layoutProvider = layoutProvider
    self.letterSpacing = letterSpacing
    self.lineHeight = lineHeight
    self.longtapActions = longtapActions
    self.margins = margins
    self.maxLines = maxLines
    self.minHiddenLines = minHiddenLines
    self.paddings = paddings
    self.ranges = ranges
    self.rowSpan = rowSpan
    self.selectable = selectable
    self.selectedActions = selectedActions
    self.strike = strike
    self.text = text
    self.textAlignmentHorizontal = textAlignmentHorizontal
    self.textAlignmentVertical = textAlignmentVertical
    self.textColor = textColor
    self.textGradient = textGradient
    self.textShadow = textShadow
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.underline = underline
    self.variables = variables
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTextTemplate?) -> DeserializationResult<DivText> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let actionValue = parent?.action?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let actionAnimationValue = parent?.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let actionsValue = parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let autoEllipsizeValue = parent?.autoEllipsize?.resolveOptionalValue(context: context) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let disappearActionsValue = parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let doubletapActionsValue = parent?.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let ellipsisValue = parent?.ellipsis?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let focusedTextColorValue = parent?.focusedTextColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
    let fontFamilyValue = parent?.fontFamily?.resolveOptionalValue(context: context) ?? .noValue
    let fontFeatureSettingsValue = parent?.fontFeatureSettings?.resolveOptionalValue(context: context) ?? .noValue
    let fontSizeValue = parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue
    let fontSizeUnitValue = parent?.fontSizeUnit?.resolveOptionalValue(context: context) ?? .noValue
    let fontWeightValue = parent?.fontWeight?.resolveOptionalValue(context: context) ?? .noValue
    let fontWeightValueValue = parent?.fontWeightValue?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValueValidator) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context) ?? .noValue
    let imagesValue = parent?.images?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let layoutProviderValue = parent?.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let letterSpacingValue = parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue
    let lineHeightValue = parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue
    let longtapActionsValue = parent?.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let maxLinesValue = parent?.maxLines?.resolveOptionalValue(context: context, validator: ResolvedValue.maxLinesValidator) ?? .noValue
    let minHiddenLinesValue = parent?.minHiddenLines?.resolveOptionalValue(context: context, validator: ResolvedValue.minHiddenLinesValidator) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let rangesValue = parent?.ranges?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let selectableValue = parent?.selectable?.resolveOptionalValue(context: context) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let strikeValue = parent?.strike?.resolveOptionalValue(context: context) ?? .noValue
    let textValue = parent?.text?.resolveValue(context: context) ?? .noValue
    let textAlignmentHorizontalValue = parent?.textAlignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue
    let textAlignmentVerticalValue = parent?.textAlignmentVertical?.resolveOptionalValue(context: context) ?? .noValue
    let textColorValue = parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue
    let textGradientValue = parent?.textGradient?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let textShadowValue = parent?.textShadow?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let underlineValue = parent?.underline?.resolveOptionalValue(context: context) ?? .noValue
    let variablesValue = parent?.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      autoEllipsizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "auto_ellipsize", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      ellipsisValue.errorsOrWarnings?.map { .nestedObjectError(field: "ellipsis", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      focusedTextColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "focused_text_color", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontFeatureSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_feature_settings", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      imagesValue.errorsOrWarnings?.map { .nestedObjectError(field: "images", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
      lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      maxLinesValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_lines", error: $0) },
      minHiddenLinesValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_hidden_lines", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rangesValue.errorsOrWarnings?.map { .nestedObjectError(field: "ranges", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectableValue.errorsOrWarnings?.map { .nestedObjectError(field: "selectable", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      strikeValue.errorsOrWarnings?.map { .nestedObjectError(field: "strike", error: $0) },
      textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) },
      textAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_alignment_horizontal", error: $0) },
      textAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_alignment_vertical", error: $0) },
      textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
      textGradientValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_gradient", error: $0) },
      textShadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_shadow", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = textValue {
      errors.append(.requiredFieldIsMissing(field: "text"))
    }
    guard
      let textNonNil = textValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivText(
      accessibility: accessibilityValue.value,
      action: actionValue.value,
      actionAnimation: actionAnimationValue.value,
      actions: actionsValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      autoEllipsize: autoEllipsizeValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      disappearActions: disappearActionsValue.value,
      doubletapActions: doubletapActionsValue.value,
      ellipsis: ellipsisValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      focusedTextColor: focusedTextColorValue.value,
      fontFamily: fontFamilyValue.value,
      fontFeatureSettings: fontFeatureSettingsValue.value,
      fontSize: fontSizeValue.value,
      fontSizeUnit: fontSizeUnitValue.value,
      fontWeight: fontWeightValue.value,
      fontWeightValue: fontWeightValueValue.value,
      height: heightValue.value,
      id: idValue.value,
      images: imagesValue.value,
      layoutProvider: layoutProviderValue.value,
      letterSpacing: letterSpacingValue.value,
      lineHeight: lineHeightValue.value,
      longtapActions: longtapActionsValue.value,
      margins: marginsValue.value,
      maxLines: maxLinesValue.value,
      minHiddenLines: minHiddenLinesValue.value,
      paddings: paddingsValue.value,
      ranges: rangesValue.value,
      rowSpan: rowSpanValue.value,
      selectable: selectableValue.value,
      selectedActions: selectedActionsValue.value,
      strike: strikeValue.value,
      text: textNonNil,
      textAlignmentHorizontal: textAlignmentHorizontalValue.value,
      textAlignmentVertical: textAlignmentVerticalValue.value,
      textColor: textColorValue.value,
      textGradient: textGradientValue.value,
      textShadow: textShadowValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      underline: underlineValue.value,
      variables: variablesValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTextTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivText> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var actionValue: DeserializationResult<DivAction> = .noValue
    var actionAnimationValue: DeserializationResult<DivAnimation> = .noValue
    var actionsValue: DeserializationResult<[DivAction]> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var autoEllipsizeValue: DeserializationResult<Expression<Bool>> = parent?.autoEllipsize?.value() ?? .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?.value() ?? .noValue
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var doubletapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var ellipsisValue: DeserializationResult<DivText.Ellipsis> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var focusedTextColorValue: DeserializationResult<Expression<Color>> = parent?.focusedTextColor?.value() ?? .noValue
    var fontFamilyValue: DeserializationResult<Expression<String>> = parent?.fontFamily?.value() ?? .noValue
    var fontFeatureSettingsValue: DeserializationResult<Expression<String>> = parent?.fontFeatureSettings?.value() ?? .noValue
    var fontSizeValue: DeserializationResult<Expression<Int>> = parent?.fontSize?.value() ?? .noValue
    var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.fontSizeUnit?.value() ?? .noValue
    var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.fontWeight?.value() ?? .noValue
    var fontWeightValueValue: DeserializationResult<Expression<Int>> = parent?.fontWeightValue?.value() ?? .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value() ?? .noValue
    var imagesValue: DeserializationResult<[DivText.Image]> = .noValue
    var layoutProviderValue: DeserializationResult<DivLayoutProvider> = .noValue
    var letterSpacingValue: DeserializationResult<Expression<Double>> = parent?.letterSpacing?.value() ?? .noValue
    var lineHeightValue: DeserializationResult<Expression<Int>> = parent?.lineHeight?.value() ?? .noValue
    var longtapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var maxLinesValue: DeserializationResult<Expression<Int>> = parent?.maxLines?.value() ?? .noValue
    var minHiddenLinesValue: DeserializationResult<Expression<Int>> = parent?.minHiddenLines?.value() ?? .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var rangesValue: DeserializationResult<[DivText.Range]> = .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var selectableValue: DeserializationResult<Expression<Bool>> = parent?.selectable?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var strikeValue: DeserializationResult<Expression<DivLineStyle>> = parent?.strike?.value() ?? .noValue
    var textValue: DeserializationResult<Expression<String>> = parent?.text?.value() ?? .noValue
    var textAlignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.textAlignmentHorizontal?.value() ?? .noValue
    var textAlignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.textAlignmentVertical?.value() ?? .noValue
    var textColorValue: DeserializationResult<Expression<Color>> = parent?.textColor?.value() ?? .noValue
    var textGradientValue: DeserializationResult<DivTextGradient> = .noValue
    var textShadowValue: DeserializationResult<DivShadow> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue
    var underlineValue: DeserializationResult<Expression<DivLineStyle>> = parent?.underline?.value() ?? .noValue
    var variablesValue: DeserializationResult<[DivVariable]> = .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = parent?.visibility?.value() ?? .noValue
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "accessibility":
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "action":
        actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionValue)
      case "action_animation":
        actionAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self).merged(with: actionAnimationValue)
      case "actions":
        actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "auto_ellipsize":
        autoEllipsizeValue = deserialize(__dictValue).merged(with: autoEllipsizeValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).merged(with: borderValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "disappear_actions":
        disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).merged(with: disappearActionsValue)
      case "doubletap_actions":
        doubletapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: doubletapActionsValue)
      case "ellipsis":
        ellipsisValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.EllipsisTemplate.self).merged(with: ellipsisValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).merged(with: focusValue)
      case "focused_text_color":
        focusedTextColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: focusedTextColorValue)
      case "font_family":
        fontFamilyValue = deserialize(__dictValue).merged(with: fontFamilyValue)
      case "font_feature_settings":
        fontFeatureSettingsValue = deserialize(__dictValue).merged(with: fontFeatureSettingsValue)
      case "font_size":
        fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
      case "font_size_unit":
        fontSizeUnitValue = deserialize(__dictValue).merged(with: fontSizeUnitValue)
      case "font_weight":
        fontWeightValue = deserialize(__dictValue).merged(with: fontWeightValue)
      case "font_weight_value":
        fontWeightValueValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator).merged(with: fontWeightValueValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue).merged(with: idValue)
      case "images":
        imagesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.self).merged(with: imagesValue)
      case "layout_provider":
        layoutProviderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self).merged(with: layoutProviderValue)
      case "letter_spacing":
        letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
      case "line_height":
        lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
      case "longtap_actions":
        longtapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: longtapActionsValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "max_lines":
        maxLinesValue = deserialize(__dictValue, validator: ResolvedValue.maxLinesValidator).merged(with: maxLinesValue)
      case "min_hidden_lines":
        minHiddenLinesValue = deserialize(__dictValue, validator: ResolvedValue.minHiddenLinesValidator).merged(with: minHiddenLinesValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "ranges":
        rangesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.RangeTemplate.self).merged(with: rangesValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "selectable":
        selectableValue = deserialize(__dictValue).merged(with: selectableValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "strike":
        strikeValue = deserialize(__dictValue).merged(with: strikeValue)
      case "text":
        textValue = deserialize(__dictValue).merged(with: textValue)
      case "text_alignment_horizontal":
        textAlignmentHorizontalValue = deserialize(__dictValue).merged(with: textAlignmentHorizontalValue)
      case "text_alignment_vertical":
        textAlignmentVerticalValue = deserialize(__dictValue).merged(with: textAlignmentVerticalValue)
      case "text_color":
        textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: textColorValue)
      case "text_gradient":
        textGradientValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextGradientTemplate.self).merged(with: textGradientValue)
      case "text_shadow":
        textShadowValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self).merged(with: textShadowValue)
      case "tooltips":
        tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
      case "transform":
        transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).merged(with: transformValue)
      case "transition_change":
        transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
      case "transition_in":
        transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
      case "transition_out":
        transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
      case "transition_triggers":
        transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
      case "underline":
        underlineValue = deserialize(__dictValue).merged(with: underlineValue)
      case "variables":
        variablesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self).merged(with: variablesValue)
      case "visibility":
        visibilityValue = deserialize(__dictValue).merged(with: visibilityValue)
      case "visibility_action":
        visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
      case "visibility_actions":
        visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
      case "width":
        widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: widthValue)
      case parent?.accessibility?.link:
        accessibilityValue = accessibilityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self) })
      case parent?.action?.link:
        actionValue = actionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.actionAnimation?.link:
        actionAnimationValue = actionAnimationValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self) })
      case parent?.actions?.link:
        actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
      case parent?.autoEllipsize?.link:
        autoEllipsizeValue = autoEllipsizeValue.merged(with: { deserialize(__dictValue) })
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self) })
      case parent?.border?.link:
        borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self) })
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator) })
      case parent?.disappearActions?.link:
        disappearActionsValue = disappearActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self) })
      case parent?.doubletapActions?.link:
        doubletapActionsValue = doubletapActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.ellipsis?.link:
        ellipsisValue = ellipsisValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.EllipsisTemplate.self) })
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self) })
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self) })
      case parent?.focusedTextColor?.link:
        focusedTextColorValue = focusedTextColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
      case parent?.fontFamily?.link:
        fontFamilyValue = fontFamilyValue.merged(with: { deserialize(__dictValue) })
      case parent?.fontFeatureSettings?.link:
        fontFeatureSettingsValue = fontFeatureSettingsValue.merged(with: { deserialize(__dictValue) })
      case parent?.fontSize?.link:
        fontSizeValue = fontSizeValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator) })
      case parent?.fontSizeUnit?.link:
        fontSizeUnitValue = fontSizeUnitValue.merged(with: { deserialize(__dictValue) })
      case parent?.fontWeight?.link:
        fontWeightValue = fontWeightValue.merged(with: { deserialize(__dictValue) })
      case parent?.fontWeightValue?.link:
        fontWeightValueValue = fontWeightValueValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator) })
      case parent?.height?.link:
        heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      case parent?.id?.link:
        idValue = idValue.merged(with: { deserialize(__dictValue) })
      case parent?.images?.link:
        imagesValue = imagesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.self) })
      case parent?.layoutProvider?.link:
        layoutProviderValue = layoutProviderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self) })
      case parent?.letterSpacing?.link:
        letterSpacingValue = letterSpacingValue.merged(with: { deserialize(__dictValue) })
      case parent?.lineHeight?.link:
        lineHeightValue = lineHeightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator) })
      case parent?.longtapActions?.link:
        longtapActionsValue = longtapActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.maxLines?.link:
        maxLinesValue = maxLinesValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.maxLinesValidator) })
      case parent?.minHiddenLines?.link:
        minHiddenLinesValue = minHiddenLinesValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.minHiddenLinesValidator) })
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
      case parent?.ranges?.link:
        rangesValue = rangesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.RangeTemplate.self) })
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator) })
      case parent?.selectable?.link:
        selectableValue = selectableValue.merged(with: { deserialize(__dictValue) })
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.strike?.link:
        strikeValue = strikeValue.merged(with: { deserialize(__dictValue) })
      case parent?.text?.link:
        textValue = textValue.merged(with: { deserialize(__dictValue) })
      case parent?.textAlignmentHorizontal?.link:
        textAlignmentHorizontalValue = textAlignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
      case parent?.textAlignmentVertical?.link:
        textAlignmentVerticalValue = textAlignmentVerticalValue.merged(with: { deserialize(__dictValue) })
      case parent?.textColor?.link:
        textColorValue = textColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
      case parent?.textGradient?.link:
        textGradientValue = textGradientValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextGradientTemplate.self) })
      case parent?.textShadow?.link:
        textShadowValue = textShadowValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self) })
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self) })
      case parent?.transform?.link:
        transformValue = transformValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self) })
      case parent?.transitionChange?.link:
        transitionChangeValue = transitionChangeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self) })
      case parent?.transitionIn?.link:
        transitionInValue = transitionInValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
      case parent?.transitionOut?.link:
        transitionOutValue = transitionOutValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
      case parent?.transitionTriggers?.link:
        transitionTriggersValue = transitionTriggersValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator) })
      case parent?.underline?.link:
        underlineValue = underlineValue.merged(with: { deserialize(__dictValue) })
      case parent?.variables?.link:
        variablesValue = variablesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self) })
      case parent?.visibility?.link:
        visibilityValue = visibilityValue.merged(with: { deserialize(__dictValue) })
      case parent?.visibilityAction?.link:
        visibilityActionValue = visibilityActionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
      case parent?.visibilityActions?.link:
        visibilityActionsValue = visibilityActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
      case parent?.width?.link:
        widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      accessibilityValue = accessibilityValue.merged(with: { parent.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      actionValue = actionValue.merged(with: { parent.action?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      actionAnimationValue = actionAnimationValue.merged(with: { parent.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      doubletapActionsValue = doubletapActionsValue.merged(with: { parent.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      ellipsisValue = ellipsisValue.merged(with: { parent.ellipsis?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      imagesValue = imagesValue.merged(with: { parent.images?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      layoutProviderValue = layoutProviderValue.merged(with: { parent.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      longtapActionsValue = longtapActionsValue.merged(with: { parent.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      rangesValue = rangesValue.merged(with: { parent.ranges?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      textGradientValue = textGradientValue.merged(with: { parent.textGradient?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      textShadowValue = textShadowValue.merged(with: { parent.textShadow?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      variablesValue = variablesValue.merged(with: { parent.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionValue = visibilityActionValue.merged(with: { parent.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      visibilityActionsValue = visibilityActionsValue.merged(with: { parent.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) })
    }
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      autoEllipsizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "auto_ellipsize", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      disappearActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "disappear_actions", error: $0) },
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      ellipsisValue.errorsOrWarnings?.map { .nestedObjectError(field: "ellipsis", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      focusedTextColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "focused_text_color", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontFeatureSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_feature_settings", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      imagesValue.errorsOrWarnings?.map { .nestedObjectError(field: "images", error: $0) },
      layoutProviderValue.errorsOrWarnings?.map { .nestedObjectError(field: "layout_provider", error: $0) },
      letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
      lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
      longtapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "longtap_actions", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      maxLinesValue.errorsOrWarnings?.map { .nestedObjectError(field: "max_lines", error: $0) },
      minHiddenLinesValue.errorsOrWarnings?.map { .nestedObjectError(field: "min_hidden_lines", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      rangesValue.errorsOrWarnings?.map { .nestedObjectError(field: "ranges", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectableValue.errorsOrWarnings?.map { .nestedObjectError(field: "selectable", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      strikeValue.errorsOrWarnings?.map { .nestedObjectError(field: "strike", error: $0) },
      textValue.errorsOrWarnings?.map { .nestedObjectError(field: "text", error: $0) },
      textAlignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_alignment_horizontal", error: $0) },
      textAlignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_alignment_vertical", error: $0) },
      textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
      textGradientValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_gradient", error: $0) },
      textShadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_shadow", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) },
      variablesValue.errorsOrWarnings?.map { .nestedObjectError(field: "variables", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = textValue {
      errors.append(.requiredFieldIsMissing(field: "text"))
    }
    guard
      let textNonNil = textValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivText(
      accessibility: accessibilityValue.value,
      action: actionValue.value,
      actionAnimation: actionAnimationValue.value,
      actions: actionsValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      autoEllipsize: autoEllipsizeValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      disappearActions: disappearActionsValue.value,
      doubletapActions: doubletapActionsValue.value,
      ellipsis: ellipsisValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      focusedTextColor: focusedTextColorValue.value,
      fontFamily: fontFamilyValue.value,
      fontFeatureSettings: fontFeatureSettingsValue.value,
      fontSize: fontSizeValue.value,
      fontSizeUnit: fontSizeUnitValue.value,
      fontWeight: fontWeightValue.value,
      fontWeightValue: fontWeightValueValue.value,
      height: heightValue.value,
      id: idValue.value,
      images: imagesValue.value,
      layoutProvider: layoutProviderValue.value,
      letterSpacing: letterSpacingValue.value,
      lineHeight: lineHeightValue.value,
      longtapActions: longtapActionsValue.value,
      margins: marginsValue.value,
      maxLines: maxLinesValue.value,
      minHiddenLines: minHiddenLinesValue.value,
      paddings: paddingsValue.value,
      ranges: rangesValue.value,
      rowSpan: rowSpanValue.value,
      selectable: selectableValue.value,
      selectedActions: selectedActionsValue.value,
      strike: strikeValue.value,
      text: textNonNil,
      textAlignmentHorizontal: textAlignmentHorizontalValue.value,
      textAlignmentVertical: textAlignmentVerticalValue.value,
      textColor: textColorValue.value,
      textGradient: textGradientValue.value,
      textShadow: textShadowValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      underline: underlineValue.value,
      variables: variablesValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTextTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivTextTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivTextTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      action: action ?? mergedParent.action,
      actionAnimation: actionAnimation ?? mergedParent.actionAnimation,
      actions: actions ?? mergedParent.actions,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      autoEllipsize: autoEllipsize ?? mergedParent.autoEllipsize,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      disappearActions: disappearActions ?? mergedParent.disappearActions,
      doubletapActions: doubletapActions ?? mergedParent.doubletapActions,
      ellipsis: ellipsis ?? mergedParent.ellipsis,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      focusedTextColor: focusedTextColor ?? mergedParent.focusedTextColor,
      fontFamily: fontFamily ?? mergedParent.fontFamily,
      fontFeatureSettings: fontFeatureSettings ?? mergedParent.fontFeatureSettings,
      fontSize: fontSize ?? mergedParent.fontSize,
      fontSizeUnit: fontSizeUnit ?? mergedParent.fontSizeUnit,
      fontWeight: fontWeight ?? mergedParent.fontWeight,
      fontWeightValue: fontWeightValue ?? mergedParent.fontWeightValue,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      images: images ?? mergedParent.images,
      layoutProvider: layoutProvider ?? mergedParent.layoutProvider,
      letterSpacing: letterSpacing ?? mergedParent.letterSpacing,
      lineHeight: lineHeight ?? mergedParent.lineHeight,
      longtapActions: longtapActions ?? mergedParent.longtapActions,
      margins: margins ?? mergedParent.margins,
      maxLines: maxLines ?? mergedParent.maxLines,
      minHiddenLines: minHiddenLines ?? mergedParent.minHiddenLines,
      paddings: paddings ?? mergedParent.paddings,
      ranges: ranges ?? mergedParent.ranges,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectable: selectable ?? mergedParent.selectable,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      strike: strike ?? mergedParent.strike,
      text: text ?? mergedParent.text,
      textAlignmentHorizontal: textAlignmentHorizontal ?? mergedParent.textAlignmentHorizontal,
      textAlignmentVertical: textAlignmentVertical ?? mergedParent.textAlignmentVertical,
      textColor: textColor ?? mergedParent.textColor,
      textGradient: textGradient ?? mergedParent.textGradient,
      textShadow: textShadow ?? mergedParent.textShadow,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      underline: underline ?? mergedParent.underline,
      variables: variables ?? mergedParent.variables,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTextTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTextTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      action: merged.action?.tryResolveParent(templates: templates),
      actionAnimation: merged.actionAnimation?.tryResolveParent(templates: templates),
      actions: merged.actions?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      autoEllipsize: merged.autoEllipsize,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      disappearActions: merged.disappearActions?.tryResolveParent(templates: templates),
      doubletapActions: merged.doubletapActions?.tryResolveParent(templates: templates),
      ellipsis: merged.ellipsis?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      focusedTextColor: merged.focusedTextColor,
      fontFamily: merged.fontFamily,
      fontFeatureSettings: merged.fontFeatureSettings,
      fontSize: merged.fontSize,
      fontSizeUnit: merged.fontSizeUnit,
      fontWeight: merged.fontWeight,
      fontWeightValue: merged.fontWeightValue,
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      images: merged.images?.tryResolveParent(templates: templates),
      layoutProvider: merged.layoutProvider?.tryResolveParent(templates: templates),
      letterSpacing: merged.letterSpacing,
      lineHeight: merged.lineHeight,
      longtapActions: merged.longtapActions?.tryResolveParent(templates: templates),
      margins: merged.margins?.tryResolveParent(templates: templates),
      maxLines: merged.maxLines,
      minHiddenLines: merged.minHiddenLines,
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      ranges: merged.ranges?.tryResolveParent(templates: templates),
      rowSpan: merged.rowSpan,
      selectable: merged.selectable,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      strike: merged.strike,
      text: merged.text,
      textAlignmentHorizontal: merged.textAlignmentHorizontal,
      textAlignmentVertical: merged.textAlignmentVertical,
      textColor: merged.textColor,
      textGradient: merged.textGradient?.tryResolveParent(templates: templates),
      textShadow: merged.textShadow?.tryResolveParent(templates: templates),
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      underline: merged.underline,
      variables: merged.variables?.tryResolveParent(templates: templates),
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
