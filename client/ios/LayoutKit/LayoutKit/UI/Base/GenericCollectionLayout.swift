import CoreGraphics
import Foundation
import VGSL

public struct GenericCollectionLayout {
  public let frames: [CGRect]
  public let contentSize: CGSize
  public let transformation: ElementsTransformation?

  public init(
    frames: [CGRect],
    contentSize: CGSize = .zero,
    transformation: ElementsTransformation? = nil
  ) {
    self.frames = frames
    self.contentSize = contentSize
    self.transformation = transformation
  }

  public init(
    frames: [CGRect],

    pageSize: CGSize,
    transformation: ElementsTransformation? = nil
  ) {
    self.frames = frames
    self.transformation = transformation

    self.contentSize = modified(VGSLUI.contentSize(for: frames)) {
      if pageSize.width > 0 {
        $0.width = $0.width.ceiled(toStep: pageSize.width)
      }
      if pageSize.height > 0 {
        $0.height = $0.height.ceiled(toStep: pageSize.height)
      }
    }
  }
}
