import Foundation

struct RunnerScenario {
  let description: String
  let caseID: Int
  let platforms: [String]
  let steps: [RunnerStep]
  let relativePath: String
}

struct RunnerScenarioHeader: Decodable {
  private enum CodingKeys: String, CodingKey {
    case caseID = "case_id"
    case platforms
  }

  let caseID: Int
  let platforms: [String]
}

extension RunnerScenario: Decodable {
  private enum CodingKeys: String, CodingKey {
    case caseID = "case_id"
    case description
    case platforms
    case steps
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    description = try container.decode(String.self, forKey: .description)
    caseID = try container.decode(Int.self, forKey: .caseID)
    platforms = try container.decode([String].self, forKey: .platforms)
    steps = try container.decode([RunnerStep].self, forKey: .steps)
    relativePath = ""

    guard caseID > 0 else {
      throw DecodingError.dataCorruptedError(
        forKey: .caseID,
        in: container,
        debugDescription: "case_id must be positive"
      )
    }
    guard !steps.isEmpty else {
      throw DecodingError.dataCorruptedError(
        forKey: .steps,
        in: container,
        debugDescription: "steps must not be empty"
      )
    }
  }

  init(decoded scenario: RunnerScenario, relativePath: String) {
    self.init(
      description: scenario.description,
      caseID: scenario.caseID,
      platforms: scenario.platforms,
      steps: scenario.steps,
      relativePath: relativePath
    )
  }
}
