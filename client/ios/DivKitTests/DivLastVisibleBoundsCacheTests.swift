@testable import DivKit

import LayoutKit
import XCTest

final class DivLastVisibleBoundsCacheTests: XCTestCase {
  private let cache = DivLastVisibleBoundsCache()

  func test_WhenCacheIsEmpty_RetrievesZeroFromCache() {
    XCTAssertEqual(cache.lastVisibleArea(for: "path"), 0)
  }

  func test_WhenUpdateLastVisibleArea_RetrievesUpdatedValueFromCache() {
    cache.updateLastVisibleArea(for: "path", area: 12)

    XCTAssertEqual(cache.lastVisibleArea(for: "path"), 12)
  }

  func test_WhenReset_RetrievesZeroFromCache() {
    cache.updateLastVisibleArea(for: "path", area: 12)

    cache.reset()

    XCTAssertEqual(cache.lastVisibleArea(for: "path"), 0)
  }

  func test_WhenDropVisibleBoundsWithMatchingPrefix_ClearsAllChildPaths() {
    cache.updateLastVisibleArea(for: "root", area: 12)
    cache.updateLastVisibleArea(for: "another_root", area: 23)
    cache.updateLastVisibleArea(for: "root" + "child", area: 34)
    cache.updateLastVisibleArea(for: "root" + "child" + "nested_child", area: 45)

    cache.dropVisibleBounds(prefix: "root")

    XCTAssertEqual(cache.lastVisibleArea(for: "root"), 0)
    XCTAssertEqual(cache.lastVisibleArea(for: "root" + "child"), 0)
    XCTAssertEqual(cache.lastVisibleArea(for: "root" + "child" + "nested_child"), 0)
    XCTAssertEqual(cache.lastVisibleArea(for: "another_root"), 23)
  }
}
