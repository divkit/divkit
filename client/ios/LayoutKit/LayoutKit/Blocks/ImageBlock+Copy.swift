import VGSL

extension ImageBlock {
  public func makeCopy(
    with imageHolder: ImageHolder
  ) -> Self {
    Self(
      imageHolder: imageHolder,
      widthTrait: widthTrait,
      height: height,
      contentMode: contentMode,
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects,
      filter: filter,
      accessibilityElement: accessibilityElement,
      appearanceAnimation: appearanceAnimation,
      blurUsingMetal: blurUsingMetal,
      tintUsingMetal: tintUsingMetal,
      path: path
    )
  }

  public func makeCopy(withState state: ImageBaseBlockState) -> Self {
    Self(
      imageHolder: imageHolder,
      widthTrait: widthTrait,
      height: height,
      contentMode: contentMode,
      tintColor: tintColor,
      tintMode: tintMode,
      effects: effects,
      filter: filter,
      accessibilityElement: accessibilityElement,
      appearanceAnimation: appearanceAnimation,
      blurUsingMetal: blurUsingMetal,
      tintUsingMetal: tintUsingMetal,
      path: path,
      state: state
    )
  }
}
