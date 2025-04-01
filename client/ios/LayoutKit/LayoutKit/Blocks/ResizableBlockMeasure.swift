import CoreGraphics
import VGSL

public struct ResizableBlockMeasure {
  public enum Measure {
    case resizable(LayoutTrait.Weight, reservedSpace: CGFloat = 0)
    case nonResizable
  }

  private let resizableBlockCount: Int
  private let lengthAvailablePerWeightUnit: CGFloat
  private let lengthAvailableForResizableBlocks: CGFloat

  private var cumulativeResizableBlockLength: CGFloat = 0
  private var resizableBlockIndex: Int = 0

  public init(
    resizableBlockCount: Int,
    lengthAvailablePerWeightUnit: CGFloat,
    lengthAvailableForResizableBlocks: CGFloat
  ) {
    self.resizableBlockCount = resizableBlockCount
    self.lengthAvailablePerWeightUnit = lengthAvailablePerWeightUnit
    self.lengthAvailableForResizableBlocks = max(0, lengthAvailableForResizableBlocks)
  }

  public mutating func measureNext(_ measure: Measure) -> CGFloat {
    switch measure {
    case .nonResizable:
      return 0
    case let .resizable(weight, marginsSpace):
      resizableBlockIndex += 1
      let blockLength = weight.rawValue * lengthAvailablePerWeightUnit
      var nextLength = cumulativeResizableBlockLength + blockLength
      // Special handling of the last block to avoid floating-point rounding errors.
      if resizableBlockIndex == resizableBlockCount {
        nextLength = lengthAvailableForResizableBlocks
      }
      let measuredLength =
        nextLength.flooredToScreenScale - cumulativeResizableBlockLength.flooredToScreenScale
      cumulativeResizableBlockLength = nextLength

      return measuredLength + marginsSpace
    }
  }
}
