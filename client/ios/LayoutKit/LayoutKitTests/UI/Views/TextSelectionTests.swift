@testable import LayoutKit
import VGSL
import XCTest

final class TextSelectionTests: XCTestCase {
  private let rect = CGRect(x: 0, y: 0, width: 100, height: 100)

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

    for item in results {
      XCTAssertEqual(
        makeSelection(point: item.value).range.lowerBound,
        item.result
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

    for item in results {
      XCTAssertEqual(
        makeSelection(point: item.value).range.upperBound,
        item.result
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

    for resultStep in resultSteps.enumerated() {
      selection.moveSelection(resultStep.offset)

      XCTAssertEqual(
        selection.getSelectedText(),
        resultStep.element
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

    for resultStep in resultSteps.enumerated() {
      selection.moveSelection(startIndex - resultStep.offset)

      XCTAssertEqual(
        selection.getSelectedText(),
        resultStep.element
      )
    }
  }

  private func makeSelection(point: TextSelection.Point) -> TextSelection {
    TextSelection(
      textModel: model,
      point: point,
      rectSetAction: {}
    )!
  }

  private func range(of word: String) -> NSRange {
    let text = text.string as NSString
    return text.range(of: word)
  }
}
