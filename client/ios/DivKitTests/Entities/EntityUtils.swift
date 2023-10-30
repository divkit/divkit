@testable import DivKit

import Foundation

import Serialization

func readEntityWithResult(
  fileName: String
) throws -> DeserializationResult<Entity> {
  let url = Bundle(for: DivKitTests.self)
    .url(forResource: "template_test_data/\(fileName)", withExtension: "json")!
  let data = try Data(contentsOf: url)
  guard let dictionary = try JSONSerialization
    .jsonObject(with: data, options: []) as? [String: Any] else {
    throw DeserializationError.invalidJSONData(data: data)
  }

  let entityDict = try dictionary.getField("entity") as [String: Any]
  let templatesDict = (dictionary["templates"] as? [String: Any]) ?? [:]

  let templateToType = calculateTemplateToType(in: templatesDict)
  let templatesByType = try mapTemplatesByType(
    templatesDict: templatesDict,
    templateToType: templateToType
  )
  let templates = try resolveTemplates(
    templatesByType: templatesByType,
    untypedTemplatesByType: templatesByType.mapValues { $0.value }
  )

  let context = TemplatesContext(
    templates: templates,
    templateToType: templateToType,
    templateData: entityDict
  )
  return EntityTemplate
    .resolveValue(context: context, parent: nil, useOnlyLinks: false)
}

func readEntity<T: TemplateValue>(
  _: T.Type,
  fileName: String
) throws -> T.ResolvedValue? {
  try readEntityWithResult(fileName: fileName).value?.value as? T.ResolvedValue
}

private func mapTemplatesByType(
  templatesDict: [String: Any],
  templateToType: [TemplateName: String]
) throws -> [TemplateName: EntityTemplate] {
  try Dictionary(
    templatesDict.keys.compactMap { [templateToType] key in
      let template: EntityTemplate = try templatesDict
        .getField(key, templateToType: templateToType)
      return (key, template)
    },
    uniquingKeysWith: { $1 }
  )
}

private func resolveTemplates(
  templatesByType: [TemplateName: EntityTemplate],
  untypedTemplatesByType: [TemplateName: Any]
) throws -> [TemplateName: Any] {
  try templatesByType.compactMapValues {
    try $0.resolveParent(templates: untypedTemplatesByType).value
  }
}
