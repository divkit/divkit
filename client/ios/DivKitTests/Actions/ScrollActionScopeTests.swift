@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import XCTest

final class ScrollActionScopeTests: ScrollActionTestCase {
  func test_ScrollTo_PendingTarget_NotClobberedByObserverWrite_Gallery() {
    setState(galleryState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(0))

    blockStateStorage.setState(
      path: elementPath,
      state: GalleryViewState(
        contentPosition: .paging(index: 1.7),
        itemsCount: 5,
        isScrolling: true,
        animated: true
      )
    )

    assertScrollStateEqual(galleryState(index: 0, itemCount: 5), getState())
  }

  func test_ScrollTo_PendingTarget_NotClobberedByObserverWrite_Pager() {
    setState(pagerState(index: 2, itemCount: 5))

    handleScrollTo(indexDestination(0))

    blockStateStorage.setState(
      path: elementPath,
      state: PagerViewState(numberOfPages: 5, currentPage: 1, animated: true)
    )

    assertScrollStateEqual(pagerState(index: 0, itemCount: 5), getState())
  }

  func test_ScrollTo_TwoActionsBeforeRebuild_LastWins() {
    setState(galleryState(index: 0, itemCount: 5))

    handleScrollTo(indexDestination(3))
    handleScrollTo(indexDestination(1))

    assertScrollStateEqual(galleryState(index: 1, itemCount: 5), getState())
  }

  func test_ScrollTo_withScopeId_firstScope_updatesOnlyFirstElement() {
    let layout = makeScopedScrollLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "first",
        typed: .divActionScrollTo(
          DivActionScrollTo(
            animated: .value(true),
            destination: indexDestination(2),
            id: .value("gallery")
          )
        )
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    XCTAssertNil(layout.reporter.lastError)
    assertScrollStateEqual(
      galleryState(index: 2, itemCount: 5),
      layout.blockStateStorage.peekPendingState(layout.firstGalleryPath) as? GalleryViewState
    )
    XCTAssertNil(layout.blockStateStorage.peekPendingState(layout.secondGalleryPath))
  }

  func test_ScrollTo_withScopeId_secondScope_updatesOnlySecondElement() {
    let layout = makeScopedScrollLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "second",
        typed: .divActionScrollTo(
          DivActionScrollTo(
            animated: .value(true),
            destination: indexDestination(3),
            id: .value("gallery")
          )
        )
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    XCTAssertNil(layout.reporter.lastError)
    assertScrollStateEqual(
      galleryState(index: 3, itemCount: 5),
      layout.blockStateStorage.peekPendingState(layout.secondGalleryPath) as? GalleryViewState
    )
    XCTAssertNil(layout.blockStateStorage.peekPendingState(layout.firstGalleryPath))
  }

  func test_ScrollBy_withScopeId_firstScope_updatesOnlyFirstElement() {
    let layout = makeScopedScrollLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "first",
        typed: .divActionScrollBy(
          DivActionScrollBy(
            animated: .value(true),
            id: .value("gallery"),
            itemCount: .value(1),
            offset: .value(0),
            overflow: .value(.clamp)
          )
        )
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    XCTAssertNil(layout.reporter.lastError)
    assertScrollStateEqual(
      galleryState(index: 0, itemCount: 5),
      layout.blockStateStorage.peekPendingState(layout.firstGalleryPath) as? GalleryViewState
    )
    XCTAssertNil(layout.blockStateStorage.peekPendingState(layout.secondGalleryPath))
  }

  func test_ScrollAction_fromUrl_withScopeId_secondScope_updatesOnlySecondElement() {
    let layout = makeScopedScrollLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "second",
        url: "div-action://set_next_item?id=gallery&step=2"
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    XCTAssertNil(layout.reporter.lastError)
    assertScrollStateEqual(
      galleryState(index: 0, itemCount: 5),
      layout.blockStateStorage.peekPendingState(layout.secondGalleryPath) as? GalleryViewState
    )
    XCTAssertNil(layout.blockStateStorage.peekPendingState(layout.firstGalleryPath))
  }

  func test_ScrollTo_withScopeId_pager_updatesOnlyScopedElement() {
    let layout = makeScopedScrollLayout(
      initialState: pagerState(index: 0, itemCount: 5)
    )

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "first",
        typed: .divActionScrollTo(
          DivActionScrollTo(
            animated: .value(true),
            destination: indexDestination(3),
            id: .value("gallery")
          )
        )
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    XCTAssertNil(layout.reporter.lastError)
    assertScrollStateEqual(
      pagerState(index: 3, itemCount: 5),
      layout.blockStateStorage.peekPendingState(layout.firstGalleryPath) as? PagerViewState
    )
    XCTAssertNil(layout.blockStateStorage.peekPendingState(layout.secondGalleryPath))
  }

  func test_ScrollTo_withoutScopeId_ambiguousId_reportsErrorAndDoesNotMutate() {
    let layout = makeScopedScrollLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionScrollTo(
          DivActionScrollTo(
            animated: .value(true),
            destination: indexDestination(2),
            id: .value("gallery")
          )
        )
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    XCTAssertNotNil(layout.reporter.lastError)
    XCTAssertNil(layout.blockStateStorage.peekPendingState(layout.firstGalleryPath))
    XCTAssertNil(layout.blockStateStorage.peekPendingState(layout.secondGalleryPath))
  }

  private func makeScopedScrollLayout(
    initialState: ElementState = galleryState(index: 0, itemCount: 5)
  ) -> ScopedScrollLayout {
    let blockStateStorage = DivBlockStateStorage()
    let idToPath = IdToPath()
    let firstScopePath = cardId.path + "container" + "0" + "first"
    let secondScopePath = cardId.path + "container" + "1" + "second"
    let firstGalleryPath = firstScopePath + "0" + "gallery"
    let secondGalleryPath = secondScopePath + "0" + "gallery"

    idToPath.add(firstScopePath, forId: "first", viewId: hostViewId)
    idToPath.add(secondScopePath, forId: "second", viewId: hostViewId)
    idToPath.add(firstGalleryPath, forId: "gallery", viewId: hostViewId)
    idToPath.add(secondGalleryPath, forId: "gallery", viewId: hostViewId)

    blockStateStorage.setState(path: firstGalleryPath, state: initialState)
    blockStateStorage.setState(path: secondGalleryPath, state: initialState)

    let reporter = MockReporter()
    let handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      idToPath: idToPath,
      reporter: reporter,
      updateCard: { _ in }
    )

    return ScopedScrollLayout(
      blockStateStorage: blockStateStorage,
      handler: handler,
      reporter: reporter,
      firstGalleryPath: firstGalleryPath,
      secondGalleryPath: secondGalleryPath
    )
  }
}

private struct ScopedScrollLayout {
  let blockStateStorage: DivBlockStateStorage
  let handler: DivActionHandler
  let reporter: MockReporter
  let firstGalleryPath: UIElementPath
  let secondGalleryPath: UIElementPath
}
