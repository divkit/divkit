import VGSL

extension ImageBlock {
  public func makeCopy(with imageHolder: ImageHolder) -> ImageBlock {
    ImageBlock(
      imageHolder: imageHolder,
      widthTrait: widthTrait,
      height: height,
      contentMode: contentMode,
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects,
      accessibilityElement: accessibilityElement
    )
  }
}
