import Foundation
import SwiftUI

struct RegressionTestsModel: Decodable {
  let tests: [RegressionTestModel]
  
  init(from decoder: Decoder) throws {
    tests = try decoder
      .container(keyedBy: CodingKeys.self)
      .decode([SafeDecodable<RegressionTestModel>].self, forKey: .tests)
      .compactMap { $0.value }
  }
  
  private enum CodingKeys: String, CodingKey {
    case tests
  }

  private struct SafeDecodable<T: Decodable>: Decodable {
    let value: T?

    init(from decoder: Decoder) throws {
      do {
        value = try T(from: decoder)
      } catch let error {
        DemoAppLogger.error(error.localizedDescription)
        value = nil
      }
    }
  }
}

struct RegressionTestModel: Decodable, Hashable {
  let expectedResults: [String]
  let platforms: [Platform]
  let steps: [String]
  let tags: [String]
  let title: String
  let url: URL

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

    let files: [String]
    do {
      files = try container.decode([String].self, forKey: .files)
    } catch {
      throw DecodingError(key: .files, title: title, error: error)
    }
    guard let file = files.first else {
      throw DecodingError("Empty files section in test '\(title)'")
    }
    if let url = makeFileUrl(file) {
      self.url = url
    } else {
      throw DecodingError("File not found: \(file)")
    }

    platforms = (try? container.decodeIfPresent([Platform].self, forKey: .platforms)) ?? [ .ios ]
    tags = (try? container.decodeIfPresent([String].self, forKey: .tags)) ?? []
  }
  
  var description: String {
    var description = ""
    if !steps.isEmpty {
      description += "Steps:"
      steps.forEach {
        description += "\n • \($0)"
      }
    }

    if !expectedResults.isEmpty {
      if !steps.isEmpty {
        description += "\n\n"
      }
      description += "Expected results:"
      expectedResults.forEach {
        description += "\n • \($0)"
      }
    }
    
    return description
  }
  
  private enum CodingKeys: String, CodingKey {
    case expectedResults = "expected_results"
    case files
    case platforms
    case steps
    case tags
    case title
  }
                           
  private struct DecodingError: LocalizedError {
    let description: String

    init(_ description: String) {
      self.description = description
    }

    init(key: CodingKeys, error: Error) {
      description = "Failed to read \(key.rawValue): \(error.localizedDescription)"
    }

    init(key: CodingKeys, title: String, error: Error) {
      description = "Failed to read \(key.rawValue) in test '\(title)': \(error.localizedDescription)"
    }
    
    var errorDescription: String? {
      NSLocalizedString(description, comment: "DecodingError")
    }
  }
}

enum Platform: String, Decodable {
  case android
  case ios
  case web
}

private func makeFileUrl(_ relativeFileName: String) -> URL? {
  let path: String
  let fileName: String
  if let slashIndex = relativeFileName.lastIndex(of: "/") {
    path = TestData.regressionPath + "/" + relativeFileName.prefix(upTo: slashIndex)
    fileName = String(relativeFileName.suffix(from: relativeFileName.index(after: slashIndex)))
  } else {
    path = TestData.regressionPath
    fileName = relativeFileName
  }
  return Bundle.main
    .url(forResource: fileName, withExtension: "", subdirectory: path)
}
