import CommonCore
import DivKit

struct Palette {
  private let json: [String: Any]

  init(json: [String: Any]) {
    self.json = json
  }

  func makeVariables(theme: Theme) -> DivVariables {
    guard let palette = json.getArray(theme.rawValue).value as? [[String: String]] else {
      return [:]
    }

    var result: [String: Color] = [:]
    palette.forEach {
      guard let name = $0["name"],
            let colorStr = $0["color"],
            let color = Color.color(withHexString: colorStr) else { return }
      result[name] = color
    }
    return result.mapToDivVariables()
  }
}
