@testable import LayoutKit

import XCTest

import VGSL

final class PagerViewLayoutTests: XCTestCase {
  func test_NeighbouredLayoutPageOrigins() {
    let layout = makeNeighbouredTestLayout()

    XCTAssertEqual(layout.pageOrigins, [0.0, 374.0, 708.0])
  }

  func test_PercentageLayoutPageOrigins() {
    let layout = makePercentageTestLayout()

    XCTAssertEqual(layout.pageOrigins, [0.0, 440.0, 880.0])
  }

  func test_NeighbouredLayoutContentSize() {
    let layout = makeNeighbouredTestLayout()

    XCTAssertEqual(layout.contentSize.width, 1098.0)
  }

  func test_PercentageLayoutContentSize() {
    let layout = makePercentageTestLayout()

    XCTAssertEqual(layout.contentSize.width, 1270.0)
  }

  func test_NeighbouredLayoutBlockPageSizes() {
    let layout = makeNeighbouredTestLayout()

    XCTAssertEqual(layout.blockPages.map(\.size), [374.0, 334.0, 390.0])
  }

  func test_PercentageLayoutBlockPageSizes() {
    let layout = makePercentageTestLayout()

    XCTAssertEqual(layout.blockPages.map(\.size), [440.0, 440.0, 390.0])
  }

  func test_NeighbouredLayoutBlockPageFrames() {
    let layout = makeNeighbouredTestLayout()

    XCTAssertEqual([
      layout.blockFrames.map(\.minX), layout.blockFrames.map(\.width),
    ], [[40.0, 394.0, 748.0], [350.0, 350.0, 350.0]])
  }

  func test_PercentageLayoutBlockPageFrames() {
    let layout = makePercentageTestLayout()

    XCTAssertEqual([
      layout.blockFrames.map(\.minX), layout.blockFrames.map(\.width),
    ], [[40.0, 440.0, 840.0], [390.0, 390.0, 390.0]])
  }

  private func makeNeighbouredTestLayout() -> PagerViewLayout {
    let metrics = GalleryViewMetrics(
      axialInsetMode: InsetMode.fixed(
        values: SideInsets(leading: 40.0, trailing: 0.0)
      ),
      spacings: [4.0, 4.0],
      crossSpacing: 0.0
    )

    let model = GalleryViewModel(
      items: Array(
        repeating: makeGalleryItem(),
        times: 3
      ),
      metrics: metrics,
      path: UIElementPath("1")
    )

    return PagerViewLayout(
      model: model,
      pageIndex: 0,
      layoutMode: .neighbourPageSize(10.0),
      boundsSize: CGSize(width: 390.0, height: 23.0)
    )
  }

  private func makePercentageTestLayout() -> PagerViewLayout {
    let metrics = GalleryViewMetrics(
      axialInsetMode: InsetMode.fixed(
        values: SideInsets(leading: 40.0, trailing: 40.0)
      ),
      spacings: [10.0, 10.0],
      crossSpacing: 0.0
    )

    let model = GalleryViewModel(
      items: Array(
        repeating: makeGalleryItem(),
        times: 3
      ),
      metrics: metrics,
      path: UIElementPath("1")
    )

    return PagerViewLayout(
      model: model,
      pageIndex: 0,
      layoutMode: .pageSize(RelativeValue(integerLiteral: 1)),
      boundsSize: CGSize(width: 390.0, height: 23.0)
    )
  }

  private func makeGalleryItem() -> GalleryViewModel.Item {
    GalleryViewModel.Item(
      crossAlignment: .leading,
      content: TextBlock(
        widthTrait: .weighted(.default),
        heightTrait: .intrinsic(
          constrained: false,
          minSize: 0.0,
          maxSize: CGFloat.infinity
        ),
        text: NSAttributedString(string: "Sample")
      )
    )
  }
}
