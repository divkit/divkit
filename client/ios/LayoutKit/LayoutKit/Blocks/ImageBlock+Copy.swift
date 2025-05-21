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
      accessibilityElement: accessibilityElement,
      blurUsingMetal: blurUsingMetal,
      tintUsingMetal: tintUsingMetal,
      path: path
    )
  }

  public func makeCopy() -> Self {
    makeCopy(with: imageHolder)
  }
}
