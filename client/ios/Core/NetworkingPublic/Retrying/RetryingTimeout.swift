import Foundation

import BasePublic

struct RetryingTimeout {
  private let minValue: TimeInterval
  private let maxValue: TimeInterval
  private let growthFactor: Double

  private var value: TimeInterval

  init(
    value: TimeInterval,
    minValue: TimeInterval = 0.1,
    maxValue: TimeInterval = 5 * 60,
    growthFactor: Double = 2.7
  ) {
    self.value = value
    self.minValue = minValue
    self.maxValue = maxValue
    self.growthFactor = growthFactor
  }

  mutating func getValueAndIncrement() -> TimeInterval {
    let currentValue = value
    value = clamp(currentValue * growthFactor, min: minValue, max: maxValue)
    return currentValue
  }
}
