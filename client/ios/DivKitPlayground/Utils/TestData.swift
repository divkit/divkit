import Foundation

enum TestData {
  static let regressionPath = "regression_test_data"
  static let samplesPath = "samples"
  static let patchesPath = "regression_test_data/patches"
  
  private static var cachedRegressionTests: [RegressionTestModel]?
  
  static var samples: [URL] {
    getAllFiles(path: samplesPath)
      .map { url, _ in url }
  }

  static var regressionTests: [RegressionTestModel] = {
    let indexFileUrl = Bundle.main
      .url(forResource: "index", withExtension: "json", subdirectory: regressionPath)!
    let indexFileData = try! Data(contentsOf: indexFileUrl)
    return try! JSONDecoder()
      .decode(RegressionTestsModel.self, from: indexFileData)
      .tests
      .filter { $0.platforms.contains(.ios) }
      .sorted { $0.title < $1.title }
  }()

  private static func getAllFiles(path: String) -> [(URL, String)] {
    getItems(path: path, extension: "")
      .flatMap { _, folderName in
        getAllFiles(path: "\(path)/\(folderName)")
      }
      + getItems(path: path, extension: ".json")
  }

  private static func getItems(
    path: String,
    extension ext: String
  ) -> [(URL, String)] {
    Bundle.main
      .urls(forResourcesWithExtension: ext, subdirectory: path)!
      .map { url in (url, String(url.lastPathComponent.split(separator: ".").first!)) }
      .sorted { $0.1 < $1.1 }
  }
}
