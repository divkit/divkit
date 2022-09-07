import Foundation

import CommonCore
import Serialization
import TemplatesSupport

public struct DivTemplates: Deserializable {
  public let templates: Templates
  public let templateToType: TemplateToType

  public static let empty = DivTemplates(dictionary: [:])

  public init(templates: Templates, templatesToType: TemplateToType) {
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

  @inlinable
  public func parseValue<T: TemplateDeserializable & TemplateValue>(
    type _: T.Type,
    from dict: [String: Any]
  ) -> DeserializationResult<T.ResolvedValue> {
    let context = Context(
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
  templateToType: TemplateToType
) -> [Link: DivTemplate] {
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
  templatesByType: [Link: DivTemplate],
  untypedTemplatesByType: [Link: Any]
) -> [Link: Any] {
  templatesByType.compactMapValues {
    try? $0.resolveParent(templates: untypedTemplatesByType).value
  }
}
