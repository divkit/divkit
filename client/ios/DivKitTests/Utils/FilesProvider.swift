import Foundation

func getFiles(_ path: String, forBundle bundle: Bundle) -> [URL] {
  guard let enumerator = FileManager.default.enumerator(
    atPath: bundle.path(forResource: path, ofType: nil)!
  ) else { return [] }

  return enumerator.compactMap {
    guard let fileName = $0 as? String, fileName.hasSuffix(".json") else { return nil }
    return bundle.url(forResource: fileName, withExtension: nil, subdirectory: path)!
  }
}
