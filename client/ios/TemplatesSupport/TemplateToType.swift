// Copyright 2018 Yandex LLC. All rights reserved.

import CommonCore
import Serialization

public typealias TemplateToType = [String: String]

public func calculateTemplateToType(in dict: [String: Any]) -> TemplateToType {
  var unresolved: TemplateToType = [:]
  for key in dict.keys {
    if let value = dict[key] as? [String: Any], let type = value["type"] as? String {
      unresolved[key] = type
    }
  }

  var result: TemplateToType = [:]
  for key in unresolved.keys {
    do {
      result[key] = try finalType(for: key, in: unresolved)
    } catch DivError.circularReference {
    } catch {
      assertionFailure()
    }
  }
  return result
}

private func finalType(
  for type: String,
  in dict: TemplateToType,
  analyzedTypes: Set<String> = []
) throws -> String {
  guard !analyzedTypes.contains(type) else {
    throw DivError.circularReference([type])
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
  } catch let DivError.circularReference(stack) {
    throw DivError.circularReference([type] + stack)
  }
}
