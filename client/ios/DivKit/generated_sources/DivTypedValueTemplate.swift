// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivTypedValueTemplate: TemplateValue {
  case stringValueTemplate(StringValueTemplate)
  case integerValueTemplate(IntegerValueTemplate)
  case numberValueTemplate(NumberValueTemplate)
  case colorValueTemplate(ColorValueTemplate)
  case booleanValueTemplate(BooleanValueTemplate)
  case urlValueTemplate(UrlValueTemplate)
  case dictValueTemplate(DictValueTemplate)
  case arrayValueTemplate(ArrayValueTemplate)

  public var value: Any {
    switch self {
    case let .stringValueTemplate(value):
      return value
    case let .integerValueTemplate(value):
      return value
    case let .numberValueTemplate(value):
      return value
    case let .colorValueTemplate(value):
      return value
    case let .booleanValueTemplate(value):
      return value
    case let .urlValueTemplate(value):
      return value
    case let .dictValueTemplate(value):
      return value
    case let .arrayValueTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivTypedValueTemplate {
    switch self {
    case let .stringValueTemplate(value):
      return .stringValueTemplate(try value.resolveParent(templates: templates))
    case let .integerValueTemplate(value):
      return .integerValueTemplate(try value.resolveParent(templates: templates))
    case let .numberValueTemplate(value):
      return .numberValueTemplate(try value.resolveParent(templates: templates))
    case let .colorValueTemplate(value):
      return .colorValueTemplate(try value.resolveParent(templates: templates))
    case let .booleanValueTemplate(value):
      return .booleanValueTemplate(try value.resolveParent(templates: templates))
    case let .urlValueTemplate(value):
      return .urlValueTemplate(try value.resolveParent(templates: templates))
    case let .dictValueTemplate(value):
      return .dictValueTemplate(try value.resolveParent(templates: templates))
    case let .arrayValueTemplate(value):
      return .arrayValueTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivTypedValueTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivTypedValue> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivTypedValue>!
      result = result ?? {
        if case let .stringValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.stringValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.stringValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .integerValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.integerValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.integerValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .numberValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.numberValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.numberValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .colorValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.colorValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.colorValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .booleanValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.booleanValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.booleanValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .urlValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.urlValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.urlValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .dictValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.dictValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.dictValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .arrayValueTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.arrayValue(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.arrayValue(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivTypedValue> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivTypedValue>?
    result = result ?? { if type == StringValue.type {
      let result = { StringValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.stringValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.stringValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == IntegerValue.type {
      let result = { IntegerValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.integerValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.integerValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == NumberValue.type {
      let result = { NumberValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.numberValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.numberValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == ColorValue.type {
      let result = { ColorValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.colorValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.colorValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == BooleanValue.type {
      let result = { BooleanValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.booleanValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.booleanValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == UrlValue.type {
      let result = { UrlValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.urlValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.urlValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DictValue.type {
      let result = { DictValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.dictValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.dictValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == ArrayValue.type {
      let result = { ArrayValueTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.arrayValue(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.arrayValue(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivTypedValueTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case StringValueTemplate.type:
      self = .stringValueTemplate(try StringValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case IntegerValueTemplate.type:
      self = .integerValueTemplate(try IntegerValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case NumberValueTemplate.type:
      self = .numberValueTemplate(try NumberValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case ColorValueTemplate.type:
      self = .colorValueTemplate(try ColorValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case BooleanValueTemplate.type:
      self = .booleanValueTemplate(try BooleanValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case UrlValueTemplate.type:
      self = .urlValueTemplate(try UrlValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case DictValueTemplate.type:
      self = .dictValueTemplate(try DictValueTemplate(dictionary: dictionary, templateToType: templateToType))
    case ArrayValueTemplate.type:
      self = .arrayValueTemplate(try ArrayValueTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-typed-value_template", representation: dictionary)
    }
  }
}
