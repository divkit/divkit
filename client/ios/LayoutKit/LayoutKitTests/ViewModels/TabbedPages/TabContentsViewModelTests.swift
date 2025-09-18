import Foundation
@testable import LayoutKit
import XCTest

final class TabContentsViewModelTests: XCTestCase {
  func test_WhenCreatingFromEmptyTabs_ThrowsMissingChildrenError() {
    XCTAssertThrowsError(try makeMe(pages: []), BlockError("Tab error: no children provided"))
  }

  private func makeMe(pages: [TabPageViewModel]) throws -> TabContentsViewModel {
    try TabContentsViewModel(pages: pages, pagesHeight: .default)
  }
}
