import VGSL

enum UITestRequest: Codable {
  case divAction(JSONDictionary)
}

enum UITestResponse: Codable {
  case success
  case failure(String)
}
