import CoreGraphics
import LayoutKit
import VGSL

final class TooltipFactorySnapshotTests: LayoutKitSnapshotTest {
  func test_Orientations() {
    for item in BlockTooltip.Position.allCases {
      perform(text: "Тултип", position: item, width: nil)
    }
  }

  func test_Multiline() {
    perform(
      text: "Много текста, который не поместится в одну строку",
      position: .topRight,
      width: 90
    )
  }

  func test_TextAlignment() {
    let alignments: [(TextAlignment, String)] = [
      (.left, "left"),
      (.center, "center"),
      (.right, "right"),
    ]
    for (alignment, name) in alignments {
      perform(
        text: "Тест выравнивания текста в тултипе",
        alignment: alignment,
        position: .top,
        width: 150,
        name: "test_TextAlignment_\(name)"
      )
    }
  }

  private func perform(
    text: String,
    alignment: TextAlignment = .left,
    position: BlockTooltip.Position,
    width: CGFloat?,
    name: String = #function
  ) {
    let block = TooltipFactory.makeTooltip(
      text: text,
      alignment: alignment,
      position: position,
      width: width
    )
    perform(
      on: block,
      size: block.intrinsicSize,
      name: "\(name)_\(position)",
      mode: .verify
    )
  }
}
