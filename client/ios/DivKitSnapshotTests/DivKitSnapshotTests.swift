@testable import DivKit
import DivKitExtensions
import DivKitMarkdownExtension
@testable import LayoutKit
import Testing
import UIKit
import VGSL
import XCTest

final class AllTests: XCTestCase {
  func testAll() async {
    await XCTestScaffold.runAllTests(hostedBy: self)
  }
}

@MainActor
@Suite
struct DivKitSnapshotTests {
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

  func doTest(_ file: JsonFile) async throws {
    let test = SnapshotTestRunner(file: file)

    try await test.run(
      caseName: file.name.removingFileExtension,
      blocksState: defaultPagerViewState,
      extensions: [labelImagePreviewExtension, MarkdownExtensionHandler()]
    )
  }

  func doTestForDifferentStates(
    _ file: JsonFile
  ) async throws {
    for state in testPagerViewStates {
      let blocksState = [
        pagerId: state,
      ]
      let test = SnapshotTestRunner(file: file)
      try await test.run(
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
  viewProvider: LabelImagePreviewProvider()
)

private class LabelImagePreviewProvider: ViewProvider {
  private var label: UILabel?

  func loadView() -> ViewType {
    if let label {
      return label
    }
    label = makeLabel()
    return label!
  }

  private func makeLabel() -> UILabel {
    let label = UILabel()
    label.text = "Preview"
    label.backgroundColor = .yellow
    return label
  }

  func equals(other: ViewProvider) -> Bool {
    loadView() == other.loadView()
  }
}

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

private let pagerId = IdAndCardId(path: testCardId.path + "pager_id")

extension String {
  var removingFileExtension: String {
    guard let dotIndex = firstIndex(of: ".") else {
      return self
    }
    return String(self[startIndex..<dotIndex])
  }
}
