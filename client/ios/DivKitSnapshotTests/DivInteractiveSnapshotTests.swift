import XCTest

private let exclusions: [String] = [
  "div-container/visibility.json",
  "div-gallery/visibility.json",
  "div-input/visibility.json",
  "div-pager/visibility.json",
  "div-slider/visibility.json",
  "div-state/visibility.json",
  
  "div-slider/thumb-style/thumb-primary-style-properties.json",
  "div-slider/thumb-style/thumb-secondary-style-properties.json",
  "div-slider/thumb-values/thumb-values-with-tick-marks.json",
  "div-slider/thumb-values/thumb-values-without-tick-marks.json",
  "div-slider/tick-mark-style/tick-mark-active-style-properties.json",
  "div-slider/tick-mark-style/tick-mark-inactive-style-properties.json",
  "div-slider/track-style/track-active-style-properties.json",
  "div-slider/track-style/track-inactive-style-properties.json",

  "div-text/text-properties.json",
]

private let testDirectory = "interactive_snapshot_test_data"

final class DivInteractiveSnapshotTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    let jsonFiles = loadJsonFiles(testDirectory, exclusions: exclusions)
    return makeSuite(input: jsonFiles.map { (name: $0.path, data: $0) }, test: doTest)
  }
}

private func doTest(_ file: JsonFile) {
  let test = DivKitSnapshotTestCase()
  test.rootDirectory = testDirectory
  test.subdirectory = file.subdirectory
  test.testDivs(
    file.name,
    customCaseName: file.name.removingFileExtension
  )
}
