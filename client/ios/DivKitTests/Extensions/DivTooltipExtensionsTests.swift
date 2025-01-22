@testable import DivKit
@testable import LayoutKit
import VGSL
import XCTest

final class DivTooltipExtensionsTests: XCTestCase {
  func test_SimpleTooltip() {
    let block = makeBlock(
      divSeparator(
        tooltips: [
          DivTooltip(
            div: divContainer(),
            duration: .value(1000),
            id: "tooltip1",
            offset: point(x: 10, y: 20),
            position: .value(.center)
          ),
        ]
      )
    )

    let expectedBlock = try! StateBlock(
      child: DecoratingBlock(
        child: SeparatorBlock(
          color: color("#14000000")
        ),
        tooltips: [
          BlockTooltip(
            id: "tooltip1",
            block: DecoratingBlock(
              child: ContainerBlock(
                layoutDirection: .vertical,
                children: []
              ),
              accessibilityElement: .default
            ),
            duration: Duration(milliseconds: 1000),
            offset: CGPoint(x: 10, y: 20),
            position: .center,
            useLegacyWidth: false
          ),
        ],
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_NestedTooltip_IsIgnored() {
    let context = DivBlockModelingContext()

    let block = makeBlock(
      divSeparator(
        tooltips: [
          DivTooltip(
            div: divSeparator(
              tooltips: [
                DivTooltip(
                  div: divContainer(),
                  id: "nested_tooltip",
                  position: .value(.center)
                ),
              ]
            ),
            id: "tooltip1",
            position: .value(.center)
          ),
        ]
      ),
      context: context,
      ignoreErrors: true
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: SeparatorBlock(
          color: color("#14000000")
        ),
        tooltips: [
          BlockTooltip(
            id: "tooltip1",
            block: DecoratingBlock(
              child: SeparatorBlock(
                color: color("#14000000")
              ),
              accessibilityElement: .default
            ),
            duration: Duration(milliseconds: 5000),
            offset: CGPoint(x: 0, y: 0),
            position: .center,
            useLegacyWidth: false
          ),
        ],
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)

    let errors = context.errorsStorage.errors
    XCTAssertEqual(1, errors.count)
    XCTAssertEqual("Tooltip can not host another tooltips", errors.first?.message)
  }
}
