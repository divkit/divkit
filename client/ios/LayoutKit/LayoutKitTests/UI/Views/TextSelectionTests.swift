@testable import LayoutKit
import VGSL
import XCTest

final class TextSelectionTests: XCTestCase {
  func testInitCreatesValidRangeLowerBound() {
    let results: [(value: TextSelection.Point, result: Int)] = [
      (
        point1,
        range(of: words[0]).lowerBound
      ), // Select first word
      (
        point2,
        range(of: words[1]).lowerBound
      ), // Select second word
    ]

    results.forEach {
      XCTAssertEqual(
        makeSelection(point: $0.value).range.lowerBound,
        $0.result
      )
    }
  }

  func testInitCreatesValidRangeUpperBound() {
    let results: [(value: TextSelection.Point, result: Int)] = [
      (
        TextSelection.Point(point: CGPoint(x: 5, y: 10), viewBounds: rect),
        range(of: words[0]).upperBound
      ), // Select first word
      (
        TextSelection.Point(point: CGPoint(x: 45, y: 10), viewBounds: rect),
        range(of: words[1]).upperBound
      ), // Select second word
    ]

    results.forEach {
      XCTAssertEqual(
        makeSelection(point: $0.value).range.upperBound,
        $0.result
      )
    }
  }

  func testMovingFirstWordSelectionByLeftPointerToTheRight() {
    let resultSteps: [String] = [
      "Hello",
      "ello",
      "llo",
      "lo",
      "o",
      " ",
      " ",
      " w",
      " wo",
      " wor",
      " worl",
      " world",
      " world!",
      " world!",
    ]

    let selection = makeSelection(point: point1) // Select first word of text
    selection.setActivePointer(to: point1) // Set left pointer active

    resultSteps.enumerated().forEach {
      selection.moveSelection($0.offset)

      XCTAssertEqual(
        selection.getSelectedText(),
        $0.element
      )
    }
  }

  func testMovingFirstWordSelectionByRightPointerToTheLeft() {
    let resultSteps: [String] = [
      "Hello",
      "Hell",
      "Hel",
      "He",
      "H",
      "H",
      "H",
    ]

    let selection = makeSelection(point: point1) // Select first word of text
    selection.setActivePointer(to: point2) // Set right pointer active
    let startIndex = words[0].count

    resultSteps.enumerated().forEach {
      selection.moveSelection(startIndex - $0.offset)

      XCTAssertEqual(
        selection.getSelectedText(),
        $0.element
      )
    }
  }

  private let rect = CGRect(x: 0, y: 0, width: 100, height: 100)

  private func makeSelection(point: TextSelection.Point) -> TextSelection {
    TextSelection(
      textModel: model,
      point: point,
      rectSetAction: {}
    )!
  }

  private lazy var context: CGContext = {
    let width = Int(rect.width)
    let height = Int(rect.height)
    let colorSpace = CGColorSpace(name: CGColorSpace.sRGB)
    let bitmapInfo = CGImageAlphaInfo.premultipliedLast.rawValue

    return CGContext(
      data: nil,
      width: width,
      height: height,
      bitsPerComponent: 8,
      bytesPerRow: width * 4,
      space: colorSpace!,
      bitmapInfo: bitmapInfo
    )!
  }()

  private let text = NSAttributedString(string: "Hello world!")

  private lazy var point1: TextSelection.Point = .init(
    point: CGPoint(x: 5, y: 10),
    viewBounds: rect
  )

  private lazy var point2: TextSelection.Point = .init(
    point: CGPoint(x: 45, y: 10),
    viewBounds: rect
  )

  private lazy var words: [String] = text.string.components(separatedBy: " ")

  private func range(of word: String) -> NSRange {
    let text = text.string as NSString
    return text.range(of: word)
  }

  private lazy var model: TextSelection.TextModel = {
    let textLayout: AttributedStringLayout<ActionsAttribute> = text.drawAndGetLayout(
      inContext: context,
      verticalPosition: .top,
      rect: rect,
      textInsets: .zero
    )

    return TextSelection.TextModel(
      layout: textLayout,
      text: text
    )
  }()
}
