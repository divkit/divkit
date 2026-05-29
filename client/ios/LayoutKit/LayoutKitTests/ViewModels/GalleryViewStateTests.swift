import Foundation
@testable import LayoutKit
import Testing
import VGSL

@Suite
struct GalleryViewStateTests {
  // MARK: - empty model

  @Test
  func emptyModel_resetsToZeroWithoutAnimation() {
    let state = GalleryViewState(
      contentPosition: .offset(500, firstVisibleItemIndex: 2),
      itemsCount: 5,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(model(count: 0))
    #expect(result.contentPosition == .offset(0, firstVisibleItemIndex: 0))
    #expect(result.itemsCount == 0)
    #expect(result.animated == false)
  }

  // MARK: - firstVisibleItemIndex out of range

  @Test(
    arguments: [
      // (firstVisibleItemIndex, itemsCount, expectedOffset)
      (10, 3, CGFloat(0)), // index far beyond count
      (3, 3, CGFloat(0)), // index == count (off-by-one)
      (-1, 3, CGFloat(0)), // negative index
    ]
  )
  func offset_invalidFirstVisibleItemIndex_resetsToZeroWithoutAnimation(
    firstVisibleItemIndex: Int,
    itemsCount: Int,
    expectedOffset: CGFloat
  ) {
    let state = GalleryViewState(
      contentPosition: .offset(500, firstVisibleItemIndex: firstVisibleItemIndex),
      itemsCount: itemsCount,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(model(count: itemsCount))
    #expect(result.contentPosition == .offset(expectedOffset, firstVisibleItemIndex: 0))
    #expect(result.animated == false)
  }

  // MARK: - offset beyond maxValidScrollRange → reset to zero

  @Test(
    arguments: [
      (500.0, 100.0), // way beyond range → reset
      (200.0, 0.0), // scrollRange=0, any positive offset → reset
    ]
  )
  func offset_beyondMaxValidScrollRange_resetsToZeroWithoutAnimation(
    offset: CGFloat,
    maxValidScrollRange: CGFloat
  ) {
    let state = GalleryViewState(
      contentPosition: .offset(offset, firstVisibleItemIndex: 0),
      itemsCount: 3,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(
      model(count: 3),
      maxValidScrollRange: maxValidScrollRange
    )
    #expect(result.contentPosition == .offset(0, firstVisibleItemIndex: 0))
    #expect(result.animated == false)
  }

  @Test(
    arguments: [
      (100.0, 100.0), // exactly at boundary → preserved
      (50.0, 100.0), // within range → preserved
    ]
  )
  func offset_withinMaxValidScrollRange_isPreserved(
    offset: CGFloat,
    maxValidScrollRange: CGFloat
  ) {
    let state = GalleryViewState(
      contentPosition: .offset(offset, firstVisibleItemIndex: 0),
      itemsCount: 3,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(
      model(count: 3),
      maxValidScrollRange: maxValidScrollRange
    )
    #expect(result.contentPosition.offset!.isApproximatelyEqualTo(offset, withAccuracy: 1e-4))
    #expect(result.animated == true)
  }

  // MARK: - no maxValidScrollRange → offset preserved

  @Test
  func offset_nomaxValidScrollRange_preservesOffsetAndAnimation() {
    let state = GalleryViewState(
      contentPosition: .offset(1934, firstVisibleItemIndex: 0),
      itemsCount: 3,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(model(count: 3))
    #expect(result.contentPosition == .offset(1934, firstVisibleItemIndex: 0))
    #expect(result.animated == true)
  }

  // MARK: - scrollRange propagation

  @Test
  func nomaxValidScrollRange_existingScrollRangePreserved() {
    let state = GalleryViewState(
      contentPosition: .offset(0, firstVisibleItemIndex: 0),
      itemsCount: 1,
      isScrolling: false,
      scrollRange: 42,
      animated: false
    )
    let result = state.resetToModelIfInconsistent(model(count: 1))
    #expect(result.scrollRange == 42)
  }

  @Test
  func maxValidScrollRange_overridesExistingScrollRange() {
    let state = GalleryViewState(
      contentPosition: .offset(0, firstVisibleItemIndex: 0),
      itemsCount: 1,
      isScrolling: false,
      scrollRange: 42,
      animated: false
    )
    let result = state.resetToModelIfInconsistent(model(count: 1), maxValidScrollRange: 99)
    #expect(result.scrollRange == 99)
  }

  // MARK: - paging index out of range

  @Test(
    arguments: [
      (CGFloat(-1), 3),
      (CGFloat(3), 3),
      (CGFloat(100), 3),
    ]
  )
  func paging_outOfRange_resetsToZeroWithoutAnimation(index: CGFloat, count: Int) {
    let state = GalleryViewState(
      contentPosition: .paging(index: index),
      itemsCount: count,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(model(count: count))
    #expect(result.contentPosition == .paging(index: 0))
    #expect(result.animated == false)
  }

  @Test
  func paging_inRange_preserved() {
    let state = GalleryViewState(
      contentPosition: .paging(index: 2),
      itemsCount: 3,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(model(count: 3))
    #expect(result.contentPosition == .paging(index: 2))
    #expect(result.animated == true)
  }
}

private let path = UIElementPath("gallery")
private let metrics = GalleryViewMetrics(
  axialInsetMode: .fixed(values: .zero),
  spacings: [],
  crossSpacing: 0
)
private let block = TextBlock(
  widthTrait: .resizable,
  text: NSAttributedString(string: "item")
)

private func model(
  count: Int,
  scrollMode: GalleryViewModel.ScrollMode = .default
) -> GalleryViewModel {
  GalleryViewModel(
    items: (0..<count).map { _ in .init(crossAlignment: .leading, content: block) },
    metrics: metrics,
    scrollMode: scrollMode,
    path: path,
    direction: .horizontal
  )
}
