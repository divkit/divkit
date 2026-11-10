@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import Testing

@Suite
struct DivSliderExtensionsTests {
  @Test(arguments: [-10, 5], [false, true])
  func WhenMaxValueDoesNotExceedMinValue_CollapsesRange(
    maxValue: Int,
    hasRanges: Bool
  ) throws {
    let model = try makeSliderModel(
      divSlider(
        maxValue: maxValue,
        minValue: 5,
        ranges: hasRanges ? [
          DivSlider.Range(
            end: .value(10),
            start: .value(0)
          ),
        ] : nil
      )
    )

    #expect(model.minValue == 5)
    #expect(model.maxValue == 5)
    #expect(model.ranges.isEmpty)
    #expect(model.marksConfiguration.modelConfiguration.minValue == 5)
    #expect(model.marksConfiguration.modelConfiguration.maxValue == 5)
  }

  @Test
  func WhenRangeIsValid_KeepsMinMaxAndRanges() throws {
    let model = try makeSliderModel(
      divSlider(
        maxValue: 100,
        minValue: 0
      )
    )

    #expect(model.minValue == 0)
    #expect(model.maxValue == 100)
    #expect(model.ranges.count == 1)
    #expect(model.ranges.first?.start == 0)
    #expect(model.ranges.first?.end == 100)
    #expect(model.marksConfiguration.modelConfiguration.minValue == 0)
    #expect(model.marksConfiguration.modelConfiguration.maxValue == 100)
  }
}

private func makeSliderModel(_ div: Div) throws -> SliderModel {
  let sliderBlock: SliderBlock = try makeBlock(div).child.unwrap()
  return sliderBlock.sliderModel
}
