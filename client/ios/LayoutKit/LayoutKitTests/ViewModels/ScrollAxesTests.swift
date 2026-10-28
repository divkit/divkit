import CoreGraphics
@testable import LayoutKit
import Testing
import VGSL

@Suite
struct ScrollAxesTests {
  private let rect = CGRect(x: 1, y: 2, width: 30, height: 40)

  @Test
  func horizontal_mapsAlongToXAndAcrossToY() {
    let direction = ScrollDirection.horizontal
    #expect(direction.axial(rect.size) == AxialSize(along: 30, across: 40))
    #expect(direction.size(AxialSize(along: 30, across: 40)) == rect.size)
    #expect(direction.along(rect.origin) == 1)
    #expect(direction.across(rect.origin) == 2)
    #expect(direction.maxAlong(rect) == 31)
    #expect(direction.maxAcross(rect) == 42)
    #expect(direction.rect(along: (1, 30), across: (2, 40)) == rect)
    #expect(direction.bounds(rect.size) == AxialBounds(along: 30, across: 40))
  }

  @Test
  func vertical_mapsAlongToYAndAcrossToX() {
    let direction = ScrollDirection.vertical
    #expect(direction.axial(rect.size) == AxialSize(along: 40, across: 30))
    #expect(direction.size(AxialSize(along: 40, across: 30)) == rect.size)
    #expect(direction.along(rect.origin) == 2)
    #expect(direction.across(rect.origin) == 1)
    #expect(direction.maxAlong(rect) == 42)
    #expect(direction.maxAcross(rect) == 31)
    #expect(direction.rect(along: (2, 40), across: (1, 30)) == rect)
    #expect(direction.bounds(rect.size) == AxialBounds(along: 40, across: 30))
  }

  @Test(arguments: ScrollDirection.allCases)
  func bounds_withoutSize_isUnbounded(direction: ScrollDirection) {
    #expect(direction.bounds(nil) == .unbounded)
  }

  @Test
  func alignmentOrigin_whenItemFits_alignsWithinSpace() {
    let origins = [Alignment.leading, .center, .trailing].map {
      $0.origin(of: 40, minimum: 10, maximum: 110)
    }
    #expect(origins == [10, 40, 70])
  }

  @Test
  func alignmentOrigin_whenItemOverflows_sticksOutOnBothSides() {
    let origins = [Alignment.leading, .center, .trailing].map {
      $0.origin(of: 160, minimum: 10, maximum: 110)
    }
    #expect(origins == [10, -20, -50])
  }

  @Test
  func boundedCrossAxisSize_splitsSpaceBetweenColumns() {
    let insets = SideInsets(leading: 10, trailing: 20)
    #expect(
      CrossAxisSizing.boundedCrossAxisSize(crossAxis: 130, crossInsets: insets) == 100
    )
    #expect(
      CrossAxisSizing.boundedCrossAxisSize(
        crossAxis: 130,
        crossInsets: insets,
        columnCount: 3,
        crossSpacing: 5
      ) == 30
    )
    #expect(
      CrossAxisSizing.boundedCrossAxisSize(
        crossAxis: 20,
        crossInsets: insets,
        columnCount: 2,
        crossSpacing: 5
      ) == 0
    )
    #expect(
      CrossAxisSizing.boundedCrossAxisSize(crossAxis: nil, crossInsets: insets) == nil
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func contentSize_withoutFrames_isZero(direction: ScrollDirection) {
    #expect(direction.contentSize(of: [], trailingGap: 5, trailingCrossInset: 7) == .zero)
  }

  @Test(arguments: ScrollDirection.allCases)
  func contentSize_addsTrailingInsetsToFarthestEdges(direction: ScrollDirection) {
    let frames = [
      direction.rect(along: (origin: 10, size: 30), across: (origin: 2, size: 40)),
      direction.rect(along: (origin: 50, size: 20), across: (origin: 2, size: 10)),
    ]

    #expect(
      direction.contentSize(of: frames, trailingGap: 5, trailingCrossInset: 7)
        == direction.size(AxialSize(along: 70 + 5, across: 42 + 7))
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func contentSize_takesFarthestFrameAlongScrollAxis_notTheLastOne(direction: ScrollDirection) {
    // Two columns: the last frame is in the short column.
    let frames = [
      direction.rect(along: (origin: 0, size: 100), across: (origin: 0, size: 10)),
      direction.rect(along: (origin: 0, size: 10), across: (origin: 20, size: 10)),
    ]

    #expect(
      direction.contentSize(of: frames, trailingGap: 0, trailingCrossInset: 0)
        == direction.size(AxialSize(along: 100, across: 30))
    )
  }

  @Test(arguments: ScrollDirection.allCases)
  func placeItems_placesItemsOneAfterAnotherWithGaps(direction: ScrollDirection) {
    let frames = direction.placeItems(
      [AxialSize(along: 30, across: 10), AxialSize(along: 20, across: 40)],
      alignments: [.leading, .leading],
      gaps: [5, 7, 9],
      slots: CrossAxisSlots(leading: 2, size: 50)
    )

    #expect(frames == [
      direction.rect(along: (origin: 5, size: 30), across: (origin: 2, size: 10)),
      direction.rect(along: (origin: 42, size: 20), across: (origin: 2, size: 40)),
    ])
  }

  @Test(arguments: ScrollDirection.allCases)
  func placeItems_alignsItemsWithinCrossAxisSlot(direction: ScrollDirection) {
    let alignments: [Alignment] = [.leading, .center, .trailing]
    let frames = direction.placeItems(
      [AxialSize](repeating: AxialSize(along: 10, across: 10), times: 3),
      alignments: alignments,
      gaps: [0, 0, 0, 0],
      slots: CrossAxisSlots(leading: 2, size: 50)
    )

    #expect(frames.map { direction.across($0.origin) } == [2, 22, 42])
  }

  @Test(arguments: ScrollDirection.allCases)
  func placeItems_withColumns_placesItemInShortestColumn(direction: ScrollDirection) {
    let frames = direction.placeItems(
      [
        AxialSize(along: 100, across: 20),
        AxialSize(along: 10, across: 20),
        AxialSize(along: 10, across: 20),
      ],
      alignments: [.leading, .leading, .leading],
      gaps: [0, 0, 0, 0],
      slots: CrossAxisSlots(leading: 1, size: 20, spacing: 4, count: 2)
    )

    // The first column becomes longer than the second one, so both remaining items go to the
    // second.
    #expect(frames == [
      direction.rect(along: (origin: 0, size: 100), across: (origin: 1, size: 20)),
      direction.rect(along: (origin: 0, size: 10), across: (origin: 25, size: 20)),
      direction.rect(along: (origin: 10, size: 10), across: (origin: 25, size: 20)),
    ])
  }
}
