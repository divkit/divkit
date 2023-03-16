import LayoutKit

extension Delay {
  init(milliseconds: Int) {
    self.init(Double(milliseconds) / 1000)
  }
}
