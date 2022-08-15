import CoreGraphics
import Foundation

public final class ClosureIntrinsicCalculator: IntrinsicCalculator {
  private let widthGetter: () -> CGFloat
  private let heightGetter: (_ width: CGFloat) -> CGFloat

  public init(
    widthGetter: @escaping () -> CGFloat,
    heightGetter: @escaping (_ width: CGFloat) -> CGFloat
  ) {
    self.widthGetter = widthGetter
    self.heightGetter = heightGetter
  }

  public func calculateWidth() -> CGFloat {
    widthGetter()
  }

  public func calculateHeight(width: CGFloat) -> CGFloat {
    heightGetter(width)
  }
}
