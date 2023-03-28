import Foundation

import CommonCorePublic

public protocol DivPatchProvider {
  func getPatch(
    url: URL,
    completion: @escaping DivPatchProviderCompletion
  )

  func cancelRequests()
}

public typealias DivPatchProviderCompletion = (Result<DivPatch, Error>) -> Void

public func parseDivPatch(_ data: Data) throws -> DivPatch {
  let dataJson = try JSONSerialization.jsonObject(with: data) as? JsonDictionary ?? [:]
  let templates = DivTemplates(
    dictionary: dataJson["templates"] as? JsonDictionary ?? [:]
  )
  return try templates.parseValue(
    type: DivPatchTemplate.self,
    from: dataJson["patch"] as? JsonDictionary ?? [:]
  ).unwrap()
}

private typealias JsonDictionary = [String: Any]
