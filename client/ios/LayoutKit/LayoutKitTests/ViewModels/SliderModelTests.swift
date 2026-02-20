import Foundation
@testable import LayoutKit
import Testing

@Suite
struct SliderModelTests {
  @Test(
    arguments: [
      (0, 999, 1),
      (0, 1000, 2),
      (0, 2000, 3),
      (0, 3000, 4),
      (-1000, 0, 2),
      (-500, 500, 2),
    ]
  )
  func stepSize(minValue: Int, maxValue: Int, expectedStepSize: CGFloat) {
    let model = makeSliderModel(minValue: minValue, maxValue: maxValue)
    #expect(model.stepSize == expectedStepSize)
  }

  @Test(
    arguments: [
      (0, 100, 1, 0, 0),
      (0, 100, 1, 100, 100),
      (0, 2000, 3, 0, 0),
      (0, 2000, 3, 2.9, 0),
      (0, 2000, 3, 3, 3),
      (0, 2000, 3, 6, 6),
      (0, 2000, 3, 2000, 1998),
      (0, 2000, 3, 2001, 2000),
      (-100, 0, 1, -100, -100),
      (-100, 0, 1, -50.5, -51),
      (-100, 0, 1, 0, 0),
      (-1000, 0, 2, -500, -500),
      (-1000, 0, 2, -501, -502),
    ]
  )
  func nearestMinValue(
    minValue: Int,
    maxValue: Int,
    stepSize: CGFloat,
    currentValue: CGFloat,
    expected: CGFloat
  ) {
    let model = makeSliderModel(minValue: minValue, maxValue: maxValue)
    #expect(model.stepSize == stepSize)
    let result = model.nearestMinValue(currentValue)
    #expect(result == expected)
  }

  @Test(
    arguments: [
      (0, 100, 1, 0, 0),
      (0, 100, 1, 50.5, 51),
      (0, 100, 1, 100, 100),
      (0, 2000, 3, 3, 3),
      (0, 2000, 3, 4.5, 3),
      (0, 2000, 3, 5.5, 6),
      (0, 2000, 3, 1999.5, 2000),
      (0, 2000, 3, 2000, 2000),
      (-100, 0, 1, -50.5, -51),
      (-100, 0, 1, -50.4, -50),
      (-100, 0, 1, 0, 0),
      (-1000, 0, 2, -501, -502),
      (-1000, 0, 2, -500.5, -500),
    ]
  )
  func nearestValue(
    minValue: Int,
    maxValue: Int,
    stepSize: CGFloat,
    currentValue: CGFloat,
    expected: CGFloat
  ) {
    let model = makeSliderModel(minValue: minValue, maxValue: maxValue)
    #expect(model.stepSize == stepSize)
    let result = model.nearestValue(currentValue)
    #expect(result == expected)
  }
}

private func makeSliderModel(minValue: Int, maxValue: Int) -> SliderModel {
  let marksConfig = MarksConfigurationModel(
    minValue: CGFloat(minValue),
    maxValue: CGFloat(maxValue),
    activeMark: .empty,
    inactiveMark: .empty,
    layoutDirection: .leftToRight
  )

  return SliderModel(
    firstThumb: .empty,
    secondThumb: nil,
    minValue: minValue,
    maxValue: maxValue,
    marksConfiguration: marksConfig,
    ranges: [],
    layoutDirection: .leftToRight,
    isEnabled: true
  )
}
