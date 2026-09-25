import Foundation
import Serialization
import VGSL

public struct DivTemplates: Deserializable, @unchecked Sendable {
  public static let empty = DivTemplates(dictionary: [:])

  public let templates: [TemplateName: Any]
  public let templateToType: [TemplateName: String]

  public init(dictionary: [String: Any]) {
    templateToType = calculateTemplateToType(in: dictionary)
    templates = resolveRawTemplates(
      dictionary,
      templateToType: templateToType,
      seededWith: [:]
    )
  }

  private init(
    resolvedTemplates: [TemplateName: Any],
    templateToType: [TemplateName: String]
  ) {
    templates = resolvedTemplates
    self.templateToType = templateToType
  }
}

extension DivTemplates {
  public func parseValue<T: ContextDeserializable>(
    type _: T.Type,
    from dict: [String: Any]
  ) -> DeserializationResult<T> {
    let resolver = templates.isEmpty ? nil : UntypedDivTemplateResolver(
      templates: templates,
      templateToType: templateToType,
      resolvedTemplates: templates
    )
    return parseUntyped(dict, resolver: resolver)
  }

  @available(*, deprecated, message: "Use parseValue(type: T.ResolvedValue.self, from:)")
  public func parseValue<T: TemplateValue>(
    type _: T.Type,
    from dict: [String: Any]
  ) -> DeserializationResult<T.ResolvedValue> {
    parseValue(type: T.ResolvedValue.self, from: dict)
  }

  public func resolve(
    newTemplates: [String: Any],
    shouldKeepExistingOnConflict: Bool = true
  ) -> DivTemplates {
    var newTemplates = newTemplates
    let alreadyResolvedTemplates = if shouldKeepExistingOnConflict {
      templates
    } else {
      templates.filter { newTemplates[$0.key] == nil }
    }

    for (alreadyResolvedTemplateType, resolvedTemplate) in alreadyResolvedTemplates {
      newTemplates[alreadyResolvedTemplateType] = resolvedTemplate
    }

    let newTemplateToType = calculateTemplateToType(in: newTemplates)

    return DivTemplates(
      resolvedTemplates: resolveRawTemplates(
        newTemplates,
        templateToType: newTemplateToType,
        seededWith: alreadyResolvedTemplates
      ),
      templateToType: newTemplateToType
    )
  }
}

func parseUntyped<T: ContextDeserializable>(
  _ dict: [String: Any],
  resolver: UntypedDivTemplateResolver?
) -> DeserializationResult<T> {
  let templateResolver: TemplateResolver? = if let resolver {
    { resolver.resolveFlat($0) }
  } else {
    nil
  }
  let context = ParsingContext(templateResolver: templateResolver)
  do {
    let value = try T(dictionary: resolver?.resolveFlat(dict) ?? dict, context: context)
    if let warnings = NonEmptyArray(context.errors + context.warnings) {
      return .partialSuccess(value, warnings: warnings)
    }
    return .success(value)
  } catch {
    let error = error as? DeserializationError ?? .generic
    var chain = error.chain[...]
    var errors = context.errors
    while let last = errors.last, let level = chain.first, last.description == level.description {
      errors.removeLast()
      chain = chain.dropFirst()
    }
    return .failure(NonEmptyArray(error) + errors + context.warnings)
  }
}

extension DeserializationError {
  fileprivate var chain: [DeserializationError] {
    switch self {
    case let .nestedObjectError(field, _) where Int(field) != nil: []
    case let .nestedObjectError(_, .composite(_, causes)): [self] + causes.flatMap(\.chain)
    case .nestedObjectError(_, .typeMismatch), .nestedObjectError(_, .invalidFieldRepresentation):
      [self]
    case let .nestedObjectError(_, error): [self] + error.chain
    default: [self]
    }
  }
}

private func resolveRawTemplates(
  _ rawTemplates: [String: Any],
  templateToType: [TemplateName: String],
  seededWith resolvedTemplates: [TemplateName: Any]
) -> [TemplateName: Any] {
  let resolver = UntypedDivTemplateResolver(
    templates: rawTemplates,
    templateToType: templateToType,
    resolvedTemplates: resolvedTemplates
  )
  for name in rawTemplates.keys {
    _ = resolver.resolveTemplate(named: name)
  }
  return resolver.resolvedTemplateCache
}
