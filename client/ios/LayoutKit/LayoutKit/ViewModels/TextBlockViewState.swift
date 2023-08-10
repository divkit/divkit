import Foundation

public struct TextBlockViewState: ElementState, Equatable {
  public let text: String

  public init(text: String) {
    self.text = text
  }
}
