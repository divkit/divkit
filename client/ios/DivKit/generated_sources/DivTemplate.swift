// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivTemplate: TemplateValue {
  case divImageTemplate(DivImageTemplate)
  case divGifImageTemplate(DivGifImageTemplate)
  case divTextTemplate(DivTextTemplate)
  case divSeparatorTemplate(DivSeparatorTemplate)
  case divContainerTemplate(DivContainerTemplate)
  case divGridTemplate(DivGridTemplate)
  case divGalleryTemplate(DivGalleryTemplate)
  case divPagerTemplate(DivPagerTemplate)
  case divTabsTemplate(DivTabsTemplate)
  case divStateTemplate(DivStateTemplate)
  case divCustomTemplate(DivCustomTemplate)
  case divIndicatorTemplate(DivIndicatorTemplate)
  case divSliderTemplate(DivSliderTemplate)
  case divInputTemplate(DivInputTemplate)
  case divSelectTemplate(DivSelectTemplate)

  public var value: Any {
    switch self {
    case let .divImageTemplate(value):
      return value
    case let .divGifImageTemplate(value):
      return value
    case let .divTextTemplate(value):
      return value
    case let .divSeparatorTemplate(value):
      return value
    case let .divContainerTemplate(value):
      return value
    case let .divGridTemplate(value):
      return value
    case let .divGalleryTemplate(value):
      return value
    case let .divPagerTemplate(value):
      return value
    case let .divTabsTemplate(value):
      return value
    case let .divStateTemplate(value):
      return value
    case let .divCustomTemplate(value):
      return value
    case let .divIndicatorTemplate(value):
      return value
    case let .divSliderTemplate(value):
      return value
    case let .divInputTemplate(value):
      return value
    case let .divSelectTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTemplate {
    switch self {
    case let .divImageTemplate(value):
      return .divImageTemplate(try value.resolveParent(templates: templates))
    case let .divGifImageTemplate(value):
      return .divGifImageTemplate(try value.resolveParent(templates: templates))
    case let .divTextTemplate(value):
      return .divTextTemplate(try value.resolveParent(templates: templates))
    case let .divSeparatorTemplate(value):
      return .divSeparatorTemplate(try value.resolveParent(templates: templates))
    case let .divContainerTemplate(value):
      return .divContainerTemplate(try value.resolveParent(templates: templates))
    case let .divGridTemplate(value):
      return .divGridTemplate(try value.resolveParent(templates: templates))
    case let .divGalleryTemplate(value):
      return .divGalleryTemplate(try value.resolveParent(templates: templates))
    case let .divPagerTemplate(value):
      return .divPagerTemplate(try value.resolveParent(templates: templates))
    case let .divTabsTemplate(value):
      return .divTabsTemplate(try value.resolveParent(templates: templates))
    case let .divStateTemplate(value):
      return .divStateTemplate(try value.resolveParent(templates: templates))
    case let .divCustomTemplate(value):
      return .divCustomTemplate(try value.resolveParent(templates: templates))
    case let .divIndicatorTemplate(value):
      return .divIndicatorTemplate(try value.resolveParent(templates: templates))
    case let .divSliderTemplate(value):
      return .divSliderTemplate(try value.resolveParent(templates: templates))
    case let .divInputTemplate(value):
      return .divInputTemplate(try value.resolveParent(templates: templates))
    case let .divSelectTemplate(value):
      return .divSelectTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTemplate?, useOnlyLinks: Bool) -> DeserializationResult<Div> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    switch parent {
    case let .divImageTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divImage(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divImage(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divGifImageTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divGifImage(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divGifImage(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divTextTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divText(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divText(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divSeparatorTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSeparator(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSeparator(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divContainerTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divContainer(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divContainer(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divGridTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divGrid(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divGrid(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divGalleryTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divGallery(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divGallery(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divPagerTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divPager(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divPager(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divTabsTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divTabs(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divTabs(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divStateTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divState(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divState(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divCustomTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divCustom(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divCustom(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divIndicatorTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divIndicator(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divIndicator(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divSliderTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSlider(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSlider(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divInputTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divInput(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divInput(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case let .divSelectTemplate(value):
      let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSelect(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSelect(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    }
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<Div> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    switch type {
    case DivImage.type:
      let result = DivImageTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divImage(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divImage(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivGifImage.type:
      let result = DivGifImageTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divGifImage(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divGifImage(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivText.type:
      let result = DivTextTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divText(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divText(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivSeparator.type:
      let result = DivSeparatorTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSeparator(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSeparator(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivContainer.type:
      let result = DivContainerTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divContainer(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divContainer(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivGrid.type:
      let result = DivGridTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divGrid(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divGrid(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivGallery.type:
      let result = DivGalleryTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divGallery(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divGallery(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivPager.type:
      let result = DivPagerTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divPager(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divPager(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivTabs.type:
      let result = DivTabsTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divTabs(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divTabs(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivState.type:
      let result = DivStateTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divState(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divState(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivCustom.type:
      let result = DivCustomTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divCustom(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divCustom(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivIndicator.type:
      let result = DivIndicatorTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divIndicator(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divIndicator(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivSlider.type:
      let result = DivSliderTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSlider(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSlider(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivInput.type:
      let result = DivInputTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divInput(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divInput(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    case DivSelect.type:
      let result = DivSelectTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
      switch result {
      case let .success(value): return .success(.divSelect(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.divSelect(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    default:
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }
  }
}

extension DivTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case DivImageTemplate.type:
      self = .divImageTemplate(try DivImageTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivGifImageTemplate.type:
      self = .divGifImageTemplate(try DivGifImageTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivTextTemplate.type:
      self = .divTextTemplate(try DivTextTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivSeparatorTemplate.type:
      self = .divSeparatorTemplate(try DivSeparatorTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivContainerTemplate.type:
      self = .divContainerTemplate(try DivContainerTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivGridTemplate.type:
      self = .divGridTemplate(try DivGridTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivGalleryTemplate.type:
      self = .divGalleryTemplate(try DivGalleryTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivPagerTemplate.type:
      self = .divPagerTemplate(try DivPagerTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivTabsTemplate.type:
      self = .divTabsTemplate(try DivTabsTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivStateTemplate.type:
      self = .divStateTemplate(try DivStateTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivCustomTemplate.type:
      self = .divCustomTemplate(try DivCustomTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivIndicatorTemplate.type:
      self = .divIndicatorTemplate(try DivIndicatorTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivSliderTemplate.type:
      self = .divSliderTemplate(try DivSliderTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivInputTemplate.type:
      self = .divInputTemplate(try DivInputTemplate(dictionary: dictionary, templateToType: templateToType))
    case DivSelectTemplate.type:
      self = .divSelectTemplate(try DivSelectTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div_template", representation: dictionary)
    }
  }
}
