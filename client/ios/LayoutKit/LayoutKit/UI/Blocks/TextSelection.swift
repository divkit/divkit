#if os(iOS)
import UIKit
import VGSL

final class TextSelection {
  struct Point {
    let point: CGPoint
    let viewBounds: CGRect

    var x: CGFloat {
      point.x
    }

    var y: CGFloat {
      point.y
    }

    var verticallyInverted: CGPoint {
      point
        .applying(CGAffineTransform(translationX: 0, y: viewBounds.height).scaledBy(x: 1, y: -1))
    }
  }

  struct TextModel {
    let layout: AttributedStringLayout<ActionsAttribute>
    let text: NSAttributedString

    var maxSymbolIndex: Int {
      layout.lines
        .compactMap { !$0.isTruncated ? $0.range.upperBound : -1 }.max() ?? Int.max
    }

    func maxLineSymbolIndex(_ pointerIndex: Int) -> Int {
      getLineRange(pointerIndex).upperBound
    }

    func minLineSymbolIndex(_ pointerIndex: Int) -> Int {
      getLineRange(pointerIndex).lowerBound
    }

    private func getLineRange(_ index: Int) -> Range<Int> {
      for line in layout.lines {
        if line.range.contains(index), let range = Range(line.range) {
          return range
        }
      }

      return 0..<maxSymbolIndex
    }

  }

  private(set) var range: Range<Int>
  private(set) var rect: CGRect? {
    didSet {
      rectSetAction()
    }
  }

  private var activePointer: ActivePointer?

  private let textModel: TextModel
  private let rectSetAction: () -> Void

  init?(
    textModel: TextModel,
    point: Point,
    rectSetAction: @escaping () -> Void
  ) {
    self.textModel = textModel
    self.range = Range<Int>.empty
    self.rectSetAction = rectSetAction

    guard let elementIndex = elementIndex(at: point.verticallyInverted) else { return nil }

    makeRange(elementIndex)
  }

  func draw(_ rect: CGRect) {
    self.rect = textModel.text.drawSelection(
      context: UIGraphicsGetCurrentContext()!,
      rect: rect,
      linesLayout: textModel.layout.lines,
      selectedRange: range
    )
  }

  func showMenu(from view: UIView) {
    if let rect {
      UIMenuController.shared.presentMenu(from: view, in: rect)
    }

    activePointer = nil
  }

  func moveSelection(_ elementIndex: Int) {
    guard let activePointer else { return }

    switch activePointer {
    case .leading:
      moveLeading(elementIndex)
    case .trailing:
      moveTrailing(elementIndex)
    }
  }

  func setActivePointer(to point: Point) {
    let textLayout = textModel.layout

    let lineIndex = textLayout.lines.lastIndex(where: {
      point.verticallyInverted.y <= $0.verticalOffset
    }) ?? 0

    let prevLineElementIndex = textLayout.getTapElementIndex(
      from: CGPoint(
        x: point.x,
        y: textLayout.lines.element(at: lineIndex - 1)?.verticalOffset
          .advanced(by: -1) ?? .infinity
      )
    )

    let nextLineElementIndex = textLayout.getTapElementIndex(
      from: CGPoint(
        x: point.x,
        y: textLayout.lines.element(at: lineIndex + 1)?.verticalOffset.advanced(by: -1) ?? 0
      )
    )

    let elementIndex = elementIndex(at: point.verticallyInverted)

    let nearestIndex = [elementIndex, prevLineElementIndex, nextLineElementIndex]
      .compactMap(identity).first { min(
        abs($0 - range.lowerBound),
        abs($0 - range.upperBound)
      ) < 5 }

    if let nearestIndex {
      activePointer = if abs(nearestIndex - range.lowerBound) <
        abs(nearestIndex - range.upperBound) {
        .leading
      } else {
        .trailing
      }
    }
  }

  func elementIndex(at point: CGPoint) -> Int? {
    textModel.layout.getTapElementIndex(from: point)
  }

  func getSelectedText() -> String {
    textModel.text.string[Range(uncheckedBounds: (
      range.lowerBound,
      min(range.upperBound, textModel.maxSymbolIndex)
    ))]
  }

  private func makeRange(_ elementIndex: Int) {
    let prefix = textModel.text.string.prefix(elementIndex)
    let suffix = textModel.text.string.suffix(from: prefix.endIndex)
    let trailing = elementIndex + suffix.distance(
      from: suffix.startIndex,
      to: suffix.firstIndex { $0.isWhitespace || $0.isNewline } ?? suffix.endIndex
    )
    let leading = prefix.lastIndex { $0.isWhitespace || $0.isNewline }
      .flatMap { prefix.distance(from: prefix.startIndex, to: $0) + 1 } ?? 0

    range = leading..<trailing
  }

  private func moveLeading(_ elementIndex: Int) {
    let elementIndex = min(elementIndex, range.upperBound)

    switch true {
    case elementIndex < range.upperBound:
      range = elementIndex..<range.upperBound
    case range.upperBound + 1 < textModel.maxLineSymbolIndex(elementIndex):
      range = elementIndex..<range.upperBound + 1
      activePointer?.toggle()
    default:
      break
    }
  }

  private func moveTrailing(_ elementIndex: Int) {
    let elementIndex = max(elementIndex, range.lowerBound)

    switch true {
    case elementIndex > range.lowerBound:
      range = range.lowerBound..<elementIndex
    case range.lowerBound - 1 > textModel.minLineSymbolIndex(elementIndex):
      range = range.lowerBound - 1..<elementIndex
      activePointer?.toggle()
    default:
      break
    }
  }

}

extension AttributedStringLayout {
  fileprivate func getTapElementIndex(from point: CGPoint) -> Int? {
    let lineLayout = lines.last(where: { point.y <= $0.verticalOffset })
    guard let lineLayout,
          point.x > lineLayout.horizontalOffset else {
      return nil
    }
    if !lineLayout.isTruncated {
      return CTLineGetStringIndexForPosition(
        lineLayout.line,
        point.movingX(by: -lineLayout.horizontalOffset)
      )
    } else {
      let previousLineRange = lines.last { $0 != lineLayout }?.range
      return (previousLineRange?.upperBound ?? 0) +
        CTLineGetStringIndexForPosition(
          lineLayout.line,
          point.movingX(by: -lineLayout.horizontalOffset)
        )
    }
  }
}

extension Range {
  fileprivate static var empty: Range<Int> {
    0..<0
  }
}

private enum ActivePointer {
  case leading
  case trailing

  mutating func toggle() {
    switch self {
    case .leading: self = .trailing
    case .trailing: self = .leading
    }
  }
}
#endif
