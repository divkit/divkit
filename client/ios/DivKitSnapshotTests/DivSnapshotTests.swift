import XCTest
import LayoutKit

private let exclusions = [
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

  "div-slider/slider_with_warning.json",
  "div-slider/slider_with_error_and_warning.json",

  "div-container/container_with_wrap_content_constrained_child.json",
  "div-container/match-parent-inside-wrap-content.json",
  "div-container/match-parent-lesser-than-paddings.json",
  "div-container/match-parent-with-big-content.json",
  "div-container/match-parent-with-margins.json",
  "div-container/match-parent-with-padding.json",
  "div-container/overlap-with-match-parent-child.json",

  "div-text/variables-rendering.json",

  "div-indicator/shapes.json",

  "div-container/wrap/vertical-orientation-wrap-content-constrained.json",
]

private let casesWithPlaceholerOnly = [
  "div-image/placeholder-color.json",
  "div-image/preview.json",
  "div-gif-image/placeholder-color.json",
  "div-gif-image/preview.json",
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
    return makeSuite(input: jsonFiles.map { (name: $0.path, data: $0) }, test: doTest)
  }
}

private func doTest(_ file: JsonFile) {
  let test = DivKitSnapshotTestCase()
  test.rootDirectory = testDirectory
  test.subdirectory = file.subdirectory
  test.testDivs(
    file.name,
    customCaseName: file.name.removingFileExtension,
    imageHolderFactory: casesWithPlaceholerOnly.contains(file.path) ? .placeholderOnly : nil,
    blocksState: file.subdirectory == indicatorSubdirectory ? defaultPagerViewState : [:]
  )
}
