import Foundation

let snapshotsPath = CommandLine.arguments[1]
let arrayName = CommandLine.arguments[2]
let jsonFiles = loadJsonFiles().filter(isSupported(file:))

print("""
let \(arrayName) = [
\(jsonFiles.map(\.description).joined(separator: ",\n"))
]

""")

private func loadJsonFiles() -> [JsonFile] {
  guard let paths = try? FileManager.default.subpathsOfDirectory(atPath: snapshotsPath) else {
    return []
  }

  return paths.compactMap { path -> JsonFile? in
    return JsonFile(
      absolutePath: snapshotsPath + "/" + path,
      relativePath: path
    )
  }.filter { $0.absolutePath.contains(".json") }
}

private func isSupported(file: JsonFile) -> Bool {
  guard let jsonDict = jsonDict(fileName: file.absolutePath) else { return false }
  guard let platforms = jsonDict["platforms"] as? [String] else {
    return true
  }
  return platforms.contains { $0 == "ios" }
}

private func jsonDict(fileName: String) -> [String: Any]? {
  guard let data = FileManager.default.contents(atPath: fileName) else {
    return nil
  }
  return (try? JSONSerialization.jsonObject(with: data)) as? [String: Any]
}

struct JsonFile: CustomStringConvertible {
  let absolutePath: String
  let relativePath: String

  var description: String {
    """
      JsonFile(absolutePath: "\(absolutePath)", relativePath: "\(relativePath)")
    """
  }
}
