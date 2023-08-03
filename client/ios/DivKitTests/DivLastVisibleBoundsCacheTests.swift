import DivKit
import LayoutKit
import XCTest

final class DivLastVisibleBoundsCacheTests: XCTestCase {
  let cache = DivLastVisibleBoundsCache()
    
  func test_WhenCacheIsEmpty_RetrievesZeroFromCache() {
    XCTAssertEqual(cache.lastVisibleBounds(for: "path"), .zero)
  }

  func test_WhenUpdateLastVisibleBounds_RetrievesUpdatedValueFromCache() {
    let rect = CGRect(origin: CGPoint(x: 10, y: 10), size: CGSize(squareDimension: 10))

    cache.updateLastVisibleBounds(for: "path", bounds: rect)

    XCTAssertEqual(cache.lastVisibleBounds(for: "path"), rect)
  }

  func test_WhenReset_RetrievesZeroFromCache() {
    let rect = CGRect(origin: CGPoint(x: 10, y: 10), size: CGSize(squareDimension: 10))

    cache.updateLastVisibleBounds(for: "path", bounds: rect)
    cache.reset()

    XCTAssertEqual(cache.lastVisibleBounds(for: "path"), .zero)
  }

  func test_WhenDropVisibleBoundsWithMatchingPrefix_ClearsAllChildPaths() {
    let rect1 = CGRect(origin: CGPoint(x: 10, y: 10), size: CGSize(squareDimension: 10))
    let rect2 = CGRect(origin: .zero, size: CGSize(squareDimension: 10))
    let rect3 = CGRect(origin: .zero, size: CGSize(squareDimension: 20))
    let parentPath: UIElementPath = "path"
    let parentPath2: UIElementPath = "path2"
    let childPath: UIElementPath = parentPath + "child"
    let nestedChildPath: UIElementPath = childPath + "nestedChild"

    cache.updateLastVisibleBounds(for: parentPath, bounds: rect1)
    cache.updateLastVisibleBounds(for: parentPath2, bounds: rect1)
    cache.updateLastVisibleBounds(for: childPath, bounds: rect2)
    cache.updateLastVisibleBounds(for: nestedChildPath, bounds: rect3)

    cache.dropVisibleBounds(forMatchingPrefix: parentPath)

    XCTAssertEqual(cache.lastVisibleBounds(for: childPath), .zero)
    XCTAssertEqual(cache.lastVisibleBounds(for: nestedChildPath), .zero)
    XCTAssertEqual(cache.lastVisibleBounds(for: parentPath2), rect1)
    XCTAssertEqual(cache.lastVisibleBounds(for: parentPath), .zero)
  }
}
