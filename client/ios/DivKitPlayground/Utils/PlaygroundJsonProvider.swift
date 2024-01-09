import BasePublic
import DivKit
import Foundation

struct PlaygroundJsonProvider {
  typealias Json = [String: Any]

  @ObservableProperty
  private(set) var json: Json = [:]

  let paletteVariableStorage = DivVariableStorage()

  func load(url: URL) {
    guard let json = try? Data(contentsOf: url).asJsonDictionary() else {
      self.json = [:]
      return
    }

    let paletteVaraibles = Palette(json: try! json.getOptionalField("palette") ?? [:])
      .makeVariables(theme: UserPreferences.playgroundTheme)
    paletteVariableStorage.replaceAll(paletteVaraibles)

    self.json = json
  }
}

extension Data {
  func asJsonDictionary() throws -> [String: Any] {
    try JSONSerialization.jsonObject(with: self, options: []) as! [String: Any]
  }
}
