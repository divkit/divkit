import Foundation
import VGSL

final class ScrollHandler {
  enum Mode {
    case free
    case fixedSizePaging
    case layoutPaging(step: Step)

    var isPagingMode: Bool {
      if case .free = self {
        false
      } else {
        true
      }
    }

    var isSinglePageMode: Bool {
      if case .layoutPaging(step: .singlePage) = self {
        true
      } else {
        false
      }
    }
  }

  enum Step {
    case singlePage
    case multiplePages
  }

  private enum ScrollState {
    case idle
    case dragging(startOffset: CGFloat)
    case settling(trigger: ScrollTrigger)

    var isDragging: Bool {
      guard case .dragging = self else { return false }
      return true
    }

    var isSettling: Bool {
      guard case .settling = self else { return false }
      return true
    }

    var isUserInitiatedScroll: Bool {
      switch self {
      case .idle, .settling(trigger: .programmatic):
        false
      case .dragging, .settling(trigger: .user):
        true
      }
    }
  }

  private enum ScrollTrigger {
    case user(startOffset: CGFloat, targetOffset: CGFloat)
    case programmatic
  }

  var mode: Mode = .free
  var direction: ScrollDirection = .horizontal
  weak var delegate: ScrollHandlerDelegate?

  var layout: GalleryViewLayouting

  private var infiniteScroll: InfiniteScroll?
  private var contentPager: ScrollableContentPager?

  private var state: ScrollState = .idle {
    didSet {
      if state.isSettling, oldValue.isDragging {
        infiniteScroll?.isPerformed = false
      }
    }
  }

  init(layout: GalleryViewLayouting) {
    self.layout = layout
  }

  func configurePager(
    pageOrigins: [CGFloat],
    isHorizontal: Bool,
    initialContentOffset: CGFloat? = nil
  ) {
    if contentPager == nil {
      contentPager = ScrollableContentPager()
    }
    contentPager?.setPageOrigins(
      pageOrigins,
      withPagingEnabled: true,
      isHorizontal: isHorizontal
    )
    let offset = initialContentOffset ?? pageOrigins.first ?? .zero
    contentPager?.setInitialOffset(offset)
  }

  func clearPager() {
    contentPager = nil
  }

  func configureInfiniteScroll(
    enabled: Bool,
    bufferSize: Int,
    boundsSize: CGFloat,
    alignment: Alignment,
    insetMode: InsetMode
  ) {
    guard enabled, !layout.blockFrames.isEmpty else {
      infiniteScroll = nil
      return
    }
    infiniteScroll = InfiniteScroll(
      origins: layout.blockFrames.map { direction.isHorizontal ? $0.minX : $0.minY },
      bufferSize: bufferSize,
      boundsSize: boundsSize,
      alignment: alignment,
      insetMode: insetMode
    )
  }

  func handlePositionChange(
    oldPosition: GalleryViewState.Position?,
    newPosition: GalleryViewState.Position,
    newLayout: Bool,
    animated: Bool
  ) {
    guard !state.isUserInitiatedScroll, oldPosition != newPosition else {
      return
    }

    state = .settling(trigger: .programmatic)

    let position = infiniteScroll?.getNewPositionForState(
      oldPosition: oldPosition,
      newPosition: newPosition,
      updateToPosition: { index in
        delegate?.updateOffsetDetached(layout.contentOffset(pageIndex: index))
      }
    ) ?? newPosition

    if newLayout {
      delegate?.updateOffsetDetached(position)
    } else {
      delegate?.updateOffsetToPosition(position, animated: animated)
    }
  }

  private func getOffset(_ scrollView: ScrollView) -> CGFloat {
    direction.isHorizontal
      ? scrollView.contentOffset.x
      : scrollView.contentOffset.y
  }
}

extension ScrollHandler: ScrollDelegate {
  func onWillBeginDragging(_ scrollView: ScrollView) {
    let offset = getOffset(scrollView)
    contentPager?.setInitialOffset(offset)
    state = .dragging(startOffset: offset)
  }

  func onWillEndDragging(
    _: ScrollView,
    withVelocity velocity: CGPoint,
    targetContentOffset: UnsafeMutablePointer<CGPoint>
  ) {
    guard case let ScrollState.dragging(startOffset: startOffset) = state else {
      return
    }

    var proposedOffset: CGFloat {
      direction.isHorizontal ? targetContentOffset.pointee.x : targetContentOffset.pointee.y
    }

    defer {
      state = .settling(
        trigger: .user(startOffset: startOffset, targetOffset: proposedOffset)
      )
    }

    guard let contentPager else {
      return
    }

    switch mode {
    case .free:
      return
    case .fixedSizePaging:
      targetContentOffset.pointee = CGPoint(
        forProjection: direction.isHorizontal,
        value: contentPager.targetPageOffset(
          forProposedOffset: proposedOffset,
          velocity: velocity.projection(direction.isHorizontal)
        ) ?? proposedOffset
      )
    case let .layoutPaging(step):
      if step == .singlePage {
        targetContentOffset.pointee = CGPoint(
          forProjection: direction.isHorizontal,
          value: contentPager.targetPageOffsetForSinglePageStep(
            startOffset: startOffset,
            proposedOffset: proposedOffset,
            velocity: velocity.projection(direction.isHorizontal)
          )
        )
      } else {
        targetContentOffset.pointee = CGPoint(
          forProjection: direction.isHorizontal,
          value: contentPager.targetPageOffset(
            forProposedOffset: proposedOffset,
            velocity: velocity.projection(direction.isHorizontal)
          ) ?? proposedOffset
        )
      }
    }
  }

  func onDidScroll(_ scrollView: ScrollView) {
    let offset = getOffset(scrollView)

    switch state {
    case .dragging:
      evaluatePagingDuringDrag(offset: offset)

    case let .settling(.user(startOffset, finalOffset)):
      loopIfNeeded(
        offset: offset,
        draggingStartOffset: startOffset,
        proposedOffset: finalOffset
      )

    case .idle, .settling(trigger: .programmatic):
      break
    }

    updateState(offset: getOffset(scrollView))
  }

  private func updateState(offset: CGFloat) {
    if mode.isPagingMode {
      delegate?.updateGalleryState(
        .paging(index: layout.pageIndex(forContentOffset: offset)), offset: offset
      )
    } else {
      delegate?.updateGalleryState(
        .offset(
          offset,
          firstVisibleItemIndex: Int(layout.pageIndex(forContentOffset: offset))
        ),
        offset: offset
      )
    }
  }

  func onDidEndDragging(_: ScrollView, willDecelerate decelerate: Bool) {
    if !decelerate {
      finishScrolling()
    }
  }

  func onDidEndDecelerating(_: ScrollView) {
    finishScrolling()
  }

  func onDidEndScrollingAnimation(_: ScrollView) {
    finishScrolling()
  }

  private func evaluatePagingDuringDrag(offset: CGFloat) {
    guard case let ScrollState.dragging(startOffset) = state,
          mode.isSinglePageMode else {
      return
    }

    let startPage = layout.pageIndex(forContentOffset: startOffset)
    let currentPage = layout.pageIndex(forContentOffset: offset)
    if currentPage >= startPage + 1 || currentPage <= startPage - 1 {
      finishScrolling(shouldInterrupt: true)
    }
  }

  private func finishScrolling(shouldInterrupt: Bool = false) {
    if shouldInterrupt {
      delegate?.interruptScrolling()
    }

    let startOffset: CGFloat? = switch state {
    case let .dragging(startOffset):
      startOffset
    case let .settling(.user(startOffset, _)):
      startOffset
    case .idle, .settling(.programmatic):
      nil
    }

    delegate?.finishScrolling(scrollStartOffset: startOffset)
    state = .idle
  }

  private func loopIfNeeded(
    offset: CGFloat,
    draggingStartOffset: CGFloat,
    proposedOffset: CGFloat
  ) {
    guard let infiniteScroll,
          !infiniteScroll.isPerformed,
          let newPosition = infiniteScroll.getNewPosition(currentOffset: offset) else { return }

    let diff = offset - newPosition.offset
    let newOffset = newPosition.offset
    let newDraggingStartOffset = draggingStartOffset - diff

    delegate?.updateOffsetDetached(newOffset)

    self.infiniteScroll?.isPerformed = true
    state = .settling(trigger: .user(
      startOffset: newDraggingStartOffset,
      targetOffset: proposedOffset
    ))

    delegate?.updateOffsetToPosition(
      .paging(index: layout.pageIndex(forContentOffset: proposedOffset - diff).rounded())
    )
  }
}

protocol ScrollHandlerDelegate: AnyObject {
  func updateOffsetDetached(_ offset: CGFloat)
  func updateOffsetDetached(_ pos: GalleryViewState.Position)
  func updateOffsetToPosition(_ pos: GalleryViewState.Position, animated: Bool)
  func updateGalleryState(_ pos: GalleryViewState.Position, offset: CGFloat)
  func interruptScrolling()
  func finishScrolling(scrollStartOffset: CGFloat?)
}

extension ScrollHandlerDelegate {
  func updateOffsetToPosition(_ pos: GalleryViewState.Position) {
    updateOffsetToPosition(pos, animated: true)
  }
}

extension CGPoint {
  fileprivate init(forProjection isHorizontal: Bool, value: CGFloat) {
    if isHorizontal {
      self.init(x: value, y: 0)
    } else {
      self.init(x: 0, y: value)
    }
  }

  fileprivate func projection(_ isHorizontal: Bool) -> CGFloat {
    isHorizontal ? x : y
  }
}
