import Combine
import DivKit
import Foundation

typealias JsonDictionary = [String: any Sendable]
typealias JsonPublisher = AnyPublisher<JsonDictionary, Never>

struct PlaygroundJsonProvider {
  private let jsonSubject = PassthroughSubject<JsonDictionary, Never>()
  private let state = State()

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
        state.currentJson = json
        state.pallete = palette

        refreshPalette()

        jsonSubject.send(json)
      }
    }
  }

  @MainActor
  func refreshPalette() {
    guard let palette = state.pallete else {
      return
    }

    let paletteVariables = palette.makeVariables(theme: UserPreferences.playgroundTheme)
    paletteVariableStorage.replaceAll(paletteVariables)
  }

  private final class State: Sendable {
    @MainActor var currentJson: JsonDictionary = [:]
    @MainActor var pallete: Palette?
  }
}

extension Data {
  func asJsonDictionary() throws -> JsonDictionary {
    try JSONSerialization.jsonObject(with: self, options: []) as! JsonDictionary
  }
}
