import XCTest
import UIKit

import BasePublic
import DivKit
import DivKitExtensions
import LayoutKit

private let exclusions = [
  // tested in DivIndicatorTests
  "div-indicator/fixed-width-max_items_rectangle.json",
  "div-indicator/fixed-width-max_items_rectangle_slider.json",
  "div-indicator/fixed-width-max_items_rectangle_worm.json",
]

private let casesWithPlaceholerOnly = [
  "div-image/placeholder-color.json",
  "div-image/preview.json",
  "div-image/custom-preview.json",
  "div-gif-image/placeholder-color.json",
  "div-gif-image/preview.json",
]

private let divExtensions: [String: [DivExtensionHandler]] = [
  "div-image/custom-preview.json": [labelImagePreviewExtension]
]

private let testDirectory = "snapshot_test_data"

private let indicatorSubdirectory = "div-indicator"

private let pagerPath = UIElementPath(testCardId) + "pager_id"

private let defaultPagerViewState = [
  pagerPath: PagerViewState(numberOfPages: 11, currentPage: 1),
]

final class DivSnapshotTests: XCTestCase {
  override class var defaultTestSuite: XCTestSuite {
    let jsonFiles = loadJsonFiles(testDirectory, exclusions: exclusions)
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
    customCaseName: file.name.removingFileExtension,
    imageHolderFactory: casesWithPlaceholerOnly.contains(file.path) ? PlaceholderFactory() : nil,
    blocksState: file.subdirectory == indicatorSubdirectory ? defaultPagerViewState : [:],
    extensions: divExtensions[file.path] ?? []
  )
}

private final class PlaceholderFactory: DivImageHolderFactory {
  func make(_: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    placeholder?.toImageHolder() ?? NilImageHolder()
  }
}

private let labelImagePreviewExtension = CustomImagePreviewExtensionHandler(
  id: "label_image_preview",
  viewFactory: {
    let label = UILabel()
    label.text = "Preview"
    label.backgroundColor = .yellow
    return label
  }
)
