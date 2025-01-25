import Foundation

extension TimeInterval {
  init(milliseconds: Int) {
    self = Double(milliseconds) / 1000.0
  }
}
