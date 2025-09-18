import CoreGraphics
import VGSL

public final class SliderBlock: BlockWithTraits {
  // MARK: - BlockWithTraits

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  // MARK: - SliderBlock

  let sliderModel: SliderModel

  public var path: UIElementPath? {
    sliderModel.path
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let width = sliderModel.sliderIntrinsicWidth
      return clamp(width, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public var debugDescription: String {
    """
    Slider min value: \(sliderModel.minValue)
    Slider max value: \(sliderModel.maxValue)
    First thumb value: \(sliderModel.firstThumb.value)
    """ + (sliderModel.secondThumb.flatMap { "Second thumb value: \($0.value)" } ?? "")
  }

  public init(
    sliderModel: SliderModel,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) {
    self.sliderModel = sliderModel
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let height = sliderModel.sliderHeight
      return clamp(height, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let otherSlider = (other as? SliderBlock) else { return false }

    return otherSlider.sliderModel == sliderModel &&
      widthTrait == otherSlider.widthTrait &&
      heightTrait == otherSlider.heightTrait
  }

  public func getImageHolders() -> [ImageHolder] {
    []
  }
}

extension SliderBlock: LayoutCachingDefaultImpl {}
extension SliderBlock: ElementStateUpdatingDefaultImpl {}
