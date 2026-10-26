@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import UIKit
import VGSL
import XCTest

class ScrollActionTestCase: XCTestCase {
  let variablesStorage = DivVariablesStorage()
  let blockStateStorage = DivBlockStateStorage()
  var handler: DivActionHandler!

  var isUpdateCardCalled = false

  let cardId = DivBlockModelingContext.testCardId
  let end = DivActionScrollDestination.endDestination(EndDestination())
  let start = DivActionScrollDestination.startDestination(StartDestination())

  var elementPath: UIElementPath {
    cardId.path + "element_id"
  }

  var hostViewId: DivViewId { DivViewId(cardId: cardId) }

  override func setUp() {
    let idToPath = IdToPath()
    idToPath.add(elementPath, forId: "element_id", viewId: hostViewId)
    handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      idToPath: idToPath,
      updateCard: { [unowned self] _ in
        self.isUpdateCardCalled = true
      },
      variablesStorage: variablesStorage
    )
  }

  func handleScrollTo(
    _ destination: DivActionScrollDestination,
    animated: Bool = true,
    duration: DivKit.Expression<Int>? = nil
  ) {
    handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionScrollTo(
          DivActionScrollTo(
            animated: .value(animated),
            destination: destination,
            duration: duration,
            id: .value("element_id")
          )
        )
      ),
      path: cardId.path,
      source: .callback,
      sender: nil
    )
  }

  func handleScrollBy(
    itemCount: Int = 0,
    offset: Int = 0,
    overflow: DivActionScrollBy.Overflow = .clamp,
    animated: Bool = true,
    duration: DivKit.Expression<Int>? = nil
  ) {
    handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionScrollBy(
          DivActionScrollBy(
            animated: .value(animated),
            duration: duration,
            id: .value("element_id"),
            itemCount: .value(itemCount),
            offset: .value(offset),
            overflow: .value(overflow)
          )
        )
      ),
      path: cardId.path,
      source: .callback,
      sender: nil
    )
  }

  func getState<T: ElementState>() -> T {
    if let pending: T = blockStateStorage.peekPendingState(elementPath) as? T {
      return pending
    }
    return blockStateStorage.getState(elementPath)!
  }

  func setState(_ state: ElementState) {
    blockStateStorage.setState(
      path: elementPath,
      state: state
    )
  }
}

func offsetDestination(_ value: Int) -> DivActionScrollDestination {
  DivActionScrollDestination.offsetDestination(OffsetDestination(value: .value(value)))
}

func indexDestination(_ value: Int) -> DivActionScrollDestination {
  DivActionScrollDestination.indexDestination(IndexDestination(value: .value(value)))
}

func galleryState(index: Int, itemCount: Int, animated: Bool = true) -> GalleryViewState {
  GalleryViewState(
    contentPageIndex: CGFloat(index),
    itemsCount: itemCount,
    animated: animated
  )
}

func galleryState(
  offset: CGFloat,
  itemCount: Int,
  range: CGFloat,
  animated: Bool = true
) -> GalleryViewState {
  GalleryViewState(
    contentPosition: .offset(offset, firstVisibleItemIndex: 0),
    itemsCount: itemCount,
    isScrolling: false,
    scrollRange: range,
    animated: animated
  )
}

func pagerState(index: Int, itemCount: Int, animated: Bool = true) -> PagerViewState {
  PagerViewState(
    numberOfPages: itemCount,
    currentPage: index,
    animated: animated
  )
}

func pagerWithInfiniteScrollState(
  index: Int,
  itemCount: Int,
  animated: Bool = true
) -> PagerViewState {
  PagerViewState(
    numberOfPages: itemCount,
    currentPage: index,
    animated: animated,
    isInfiniteScrollable: true
  )
}

func tabState(index: Int, itemCount: Int) -> TabViewState {
  TabViewState(selectedPageIndex: CGFloat(index), countOfPages: itemCount)
}

protocol ScrollStateForTesting: ElementState, Equatable {
  var scrollRequest: ScrollRequest? { get }
  var withoutScrollRequest: Self { get }
}

extension GalleryViewState: ScrollStateForTesting {
  var withoutScrollRequest: GalleryViewState {
    GalleryViewState(
      contentPosition: contentPosition, itemsCount: itemsCount,
      isScrolling: isScrolling, scrollRange: scrollRange, animated: animated
    )
  }
}

extension PagerViewState: ScrollStateForTesting {
  var withoutScrollRequest: PagerViewState {
    PagerViewState(
      numberOfPages: numberOfPages, currentPage: Int(currentPage), animated: animated,
      isInfiniteScrollable: isInfiniteScrollable, isScrolling: isScrolling,
      navigationDirection: navigationDirection
    )
  }
}

extension TabViewState: ScrollStateForTesting {
  var withoutScrollRequest: TabViewState {
    TabViewState(
      selectedPageIndex: selectedPageIndex,
      countOfPages: countOfPages,
      animated: animated
    )
  }
}

func assertScrollStateEqual<State: ScrollStateForTesting>(
  _ actual: State?,
  _ expected: State?,
  file: StaticString = #filePath,
  line: UInt = #line
) {
  XCTAssertEqual(
    actual?.withoutScrollRequest,
    expected?.withoutScrollRequest,
    file: file,
    line: line
  )
  XCTAssertEqual(
    actual?.scrollRequest?.duration,
    expected?.scrollRequest?.duration,
    file: file,
    line: line
  )
}

func makeScrollTestTabs(
  path: UIElementPath,
  clock: TestScrollAnimationClock,
  observer: ElementStateObserver,
  frame: CGRect = CGRect(x: 0, y: 0, width: 100, height: 100)
) throws -> (TabContentsView, TabContentsViewModel) {
  let view = TabContentsView(scrollClock: clock)
  view.frame = frame
  let page = TextBlock(
    widthTrait: .fixed(100), heightTrait: .fixed(100), text: NSAttributedString(string: "Page")
  )
  let model = try TabContentsViewModel(
    pages: (0..<5).map { page.makeTabPage(with: path + "\($0)") },
    pagesHeight: .byHighestPage, path: path
  )
  view.configure(
    model: model, state: TabViewState(selectedPageIndex: 0, countOfPages: 5),
    observer: observer, overscrollDelegate: nil, renderingDelegate: nil
  )
  view.layoutIfNeeded()
  return (view, model)
}
