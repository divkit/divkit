#if os(iOS)
import CoreGraphics
import DivKit
import LayoutKit
import VGSL

/// Extension handler that ensures images are cropped to match the expected aspect ratio.
///
/// When a DivImage has an `aspect` property defined, this handler wraps the image holder
/// in an `AspectCorrectionImageHolder` that automatically crops loaded images to match
/// the expected aspect ratio.
public final class AspectCorrectionExtensionHandler: DivExtensionHandler {
  public let id: String

  private let aspectTolerance: CGFloat

  public init(
    id: String = "aspect_correction",
    aspectTolerance: CGFloat = 0.001
  ) {
    self.id = id
    self.aspectTolerance = aspectTolerance
  }

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard
      let imageBlock = block as? ImageBlock,
      let divImage = div as? DivImage
    else {
      return block
    }

    guard let expectedAspect = resolveExpectedAspect(
      divImage: divImage,
      expressionResolver: context.expressionResolver
    ) else {
      return block
    }

    let wrappedImageHolder = AspectCorrectionImageHolder(
      wrapped: imageBlock.imageHolder,
      expectedAspect: expectedAspect,
      aspectTolerance: aspectTolerance
    )

    return ImageBlock(
      imageHolder: wrappedImageHolder,
      widthTrait: imageBlock.widthTrait,
      height: imageBlock.height,
      contentMode: imageBlock.contentMode,
      tintColor: imageBlock.tintColor,
      tintMode: imageBlock.tintMode,
      effects: imageBlock.effects,
      filter: imageBlock.filter,
      accessibilityElement: imageBlock.accessibilityElement,
      appearanceAnimation: imageBlock.appearanceAnimation,
      blurUsingMetal: imageBlock.blurUsingMetal,
      tintUsingMetal: imageBlock.tintUsingMetal,
      path: imageBlock.path
    )
  }

  private func resolveExpectedAspect(
    divImage: DivImage,
    expressionResolver: ExpressionResolver
  ) -> Double? {
    divImage.aspect?.resolveRatio(expressionResolver)
  }
}
#endif
