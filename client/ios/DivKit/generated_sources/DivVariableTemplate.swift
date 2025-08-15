// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

@frozen
public enum DivVariableTemplate: TemplateValue, Sendable {
  case stringVariableTemplate(StringVariableTemplate)
  case numberVariableTemplate(NumberVariableTemplate)
  case integerVariableTemplate(IntegerVariableTemplate)
  case booleanVariableTemplate(BooleanVariableTemplate)
  case colorVariableTemplate(ColorVariableTemplate)
  case urlVariableTemplate(UrlVariableTemplate)
  case dictVariableTemplate(DictVariableTemplate)
  case arrayVariableTemplate(ArrayVariableTemplate)

  public var value: Any {
    switch self {
    case let .stringVariableTemplate(value):
      return value
    case let .numberVariableTemplate(value):
      return value
    case let .integerVariableTemplate(value):
      return value
    case let .booleanVariableTemplate(value):
      return value
    case let .colorVariableTemplate(value):
      return value
    case let .urlVariableTemplate(value):
      return value
    case let .dictVariableTemplate(value):
      return value
    case let .arrayVariableTemplate(value):
      return value
    }
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivVariableTemplate {
    switch self {
    case let .stringVariableTemplate(value):
      return .stringVariableTemplate(try value.resolveParent(templates: templates))
    case let .numberVariableTemplate(value):
      return .numberVariableTemplate(try value.resolveParent(templates: templates))
    case let .integerVariableTemplate(value):
      return .integerVariableTemplate(try value.resolveParent(templates: templates))
    case let .booleanVariableTemplate(value):
      return .booleanVariableTemplate(try value.resolveParent(templates: templates))
    case let .colorVariableTemplate(value):
      return .colorVariableTemplate(try value.resolveParent(templates: templates))
    case let .urlVariableTemplate(value):
      return .urlVariableTemplate(try value.resolveParent(templates: templates))
    case let .dictVariableTemplate(value):
      return .dictVariableTemplate(try value.resolveParent(templates: templates))
    case let .arrayVariableTemplate(value):
      return .arrayVariableTemplate(try value.resolveParent(templates: templates))
    }
  }

  public static func resolveValue(context: TemplatesContext, parent: DivVariableTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivVariable> {
    guard let parent = parent else {
      if useOnlyLinks {
        return .failure(NonEmptyArray(.missingType(representation: context.templateData)))
      } else {
        return resolveUnknownValue(context: context, useOnlyLinks: useOnlyLinks)
      }
    }

    return {
      var result: DeserializationResult<DivVariable>!
      result = result ?? {
        if case let .stringVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.stringVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.stringVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .numberVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.numberVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.numberVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .integerVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.integerVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.integerVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .booleanVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.booleanVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.booleanVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .colorVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.colorVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.colorVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .urlVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.urlVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.urlVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .dictVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.dictVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.dictVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      result = result ?? {
        if case let .arrayVariableTemplate(value) = parent {
          let result = value.resolveValue(context: context, useOnlyLinks: useOnlyLinks)
          switch result {
            case let .success(value): return .success(.arrayVariable(value))
            case let .partialSuccess(value, warnings): return .partialSuccess(.arrayVariable(value), warnings: warnings)
            case let .failure(errors): return .failure(errors)
            case .noValue: return .noValue
          }
        } else { return nil }
      }()
      return result
    }()
  }

  private static func resolveUnknownValue(context: TemplatesContext, useOnlyLinks: Bool) -> DeserializationResult<DivVariable> {
    guard let type = (context.templateData["type"] as? String).flatMap({ context.templateToType[$0] ?? $0 }) else {
      return .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }

    return {
      var result: DeserializationResult<DivVariable>?
    result = result ?? { if type == StringVariable.type {
      let result = { StringVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.stringVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.stringVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == NumberVariable.type {
      let result = { NumberVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.numberVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.numberVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == IntegerVariable.type {
      let result = { IntegerVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.integerVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.integerVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == BooleanVariable.type {
      let result = { BooleanVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.booleanVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.booleanVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == ColorVariable.type {
      let result = { ColorVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.colorVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.colorVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == UrlVariable.type {
      let result = { UrlVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.urlVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.urlVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == DictVariable.type {
      let result = { DictVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.dictVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.dictVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    result = result ?? { if type == ArrayVariable.type {
      let result = { ArrayVariableTemplate.resolveValue(context: context, useOnlyLinks: useOnlyLinks) }()
      switch result {
      case let .success(value): return .success(.arrayVariable(value))
      case let .partialSuccess(value, warnings): return .partialSuccess(.arrayVariable(value), warnings: warnings)
      case let .failure(errors): return .failure(errors)
      case .noValue: return .noValue
      }
    } else { return nil } }()
    return result ?? .failure(NonEmptyArray(.requiredFieldIsMissing(field: "type")))
    }()
  }
}

extension DivVariableTemplate {
  public init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    let receivedType = try dictionary.getField("type") as String
    let blockType = templateToType[receivedType] ?? receivedType
    switch blockType {
    case StringVariableTemplate.type:
      self = .stringVariableTemplate(try StringVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case NumberVariableTemplate.type:
      self = .numberVariableTemplate(try NumberVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case IntegerVariableTemplate.type:
      self = .integerVariableTemplate(try IntegerVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case BooleanVariableTemplate.type:
      self = .booleanVariableTemplate(try BooleanVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case ColorVariableTemplate.type:
      self = .colorVariableTemplate(try ColorVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case UrlVariableTemplate.type:
      self = .urlVariableTemplate(try UrlVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case DictVariableTemplate.type:
      self = .dictVariableTemplate(try DictVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    case ArrayVariableTemplate.type:
      self = .arrayVariableTemplate(try ArrayVariableTemplate(dictionary: dictionary, templateToType: templateToType))
    default:
      throw DeserializationError.invalidFieldRepresentation(field: "div-variable_template", representation: dictionary)
    }
  }
}
