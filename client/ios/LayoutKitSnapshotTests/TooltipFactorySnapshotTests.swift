import CoreGraphics
import XCTest

import CommonCorePublic
import LayoutKit

final class TooltipFactorySnapshotTests: XCTestCase {
  func test_Orientations() {
    BlockTooltip.Position.allCases.forEach {
      perform(text: "Тултип", position: $0, width: nil)
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
    LayoutKitSnapshotTest.perform(
      on: block,
      size: block.intrinsicSize,
      name: "\(name)_\(position)",
      mode: .verify
    )
  }
}
