import LayoutKit

extension Duration {
  init(milliseconds: Int) {
    self.init(Double(milliseconds) / 1000)
  }
}
