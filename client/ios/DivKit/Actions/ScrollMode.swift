import Foundation

enum ScrollMode {
  case forward(Int, overflow: OverflowMode)
  case backward(Int, overflow: OverflowMode)
  case position(Int)
  case start
  case end
}

enum OverflowMode {
  case clamp
  case ring
}
