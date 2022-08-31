import Foundation

enum Samples {
  static let regressionPath = "Regression"
  static let samplesPath = "samples"
  static let patchesPath = "Regression/Patches"
  static let patchesFolderName = "Patches"
  
  static func getItems(
    path: String,
    extension ext: String
  ) -> [(URL, String)] {
    return Bundle.main
      .urls(forResourcesWithExtension: ext, subdirectory: path)!
      .map { url in (url, String(url.lastPathComponent.split(separator: ".").first!)) }
      .sorted { $0.1 < $1.1 }
  }
  
  static var allSamples: [URL] {
    getItems(path: samplesPath, extension: "")
      .flatMap { _, folderName in
        getItems(path: "\(samplesPath)/\(folderName)", extension: ".json")
      }
      .map { url, _ in url }
  }
}
