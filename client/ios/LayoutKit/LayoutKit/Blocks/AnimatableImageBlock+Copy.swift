import VGSL

extension AnimatableImageBlock {
  public func makeCopy() -> AnimatableImageBlock {
    AnimatableImageBlock(
      imageHolder: imageHolder,
      widthTrait: widthTrait,
      height: height,
      contentMode: contentMode,
      accessibilityElement: accessibilityElement,
      path: path
    )
  }
}
