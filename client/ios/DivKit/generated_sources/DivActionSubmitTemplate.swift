// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSubmitTemplate: TemplateValue, Sendable {
  public final class RequestTemplate: TemplateValue, Sendable {
    public final class HeaderTemplate: TemplateValue, Sendable {
      public let name: Field<Expression<String>>?
      public let value: Field<Expression<String>>?

      public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
        self.init(
          name: dictionary.getOptionalExpressionField("name"),
          value: dictionary.getOptionalExpressionField("value")
        )
      }

      init(
        name: Field<Expression<String>>? = nil,
        value: Field<Expression<String>>? = nil
      ) {
        self.name = name
        self.value = value
      }

      private static func resolveOnlyLinks(context: TemplatesContext, parent: HeaderTemplate?) -> DeserializationResult<DivActionSubmit.Request.Header> {
        let nameValue = { parent?.name?.resolveValue(context: context) ?? .noValue }()
        let valueValue = { parent?.value?.resolveValue(context: context) ?? .noValue }()
        var errors = mergeErrors(
          nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
          valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
        )
        if case .noValue = nameValue {
          errors.append(.requiredFieldIsMissing(field: "name"))
        }
        if case .noValue = valueValue {
          errors.append(.requiredFieldIsMissing(field: "value"))
        }
        guard
          let nameNonNil = nameValue.value,
          let valueNonNil = valueValue.value
        else {
          return .failure(NonEmptyArray(errors)!)
        }
        let result = DivActionSubmit.Request.Header(
          name: { nameNonNil }(),
          value: { valueNonNil }()
        )
        return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
      }

      public static func resolveValue(context: TemplatesContext, parent: HeaderTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSubmit.Request.Header> {
        if useOnlyLinks {
          return resolveOnlyLinks(context: context, parent: parent)
        }
        var nameValue: DeserializationResult<Expression<String>> = { parent?.name?.value() ?? .noValue }()
        var valueValue: DeserializationResult<Expression<String>> = { parent?.value?.value() ?? .noValue }()
        _ = {
          // Each field is parsed in its own lambda to keep the stack size managable
          // Otherwise the compiler will allocate stack for each intermediate variable
          // upfront even when we don't actually visit a relevant branch
          for (key, __dictValue) in context.templateData {
            _ = {
              if key == "name" {
               nameValue = deserialize(__dictValue).merged(with: nameValue)
              }
            }()
            _ = {
              if key == "value" {
               valueValue = deserialize(__dictValue).merged(with: valueValue)
              }
            }()
            _ = {
             if key == parent?.name?.link {
               nameValue = nameValue.merged(with: { deserialize(__dictValue) })
              }
            }()
            _ = {
             if key == parent?.value?.link {
               valueValue = valueValue.merged(with: { deserialize(__dictValue) })
              }
            }()
          }
        }()
        var errors = mergeErrors(
          nameValue.errorsOrWarnings?.map { .nestedObjectError(field: "name", error: $0) },
          valueValue.errorsOrWarnings?.map { .nestedObjectError(field: "value", error: $0) }
        )
        if case .noValue = nameValue {
          errors.append(.requiredFieldIsMissing(field: "name"))
        }
        if case .noValue = valueValue {
          errors.append(.requiredFieldIsMissing(field: "value"))
        }
        guard
          let nameNonNil = nameValue.value,
          let valueNonNil = valueValue.value
        else {
          return .failure(NonEmptyArray(errors)!)
        }
        let result = DivActionSubmit.Request.Header(
          name: { nameNonNil }(),
          value: { valueNonNil }()
        )
        return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
      }

      private func mergedWithParent(templates: [TemplateName: Any]) throws -> HeaderTemplate {
        return self
      }

      public func resolveParent(templates: [TemplateName: Any]) throws -> HeaderTemplate {
        return try mergedWithParent(templates: templates)
      }
    }

    public typealias Method = DivActionSubmit.Request.Method

    public let headers: Field<[HeaderTemplate]>?
    public let method: Field<Expression<Method>>? // default value: post
    public let url: Field<Expression<URL>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        headers: dictionary.getOptionalArray("headers", templateToType: templateToType),
        method: dictionary.getOptionalExpressionField("method"),
        url: dictionary.getOptionalExpressionField("url", transform: URL.init(stringToEncode:))
      )
    }

    init(
      headers: Field<[HeaderTemplate]>? = nil,
      method: Field<Expression<Method>>? = nil,
      url: Field<Expression<URL>>? = nil
    ) {
      self.headers = headers
      self.method = method
      self.url = url
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: RequestTemplate?) -> DeserializationResult<DivActionSubmit.Request> {
      let headersValue = { parent?.headers?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
      let methodValue = { parent?.method?.resolveOptionalValue(context: context) ?? .noValue }()
      let urlValue = { parent?.url?.resolveValue(context: context, transform: URL.init(stringToEncode:)) ?? .noValue }()
      var errors = mergeErrors(
        headersValue.errorsOrWarnings?.map { .nestedObjectError(field: "headers", error: $0) },
        methodValue.errorsOrWarnings?.map { .nestedObjectError(field: "method", error: $0) },
        urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
      )
      if case .noValue = urlValue {
        errors.append(.requiredFieldIsMissing(field: "url"))
      }
      guard
        let urlNonNil = urlValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivActionSubmit.Request(
        headers: { headersValue.value }(),
        method: { methodValue.value }(),
        url: { urlNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: RequestTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSubmit.Request> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var headersValue: DeserializationResult<[DivActionSubmit.Request.Header]> = .noValue
      var methodValue: DeserializationResult<Expression<DivActionSubmit.Request.Method>> = { parent?.method?.value() ?? .noValue }()
      var urlValue: DeserializationResult<Expression<URL>> = { parent?.url?.value() ?? .noValue }()
      _ = {
        // Each field is parsed in its own lambda to keep the stack size managable
        // Otherwise the compiler will allocate stack for each intermediate variable
        // upfront even when we don't actually visit a relevant branch
        for (key, __dictValue) in context.templateData {
          _ = {
            if key == "headers" {
             headersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.RequestTemplate.HeaderTemplate.self).merged(with: headersValue)
            }
          }()
          _ = {
            if key == "method" {
             methodValue = deserialize(__dictValue).merged(with: methodValue)
            }
          }()
          _ = {
            if key == "url" {
             urlValue = deserialize(__dictValue, transform: URL.init(stringToEncode:)).merged(with: urlValue)
            }
          }()
          _ = {
           if key == parent?.headers?.link {
             headersValue = headersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.RequestTemplate.HeaderTemplate.self) })
            }
          }()
          _ = {
           if key == parent?.method?.link {
             methodValue = methodValue.merged(with: { deserialize(__dictValue) })
            }
          }()
          _ = {
           if key == parent?.url?.link {
             urlValue = urlValue.merged(with: { deserialize(__dictValue, transform: URL.init(stringToEncode:)) })
            }
          }()
        }
      }()
      if let parent = parent {
        _ = { headersValue = headersValue.merged(with: { parent.headers?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      }
      var errors = mergeErrors(
        headersValue.errorsOrWarnings?.map { .nestedObjectError(field: "headers", error: $0) },
        methodValue.errorsOrWarnings?.map { .nestedObjectError(field: "method", error: $0) },
        urlValue.errorsOrWarnings?.map { .nestedObjectError(field: "url", error: $0) }
      )
      if case .noValue = urlValue {
        errors.append(.requiredFieldIsMissing(field: "url"))
      }
      guard
        let urlNonNil = urlValue.value
      else {
        return .failure(NonEmptyArray(errors)!)
      }
      let result = DivActionSubmit.Request(
        headers: { headersValue.value }(),
        method: { methodValue.value }(),
        url: { urlNonNil }()
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> RequestTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> RequestTemplate {
      let merged = try mergedWithParent(templates: templates)

      return RequestTemplate(
        headers: merged.headers?.tryResolveParent(templates: templates),
        method: merged.method,
        url: merged.url
      )
    }
  }

  public static let type: String = "submit"
  public let parent: String?
  public let containerId: Field<Expression<String>>?
  public let onFailActions: Field<[DivActionTemplate]>?
  public let onSuccessActions: Field<[DivActionTemplate]>?
  public let request: Field<RequestTemplate>?

  public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
    self.init(
      parent: dictionary["type"] as? String,
      containerId: dictionary.getOptionalExpressionField("container_id"),
      onFailActions: dictionary.getOptionalArray("on_fail_actions", templateToType: templateToType),
      onSuccessActions: dictionary.getOptionalArray("on_success_actions", templateToType: templateToType),
      request: dictionary.getOptionalField("request", templateToType: templateToType)
    )
  }

  init(
    parent: String?,
    containerId: Field<Expression<String>>? = nil,
    onFailActions: Field<[DivActionTemplate]>? = nil,
    onSuccessActions: Field<[DivActionTemplate]>? = nil,
    request: Field<RequestTemplate>? = nil
  ) {
    self.parent = parent
    self.containerId = containerId
    self.onFailActions = onFailActions
    self.onSuccessActions = onSuccessActions
    self.request = request
  }

  private static func resolveOnlyLinks(context: TemplatesContext, parent: DivActionSubmitTemplate?) -> DeserializationResult<DivActionSubmit> {
    let containerIdValue = { parent?.containerId?.resolveValue(context: context) ?? .noValue }()
    let onFailActionsValue = { parent?.onFailActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let onSuccessActionsValue = { parent?.onSuccessActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue }()
    let requestValue = { parent?.request?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue }()
    var errors = mergeErrors(
      containerIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "container_id", error: $0) },
      onFailActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_fail_actions", error: $0) },
      onSuccessActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_success_actions", error: $0) },
      requestValue.errorsOrWarnings?.map { .nestedObjectError(field: "request", error: $0) }
    )
    if case .noValue = containerIdValue {
      errors.append(.requiredFieldIsMissing(field: "container_id"))
    }
    if case .noValue = requestValue {
      errors.append(.requiredFieldIsMissing(field: "request"))
    }
    guard
      let containerIdNonNil = containerIdValue.value,
      let requestNonNil = requestValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSubmit(
      containerId: { containerIdNonNil }(),
      onFailActions: { onFailActionsValue.value }(),
      onSuccessActions: { onSuccessActionsValue.value }(),
      request: { requestNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionSubmitTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSubmit> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var containerIdValue: DeserializationResult<Expression<String>> = { parent?.containerId?.value() ?? .noValue }()
    var onFailActionsValue: DeserializationResult<[DivAction]> = .noValue
    var onSuccessActionsValue: DeserializationResult<[DivAction]> = .noValue
    var requestValue: DeserializationResult<DivActionSubmit.Request> = .noValue
    _ = {
      // Each field is parsed in its own lambda to keep the stack size managable
      // Otherwise the compiler will allocate stack for each intermediate variable
      // upfront even when we don't actually visit a relevant branch
      for (key, __dictValue) in context.templateData {
        _ = {
          if key == "container_id" {
           containerIdValue = deserialize(__dictValue).merged(with: containerIdValue)
          }
        }()
        _ = {
          if key == "on_fail_actions" {
           onFailActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: onFailActionsValue)
          }
        }()
        _ = {
          if key == "on_success_actions" {
           onSuccessActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: onSuccessActionsValue)
          }
        }()
        _ = {
          if key == "request" {
           requestValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.RequestTemplate.self).merged(with: requestValue)
          }
        }()
        _ = {
         if key == parent?.containerId?.link {
           containerIdValue = containerIdValue.merged(with: { deserialize(__dictValue) })
          }
        }()
        _ = {
         if key == parent?.onFailActions?.link {
           onFailActionsValue = onFailActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.onSuccessActions?.link {
           onSuccessActionsValue = onSuccessActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
          }
        }()
        _ = {
         if key == parent?.request?.link {
           requestValue = requestValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.RequestTemplate.self) })
          }
        }()
      }
    }()
    if let parent = parent {
      _ = { onFailActionsValue = onFailActionsValue.merged(with: { parent.onFailActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { onSuccessActionsValue = onSuccessActionsValue.merged(with: { parent.onSuccessActions?.resolveOptionalValue(context: context, useOnlyLinks: true) }) }()
      _ = { requestValue = requestValue.merged(with: { parent.request?.resolveValue(context: context, useOnlyLinks: true) }) }()
    }
    var errors = mergeErrors(
      containerIdValue.errorsOrWarnings?.map { .nestedObjectError(field: "container_id", error: $0) },
      onFailActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_fail_actions", error: $0) },
      onSuccessActionsValue.errorsOrWarnings?.map { .nestedObjectError(field: "on_success_actions", error: $0) },
      requestValue.errorsOrWarnings?.map { .nestedObjectError(field: "request", error: $0) }
    )
    if case .noValue = containerIdValue {
      errors.append(.requiredFieldIsMissing(field: "container_id"))
    }
    if case .noValue = requestValue {
      errors.append(.requiredFieldIsMissing(field: "request"))
    }
    guard
      let containerIdNonNil = containerIdValue.value,
      let requestNonNil = requestValue.value
    else {
      return .failure(NonEmptyArray(errors)!)
    }
    let result = DivActionSubmit(
      containerId: { containerIdNonNil }(),
      onFailActions: { onFailActionsValue.value }(),
      onSuccessActions: { onSuccessActionsValue.value }(),
      request: { requestNonNil }()
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  private func mergedWithParent(templates: [TemplateName: Any]) throws -> DivActionSubmitTemplate {
    guard let parent = parent, parent != Self.type else { return self }
    guard let parentTemplate = templates[parent] as? DivActionSubmitTemplate else {
      throw DeserializationError.unknownType(type: parent)
    }
    let mergedParent = try parentTemplate.mergedWithParent(templates: templates)

    return DivActionSubmitTemplate(
      parent: nil,
      containerId: containerId ?? mergedParent.containerId,
      onFailActions: onFailActions ?? mergedParent.onFailActions,
      onSuccessActions: onSuccessActions ?? mergedParent.onSuccessActions,
      request: request ?? mergedParent.request
    )
  }

  public func resolveParent(templates: [TemplateName: Any]) throws -> DivActionSubmitTemplate {
    let merged = try mergedWithParent(templates: templates)

    return DivActionSubmitTemplate(
      parent: nil,
      containerId: merged.containerId,
      onFailActions: merged.onFailActions?.tryResolveParent(templates: templates),
      onSuccessActions: merged.onSuccessActions?.tryResolveParent(templates: templates),
      request: try merged.request?.resolveParent(templates: templates)
    )
  }
}
