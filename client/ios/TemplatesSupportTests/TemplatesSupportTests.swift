@testable import TemplatesSupport

import XCTest

import Serialization

final class TemplatesSupportTests: XCTestCase {}

func readEntity<T: TemplateDeserializable & TemplateValue>(
  _: T.Type,
  fileName: String
) throws -> T.ResolvedValue? {
  let url = Bundle(for: TemplatesSupportTests.self)
    .url(forResource: "template_test_data/\(fileName)", withExtension: "json")!
  let data = try Data(contentsOf: url)
  guard let dictionary = try JSONSerialization
    .jsonObject(with: data, options: []) as? [String: Any] else {
    throw DeserializationError.invalidJSONData(data: data)
  }

  let entityDict = try dictionary.getField("entity") as [String: Any]
  let templatesDict = (dictionary["templates"] as? [String: Any]) ?? [:]
  let templateToType = calculateTemplateToType(in: templatesDict)

  var templates: Templates = [:]
  try templatesDict.forEach { [templateToType] name, value in
    templates[name] = try EntityTemplate(
      dictionary: value as! [String: Any],
      templateToType: templateToType
    ).value
  }

  let context = Context(
    templates: templates,
    templateToType: templateToType,
    templateData: entityDict
  )
  return try T(dictionary: entityDict, templateToType: templateToType)
    .resolveParent(templates: templates)
    .resolveValue(context: context, useOnlyLinks: false)
    .value
}
