import LayoutKit

extension Duration {
  init?(milliseconds: Int) {
    guard milliseconds > 0 else { return nil }
    self.init(Double(milliseconds) / 1000)
  }
}
