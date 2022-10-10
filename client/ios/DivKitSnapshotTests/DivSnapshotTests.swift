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

  "div-slider/slider_with_warning.json",
  "div-slider/slider_with_error_and_warning.json",

  "div-container/container_with_wrap_content_constrained_child.json",

  "div-grid/content-alignment.json",
  "div-container/horizontal-orientation-alignments.json",
  "div-container/overlap-orientation-default-alignments.json",
  "div-container/vertical-orientation-alignments.json",
  "div-text/images.json",
  "div-gallery/visibility.json",
  
]

private let casesWithPlaceholerOnly = [
  "div-image/placeholder-color.json",
  "div-image/preview.json",
  "div-gif-image/placeholder-color.json",
  "div-gif-image/preview.json",
]

private let testDirectory = "snapshot_test_data"

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
    imageHolderFactory: casesWithPlaceholerOnly.contains(file.path) ? .placeholderOnly : nil
  )
}
