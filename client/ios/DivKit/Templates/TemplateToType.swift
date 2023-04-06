import CommonCorePublic
import Serialization

func calculateTemplateToType(in dict: [String: Any]) -> [TemplateName: String] {
  var unresolved: [TemplateName: String] = [:]
  for key in dict.keys {
    if let value = dict[key] as? [String: Any], let type = value["type"] as? String {
      unresolved[key] = type
    }
  }

  var result: [TemplateName: String] = [:]
  for key in unresolved.keys {
    do {
      result[key] = try finalType(for: key, in: unresolved)
    } catch TemplateError.circularReference {
    } catch {
      assertionFailure()
    }
  }
  return result
}

private func finalType(
  for type: String,
  in dict: [TemplateName: String],
  analyzedTypes: Set<String> = []
) throws -> String {
  guard !analyzedTypes.contains(type) else {
    throw TemplateError.circularReference([type])
  }

  guard let next = dict[type] else {
    return type
  }

  do {
    return try finalType(
      for: next,
      in: dict,
      analyzedTypes: modified(analyzedTypes) { $0.insert(type) }
    )
  } catch let TemplateError.circularReference(stack) {
    throw TemplateError.circularReference([type] + stack)
  }
}
