import LayoutKit

struct LottiePlaybackState: ElementState, Equatable {
  enum Command: String {
    case start
    case stop
  }

  let command: Command
  let sequence: UInt64
}
