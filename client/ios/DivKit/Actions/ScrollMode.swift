import Foundation

enum ScrollMode: Hashable {
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
