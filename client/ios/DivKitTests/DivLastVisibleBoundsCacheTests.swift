import XCTest

@testable import DivKit
import LayoutKit

final class DivLastVisibleBoundsCacheTests: XCTestCase {
  private let cache = DivLastVisibleBoundsCache()

  func test_lastVisibleArea_ReturnsZeroFromEmptyCache() {
    XCTAssertEqual(cache.lastVisibleArea(for: "path"), 0)
  }

  func test_updateLastVisibleArea_PutsValue() {
    cache.updateLastVisibleArea(for: "path", area: 12)

    XCTAssertEqual(cache.lastVisibleArea(for: "path"), 12)
  }

  func test_reset_ResetsCache() {
    cache.updateLastVisibleArea(for: "path", area: 12)

    cache.reset()

    XCTAssertEqual(cache.lastVisibleArea(for: "path"), 0)
  }

  func test_dropVisibleBounds_RemovesValuesWithGivenPrefix() {
    cache.updateLastVisibleArea(for: "root", area: 50)
    cache.updateLastVisibleArea(for: "root" + "container", area: 75)
    cache.updateLastVisibleArea(for: "root" + "container" + "element", area: 100)
    cache.updateLastVisibleArea(for: "another_root", area: 10)

    cache.dropVisibleBounds(prefix: "root" + "container")

    XCTAssertEqual(cache.lastVisibleArea(for: "root" + "container"), 0)
    XCTAssertEqual(cache.lastVisibleArea(for: "root" + "container" + "element"), 0)
  }

  func test_dropVisibleBounds_KeepsValuesWithDifferentPrefx() {
    cache.updateLastVisibleArea(for: "root", area: 50)
    cache.updateLastVisibleArea(for: "root" + "container", area: 75)
    cache.updateLastVisibleArea(for: "root" + "container" + "element", area: 100)
    cache.updateLastVisibleArea(for: "another_root", area: 10)

    cache.dropVisibleBounds(prefix: "root" + "container")

    XCTAssertEqual(cache.lastVisibleArea(for: "root"), 50)
    XCTAssertEqual(cache.lastVisibleArea(for: "another_root"), 10)
  }

  func test_onBecomeInvisible_RemovesValuesForPreviouslyVisibleElement() {
    cache.updateLastVisibleArea(for: "root", area: 50)
    cache.updateLastVisibleArea(for: "root" + "container", area: 75)

    cache.onBecomeInvisible("root")

    XCTAssertEqual(cache.lastVisibleArea(for: "root"), 0)
    XCTAssertEqual(cache.lastVisibleArea(for: "root" + "container"), 0)
  }

  func test_onBecomeInvisible_DoesNotRemoveValuesForPreviouslyInvisibleElement() {
    cache.onBecomeInvisible("root")
    cache.updateLastVisibleArea(for: "root", area: 50)
    cache.updateLastVisibleArea(for: "root" + "container", area: 75)

    cache.onBecomeInvisible("root")

    XCTAssertEqual(cache.lastVisibleArea(for: "root"), 50)
    XCTAssertEqual(cache.lastVisibleArea(for: "root" + "container"), 75)
  }
}
