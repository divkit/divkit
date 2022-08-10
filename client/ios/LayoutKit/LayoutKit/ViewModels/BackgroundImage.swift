import CommonCore

public struct BackgroundImage {
  let imageHolder: ImageHolder
  let contentMode: ImageContentMode
  let alpha: Double

  public init(
    imageHolder: ImageHolder,
    contentMode: ImageContentMode = .default,
    alpha: Double = 1.0
  ) {
    self.imageHolder = imageHolder
    self.contentMode = contentMode
    self.alpha = alpha
  }
}

extension BackgroundImage: Equatable {
  public static func ==(lhs: BackgroundImage, rhs: BackgroundImage) -> Bool {
    lhs.imageHolder == rhs.imageHolder &&
      lhs.contentMode == rhs.contentMode &&
      lhs.alpha == rhs.alpha
  }
}

extension BackgroundImage: CustomDebugStringConvertible {
  public var debugDescription: String {
    "Holder: \(imageHolder.debugDescription), \(contentMode.debugDescription), Alpha:\(alpha)"
  }
}
