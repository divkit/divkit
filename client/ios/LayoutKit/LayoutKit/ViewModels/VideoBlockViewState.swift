import Foundation

public struct VideoBlockViewState: ElementState, Equatable {
  public enum State {
    case paused
    case playing
  }

  public let state: State

  public init(state: State) {
    self.state = state
  }
}
