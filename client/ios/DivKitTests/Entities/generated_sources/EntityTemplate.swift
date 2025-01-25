// Generated code. Do not modify.

@testable import DivKit
import Foundation
import Serialization
import VGSL

import enum DivKit.Expression

@frozen
public enum EntityTemplate: TemplateValue {
  case entityWithArrayTemplate(EntityWithArrayTemplate)
  case entityWithArrayOfEnumsTemplate(EntityWithArrayOfEnumsTemplate)
  case entityWithArrayOfExpressionsTemplate(EntityWithArrayOfExpressionsTemplate)
  case entityWithArrayOfNestedItemsTemplate(EntityWithArrayOfNestedItemsTemplate)
  case entityWithArrayWithTransformTemplate(EntityWithArrayWithTransformTemplate)
  case entityWithComplexPropertyTemplate(EntityWithComplexPropertyTemplate)
  case entityWithComplexPropertyWithDefaultValueTemplate(EntityWithComplexPropertyWithDefaultValueTemplate)
  case entityWithEntityPropertyTemplate(EntityWithEntityPropertyTemplate)
  case entityWithOptionalComplexPropertyTemplate(EntityWithOptionalComplexPropertyTemplate)
  case entityWithOptionalPropertyTemplate(EntityWithOptionalPropertyTemplate)
  case entityWithOptionalStringEnumPropertyTemplate(EntityWithOptionalStringEnumPropertyTemplate)
  case entityWithPropertyWithDefaultValueTemplate(EntityWithPropertyWithDefaultValueTemplate)
  case entityWithRawArrayTemplate(EntityWithRawArrayTemplate)
  case entityWithRequiredPropertyTemplate(EntityWithRequiredPropertyTemplate)
  case entityWithSimplePropertiesTemplate(EntityWithSimplePropertiesTemplate)
  case entityWithStringArrayPropertyTemplate(EntityWithStringArrayPropertyTemplate)
  case entityWithStringEnumPropertyTemplate(EntityWithStringEnumPropertyTemplate)
  case entityWithStringEnumPropertyWithDefaultValueTemplate(EntityWithStringEnumPropertyWithDefaultValueTemplate)
  case entityWithoutPropertiesTemplate(EntityWithoutPropertiesTemplate)

  public var value: Any {
    switch self {
    case let .entityWithArrayTemplate(value):
      return value
    case let .entityWithArrayOfEnumsTemplate(value):
      return value
    case let .entityWithArrayOfExpressionsTemplate(value):
      return value
    case let .entityWithArrayOfNestedItemsTemplate(value):
      return value
    case let .entityWithArrayWithTransformTemplate(value):
      return value
    case let .entityWithComplexPropertyTemplate(value):
      return value
    case let .entityWithComplexPropertyWithDefaultValueTemplate(value):
      return value
    case let .entityWithEntityPropertyTemplate(value):
      return value
    case let .entityWithOptionalComplexPropertyTemplate(value):
      return value
    case let .entityWithOptionalPropertyTemplate(value):
      return value
    case let .entityWithOptionalStringEnumPropertyTemplate(value):
      return value
    case let .entityWithPropertyWithDefaultValueTemplate(value):
      return value
    case let .entityWithRawArrayTemplate(value):
      return value
    case let .entityWithRequiredPropertyTemplate(value):
      return value
    case let .entityWithSimplePropertiesTemplate(value):
      return value
    case let .entityWithStringArrayPropertyTemplate(value):
      return value
    case let .entityWithStringEnumPropertyTemplate(value):
      return value
    case let .entityWithStringEnumPropertyWithDefaultValueTemplate(value):
      return value
    case let .entityWithoutPropertiesTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> EntityTemplate {
    switch self {
    case let .entityWithArrayTemplate(value):
      return .entityWithArrayTemplate(try value.resolveParent(templates: templates))
    case let .entityWithArrayOfEnumsTemplate(value):
      return .entityWithArrayOfEnumsTemplate(try value.resolveParent(templates: templates))
    case let .entityWithArrayOfExpressionsTemplate(value):
      return .entityWithArrayOfExpressionsTemplate(try value.resolveParent(templates: templates))
    case let .entityWithArrayOfNestedItemsTemplate(value):
      return .entityWithArrayOfNestedItemsTemplate(try value.resolveParent(templates: templates))
    case let .entityWithArrayWithTransformTemplate(value):
      return .entityWithArrayWithTransformTemplate(try value.resolveParent(templates: templates))
    case let .entityWithComplexPropertyTemplate(value):
      return .entityWithComplexPropertyTemplate(try value.resolveParent(templates: templates))
    case let .entityWithComplexPropertyWithDefaultValueTemplate(value):
      return .entityWithComplexPropertyWithDefaultValueTemplate(try value.resolveParent(templates: templates))
    case let .entityWithEntityPropertyTemplate(value):
      return .entityWithEntityPropertyTemplate(try value.resolveParent(templates: templates))
    case let .entityWithOptionalComplexPropertyTemplate(value):
      return .entityWithOptionalComplexPropertyTemplate(try value.resolveParent(templates: templates))
    case let .entityWithOptionalPropertyTemplate(value):
      return .entityWithOptionalPropertyTemplate(try value.resolveParent(templates: templates))
    case let .entityWithOptionalStringEnumPropertyTemplate(value):
      return .entityWithOptionalStringEnumPropertyTemplate(try value.resolveParent(templates: templates))
    case let .entityWithPropertyWithDefaultValueTemplate(value):
      return .entityWithPropertyWithDefaultValueTemplate(try value.resolveParent(templates: templates))
    case let .entityWithRawArrayTemplate(value):
      return .entityWithRawArrayTemplate(try value.resolveParent(templates: templates))
    case let .entityWithRequiredPropertyTemplate(value):
      return .entityWithRequiredPropertyTemplate(try value.resolveParent(templates: templates))
    case let .entityWithSimplePropertiesTemplate(value):
      return .entityWithSimplePropertiesTemplate(try value.resolveParent(templates: templates))
    case let .entityWithStringArrayPropertyTemplate(value):
      return .entityWithStringArrayPropertyTemplate(try value.resolveParent(templates: templates))
    case let .entityWithStringEnumPropertyTemplate(value):
      return .entityWithStringEnumPropertyTemplate(try value.resolveParent(templates: templates))
    case let .entityWithStringEnumPropertyWithDefaultValueTemplate(value):
      return .entityWithStringEnumPropertyWithDefaultValueTemplate(try value.resolveParent(templates: templates))
    case let .entityWithoutPropertiesTemplate(value):
      return .entityWithoutPropertiesTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: EntityTemplate?, useOnlyLinks: Bool) -> DeserializationResult<Entity> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<Entity>!
      result = result ?? {
        if case let .entityWithArrayTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithArray(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArray(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithArrayOfEnumsTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithArrayOfEnums(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArrayOfEnums(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithArrayOfExpressionsTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithArrayOfExpressions(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArrayOfExpressions(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithArrayOfNestedItemsTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithArrayOfNestedItems(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArrayOfNestedItems(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithArrayWithTransformTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithArrayWithTransform(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArrayWithTransform(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithComplexPropertyTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithComplexProperty(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithComplexProperty(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithComplexPropertyWithDefaultValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithComplexPropertyWithDefaultValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithComplexPropertyWithDefaultValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithEntityPropertyTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithEntityProperty(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithEntityProperty(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithOptionalComplexPropertyTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithOptionalComplexProperty(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithOptionalComplexProperty(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithOptionalPropertyTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithOptionalProperty(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithOptionalProperty(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithOptionalStringEnumPropertyTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithOptionalStringEnumProperty(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithOptionalStringEnumProperty(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithPropertyWithDefaultValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithPropertyWithDefaultValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithPropertyWithDefaultValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithRawArrayTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithRawArray(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithRawArray(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithRequiredPropertyTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithRequiredProperty(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithRequiredProperty(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithSimplePropertiesTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithSimpleProperties(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithSimpleProperties(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithStringArrayPropertyTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithStringArrayProperty(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithStringArrayProperty(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithStringEnumPropertyTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithStringEnumProperty(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithStringEnumProperty(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithStringEnumPropertyWithDefaultValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithStringEnumPropertyWithDefaultValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithStringEnumPropertyWithDefaultValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .entityWithoutPropertiesTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.entityWithoutProperties(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithoutProperties(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<Entity> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<Entity>?
    result = result ?? { if type == EntityWithArray.type {
      let result = { EntityWithArrayTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithArray(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArray(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithArrayOfEnums.type {
      let result = { EntityWithArrayOfEnumsTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithArrayOfEnums(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArrayOfEnums(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithArrayOfExpressions.type {
      let result = { EntityWithArrayOfExpressionsTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithArrayOfExpressions(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArrayOfExpressions(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithArrayOfNestedItems.type {
      let result = { EntityWithArrayOfNestedItemsTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithArrayOfNestedItems(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArrayOfNestedItems(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithArrayWithTransform.type {
      let result = { EntityWithArrayWithTransformTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithArrayWithTransform(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithArrayWithTransform(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithComplexProperty.type {
      let result = { EntityWithComplexPropertyTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithComplexProperty(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithComplexProperty(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithComplexPropertyWithDefaultValue.type {
      let result = { EntityWithComplexPropertyWithDefaultValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithComplexPropertyWithDefaultValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithComplexPropertyWithDefaultValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithEntityProperty.type {
      let result = { EntityWithEntityPropertyTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithEntityProperty(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithEntityProperty(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithOptionalComplexProperty.type {
      let result = { EntityWithOptionalComplexPropertyTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithOptionalComplexProperty(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithOptionalComplexProperty(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithOptionalProperty.type {
      let result = { EntityWithOptionalPropertyTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithOptionalProperty(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithOptionalProperty(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithOptionalStringEnumProperty.type {
      let result = { EntityWithOptionalStringEnumPropertyTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithOptionalStringEnumProperty(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithOptionalStringEnumProperty(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithPropertyWithDefaultValue.type {
      let result = { EntityWithPropertyWithDefaultValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithPropertyWithDefaultValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithPropertyWithDefaultValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithRawArray.type {
      let result = { EntityWithRawArrayTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithRawArray(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithRawArray(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithRequiredProperty.type {
      let result = { EntityWithRequiredPropertyTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithRequiredProperty(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithRequiredProperty(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithSimpleProperties.type {
      let result = { EntityWithSimplePropertiesTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithSimpleProperties(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithSimpleProperties(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithStringArrayProperty.type {
      let result = { EntityWithStringArrayPropertyTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithStringArrayProperty(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithStringArrayProperty(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithStringEnumProperty.type {
      let result = { EntityWithStringEnumPropertyTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithStringEnumProperty(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithStringEnumProperty(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithStringEnumPropertyWithDefaultValue.type {
      let result = { EntityWithStringEnumPropertyWithDefaultValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithStringEnumPropertyWithDefaultValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithStringEnumPropertyWithDefaultValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == EntityWithoutProperties.type {
      let result = { EntityWithoutPropertiesTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.entityWithoutProperties(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.entityWithoutProperties(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension EntityTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case EntityWithArrayTemplate.type:
      self = .entityWithArrayTemplate(try EntityWithArrayTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithArrayOfEnumsTemplate.type:
      self = .entityWithArrayOfEnumsTemplate(try EntityWithArrayOfEnumsTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithArrayOfExpressionsTemplate.type:
      self = .entityWithArrayOfExpressionsTemplate(try EntityWithArrayOfExpressionsTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithArrayOfNestedItemsTemplate.type:
      self = .entityWithArrayOfNestedItemsTemplate(try EntityWithArrayOfNestedItemsTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithArrayWithTransformTemplate.type:
      self = .entityWithArrayWithTransformTemplate(try EntityWithArrayWithTransformTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithComplexPropertyTemplate.type:
      self = .entityWithComplexPropertyTemplate(try EntityWithComplexPropertyTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithComplexPropertyWithDefaultValueTemplate.type:
      self = .entityWithComplexPropertyWithDefaultValueTemplate(try EntityWithComplexPropertyWithDefaultValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithEntityPropertyTemplate.type:
      self = .entityWithEntityPropertyTemplate(try EntityWithEntityPropertyTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithOptionalComplexPropertyTemplate.type:
      self = .entityWithOptionalComplexPropertyTemplate(try EntityWithOptionalComplexPropertyTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithOptionalPropertyTemplate.type:
      self = .entityWithOptionalPropertyTemplate(try EntityWithOptionalPropertyTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithOptionalStringEnumPropertyTemplate.type:
      self = .entityWithOptionalStringEnumPropertyTemplate(try EntityWithOptionalStringEnumPropertyTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithPropertyWithDefaultValueTemplate.type:
      self = .entityWithPropertyWithDefaultValueTemplate(try EntityWithPropertyWithDefaultValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithRawArrayTemplate.type:
      self = .entityWithRawArrayTemplate(try EntityWithRawArrayTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithRequiredPropertyTemplate.type:
      self = .entityWithRequiredPropertyTemplate(try EntityWithRequiredPropertyTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithSimplePropertiesTemplate.type:
      self = .entityWithSimplePropertiesTemplate(try EntityWithSimplePropertiesTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithStringArrayPropertyTemplate.type:
      self = .entityWithStringArrayPropertyTemplate(try EntityWithStringArrayPropertyTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithStringEnumPropertyTemplate.type:
      self = .entityWithStringEnumPropertyTemplate(try EntityWithStringEnumPropertyTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithStringEnumPropertyWithDefaultValueTemplate.type:
      self = .entityWithStringEnumPropertyWithDefaultValueTemplate(try EntityWithStringEnumPropertyWithDefaultValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case EntityWithoutPropertiesTemplate.type:
      self = .entityWithoutPropertiesTemplate(try EntityWithoutPropertiesTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "entity_template", representation: dictionary)
    }
  }
}
