@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import XCTest

final class DivTabsExtensionsTests: XCTestCase {
  func test_RebuildBeforeViewCreation_PreservesRelativeScroll() throws {
    let storage = DivBlockStateStorage()
    let context = DivBlockModelingContext(blockStateStorage: storage)
    let div = try XCTUnwrap(divTabs(items: [
      divTabsItem(div: divText(text: "First"), title: "First"),
      divTabsItem(div: divText(text: "Second"), title: "Second"),
    ]).value as? DivTabs)
    let request = ScrollRequest(duration: 2, delta: .items(1, wraps: false))
    storage.setPendingState(.root + "tabs", state: TabViewState(
      selectedPageIndex: 0, countOfPages: 2, animated: false, scrollRequest: request
    ))
    _ = try div.makeBlock(context: context)

    let wrapper = try XCTUnwrap(div.makeBlock(context: context) as? WrapperBlock)
    let tabs = try XCTUnwrap(wrapper.child as? TabsBlock)
    let view = TabContentsView(scrollClock: TestScrollAnimationClock())
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    view.configure(
      model: tabs.model.contentsModel,
      state: tabs.state,
      observer: storage,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    view.layoutIfNeeded()

    XCTAssertEqual(view.selectedPageIndex, 1)
    XCTAssertEqual(tabs.state.scrollRequest?.duration, 2)
    XCTAssertFalse(tabs.state.animated)
  }

  func test_WhenSwitchByContentTabsIsDisabled_ScrollingDisabledInContentsModel() throws {
    let block = try makeBlock(fromFile: "switch_by_content_swipe_disabled") as? WrapperBlock
    let tabsBlock = block?.child as? TabsBlock

    XCTAssertEqual(tabsBlock?.model.contentsModel.scrollingEnabled, false)
  }

  func test_ScrollingIsEnabledInContentsModelByDefault() throws {
    let block = try makeBlock(fromFile: "switch_by_content_swipe_enabled") as? WrapperBlock
    let tabsBlock = block?.child as? TabsBlock

    XCTAssertEqual(tabsBlock?.model.contentsModel.scrollingEnabled, true)
  }

  func test_AddsIndexedParentPathToItems() throws {
    let block = try makeBlock(fromFile: "switch_by_content_swipe_enabled") as? WrapperBlock
    let tabsBlock = block?.child as? TabsBlock

    let titlePath = tabsBlock?.model.listModel.tabTitles.first?.path
    let contentPath = tabsBlock?.model.contentsModel.path
    let pagePath = tabsBlock?.model.contentsModel.pages.first?.path

    // We are using "tabs" const instead of DivTabs.type to emphasise its importance for analytics.
    // DivTabs.type changes can brake analytic reports.
    XCTAssertEqual(titlePath, UIElementPath.root + "tabs" + "title0")
    XCTAssertEqual(contentPath, UIElementPath.root + "tabs")
    XCTAssertEqual(pagePath, UIElementPath.root + "tabs" + 0)
  }
}

private func makeBlock(fromFile filename: String) throws -> Block {
  try DivTabs.make(
    fromFile: filename,
    subdirectory: "div-tabs",
    context: .default
  )
}
