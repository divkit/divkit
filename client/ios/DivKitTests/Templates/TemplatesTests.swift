@testable import DivKit

import XCTest

import Serialization

final class TemplatesTests: XCTestCase {
  func test_TextCardWithTemplates() throws {
    let data = try RawDivData(file: "text_card_with_template")

    let resolved = try data.resolve().unwrap()

    XCTAssertEqual(resolved, TemplatesTest.text)
  }

  func test_TextCardWithoutTemplates() throws {
    let data = try RawDivData(file: "text_card_without_template")

    let resolved = try data.resolve().unwrap()

    XCTAssertEqual(resolved, TemplatesTest.text)
  }

  func test_GalleryWithTemplate() throws {
    let data = try RawDivData(file: "gallery_with_template")

    let resolved = try data.resolve().unwrap()

    XCTAssertEqual(resolved, TemplatesTest.gallery)
  }

  func test_GalleryWithoutTemplate() throws {
    let data = try RawDivData(file: "gallery_without_template")

    let resolved = try data.resolve().unwrap()

    XCTAssertEqual(resolved, TemplatesTest.gallery)
  }
}
