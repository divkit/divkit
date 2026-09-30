import Foundation

struct RunnerScenario {
  let description: String
  let platforms: [String]
  let steps: [RunnerStep]
  let relativePath: String

  init(data: Data, relativePath: String) throws {
    let scenario = try JSONDecoder().decode(Scenario.self, from: data)
    description = scenario.description
    platforms = scenario.platforms
    steps = scenario.steps
    self.relativePath = relativePath
  }
}

struct RunnerScenarioHeader: Decodable {
  let platforms: [String]
}

private struct Scenario: Decodable {
  private enum CodingKeys: String, CodingKey {
    case description
    case platforms
    case steps
  }

  let description: String
  let platforms: [String]
  let steps: [RunnerStep]

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    description = try container.decode(String.self, forKey: .description)
    platforms = try container.decode([String].self, forKey: .platforms)
    steps = try container.decode([RunnerStep].self, forKey: .steps)

    guard !steps.isEmpty else {
      throw DecodingError.dataCorruptedError(
        forKey: .steps,
        in: container,
        debugDescription: "steps must not be empty"
      )
    }
  }
}
