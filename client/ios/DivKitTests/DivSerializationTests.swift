// Copyright 2022 Yandex LLC. All rights reserved.

@testable import DivKit

import XCTest

import CommonCore

final class DivSerializationTests: XCTestCase {
  func test_Serialize_Type() {
    let size = DivMatchParentSize()
    let dictionary = size.toDictionary()
    XCTAssertEqual(DivMatchParentSize.type, dictionary["type"] as! String)
  }

  func test_Serialize_StringValue() {
    let action = DivAction(
      logId: "Some logId"
    )
    let dictionary = action.toDictionary()
    XCTAssertEqual(action.logId, dictionary["log_id"] as! String)
  }

  func test_Serialize_StringExpression() {
    let menuItem = DivAction.MenuItem(
      text: makeLink("Text: @{some_var}")
    )
    let dictionary = menuItem.toDictionary()
    XCTAssertEqual("Text: @{some_var}", dictionary["text"] as! String)
  }

  func test_Serialize_CFStringText() {
    let text = DivText(
      text: .value("Some text" as CFString)
    )
    let dictionary = text.toDictionary()
    XCTAssertEqual("Some text", dictionary["text"] as! String)
  }

  func test_Serialize_CFStringExpression() {
    let text = DivText(
      text: makeLink("Text: @{some_var}")
    )
    let dictionary = text.toDictionary()
    XCTAssertEqual("Text: @{some_var}", dictionary["text"] as! String)
  }

  func test_Serialize_UrlValue() {
    let url = URL(string: "https://some/url?param=value")!
    let image = DivImage(
      imageUrl: .value(url)
    )
    let dictionary = image.toDictionary()
    XCTAssertEqual(url.absoluteString, dictionary["image_url"] as! String)
  }

  func test_Serialize_UrlExpression() {
    let image = DivImage(
      imageUrl: makeLink("https://@{some_var}")
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
      value: makeLink("@{some_var}")
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

  func test_Serialize_BoolIntValue() {
    let size = DivWrapContentSize(
      constrained: .value(true)
    )
    let dictionary = size.toDictionary()
    XCTAssertEqual(1, dictionary["constrained"] as! Int)
  }

  func test_Serialize_BoolIntExpression() {
    let size = DivWrapContentSize(
      constrained: makeLink("@{some_var}")
    )
    let dictionary = size.toDictionary()
    XCTAssertEqual("@{some_var}", dictionary["constrained"] as! String)
  }

  private func makeLink<T>(_ rawValue: String) -> Expression<T> {
    .link(ExpressionLink(rawValue: rawValue, validator: nil)!)
  }
}
