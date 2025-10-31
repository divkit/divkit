import VGSL

extension AnimatableImageBlock {
  public func makeCopy(withState state: ImageBaseBlockState) -> Self {
    Self(
      imageHolder: imageHolder,
      widthTrait: widthTrait,
      height: height,
      contentMode: contentMode,
      accessibilityElement: accessibilityElement,
      path: path,
      state: state
    )
  }
}
