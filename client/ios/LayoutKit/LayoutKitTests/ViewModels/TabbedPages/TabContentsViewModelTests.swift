import Foundation
@testable import LayoutKit
import XCTest

final class TabContentsViewModelTests: XCTestCase {
  private func makeMe(pages: [TabPageViewModel]) throws -> TabContentsViewModel {
    try TabContentsViewModel(pages: pages, pagesHeight: .default)
  }

  func test_WhenCreatingFromEmptyTabs_ThrowsMissingChildrenError() {
    XCTAssertThrowsError(try makeMe(pages: []), BlockError("Tab error: no children provided"))
  }
}
