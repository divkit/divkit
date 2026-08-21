@testable import DivKit
import DivKitExtensions
import DivKitMarkdownExtension
import DivKitTestsSupport
import Foundation
@testable import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct DivKitSnapshotTests {
  @Test("Snapshots", .serialized, arguments: selectedSnapshotTestsFiles)
  func snapshotTest(jsonFile: JsonFile) async throws {
    if exclusions.contains(where: { $0 == jsonFile.relativePath }) {
      try await doTestForDifferentStates(jsonFile)
      return
    }
    try await doTest(jsonFile)
  }

  @Test(
    "Interactive Snapshots",
    .serialized,
    .timeLimit(.minutes(1)),
    arguments: selectedInteractiveSnapshotTestsFiles
  )
  func interactiveSnapshotTest(jsonFile: JsonFile) async throws {
    try await doTest(jsonFile)
  }

  private func doTest(_ file: JsonFile) async throws {
    let test = SnapshotTestRunner(file: file)

    try await test.run(
      caseName: file.name.removingFileExtension,
      statesByElementId: defaultPagerViewState,
      extensions: [
        CustomImagePreviewExtensionHandler(
          id: "label_image_preview",
          viewProvider: LabelImagePreviewProvider()
        ),
        MarkdownExtensionHandler(),
      ]
    )
  }

  private func doTestForDifferentStates(
    _ file: JsonFile
  ) async throws {
    for state in testPagerViewStates {
      let test = SnapshotTestRunner(file: file)
      try await test.run(
        caseName: "\(state.currentPage)_" + file.name.removingFileExtension,
        statesByElementId: [pagerId: state]
      )
    }
  }
}

private let selectedSnapshotTestsFiles = selectedJsonFiles(
  snapshotTestsFiles,
  kind: "snapshot-json",
  prefix: "snapshot_test_data/"
)

private let selectedInteractiveSnapshotTestsFiles = selectedJsonFiles(
  interactiveSnapshotTestsFiles,
  kind: "interactive-snapshot-json",
  prefix: "interactive_snapshot_test_data/"
)

private func selectedJsonFiles(
  _ files: [JsonFile],
  kind: String,
  prefix: String
) -> [JsonFile] {
  guard let selectedKind = ProcessInfo.processInfo.testArgument("divkit-test-kind"),
        ["snapshot-json", "interactive-snapshot-json"].contains(selectedKind) else {
    return files
  }
  guard selectedKind == kind else {
    return []
  }
  guard let selector = ProcessInfo.processInfo.testArgument("divkit-test-filter"),
        selector.hasPrefix(prefix),
        !selector.contains("..") else {
    preconditionFailure("Invalid \(kind) selector")
  }

  let relativePath = String(selector.dropFirst(prefix.count))
  let selected = files.filter { $0.relativePath == relativePath }
  precondition(!selected.isEmpty, "No \(kind) test matches: \(selector)")
  return selected
}

private let exclusions = [
  "div-indicator/fixed-width-max_items_rectangle.json",
  "div-indicator/fixed-width-max_items_rectangle_slider.json",
  "div-indicator/fixed-width-max_items_rectangle_worm.json",
]

@MainActor
private class LabelImagePreviewProvider: @MainActor ViewProvider {
  private var label: UILabel?

  func loadView() -> ViewType {
    if let label {
      return label
    }
    label = makeLabel()
    return label!
  }

  func equals(other: ViewProvider) -> Bool {
    loadView() == other.loadView()
  }

  private func makeLabel() -> UILabel {
    let label = UILabel()
    label.text = "Preview"
    label.backgroundColor = .yellow
    return label
  }
}

private var defaultPagerViewState: [String: ElementState] {
  [pagerId: PagerViewState(numberOfPages: 11, floatCurrentPage: 1.0)]
}

private let testPagerViewStates = [
  PagerViewState(numberOfPages: 11, floatCurrentPage: 0),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 1.5),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 2),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 5.2),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 7.7),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 9.4),
  PagerViewState(numberOfPages: 11, floatCurrentPage: 10),
]

private let pagerId = "pager_id"

extension String {
  var removingFileExtension: String {
    guard let dotIndex = firstIndex(of: ".") else {
      return self
    }
    return String(self[startIndex..<dotIndex])
  }
}
