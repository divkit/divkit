import XCTest

import LayoutKit

final class UIElementPathTests: XCTestCase {
  func test_description() {
    let path = UIElementPath("root") + "container" + "element"
    XCTAssertEqual("root/container/element", path.description)
  }

  func test_description_SingleElement() {
    let path = UIElementPath("root")
    XCTAssertEqual("root", path.description)
  }

  func test_root() {
    let path = UIElementPath("root") + "container" + "element"
    XCTAssertEqual("root", path.root)
  }

  func test_root_SingleElement() {
    let path = UIElementPath("root")
    XCTAssertEqual("root", path.root)
  }

  func test_parent() {
    let containerPath = UIElementPath("root") + "container"
    let elementPath = UIElementPath("root") + "container" + "element"
    XCTAssertEqual(containerPath, elementPath.parent)
  }

  func test_parent_SingleElement() {
    XCTAssertNil(UIElementPath("root").parent)
  }

  func test_leaf() {
    let path = UIElementPath("root") + "container" + "element"
    XCTAssertEqual("element", path.leaf)
  }

  func test_leaf_SingleElement() {
    let path = UIElementPath("root")
    XCTAssertEqual("root", path.leaf)
  }

  func test_parse() {
    let path = UIElementPath.parse("root/container/element")
    XCTAssertEqual("root", path.root)
    XCTAssertEqual(UIElementPath("root") + "container", path.parent)
    XCTAssertEqual("element", path.leaf)
  }
}
