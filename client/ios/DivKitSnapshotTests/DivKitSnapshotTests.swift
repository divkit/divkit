import DivKitExtensions
import DivKit
import Testing
@testable import LayoutKit

import UIKit
import class XCTest.XCTestCase

final class AllTests: XCTestCase {
  func testAll() async {
    await XCTestScaffold.runAllTests(hostedBy: self)
  }
}

@Suite struct DivKitSnapshotTests {
  @Test(arguments: snapshotTestsFiles)
  func snapshotTest(jsonFile: JsonFile) async throws {
    if exclusions.contains(where: { $0 == jsonFile.relativePath }) {
      try await doTestForDifferentStates(jsonFile)
      return
    }
    try await doTest(jsonFile)
  }

  @Test(arguments: interactiveSnapshotTestsFiles)
  func interactiveSnapshotTest(jsonFile: JsonFile) async throws {
    try await doTest(jsonFile)
  }

  @MainActor
  func doTest(_ file: JsonFile) throws {
    let test = SnapshotTestRunner(file: file)

    try test.run(
      caseName: file.name.removingFileExtension,
      blocksState: defaultPagerViewState,
      extensions: [labelImagePreviewExtension]
    )
  }

  @MainActor
  func doTestForDifferentStates(
    _ file: JsonFile
  ) throws {
    for state in testPagerViewStates {
      let blocksState = [
        pagerId: state,
      ]
      let test = SnapshotTestRunner(file: file)
      try test.run(
        caseName: "\(state.currentPage)_" + file.name.removingFileExtension,
        blocksState: blocksState
      )
    }
  }
}

private let exclusions = [
  "div-indicator/fixed-width-max_items_rectangle.json",
  "div-indicator/fixed-width-max_items_rectangle_slider.json",
  "div-indicator/fixed-width-max_items_rectangle_worm.json",
]

private let labelImagePreviewExtension = CustomImagePreviewExtensionHandler(
  id: "label_image_preview",
  viewFactory: {
    let label = UILabel()
    label.text = "Preview"
    label.backgroundColor = .yellow
    return label
  }
)

private let defaultPagerViewState = [
  pagerId: PagerViewState(numberOfPages: 11, currentPage: 1),
]

private let testPagerViewStates = [
  PagerViewState(numberOfPages: 11, floatCurrentPage: 0),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 1.5),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 2),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 5.2),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 7.7),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 9.4),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 10),
]

private let pagerId = IdAndCardId(path: UIElementPath(testCardId) + "pager_id")

extension String {
  var removingFileExtension: String {
    guard let dotIndex = firstIndex(of: ".") else {
      return self
    }
    return String(self[startIndex..<dotIndex])
  }
}
