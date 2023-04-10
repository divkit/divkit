import Foundation

import CommonCorePublic
import Serialization

public struct DivTemplates: Deserializable {
  public let templates: [TemplateName: Any]
  public let templateToType: [TemplateName: String]

  public static let empty = DivTemplates(dictionary: [:])

  public init(
    templates: [TemplateName: Any],
    templatesToType: [TemplateName: String]
  ) {
    self.templates = templates
    self.templateToType = templatesToType
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
  }

  public func parseValue<T: TemplateValue>(
    type _: T.Type,
    from dict: [String: Any]
  ) -> DeserializationResult<T.ResolvedValue> {
    let context = TemplatesContext(
      templates: templates,
      templateToType: templateToType,
      templateData: dict
    )
    return T.resolveValue(context: context, parent: nil, useOnlyLinks: false)
  }

  public func resolve(
    newTemplates: [String: Any]
  ) -> DivTemplates {
    let alreadyResolvedTemplateTypes = Set(templates.keys)

    let templatesByType = mapTemplatesByType(
      templatesDictionary: newTemplates.filter { key, _ in
        !alreadyResolvedTemplateTypes.contains(key)
      },
      templateToType: templateToType
    )

    let untypedTemplatesByType = templatesByType.mapValues { $0.value }
      .merging(templates, uniquingKeysWith: { $1 })

    return DivTemplates(
      templates: resolveTemplates(
        templatesByType: templatesByType,
        untypedTemplatesByType: untypedTemplatesByType
      ).merging(templates, uniquingKeysWith: { $1 }),
      templatesToType: templateToType
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
