import LayoutKit
import VGSL
import XCTest

final class GalleryViewMetricsTests: XCTestCase {
  func test_WhenCreated_TurnsSideGapsIntoFixedAxialInsets() {
    let metrics = GalleryViewMetrics(gaps: [12, 10, 16])

    let expectedInsets = SideInsets(leading: 12, trailing: 16)
    XCTAssertEqual(metrics.axialInsetMode, .fixed(values: expectedInsets))
  }

  func test_WhenCreated_UsesGapsAsSpacingsExcepsSideOnes() {
    let metrics = GalleryViewMetrics(gaps: [12, 10, 11, 16])

    let expectedSpacings: [CGFloat] = [10, 11]
    XCTAssertEqual(metrics.spacings, expectedSpacings)
  }

  func test_WhenCalculatingGapsWithFixedInsets_TreatsThemAsSideGaps() {
    let insetValues = SideInsets(leading: 16, trailing: 14)
    let metrics = GalleryViewMetrics(
      axialInsetMode: .fixed(values: insetValues),
      spacings: [2, 3, 1],
      crossSpacing: 0
    )

    let arbitraryBoundsSize: CGFloat = 320
    let gaps = metrics.gaps(forSize: arbitraryBoundsSize)

    let expectedGaps: [CGFloat] = [insetValues.leading, 2, 3, 1, insetValues.trailing]
    XCTAssertEqual(gaps, expectedGaps)
  }

  func test_WhenCalculatingGapsWithResizableInsets_UsesThemToFillSpaceBeyondMaxViewportSize() {
    let maxViewportSize: CGFloat = 100
    let metrics = GalleryViewMetrics(
      axialInsetMode: .resizable(params: .init(minValue: 9, maxViewportSize: maxViewportSize)),
      spacings: [2, 3, 1],
      crossSpacing: 0
    )

    let boundsSize: CGFloat = 320
    let gaps = metrics.gaps(forSize: boundsSize)

    let expectedSideGap: CGFloat = (boundsSize - maxViewportSize) / 2
    XCTAssertEqual(gaps, [expectedSideGap, 2, 3, 1, expectedSideGap])
  }

  func test_WhenCalculatingGapsWithResizableInsets_ClampsThemToMinValuesIfExceedingSize() {
    let minInsetValue: CGFloat = 9
    let metrics = GalleryViewMetrics(
      axialInsetMode: .resizable(params: .init(minValue: minInsetValue, maxViewportSize: 100)),
      spacings: [2, 3, 1],
      crossSpacing: 0
    )

    let boundsSize: CGFloat = 110
    let gaps = metrics.gaps(forSize: boundsSize)

    XCTAssertEqual(gaps, [minInsetValue, 2, 3, 1, minInsetValue])
  }
}
