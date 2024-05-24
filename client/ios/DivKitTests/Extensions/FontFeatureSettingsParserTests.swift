@testable import DivKit

import XCTest

final class FontFeatureSettingsParserTests: XCTestCase {
  func test_singleQuotes() {
    let font = font.withFontFeatureSettings("'frac'")

    XCTAssertEqual(
      font.featureSettings,
      [[.type: kFractionsType, .selector: kDiagonalFractionsSelector]]
    )
  }

  func test_doubleQuotes() {
    let font = font.withFontFeatureSettings("\"frac\"")

    XCTAssertEqual(
      font.featureSettings,
      [[.type: kFractionsType, .selector: kDiagonalFractionsSelector]]
    )
  }

  func test_noQuotes() {
    let font = font.withFontFeatureSettings("frac")

    XCTAssertNil(font.featureSettings)
  }

  func test_invalidFormat() {
    let font = font.withFontFeatureSettings("'frac' on off")

    XCTAssertNil(font.featureSettings)
  }

  func test_enabledWithOn() {
    let font = font.withFontFeatureSettings("'frac' on")

    XCTAssertEqual(
      font.featureSettings,
      [[.type: kFractionsType, .selector: kDiagonalFractionsSelector]]
    )
  }

  func test_enabledWith1() {
    let font = font.withFontFeatureSettings("'frac' 1")

    XCTAssertEqual(
      font.featureSettings,
      [[.type: kFractionsType, .selector: kDiagonalFractionsSelector]]
    )
  }

  func test_disabledWithOff() {
    let font = font.withFontFeatureSettings("'frac' off")

    XCTAssertNil(font.featureSettings)
  }

  func test_disabledWith0() {
    let font = font.withFontFeatureSettings("'frac' 0")

    XCTAssertNil(font.featureSettings)
  }

  func test_disabledAfterEnabled() {
    let font = font.withFontFeatureSettings("'frac'").withFontFeatureSettings("'frac' off")

    XCTAssertNil(font.featureSettings)
  }

  func test_unknownFeature() {
    let font = font.withFontFeatureSettings("'unknown'")

    XCTAssertNil(font.featureSettings)
  }

  func test_multipleFeatures() {
    let font = font.withFontFeatureSettings("'frac', 'smcp'")

    XCTAssertEqual(
      font.featureSettings,
      [
        [.type: kLowerCaseType, .selector: kLowerCaseSmallCapsSelector],
        [.type: kFractionsType, .selector: kDiagonalFractionsSelector],
      ]
    )
  }

  func test_knownFeatureWithUnknownFeature() {
    let font = font.withFontFeatureSettings("'frac', 'unknown'")

    XCTAssertEqual(
      font.featureSettings,
      [[.type: kFractionsType, .selector: kDiagonalFractionsSelector]]
    )
  }

  func test_enabledMultipleTimes() {
    let font = font.withFontFeatureSettings("'frac'").withFontFeatureSettings("'frac'")

    XCTAssertEqual(
      font.featureSettings,
      [[.type: kFractionsType, .selector: kDiagonalFractionsSelector]]
    )
  }

  func test_enabledFeatureForFontWithFeature() {
    let font = font.withFontFeatureSettings("'frac'").withFontFeatureSettings("'smcp'")

    XCTAssertEqual(
      font.featureSettings,
      [
        [.type: kLowerCaseType, .selector: kLowerCaseSmallCapsSelector],
        [.type: kFractionsType, .selector: kDiagonalFractionsSelector],
      ]
    )
  }
}

private let font = UIFont.systemFont(ofSize: 14)
