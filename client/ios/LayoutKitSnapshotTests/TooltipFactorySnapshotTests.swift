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

  private func perform(
    text: String,
    position: BlockTooltip.Position,
    width: CGFloat?,
    name: String = #function
  ) {
    let block = TooltipFactory.makeTooltip(
      text: text,
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
