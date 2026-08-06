#if os(iOS)
@testable import LayoutKit
import VGSL
import XCTest

final class SliderViewAccessibilityTests: XCTestCase {
  // MARK: - isAccessibilityElement / accessibilityTraits / accessibilityValue

  func test_sliderView_isAccessibilityElement_afterSetSliderModel() {
    let view = makeSliderView(minValue: 0, maxValue: 10, initialValue: 5)
    XCTAssertTrue(view.isAccessibilityElement)
  }

  func test_sliderView_accessibilityTraits_adjustable_afterSetSliderModel() {
    let view = makeSliderView(minValue: 0, maxValue: 10, initialValue: 5)
    XCTAssertTrue(view.accessibilityTraits.contains(.adjustable))
  }

  func test_sliderView_accessibilityValue_equalsInitialValue() {
    let view = makeSliderView(minValue: 0, maxValue: 10, initialValue: 5)
    XCTAssertEqual(view.accessibilityValue, "5")
  }

  // MARK: - accessibilityIncrement

  func test_sliderView_accessibilityIncrement_increasesValue() {
    let view = makeSliderView(minValue: 0, maxValue: 10, initialValue: 5)
    view.accessibilityIncrement()
    XCTAssertEqual(view.accessibilityValue, "6")
  }

  func test_sliderView_accessibilityIncrement_clampsAtMaxValue() {
    let view = makeSliderView(minValue: 0, maxValue: 10, initialValue: 10)
    view.accessibilityIncrement()
    XCTAssertEqual(view.accessibilityValue, "10")
  }

  // MARK: - accessibilityDecrement

  func test_sliderView_accessibilityDecrement_decreasesValue() {
    let view = makeSliderView(minValue: 0, maxValue: 10, initialValue: 5)
    view.accessibilityDecrement()
    XCTAssertEqual(view.accessibilityValue, "4")
  }

  func test_sliderView_accessibilityDecrement_clampsAtMinValue() {
    let view = makeSliderView(minValue: 0, maxValue: 10, initialValue: 0)
    view.accessibilityDecrement()
    XCTAssertEqual(view.accessibilityValue, "0")
  }
}

// MARK: - Helpers

private func makeSliderView(minValue: Int, maxValue: Int, initialValue: Int) -> SliderView {
  let thumbBinding = Binding<Int>(
    name: "firstThumb",
    value: Property(initialValue: initialValue)
  )

  let thumbModel = SliderModel.ThumbModel(
    block: EmptyBlock.zeroSized,
    value: thumbBinding,
    size: .zero,
    offsetX: 0,
    offsetY: 0
  )

  let marksConfig = MarksConfigurationModel(
    minValue: CGFloat(minValue),
    maxValue: CGFloat(maxValue),
    activeMark: .empty,
    inactiveMark: .empty,
    layoutDirection: .leftToRight
  )

  let sliderModel = SliderModel(
    firstThumb: thumbModel,
    secondThumb: nil,
    minValue: minValue,
    maxValue: maxValue,
    marksConfiguration: marksConfig,
    ranges: [],
    layoutDirection: .leftToRight,
    isEnabled: true,
    pressStartActions: [],
    pressEndActions: []
  )

  let view = SliderView()
  view.setSliderModel(sliderModel)
  return view
}
#endif
