import Foundation

enum RegressionFile {
  static let regressionPath = "regression_test_data"

  static func makeUrl(_ relativeFileName: String) -> URL? {
    let path: String
    let fileName: String
    if let slashIndex = relativeFileName.lastIndex(of: "/") {
      path = regressionPath + "/" + relativeFileName.prefix(upTo: slashIndex)
      fileName = String(relativeFileName.suffix(from: relativeFileName.index(after: slashIndex)))
    } else {
      path = regressionPath
      fileName = relativeFileName
    }
    return Bundle.main
      .url(forResource: fileName, withExtension: "", subdirectory: path)
  }
}
