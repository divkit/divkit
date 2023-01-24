import XCTest

private let testDirectory = "interactive_snapshot_test_data"

final class DivInteractiveSnapshotTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    let jsonFiles = loadJsonFiles(testDirectory)
      .filter {
        DivKitSnapshotTestCase.isSupported(file: $0, rootDirectory: testDirectory)
      }
    return makeSuite(
      input: jsonFiles.map { (name: $0.path, data: $0) },
      test: doTest
    )
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
