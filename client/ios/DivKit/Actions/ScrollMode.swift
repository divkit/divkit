import Foundation

enum ScrollMode {
  case forward(step: CGFloat, overflow: OverflowMode)
  case backward(step: CGFloat, overflow: OverflowMode)
  case position(step: CGFloat)
  case start
  case end
}
