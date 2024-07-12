import VGSL

public struct BackgroundImage {
  let imageHolder: ImageHolder
  let contentMode: ImageContentMode
  let alpha: Double
  let effects: [ImageEffect]

  public init(
    imageHolder: ImageHolder,
    contentMode: ImageContentMode = .default,
    alpha: Double = 1.0,
    effects: [ImageEffect] = []
  ) {
    self.imageHolder = imageHolder
    self.contentMode = contentMode
    self.alpha = alpha
    self.effects = effects
  }
}

extension BackgroundImage: Equatable {
  public static func ==(lhs: BackgroundImage, rhs: BackgroundImage) -> Bool {
    compare(lhs.imageHolder, rhs.imageHolder) &&
      lhs.contentMode == rhs.contentMode &&
      lhs.alpha == rhs.alpha &&
      lhs.effects == rhs.effects
  }
}

extension BackgroundImage: CustomDebugStringConvertible {
  public var debugDescription: String {
    "Holder: \(imageHolder.debugDescription), \(contentMode.debugDescription), Alpha:\(alpha)"
  }
}
