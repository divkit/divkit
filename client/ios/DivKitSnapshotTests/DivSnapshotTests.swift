import XCTest

private let exclusions = [
  "div-indicator/active_size.json",
  "div-indicator/fixed-width-max_items_circle.json",
  "div-indicator/wrap_content-height.json",
  "div-indicator/margins.json",
  "div-indicator/fixed-height.json",
  "div-indicator/wrap_content-width-max_items.json",
  "div-indicator/match_parent-width-max_items.json",
  "div-indicator/minimum_size.json",
  "div-indicator/paddings.json",
  "div-indicator/corners_radius.json",
  "div-indicator/fixed-width-max_items_rectangle.json",
  "div-indicator/fixed-width-max_items_rectangle_slider.json",
  "div-indicator/fixed-width-max_items_rectangle_worm.json",

  // TODO: remove after implementation:
  "div-gallery/vertical-grid-gallery-padding.json",
  "div-gallery/vertical-grid-gallery-item-spacing.json",

  "div-image/aspect-wrap_content.json",
  "div-gif-image/aspect-wrap_content.json",
  "div-pager/vertical-pager-fixed-width.json",
  "div-pager/vertical-pager-resizable-width.json",
  
]

private let casesWithPlaceholerOnly = [
  "div-image/placeholder-color.json",
  "div-image/preview.json",
  "div-gif-image/placeholder-color.json",
  "div-gif-image/preview.json",
]

final class DivSnapshotTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    makeSuite(input: jsons.map { (name: $0.path, data: $0) }, test: doTest)
  }
}

private func doTest(_ file: JsonFile) {
  let test = DivKitSnapshotTestCase()
  test.rootDirectory = "snapshot_test_data"
  test.subdirectory = file.subdirectory
  test.testDivs(
    file.name,
    customCaseName: file.name.removingFileExtension,
    imageHolderFactory: casesWithPlaceholerOnly.contains(file.path) ? .placeholderOnly : nil
  )
}

private struct JsonFile {
  let path: String
  let name: String
  let subdirectory: String
}

private var jsons: [JsonFile] {
  let testBundle = Bundle(for: DivKitSnapshotTestCase.self)
  let snapshotsPath = testBundle.bundleURL.appendingPathComponent("snapshot_test_data").path

  guard let paths = try? FileManager.default.subpathsOfDirectory(atPath: snapshotsPath) else {
    return []
  }

  return paths.compactMap { path -> JsonFile? in
    guard let index = path.lastIndex(of: "/") else {
      return nil
    }
    return JsonFile(
      path: path,
      name: String(path[path.index(after: index)...]),
      subdirectory: String(path[..<index])
    )
  }
  .filter { $0.name.contains(".json") && !exclusions.contains($0.path) }
}
