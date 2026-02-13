import DivKit
import VGSL

struct Palette {
  private let json: [String: any Sendable]

  init(json: [String: any Sendable]) {
    self.json = json
  }

  func makeVariables(theme: Theme) -> DivVariables {
    guard let palette: [[String: String]] = try? json.getArray(theme.rawValue) else {
      return [:]
    }

    var result: [String: Color] = [:]
    for item in palette {
      guard let name = item["name"],
            let colorStr = item["color"],
            let color = Color.color(withHexString: colorStr) else { continue }
      result[name] = color
    }
    return result.mapToDivVariables()
  }
}
