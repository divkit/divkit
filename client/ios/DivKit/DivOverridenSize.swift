import Foundation

struct DivOverridenSize {
  public let original: DivSize
  public let overriden: DivSize

  public init(original: DivSize, overriden: DivSize) {
    self.original = original
    self.overriden = overriden
  }
}
