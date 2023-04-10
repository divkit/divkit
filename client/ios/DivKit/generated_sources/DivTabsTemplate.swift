// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivTabsTemplate: TemplateValue {
  public final class ItemTemplate: TemplateValue {
    public let div: Field<DivTemplate>?
    public let title: Field<Expression<String>>? // at least 1 char
    public let titleClickAction: Field<DivActionTemplate>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      do {
        self.init(
          div: try dictionary.getOptionalField("div", templateToType: templateToType),
          title: try dictionary.getOptionalExpressionField("title"),
          titleClickAction: try dictionary.getOptionalField("title_click_action", templateToType: templateToType)
        )
      } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
        throw DeserializationError.invalidFieldRepresentation(field: "item_template." + field, representation: representation)
      }
    }

    init(
      div: Field<DivTemplate>? = nil,
      title: Field<Expression<String>>? = nil,
      titleClickAction: Field<DivActionTemplate>? = nil
    ) {
      self.div = div
      self.title = title
      self.titleClickAction = titleClickAction
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ItemTemplate?) -> DeserializationResult<DivTabs.Item> {
      let divValue = parent?.div?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
      let titleValue = parent?.title?.resolveValue(context: context, validator: ResolvedValue.titleValidator) ?? .noValue
      let titleClickActionValue = parent?.titleClickAction?.resolveOptionalValue(context: context, validator: ResolvedValue.titleClickActionValidator, useOnlyLinks: true) ?? .noValue
      var errors = mergeErrors(
        divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
        titleValue.errorsOrWarnings?.map { .nestedObjectError(field: "title", error: $0) },
        titleClickActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "title_click_action", error: $0) }
      )
      if case .noValue = divValue {
        errors.append(.requiredFieldIsMissing(field: "div"))
      }
      if case .noValue = titleValue {
        errors.append(.requiredFieldIsMissing(field: "title"))
      }
      guard
        let divNonNil = divValue.value,
        let titleNonNil = titleValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivTabs.Item(
        div: divNonNil,
        title: titleNonNil,
        titleClickAction: titleClickActionValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ItemTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTabs.Item> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var divValue: DeserializationResult<Div> = .noValue
      var titleValue: DeserializationResult<Expression<String>> = parent?.title?.value() ?? .noValue
      var titleClickActionValue: DeserializationResult<DivAction> = .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "div":
          divValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self).merged(with: divValue)
        case "title":
          titleValue = deserialize(__dictValue, validator: ResolvedValue.titleValidator).merged(with: titleValue)
        case "title_click_action":
          titleClickActionValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.titleClickActionValidator, type: DivActionTemplate.self).merged(with: titleClickActionValue)
        case parent?.div?.link:
          divValue = divValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivTemplate.self))
        case parent?.title?.link:
          titleValue = titleValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.titleValidator))
        case parent?.titleClickAction?.link:
          titleClickActionValue = titleClickActionValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.titleClickActionValidator, type: DivActionTemplate.self))
        default: break
        }
      }
      if let parent = parent {
        divValue = divValue.merged(with: parent.div?.resolveValue(context: context, useOnlyLinks: true))
        titleClickActionValue = titleClickActionValue.merged(with: parent.titleClickAction?.resolveOptionalValue(context: context, validator: ResolvedValue.titleClickActionValidator, useOnlyLinks: true))
      }
      var errors = mergeErrors(
        divValue.errorsOrWarnings?.map { .nestedObjectError(field: "div", error: $0) },
        titleValue.errorsOrWarnings?.map { .nestedObjectError(field: "title", error: $0) },
        titleClickActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "title_click_action", error: $0) }
      )
      if case .noValue = divValue {
        errors.append(.requiredFieldIsMissing(field: "div"))
      }
      if case .noValue = titleValue {
        errors.append(.requiredFieldIsMissing(field: "title"))
      }
      guard
        let divNonNil = divValue.value,
        let titleNonNil = titleValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivTabs.Item(
        div: divNonNil,
        title: titleNonNil,
        titleClickAction: titleClickActionValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ItemTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ItemTemplate {
      let merged = try mergedWithParent(templates: templates)

      return ItemTemplate(
        div: try merged.div?.resolveParent(templates: templates),
        title: merged.title,
        titleClickAction: merged.titleClickAction?.tryResolveParent(templates: templates)
      )
    }
  }

  public final class TabTitleStyleTemplate: TemplateValue {
    public typealias AnimationType = DivTabs.TabTitleStyle.AnimationType

    public let activeBackgroundColor: Field<Expression<Color>>? // default value: #FFFFDC60
    public let activeFontWeight: Field<Expression<DivFontWeight>>?
    public let activeTextColor: Field<Expression<Color>>? // default value: #CC000000
    public let animationDuration: Field<Expression<Int>>? // constraint: number >= 0; default value: 300
    public let animationType: Field<Expression<AnimationType>>? // default value: slide
    public let cornerRadius: Field<Expression<Int>>? // constraint: number >= 0
    public let cornersRadius: Field<DivCornersRadiusTemplate>?
    public let fontFamily: Field<Expression<DivFontFamily>>? // default value: text
    public let fontSize: Field<Expression<Int>>? // constraint: number >= 0; default value: 12
    public let fontSizeUnit: Field<Expression<DivSizeUnit>>? // default value: sp
    public let fontWeight: Field<Expression<DivFontWeight>>? // default value: regular
    public let inactiveBackgroundColor: Field<Expression<Color>>?
    public let inactiveFontWeight: Field<Expression<DivFontWeight>>?
    public let inactiveTextColor: Field<Expression<Color>>? // default value: #80000000
    public let itemSpacing: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
    public let letterSpacing: Field<Expression<Double>>? // default value: 0
    public let lineHeight: Field<Expression<Int>>? // constraint: number >= 0
    public let paddings: Field<DivEdgeInsetsTemplate>? // default value: DivEdgeInsets(bottom: .value(6), left: .value(8), right: .value(8), top: .value(6))

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        activeBackgroundColor: try dictionary.getOptionalExpressionField("active_background_color", transform: Color.color(withHexString:)),
        activeFontWeight: try dictionary.getOptionalExpressionField("active_font_weight"),
        activeTextColor: try dictionary.getOptionalExpressionField("active_text_color", transform: Color.color(withHexString:)),
        animationDuration: try dictionary.getOptionalExpressionField("animation_duration"),
        animationType: try dictionary.getOptionalExpressionField("animation_type"),
        cornerRadius: try dictionary.getOptionalExpressionField("corner_radius"),
        cornersRadius: try dictionary.getOptionalField("corners_radius", templateToType: templateToType),
        fontFamily: try dictionary.getOptionalExpressionField("font_family"),
        fontSize: try dictionary.getOptionalExpressionField("font_size"),
        fontSizeUnit: try dictionary.getOptionalExpressionField("font_size_unit"),
        fontWeight: try dictionary.getOptionalExpressionField("font_weight"),
        inactiveBackgroundColor: try dictionary.getOptionalExpressionField("inactive_background_color", transform: Color.color(withHexString:)),
        inactiveFontWeight: try dictionary.getOptionalExpressionField("inactive_font_weight"),
        inactiveTextColor: try dictionary.getOptionalExpressionField("inactive_text_color", transform: Color.color(withHexString:)),
        itemSpacing: try dictionary.getOptionalExpressionField("item_spacing"),
        letterSpacing: try dictionary.getOptionalExpressionField("letter_spacing"),
        lineHeight: try dictionary.getOptionalExpressionField("line_height"),
        paddings: try dictionary.getOptionalField("paddings", templateToType: templateToType)
      )
    }

    init(
      activeBackgroundColor: Field<Expression<Color>>? = nil,
      activeFontWeight: Field<Expression<DivFontWeight>>? = nil,
      activeTextColor: Field<Expression<Color>>? = nil,
      animationDuration: Field<Expression<Int>>? = nil,
      animationType: Field<Expression<AnimationType>>? = nil,
      cornerRadius: Field<Expression<Int>>? = nil,
      cornersRadius: Field<DivCornersRadiusTemplate>? = nil,
      fontFamily: Field<Expression<DivFontFamily>>? = nil,
      fontSize: Field<Expression<Int>>? = nil,
      fontSizeUnit: Field<Expression<DivSizeUnit>>? = nil,
      fontWeight: Field<Expression<DivFontWeight>>? = nil,
      inactiveBackgroundColor: Field<Expression<Color>>? = nil,
      inactiveFontWeight: Field<Expression<DivFontWeight>>? = nil,
      inactiveTextColor: Field<Expression<Color>>? = nil,
      itemSpacing: Field<Expression<Int>>? = nil,
      letterSpacing: Field<Expression<Double>>? = nil,
      lineHeight: Field<Expression<Int>>? = nil,
      paddings: Field<DivEdgeInsetsTemplate>? = nil
    ) {
      self.activeBackgroundColor = activeBackgroundColor
      self.activeFontWeight = activeFontWeight
      self.activeTextColor = activeTextColor
      self.animationDuration = animationDuration
      self.animationType = animationType
      self.cornerRadius = cornerRadius
      self.cornersRadius = cornersRadius
      self.fontFamily = fontFamily
      self.fontSize = fontSize
      self.fontSizeUnit = fontSizeUnit
      self.fontWeight = fontWeight
      self.inactiveBackgroundColor = inactiveBackgroundColor
      self.inactiveFontWeight = inactiveFontWeight
      self.inactiveTextColor = inactiveTextColor
      self.itemSpacing = itemSpacing
      self.letterSpacing = letterSpacing
      self.lineHeight = lineHeight
      self.paddings = paddings
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: TabTitleStyleTemplate?) -> DeserializationResult<DivTabs.TabTitleStyle> {
      let activeBackgroundColorValue = parent?.activeBackgroundColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.activeBackgroundColorValidator) ?? .noValue
      let activeFontWeightValue = parent?.activeFontWeight?.resolveOptionalValue(context: context, validator: ResolvedValue.activeFontWeightValidator) ?? .noValue
      let activeTextColorValue = parent?.activeTextColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.activeTextColorValidator) ?? .noValue
      let animationDurationValue = parent?.animationDuration?.resolveOptionalValue(context: context, validator: ResolvedValue.animationDurationValidator) ?? .noValue
      let animationTypeValue = parent?.animationType?.resolveOptionalValue(context: context, validator: ResolvedValue.animationTypeValidator) ?? .noValue
      let cornerRadiusValue = parent?.cornerRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornerRadiusValidator) ?? .noValue
      let cornersRadiusValue = parent?.cornersRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornersRadiusValidator, useOnlyLinks: true) ?? .noValue
      let fontFamilyValue = parent?.fontFamily?.resolveOptionalValue(context: context, validator: ResolvedValue.fontFamilyValidator) ?? .noValue
      let fontSizeValue = parent?.fontSize?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeValidator) ?? .noValue
      let fontSizeUnitValue = parent?.fontSizeUnit?.resolveOptionalValue(context: context, validator: ResolvedValue.fontSizeUnitValidator) ?? .noValue
      let fontWeightValue = parent?.fontWeight?.resolveOptionalValue(context: context, validator: ResolvedValue.fontWeightValidator) ?? .noValue
      let inactiveBackgroundColorValue = parent?.inactiveBackgroundColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.inactiveBackgroundColorValidator) ?? .noValue
      let inactiveFontWeightValue = parent?.inactiveFontWeight?.resolveOptionalValue(context: context, validator: ResolvedValue.inactiveFontWeightValidator) ?? .noValue
      let inactiveTextColorValue = parent?.inactiveTextColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.inactiveTextColorValidator) ?? .noValue
      let itemSpacingValue = parent?.itemSpacing?.resolveOptionalValue(context: context, validator: ResolvedValue.itemSpacingValidator) ?? .noValue
      let letterSpacingValue = parent?.letterSpacing?.resolveOptionalValue(context: context) ?? .noValue
      let lineHeightValue = parent?.lineHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.lineHeightValidator) ?? .noValue
      let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true) ?? .noValue
      let errors = mergeErrors(
        activeBackgroundColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_background_color", error: $0) },
        activeFontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_font_weight", error: $0) },
        activeTextColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_text_color", error: $0) },
        animationDurationValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_duration", error: $0) },
        animationTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_type", error: $0) },
        cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
        cornersRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corners_radius", error: $0) },
        fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        inactiveBackgroundColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_background_color", error: $0) },
        inactiveFontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_font_weight", error: $0) },
        inactiveTextColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_text_color", error: $0) },
        itemSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_spacing", error: $0) },
        letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
        lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
        paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) }
      )
      let result = DivTabs.TabTitleStyle(
        activeBackgroundColor: activeBackgroundColorValue.value,
        activeFontWeight: activeFontWeightValue.value,
        activeTextColor: activeTextColorValue.value,
        animationDuration: animationDurationValue.value,
        animationType: animationTypeValue.value,
        cornerRadius: cornerRadiusValue.value,
        cornersRadius: cornersRadiusValue.value,
        fontFamily: fontFamilyValue.value,
        fontSize: fontSizeValue.value,
        fontSizeUnit: fontSizeUnitValue.value,
        fontWeight: fontWeightValue.value,
        inactiveBackgroundColor: inactiveBackgroundColorValue.value,
        inactiveFontWeight: inactiveFontWeightValue.value,
        inactiveTextColor: inactiveTextColorValue.value,
        itemSpacing: itemSpacingValue.value,
        letterSpacing: letterSpacingValue.value,
        lineHeight: lineHeightValue.value,
        paddings: paddingsValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: TabTitleStyleTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTabs.TabTitleStyle> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var activeBackgroundColorValue: DeserializationResult<Expression<Color>> = parent?.activeBackgroundColor?.value() ?? .noValue
      var activeFontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.activeFontWeight?.value() ?? .noValue
      var activeTextColorValue: DeserializationResult<Expression<Color>> = parent?.activeTextColor?.value() ?? .noValue
      var animationDurationValue: DeserializationResult<Expression<Int>> = parent?.animationDuration?.value() ?? .noValue
      var animationTypeValue: DeserializationResult<Expression<DivTabs.TabTitleStyle.AnimationType>> = parent?.animationType?.value() ?? .noValue
      var cornerRadiusValue: DeserializationResult<Expression<Int>> = parent?.cornerRadius?.value() ?? .noValue
      var cornersRadiusValue: DeserializationResult<DivCornersRadius> = .noValue
      var fontFamilyValue: DeserializationResult<Expression<DivFontFamily>> = parent?.fontFamily?.value() ?? .noValue
      var fontSizeValue: DeserializationResult<Expression<Int>> = parent?.fontSize?.value() ?? .noValue
      var fontSizeUnitValue: DeserializationResult<Expression<DivSizeUnit>> = parent?.fontSizeUnit?.value() ?? .noValue
      var fontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.fontWeight?.value() ?? .noValue
      var inactiveBackgroundColorValue: DeserializationResult<Expression<Color>> = parent?.inactiveBackgroundColor?.value() ?? .noValue
      var inactiveFontWeightValue: DeserializationResult<Expression<DivFontWeight>> = parent?.inactiveFontWeight?.value() ?? .noValue
      var inactiveTextColorValue: DeserializationResult<Expression<Color>> = parent?.inactiveTextColor?.value() ?? .noValue
      var itemSpacingValue: DeserializationResult<Expression<Int>> = parent?.itemSpacing?.value() ?? .noValue
      var letterSpacingValue: DeserializationResult<Expression<Double>> = parent?.letterSpacing?.value() ?? .noValue
      var lineHeightValue: DeserializationResult<Expression<Int>> = parent?.lineHeight?.value() ?? .noValue
      var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "active_background_color":
          activeBackgroundColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.activeBackgroundColorValidator).merged(with: activeBackgroundColorValue)
        case "active_font_weight":
          activeFontWeightValue = deserialize(__dictValue, validator: ResolvedValue.activeFontWeightValidator).merged(with: activeFontWeightValue)
        case "active_text_color":
          activeTextColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.activeTextColorValidator).merged(with: activeTextColorValue)
        case "animation_duration":
          animationDurationValue = deserialize(__dictValue, validator: ResolvedValue.animationDurationValidator).merged(with: animationDurationValue)
        case "animation_type":
          animationTypeValue = deserialize(__dictValue, validator: ResolvedValue.animationTypeValidator).merged(with: animationTypeValue)
        case "corner_radius":
          cornerRadiusValue = deserialize(__dictValue, validator: ResolvedValue.cornerRadiusValidator).merged(with: cornerRadiusValue)
        case "corners_radius":
          cornersRadiusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.cornersRadiusValidator, type: DivCornersRadiusTemplate.self).merged(with: cornersRadiusValue)
        case "font_family":
          fontFamilyValue = deserialize(__dictValue, validator: ResolvedValue.fontFamilyValidator).merged(with: fontFamilyValue)
        case "font_size":
          fontSizeValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator).merged(with: fontSizeValue)
        case "font_size_unit":
          fontSizeUnitValue = deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator).merged(with: fontSizeUnitValue)
        case "font_weight":
          fontWeightValue = deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator).merged(with: fontWeightValue)
        case "inactive_background_color":
          inactiveBackgroundColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.inactiveBackgroundColorValidator).merged(with: inactiveBackgroundColorValue)
        case "inactive_font_weight":
          inactiveFontWeightValue = deserialize(__dictValue, validator: ResolvedValue.inactiveFontWeightValidator).merged(with: inactiveFontWeightValue)
        case "inactive_text_color":
          inactiveTextColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.inactiveTextColorValidator).merged(with: inactiveTextColorValue)
        case "item_spacing":
          itemSpacingValue = deserialize(__dictValue, validator: ResolvedValue.itemSpacingValidator).merged(with: itemSpacingValue)
        case "letter_spacing":
          letterSpacingValue = deserialize(__dictValue).merged(with: letterSpacingValue)
        case "line_height":
          lineHeightValue = deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator).merged(with: lineHeightValue)
        case "paddings":
          paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
        case parent?.activeBackgroundColor?.link:
          activeBackgroundColorValue = activeBackgroundColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.activeBackgroundColorValidator))
        case parent?.activeFontWeight?.link:
          activeFontWeightValue = activeFontWeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.activeFontWeightValidator))
        case parent?.activeTextColor?.link:
          activeTextColorValue = activeTextColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.activeTextColorValidator))
        case parent?.animationDuration?.link:
          animationDurationValue = animationDurationValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.animationDurationValidator))
        case parent?.animationType?.link:
          animationTypeValue = animationTypeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.animationTypeValidator))
        case parent?.cornerRadius?.link:
          cornerRadiusValue = cornerRadiusValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.cornerRadiusValidator))
        case parent?.cornersRadius?.link:
          cornersRadiusValue = cornersRadiusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.cornersRadiusValidator, type: DivCornersRadiusTemplate.self))
        case parent?.fontFamily?.link:
          fontFamilyValue = fontFamilyValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontFamilyValidator))
        case parent?.fontSize?.link:
          fontSizeValue = fontSizeValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeValidator))
        case parent?.fontSizeUnit?.link:
          fontSizeUnitValue = fontSizeUnitValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontSizeUnitValidator))
        case parent?.fontWeight?.link:
          fontWeightValue = fontWeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.fontWeightValidator))
        case parent?.inactiveBackgroundColor?.link:
          inactiveBackgroundColorValue = inactiveBackgroundColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.inactiveBackgroundColorValidator))
        case parent?.inactiveFontWeight?.link:
          inactiveFontWeightValue = inactiveFontWeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.inactiveFontWeightValidator))
        case parent?.inactiveTextColor?.link:
          inactiveTextColorValue = inactiveTextColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.inactiveTextColorValidator))
        case parent?.itemSpacing?.link:
          itemSpacingValue = itemSpacingValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.itemSpacingValidator))
        case parent?.letterSpacing?.link:
          letterSpacingValue = letterSpacingValue.merged(with: deserialize(__dictValue))
        case parent?.lineHeight?.link:
          lineHeightValue = lineHeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.lineHeightValidator))
        case parent?.paddings?.link:
          paddingsValue = paddingsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self))
        default: break
        }
      }
      if let parent = parent {
        cornersRadiusValue = cornersRadiusValue.merged(with: parent.cornersRadius?.resolveOptionalValue(context: context, validator: ResolvedValue.cornersRadiusValidator, useOnlyLinks: true))
        paddingsValue = paddingsValue.merged(with: parent.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true))
      }
      let errors = mergeErrors(
        activeBackgroundColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_background_color", error: $0) },
        activeFontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_font_weight", error: $0) },
        activeTextColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "active_text_color", error: $0) },
        animationDurationValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_duration", error: $0) },
        animationTypeValue.errorsOrWarnings?.map { .nestedObjectError(field: "animation_type", error: $0) },
        cornerRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corner_radius", error: $0) },
        cornersRadiusValue.errorsOrWarnings?.map { .nestedObjectError(field: "corners_radius", error: $0) },
        fontFamilyValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_family", error: $0) },
        fontSizeValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size", error: $0) },
        fontSizeUnitValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_size_unit", error: $0) },
        fontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "font_weight", error: $0) },
        inactiveBackgroundColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_background_color", error: $0) },
        inactiveFontWeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_font_weight", error: $0) },
        inactiveTextColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "inactive_text_color", error: $0) },
        itemSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "item_spacing", error: $0) },
        letterSpacingValue.errorsOrWarnings?.map { .nestedObjectError(field: "letter_spacing", error: $0) },
        lineHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "line_height", error: $0) },
        paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) }
      )
      let result = DivTabs.TabTitleStyle(
        activeBackgroundColor: activeBackgroundColorValue.value,
        activeFontWeight: activeFontWeightValue.value,
        activeTextColor: activeTextColorValue.value,
        animationDuration: animationDurationValue.value,
        animationType: animationTypeValue.value,
        cornerRadius: cornerRadiusValue.value,
        cornersRadius: cornersRadiusValue.value,
        fontFamily: fontFamilyValue.value,
        fontSize: fontSizeValue.value,
        fontSizeUnit: fontSizeUnitValue.value,
        fontWeight: fontWeightValue.value,
        inactiveBackgroundColor: inactiveBackgroundColorValue.value,
        inactiveFontWeight: inactiveFontWeightValue.value,
        inactiveTextColor: inactiveTextColorValue.value,
        itemSpacing: itemSpacingValue.value,
        letterSpacing: letterSpacingValue.value,
        lineHeight: lineHeightValue.value,
        paddings: paddingsValue.value
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> TabTitleStyleTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> TabTitleStyleTemplate {
      let merged = try mergedWithParent(templates: templates)

      return TabTitleStyleTemplate(
        activeBackgroundColor: merged.activeBackgroundColor,
        activeFontWeight: merged.activeFontWeight,
        activeTextColor: merged.activeTextColor,
        animationDuration: merged.animationDuration,
        animationType: merged.animationType,
        cornerRadius: merged.cornerRadius,
        cornersRadius: merged.cornersRadius?.tryResolveParent(templates: templates),
        fontFamily: merged.fontFamily,
        fontSize: merged.fontSize,
        fontSizeUnit: merged.fontSizeUnit,
        fontWeight: merged.fontWeight,
        inactiveBackgroundColor: merged.inactiveBackgroundColor,
        inactiveFontWeight: merged.inactiveFontWeight,
        inactiveTextColor: merged.inactiveTextColor,
        itemSpacing: merged.itemSpacing,
        letterSpacing: merged.letterSpacing,
        lineHeight: merged.lineHeight,
        paddings: merged.paddings?.tryResolveParent(templates: templates)
      )
    }
  }

  public static let type: String = "tabs"
  public let parent: String? // at least 1 char
  public let accessibility: Field<DivAccessibilityTemplate>?
  public let alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>?
  public let alignmentVertical: Field<Expression<DivAlignmentVertical>>?
  public let alpha: Field<Expression<Double>>? // constraint: number >= 0.0 && number <= 1.0; default value: 1.0
  public let background: Field<[DivBackgroundTemplate]>? // at least 1 elements
  public let border: Field<DivBorderTemplate>?
  public let columnSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let dynamicHeight: Field<Expression<Bool>>? // default value: false
  public let extensions: Field<[DivExtensionTemplate]>? // at least 1 elements
  public let focus: Field<DivFocusTemplate>?
  public let hasSeparator: Field<Expression<Bool>>? // default value: false
  public let height: Field<DivSizeTemplate>? // default value: .divWrapContentSize(DivWrapContentSize())
  public let id: Field<String>? // at least 1 char
  public let items: Field<[ItemTemplate]>? // at least 1 elements
  public let margins: Field<DivEdgeInsetsTemplate>?
  public let paddings: Field<DivEdgeInsetsTemplate>?
  public let restrictParentScroll: Field<Expression<Bool>>? // default value: false
  public let rowSpan: Field<Expression<Int>>? // constraint: number >= 0
  public let selectedActions: Field<[DivActionTemplate]>? // at least 1 elements
  public let selectedTab: Field<Expression<Int>>? // constraint: number >= 0; default value: 0
  public let separatorColor: Field<Expression<Color>>? // default value: #14000000
  public let separatorPaddings: Field<DivEdgeInsetsTemplate>? // default value: DivEdgeInsets(bottom: .value(0), left: .value(12), right: .value(12), top: .value(0))
  public let switchTabsByContentSwipeEnabled: Field<Expression<Bool>>? // default value: true
  public let tabTitleStyle: Field<TabTitleStyleTemplate>?
  public let titlePaddings: Field<DivEdgeInsetsTemplate>? // default value: DivEdgeInsets(bottom: .value(8), left: .value(12), right: .value(12), top: .value(0))
  public let tooltips: Field<[DivTooltipTemplate]>? // at least 1 elements
  public let transform: Field<DivTransformTemplate>?
  public let transitionChange: Field<DivChangeTransitionTemplate>?
  public let transitionIn: Field<DivAppearanceTransitionTemplate>?
  public let transitionOut: Field<DivAppearanceTransitionTemplate>?
  public let transitionTriggers: Field<[DivTransitionTrigger]>? // at least 1 elements
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
        alignmentHorizontal: try dictionary.getOptionalExpressionField("alignment_horizontal"),
        alignmentVertical: try dictionary.getOptionalExpressionField("alignment_vertical"),
        alpha: try dictionary.getOptionalExpressionField("alpha"),
        background: try dictionary.getOptionalArray("background", templateToType: templateToType),
        border: try dictionary.getOptionalField("border", templateToType: templateToType),
        columnSpan: try dictionary.getOptionalExpressionField("column_span"),
        dynamicHeight: try dictionary.getOptionalExpressionField("dynamic_height"),
        extensions: try dictionary.getOptionalArray("extensions", templateToType: templateToType),
        focus: try dictionary.getOptionalField("focus", templateToType: templateToType),
        hasSeparator: try dictionary.getOptionalExpressionField("has_separator"),
        height: try dictionary.getOptionalField("height", templateToType: templateToType),
        id: try dictionary.getOptionalField("id"),
        items: try dictionary.getOptionalArray("items", templateToType: templateToType),
        margins: try dictionary.getOptionalField("margins", templateToType: templateToType),
        paddings: try dictionary.getOptionalField("paddings", templateToType: templateToType),
        restrictParentScroll: try dictionary.getOptionalExpressionField("restrict_parent_scroll"),
        rowSpan: try dictionary.getOptionalExpressionField("row_span"),
        selectedActions: try dictionary.getOptionalArray("selected_actions", templateToType: templateToType),
        selectedTab: try dictionary.getOptionalExpressionField("selected_tab"),
        separatorColor: try dictionary.getOptionalExpressionField("separator_color", transform: Color.color(withHexString:)),
        separatorPaddings: try dictionary.getOptionalField("separator_paddings", templateToType: templateToType),
        switchTabsByContentSwipeEnabled: try dictionary.getOptionalExpressionField("switch_tabs_by_content_swipe_enabled"),
        tabTitleStyle: try dictionary.getOptionalField("tab_title_style", templateToType: templateToType),
        titlePaddings: try dictionary.getOptionalField("title_paddings", templateToType: templateToType),
        tooltips: try dictionary.getOptionalArray("tooltips", templateToType: templateToType),
        transform: try dictionary.getOptionalField("transform", templateToType: templateToType),
        transitionChange: try dictionary.getOptionalField("transition_change", templateToType: templateToType),
        transitionIn: try dictionary.getOptionalField("transition_in", templateToType: templateToType),
        transitionOut: try dictionary.getOptionalField("transition_out", templateToType: templateToType),
        transitionTriggers: try dictionary.getOptionalArray("transition_triggers"),
        visibility: try dictionary.getOptionalExpressionField("visibility"),
        visibilityAction: try dictionary.getOptionalField("visibility_action", templateToType: templateToType),
        visibilityActions: try dictionary.getOptionalArray("visibility_actions", templateToType: templateToType),
        width: try dictionary.getOptionalField("width", templateToType: templateToType)
      )
    } catch let DeserializationError.invalidFieldRepresentation(field: field, representation: representation) {
      throw DeserializationError.invalidFieldRepresentation(field: "div-tabs_template." + field, representation: representation)
    }
  }

  init(
    parent: String?,
    accessibility: Field<DivAccessibilityTemplate>? = nil,
    alignmentHorizontal: Field<Expression<DivAlignmentHorizontal>>? = nil,
    alignmentVertical: Field<Expression<DivAlignmentVertical>>? = nil,
    alpha: Field<Expression<Double>>? = nil,
    background: Field<[DivBackgroundTemplate]>? = nil,
    border: Field<DivBorderTemplate>? = nil,
    columnSpan: Field<Expression<Int>>? = nil,
    dynamicHeight: Field<Expression<Bool>>? = nil,
    extensions: Field<[DivExtensionTemplate]>? = nil,
    focus: Field<DivFocusTemplate>? = nil,
    hasSeparator: Field<Expression<Bool>>? = nil,
    height: Field<DivSizeTemplate>? = nil,
    id: Field<String>? = nil,
    items: Field<[ItemTemplate]>? = nil,
    margins: Field<DivEdgeInsetsTemplate>? = nil,
    paddings: Field<DivEdgeInsetsTemplate>? = nil,
    restrictParentScroll: Field<Expression<Bool>>? = nil,
    rowSpan: Field<Expression<Int>>? = nil,
    selectedActions: Field<[DivActionTemplate]>? = nil,
    selectedTab: Field<Expression<Int>>? = nil,
    separatorColor: Field<Expression<Color>>? = nil,
    separatorPaddings: Field<DivEdgeInsetsTemplate>? = nil,
    switchTabsByContentSwipeEnabled: Field<Expression<Bool>>? = nil,
    tabTitleStyle: Field<TabTitleStyleTemplate>? = nil,
    titlePaddings: Field<DivEdgeInsetsTemplate>? = nil,
    tooltips: Field<[DivTooltipTemplate]>? = nil,
    transform: Field<DivTransformTemplate>? = nil,
    transitionChange: Field<DivChangeTransitionTemplate>? = nil,
    transitionIn: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionOut: Field<DivAppearanceTransitionTemplate>? = nil,
    transitionTriggers: Field<[DivTransitionTrigger]>? = nil,
    visibility: Field<Expression<DivVisibility>>? = nil,
    visibilityAction: Field<DivVisibilityActionTemplate>? = nil,
    visibilityActions: Field<[DivVisibilityActionTemplate]>? = nil,
    width: Field<DivSizeTemplate>? = nil
  ) {
    self.parent = parent
    self.accessibility = accessibility
    self.alignmentHorizontal = alignmentHorizontal
    self.alignmentVertical = alignmentVertical
    self.alpha = alpha
    self.background = background
    self.border = border
    self.columnSpan = columnSpan
    self.dynamicHeight = dynamicHeight
    self.extensions = extensions
    self.focus = focus
    self.hasSeparator = hasSeparator
    self.height = height
    self.id = id
    self.items = items
    self.margins = margins
    self.paddings = paddings
    self.restrictParentScroll = restrictParentScroll
    self.rowSpan = rowSpan
    self.selectedActions = selectedActions
    self.selectedTab = selectedTab
    self.separatorColor = separatorColor
    self.separatorPaddings = separatorPaddings
    self.switchTabsByContentSwipeEnabled = switchTabsByContentSwipeEnabled
    self.tabTitleStyle = tabTitleStyle
    self.titlePaddings = titlePaddings
    self.tooltips = tooltips
    self.transform = transform
    self.transitionChange = transitionChange
    self.transitionIn = transitionIn
    self.transitionOut = transitionOut
    self.transitionTriggers = transitionTriggers
    self.visibility = visibility
    self.visibilityAction = visibilityAction
    self.visibilityActions = visibilityActions
    self.width = width
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivTabsTemplate?) -> DeserializationResult<DivTabs> {
    let accessibilityValue = parent?.accessibility?.resolveOptionalValue(context: context, validator: ResolvedValue.accessibilityValidator, useOnlyLinks: true) ?? .noValue
    let alignmentHorizontalValue = parent?.alignmentHorizontal?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentHorizontalValidator) ?? .noValue
    let alignmentVerticalValue = parent?.alignmentVertical?.resolveOptionalValue(context: context, validator: ResolvedValue.alignmentVerticalValidator) ?? .noValue
    let alphaValue = parent?.alpha?.resolveOptionalValue(context: context, validator: ResolvedValue.alphaValidator) ?? .noValue
    let backgroundValue = parent?.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true) ?? .noValue
    let borderValue = parent?.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true) ?? .noValue
    let columnSpanValue = parent?.columnSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.columnSpanValidator) ?? .noValue
    let dynamicHeightValue = parent?.dynamicHeight?.resolveOptionalValue(context: context, validator: ResolvedValue.dynamicHeightValidator) ?? .noValue
    let extensionsValue = parent?.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true) ?? .noValue
    let focusValue = parent?.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true) ?? .noValue
    let hasSeparatorValue = parent?.hasSeparator?.resolveOptionalValue(context: context, validator: ResolvedValue.hasSeparatorValidator) ?? .noValue
    let heightValue = parent?.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true) ?? .noValue
    let idValue = parent?.id?.resolveOptionalValue(context: context, validator: ResolvedValue.idValidator) ?? .noValue
    let itemsValue = parent?.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true) ?? .noValue
    let marginsValue = parent?.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true) ?? .noValue
    let paddingsValue = parent?.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true) ?? .noValue
    let restrictParentScrollValue = parent?.restrictParentScroll?.resolveOptionalValue(context: context, validator: ResolvedValue.restrictParentScrollValidator) ?? .noValue
    let rowSpanValue = parent?.rowSpan?.resolveOptionalValue(context: context, validator: ResolvedValue.rowSpanValidator) ?? .noValue
    let selectedActionsValue = parent?.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true) ?? .noValue
    let selectedTabValue = parent?.selectedTab?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedTabValidator) ?? .noValue
    let separatorColorValue = parent?.separatorColor?.resolveOptionalValue(context: context, transform: Color.color(withHexString:), validator: ResolvedValue.separatorColorValidator) ?? .noValue
    let separatorPaddingsValue = parent?.separatorPaddings?.resolveOptionalValue(context: context, validator: ResolvedValue.separatorPaddingsValidator, useOnlyLinks: true) ?? .noValue
    let switchTabsByContentSwipeEnabledValue = parent?.switchTabsByContentSwipeEnabled?.resolveOptionalValue(context: context, validator: ResolvedValue.switchTabsByContentSwipeEnabledValidator) ?? .noValue
    let tabTitleStyleValue = parent?.tabTitleStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.tabTitleStyleValidator, useOnlyLinks: true) ?? .noValue
    let titlePaddingsValue = parent?.titlePaddings?.resolveOptionalValue(context: context, validator: ResolvedValue.titlePaddingsValidator, useOnlyLinks: true) ?? .noValue
    let tooltipsValue = parent?.tooltips?.resolveOptionalValue(context: context, validator: ResolvedValue.tooltipsValidator, useOnlyLinks: true) ?? .noValue
    let transformValue = parent?.transform?.resolveOptionalValue(context: context, validator: ResolvedValue.transformValidator, useOnlyLinks: true) ?? .noValue
    let transitionChangeValue = parent?.transitionChange?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionChangeValidator, useOnlyLinks: true) ?? .noValue
    let transitionInValue = parent?.transitionIn?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionInValidator, useOnlyLinks: true) ?? .noValue
    let transitionOutValue = parent?.transitionOut?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionOutValidator, useOnlyLinks: true) ?? .noValue
    let transitionTriggersValue = parent?.transitionTriggers?.resolveOptionalValue(context: context, validator: ResolvedValue.transitionTriggersValidator) ?? .noValue
    let visibilityValue = parent?.visibility?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityValidator) ?? .noValue
    let visibilityActionValue = parent?.visibilityAction?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionValidator, useOnlyLinks: true) ?? .noValue
    let visibilityActionsValue = parent?.visibilityActions?.resolveOptionalValue(context: context, validator: ResolvedValue.visibilityActionsValidator, useOnlyLinks: true) ?? .noValue
    let widthValue = parent?.width?.resolveOptionalValue(context: context, validator: ResolvedValue.widthValidator, useOnlyLinks: true) ?? .noValue
    var errors = mergeErrors(
      accessibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "accessibility", error: $0) },
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      dynamicHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "dynamic_height", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      hasSeparatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "has_separator", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      restrictParentScrollValue.errorsOrWarnings?.map { .nestedObjectError(field: "restrict_parent_scroll", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      selectedTabValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_tab", error: $0) },
      separatorColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "separator_color", error: $0) },
      separatorPaddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "separator_paddings", error: $0) },
      switchTabsByContentSwipeEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "switch_tabs_by_content_swipe_enabled", error: $0) },
      tabTitleStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "tab_title_style", error: $0) },
      titlePaddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "title_paddings", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = itemsValue {
      errors.append(.requiredFieldIsMissing(field: "items"))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTabs(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      dynamicHeight: dynamicHeightValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      hasSeparator: hasSeparatorValue.value,
      height: heightValue.value,
      id: idValue.value,
      items: itemsNonNil,
      margins: marginsValue.value,
      paddings: paddingsValue.value,
      restrictParentScroll: restrictParentScrollValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
      selectedTab: selectedTabValue.value,
      separatorColor: separatorColorValue.value,
      separatorPaddings: separatorPaddingsValue.value,
      switchTabsByContentSwipeEnabled: switchTabsByContentSwipeEnabledValue.value,
      tabTitleStyle: tabTitleStyleValue.value,
      titlePaddings: titlePaddingsValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTabsTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTabs> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var accessibilityValue: DeserializationResult<DivAccessibility> = .noValue
    var alignmentHorizontalValue: DeserializationResult<Expression<DivAlignmentHorizontal>> = parent?.alignmentHorizontal?.value() ?? .noValue
    var alignmentVerticalValue: DeserializationResult<Expression<DivAlignmentVertical>> = parent?.alignmentVertical?.value() ?? .noValue
    var alphaValue: DeserializationResult<Expression<Double>> = parent?.alpha?.value() ?? .noValue
    var backgroundValue: DeserializationResult<[DivBackground]> = .noValue
    var borderValue: DeserializationResult<DivBorder> = .noValue
    var columnSpanValue: DeserializationResult<Expression<Int>> = parent?.columnSpan?.value() ?? .noValue
    var dynamicHeightValue: DeserializationResult<Expression<Bool>> = parent?.dynamicHeight?.value() ?? .noValue
    var extensionsValue: DeserializationResult<[DivExtension]> = .noValue
    var focusValue: DeserializationResult<DivFocus> = .noValue
    var hasSeparatorValue: DeserializationResult<Expression<Bool>> = parent?.hasSeparator?.value() ?? .noValue
    var heightValue: DeserializationResult<DivSize> = .noValue
    var idValue: DeserializationResult<String> = parent?.id?.value(validatedBy: ResolvedValue.idValidator) ?? .noValue
    var itemsValue: DeserializationResult<[DivTabs.Item]> = .noValue
    var marginsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var paddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var restrictParentScrollValue: DeserializationResult<Expression<Bool>> = parent?.restrictParentScroll?.value() ?? .noValue
    var rowSpanValue: DeserializationResult<Expression<Int>> = parent?.rowSpan?.value() ?? .noValue
    var selectedActionsValue: DeserializationResult<[DivAction]> = .noValue
    var selectedTabValue: DeserializationResult<Expression<Int>> = parent?.selectedTab?.value() ?? .noValue
    var separatorColorValue: DeserializationResult<Expression<Color>> = parent?.separatorColor?.value() ?? .noValue
    var separatorPaddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var switchTabsByContentSwipeEnabledValue: DeserializationResult<Expression<Bool>> = parent?.switchTabsByContentSwipeEnabled?.value() ?? .noValue
    var tabTitleStyleValue: DeserializationResult<DivTabs.TabTitleStyle> = .noValue
    var titlePaddingsValue: DeserializationResult<DivEdgeInsets> = .noValue
    var tooltipsValue: DeserializationResult<[DivTooltip]> = .noValue
    var transformValue: DeserializationResult<DivTransform> = .noValue
    var transitionChangeValue: DeserializationResult<DivChangeTransition> = .noValue
    var transitionInValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionOutValue: DeserializationResult<DivAppearanceTransition> = .noValue
    var transitionTriggersValue: DeserializationResult<[DivTransitionTrigger]> = parent?.transitionTriggers?.value(validatedBy: ResolvedValue.transitionTriggersValidator) ?? .noValue
    var visibilityValue: DeserializationResult<Expression<DivVisibility>> = parent?.visibility?.value() ?? .noValue
    var visibilityActionValue: DeserializationResult<DivVisibilityAction> = .noValue
    var visibilityActionsValue: DeserializationResult<[DivVisibilityAction]> = .noValue
    var widthValue: DeserializationResult<DivSize> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "accessibility":
        accessibilityValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.accessibilityValidator, type: DivAccessibilityTemplate.self).merged(with: accessibilityValue)
      case "alignment_horizontal":
        alignmentHorizontalValue = deserialize(__dictValue, validator: ResolvedValue.alignmentHorizontalValidator).merged(with: alignmentHorizontalValue)
      case "alignment_vertical":
        alignmentVerticalValue = deserialize(__dictValue, validator: ResolvedValue.alignmentVerticalValidator).merged(with: alignmentVerticalValue)
      case "alpha":
        alphaValue = deserialize(__dictValue, validator: ResolvedValue.alphaValidator).merged(with: alphaValue)
      case "background":
        backgroundValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self).merged(with: backgroundValue)
      case "border":
        borderValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self).merged(with: borderValue)
      case "column_span":
        columnSpanValue = deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator).merged(with: columnSpanValue)
      case "dynamic_height":
        dynamicHeightValue = deserialize(__dictValue, validator: ResolvedValue.dynamicHeightValidator).merged(with: dynamicHeightValue)
      case "extensions":
        extensionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self).merged(with: extensionsValue)
      case "focus":
        focusValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self).merged(with: focusValue)
      case "has_separator":
        hasSeparatorValue = deserialize(__dictValue, validator: ResolvedValue.hasSeparatorValidator).merged(with: hasSeparatorValue)
      case "height":
        heightValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self).merged(with: heightValue)
      case "id":
        idValue = deserialize(__dictValue, validator: ResolvedValue.idValidator).merged(with: idValue)
      case "items":
        itemsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivTabsTemplate.ItemTemplate.self).merged(with: itemsValue)
      case "margins":
        marginsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self).merged(with: marginsValue)
      case "paddings":
        paddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self).merged(with: paddingsValue)
      case "restrict_parent_scroll":
        restrictParentScrollValue = deserialize(__dictValue, validator: ResolvedValue.restrictParentScrollValidator).merged(with: restrictParentScrollValue)
      case "row_span":
        rowSpanValue = deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator).merged(with: rowSpanValue)
      case "selected_actions":
        selectedActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self).merged(with: selectedActionsValue)
      case "selected_tab":
        selectedTabValue = deserialize(__dictValue, validator: ResolvedValue.selectedTabValidator).merged(with: selectedTabValue)
      case "separator_color":
        separatorColorValue = deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.separatorColorValidator).merged(with: separatorColorValue)
      case "separator_paddings":
        separatorPaddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.separatorPaddingsValidator, type: DivEdgeInsetsTemplate.self).merged(with: separatorPaddingsValue)
      case "switch_tabs_by_content_swipe_enabled":
        switchTabsByContentSwipeEnabledValue = deserialize(__dictValue, validator: ResolvedValue.switchTabsByContentSwipeEnabledValidator).merged(with: switchTabsByContentSwipeEnabledValue)
      case "tab_title_style":
        tabTitleStyleValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tabTitleStyleValidator, type: DivTabsTemplate.TabTitleStyleTemplate.self).merged(with: tabTitleStyleValue)
      case "title_paddings":
        titlePaddingsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.titlePaddingsValidator, type: DivEdgeInsetsTemplate.self).merged(with: titlePaddingsValue)
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
      case parent?.alignmentHorizontal?.link:
        alignmentHorizontalValue = alignmentHorizontalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alignmentHorizontalValidator))
      case parent?.alignmentVertical?.link:
        alignmentVerticalValue = alignmentVerticalValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alignmentVerticalValidator))
      case parent?.alpha?.link:
        alphaValue = alphaValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.alphaValidator))
      case parent?.background?.link:
        backgroundValue = backgroundValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.backgroundValidator, type: DivBackgroundTemplate.self))
      case parent?.border?.link:
        borderValue = borderValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.borderValidator, type: DivBorderTemplate.self))
      case parent?.columnSpan?.link:
        columnSpanValue = columnSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.columnSpanValidator))
      case parent?.dynamicHeight?.link:
        dynamicHeightValue = dynamicHeightValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.dynamicHeightValidator))
      case parent?.extensions?.link:
        extensionsValue = extensionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.extensionsValidator, type: DivExtensionTemplate.self))
      case parent?.focus?.link:
        focusValue = focusValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.focusValidator, type: DivFocusTemplate.self))
      case parent?.hasSeparator?.link:
        hasSeparatorValue = hasSeparatorValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.hasSeparatorValidator))
      case parent?.height?.link:
        heightValue = heightValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.heightValidator, type: DivSizeTemplate.self))
      case parent?.id?.link:
        idValue = idValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.idValidator))
      case parent?.items?.link:
        itemsValue = itemsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.itemsValidator, type: DivTabsTemplate.ItemTemplate.self))
      case parent?.margins?.link:
        marginsValue = marginsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.marginsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.paddings?.link:
        paddingsValue = paddingsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.paddingsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.restrictParentScroll?.link:
        restrictParentScrollValue = restrictParentScrollValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.restrictParentScrollValidator))
      case parent?.rowSpan?.link:
        rowSpanValue = rowSpanValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.rowSpanValidator))
      case parent?.selectedActions?.link:
        selectedActionsValue = selectedActionsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.selectedActionsValidator, type: DivActionTemplate.self))
      case parent?.selectedTab?.link:
        selectedTabValue = selectedTabValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.selectedTabValidator))
      case parent?.separatorColor?.link:
        separatorColorValue = separatorColorValue.merged(with: deserialize(__dictValue, transform: Color.color(withHexString:), validator: ResolvedValue.separatorColorValidator))
      case parent?.separatorPaddings?.link:
        separatorPaddingsValue = separatorPaddingsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.separatorPaddingsValidator, type: DivEdgeInsetsTemplate.self))
      case parent?.switchTabsByContentSwipeEnabled?.link:
        switchTabsByContentSwipeEnabledValue = switchTabsByContentSwipeEnabledValue.merged(with: deserialize(__dictValue, validator: ResolvedValue.switchTabsByContentSwipeEnabledValidator))
      case parent?.tabTitleStyle?.link:
        tabTitleStyleValue = tabTitleStyleValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.tabTitleStyleValidator, type: DivTabsTemplate.TabTitleStyleTemplate.self))
      case parent?.titlePaddings?.link:
        titlePaddingsValue = titlePaddingsValue.merged(with: deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, validator: ResolvedValue.titlePaddingsValidator, type: DivEdgeInsetsTemplate.self))
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
      backgroundValue = backgroundValue.merged(with: parent.background?.resolveOptionalValue(context: context, validator: ResolvedValue.backgroundValidator, useOnlyLinks: true))
      borderValue = borderValue.merged(with: parent.border?.resolveOptionalValue(context: context, validator: ResolvedValue.borderValidator, useOnlyLinks: true))
      extensionsValue = extensionsValue.merged(with: parent.extensions?.resolveOptionalValue(context: context, validator: ResolvedValue.extensionsValidator, useOnlyLinks: true))
      focusValue = focusValue.merged(with: parent.focus?.resolveOptionalValue(context: context, validator: ResolvedValue.focusValidator, useOnlyLinks: true))
      heightValue = heightValue.merged(with: parent.height?.resolveOptionalValue(context: context, validator: ResolvedValue.heightValidator, useOnlyLinks: true))
      itemsValue = itemsValue.merged(with: parent.items?.resolveValue(context: context, validator: ResolvedValue.itemsValidator, useOnlyLinks: true))
      marginsValue = marginsValue.merged(with: parent.margins?.resolveOptionalValue(context: context, validator: ResolvedValue.marginsValidator, useOnlyLinks: true))
      paddingsValue = paddingsValue.merged(with: parent.paddings?.resolveOptionalValue(context: context, validator: ResolvedValue.paddingsValidator, useOnlyLinks: true))
      selectedActionsValue = selectedActionsValue.merged(with: parent.selectedActions?.resolveOptionalValue(context: context, validator: ResolvedValue.selectedActionsValidator, useOnlyLinks: true))
      separatorPaddingsValue = separatorPaddingsValue.merged(with: parent.separatorPaddings?.resolveOptionalValue(context: context, validator: ResolvedValue.separatorPaddingsValidator, useOnlyLinks: true))
      tabTitleStyleValue = tabTitleStyleValue.merged(with: parent.tabTitleStyle?.resolveOptionalValue(context: context, validator: ResolvedValue.tabTitleStyleValidator, useOnlyLinks: true))
      titlePaddingsValue = titlePaddingsValue.merged(with: parent.titlePaddings?.resolveOptionalValue(context: context, validator: ResolvedValue.titlePaddingsValidator, useOnlyLinks: true))
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
      alignmentHorizontalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_horizontal", error: $0) },
      alignmentVerticalValue.errorsOrWarnings?.map { .nestedObjectError(field: "alignment_vertical", error: $0) },
      alphaValue.errorsOrWarnings?.map { .nestedObjectError(field: "alpha", error: $0) },
      backgroundValue.errorsOrWarnings?.map { .nestedObjectError(field: "background", error: $0) },
      borderValue.errorsOrWarnings?.map { .nestedObjectError(field: "border", error: $0) },
      columnSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "column_span", error: $0) },
      dynamicHeightValue.errorsOrWarnings?.map { .nestedObjectError(field: "dynamic_height", error: $0) },
      extensionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "extensions", error: $0) },
      focusValue.errorsOrWarnings?.map { .nestedObjectError(field: "focus", error: $0) },
      hasSeparatorValue.errorsOrWarnings?.map { .nestedObjectError(field: "has_separator", error: $0) },
      heightValue.errorsOrWarnings?.map { .nestedObjectError(field: "height", error: $0) },
      idValue.errorsOrWarnings?.map { .nestedObjectError(field: "id", error: $0) },
      itemsValue.errorsOrWarnings?.map { .nestedObjectError(field: "items", error: $0) },
      marginsValue.errorsOrWarnings?.map { .nestedObjectError(field: "margins", error: $0) },
      paddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "paddings", error: $0) },
      restrictParentScrollValue.errorsOrWarnings?.map { .nestedObjectError(field: "restrict_parent_scroll", error: $0) },
      rowSpanValue.errorsOrWarnings?.map { .nestedObjectError(field: "row_span", error: $0) },
      selectedActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_actions", error: $0) },
      selectedTabValue.errorsOrWarnings?.map { .nestedObjectError(field: "selected_tab", error: $0) },
      separatorColorValue.errorsOrWarnings?.map { .nestedObjectError(field: "separator_color", error: $0) },
      separatorPaddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "separator_paddings", error: $0) },
      switchTabsByContentSwipeEnabledValue.errorsOrWarnings?.map { .nestedObjectError(field: "switch_tabs_by_content_swipe_enabled", error: $0) },
      tabTitleStyleValue.errorsOrWarnings?.map { .nestedObjectError(field: "tab_title_style", error: $0) },
      titlePaddingsValue.errorsOrWarnings?.map { .nestedObjectError(field: "title_paddings", error: $0) },
      tooltipsValue.errorsOrWarnings?.map { .nestedObjectError(field: "tooltips", error: $0) },
      transformValue.errorsOrWarnings?.map { .nestedObjectError(field: "transform", error: $0) },
      transitionChangeValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_change", error: $0) },
      transitionInValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_in", error: $0) },
      transitionOutValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_out", error: $0) },
      transitionTriggersValue.errorsOrWarnings?.map { .nestedObjectError(field: "transition_triggers", error: $0) },
      visibilityValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility", error: $0) },
      visibilityActionValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_action", error: $0) },
      visibilityActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "visibility_actions", error: $0) },
      widthValue.errorsOrWarnings?.map { .nestedObjectError(field: "width", error: $0) }
    )
    if case .noValue = itemsValue {
      errors.append(.requiredFieldIsMissing(field: "items"))
    }
    guard
      let itemsNonNil = itemsValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivTabs(
      accessibility: accessibilityValue.value,
      alignmentHorizontal: alignmentHorizontalValue.value,
      alignmentVertical: alignmentVerticalValue.value,
      alpha: alphaValue.value,
      background: backgroundValue.value,
      border: borderValue.value,
      columnSpan: columnSpanValue.value,
      dynamicHeight: dynamicHeightValue.value,
      extensions: extensionsValue.value,
      focus: focusValue.value,
      hasSeparator: hasSeparatorValue.value,
      height: heightValue.value,
      id: idValue.value,
      items: itemsNonNil,
      margins: marginsValue.value,
      paddings: paddingsValue.value,
      restrictParentScroll: restrictParentScrollValue.value,
      rowSpan: rowSpanValue.value,
      selectedActions: selectedActionsValue.value,
      selectedTab: selectedTabValue.value,
      separatorColor: separatorColorValue.value,
      separatorPaddings: separatorPaddingsValue.value,
      switchTabsByContentSwipeEnabled: switchTabsByContentSwipeEnabledValue.value,
      tabTitleStyle: tabTitleStyleValue.value,
      titlePaddings: titlePaddingsValue.value,
      tooltips: tooltipsValue.value,
      transform: transformValue.value,
      transitionChange: transitionChangeValue.value,
      transitionIn: transitionInValue.value,
      transitionOut: transitionOutValue.value,
      transitionTriggers: transitionTriggersValue.value,
      visibility: visibilityValue.value,
      visibilityAction: visibilityActionValue.value,
      visibilityActions: visibilityActionsValue.value,
      width: widthValue.value
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivTabsTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivTabsTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivTabsTemplate(
      parent: nil,
      accessibility: accessibility ?? mergedParent.accessibility,
      alignmentHorizontal: alignmentHorizontal ?? mergedParent.alignmentHorizontal,
      alignmentVertical: alignmentVertical ?? mergedParent.alignmentVertical,
      alpha: alpha ?? mergedParent.alpha,
      background: background ?? mergedParent.background,
      border: border ?? mergedParent.border,
      columnSpan: columnSpan ?? mergedParent.columnSpan,
      dynamicHeight: dynamicHeight ?? mergedParent.dynamicHeight,
      extensions: extensions ?? mergedParent.extensions,
      focus: focus ?? mergedParent.focus,
      hasSeparator: hasSeparator ?? mergedParent.hasSeparator,
      height: height ?? mergedParent.height,
      id: id ?? mergedParent.id,
      items: items ?? mergedParent.items,
      margins: margins ?? mergedParent.margins,
      paddings: paddings ?? mergedParent.paddings,
      restrictParentScroll: restrictParentScroll ?? mergedParent.restrictParentScroll,
      rowSpan: rowSpan ?? mergedParent.rowSpan,
      selectedActions: selectedActions ?? mergedParent.selectedActions,
      selectedTab: selectedTab ?? mergedParent.selectedTab,
      separatorColor: separatorColor ?? mergedParent.separatorColor,
      separatorPaddings: separatorPaddings ?? mergedParent.separatorPaddings,
      switchTabsByContentSwipeEnabled: switchTabsByContentSwipeEnabled ?? mergedParent.switchTabsByContentSwipeEnabled,
      tabTitleStyle: tabTitleStyle ?? mergedParent.tabTitleStyle,
      titlePaddings: titlePaddings ?? mergedParent.titlePaddings,
      tooltips: tooltips ?? mergedParent.tooltips,
      transform: transform ?? mergedParent.transform,
      transitionChange: transitionChange ?? mergedParent.transitionChange,
      transitionIn: transitionIn ?? mergedParent.transitionIn,
      transitionOut: transitionOut ?? mergedParent.transitionOut,
      transitionTriggers: transitionTriggers ?? mergedParent.transitionTriggers,
      visibility: visibility ?? mergedParent.visibility,
      visibilityAction: visibilityAction ?? mergedParent.visibilityAction,
      visibilityActions: visibilityActions ?? mergedParent.visibilityActions,
      width: width ?? mergedParent.width
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTabsTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivTabsTemplate(
      parent: nil,
      accessibility: merged.accessibility?.tryResolveParent(templates: templates),
      alignmentHorizontal: merged.alignmentHorizontal,
      alignmentVertical: merged.alignmentVertical,
      alpha: merged.alpha,
      background: merged.background?.tryResolveParent(templates: templates),
      border: merged.border?.tryResolveParent(templates: templates),
      columnSpan: merged.columnSpan,
      dynamicHeight: merged.dynamicHeight,
      extensions: merged.extensions?.tryResolveParent(templates: templates),
      focus: merged.focus?.tryResolveParent(templates: templates),
      hasSeparator: merged.hasSeparator,
      height: merged.height?.tryResolveParent(templates: templates),
      id: merged.id,
      items: try merged.items?.resolveParent(templates: templates),
      margins: merged.margins?.tryResolveParent(templates: templates),
      paddings: merged.paddings?.tryResolveParent(templates: templates),
      restrictParentScroll: merged.restrictParentScroll,
      rowSpan: merged.rowSpan,
      selectedActions: merged.selectedActions?.tryResolveParent(templates: templates),
      selectedTab: merged.selectedTab,
      separatorColor: merged.separatorColor,
      separatorPaddings: merged.separatorPaddings?.tryResolveParent(templates: templates),
      switchTabsByContentSwipeEnabled: merged.switchTabsByContentSwipeEnabled,
      tabTitleStyle: merged.tabTitleStyle?.tryResolveParent(templates: templates),
      titlePaddings: merged.titlePaddings?.tryResolveParent(templates: templates),
      tooltips: merged.tooltips?.tryResolveParent(templates: templates),
      transform: merged.transform?.tryResolveParent(templates: templates),
      transitionChange: merged.transitionChange?.tryResolveParent(templates: templates),
      transitionIn: merged.transitionIn?.tryResolveParent(templates: templates),
      transitionOut: merged.transitionOut?.tryResolveParent(templates: templates),
      transitionTriggers: merged.transitionTriggers,
      visibility: merged.visibility,
      visibilityAction: merged.visibilityAction?.tryResolveParent(templates: templates),
      visibilityActions: merged.visibilityActions?.tryResolveParent(templates: templates),
      width: merged.width?.tryResolveParent(templates: templates)
    )
  }
}
