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

  // MARK: - firstVisibleItemIndex out of range without a known scroll range

  @Test(
    arguments: [
      // (firstVisibleItemIndex, itemsCount, expectedIndex)
      (10, 3, 2), // index far beyond count
      (3, 3, 2), // index == count (off-by-one)
      (-1, 3, 0), // negative index
    ]
  )
  func offset_invalidFirstVisibleItemIndex_clampsIndexKeepingOffset(
    firstVisibleItemIndex: Int,
    itemsCount: Int,
    expectedIndex: Int
  ) {
    let state = GalleryViewState(
      contentPosition: .offset(500, firstVisibleItemIndex: firstVisibleItemIndex),
      itemsCount: itemsCount,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(model(count: itemsCount))
    #expect(result.contentPosition == .offset(500, firstVisibleItemIndex: expectedIndex))
    #expect(result.animated == false)
  }

  // MARK: - offset beyond maxValidScrollRange → clamped to the new trailing edge

  @Test(
    arguments: [
      // (offset, maxValidScrollRange, expectedOffset, expectedIndex)
      (500.0, 100.0, 100.0, 1), // content got shorter → pinned to the new trailing edge
      (200.0, 0.0, 0.0, 0), // content fits entirely → pinned to the beginning, index reset
      (500.0, -50.0, 0.0, 0), // content smaller than bounds → pinned to the beginning
    ]
  )
  func offset_beyondMaxValidScrollRange_isClampedWithoutAnimation(
    offset: CGFloat,
    maxValidScrollRange: CGFloat,
    expectedOffset: CGFloat,
    expectedIndex: Int
  ) {
    let state = GalleryViewState(
      contentPosition: .offset(offset, firstVisibleItemIndex: 1),
      itemsCount: 3,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(
      model(count: 3),
      maxValidScrollRange: maxValidScrollRange
    )
    #expect(result.contentPosition == .offset(expectedOffset, firstVisibleItemIndex: expectedIndex))
    #expect(result.scrollRange == maxValidScrollRange)
    #expect(result.animated == false)
  }

  // MARK: - invalid firstVisibleItemIndex with a known scroll range → clamped, not reset

  @Test(
    arguments: [
      // (offset, firstVisibleItemIndex, expectedOffset, expectedIndex)
      (700.0, 7, 100.0, 2), // both offset and index are stale → pinned to the trailing edge
      (50.0, 7, 50.0, 2), // offset still fits, only the index is stale → offset preserved
      (700.0, -1, 100.0, 0), // negative index → clamped to the first item
      (-30.0, 7, 0.0, 0), // negative offset left from bounce → clamped to the beginning
    ]
  )
  func offset_invalidIndexWithMaxValidScrollRange_isClampedWithoutAnimation(
    offset: CGFloat,
    firstVisibleItemIndex: Int,
    expectedOffset: CGFloat,
    expectedIndex: Int
  ) {
    let state = GalleryViewState(
      contentPosition: .offset(offset, firstVisibleItemIndex: firstVisibleItemIndex),
      itemsCount: 10,
      isScrolling: false,
      animated: true
    )
    let result = state.resetToModelIfInconsistent(
      model(count: 3),
      maxValidScrollRange: 100
    )
    #expect(result.contentPosition == .offset(expectedOffset, firstVisibleItemIndex: expectedIndex))
    #expect(result.itemsCount == 3)
    #expect(result.animated == false)
  }

  // MARK: - block-level pass followed by the range-aware one

  @Test
  func invalidIndexWithoutRange_thenRangeAwarePass_clampsOffsetToTrailingEdge() {
    let shrunkModel = model(count: 3)
    let state = GalleryViewState(
      contentPosition: .offset(700, firstVisibleItemIndex: 8),
      itemsCount: 10,
      isScrolling: false,
      animated: true
    )
    let result = state
      .resetToModelIfInconsistent(shrunkModel)
      .resetToModelIfInconsistent(shrunkModel, maxValidScrollRange: 100)
    #expect(result.contentPosition == .offset(100, firstVisibleItemIndex: 2))
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
