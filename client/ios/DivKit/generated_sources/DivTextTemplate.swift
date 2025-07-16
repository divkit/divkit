// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivTextTemplate: TemplateValue, @unchecked Sendable {
  public final class EllipsisTemplate: TemplateValue, Sendable {
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
      let actionsValue = { parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let imagesValue = { parent?.images?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let rangesValue = { parent?.ranges?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let textValue = { parent?.text?.resolveValue(context: context) ?? .noValue }()
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
        actions: { actionsValue.value }(),
        images: { imagesValue.value }(),
        ranges: { rangesValue.value }(),
        text: { textNonNil }()
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
      var textValue: DeserializationResult<Expression<String>> = { parent?.text?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "actions" {
             actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
            }
          }()
          _ = {
            if key == "images" {
             imagesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.self).merged(with: imagesValue)
            }
          }()
          _ = {
            if key == "ranges" {
             rangesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.RangeTemplate.self).merged(with: rangesValue)
            }
          }()
          _ = {
            if key == "text" {
             textValue = deserialize(__dictValue).merged(with: textValue)
            }
          }()
          _ = {
           if key == parent?.actions?.link {
             actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.images?.link {
             imagesValue = imagesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.ranges?.link {
             rangesValue = rangesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.RangeTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.text?.link {
             textValue = textValue.merged(with: { deserialize(__dictValue) })
            }
          }()
        }
      }()
      if let parent = parent {
        _ = { actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
        _ = { imagesValue = imagesValue.merged(with: { parent.images?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
        _ = { rangesValue = rangesValue.merged(with: { parent.ranges?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
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
        actions: { actionsValue.value }(),
        images: { imagesValue.value }(),
        ranges: { rangesValue.value }(),
        text: { textNonNil }()
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

  public final class ImageTemplate: TemplateValue, Sendable {
    public final class AccessibilityTemplate: TemplateValue, Sendable {
      public typealias Kind = DivText.Image.Accessibility.Kind

      public let description: Field<Expression<String>>?
      public let type: Field<Kind>? // default value: auto

      public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
        self.init(
          description: dictionary.getOptionalExpressionField("description"),
          type: dictionary.getOptionalField("type")
        )
      }

      init(
        description: Field<Expression<String>>? = nil,
        type: Field<Kind>? = nil
      ) {
        self.description = description
        self.type = type
      }

      private static func resolveOnlyLinks(context: TemplatesContext, parent: AccessibilityTemplate?) -> DeserializationResult<DivText.Image.Accessibility> {
        let descriptionValue = { parent?.description?.resolveOptionalValue(context: context) ?? .noValue }()
        let typeValue = { parent?.type?.resolveOptionalValue(context: context) ?? .noValue }()
        let errors = mergeErrors(
          descriptionValue.errorsOrWarnings?.map { .nestedObjectError(field: "description", error: $0) },
          typeValue.errorsOrWarnings?.map { .nestedObjectError(field: "type", error: $0) }
        )
        let result = DivText.Image.Accessibility(
          description: { descriptionValue.value }(),
          type: { typeValue.value }()
        )
        return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
      }

      public static func resolveValue(context: TemplatesContext, parent: AccessibilityTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivText.Image.Accessibility> {
        if useOnlyLinks {
          return resolveOnlyLinks(context: context, parent: parent)
        }
        var descriptionValue: DeserializationResult<Expression<String>> = { parent?.description?.value() ?? .noValue }()
        var typeValue: DeserializationResult<DivText.Image.Accessibility.Kind> = { parent?.type?.value() ?? .noValue }()
        _ = {
          // Each field is parsed in its own lambda to keep the stack size managable
          // Otherwise the compiler will allocate stack for each intermediate variable
          // upfront even when we don't actually visit a relevant branch
          for (key, __dictValue) in context.templateData {
            _ = {
              if key == "description" {
               descriptionValue = deserialize(__dictValue).merged(with: descriptionValue)
              }
            }()
            _ = {
              if key == "type" {
               typeValue = deserialize(__dictValue).merged(with: typeValue)
              }
            }()
            _ = {
             if key == parent?.description?.link {
               descriptionValue = descriptionValue.merged(with: { deserialize(__dictValue) })
              }
            }()
            _ = {
             if key == parent?.type?.link {
               typeValue = typeValue.merged(with: { deserialize(__dictValue) })
              }
            }()
          }
        }()
        let errors = mergeErrors(
          descriptionValue.errorsOrWarnings?.map { .nestedObjectError(field: "description", error: $0) },
          typeValue.errorsOrWarnings?.map { .nestedObjectError(field: "type", error: $0) }
        )
        let result = DivText.Image.Accessibility(
          description: { descriptionValue.value }(),
          type: { typeValue.value }()
        )
        return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
      }

      private func mergedWithParent(templates: [TemplateName: Any]) throws -> AccessibilityTemplate {
        return self
      }

      public func resolveParent(templates: [TemplateName: Any]) throws -> AccessibilityTemplate {
        return try mergedWithParent(templates: templates)
      }
    }

    public typealias IndexingDirection = DivText.Image.IndexingDirection

    public let accessibility: Field<AccessibilityTemplate>?
    public let alignmentVertical: Field<Expression<DivTextAlignmentVertical>>? // default value: center
    public let height: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(20))
    public let indexingDirection: Field<Expression<IndexingDirection>>? // default value: normal
    public let preloadRequired: Field<Expression<Bool>>? // default value: false
    public let start: Field<Expression<Int>>? // constraint: number >= 0
    public let tintColor: Field<Expression<Color>>?
    public let tintMode: Field<Expression<DivBlendMode>>? // default value: source_in
    public let url: Field<Expression<URL>>?
    public let width: Field<DivFixedSizeTemplate>? // default value: DivFixedSize(value: .value(20))

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        accessibility: dictionary.getOptionalField("accessibility", templateToType: templateToType),
        alignmentVertical: dictionary.getOptionalExpressionField("alignment_vertical"),
        height: dictionary.getOptionalField("height", templateToType: templateToType),
        indexingDirection: dictionary.getOptionalExpressionField("indexing_direction"),
        preloadRequired: dictionary.getOptionalExpressionField("preload_required"),
        start: dictionary.getOptionalExpressionField("start"),
        tintColor: dictionary.getOptionalExpressionField("tint_color", transform: Color.color(withHexString:)),
        tintMode: dictionary.getOptionalExpressionField("tint_mode"),
        url: dictionary.getOptionalExpressionField("url", transform: URL.makeFromNonEncodedString),
        width: dictionary.getOptionalField("width", templateToType: templateToType)
      )
    }

    init(
      accessibility: Field<AccessibilityTemplate>? = nil,
      alignmentVertical: Field<Expression<DivTextAlignmentVertical>>? = nil,
      height: Field<DivFixedSizeTemplate>? = nil,
      indexingDirection: Field<Expression<IndexingDirection>>? = nil,
      preloadRequired: Field<Expression<Bool>>? = nil,
      start: Field<Expression<Int>>? = nil,
      tintColor: Field<Expression<Color>>? = nil,
      tintMode: Field<Expression<DivBlendMode>>? = nil,
      url: Field<Expression<URL>>? = nil,
      width: Field<DivFixedSizeTemplate>? = nil
    ) {
      self.accessibility = accessibility
      self.alignmentVertical = alignmentVertical
      self.height = height
      self.indexingDirection = indexingDirection
      self.preloadRequired = preloadRequired
      self.start = start
      self.tintColor = tintColor
      self.tintMode = tintMode
      self.url = url
      self.width = width
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ImageTemplate?) -> DeserializationResult<DivText.Image> {
      let accessibilityValue = { parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let alignmentVerticalValue = { parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
      let heightValue = { parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let indexingDirectionValue = { parent?.indexingDirection?.resolveOptionalValue(context: context) ?? .noValue }()
      let preloadRequiredValue = { parent?.preloadRequired?.resolveOptionalValue(context: context) ?? .noValue }()
      let startValue = { parent?.start?.resolveValue(context: context, validator: ResolvedValue.startValidator) ?? .noValue }()
      let tintColorValue = { parent?.tintColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
      let tintModeValue = { parent?.tintMode?.resolveOptionalValue(context: context) ?? .noValue }()
      let urlValue = { parent?.url?.resolveValue(context: context, transform: URL.makeFromNonEncodedString) ?? .noValue }()
      let widthValue = { parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      var errors = mergeErrors(
        accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
        alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
        heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
        indexingDirectionValue.errorsOrWarnings?.map { .nestedObjectError(field: "indexing_direction", error: $0) },
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
        accessibility: { accessibilityValue.value }(),
        alignmentVertical: { alignmentVerticalValue.value }(),
        height: { heightValue.value }(),
        indexingDirection: { indexingDirectionValue.value }(),
        preloadRequired: { preloadRequiredValue.value }(),
        start: { startNonNil }(),
        tintColor: { tintColorValue.value }(),
        tintMode: { tintModeValue.value }(),
        url: { urlNonNil }(),
        width: { widthValue.value }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ImageTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivText.Image> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var accessibilityValue: DeserializationResult<DivText.Image.Accessibility> = .noValue
      var alignmentVerticalValue: DeserializationResult<Expression<DivTextAlignmentVertical>> = { parent?.alignmentVertical?.value() ?? .noValue }()
      var heightValue: DeserializationResult<DivFixedSize> = .noValue
      var indexingDirectionValue: DeserializationResult<Expression<DivText.Image.IndexingDirection>> = { parent?.indexingDirection?.value() ?? .noValue }()
      var preloadRequiredValue: DeserializationResult<Expression<Bool>> = { parent?.preloadRequired?.value() ?? .noValue }()
      var startValue: DeserializationResult<Expression<Int>> = { parent?.start?.value() ?? .noValue }()
      var tintColorValue: DeserializationResult<Expression<Color>> = { parent?.tintColor?.value() ?? .noValue }()
      var tintModeValue: DeserializationResult<Expression<DivBlendMode>> = { parent?.tintMode?.value() ?? .noValue }()
      var urlValue: DeserializationResult<Expression<URL>> = { parent?.url?.value() ?? .noValue }()
      var widthValue: DeserializationResult<DivFixedSize> = .noValue
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "accessibility" {
             accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.AccessibilityTemplate.self).merged(with: accessibilityValue)
            }
          }()
          _ = {
            if key == "alignment_vertical" {
             alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
            }
          }()
          _ = {
            if key == "height" {
             heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: heightValue)
            }
          }()
          _ = {
            if key == "indexing_direction" {
             indexingDirectionValue = deserialize(__dictValue).merged(with: indexingDirectionValue)
            }
          }()
          _ = {
            if key == "preload_required" {
             preloadRequiredValue = deserialize(__dictValue).merged(with: preloadRequiredValue)
            }
          }()
          _ = {
            if key == "start" {
             startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).merged(with: startValue)
            }
          }()
          _ = {
            if key == "tint_color" {
             tintColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: tintColorValue)
            }
          }()
          _ = {
            if key == "tint_mode" {
             tintModeValue = deserialize(__dictValue).merged(with: tintModeValue)
            }
          }()
          _ = {
            if key == "url" {
             urlValue = deserialize(__dictValue, transform: URL.makeFromNonEncodedString).merged(with: urlValue)
            }
          }()
          _ = {
            if key == "width" {
             widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self).merged(with: widthValue)
            }
          }()
          _ = {
           if key == parent?.accessibility?.link {
             accessibilityValue = accessibilityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.AccessibilityTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.alignmentVertical?.link {
             alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.height?.link {
             heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.indexingDirection?.link {
             indexingDirectionValue = indexingDirectionValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.preloadRequired?.link {
             preloadRequiredValue = preloadRequiredValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.start?.link {
             startValue = startValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startValidator) })
            }
          }()
          _ = {
           if key == parent?.tintColor?.link {
             tintColorValue = tintColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
            }
          }()
          _ = {
           if key == parent?.tintMode?.link {
             tintModeValue = tintModeValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.url?.link {
             urlValue = urlValue.merged(with: { deserialize(__dictValue, transform: URL.makeFromNonEncodedString) })
            }
          }()
          _ = {
           if key == parent?.width?.link {
             widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFixedSizeTemplate.self) })
            }
          }()
        }
      }()
      if let parent = parent {
        _ = { accessibilityValue = accessibilityValue.merged(with: { parent.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
        _ = { heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
        _ = { widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      }
      var errors = mergeErrors(
        accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
        alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
        heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
        indexingDirectionValue.errorsOrWarnings?.map { .nestedObjectError(field: "indexing_direction", error: $0) },
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
        accessibility: { accessibilityValue.value }(),
        alignmentVertical: { alignmentVerticalValue.value }(),
        height: { heightValue.value }(),
        indexingDirection: { indexingDirectionValue.value }(),
        preloadRequired: { preloadRequiredValue.value }(),
        start: { startNonNil }(),
        tintColor: { tintColorValue.value }(),
        tintMode: { tintModeValue.value }(),
        url: { urlNonNil }(),
        width: { widthValue.value }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ImageTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ImageTemplate {
      let merged = try mergedWithParent(templates: templates)

      return ImageTemplate(
        accessibility: merged.accessibility?.tryResolveParent(templates: templates),
        alignmentVertical: merged.alignmentVertical,
        height: merged.height?.tryResolveParent(templates: templates),
        indexingDirection: merged.indexingDirection,
        preloadRequired: merged.preloadRequired,
        start: merged.start,
        tintColor: merged.tintColor,
        tintMode: merged.tintMode,
        url: merged.url,
        width: merged.width?.tryResolveParent(templates: templates)
      )
    }
  }

  public final class RangeTemplate: TemplateValue, @unchecked Sendable {
    public let actions: Field<[DivActionTemplate]>?
    public let alignmentVertical: Field<Expression<DivTextAlignmentVertical>>?
    public let background: Field<DivTextRangeBackgroundTemplate>?
    public let baselineOffset: Field<Expression<Double>>? // default value: 0
    public let border: Field<DivTextRangeBorderTemplate>?
    public let end: Field<Expression<Int>>? // constraint: number > 0
    public let fontFamily: Field<Expression<String>>?
    public let fontFeatureSettings: Field<Expression<String>>?
    public let fontSize: Field<Expression<Int>>? // constraint: number >= 0
    public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
    public let fontVariationSettings: Field<Expression<[String: Any]>>?
    public let fontWeight: Field<Expression<DivFontWeight>>?
    public let fontWeightValue: Field<Expression<Int>>? // constraint: number > 0
    public let letterSpacing: Field<Expression<Double>>?
    public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
    public let mask: Field<DivTextRangeMaskTemplate>?
    public let start: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
    public let strike: Field<Expression<DivLineStyle>>?
    public let textColor: Field<Expression<Color>>?
    public let textShadow: Field<DivShadowTemplate>?
    public let topOffset: Field<Expression<Int>>? // constraint: number >= 0
    public let underline: Field<Expression<DivLineStyle>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        actions: dictionary.getOptionalArray("actions", templateToType: templateToType),
        alignmentVertical: dictionary.getOptionalExpressionField("alignment_vertical"),
        background: dictionary.getOptionalField("background", templateToType: templateToType),
        baselineOffset: dictionary.getOptionalExpressionField("baseline_offset"),
        border: dictionary.getOptionalField("border", templateToType: templateToType),
        end: dictionary.getOptionalExpressionField("end"),
        fontFamily: dictionary.getOptionalExpressionField("font_family"),
        fontFeatureSettings: dictionary.getOptionalExpressionField("font_feature_settings"),
        fontSize: dictionary.getOptionalExpressionField("font_size"),
        fontSizeUnit: dictionary.getOptionalExpressionField("font_size_unit"),
        fontVariationSettings: dictionary.getOptionalExpressionField("font_variation_settings"),
        fontWeight: dictionary.getOptionalExpressionField("font_weight"),
        fontWeightValue: dictionary.getOptionalExpressionField("font_weight_value"),
        letterSpacing: dictionary.getOptionalExpressionField("letter_spacing"),
        lineHeight: dictionary.getOptionalExpressionField("line_height"),
        mask: dictionary.getOptionalField("mask", templateToType: templateToType),
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
      alignmentVertical: Field<Expression<DivTextAlignmentVertical>>? = nil,
      background: Field<DivTextRangeBackgroundTemplate>? = nil,
      baselineOffset: Field<Expression<Double>>? = nil,
      border: Field<DivTextRangeBorderTemplate>? = nil,
      end: Field<Expression<Int>>? = nil,
      fontFamily: Field<Expression<String>>? = nil,
      fontFeatureSettings: Field<Expression<String>>? = nil,
      fontSize: Field<Expression<Int>>? = nil,
      fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
      fontVariationSettings: Field<Expression<[String: Any]>>? = nil,
      fontWeight: Field<Expression<DivFontWeight>>? = nil,
      fontWeightValue: Field<Expression<Int>>? = nil,
      letterSpacing: Field<Expression<Double>>? = nil,
      lineHeight: Field<Expression<Int>>? = nil,
      mask: Field<DivTextRangeMaskTemplate>? = nil,
      start: Field<Expression<Int>>? = nil,
      strike: Field<Expression<DivLineStyle>>? = nil,
      textColor: Field<Expression<Color>>? = nil,
      textShadow: Field<DivShadowTemplate>? = nil,
      topOffset: Field<Expression<Int>>? = nil,
      underline: Field<Expression<DivLineStyle>>? = nil
    ) {
      self.actions = actions
      self.alignmentVertical = alignmentVertical
      self.background = background
      self.baselineOffset = baselineOffset
      self.border = border
      self.end = end
      self.fontFamily = fontFamily
      self.fontFeatureSettings = fontFeatureSettings
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit
      self.fontVariationSettings = fontVariationSettings
      self.fontWeight = fontWeight
      self.fontWeightValue = fontWeightValue
      self.letterSpacing = letterSpacing
      self.lineHeight = lineHeight
      self.mask = mask
      self.start = start
      self.strike = strike
      self.textColor = textColor
      self.textShadow = textShadow
      self.topOffset = topOffset
      self.underline = underline
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: RangeTemplate?) -> DeserializationResult<DivText.Range> {
      let actionsValue = { parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let alignmentVerticalValue = { parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
      let backgroundValue = { parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let baselineOffsetValue = { parent?.baselineOffset?.resolveOptionalValue(context: context) ?? .noValue }()
      let borderValue = { parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let endValue = { parent?.end?.resolveOptionalValue(context: context, validator: ResolvedValue.endValidator) ?? .noValue }()
      let fontFamilyValue = { parent?.fontFamily?.resolveOptionalValue(context: context) ?? .noValue }()
      let fontFeatureSettingsValue = { parent?.fontFeatureSettings?.resolveOptionalValue(context: context) ?? .noValue }()
      let fontSizeValue = { parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue }()
      let fontSizeUnitValue = { parent?.fontSizeUnit?.resolveOptionalValue(context: context) ?? .noValue }()
      let fontVariationSettingsValue = { parent?.fontVariationSettings?.resolveOptionalValue(context: context) ?? .noValue }()
      let fontWeightValue = { parent?.fontWeight?.resolveOptionalValue(context: context) ?? .noValue }()
      let fontWeightValueValue = { parent?.fontWeightValue?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValueValidator) ?? .noValue }()
      let letterSpacingValue = { parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue }()
      let lineHeightValue = { parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue }()
      let maskValue = { parent?.mask?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let startValue = { parent?.start?.resolveOptionalValue(context: context, validator: ResolvedValue.startValidator) ?? .noValue }()
      let strikeValue = { parent?.strike?.resolveOptionalValue(context: context) ?? .noValue }()
      let textColorValue = { parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
      let textShadowValue = { parent?.textShadow?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let topOffsetValue = { parent?.topOffset?.resolveOptionalValue(context: context, validator: ResolvedValue.topOffsetValidator) ?? .noValue }()
      let underlineValue = { parent?.underline?.resolveOptionalValue(context: context) ?? .noValue }()
      let errors = mergeErrors(
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
        backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
        baselineOffsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "baseline_offset", error: $0) },
        borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
        endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
        fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
        fontFeatureSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_feature_settings", error: $0) },
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontVariationSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_variation_settings", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
        letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
        lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
        maskValue.errorsOrWarnings?.map { .nestedObjectError(field: "mask", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
        strikeValue.errorsOrWarnings?.map { .nestedObjectError(field: "strike", error: $0) },
        textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
        textShadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_shadow", error: $0) },
        topOffsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "top_offset", error: $0) },
        underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) }
      )
      let result = DivText.Range(
        actions: { actionsValue.value }(),
        alignmentVertical: { alignmentVerticalValue.value }(),
        background: { backgroundValue.value }(),
        baselineOffset: { baselineOffsetValue.value }(),
        border: { borderValue.value }(),
        end: { endValue.value }(),
        fontFamily: { fontFamilyValue.value }(),
        fontFeatureSettings: { fontFeatureSettingsValue.value }(),
        fontSize: { fontSizeValue.value }(),
        fontSizeUnit: { fontSizeUnitValue.value }(),
        fontVariationSettings: { fontVariationSettingsValue.value }(),
        fontWeight: { fontWeightValue.value }(),
        fontWeightValue: { fontWeightValueValue.value }(),
        letterSpacing: { letterSpacingValue.value }(),
        lineHeight: { lineHeightValue.value }(),
        mask: { maskValue.value }(),
        start: { startValue.value }(),
        strike: { strikeValue.value }(),
        textColor: { textColorValue.value }(),
        textShadow: { textShadowValue.value }(),
        topOffset: { topOffsetValue.value }(),
        underline: { underlineValue.value }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: RangeTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivText.Range> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var actionsValue: DeserializationResult<[DivAction]> = .noValue
      var alignmentVerticalValue: DeserializationResult<Expression<DivTextAlignmentVertical>> = { parent?.alignmentVertical?.value() ?? .noValue }()
      var backgroundValue: DeserializationResult<DivTextRangeBackground> = .noValue
      var baselineOffsetValue: DeserializationResult<Expression<Double>> = { parent?.baselineOffset?.value() ?? .noValue }()
      var borderValue: DeserializationResult<DivTextRangeBorder> = .noValue
      var endValue: DeserializationResult<Expression<Int>> = { parent?.end?.value() ?? .noValue }()
      var fontFamilyValue: DeserializationResult<Expression<String>> = { parent?.fontFamily?.value() ?? .noValue }()
      var fontFeatureSettingsValue: DeserializationResult<Expression<String>> = { parent?.fontFeatureSettings?.value() ?? .noValue }()
      var fontSizeValue: DeserializationResult<Expression<Int>> = { parent?.fontSize?.value() ?? .noValue }()
      var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = { parent?.fontSizeUnit?.value() ?? .noValue }()
      var fontVariationSettingsValue: DeserializationResult<Expression<[String: Any]>> = { parent?.fontVariationSettings?.value() ?? .noValue }()
      var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = { parent?.fontWeight?.value() ?? .noValue }()
      var fontWeightValueValue: DeserializationResult<Expression<Int>> = { parent?.fontWeightValue?.value() ?? .noValue }()
      var letterSpacingValue: DeserializationResult<Expression<Double>> = { parent?.letterSpacing?.value() ?? .noValue }()
      var lineHeightValue: DeserializationResult<Expression<Int>> = { parent?.lineHeight?.value() ?? .noValue }()
      var maskValue: DeserializationResult<DivTextRangeMask> = .noValue
      var startValue: DeserializationResult<Expression<Int>> = { parent?.start?.value() ?? .noValue }()
      var strikeValue: DeserializationResult<Expression<DivLineStyle>> = { parent?.strike?.value() ?? .noValue }()
      var textColorValue: DeserializationResult<Expression<Color>> = { parent?.textColor?.value() ?? .noValue }()
      var textShadowValue: DeserializationResult<DivShadow> = .noValue
      var topOffsetValue: DeserializationResult<Expression<Int>> = { parent?.topOffset?.value() ?? .noValue }()
      var underlineValue: DeserializationResult<Expression<DivLineStyle>> = { parent?.underline?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "actions" {
             actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
            }
          }()
          _ = {
            if key == "alignment_vertical" {
             alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
            }
          }()
          _ = {
            if key == "background" {
             backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeBackgroundTemplate.self).merged(with: backgroundValue)
            }
          }()
          _ = {
            if key == "baseline_offset" {
             baselineOffsetValue = deserialize(__dictValue).merged(with: baselineOffsetValue)
            }
          }()
          _ = {
            if key == "border" {
             borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeBorderTemplate.self).merged(with: borderValue)
            }
          }()
          _ = {
            if key == "end" {
             endValue = deserialize(__dictValue, validator: ResolvedValue.endValidator).merged(with: endValue)
            }
          }()
          _ = {
            if key == "font_family" {
             fontFamilyValue = deserialize(__dictValue).merged(with: fontFamilyValue)
            }
          }()
          _ = {
            if key == "font_feature_settings" {
             fontFeatureSettingsValue = deserialize(__dictValue).merged(with: fontFeatureSettingsValue)
            }
          }()
          _ = {
            if key == "font_size" {
             fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
            }
          }()
          _ = {
            if key == "font_size_unit" {
             fontSizeUnitValue = deserialize(__dictValue).merged(with: fontSizeUnitValue)
            }
          }()
          _ = {
            if key == "font_variation_settings" {
             fontVariationSettingsValue = deserialize(__dictValue).merged(with: fontVariationSettingsValue)
            }
          }()
          _ = {
            if key == "font_weight" {
             fontWeightValue = deserialize(__dictValue).merged(with: fontWeightValue)
            }
          }()
          _ = {
            if key == "font_weight_value" {
             fontWeightValueValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator).merged(with: fontWeightValueValue)
            }
          }()
          _ = {
            if key == "letter_spacing" {
             letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
            }
          }()
          _ = {
            if key == "line_height" {
             lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
            }
          }()
          _ = {
            if key == "mask" {
             maskValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeMaskTemplate.self).merged(with: maskValue)
            }
          }()
          _ = {
            if key == "start" {
             startValue = deserialize(__dictValue, validator: ResolvedValue.startValidator).merged(with: startValue)
            }
          }()
          _ = {
            if key == "strike" {
             strikeValue = deserialize(__dictValue).merged(with: strikeValue)
            }
          }()
          _ = {
            if key == "text_color" {
             textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: textColorValue)
            }
          }()
          _ = {
            if key == "text_shadow" {
             textShadowValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self).merged(with: textShadowValue)
            }
          }()
          _ = {
            if key == "top_offset" {
             topOffsetValue = deserialize(__dictValue, validator: ResolvedValue.topOffsetValidator).merged(with: topOffsetValue)
            }
          }()
          _ = {
            if key == "underline" {
             underlineValue = deserialize(__dictValue).merged(with: underlineValue)
            }
          }()
          _ = {
           if key == parent?.actions?.link {
             actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.alignmentVertical?.link {
             alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.background?.link {
             backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeBackgroundTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.baselineOffset?.link {
             baselineOffsetValue = baselineOffsetValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.border?.link {
             borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeBorderTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.end?.link {
             endValue = endValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.endValidator) })
            }
          }()
          _ = {
           if key == parent?.fontFamily?.link {
             fontFamilyValue = fontFamilyValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.fontFeatureSettings?.link {
             fontFeatureSettingsValue = fontFeatureSettingsValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.fontSize?.link {
             fontSizeValue = fontSizeValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator) })
            }
          }()
          _ = {
           if key == parent?.fontSizeUnit?.link {
             fontSizeUnitValue = fontSizeUnitValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.fontVariationSettings?.link {
             fontVariationSettingsValue = fontVariationSettingsValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.fontWeight?.link {
             fontWeightValue = fontWeightValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.fontWeightValue?.link {
             fontWeightValueValue = fontWeightValueValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator) })
            }
          }()
          _ = {
           if key == parent?.letterSpacing?.link {
             letterSpacingValue = letterSpacingValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.lineHeight?.link {
             lineHeightValue = lineHeightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator) })
            }
          }()
          _ = {
           if key == parent?.mask?.link {
             maskValue = maskValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextRangeMaskTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.start?.link {
             startValue = startValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.startValidator) })
            }
          }()
          _ = {
           if key == parent?.strike?.link {
             strikeValue = strikeValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.textColor?.link {
             textColorValue = textColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
            }
          }()
          _ = {
           if key == parent?.textShadow?.link {
             textShadowValue = textShadowValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.topOffset?.link {
             topOffsetValue = topOffsetValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.topOffsetValidator) })
            }
          }()
          _ = {
           if key == parent?.underline?.link {
             underlineValue = underlineValue.merged(with: { deserialize(__dictValue) })
            }
          }()
        }
      }()
      if let parent = parent {
        _ = { actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
        _ = { backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
        _ = { borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
        _ = { maskValue = maskValue.merged(with: { parent.mask?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
        _ = { textShadowValue = textShadowValue.merged(with: { parent.textShadow?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      }
      let errors = mergeErrors(
        actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
        alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
        backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
        baselineOffsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "baseline_offset", error: $0) },
        borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
        endValue.errorsOrWarnings?.map { .nestedObjectError(field: "end", error: $0) },
        fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
        fontFeatureSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_feature_settings", error: $0) },
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontVariationSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_variation_settings", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
        letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
        lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
        maskValue.errorsOrWarnings?.map { .nestedObjectError(field: "mask", error: $0) },
        startValue.errorsOrWarnings?.map { .nestedObjectError(field: "start", error: $0) },
        strikeValue.errorsOrWarnings?.map { .nestedObjectError(field: "strike", error: $0) },
        textColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_color", error: $0) },
        textShadowValue.errorsOrWarnings?.map { .nestedObjectError(field: "text_shadow", error: $0) },
        topOffsetValue.errorsOrWarnings?.map { .nestedObjectError(field: "top_offset", error: $0) },
        underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) }
      )
      let result = DivText.Range(
        actions: { actionsValue.value }(),
        alignmentVertical: { alignmentVerticalValue.value }(),
        background: { backgroundValue.value }(),
        baselineOffset: { baselineOffsetValue.value }(),
        border: { borderValue.value }(),
        end: { endValue.value }(),
        fontFamily: { fontFamilyValue.value }(),
        fontFeatureSettings: { fontFeatureSettingsValue.value }(),
        fontSize: { fontSizeValue.value }(),
        fontSizeUnit: { fontSizeUnitValue.value }(),
        fontVariationSettings: { fontVariationSettingsValue.value }(),
        fontWeight: { fontWeightValue.value }(),
        fontWeightValue: { fontWeightValueValue.value }(),
        letterSpacing: { letterSpacingValue.value }(),
        lineHeight: { lineHeightValue.value }(),
        mask: { maskValue.value }(),
        start: { startValue.value }(),
        strike: { strikeValue.value }(),
        textColor: { textColorValue.value }(),
        textShadow: { textShadowValue.value }(),
        topOffset: { topOffsetValue.value }(),
        underline: { underlineValue.value }()
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
        alignmentVertical: merged.alignmentVertical,
        background: merged.background?.tryResolveParent(templates: templates),
        baselineOffset: merged.baselineOffset,
        border: merged.border?.tryResolveParent(templates: templates),
        end: merged.end,
        fontFamily: merged.fontFamily,
        fontFeatureSettings: merged.fontFeatureSettings,
        fontSize: merged.fontSize,
        fontSizeUnit: merged.fontSizeUnit,
        fontVariationSettings: merged.fontVariationSettings,
        fontWeight: merged.fontWeight,
        fontWeightValue: merged.fontWeightValue,
        letterSpacing: merged.letterSpacing,
        lineHeight: merged.lineHeight,
        mask: merged.mask?.tryResolveParent(templates: templates),
        start: merged.start,
        strike: merged.strike,
        textColor: merged.textColor,
        textShadow: merged.textShadow?.tryResolveParent(templates: templates),
        topOffset: merged.topOffset,
        underline: merged.underline
      )
    }
  }

  public typealias Truncate = DivText.Truncate

  public static let type: String = "text"
  public let parent: String?
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let action: Field<DivActionTemplate>?
  public let actionAnimation: Field<DivAnimationTemplate>? // default value: DivAnimation(duration: .value(100), endValue: .value(0.6), name: .value(.fade), startValue: .value(1))
  public let actions: Field<[DivActionTemplate]>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let animators: Field<[DivAnimatorTemplate]>?
  public let autoEllipsize: Field<Expression<Bool>>?
  public let background: Field<[DivBackgroundTemplate]>?
  public let border: Field<DivBorderTemplate>?
  public let captureFocusOnAction: Field<Expression<Bool>>? // default value: true
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
  public let fontVariationSettings: Field<Expression<[String: Any]>>?
  public let fontWeight: Field<Expression<DivFontWeight>>?
  public let fontWeightValue: Field<Expression<Int>>? // constraint: number > 0
  public let functions: Field<[DivFunctionTemplate]>?
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let hoverEndActions: Field<[DivActionTemplate]>?
  public let hoverStartActions: Field<[DivActionTemplate]>?
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
  public let pressEndActions: Field<[DivActionTemplate]>?
  public let pressStartActions: Field<[DivActionTemplate]>?
  public let ranges: Field<[RangeTemplate]>?
  public let reuseId: Field<Expression<String>>?
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
  public let tightenWidth: Field<Expression<Bool>>? // default value: false
  public let tooltips: Field<[DivTooltipTemplate]>?
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
  public let truncate: Field<Expression<Truncate>>? // default value: end
  public let underline: Field<Expression<DivLineStyle>>? // default value: none
  public let variableTriggers: Field<[DivTriggerTemplate]>?
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
      animators: dictionary.getOptionalArray("animators", templateToType: templateToType),
      autoEllipsize: dictionary.getOptionalExpressionField("auto_ellipsize"),
      background: dictionary.getOptionalArray("background", templateToType: templateToType),
      border: dictionary.getOptionalField("border", templateToType: templateToType),
      captureFocusOnAction: dictionary.getOptionalExpressionField("capture_focus_on_action"),
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
      fontVariationSettings: dictionary.getOptionalExpressionField("font_variation_settings"),
      fontWeight: dictionary.getOptionalExpressionField("font_weight"),
      fontWeightValue: dictionary.getOptionalExpressionField("font_weight_value"),
      functions: dictionary.getOptionalArray("functions", templateToType: templateToType),
      height: dictionary.getOptionalField("height", templateToType: templateToType),
      hoverEndActions: dictionary.getOptionalArray("hover_end_actions", templateToType: templateToType),
      hoverStartActions: dictionary.getOptionalArray("hover_start_actions", templateToType: templateToType),
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
      pressEndActions: dictionary.getOptionalArray("press_end_actions", templateToType: templateToType),
      pressStartActions: dictionary.getOptionalArray("press_start_actions", templateToType: templateToType),
      ranges: dictionary.getOptionalArray("ranges", templateToType: templateToType),
      reuseId: dictionary.getOptionalExpressionField("reuse_id"),
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
      tightenWidth: dictionary.getOptionalExpressionField("tighten_width"),
      tooltips: dictionary.getOptionalArray("tooltips", templateToType: templateToType),
      transform: dictionary.getOptionalField("transform", templateToType: templateToType),
      transitionChange: dictionary.getOptionalField("transition_change", templateToType: templateToType),
      transitionIn: dictionary.getOptionalField("transition_in", templateToType: templateToType),
      transitionOut: dictionary.getOptionalField("transition_out", templateToType: templateToType),
      transitionTriggers: dictionary.getOptionalArray("transition_triggers"),
      truncate: dictionary.getOptionalExpressionField("truncate"),
      underline: dictionary.getOptionalExpressionField("underline"),
      variableTriggers: dictionary.getOptionalArray("variable_triggers", templateToType: templateToType),
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
    animators: Field<[DivAnimatorTemplate]>? = nil,
    autoEllipsize: Field<Expression<Bool>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    captureFocusOnAction: Field<Expression<Bool>>? = nil,
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
    fontVariationSettings: Field<Expression<[String: Any]>>? = nil,
    fontWeight: Field<Expression<DivFontWeight>>? = nil,
    fontWeightValue: Field<Expression<Int>>? = nil,
    functions: Field<[DivFunctionTemplate]>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    hoverEndActions: Field<[DivActionTemplate]>? = nil,
    hoverStartActions: Field<[DivActionTemplate]>? = nil,
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
    pressEndActions: Field<[DivActionTemplate]>? = nil,
    pressStartActions: Field<[DivActionTemplate]>? = nil,
    ranges: Field<[RangeTemplate]>? = nil,
    reuseId: Field<Expression<String>>? = nil,
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
    tightenWidth: Field<Expression<Bool>>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    truncate: Field<Expression<Truncate>>? = nil,
    underline: Field<Expression<DivLineStyle>>? = nil,
    variableTriggers: Field<[DivTriggerTemplate]>? = nil,
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
    self.animators = animators
    self.autoEllipsize = autoEllipsize
    self.background = background
    self.border = border
    self.captureFocusOnAction = captureFocusOnAction
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
    self.fontVariationSettings = fontVariationSettings
    self.fontWeight = fontWeight
    self.fontWeightValue = fontWeightValue
    self.functions = functions
    self.height = height
    self.hoverEndActions = hoverEndActions
    self.hoverStartActions = hoverStartActions
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
    self.pressEndActions = pressEndActions
    self.pressStartActions = pressStartActions
    self.ranges = ranges
    self.reuseId = reuseId
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
    self.tightenWidth = tightenWidth
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.truncate = truncate
    self.underline = underline
    self.variableTriggers = variableTriggers
    self.variables = variables
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTextTemplate?) -> DeserializationResult<DivText> {
    let accessibilityValue = { parent?.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionValue = { parent?.action?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionAnimationValue = { parent?.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let actionsValue = { parent?.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let alignmentHorizontalValue = { parent?.alignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue }()
    let alignmentVerticalValue = { parent?.alignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
    let alphaValue = { parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue }()
    let animatorsValue = { parent?.animators?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let autoEllipsizeValue = { parent?.autoEllipsize?.resolveOptionalValue(context: context) ?? .noValue }()
    let backgroundValue = { parent?.background?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let borderValue = { parent?.border?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let captureFocusOnActionValue = { parent?.captureFocusOnAction?.resolveOptionalValue(context: context) ?? .noValue }()
    let columnSpanValue = { parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue }()
    let disappearActionsValue = { parent?.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let doubletapActionsValue = { parent?.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let ellipsisValue = { parent?.ellipsis?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let extensionsValue = { parent?.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let focusValue = { parent?.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let focusedTextColorValue = { parent?.focusedTextColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let fontFamilyValue = { parent?.fontFamily?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontFeatureSettingsValue = { parent?.fontFeatureSettings?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontSizeValue = { parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue }()
    let fontSizeUnitValue = { parent?.fontSizeUnit?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontVariationSettingsValue = { parent?.fontVariationSettings?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontWeightValue = { parent?.fontWeight?.resolveOptionalValue(context: context) ?? .noValue }()
    let fontWeightValueValue = { parent?.fontWeightValue?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValueValidator) ?? .noValue }()
    let functionsValue = { parent?.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let heightValue = { parent?.height?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let hoverEndActionsValue = { parent?.hoverEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let hoverStartActionsValue = { parent?.hoverStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let idValue = { parent?.id?.resolveOptionalValue(context: context) ?? .noValue }()
    let imagesValue = { parent?.images?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let layoutProviderValue = { parent?.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let letterSpacingValue = { parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue }()
    let lineHeightValue = { parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue }()
    let longtapActionsValue = { parent?.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let marginsValue = { parent?.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let maxLinesValue = { parent?.maxLines?.resolveOptionalValue(context: context, validator: ResolvedValue.maxLinesValidator) ?? .noValue }()
    let minHiddenLinesValue = { parent?.minHiddenLines?.resolveOptionalValue(context: context, validator: ResolvedValue.minHiddenLinesValidator) ?? .noValue }()
    let paddingsValue = { parent?.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let pressEndActionsValue = { parent?.pressEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let pressStartActionsValue = { parent?.pressStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let rangesValue = { parent?.ranges?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let reuseIdValue = { parent?.reuseId?.resolveOptionalValue(context: context) ?? .noValue }()
    let rowSpanValue = { parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue }()
    let selectableValue = { parent?.selectable?.resolveOptionalValue(context: context) ?? .noValue }()
    let selectedActionsValue = { parent?.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let strikeValue = { parent?.strike?.resolveOptionalValue(context: context) ?? .noValue }()
    let textValue = { parent?.text?.resolveValue(context: context) ?? .noValue }()
    let textAlignmentHorizontalValue = { parent?.textAlignmentHorizontal?.resolveOptionalValue(context: context) ?? .noValue }()
    let textAlignmentVerticalValue = { parent?.textAlignmentVertical?.resolveOptionalValue(context: context) ?? .noValue }()
    let textColorValue = { parent?.textColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:)) ?? .noValue }()
    let textGradientValue = { parent?.textGradient?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let textShadowValue = { parent?.textShadow?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let tightenWidthValue = { parent?.tightenWidth?.resolveOptionalValue(context: context) ?? .noValue }()
    let tooltipsValue = { parent?.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transformValue = { parent?.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionChangeValue = { parent?.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionInValue = { parent?.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionOutValue = { parent?.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let transitionTriggersValue = { parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue }()
    let truncateValue = { parent?.truncate?.resolveOptionalValue(context: context) ?? .noValue }()
    let underlineValue = { parent?.underline?.resolveOptionalValue(context: context) ?? .noValue }()
    let variableTriggersValue = { parent?.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let variablesValue = { parent?.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let visibilityValue = { parent?.visibility?.resolveOptionalValue(context: context) ?? .noValue }()
    let visibilityActionValue = { parent?.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let visibilityActionsValue = { parent?.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let widthValue = { parent?.width?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      animatorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "animators", error: $0) },
      autoEllipsizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "auto_ellipsize", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      captureFocusOnActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "capture_focus_on_action", error: $0) },
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
      fontVariationSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_variation_settings", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      hoverEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_end_actions", error: $0) },
      hoverStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_start_actions", error: $0) },
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
      pressEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_end_actions", error: $0) },
      pressStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_start_actions", error: $0) },
      rangesValue.errorsOrWarnings?.map { .nestedObjectError(field: "ranges", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
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
      tightenWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "tighten_width", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      truncateValue.errorsOrWarnings?.map { .nestedObjectError(field: "truncate", error: $0) },
      underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
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
      accessibility: { accessibilityValue.value }(),
      action: { actionValue.value }(),
      actionAnimation: { actionAnimationValue.value }(),
      actions: { actionsValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      autoEllipsize: { autoEllipsizeValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      captureFocusOnAction: { captureFocusOnActionValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      doubletapActions: { doubletapActionsValue.value }(),
      ellipsis: { ellipsisValue.value }(),
      extensions: { extensionsValue.value }(),
      focus: { focusValue.value }(),
      focusedTextColor: { focusedTextColorValue.value }(),
      fontFamily: { fontFamilyValue.value }(),
      fontFeatureSettings: { fontFeatureSettingsValue.value }(),
      fontSize: { fontSizeValue.value }(),
      fontSizeUnit: { fontSizeUnitValue.value }(),
      fontVariationSettings: { fontVariationSettingsValue.value }(),
      fontWeight: { fontWeightValue.value }(),
      fontWeightValue: { fontWeightValueValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      hoverEndActions: { hoverEndActionsValue.value }(),
      hoverStartActions: { hoverStartActionsValue.value }(),
      id: { idValue.value }(),
      images: { imagesValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      letterSpacing: { letterSpacingValue.value }(),
      lineHeight: { lineHeightValue.value }(),
      longtapActions: { longtapActionsValue.value }(),
      margins: { marginsValue.value }(),
      maxLines: { maxLinesValue.value }(),
      minHiddenLines: { minHiddenLinesValue.value }(),
      paddings: { paddingsValue.value }(),
      pressEndActions: { pressEndActionsValue.value }(),
      pressStartActions: { pressStartActionsValue.value }(),
      ranges: { rangesValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      selectable: { selectableValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      strike: { strikeValue.value }(),
      text: { textNonNil }(),
      textAlignmentHorizontal: { textAlignmentHorizontalValue.value }(),
      textAlignmentVertical: { textAlignmentVerticalValue.value }(),
      textColor: { textColorValue.value }(),
      textGradient: { textGradientValue.value }(),
      textShadow: { textShadowValue.value }(),
      tightenWidth: { tightenWidthValue.value }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
      transitionChange: { transitionChangeValue.value }(),
      transitionIn: { transitionInValue.value }(),
      transitionOut: { transitionOutValue.value }(),
      transitionTriggers: { transitionTriggersValue.value }(),
      truncate: { truncateValue.value }(),
      underline: { underlineValue.value }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }(),
      visibility: { visibilityValue.value }(),
      visibilityAction: { visibilityActionValue.value }(),
      visibilityActions: { visibilityActionsValue.value }(),
      width: { widthValue.value }()
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
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = { parent?.alignmentHorizontal?.value() ?? .noValue }()
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = { parent?.alignmentVertical?.value() ?? .noValue }()
    var alphaValue: DeserializationResult<Expression<Double>> = { parent?.alpha?.value() ?? .noValue }()
    var animatorsValue: DeserializationResult<[DivAnimator]> = .noValue
    var autoEllipsizeValue: DeserializationResult<Expression<Bool>> = { parent?.autoEllipsize?.value() ?? .noValue }()
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var captureFocusOnActionValue: DeserializationResult<Expression<Bool>> = { parent?.captureFocusOnAction?.value() ?? .noValue }()
    var columnSpanValue: DeserializationResult<Expression<Int>> = { parent?.columnSpan?.value() ?? .noValue }()
    var disappearActionsValue: DeserializationResult<[DivDisappearAction]> = .noValue
    var doubletapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var ellipsisValue: DeserializationResult<DivText.Ellipsis> = .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var focusedTextColorValue: DeserializationResult<Expression<Color>> = { parent?.focusedTextColor?.value() ?? .noValue }()
    var fontFamilyValue: DeserializationResult<Expression<String>> = { parent?.fontFamily?.value() ?? .noValue }()
    var fontFeatureSettingsValue: DeserializationResult<Expression<String>> = { parent?.fontFeatureSettings?.value() ?? .noValue }()
    var fontSizeValue: DeserializationResult<Expression<Int>> = { parent?.fontSize?.value() ?? .noValue }()
    var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = { parent?.fontSizeUnit?.value() ?? .noValue }()
    var fontVariationSettingsValue: DeserializationResult<Expression<[String: Any]>> = { parent?.fontVariationSettings?.value() ?? .noValue }()
    var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = { parent?.fontWeight?.value() ?? .noValue }()
    var fontWeightValueValue: DeserializationResult<Expression<Int>> = { parent?.fontWeightValue?.value() ?? .noValue }()
    var functionsValue: DeserializationResult<[DivFunction]> = .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var hoverEndActionsValue: DeserializationResult<[DivAction]> = .noValue
    var hoverStartActionsValue: DeserializationResult<[DivAction]> = .noValue
    var idValue: DeserializationResult<String> = { parent?.id?.value() ?? .noValue }()
    var imagesValue: DeserializationResult<[DivText.Image]> = .noValue
    var layoutProviderValue: DeserializationResult<DivLayoutProvider> = .noValue
    var letterSpacingValue: DeserializationResult<Expression<Double>> = { parent?.letterSpacing?.value() ?? .noValue }()
    var lineHeightValue: DeserializationResult<Expression<Int>> = { parent?.lineHeight?.value() ?? .noValue }()
    var longtapActionsValue: DeserializationResult<[DivAction]> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var maxLinesValue: DeserializationResult<Expression<Int>> = { parent?.maxLines?.value() ?? .noValue }()
    var minHiddenLinesValue: DeserializationResult<Expression<Int>> = { parent?.minHiddenLines?.value() ?? .noValue }()
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var pressEndActionsValue: DeserializationResult<[DivAction]> = .noValue
    var pressStartActionsValue: DeserializationResult<[DivAction]> = .noValue
    var rangesValue: DeserializationResult<[DivText.Range]> = .noValue
    var reuseIdValue: DeserializationResult<Expression<String>> = { parent?.reuseId?.value() ?? .noValue }()
    var rowSpanValue: DeserializationResult<Expression<Int>> = { parent?.rowSpan?.value() ?? .noValue }()
    var selectableValue: DeserializationResult<Expression<Bool>> = { parent?.selectable?.value() ?? .noValue }()
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var strikeValue: DeserializationResult<Expression<DivLineStyle>> = { parent?.strike?.value() ?? .noValue }()
    var textValue: DeserializationResult<Expression<String>> = { parent?.text?.value() ?? .noValue }()
    var textAlignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = { parent?.textAlignmentHorizontal?.value() ?? .noValue }()
    var textAlignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = { parent?.textAlignmentVertical?.value() ?? .noValue }()
    var textColorValue: DeserializationResult<Expression<Color>> = { parent?.textColor?.value() ?? .noValue }()
    var textGradientValue: DeserializationResult<DivTextGradient> = .noValue
    var textShadowValue: DeserializationResult<DivShadow> = .noValue
    var tightenWidthValue: DeserializationResult<Expression<Bool>> = { parent?.tightenWidth?.value() ?? .noValue }()
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = { parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue }()
    var truncateValue: DeserializationResult<Expression<DivText.Truncate>> = { parent?.truncate?.value() ?? .noValue }()
    var underlineValue: DeserializationResult<Expression<DivLineStyle>> = { parent?.underline?.value() ?? .noValue }()
    var variableTriggersValue: DeserializationResult<[DivTrigger]> = .noValue
    var variablesValue: DeserializationResult<[DivVariable]> = .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = { parent?.visibility?.value() ?? .noValue }()
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "accessibility" {
           accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
          }
        }()
        _ = {
          if key == "action" {
           actionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionValue)
          }
        }()
        _ = {
          if key == "action_animation" {
           actionAnimationValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self).merged(with: actionAnimationValue)
          }
        }()
        _ = {
          if key == "actions" {
           actionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: actionsValue)
          }
        }()
        _ = {
          if key == "alignment_horizontal" {
           alignmentHorizontalValue = deserialize(__dictValue).merged(with: alignmentHorizontalValue)
          }
        }()
        _ = {
          if key == "alignment_vertical" {
           alignmentVerticalValue = deserialize(__dictValue).merged(with: alignmentVerticalValue)
          }
        }()
        _ = {
          if key == "alpha" {
           alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
          }
        }()
        _ = {
          if key == "animators" {
           animatorsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimatorTemplate.self).merged(with: animatorsValue)
          }
        }()
        _ = {
          if key == "auto_ellipsize" {
           autoEllipsizeValue = deserialize(__dictValue).merged(with: autoEllipsizeValue)
          }
        }()
        _ = {
          if key == "background" {
           backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
          }
        }()
        _ = {
          if key == "border" {
           borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self).merged(with: borderValue)
          }
        }()
        _ = {
          if key == "capture_focus_on_action" {
           captureFocusOnActionValue = deserialize(__dictValue).merged(with: captureFocusOnActionValue)
          }
        }()
        _ = {
          if key == "column_span" {
           columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
          }
        }()
        _ = {
          if key == "disappear_actions" {
           disappearActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self).merged(with: disappearActionsValue)
          }
        }()
        _ = {
          if key == "doubletap_actions" {
           doubletapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: doubletapActionsValue)
          }
        }()
        _ = {
          if key == "ellipsis" {
           ellipsisValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.EllipsisTemplate.self).merged(with: ellipsisValue)
          }
        }()
        _ = {
          if key == "extensions" {
           extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self).merged(with: extensionsValue)
          }
        }()
        _ = {
          if key == "focus" {
           focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self).merged(with: focusValue)
          }
        }()
        _ = {
          if key == "focused_text_color" {
           focusedTextColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: focusedTextColorValue)
          }
        }()
        _ = {
          if key == "font_family" {
           fontFamilyValue = deserialize(__dictValue).merged(with: fontFamilyValue)
          }
        }()
        _ = {
          if key == "font_feature_settings" {
           fontFeatureSettingsValue = deserialize(__dictValue).merged(with: fontFeatureSettingsValue)
          }
        }()
        _ = {
          if key == "font_size" {
           fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
          }
        }()
        _ = {
          if key == "font_size_unit" {
           fontSizeUnitValue = deserialize(__dictValue).merged(with: fontSizeUnitValue)
          }
        }()
        _ = {
          if key == "font_variation_settings" {
           fontVariationSettingsValue = deserialize(__dictValue).merged(with: fontVariationSettingsValue)
          }
        }()
        _ = {
          if key == "font_weight" {
           fontWeightValue = deserialize(__dictValue).merged(with: fontWeightValue)
          }
        }()
        _ = {
          if key == "font_weight_value" {
           fontWeightValueValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator).merged(with: fontWeightValueValue)
          }
        }()
        _ = {
          if key == "functions" {
           functionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionTemplate.self).merged(with: functionsValue)
          }
        }()
        _ = {
          if key == "height" {
           heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: heightValue)
          }
        }()
        _ = {
          if key == "hover_end_actions" {
           hoverEndActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: hoverEndActionsValue)
          }
        }()
        _ = {
          if key == "hover_start_actions" {
           hoverStartActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: hoverStartActionsValue)
          }
        }()
        _ = {
          if key == "id" {
           idValue = deserialize(__dictValue).merged(with: idValue)
          }
        }()
        _ = {
          if key == "images" {
           imagesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.self).merged(with: imagesValue)
          }
        }()
        _ = {
          if key == "layout_provider" {
           layoutProviderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self).merged(with: layoutProviderValue)
          }
        }()
        _ = {
          if key == "letter_spacing" {
           letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
          }
        }()
        _ = {
          if key == "line_height" {
           lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
          }
        }()
        _ = {
          if key == "longtap_actions" {
           longtapActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: longtapActionsValue)
          }
        }()
        _ = {
          if key == "margins" {
           marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
          }
        }()
        _ = {
          if key == "max_lines" {
           maxLinesValue = deserialize(__dictValue, validator: ResolvedValue.maxLinesValidator).merged(with: maxLinesValue)
          }
        }()
        _ = {
          if key == "min_hidden_lines" {
           minHiddenLinesValue = deserialize(__dictValue, validator: ResolvedValue.minHiddenLinesValidator).merged(with: minHiddenLinesValue)
          }
        }()
        _ = {
          if key == "paddings" {
           paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
          }
        }()
        _ = {
          if key == "press_end_actions" {
           pressEndActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: pressEndActionsValue)
          }
        }()
        _ = {
          if key == "press_start_actions" {
           pressStartActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: pressStartActionsValue)
          }
        }()
        _ = {
          if key == "ranges" {
           rangesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.RangeTemplate.self).merged(with: rangesValue)
          }
        }()
        _ = {
          if key == "reuse_id" {
           reuseIdValue = deserialize(__dictValue).merged(with: reuseIdValue)
          }
        }()
        _ = {
          if key == "row_span" {
           rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
          }
        }()
        _ = {
          if key == "selectable" {
           selectableValue = deserialize(__dictValue).merged(with: selectableValue)
          }
        }()
        _ = {
          if key == "selected_actions" {
           selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: selectedActionsValue)
          }
        }()
        _ = {
          if key == "strike" {
           strikeValue = deserialize(__dictValue).merged(with: strikeValue)
          }
        }()
        _ = {
          if key == "text" {
           textValue = deserialize(__dictValue).merged(with: textValue)
          }
        }()
        _ = {
          if key == "text_alignment_horizontal" {
           textAlignmentHorizontalValue = deserialize(__dictValue).merged(with: textAlignmentHorizontalValue)
          }
        }()
        _ = {
          if key == "text_alignment_vertical" {
           textAlignmentVerticalValue = deserialize(__dictValue).merged(with: textAlignmentVerticalValue)
          }
        }()
        _ = {
          if key == "text_color" {
           textColorValue = deserialize(__dictValue, transform: Color.color(withHexString:)).merged(with: textColorValue)
          }
        }()
        _ = {
          if key == "text_gradient" {
           textGradientValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextGradientTemplate.self).merged(with: textGradientValue)
          }
        }()
        _ = {
          if key == "text_shadow" {
           textShadowValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self).merged(with: textShadowValue)
          }
        }()
        _ = {
          if key == "tighten_width" {
           tightenWidthValue = deserialize(__dictValue).merged(with: tightenWidthValue)
          }
        }()
        _ = {
          if key == "tooltips" {
           tooltipsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self).merged(with: tooltipsValue)
          }
        }()
        _ = {
          if key == "transform" {
           transformValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self).merged(with: transformValue)
          }
        }()
        _ = {
          if key == "transition_change" {
           transitionChangeValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self).merged(with: transitionChangeValue)
          }
        }()
        _ = {
          if key == "transition_in" {
           transitionInValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionInValue)
          }
        }()
        _ = {
          if key == "transition_out" {
           transitionOutValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self).merged(with: transitionOutValue)
          }
        }()
        _ = {
          if key == "transition_triggers" {
           transitionTriggersValue = deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator).merged(with: transitionTriggersValue)
          }
        }()
        _ = {
          if key == "truncate" {
           truncateValue = deserialize(__dictValue).merged(with: truncateValue)
          }
        }()
        _ = {
          if key == "underline" {
           underlineValue = deserialize(__dictValue).merged(with: underlineValue)
          }
        }()
        _ = {
          if key == "variable_triggers" {
           variableTriggersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self).merged(with: variableTriggersValue)
          }
        }()
        _ = {
          if key == "variables" {
           variablesValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self).merged(with: variablesValue)
          }
        }()
        _ = {
          if key == "visibility" {
           visibilityValue = deserialize(__dictValue).merged(with: visibilityValue)
          }
        }()
        _ = {
          if key == "visibility_action" {
           visibilityActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionValue)
          }
        }()
        _ = {
          if key == "visibility_actions" {
           visibilityActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self).merged(with: visibilityActionsValue)
          }
        }()
        _ = {
          if key == "width" {
           widthValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self).merged(with: widthValue)
          }
        }()
        _ = {
         if key == parent?.accessibility?.link {
           accessibilityValue = accessibilityValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAccessibilityTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.action?.link {
           actionValue = actionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.actionAnimation?.link {
           actionAnimationValue = actionAnimationValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimationTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.actions?.link {
           actionsValue = actionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.alignmentHorizontal?.link {
           alignmentHorizontalValue = alignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.alignmentVertical?.link {
           alignmentVerticalValue = alignmentVerticalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.alpha?.link {
           alphaValue = alphaValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.alphaValidator) })
          }
        }()
        _ = {
         if key == parent?.animators?.link {
           animatorsValue = animatorsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAnimatorTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.autoEllipsize?.link {
           autoEllipsizeValue = autoEllipsizeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.background?.link {
           backgroundValue = backgroundValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBackgroundTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.border?.link {
           borderValue = borderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivBorderTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.captureFocusOnAction?.link {
           captureFocusOnActionValue = captureFocusOnActionValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.columnSpan?.link {
           columnSpanValue = columnSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator) })
          }
        }()
        _ = {
         if key == parent?.disappearActions?.link {
           disappearActionsValue = disappearActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivDisappearActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.doubletapActions?.link {
           doubletapActionsValue = doubletapActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.ellipsis?.link {
           ellipsisValue = ellipsisValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.EllipsisTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.extensions?.link {
           extensionsValue = extensionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivExtensionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.focus?.link {
           focusValue = focusValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFocusTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.focusedTextColor?.link {
           focusedTextColorValue = focusedTextColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.fontFamily?.link {
           fontFamilyValue = fontFamilyValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.fontFeatureSettings?.link {
           fontFeatureSettingsValue = fontFeatureSettingsValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.fontSize?.link {
           fontSizeValue = fontSizeValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator) })
          }
        }()
        _ = {
         if key == parent?.fontSizeUnit?.link {
           fontSizeUnitValue = fontSizeUnitValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.fontVariationSettings?.link {
           fontVariationSettingsValue = fontVariationSettingsValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.fontWeight?.link {
           fontWeightValue = fontWeightValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.fontWeightValue?.link {
           fontWeightValueValue = fontWeightValueValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.fontWeightValueValidator) })
          }
        }()
        _ = {
         if key == parent?.functions?.link {
           functionsValue = functionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivFunctionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.height?.link {
           heightValue = heightValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.hoverEndActions?.link {
           hoverEndActionsValue = hoverEndActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.hoverStartActions?.link {
           hoverStartActionsValue = hoverStartActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.id?.link {
           idValue = idValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.images?.link {
           imagesValue = imagesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.ImageTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.layoutProvider?.link {
           layoutProviderValue = layoutProviderValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivLayoutProviderTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.letterSpacing?.link {
           letterSpacingValue = letterSpacingValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.lineHeight?.link {
           lineHeightValue = lineHeightValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator) })
          }
        }()
        _ = {
         if key == parent?.longtapActions?.link {
           longtapActionsValue = longtapActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.margins?.link {
           marginsValue = marginsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.maxLines?.link {
           maxLinesValue = maxLinesValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.maxLinesValidator) })
          }
        }()
        _ = {
         if key == parent?.minHiddenLines?.link {
           minHiddenLinesValue = minHiddenLinesValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.minHiddenLinesValidator) })
          }
        }()
        _ = {
         if key == parent?.paddings?.link {
           paddingsValue = paddingsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivEdgeInsetsTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.pressEndActions?.link {
           pressEndActionsValue = pressEndActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.pressStartActions?.link {
           pressStartActionsValue = pressStartActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.ranges?.link {
           rangesValue = rangesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextTemplate.RangeTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.reuseId?.link {
           reuseIdValue = reuseIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.rowSpan?.link {
           rowSpanValue = rowSpanValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator) })
          }
        }()
        _ = {
         if key == parent?.selectable?.link {
           selectableValue = selectableValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.selectedActions?.link {
           selectedActionsValue = selectedActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.strike?.link {
           strikeValue = strikeValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.text?.link {
           textValue = textValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.textAlignmentHorizontal?.link {
           textAlignmentHorizontalValue = textAlignmentHorizontalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.textAlignmentVertical?.link {
           textAlignmentVerticalValue = textAlignmentVerticalValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.textColor?.link {
           textColorValue = textColorValue.merged(with: { deserialize(__dictValue, transform: Color.color(withHexString:)) })
          }
        }()
        _ = {
         if key == parent?.textGradient?.link {
           textGradientValue = textGradientValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTextGradientTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.textShadow?.link {
           textShadowValue = textShadowValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivShadowTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.tightenWidth?.link {
           tightenWidthValue = tightenWidthValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.tooltips?.link {
           tooltipsValue = tooltipsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTooltipTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transform?.link {
           transformValue = transformValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTransformTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionChange?.link {
           transitionChangeValue = transitionChangeValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivChangeTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionIn?.link {
           transitionInValue = transitionInValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionOut?.link {
           transitionOutValue = transitionOutValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivAppearanceTransitionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.transitionTriggers?.link {
           transitionTriggersValue = transitionTriggersValue.merged(with: { deserialize(__dictValue, validator: ResolvedValue.transitionTriggersValidator) })
          }
        }()
        _ = {
         if key == parent?.truncate?.link {
           truncateValue = truncateValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.underline?.link {
           underlineValue = underlineValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.variableTriggers?.link {
           variableTriggersValue = variableTriggersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTriggerTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.variables?.link {
           variablesValue = variablesValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVariableTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.visibility?.link {
           visibilityValue = visibilityValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.visibilityAction?.link {
           visibilityActionValue = visibilityActionValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.visibilityActions?.link {
           visibilityActionsValue = visibilityActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivVisibilityActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.width?.link {
           widthValue = widthValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivSizeTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { accessibilityValue = accessibilityValue.merged(with: { parent.accessibility?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { actionValue = actionValue.merged(with: { parent.action?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { actionAnimationValue = actionAnimationValue.merged(with: { parent.actionAnimation?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { actionsValue = actionsValue.merged(with: { parent.actions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { animatorsValue = animatorsValue.merged(with: { parent.animators?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { backgroundValue = backgroundValue.merged(with: { parent.background?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { borderValue = borderValue.merged(with: { parent.border?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { disappearActionsValue = disappearActionsValue.merged(with: { parent.disappearActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { doubletapActionsValue = doubletapActionsValue.merged(with: { parent.doubletapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { ellipsisValue = ellipsisValue.merged(with: { parent.ellipsis?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { extensionsValue = extensionsValue.merged(with: { parent.extensions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { focusValue = focusValue.merged(with: { parent.focus?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { functionsValue = functionsValue.merged(with: { parent.functions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { heightValue = heightValue.merged(with: { parent.height?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { hoverEndActionsValue = hoverEndActionsValue.merged(with: { parent.hoverEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { hoverStartActionsValue = hoverStartActionsValue.merged(with: { parent.hoverStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { imagesValue = imagesValue.merged(with: { parent.images?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { layoutProviderValue = layoutProviderValue.merged(with: { parent.layoutProvider?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { longtapActionsValue = longtapActionsValue.merged(with: { parent.longtapActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { marginsValue = marginsValue.merged(with: { parent.margins?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { paddingsValue = paddingsValue.merged(with: { parent.paddings?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { pressEndActionsValue = pressEndActionsValue.merged(with: { parent.pressEndActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { pressStartActionsValue = pressStartActionsValue.merged(with: { parent.pressStartActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { rangesValue = rangesValue.merged(with: { parent.ranges?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { selectedActionsValue = selectedActionsValue.merged(with: { parent.selectedActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { textGradientValue = textGradientValue.merged(with: { parent.textGradient?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { textShadowValue = textShadowValue.merged(with: { parent.textShadow?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { tooltipsValue = tooltipsValue.merged(with: { parent.tooltips?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transformValue = transformValue.merged(with: { parent.transform?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionChangeValue = transitionChangeValue.merged(with: { parent.transitionChange?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionInValue = transitionInValue.merged(with: { parent.transitionIn?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { transitionOutValue = transitionOutValue.merged(with: { parent.transitionOut?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { variableTriggersValue = variableTriggersValue.merged(with: { parent.variableTriggers?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { variablesValue = variablesValue.merged(with: { parent.variables?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { visibilityActionValue = visibilityActionValue.merged(with: { parent.visibilityAction?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { visibilityActionsValue = visibilityActionsValue.merged(with: { parent.visibilityActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { widthValue = widthValue.merged(with: { parent.width?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      actionValue.errorsOrWarnings?.map { .nestedObjectError(field: "action", error: $0) },
      actionAnimationValue.errorsOrWarnings?.map { .nestedObjectError(field: "action_animation", error: $0) },
      actionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "actions", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      animatorsValue.errorsOrWarnings?.map { .nestedObjectError(field: "animators", error: $0) },
      autoEllipsizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "auto_ellipsize", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      captureFocusOnActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "capture_focus_on_action", error: $0) },
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
      fontVariationSettingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_variation_settings", error: $0) },
      fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
      fontWeightValueValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight_value", error: $0) },
      functionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "functions", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      hoverEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_end_actions", error: $0) },
      hoverStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "hover_start_actions", error: $0) },
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
      pressEndActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_end_actions", error: $0) },
      pressStartActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "press_start_actions", error: $0) },
      rangesValue.errorsOrWarnings?.map { .nestedObjectError(field: "ranges", error: $0) },
      reuseIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "reuse_id", error: $0) },
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
      tightenWidthValue.errorsOrWarnings?.map { .nestedObjectError(field: "tighten_width", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      truncateValue.errorsOrWarnings?.map { .nestedObjectError(field: "truncate", error: $0) },
      underlineValue.errorsOrWarnings?.map { .nestedObjectError(field: "underline", error: $0) },
      variableTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "variable_triggers", error: $0) },
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
      accessibility: { accessibilityValue.value }(),
      action: { actionValue.value }(),
      actionAnimation: { actionAnimationValue.value }(),
      actions: { actionsValue.value }(),
      alignmentHorizontal: { alignmentHorizontalValue.value }(),
      alignmentVertical: { alignmentVerticalValue.value }(),
      alpha: { alphaValue.value }(),
      animators: { animatorsValue.value }(),
      autoEllipsize: { autoEllipsizeValue.value }(),
      background: { backgroundValue.value }(),
      border: { borderValue.value }(),
      captureFocusOnAction: { captureFocusOnActionValue.value }(),
      columnSpan: { columnSpanValue.value }(),
      disappearActions: { disappearActionsValue.value }(),
      doubletapActions: { doubletapActionsValue.value }(),
      ellipsis: { ellipsisValue.value }(),
      extensions: { extensionsValue.value }(),
      focus: { focusValue.value }(),
      focusedTextColor: { focusedTextColorValue.value }(),
      fontFamily: { fontFamilyValue.value }(),
      fontFeatureSettings: { fontFeatureSettingsValue.value }(),
      fontSize: { fontSizeValue.value }(),
      fontSizeUnit: { fontSizeUnitValue.value }(),
      fontVariationSettings: { fontVariationSettingsValue.value }(),
      fontWeight: { fontWeightValue.value }(),
      fontWeightValue: { fontWeightValueValue.value }(),
      functions: { functionsValue.value }(),
      height: { heightValue.value }(),
      hoverEndActions: { hoverEndActionsValue.value }(),
      hoverStartActions: { hoverStartActionsValue.value }(),
      id: { idValue.value }(),
      images: { imagesValue.value }(),
      layoutProvider: { layoutProviderValue.value }(),
      letterSpacing: { letterSpacingValue.value }(),
      lineHeight: { lineHeightValue.value }(),
      longtapActions: { longtapActionsValue.value }(),
      margins: { marginsValue.value }(),
      maxLines: { maxLinesValue.value }(),
      minHiddenLines: { minHiddenLinesValue.value }(),
      paddings: { paddingsValue.value }(),
      pressEndActions: { pressEndActionsValue.value }(),
      pressStartActions: { pressStartActionsValue.value }(),
      ranges: { rangesValue.value }(),
      reuseId: { reuseIdValue.value }(),
      rowSpan: { rowSpanValue.value }(),
      selectable: { selectableValue.value }(),
      selectedActions: { selectedActionsValue.value }(),
      strike: { strikeValue.value }(),
      text: { textNonNil }(),
      textAlignmentHorizontal: { textAlignmentHorizontalValue.value }(),
      textAlignmentVertical: { textAlignmentVerticalValue.value }(),
      textColor: { textColorValue.value }(),
      textGradient: { textGradientValue.value }(),
      textShadow: { textShadowValue.value }(),
      tightenWidth: { tightenWidthValue.value }(),
      tooltips: { tooltipsValue.value }(),
      transform: { transformValue.value }(),
      transitionChange: { transitionChangeValue.value }(),
      transitionIn: { transitionInValue.value }(),
      transitionOut: { transitionOutValue.value }(),
      transitionTriggers: { transitionTriggersValue.value }(),
      truncate: { truncateValue.value }(),
      underline: { underlineValue.value }(),
      variableTriggers: { variableTriggersValue.value }(),
      variables: { variablesValue.value }(),
      visibility: { visibilityValue.value }(),
      visibilityAction: { visibilityActionValue.value }(),
      visibilityActions: { visibilityActionsValue.value }(),
      width: { widthValue.value }()
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
      animators: animators ?? mergedParent.animators,
      autoEllipsize: autoEllipsize ?? mergedParent.autoEllipsize,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      captureFocusOnAction: captureFocusOnAction ?? mergedParent.captureFocusOnAction,
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
      fontVariationSettings: fontVariationSettings ?? mergedParent.fontVariationSettings,
      fontWeight: fontWeight ?? mergedParent.fontWeight,
      fontWeightValue: fontWeightValue ?? mergedParent.fontWeightValue,
      functions: functions ?? mergedParent.functions,
      height: height ?? mergedParent.height,
      hoverEndActions: hoverEndActions ?? mergedParent.hoverEndActions,
      hoverStartActions: hoverStartActions ?? mergedParent.hoverStartActions,
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
      pressEndActions: pressEndActions ?? mergedParent.pressEndActions,
      pressStartActions: pressStartActions ?? mergedParent.pressStartActions,
      ranges: ranges ?? mergedParent.ranges,
      reuseId: reuseId ?? mergedParent.reuseId,
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
      tightenWidth: tightenWidth ?? mergedParent.tightenWidth,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      truncate: truncate ?? mergedParent.truncate,
      underline: underline ?? mergedParent.underline,
      variableTriggers: variableTriggers ?? mergedParent.variableTriggers,
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
      animators: merged.animators?.tryResolveParent(templates: templates),
      autoEllipsize: merged.autoEllipsize,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      captureFocusOnAction: merged.captureFocusOnAction,
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
      fontVariationSettings: merged.fontVariationSettings,
      fontWeight: merged.fontWeight,
      fontWeightValue: merged.fontWeightValue,
      functions: merged.functions?.tryResolveParent(templates: templates),
      height: merged.height?.tryResolveParent(templates: templates),
      hoverEndActions: merged.hoverEndActions?.tryResolveParent(templates: templates),
      hoverStartActions: merged.hoverStartActions?.tryResolveParent(templates: templates),
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
      pressEndActions: merged.pressEndActions?.tryResolveParent(templates: templates),
      pressStartActions: merged.pressStartActions?.tryResolveParent(templates: templates),
      ranges: merged.ranges?.tryResolveParent(templates: templates),
      reuseId: merged.reuseId,
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
      tightenWidth: merged.tightenWidth,
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      truncate: merged.truncate,
      underline: merged.underline,
      variableTriggers: merged.variableTriggers?.tryResolveParent(templates: templates),
      variables: merged.variables?.tryResolveParent(templates: templates),
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
