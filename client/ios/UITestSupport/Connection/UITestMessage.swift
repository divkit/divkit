import VGSL

enum UITestRequest: Codable {
  case divAction(JSONDictionary)
  case logs
}

enum UITestResponse: Codable, Sendable {
  enum SuccessResponse: Codable, Sendable {
    case divAction
    case logs(UITestLogSnapshot)
  }

  case success(SuccessResponse)
  case failure(String)
}

struct UITestLogSnapshot: Codable, Sendable, Equatable {
  let events: [UITestLogEvent]
  let durations: [UITestLogDuration]
}

enum UITestLogSource: String, Codable, Sendable {
  case visibility
  case disappear

  var hasDuration: Bool {
    switch self {
    case .visibility, .disappear:
      true
    }
  }
}

struct UITestLogEvent: Codable, Sendable, Equatable {
  let source: UITestLogSource
  let id: String
}

struct UITestLogDuration: Codable, Sendable, Equatable {
  let source: UITestLogSource
  let id: String
  let durationMilliseconds: Int
}
