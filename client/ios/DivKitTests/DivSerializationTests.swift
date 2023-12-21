@testable import DivKit

import XCTest

import CommonCorePublic

final class DivSerializationTests: XCTestCase {
  func test_Serialize_Type() {
    let size = DivMatchParentSize()
    let dictionary = size.toDictionary()
    XCTAssertEqual(DivMatchParentSize.type, dictionary["type"] as! String)
  }

  func test_Serialize_StringValue() {
    let action = DivAction(
      logId: "action_log_id"
    )
    let dictionary = action.toDictionary()
    XCTAssertEqual("action_log_id", dictionary["log_id"] as! String)
  }

  func test_Serialize_StringExpression() {
    let menuItem = DivAction.MenuItem(
      text: expression("Text: @{some_var}")
    )
    let dictionary = menuItem.toDictionary()
    XCTAssertEqual("Text: @{some_var}", dictionary["text"] as! String)
  }

  func test_Serialize_UrlValue() {
    let image = DivImage(
      imageUrl: .value(url("https://some/url?param=value"))
    )
    let dictionary = image.toDictionary()
    XCTAssertEqual("https://some/url?param=value", dictionary["image_url"] as! String)
  }

  func test_Serialize_UrlExpression() {
    let image = DivImage(
      imageUrl: expression("https://@{some_var}")
    )
    let dictionary = image.toDictionary()
    XCTAssertEqual("https://@{some_var}", dictionary["image_url"] as! String)
  }

  func test_Serialize_IntValue() {
    let size = DivFixedSize(
      value: .value(123)
    )
    let dictionary = size.toDictionary()
    XCTAssertEqual(123, dictionary["value"] as! Int)
  }

  func test_Serialize_IntExpression() {
    let size = DivFixedSize(
      value: expression("@{some_var}")
    )
    let dictionary = size.toDictionary()
    XCTAssertEqual("@{some_var}", dictionary["value"] as! String)
  }

  func test_Serialize_DoubleValue() {
    let variable = NumberVariable(
      name: "var",
      value: 123.45
    )
    let dictionary = variable.toDictionary()
    XCTAssertEqual(123.45, dictionary["value"] as! Double)
  }

  func test_Serialize_BooleanIntValue() {
    let size = DivWrapContentSize(
      constrained: .value(true)
    )
    let dictionary = size.toDictionary()
    XCTAssertTrue(dictionary["constrained"] as! Bool)
  }

  func test_Serialize_BooleanIntExpression() {
    let size = DivWrapContentSize(
      constrained: expression("@{some_var}")
    )
    let dictionary = size.toDictionary()
    XCTAssertEqual("@{some_var}", dictionary["constrained"] as! String)
  }

  func test_Serialize_ArrayValue() {
    let variable = ArrayVariable(
      name: "var",
      value: [1, "aba", 2.3]
    )
    let dictionary = variable.toDictionary()
    XCTAssertEqual([1, "aba", 2.3], dictionary["value"] as! [AnyHashable])
  }
}
