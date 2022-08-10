import CoreGraphics

struct IndicatorState {
  enum Kind {
    case normal, highlighted
  }

  let index: Int
  let kind: Kind
  let progress: CGFloat

  init(index: Int, currentPosition: CGFloat, params: PageIndicatorLayerParams, numberOfPages: Int) {
    self.index = index
    let floatIndex = CGFloat(index)

    let positionDiff = abs(floatIndex - currentPosition)
    guard positionDiff >= 1, numberOfPages > 1 else {
      progress = 1 - positionDiff
      kind = .highlighted
      return
    }

    guard numberOfPages > params.visiblePageCount else {
      progress = 1
      kind = .normal
      return
    }

    let clampedIndex = floatIndex.clamp(1...CGFloat(numberOfPages - 2))
    let scrollOffsetProgress = params.scrollOffsetProgress
    let headIndexOffset = CGFloat(params.head) + scrollOffsetProgress
    let tailIndexOffset = CGFloat(params.tail) + scrollOffsetProgress

    let minEdgeIndexOffset = min(
      clampedIndex - headIndexOffset,
      tailIndexOffset - clampedIndex
    )
    progress = minEdgeIndexOffset.clamp(0...1)
    kind = .normal
  }
}
