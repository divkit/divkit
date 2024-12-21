import Combine
import DivKit
import Foundation

typealias JsonDictionary = [String: any Sendable]
typealias JsonPublisher = AnyPublisher<JsonDictionary, Never>

struct PlaygroundJsonProvider {
  private let jsonSubject = PassthroughSubject<JsonDictionary, Never>()

  var jsonPublisher: JsonPublisher {
    jsonSubject.eraseToAnyPublisher()
  }

  let paletteVariableStorage = DivVariableStorage()

  func load(url: URL) {
    Task {
      let data = try? await URLSession.shared.data(from: url).0
      let json = (try? data?.asJsonDictionary()) ?? [:]
      let palette = Palette(json: (try? json.getOptionalField("palette")) ?? [:])

      await MainActor.run {
        let paletteVaraibles = palette.makeVariables(theme: UserPreferences.playgroundTheme)
        paletteVariableStorage.replaceAll(paletteVaraibles)

        jsonSubject.send(json)
      }
    }
  }
}

extension Data {
  func asJsonDictionary() throws -> JsonDictionary {
    try JSONSerialization.jsonObject(with: self, options: []) as! JsonDictionary
  }
}
