@testable @_spi(Internal) import DivKit
@testable import LayoutKit
import XCTest

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

  func test_updateVisibilityTimer_PutsTimer() {
    let timer = makeTimer()

    cache.updateVisibilityTimer(for: "path", timer: timer)

    XCTAssertTrue(cache.visibilityTimer(for: "path")?.timer === timer.timer)
  }

  func test_dropVisibleBounds_RemovesTimersWithGivenPrefix() {
    let timer = makeTimer()
    cache.updateVisibilityTimer(for: "root" + "container" + "appear", timer: timer)
    cache.updateVisibilityTimer(for: "another_root" + "appear", timer: timer)

    cache.dropVisibleBounds(prefix: "root" + "container")

    XCTAssertNil(cache.visibilityTimer(for: "root" + "container" + "appear"))
    XCTAssertTrue(cache.visibilityTimer(for: "another_root" + "appear")?.timer === timer.timer)
  }

  func test_reset_RemovesTimers() {
    cache.updateVisibilityTimer(for: "path", timer: makeTimer())

    cache.reset()

    XCTAssertNil(cache.visibilityTimer(for: "path"))
  }

  private func makeTimer() -> PendingVisibilityTimer {
    PendingVisibilityTimer(
      timer: TestTimer(timeInterval: 1, block: {}, repeating: false),
      pause: nil
    )
  }
}
