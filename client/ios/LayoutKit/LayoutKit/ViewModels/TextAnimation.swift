import Foundation

public struct TextAnimation: Equatable {
  public let duration: Int
  public let repeatCount: Int?
  public let startDelay: Int

  public init(duration: Int, repeatCount: Int?, startDelay: Int) {
    self.duration = duration
    self.repeatCount = repeatCount
    self.startDelay = startDelay
  }
}
