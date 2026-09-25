import Foundation

struct RunnerScenario {
  let description: String
  let platforms: [String]
  let steps: [RunnerStep]
  let relativePath: String
}

struct RunnerScenarioHeader: Decodable {
  let platforms: [String]
}

extension RunnerScenario: Decodable {
  private enum CodingKeys: String, CodingKey {
    case description
    case platforms
    case steps
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    description = try container.decode(String.self, forKey: .description)
    platforms = try container.decode([String].self, forKey: .platforms)
    steps = try container.decode([RunnerStep].self, forKey: .steps)
    relativePath = ""

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
      platforms: scenario.platforms,
      steps: scenario.steps,
      relativePath: relativePath
    )
  }
}
