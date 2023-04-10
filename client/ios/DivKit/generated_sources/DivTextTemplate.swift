// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTextTemplate: TemplateValue {
  public final class EllipsisTemplate: TemplateValue {
    public let actions: Field<[DivActionTemplate]>? // at least 1 elements
    public let images: Field<[ImageTemplate]>? // at least 1 elements
    public let ranges: Field<[RangeTemplate]>? // at least 1 elements
    public let text: Field<Expression<String>>? // at least 1 char

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          actions: try dictionary.getOptionalArray("actions", templateToType: templateToType),
          images: try dictionary.getOptionalArray("images", templateToType: templateToType),
          ranges: try dictionary.getOptionalArray("ranges", templateToType: templateToType),
          text: try dictionary.getOptionalExpressionField("text")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "ellipsis_template." + field, representation: representation)
      }
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
      let actionsValue = parent?.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true) ?? .noValue
      let imagesValue = parent?.images?.resolveOptionalValue(context: context, validator: ResolvedValue.imagesValidator, useOnlyLinks: true) ?? .noValue
      let rangesValue = parent?.ranges?.resolveOptionalValue(context: context, validator: ResolvedValue.rangesValidator, useOnlyLinks: true) ?? .noValue
      let textValue = parent?.text?.resolveValue(context: context, validator: ResolvedValue.textValidator) ?? .noValue
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
          actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self).merged(with: actionsValue)
        case "images":
          imagesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.imagesValidator, type: DivTextTemplate.ImageTemplate.self).merged(with: imagesValue)
        case "ranges":
          rangesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.rangesValidator, type: DivTextTemplate.RangeTemplate.self).merged(with: rangesValue)
        case "text":
          textValue = deserialize(__dictValue, validator: ResolvedValue.textValidator).merged(with: textValue)
        case parent?.actions?.link:
          actionsValue = actionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self))
        case parent?.images?.link:
          imagesValue = imagesValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.imagesValidator, type: DivTextTemplate.ImageTemplate.self))
        case parent?.ranges?.link:
          rangesValue = rangesValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.rangesValidator, type: DivTextTemplate.RangeTemplate.self))
        case parent?.text?.link:
          textValue = textValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.textValidator))
        default: break
        }
      }
      if let parent = parent {
        actionsValue = actionsValue.merged(with: parent.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true))
        imagesValue = imagesValue.merged(with: parent.images?.resolveOptionalValue(context: context, validator: ResolvedValue.imagesValidator, useOnlyLinks: true))
        rangesValue = rangesValue.merged(with: parent.ranges?.resolveOptionalValue(context: context, validator: ResolvedValue.rangesValidator, useOnlyLinks: true))
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
    public let start: Field<Expression<Int>>? // constraint: number >= 0
    public let tintColor: Field<Expression<Color>>?
    public let tintMode: Field<Expression<DivBlendMode>>? // default value: source_in
    public let url: Field<Expression<URL>>?
    public let width: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(20))

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          height: try dictionary.getOptionalField("height", templateToType: templateToType),
          start: try dictionary.getOptionalExpressionField("start"),
          tintColor: try dictionary.getOptionalExpressionField("tint_color", transform: Color.color(withHexString:)),
          tintMode: try dictionary.getOptionalExpressionField("tint_mode"),
          url: try dictionary.getOptionalExpressionField("url", transform: URL.init(string:)),
          width: try dictionary.getOptionalField("width", templateToType: templateToType)
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "image_template." + field, representation: representation)
      }
    }

    init(
      height: Field<DivFixedSizeTemplate>? = nil,
      start: Field<Expression<Int>>? = nil,
      tintColor: Field<Expression<Color>>? = nil,
      tintMode: Field<Expression<DivBlendMode>>? = nil,
      url: Field<Expression<URL>>? = nil,
      width: Field<DivFixedSizeTemplate>? = nil
    ) {
      self.height = height
      self.start = start
      self.tintColor = tintColor
      self.tintMode = tintMode
      self.url = url
      self.width = width
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ImageTemplate?) -> DeserializationResult<DivText.Image> {
      let heightValue = parent?.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true) ?? .noValue
      let startValue = parent?.start?.resolveValue(context: context, validator: ResolvedValue.startValidator) ?? .noValue
      let tintColorValue = parent?.tintColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.tintColorValidator) ?? .noValue
      let tintModeValue = parent?.tintMode?.resolveOptionalValue(context: context, validator: ResolvedValue.tintModeValidator) ?? .noValue
      let urlValue = parent?.url?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
      let widthValue = parent?.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true) ?? .noValue
      var errors = mergeErrors(
        heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
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
      var startValue: DeserializationResult<Expression<Int>> = parent?.start?.value() ?? .noValue
      var tintColorValue: DeserializationResult<Expression<Color>> = parent?.tintColor?.value() ?? .noValue
      var tintModeValue: DeserializationResult<Expression<DivBlendMode>> = parent?.tintMode?.value() ?? .noValue
      var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
      var widthValue: DeserializationResult<DivFixedSize> = .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "height":
          heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivFixedSizeTemplate.self).merged(with: heightValue)
        case "start":
          startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).merged(with: startValue)
        case "tint_color":
          tintColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.tintColorValidator).merged(with: tintColorValue)
        case "tint_mode":
          tintModeValue = deserialize(__dictValue, validator: ResolvedValue.tintModeValidator).merged(with: tintModeValue)
        case "url":
          urlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: urlValue)
        case "width":
          widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.widthValidator, type: DivFixedSizeTemplate.self).merged(with: widthValue)
        case parent?.height?.link:
          heightValue = heightValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivFixedSizeTemplate.self))
        case parent?.start?.link:
          startValue = startValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.startValidator))
        case parent?.tintColor?.link:
          tintColorValue = tintColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.tintColorValidator))
        case parent?.tintMode?.link:
          tintModeValue = tintModeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.tintModeValidator))
        case parent?.url?.link:
          urlValue = urlValue.merged(with: deserialize(__dictValue, transform: URL.init(string:)))
        case parent?.width?.link:
          widthValue = widthValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.widthValidator, type: DivFixedSizeTemplate.self))
        default: break
        }
      }
      if let parent = parent {
        heightValue = heightValue.merged(with: parent.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true))
        widthValue = widthValue.merged(with: parent.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true))
      }
      var errors = mergeErrors(
        heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
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
        start: merged.start,
        tintColor: merged.tintColor,
        tintMode: merged.tintMode,
        url: merged.url,
        width: merged.width?.tryResolveParent(templates: templates)
      )
    }
  }

  public final class RangeTemplate: TemplateValue {
    public let actions: Field<[DivActionTemplate]>? // at least 1 elements
    public let background: Field<DivTextRangeBackgroundTemplate>?
    public let border: Field<DivTextRangeBorderTemplate>?
    public let end: Field<Expression<Int>>? // constraint: number > 0
    public let fontFamily: Field<Expression<DivFontFamily>>?
    public let fontSize: Field<Expression<Int>>? // constraint: number >= 0
    public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
    public let fontWeight: Field<Expression<DivFontWeight>>?
    public let letterSpacing: Field<Expression<Double>>?
    public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
    public let start: Field<Expression<Int>>? // constraint: number >= 0
    public let strike: Field<Expression<DivLineStyle>>?
    public let textColor: Field<Expression<Color>>?
    public let topOffset: Field<Expression<Int>>? // constraint: number >= 0
    public let underline: Field<Expression<DivLineStyle>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          actions: try dictionary.getOptionalArray("actions", templateToType: templateToType),
          background: try dictionary.getOptionalField("background", templateToType: templateToType),
          border: try dictionary.getOptionalField("border", templateToType: templateToType),
          end: try dictionary.getOptionalExpressionField("end"),
          fontFamily: try dictionary.getOptionalExpressionField("font_family"),
          fontSize: try dictionary.getOptionalExpressionField("font_size"),
          fontSizeUnit: try dictionary.getOptionalExpressionField("font_size_unit"),
          fontWeight: try dictionary.getOptionalExpressionField("font_weight"),
          letterSpacing: try dictionary.getOptionalExpressionField("letter_spacing"),
          lineHeight: try dictionary.getOptionalExpressionField("line_height"),
          start: try dictionary.getOptionalExpressionField("start"),
          strike: try dictionary.getOptionalExpressionField("strike"),
          textColor: try dictionary.getOptionalExpressionField("text_color", transform: Color.color(withHexString:)),
          topOffset: try dictionary.getOptionalExpressionField("top_offset"),
          underline: try dictionary.getOptionalExpressionField("underline")
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "range_template." + field, representation: representation)
      }
    }

    init(
      actions: Field<[DivActionTemplate]>? = nil,
      background: Field<DivTextRangeBackgroundTemplate>? = nil,
      border: Field<DivTextRangeBorderTemplate>? = nil,
      end: Field<Expression<Int>>? = nil,
      fontFamily: Field<Expression<DivFontFamily>>? = nil,
      fontSize: Field<Expression<Int>>? = nil,
      fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
      fontWeight: Field<Expression<DivFontWeight>>? = nil,
      letterSpacing: Field<Expression<Double>>? = nil,
      lineHeight: Field<Expression<Int>>? = nil,
      start: Field<Expression<Int>>? = nil,
      strike: Field<Expression<DivLineStyle>>? = nil,
      textColor: Field<Expression<Color>>? = nil,
      topOffset: Field<Expression<Int>>? = nil,
      underline: Field<Expression<DivLineStyle>>? = nil
    ) {
      self.actions = actions
      self.background = background
      self.border = border
      self.end = end
      self.fontFamily = fontFamily
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit
      self.fontWeight = fontWeight
      self.letterSpacing = letterSpacing
      self.lineHeight = lineHeight
      self.start = start
      self.strike = strike
      self.textColor = textColor
      self.topOffset = topOffset
      self.underline = underline
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: RangeTemplate?) -> DeserializationResult<DivText.Range> {
      let actionsValue = parent?.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true) ?? .noValue
      let backgroundValue = parent?.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true) ?? .noValue
      let borderValue = parent?.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true) ?? .noValue
      let endValue = parent?.end?.resolveValue(context: context, validator: ResolvedValue.endValidator) ?? .noValue
      let fontFamilyValue = parent?.fontFamily?.resolveOptionalValue(context: context, validator: ResolvedValue.fontFamilyValidator) ?? .noValue
      let fontSizeValue = parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue
      let fontSizeUnitValue = parent?.fontSizeUnit?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeUnitValidator) ?? .noValue
      let fontWeightValue = parent?.fontWeight?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValidator) ?? .noValue
      let letterSpacingValue = parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue
      let lineHeightValue = parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue
      let startValue = parent?.start?.resolveValue(context: context, validator: ResolvedValue.startValidator) ?? .noValue
      let strikeValue = parent?.strike?.resolveOptionalValue(context: context, validator: ResolvedValue.strikeValidator) ?? .noValue
      let textColorValue = parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator) ?? .noValue
      let topOffsetValue = parent?.topOffset?.resolveOptionalValue(context: context, validator: ResolvedValue.topOffsetValidator) ?? .noValue
      let underlineValue = parent?.underline?.resolveOptionalValue(context: context, validator: ResolvedValue.underlineValidator) ?? .noValue
      var errors = mergeErrors(
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
        borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
        endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
        fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
        lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
        strikeValue.errorsOrWarnings?.map { .nestedObjectError(field: "strike", error: $0) },
        textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
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
        fontSize: fontSizeValue.value,
        fontSizeUnit: fontSizeUnitValue.value,
        fontWeight: fontWeightValue.value,
        letterSpacing: letterSpacingValue.value,
        lineHeight: lineHeightValue.value,
        start: startNonNil,
        strike: strikeValue.value,
        textColor: textColorValue.value,
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
      var fontFamilyValue: DeserializationResult<Expression<DivFontFamily>> = parent?.fontFamily?.value() ?? .noValue
      var fontSizeValue: DeserializationResult<Expression<Int>> = parent?.fontSize?.value() ?? .noValue
      var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.fontSizeUnit?.value() ?? .noValue
      var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.fontWeight?.value() ?? .noValue
      var letterSpacingValue: DeserializationResult<Expression<Double>> = parent?.letterSpacing?.value() ?? .noValue
      var lineHeightValue: DeserializationResult<Expression<Int>> = parent?.lineHeight?.value() ?? .noValue
      var startValue: DeserializationResult<Expression<Int>> = parent?.start?.value() ?? .noValue
      var strikeValue: DeserializationResult<Expression<DivLineStyle>> = parent?.strike?.value() ?? .noValue
      var textColorValue: DeserializationResult<Expression<Color>> = parent?.textColor?.value() ?? .noValue
      var topOffsetValue: DeserializationResult<Expression<Int>> = parent?.topOffset?.value() ?? .noValue
      var underlineValue: DeserializationResult<Expression<DivLineStyle>> = parent?.underline?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "actions":
          actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self).merged(with: actionsValue)
        case "background":
          backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivTextRangeBackgroundTemplate.self).merged(with: backgroundValue)
        case "border":
          borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivTextRangeBorderTemplate.self).merged(with: borderValue)
        case "end":
          endValue = deserialize(__dictValue, validator: ResolvedValue.endValidator).merged(with: endValue)
        case "font_family":
          fontFamilyValue = deserialize(__dictValue, validator: ResolvedValue.fontFamilyValidator).merged(with: fontFamilyValue)
        case "font_size":
          fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
        case "font_size_unit":
          fontSizeUnitValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator).merged(with: fontSizeUnitValue)
        case "font_weight":
          fontWeightValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator).merged(with: fontWeightValue)
        case "letter_spacing":
          letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
        case "line_height":
          lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
        case "start":
          startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).merged(with: startValue)
        case "strike":
          strikeValue = deserialize(__dictValue, validator: ResolvedValue.strikeValidator).merged(with: strikeValue)
        case "text_color":
          textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator).merged(with: textColorValue)
        case "top_offset":
          topOffsetValue = deserialize(__dictValue, validator: ResolvedValue.topOffsetValidator).merged(with: topOffsetValue)
        case "underline":
          underlineValue = deserialize(__dictValue, validator: ResolvedValue.underlineValidator).merged(with: underlineValue)
        case parent?.actions?.link:
          actionsValue = actionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self))
        case parent?.background?.link:
          backgroundValue = backgroundValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivTextRangeBackgroundTemplate.self))
        case parent?.border?.link:
          borderValue = borderValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivTextRangeBorderTemplate.self))
        case parent?.end?.link:
          endValue = endValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.endValidator))
        case parent?.fontFamily?.link:
          fontFamilyValue = fontFamilyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontFamilyValidator))
        case parent?.fontSize?.link:
          fontSizeValue = fontSizeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator))
        case parent?.fontSizeUnit?.link:
          fontSizeUnitValue = fontSizeUnitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator))
        case parent?.fontWeight?.link:
          fontWeightValue = fontWeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator))
        case parent?.letterSpacing?.link:
          letterSpacingValue = letterSpacingValue.merged(with: deserialize(__dictValue))
        case parent?.lineHeight?.link:
          lineHeightValue = lineHeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator))
        case parent?.start?.link:
          startValue = startValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.startValidator))
        case parent?.strike?.link:
          strikeValue = strikeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.strikeValidator))
        case parent?.textColor?.link:
          textColorValue = textColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator))
        case parent?.topOffset?.link:
          topOffsetValue = topOffsetValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.topOffsetValidator))
        case parent?.underline?.link:
          underlineValue = underlineValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.underlineValidator))
        default: break
        }
      }
      if let parent = parent {
        actionsValue = actionsValue.merged(with: parent.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true))
        backgroundValue = backgroundValue.merged(with: parent.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true))
        borderValue = borderValue.merged(with: parent.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true))
      }
      var errors = mergeErrors(
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
        borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
        endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
        fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
        lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
        strikeValue.errorsOrWarnings?.map { .nestedObjectError(field: "strike", error: $0) },
        textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
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
        fontSize: fontSizeValue.value,
        fontSizeUnit: fontSizeUnitValue.value,
        fontWeight: fontWeightValue.value,
        letterSpacing: letterSpacingValue.value,
        lineHeight: lineHeightValue.value,
        start: startNonNil,
        strike: strikeValue.value,
        textColor: textColorValue.value,
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
        fontSize: merged.fontSize,
        fontSizeUnit: merged.fontSizeUnit,
        fontWeight: merged.fontWeight,
        letterSpacing: merged.letterSpacing,
        lineHeight: merged.lineHeight,
        start: merged.start,
        strike: merged.strike,
        textColor: merged.textColor,
        topOffset: merged.topOffset,
        underline: merged.underline
      )
    }
  }

  public static let type: String = "text"
  public let parent: String? // at least 1 char
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let action: Field<DivActionTemplate>?
  public let actionAnimation: Field<DivAnimationTemplate>? // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: Field<[DivActionTemplate]>? // at least 1 elements
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let autoEllipsize: Field<Expression<Bool>>?
  public let background: Field<[DivBackgroundTemplate]>? // at least 1 elements
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let doubletapActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let ellipsis: Field<EllipsisTemplate>?
  public let extensions: Field<[DivExtensionTemplate]>? // at least 1 elements
  public let focus: Field<DivFocusTemplate>?
  public let focusedTextColor: Field<Expression<Color>>?
  public let fontFamily: Field<Expression<DivFontFamily>>? // default value: text
  public let fontSize: Field<Expression<Int>>? // constraint: number >= 0; default value: 12
  public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
  public let fontWeight: Field<Expression<DivFontWeight>>? // default value: regular
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>? // at least 1 char
  public let images: Field<[ImageTemplate]>? // at least 1 elements
  public let letterSpacing: Field<Expression<Double>>? // default value: 0
  public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
  public let longtapActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let maxLines: Field<Expression<Int>>? // constraint: number >= 0
  public let minHiddenLines: Field<Expression<Int>>? // constraint: number >= 0
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let ranges: Field<[RangeTemplate]>? // at least 1 elements
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectable: Field<Expression<Bool>>? // default value: false
  public let selectedActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let strike: Field<Expression<DivLineStyle>>? // default value: none
  public let text: Field<Expression<CFString>>? // at least 1 char
  public let textAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? // default value: left
  public let textAlignmentVertical: Field<Expression<DivAlignmentVertical>>? // default value: top
  public let textColor: Field<Expression<Color>>? // default value: #FF000000
  public let textGradient: Field<DivTextGradientTemplate>?
  public let tooltips: Field<[DivTooltipTemplate]>? // at least 1 elements
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let underline: Field<Expression<DivLineStyle>>? // default value: none
  public let visibility: Field<Expression<DivVisibility>>? // default value: visible
  public let visibilityAction: Field<DivVisibilityActionTemplate>?
  public let visibilityActions: Field<[DivVisibilityActionTemplate]>? // at least 1 elements
  public let width: Field<DivSizeTemplate>? // default value: .divMatchParentSize(DivMatchParentSize())

  static let parentValidator: AnyValueValidator<String> =
    makeStringValidator(minLength: 1)

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    do {
      self.init(
        parent: try dictionary.getOptionalField("type", validator: Self.parentValidator),
        accessibility: try dictionary.getOptionalField("accessibility", templateToType: templateToType),
        action: try dictionary.getOptionalField("action", templateToType: templateToType),
        actionAnimation: try dictionary.getOptionalField("action_animation", templateToType: templateToType),
        actions: try dictionary.getOptionalArray("actions", templateToType: templateToType),
        alignmentHorizontal: try dictionary.getOptionalExpressionField("alignment_horizontal"),
        alignmentVertical: try dictionary.getOptionalExpressionField("alignment_vertical"),
        alpha: try dictionary.getOptionalExpressionField("alpha"),
        autoEllipsize: try dictionary.getOptionalExpressionField("auto_ellipsize"),
        background: try dictionary.getOptionalArray("background", templateToType: templateToType),
        border: try dictionary.getOptionalField("border", templateToType: templateToType),
        columnSpan: try dictionary.getOptionalExpressionField("column_span"),
        doubletapActions: try dictionary.getOptionalArray("doubletap_actions", templateToType: templateToType),
        ellipsis: try dictionary.getOptionalField("ellipsis", templateToType: templateToType),
        extensions: try dictionary.getOptionalArray("extensions", templateToType: templateToType),
        focus: try dictionary.getOptionalField("focus", templateToType: templateToType),
        focusedTextColor: try dictionary.getOptionalExpressionField("focused_text_color", transform: Color.color(withHexString:)),
        fontFamily: try dictionary.getOptionalExpressionField("font_family"),
        fontSize: try dictionary.getOptionalExpressionField("font_size"),
        fontSizeUnit: try dictionary.getOptionalExpressionField("font_size_unit"),
        fontWeight: try dictionary.getOptionalExpressionField("font_weight"),
        height: try dictionary.getOptionalField("height", templateToType: templateToType),
        id: try dictionary.getOptionalField("id"),
        images: try dictionary.getOptionalArray("images", templateToType: templateToType),
        letterSpacing: try dictionary.getOptionalExpressionField("letter_spacing"),
        lineHeight: try dictionary.getOptionalExpressionField("line_height"),
        longtapActions: try dictionary.getOptionalArray("longtap_actions", templateToType: templateToType),
        margins: try dictionary.getOptionalField("margins", templateToType: templateToType),
        maxLines: try dictionary.getOptionalExpressionField("max_lines"),
        minHiddenLines: try dictionary.getOptionalExpressionField("min_hidden_lines"),
        paddings: try dictionary.getOptionalField("paddings", templateToType: templateToType),
        ranges: try dictionary.getOptionalArray("ranges", templateToType: templateToType),
        rowSpan: try dictionary.getOptionalExpressionField("row_span"),
        selectable: try dictionary.getOptionalExpressionField("selectable"),
        selectedActions: try dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
        strike: try dictionary.getOptionalExpressionField("strike"),
        text: try dictionary.getOptionalExpressionField("text"),
        textAlignmentHorizontal: try dictionary.getOptionalExpressionField("text_alignment_horizontal"),
        textAlignmentVertical: try dictionary.getOptionalExpressionField("text_alignment_vertical"),
        textColor: try dictionary.getOptionalExpressionField("text_color", transform: Color.color(withHexString:)),
        textGradient: try dictionary.getOptionalField("text_gradient", templateToType: templateToType),
        tooltips: try dictionary.getOptionalArray("tooltips", templateToType: templateToType),
        transform: try dictionary.getOptionalField("transform", templateToType: templateToType),
        transitionChange: try dictionary.getOptionalField("transition_change", templateToType: templateToType),
        transitionIn: try dictionary.getOptionalField("transition_in", templateToType: templateToType),
        transitionOut: try dictionary.getOptionalField("transition_out", templateToType: templateToType),
        transitionTriggers: try dictionary.getOptionalArray("transition_triggers"),
        underline: try dictionary.getOptionalExpressionField("underline"),
        visibility: try dictionary.getOptionalExpressionField("visibility"),
        visibilityAction: try dictionary.getOptionalField("visibility_action", templateToType: templateToType),
        visibilityActions: try dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
        width: try dictionary.getOptionalField("width", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-text_template." + field, representation: representation)
    }
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
    doubletapActions: Field<[DivActionTemplate]>? = nil,
    ellipsis: Field<EllipsisTemplate>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    focusedTextColor: Field<Expression<Color>>? = nil,
    fontFamily: Field<Expression<DivFontFamily>>? = nil,
    fontSize: Field<Expression<Int>>? = nil,
    fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
    fontWeight: Field<Expression<DivFontWeight>>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    images: Field<[ImageTemplate]>? = nil,
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
    text: Field<Expression<CFString>>? = nil,
    textAlignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    textAlignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    textColor: Field<Expression<Color>>? = nil,
    textGradient: Field<DivTextGradientTemplate>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    underline: Field<Expression<DivLineStyle>>? = nil,
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
    self.doubletapActions = doubletapActions
    self.ellipsis = ellipsis
    self.extensions = extensions
    self.focus = focus
    self.focusedTextColor = focusedTextColor
    self.fontFamily = fontFamily
    self.fontSize = fontSize
    self.fontSizeUnit = fontSizeUnit
    self.fontWeight = fontWeight
    self.height = height
    self.id = id
    self.images = images
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
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.underline = underline
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTextTemplate?) -> DeserializationResult<DivText> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.accessibilityValidator, useOnlyLinks: true) ?? .noValue
    let actionValue = parent?.action?.resolveOptionalValue(context: context, validator: ResolvedValue.actionValidator, useOnlyLinks: true) ?? .noValue
    let actionAnimationValue = parent?.actionAnimation?.resolveOptionalValue(context: context, validator: ResolvedValue.actionAnimationValidator, useOnlyLinks: true) ?? .noValue
    let actionsValue = parent?.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentHorizontalValidator) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentVerticalValidator) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let autoEllipsizeValue = parent?.autoEllipsize?.resolveOptionalValue(context: context, validator: ResolvedValue.autoEllipsizeValidator) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let doubletapActionsValue = parent?.doubletapActions?.resolveOptionalValue(context: context, validator: ResolvedValue.doubletapActionsValidator, useOnlyLinks: true) ?? .noValue
    let ellipsisValue = parent?.ellipsis?.resolveOptionalValue(context: context, validator: ResolvedValue.ellipsisValidator, useOnlyLinks: true) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true) ?? .noValue
    let focusedTextColorValue = parent?.focusedTextColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.focusedTextColorValidator) ?? .noValue
    let fontFamilyValue = parent?.fontFamily?.resolveOptionalValue(context: context, validator: ResolvedValue.fontFamilyValidator) ?? .noValue
    let fontSizeValue = parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue
    let fontSizeUnitValue = parent?.fontSizeUnit?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeUnitValidator) ?? .noValue
    let fontWeightValue = parent?.fontWeight?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValidator) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context, validator: ResolvedValue.idValidator) ?? .noValue
    let imagesValue = parent?.images?.resolveOptionalValue(context: context, validator: ResolvedValue.imagesValidator, useOnlyLinks: true) ?? .noValue
    let letterSpacingValue = parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue
    let lineHeightValue = parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue
    let longtapActionsValue = parent?.longtapActions?.resolveOptionalValue(context: context, validator: ResolvedValue.longtapActionsValidator, useOnlyLinks: true) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true) ?? .noValue
    let maxLinesValue = parent?.maxLines?.resolveOptionalValue(context: context, validator: ResolvedValue.maxLinesValidator) ?? .noValue
    let minHiddenLinesValue = parent?.minHiddenLines?.resolveOptionalValue(context: context, validator: ResolvedValue.minHiddenLinesValidator) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true) ?? .noValue
    let rangesValue = parent?.ranges?.resolveOptionalValue(context: context, validator: ResolvedValue.rangesValidator, useOnlyLinks: true) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let selectableValue = parent?.selectable?.resolveOptionalValue(context: context, validator: ResolvedValue.selectableValidator) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true) ?? .noValue
    let strikeValue = parent?.strike?.resolveOptionalValue(context: context, validator: ResolvedValue.strikeValidator) ?? .noValue
    let textValue = parent?.text?.resolveValue(context: context, validator: ResolvedValue.textValidator) ?? .noValue
    let textAlignmentHorizontalValue = parent?.textAlignmentHorizontal?.resolveOptionalValue(context: context, validator: ResolvedValue.textAlignmentHorizontalValidator) ?? .noValue
    let textAlignmentVerticalValue = parent?.textAlignmentVertical?.resolveOptionalValue(context: context, validator: ResolvedValue.textAlignmentVerticalValidator) ?? .noValue
    let textColorValue = parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator) ?? .noValue
    let textGradientValue = parent?.textGradient?.resolveOptionalValue(context: context, validator: ResolvedValue.textGradientValidator, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, validator: ResolvedValue.tooltipsValidator, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, validator: ResolvedValue.transformValidator, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionChangeValidator, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionInValidator, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionOutValidator, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let underlineValue = parent?.underline?.resolveOptionalValue(context: context, validator: ResolvedValue.underlineValidator) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityValidator) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionValidator, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionsValidator, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true) ?? .noValue
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
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      ellipsisValue.errorsOrWarnings?.map { .nestedObjectError(field: "ellipsis", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      focusedTextColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "focused_text_color", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      imagesValue.errorsOrWarnings?.map { .nestedObjectError(field: "images", error: $0) },
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
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) },
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
      doubletapActions: doubletapActionsValue.value,
      ellipsis: ellipsisValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      focusedTextColor: focusedTextColorValue.value,
      fontFamily: fontFamilyValue.value,
      fontSize: fontSizeValue.value,
      fontSizeUnit: fontSizeUnitValue.value,
      fontWeight: fontWeightValue.value,
      height: heightValue.value,
      id: idValue.value,
      images: imagesValue.value,
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
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      underline: underlineValue.value,
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
    var doubletapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var ellipsisValue: DeserializationResult<DivText.Ellipsis> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var focusedTextColorValue: DeserializationResult<Expression<Color>> = parent?.focusedTextColor?.value() ?? .noValue
    var fontFamilyValue: DeserializationResult<Expression<DivFontFamily>> = parent?.fontFamily?.value() ?? .noValue
    var fontSizeValue: DeserializationResult<Expression<Int>> = parent?.fontSize?.value() ?? .noValue
    var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.fontSizeUnit?.value() ?? .noValue
    var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.fontWeight?.value() ?? .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var imagesValue: DeserializationResult<[DivText.Image]> = .noValue
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
    var textValue: DeserializationResult<Expression<CFString>> = parent?.text?.value() ?? .noValue
    var textAlignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.textAlignmentHorizontal?.value() ?? .noValue
    var textAlignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.textAlignmentVertical?.value() ?? .noValue
    var textColorValue: DeserializationResult<Expression<Color>> = parent?.textColor?.value() ?? .noValue
    var textGradientValue: DeserializationResult<DivTextGradient> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue
    var underlineValue: DeserializationResult<Expression<DivLineStyle>> = parent?.underline?.value() ?? .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = parent?.visibility?.value() ?? .noValue
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "accessibility":
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.accessibilityValidator, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "action":
        actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionValidator, type: DivActionTemplate.self).merged(with: actionValue)
      case "action_animation":
        actionAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionAnimationValidator, type: DivAnimationTemplate.self).merged(with: actionAnimationValue)
      case "actions":
        actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self).merged(with: actionsValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue, validator: ResolvedValue.alignmentHorizontalValidator).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue, validator: ResolvedValue.alignmentVerticalValidator).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "auto_ellipsize":
        autoEllipsizeValue = deserialize(__dictValue, validator: ResolvedValue.autoEllipsizeValidator).merged(with: autoEllipsizeValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self).merged(with: borderValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "doubletap_actions":
        doubletapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.doubletapActionsValidator, type: DivActionTemplate.self).merged(with: doubletapActionsValue)
      case "ellipsis":
        ellipsisValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.ellipsisValidator, type: DivTextTemplate.EllipsisTemplate.self).merged(with: ellipsisValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self).merged(with: focusValue)
      case "focused_text_color":
        focusedTextColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.focusedTextColorValidator).merged(with: focusedTextColorValue)
      case "font_family":
        fontFamilyValue = deserialize(__dictValue, validator: ResolvedValue.fontFamilyValidator).merged(with: fontFamilyValue)
      case "font_size":
        fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
      case "font_size_unit":
        fontSizeUnitValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator).merged(with: fontSizeUnitValue)
      case "font_weight":
        fontWeightValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator).merged(with: fontWeightValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator).merged(with: idValue)
      case "images":
        imagesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.imagesValidator, type: DivTextTemplate.ImageTemplate.self).merged(with: imagesValue)
      case "letter_spacing":
        letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
      case "line_height":
        lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
      case "longtap_actions":
        longtapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.longtapActionsValidator, type: DivActionTemplate.self).merged(with: longtapActionsValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "max_lines":
        maxLinesValue = deserialize(__dictValue, validator: ResolvedValue.maxLinesValidator).merged(with: maxLinesValue)
      case "min_hidden_lines":
        minHiddenLinesValue = deserialize(__dictValue, validator: ResolvedValue.minHiddenLinesValidator).merged(with: minHiddenLinesValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "ranges":
        rangesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.rangesValidator, type: DivTextTemplate.RangeTemplate.self).merged(with: rangesValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "selectable":
        selectableValue = deserialize(__dictValue, validator: ResolvedValue.selectableValidator).merged(with: selectableValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "strike":
        strikeValue = deserialize(__dictValue, validator: ResolvedValue.strikeValidator).merged(with: strikeValue)
      case "text":
        textValue = deserialize(__dictValue, validator: ResolvedValue.textValidator).merged(with: textValue)
      case "text_alignment_horizontal":
        textAlignmentHorizontalValue = deserialize(__dictValue, validator: ResolvedValue.textAlignmentHorizontalValidator).merged(with: textAlignmentHorizontalValue)
      case "text_alignment_vertical":
        textAlignmentVerticalValue = deserialize(__dictValue, validator: ResolvedValue.textAlignmentVerticalValidator).merged(with: textAlignmentVerticalValue)
      case "text_color":
        textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator).merged(with: textColorValue)
      case "text_gradient":
        textGradientValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.textGradientValidator, type: DivTextGradientTemplate.self).merged(with: textGradientValue)
      case "tooltips":
        tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tooltipsValidator, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
      case "transform":
        transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transformValidator, type: DivTransformTemplate.self).merged(with: transformValue)
      case "transition_change":
        transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionChangeValidator, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
      case "transition_in":
        transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionInValidator, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
      case "transition_out":
        transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionOutValidator, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
      case "transition_triggers":
        transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
      case "underline":
        underlineValue = deserialize(__dictValue, validator: ResolvedValue.underlineValidator).merged(with: underlineValue)
      case "visibility":
        visibilityValue = deserialize(__dictValue, validator: ResolvedValue.visibilityValidator).merged(with: visibilityValue)
      case "visibility_action":
        visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionValidator, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
      case "visibility_actions":
        visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionsValidator, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
      case "width":
        widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.widthValidator, type: DivSizeTemplate.self).merged(with: widthValue)
      case parent?.accessibility?.link:
        accessibilityValue = accessibilityValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.accessibilityValidator, type: DivAccessibilityTemplate.self))
      case parent?.action?.link:
        actionValue = actionValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionValidator, type: DivActionTemplate.self))
      case parent?.actionAnimation?.link:
        actionAnimationValue = actionAnimationValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionAnimationValidator, type: DivAnimationTemplate.self))
      case parent?.actions?.link:
        actionsValue = actionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.actionsValidator, type: DivActionTemplate.self))
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alignmentHorizontalValidator))
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alignmentVerticalValidator))
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.autoEllipsize?.link:
        autoEllipsizeValue = autoEllipsizeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.autoEllipsizeValidator))
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self))
      case parent?.border?.link:
        borderValue = borderValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self))
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator))
      case parent?.doubletapActions?.link:
        doubletapActionsValue = doubletapActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.doubletapActionsValidator, type: DivActionTemplate.self))
      case parent?.ellipsis?.link:
        ellipsisValue = ellipsisValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.ellipsisValidator, type: DivTextTemplate.EllipsisTemplate.self))
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self))
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self))
      case parent?.focusedTextColor?.link:
        focusedTextColorValue = focusedTextColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.focusedTextColorValidator))
      case parent?.fontFamily?.link:
        fontFamilyValue = fontFamilyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontFamilyValidator))
      case parent?.fontSize?.link:
        fontSizeValue = fontSizeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator))
      case parent?.fontSizeUnit?.link:
        fontSizeUnitValue = fontSizeUnitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator))
      case parent?.fontWeight?.link:
        fontWeightValue = fontWeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator))
      case parent?.height?.link:
        heightValue = heightValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.images?.link:
        imagesValue = imagesValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.imagesValidator, type: DivTextTemplate.ImageTemplate.self))
      case parent?.letterSpacing?.link:
        letterSpacingValue = letterSpacingValue.merged(with: deserialize(__dictValue))
      case parent?.lineHeight?.link:
        lineHeightValue = lineHeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator))
      case parent?.longtapActions?.link:
        longtapActionsValue = longtapActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.longtapActionsValidator, type: DivActionTemplate.self))
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.maxLines?.link:
        maxLinesValue = maxLinesValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.maxLinesValidator))
      case parent?.minHiddenLines?.link:
        minHiddenLinesValue = minHiddenLinesValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.minHiddenLinesValidator))
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.ranges?.link:
        rangesValue = rangesValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.rangesValidator, type: DivTextTemplate.RangeTemplate.self))
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator))
      case parent?.selectable?.link:
        selectableValue = selectableValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.selectableValidator))
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self))
      case parent?.strike?.link:
        strikeValue = strikeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.strikeValidator))
      case parent?.text?.link:
        textValue = textValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.textValidator))
      case parent?.textAlignmentHorizontal?.link:
        textAlignmentHorizontalValue = textAlignmentHorizontalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.textAlignmentHorizontalValidator))
      case parent?.textAlignmentVertical?.link:
        textAlignmentVerticalValue = textAlignmentVerticalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.textAlignmentVerticalValidator))
      case parent?.textColor?.link:
        textColorValue = textColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.textColorValidator))
      case parent?.textGradient?.link:
        textGradientValue = textGradientValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.textGradientValidator, type: DivTextGradientTemplate.self))
      case parent?.tooltips?.link:
        tooltipsValue = tooltipsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tooltipsValidator, type: DivTooltipTemplate.self))
      case parent?.transform?.link:
        transformValue = transformValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transformValidator, type: DivTransformTemplate.self))
      case parent?.transitionChange?.link:
        transitionChangeValue = transitionChangeValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionChangeValidator, type: DivChangeTransitionTemplate.self))
      case parent?.transitionIn?.link:
        transitionInValue = transitionInValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionInValidator, type: DivAppearanceTransitionTemplate.self))
      case parent?.transitionOut?.link:
        transitionOutValue = transitionOutValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.transitionOutValidator, type: DivAppearanceTransitionTemplate.self))
      case parent?.transitionTriggers?.link:
        transitionTriggersValue = transitionTriggersValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator))
      case parent?.underline?.link:
        underlineValue = underlineValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.underlineValidator))
      case parent?.visibility?.link:
        visibilityValue = visibilityValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.visibilityValidator))
      case parent?.visibilityAction?.link:
        visibilityActionValue = visibilityActionValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionValidator, type: DivVisibilityActionTemplate.self))
      case parent?.visibilityActions?.link:
        visibilityActionsValue = visibilityActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.visibilityActionsValidator, type: DivVisibilityActionTemplate.self))
      case parent?.width?.link:
        widthValue = widthValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.widthValidator, type: DivSizeTemplate.self))
      default: break
      }
    }
    if let parent = parent {
      accessibilityValue = accessibilityValue.merged(with: parent.accessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.accessibilityValidator, useOnlyLinks: true))
      actionValue = actionValue.merged(with: parent.action?.resolveOptionalValue(context: context, validator: ResolvedValue.actionValidator, useOnlyLinks: true))
      actionAnimationValue = actionAnimationValue.merged(with: parent.actionAnimation?.resolveOptionalValue(context: context, validator: ResolvedValue.actionAnimationValidator, useOnlyLinks: true))
      actionsValue = actionsValue.merged(with: parent.actions?.resolveOptionalValue(context: context, validator: ResolvedValue.actionsValidator, useOnlyLinks: true))
      backgroundValue = backgroundValue.merged(with: parent.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true))
      borderValue = borderValue.merged(with: parent.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true))
      doubletapActionsValue = doubletapActionsValue.merged(with: parent.doubletapActions?.resolveOptionalValue(context: context, validator: ResolvedValue.doubletapActionsValidator, useOnlyLinks: true))
      ellipsisValue = ellipsisValue.merged(with: parent.ellipsis?.resolveOptionalValue(context: context, validator: ResolvedValue.ellipsisValidator, useOnlyLinks: true))
      extensionsValue = extensionsValue.merged(with: parent.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true))
      focusValue = focusValue.merged(with: parent.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true))
      heightValue = heightValue.merged(with: parent.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true))
      imagesValue = imagesValue.merged(with: parent.images?.resolveOptionalValue(context: context, validator: ResolvedValue.imagesValidator, useOnlyLinks: true))
      longtapActionsValue = longtapActionsValue.merged(with: parent.longtapActions?.resolveOptionalValue(context: context, validator: ResolvedValue.longtapActionsValidator, useOnlyLinks: true))
      marginsValue = marginsValue.merged(with: parent.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true))
      paddingsValue = paddingsValue.merged(with: parent.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true))
      rangesValue = rangesValue.merged(with: parent.ranges?.resolveOptionalValue(context: context, validator: ResolvedValue.rangesValidator, useOnlyLinks: true))
      selectedActionsValue = selectedActionsValue.merged(with: parent.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true))
      textGradientValue = textGradientValue.merged(with: parent.textGradient?.resolveOptionalValue(context: context, validator: ResolvedValue.textGradientValidator, useOnlyLinks: true))
      tooltipsValue = tooltipsValue.merged(with: parent.tooltips?.resolveOptionalValue(context: context, validator: ResolvedValue.tooltipsValidator, useOnlyLinks: true))
      transformValue = transformValue.merged(with: parent.transform?.resolveOptionalValue(context: context, validator: ResolvedValue.transformValidator, useOnlyLinks: true))
      transitionChangeValue = transitionChangeValue.merged(with: parent.transitionChange?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionChangeValidator, useOnlyLinks: true))
      transitionInValue = transitionInValue.merged(with: parent.transitionIn?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionInValidator, useOnlyLinks: true))
      transitionOutValue = transitionOutValue.merged(with: parent.transitionOut?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionOutValidator, useOnlyLinks: true))
      visibilityActionValue = visibilityActionValue.merged(with: parent.visibilityAction?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionValidator, useOnlyLinks: true))
      visibilityActionsValue = visibilityActionsValue.merged(with: parent.visibilityActions?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionsValidator, useOnlyLinks: true))
      widthValue = widthValue.merged(with: parent.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true))
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
      doubletapActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "doubletap_actions", error: $0) },
      ellipsisValue.errorsOrWarnings?.map { .nestedObjectError(field: "ellipsis", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      focusedTextColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "focused_text_color", error: $0) },
      fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
      fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
      fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      imagesValue.errorsOrWarnings?.map { .nestedObjectError(field: "images", error: $0) },
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
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) },
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
      doubletapActions: doubletapActionsValue.value,
      ellipsis: ellipsisValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      focusedTextColor: focusedTextColorValue.value,
      fontFamily: fontFamilyValue.value,
      fontSize: fontSizeValue.value,
      fontSizeUnit: fontSizeUnitValue.value,
      fontWeight: fontWeightValue.value,
      height: heightValue.value,
      id: idValue.value,
      images: imagesValue.value,
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
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      underline: underlineValue.value,
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
      doubletapActions: doubletapActions ?? mergedParent.doubletapActions,
      ellipsis: ellipsis ?? mergedParent.ellipsis,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      focusedTextColor: focusedTextColor ?? mergedParent.focusedTextColor,
      fontFamily: fontFamily ?? mergedParent.fontFamily,
      fontSize: fontSize ?? mergedParent.fontSize,
      fontSizeUnit: fontSizeUnit ?? mergedParent.fontSizeUnit,
      fontWeight: fontWeight ?? mergedParent.fontWeight,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      images: images ?? mergedParent.images,
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
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      underline: underline ?? mergedParent.underline,
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
      doubletapActions: merged.doubletapActions?.tryResolveParent(templates: templates),
      ellipsis: merged.ellipsis?.tryResolveParent(templates: templates),
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      focusedTextColor: merged.focusedTextColor,
      fontFamily: merged.fontFamily,
      fontSize: merged.fontSize,
      fontSizeUnit: merged.fontSizeUnit,
      fontWeight: merged.fontWeight,
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      images: merged.images?.tryResolveParent(templates: templates),
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
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      underline: merged.underline,
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
