import Foundation
import Serialization
import VGSL

public struct DivTemplates: Deserializable, @unchecked Sendable {
  public static let empty = DivTemplates(dictionary: [:])

  public let templates: [TemplateName: Any]
  public let templateToType: [TemplateName: String]

  /// In raw mode `templates` holds flattened template dictionaries instead of
  /// parsed typed templates. The marker is stored explicitly: an empty
  /// dictionary cannot distinguish the two modes.
  let isRawMode: Bool

  /// Per-card resolver seeded with the flattened `templates`. `nil` in typed
  /// mode and for raw-mode containers with no templates.
  private var rawTemplateResolver: UntypedDivTemplateResolver? {
    guard isRawMode, !templates.isEmpty else {
      return nil
    }
    return UntypedDivTemplateResolver(
      templates: templates,
      templateToType: templateToType,
      resolvedTemplates: templates
    )
  }

  public init(
    templates: [TemplateName: Any],
    templatesToType: [TemplateName: String]
  ) {
    self.templates = templates
    self.templateToType = templatesToType
    isRawMode = false
  }

  public init(dictionary: [String: Any]) {
    templateToType = calculateTemplateToType(in: dictionary)

    let templatesByType = mapTemplatesByType(
      templatesDictionary: dictionary,
      templateToType: templateToType
    )

    let untypedTemplatesByType = templatesByType.mapValues { $0.value }

    templates = resolveTemplates(
      templatesByType: templatesByType,
      untypedTemplatesByType: untypedTemplatesByType
    )
    isRawMode = false
  }

  public init(dictionary: [String: Any], flagsInfo: DivFlagsInfo) {
    guard flagsInfo.useUntypedTemplateResolver else {
      self = DivTemplates(dictionary: dictionary)
      return
    }

    templateToType = calculateTemplateToType(in: dictionary)

    templates = resolveRawTemplates(
      dictionary,
      templateToType: templateToType,
      seededWith: [:]
    )
    isRawMode = true
  }

  private init(
    resolvedTemplates: [TemplateName: Any],
    templateToType: [TemplateName: String]
  ) {
    templates = resolvedTemplates
    self.templateToType = templateToType
    isRawMode = true
  }
}

extension DivTemplates {
  public init(
    templatesToResolve: [String: Any],
    allTemplates: [String: Any]
  ) {
    templateToType = calculateTemplateToType(in: allTemplates)

    let templatesByType = mapTemplatesByType(
      templatesDictionary: templatesToResolve,
      templateToType: templateToType
    )

    let untypedTemplatesByType = templatesByType.mapValues { $0.value }

    templates = resolveTemplates(
      templatesByType: templatesByType,
      untypedTemplatesByType: untypedTemplatesByType
    )
    isRawMode = false
  }

  public func parseValue<T: TemplateValue>(
    type _: T.Type,
    from dict: [String: Any]
  ) -> DeserializationResult<T.ResolvedValue> {
    if isRawMode {
      if T.ResolvedValue.self == DivData.self {
        let resolver = rawTemplateResolver
        let untypedResult = DivData.resolveUntyped(card: dict, resolver: resolver)
        if let result = untypedResult as? DeserializationResult<T.ResolvedValue> {
          return result
        }
      }
      // Non-card values fall back to typed parsing of the flattened templates.
      return DivTemplates(dictionary: templates).parseValue(type: T.self, from: dict)
    }

    let context = TemplatesContext(
      templates: templates,
      templateToType: templateToType,
      templateData: dict
    )
    return T.resolveValue(context: context, parent: nil, useOnlyLinks: false)
  }

  public func resolve(
    newTemplates: [String: Any],
    shouldKeepExistingOnConflict: Bool = true
  ) -> DivTemplates {
    if isRawMode {
      return resolveRaw(
        newTemplates: newTemplates,
        shouldKeepExistingOnConflict: shouldKeepExistingOnConflict
      )
    }

    var newTemplates = newTemplates
    let alreadyResolvedTemplateTypes = if shouldKeepExistingOnConflict {
      Set(templates.keys)
    } else {
      Set(templates.keys).subtracting(Set(newTemplates.keys))
    }

    for alreadyResolvedTemplateType in alreadyResolvedTemplateTypes {
      newTemplates[alreadyResolvedTemplateType] = [
        "type": self.templateToType[alreadyResolvedTemplateType] ?? "",
      ]
    }

    let newTemplateToType = calculateTemplateToType(in: newTemplates)

    let templatesByType = mapTemplatesByType(
      templatesDictionary: newTemplates.filter { key, _ in
        !alreadyResolvedTemplateTypes.contains(key)
      },
      templateToType: newTemplateToType
    )

    let untypedTemplatesByType = templatesByType.mapValues { $0.value }
      .merging(
        templates,
        uniquingKeysWith: { shouldKeepExistingOnConflict ? $1 : $0 }
      )

    return DivTemplates(
      templates: resolveTemplates(
        templatesByType: templatesByType,
        untypedTemplatesByType: untypedTemplatesByType
      ).merging(
        templates,
        uniquingKeysWith: { shouldKeepExistingOnConflict ? $1 : $0 }
      ),
      templatesToType: newTemplateToType
    )
  }

  private func resolveRaw(
    newTemplates: [String: Any],
    shouldKeepExistingOnConflict: Bool
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

private func mapTemplatesByType(
  templatesDictionary: [String: Any],
  templateToType: [TemplateName: String]
) -> [TemplateName: DivTemplate] {
  Dictionary(
    templatesDictionary.keys.compactMap { [templateToType] key in
      guard let divTemplate: DivTemplate = try? templatesDictionary.getField(
        key,
        templateToType: templateToType
      ) else { return nil }
      return (key, divTemplate)
    },
    uniquingKeysWith: Combine.lastWithAssertionFailure
  )
}

private func resolveTemplates(
  templatesByType: [TemplateName: DivTemplate],
  untypedTemplatesByType: [TemplateName: Any]
) -> [TemplateName: Any] {
  templatesByType.compactMapValues {
    try? $0.resolveParent(templates: untypedTemplatesByType).value
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
