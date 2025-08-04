import SwiftUI

struct RegressionTestsModel: Decodable {
  private enum CodingKeys: String, CodingKey {
    case tests
  }

  private struct SafeDecodable<T: Decodable>: Decodable {
    let value: T?

    init(from decoder: Decoder) throws {
      do {
        value = try T(from: decoder)
      } catch {
        AppLogger.error(error.localizedDescription)
        value = nil
      }
    }
  }

  let tests: [RegressionTestModel]

  init(from decoder: Decoder) throws {
    tests = try decoder
      .container(keyedBy: CodingKeys.self)
      .decode([SafeDecodable<RegressionTestModel>].self, forKey: .tests)
      .compactMap(\.value)
  }

}

struct RegressionTestModel: Decodable, Hashable {
  private enum CodingKeys: String, CodingKey {
    case expectedResults = "expected_results"
    case file
    case platforms
    case steps
    case tags
    case title
  }

  private struct DecodingError: LocalizedError {
    let description: String

    var errorDescription: String? {
      NSLocalizedString(description, comment: "DecodingError")
    }

    init(_ description: String) {
      self.description = description
    }

    init(key: CodingKeys, error: Error) {
      description = "Failed to read \(key.rawValue): \(error.localizedDescription)"
    }

    init(key: CodingKeys, title: String, error: Error) {
      description =
        "Failed to read \(key.rawValue) in test '\(title)': \(error.localizedDescription)"
    }

  }

  let expectedResults: [String]
  let platforms: [Platform]
  let steps: [String]
  let tags: [String]
  let title: String
  let url: URL

  var description: String {
    var description = ""
    if !steps.isEmpty {
      description += "Steps:"
      for step in steps {
        description += "\n • \(step)"
      }
    }

    if !expectedResults.isEmpty {
      if !steps.isEmpty {
        description += "\n\n"
      }
      description += "Expected results:"
      for result in expectedResults {
        description += "\n • \(result)"
      }
    }

    return description
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)

    do {
      title = try container.decode(String.self, forKey: .title)
    } catch {
      throw DecodingError(key: .title, error: error)
    }

    do {
      expectedResults = try container.decodeIfPresent([String].self, forKey: .expectedResults) ?? []
    } catch {
      throw DecodingError(key: .expectedResults, title: title, error: error)
    }

    do {
      steps = try container.decodeIfPresent([String].self, forKey: .steps) ?? []
    } catch {
      throw DecodingError(key: .steps, title: title, error: error)
    }

    let file: String
    do {
      file = try container.decode(String.self, forKey: .file)
    } catch {
      throw DecodingError(key: .file, title: title, error: error)
    }
    if let url = RegressionFile.makeUrl(file) {
      self.url = url
    } else {
      throw DecodingError("File not found: \(file)")
    }

    platforms = (try? container.decodeIfPresent([Platform].self, forKey: .platforms)) ?? [.ios]
    tags = (try? container.decodeIfPresent([String].self, forKey: .tags)) ?? []
  }

}

enum Platform: String, Decodable {
  case android
  case ios
  case web
}
