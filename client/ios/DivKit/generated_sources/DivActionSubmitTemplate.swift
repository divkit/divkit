// Generated code. Do not modify.

import Foundation
import Serialization
import VGSL

public final class DivActionSubmitTemplate: TemplateValue {
  public final class ParameterTemplate: TemplateValue {
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

    private static func resolveOnlyLinks(context: TemplatesContext, parent: ParameterTemplate?) -> DeserializationResult<DivActionSubmit.Parameter> {
      let nameValue = parent?.name?.resolveValue(context: context) ?? .noValue
      let valueValue = parent?.value?.resolveValue(context: context) ?? .noValue
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
      let result = DivActionSubmit.Parameter(
        name: nameNonNil,
        value: valueNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: ParameterTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSubmit.Parameter> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var nameValue: DeserializationResult<Expression<String>> = parent?.name?.value() ?? .noValue
      var valueValue: DeserializationResult<Expression<String>> = parent?.value?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "name":
          nameValue = deserialize(__dictValue).merged(with: nameValue)
        case "value":
          valueValue = deserialize(__dictValue).merged(with: valueValue)
        case parent?.name?.link:
          nameValue = nameValue.merged(with: { deserialize(__dictValue) })
        case parent?.value?.link:
          valueValue = valueValue.merged(with: { deserialize(__dictValue) })
        default: break
        }
      }
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
      let result = DivActionSubmit.Parameter(
        name: nameNonNil,
        value: valueNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    private func mergedWithParent(templates: [TemplateName: Any]) throws -> ParameterTemplate {
      return self
    }

    public func resolveParent(templates: [TemplateName: Any]) throws -> ParameterTemplate {
      return try mergedWithParent(templates: templates)
    }
  }

  public final class RequestTemplate: TemplateValue {
    public typealias Method = DivActionSubmit.Request.Method

    public let headers: Field<[ParameterTemplate]>?
    public let method: Field<Expression<Method>>? // default value: POST
    public let queryParameters: Field<[ParameterTemplate]>?
    public let url: Field<Expression<URL>>?

    public convenience init(dictionary: [String: Any], templateToType: [TemplateName: String]) throws {
      self.init(
        headers: dictionary.getOptionalArray("headers", templateToType: templateToType),
        method: dictionary.getOptionalExpressionField("method"),
        queryParameters: dictionary.getOptionalArray("query_parameters", templateToType: templateToType),
        url: dictionary.getOptionalExpressionField("url", transform: URL.init(string:))
      )
    }

    init(
      headers: Field<[ParameterTemplate]>? = nil,
      method: Field<Expression<Method>>? = nil,
      queryParameters: Field<[ParameterTemplate]>? = nil,
      url: Field<Expression<URL>>? = nil
    ) {
      self.headers = headers
      self.method = method
      self.queryParameters = queryParameters
      self.url = url
    }

    private static func resolveOnlyLinks(context: TemplatesContext, parent: RequestTemplate?) -> DeserializationResult<DivActionSubmit.Request> {
      let headersValue = parent?.headers?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let methodValue = parent?.method?.resolveOptionalValue(context: context) ?? .noValue
      let queryParametersValue = parent?.queryParameters?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
      let urlValue = parent?.url?.resolveValue(context: context, transform: URL.init(string:)) ?? .noValue
      var errors = mergeErrors(
        headersValue.errorsOrWarnings?.map { .nestedObjectError(field: "headers", error: $0) },
        methodValue.errorsOrWarnings?.map { .nestedObjectError(field: "method", error: $0) },
        queryParametersValue.errorsOrWarnings?.map { .nestedObjectError(field: "query_parameters", error: $0) },
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
        headers: headersValue.value,
        method: methodValue.value,
        queryParameters: queryParametersValue.value,
        url: urlNonNil
      )
      return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
    }

    public static func resolveValue(context: TemplatesContext, parent: RequestTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSubmit.Request> {
      if useOnlyLinks {
        return resolveOnlyLinks(context: context, parent: parent)
      }
      var headersValue: DeserializationResult<[DivActionSubmit.Parameter]> = .noValue
      var methodValue: DeserializationResult<Expression<DivActionSubmit.Request.Method>> = parent?.method?.value() ?? .noValue
      var queryParametersValue: DeserializationResult<[DivActionSubmit.Parameter]> = .noValue
      var urlValue: DeserializationResult<Expression<URL>> = parent?.url?.value() ?? .noValue
      context.templateData.forEach { key, __dictValue in
        switch key {
        case "headers":
          headersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.ParameterTemplate.self).merged(with: headersValue)
        case "method":
          methodValue = deserialize(__dictValue).merged(with: methodValue)
        case "query_parameters":
          queryParametersValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.ParameterTemplate.self).merged(with: queryParametersValue)
        case "url":
          urlValue = deserialize(__dictValue, transform: URL.init(string:)).merged(with: urlValue)
        case parent?.headers?.link:
          headersValue = headersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.ParameterTemplate.self) })
        case parent?.method?.link:
          methodValue = methodValue.merged(with: { deserialize(__dictValue) })
        case parent?.queryParameters?.link:
          queryParametersValue = queryParametersValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.ParameterTemplate.self) })
        case parent?.url?.link:
          urlValue = urlValue.merged(with: { deserialize(__dictValue, transform: URL.init(string:)) })
        default: break
        }
      }
      if let parent = parent {
        headersValue = headersValue.merged(with: { parent.headers?.resolveOptionalValue(context: context, useOnlyLinks: true) })
        queryParametersValue = queryParametersValue.merged(with: { parent.queryParameters?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      }
      var errors = mergeErrors(
        headersValue.errorsOrWarnings?.map { .nestedObjectError(field: "headers", error: $0) },
        methodValue.errorsOrWarnings?.map { .nestedObjectError(field: "method", error: $0) },
        queryParametersValue.errorsOrWarnings?.map { .nestedObjectError(field: "query_parameters", error: $0) },
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
        headers: headersValue.value,
        method: methodValue.value,
        queryParameters: queryParametersValue.value,
        url: urlNonNil
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
        queryParameters: merged.queryParameters?.tryResolveParent(templates: templates),
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
    let containerIdValue = parent?.containerId?.resolveValue(context: context) ?? .noValue
    let onFailActionsValue = parent?.onFailActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let onSuccessActionsValue = parent?.onSuccessActions?.resolveOptionalValue(context: context, useOnlyLinks: true) ?? .noValue
    let requestValue = parent?.request?.resolveValue(context: context, useOnlyLinks: true) ?? .noValue
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
      containerId: containerIdNonNil,
      onFailActions: onFailActionsValue.value,
      onSuccessActions: onSuccessActionsValue.value,
      request: requestNonNil
    )
    return errors.isEmpty ? .success(result) : .partialSuccess(result, warnings: NonEmptyArray(errors)!)
  }

  public static func resolveValue(context: TemplatesContext, parent: DivActionSubmitTemplate?, useOnlyLinks: Bool) -> DeserializationResult<DivActionSubmit> {
    if useOnlyLinks {
      return resolveOnlyLinks(context: context, parent: parent)
    }
    var containerIdValue: DeserializationResult<Expression<String>> = parent?.containerId?.value() ?? .noValue
    var onFailActionsValue: DeserializationResult<[DivAction]> = .noValue
    var onSuccessActionsValue: DeserializationResult<[DivAction]> = .noValue
    var requestValue: DeserializationResult<DivActionSubmit.Request> = .noValue
    context.templateData.forEach { key, __dictValue in
      switch key {
      case "container_id":
        containerIdValue = deserialize(__dictValue).merged(with: containerIdValue)
      case "on_fail_actions":
        onFailActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: onFailActionsValue)
      case "on_success_actions":
        onSuccessActionsValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self).merged(with: onSuccessActionsValue)
      case "request":
        requestValue = deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.RequestTemplate.self).merged(with: requestValue)
      case parent?.containerId?.link:
        containerIdValue = containerIdValue.merged(with: { deserialize(__dictValue) })
      case parent?.onFailActions?.link:
        onFailActionsValue = onFailActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.onSuccessActions?.link:
        onSuccessActionsValue = onSuccessActionsValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionTemplate.self) })
      case parent?.request?.link:
        requestValue = requestValue.merged(with: { deserialize(__dictValue, templates: context.templates, templateToType: context.templateToType, type: DivActionSubmitTemplate.RequestTemplate.self) })
      default: break
      }
    }
    if let parent = parent {
      onFailActionsValue = onFailActionsValue.merged(with: { parent.onFailActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      onSuccessActionsValue = onSuccessActionsValue.merged(with: { parent.onSuccessActions?.resolveOptionalValue(context: context, useOnlyLinks: true) })
      requestValue = requestValue.merged(with: { parent.request?.resolveValue(context: context, useOnlyLinks: true) })
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
      containerId: containerIdNonNil,
      onFailActions: onFailActionsValue.value,
      onSuccessActions: onSuccessActionsValue.value,
      request: requestNonNil
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
