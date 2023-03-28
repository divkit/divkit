import CoreGraphics
import Foundation

import BasePublic

public struct GenericCollectionLayout {
  public let frames: [CGRect]
  public let contentSize: CGSize

  public init(frames: [CGRect], contentSize: CGSize = .zero) {
    self.frames = frames
    self.contentSize = contentSize
  }

  public init(frames: [CGRect], pageSize: CGSize) {
    self.frames = frames

    self.contentSize = modified(BasePublic.contentSize(for: frames)) {
      if pageSize.width > 0 {
        $0.width = $0.width.ceiled(toStep: pageSize.width)
      }
      if pageSize.height > 0 {
        $0.height = $0.height.ceiled(toStep: pageSize.height)
      }
    }
  }
}
