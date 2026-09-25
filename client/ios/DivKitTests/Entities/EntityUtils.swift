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
  return DivTemplates(dictionary: templatesDict).parseValue(type: Entity.self, from: entityDict)
}

func readEntity<T>(
  _: T.Type,
  fileName: String
) throws -> T? {
  try readEntityWithResult(fileName: fileName).value?.value as? T
}

extension Entity: ContextDeserializable {}
