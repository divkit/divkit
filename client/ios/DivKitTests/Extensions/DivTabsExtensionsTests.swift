import XCTest

import DivKit
import LayoutKit

final class DivTabsExtensionsTests: XCTestCase {
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
  try DivTabsTemplate.make(
    fromFile: filename,
    subdirectory: "div-tabs",
    context: .default
  )
}
