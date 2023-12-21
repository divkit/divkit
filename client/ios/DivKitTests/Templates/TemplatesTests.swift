@testable import DivKit

import XCTest

import Serialization

final class TemplatesTests: XCTestCase {
  func test_Text_WithTemplates() throws {
    let data = try RawDivData(file: "text_card_with_template")

    let resolved = try data.resolve().unwrap()

    assertEqual(resolved, text)
  }

  func test_Text_WithoutTemplates() throws {
    let data = try RawDivData(file: "text_card_without_template")

    let resolved = try data.resolve().unwrap()

    assertEqual(resolved, text)
  }

  func test_Gallery_WithTemplate() throws {
    let data = try RawDivData(file: "gallery_with_template")

    let resolved = try data.resolve().unwrap()

    assertEqual(resolved, gallery)
  }

  func test_Gallery_WithoutTemplate() throws {
    let data = try RawDivData(file: "gallery_without_template")

    let resolved = try data.resolve().unwrap()

    assertEqual(resolved, gallery)
  }
}

private let text = divData(
  logId: "test",
  states: [DivData.State(
    div: divText(
      fontSize: 14,
      fontWeight: .bold,
      text: "Lorem ipsum dolor sit amet"
    ),
    stateId: 0
  )]
)

private let gallery = divData(
  logId: "test",
  states: [DivData.State(
    div: divGallery(
      items: (0..<3).map { _ in
        divContainer(
          height: divFixedSize(320),
          items: [
            divText(
              fontSize: 16,
              text: "Title"
            ),
            divText(
              fontSize: 12,
              text: "Lorem ipsum dolor sit amet"
            ),
            divImage(
              height: divFixedSize(100),
              imageUrl: "https://image.url",
              width: divFixedSize(100)
            ),
          ],
          width: divFixedSize(260)
        )
      }
    ),
    stateId: 1
  )]
)
