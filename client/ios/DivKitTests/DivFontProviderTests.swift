@testable import DivKit
import XCTest

final class DivFontProviderTests: XCTestCase {
  private var fontProvider: DivFontProvider = DefaultFontProvider()

  func test_withDefaultWeight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .regular),
      fontProvider.font(size: 20)
    )
  }

  func test_withBoldWeight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .bold),
      fontProvider.font(weight: .bold, size: 20)
    )
  }

  func test_withLightWeight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .light),
      fontProvider.font(weight: .light, size: 20)
    )
  }

  func test_with0Weight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .regular),
      fontProvider.font(family: "default", weight: 0, size: 20)
    )
  }

  func test_with100Weight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .light),
      fontProvider.font(family: "default", weight: 100, size: 20)
    )
  }

  func test_with400Weight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .regular),
      fontProvider.font(family: "default", weight: 400, size: 20)
    )
  }

  func test_with450Weight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .medium),
      fontProvider.font(family: "default", weight: 450, size: 20)
    )
  }

  func test_with500Weight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .medium),
      fontProvider.font(family: "default", weight: 500, size: 20)
    )
  }

  func test_with1000Weight() {
    XCTAssertEqual(
      .systemFont(ofSize: 20, weight: .bold),
      fontProvider.font(family: "default", weight: 1000, size: 20)
    )
  }
}
