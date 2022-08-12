// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation
import XCTest

import LayoutKit

final class TabContentsViewModelTests: XCTestCase {
  private func makeMe(pages: [TabPageViewModel]) throws -> TabContentsViewModel {
    try TabContentsViewModel(pages: pages, pagesHeight: .default)
  }

  func test_WhenCreatingFromEmptyTabs_ThrowsMissingChildrenError() {
    XCTAssertThrowsError(try makeMe(pages: []), TabError.missingChildren)
  }
}
