import BasePublic
import DivKit
import Foundation

struct PlaygroundJsonProvider {
  typealias Json = [String: Any]
  @ObservableProperty
  private(set) var json: Json = [:]
  private let variablesStorage: DivVariablesStorage

  public init(variablesStorage: DivVariablesStorage) {
    self.variablesStorage = variablesStorage
  }

  public func load(url: URL) {
    guard let json = try? Data(contentsOf: url).asJsonDictionary() else {
      self.json = [:]
      return
    }
    let palette = Palette(json: try! json.getOptionalField("palette") ?? [:])
    variablesStorage.set(
      variables: palette.makeVariables(theme: UserPreferences.playgroundTheme),
      triggerUpdate: false
    )
    self.json = json
  }
}

extension Data {
  func asJsonDictionary() throws -> [String: Any] {
    try JSONSerialization.jsonObject(with: self, options: []) as! [String: Any]
  }
}
