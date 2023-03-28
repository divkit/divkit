import CommonCorePublic
import LayoutKitInterface

public enum Background: Equatable {
  case solidColor(Color)
  case tiledImage(Image)
  case image(BackgroundImage)
  case gradient(Gradient)
  case transparentAction(UserInterfaceAction)
  case ninePatchImage(NinePatchImage)
  case block(Block)
  indirect case composite(Background, Background, Float?)
  indirect case withInsets(background: Background, contentInsets: EdgeInsets)

  public init(backgrounds: Background...) {
    self = backgrounds.dropFirst().reduce(backgrounds.first!) {
      Background.composite($0, $1, nil)
    }
  }
}

public func +(lhs: Background, rhs: Background) -> Background {
  .composite(lhs, rhs, nil)
}

public func +=(lhs: inout Background, rhs: Background) {
  lhs = lhs + rhs
}

public func ==(lhs: Background, rhs: Background) -> Bool {
  switch (lhs, rhs) {
  case let (.solidColor(color1), .solidColor(color2)):
    return color1 == color2
  case let (.tiledImage(image1), .tiledImage(image2)):
    return imagesDataAreEqual(image1, image2)
  case let (.image(image1), .image(image2)):
    return image1 == image2
  case let (.ninePatchImage(image1), .ninePatchImage(image2)):
    return image1 == image2
  case let (.gradient(gradient1), .gradient(gradient2)):
    return gradient1 == gradient2
  case let (.transparentAction(action1), .transparentAction(action2)):
    return action1 == action2
  case let (.block(block1), .block(block2)):
    return block1 == block2
  case let (
    .composite(background11, background12, blendingCoefficient1),
    .composite(background21, background22, blendingCoefficient2)
  ):
    return background11 == background21 && background12 == background22 && blendingCoefficient1 ==
      blendingCoefficient2
  case let (.withInsets(background1, insets1), .withInsets(background2, insets2)):
    return background1 == background2 && insets1 == insets2
  case (.solidColor, _),
       (.tiledImage, _),
       (.image, _),
       (.gradient, _),
       (.block, _),
       (.transparentAction, _),
       (.composite, _),
       (.withInsets, _),
       (.ninePatchImage, _):
    return false
  }
}

extension Background: CustomDebugStringConvertible {
  public var debugDescription: String {
    switch self {
    case let .gradient(gradient):
      return "Gradient \(gradient.debugDescription)"
    case let .image(image):
      return "Image \(image.debugDescription)"
    case let .ninePatchImage(image):
      return "NinePatchImage \(image.debugDescription)"
    case let .solidColor(color):
      return "Solid \(color.debugDescription)"
    case let .tiledImage(image):
      return "TiledImage \(image.size.width) x \(image.size.height)"
    case let .transparentAction(action):
      return "Action payload: \(action.payload), path:\(action.path)"
    case let .block(block):
      return "Block \(block.debugDescription)"
    case let .composite(bck1, bck2, coef):
      #if os(iOS)
      let defaultFrontCoef = CompositeView.defaultBlendingCoefficient
      #else // OSX
      let defaultFrontCoef: Float = 1
      #endif

      return """
      Composite, front coef: \(coef ?? defaultFrontCoef) [
      \(bck1.debugDescription.indented())
      \(bck2.debugDescription.indented())
      ]
      """
    case let .withInsets(background, insets):
      return """
      Inset by \(insets)
      \(background.debugDescription.indented())
      """
    }
  }
}

extension Background: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    switch self {
    case let .composite(first, second, _):
      return first.getImageHolders() + second.getImageHolders()
    case let .image(image):
      return [image.imageHolder]
    case let .ninePatchImage(image):
      return [image.imageHolder]
    case let .withInsets(background, _):
      return background.getImageHolders()
    case let .block(block):
      return block.getImageHolders()
    case .solidColor,
         .tiledImage,
         .gradient,
         .transparentAction:
      return []
    }
  }
}

extension Array where Element == Background {
  public func composite() -> Element? {
    if isEmpty { return nil }
    return dropFirst().reduce(first!, +)
  }
}
