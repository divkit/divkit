import CommonCorePublic

public struct NinePatchImage {
  let imageHolder: ImageHolder
  let insets: EdgeInsets

  public init(
    imageHolder: ImageHolder,
    insets: EdgeInsets
  ) {
    self.imageHolder = imageHolder
    self.insets = insets
  }
}

extension NinePatchImage: Equatable {
  public static func ==(lhs: NinePatchImage, rhs: NinePatchImage) -> Bool {
    lhs.imageHolder == rhs.imageHolder &&
      lhs.insets == rhs.insets
  }
}

extension NinePatchImage: CustomDebugStringConvertible {
  public var debugDescription: String {
    "Holder: \(imageHolder.debugDescription), Insets:\(insets)"
  }
}
