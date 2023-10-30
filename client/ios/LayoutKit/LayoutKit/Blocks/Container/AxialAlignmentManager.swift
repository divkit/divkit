import Foundation

final class AxialAlignmentManager {
  fileprivate enum Shift {
    case single(CGFloat)
    case array([CGFloat])
  }

  private let layoutDirection: ContainerBlock.LayoutDirection
  private let axialAlignment: ContainerBlock.AxialAlignment

  public init(
    layoutDirection: ContainerBlock.LayoutDirection,
    axialAlignment: ContainerBlock.AxialAlignment
  ) {
    self.layoutDirection = layoutDirection
    self.axialAlignment = axialAlignment
  }

  public func applyOffset(
    to frames: [CGRect],
    forAvailableSpace availableSpace: CGFloat,
    contentSize: CGFloat = 0
  ) -> [CGRect] {
    let shift = makeShift(
      forAvailableSpace: availableSpace,
      contentSize: contentSize,
      numberOfItems: frames.count
    )
    return frames.applyOffset(
      with: axialAlignment,
      layoutDirection: layoutDirection,
      shift: shift
    )
  }

  private func makeShift(
    forAvailableSpace availableSpace: CGFloat,
    contentSize: CGFloat = 0,
    numberOfItems: Int
  ) -> Shift {
    switch axialAlignment {
    case .leading:
      return .single(0)
    case .center:
      return .single(((availableSpace - contentSize) * 0.5).roundedToScreenScale)
    case .trailing:
      return .single(availableSpace - contentSize)
    case .spaceBetween:
      let totalSpace = availableSpace - contentSize
      let spaceBetweenItems = totalSpace / CGFloat(max(numberOfItems - 1, 1))
      return .array((0..<numberOfItems).map { CGFloat($0) * spaceBetweenItems })
    case .spaceAround:
      let totalSpace = availableSpace - contentSize
      let spaceBetweenItems = totalSpace / CGFloat(numberOfItems)
      let offset = spaceBetweenItems * 0.5
      return .array((0..<numberOfItems).map { CGFloat($0) * spaceBetweenItems + offset })
    case .spaceEvenly:
      let totalSpace = availableSpace - contentSize
      let spaceBetweenItems = totalSpace / CGFloat(numberOfItems + 1)
      return .array((0..<numberOfItems).map { CGFloat($0 + 1) * spaceBetweenItems })
    }
  }
}

extension Array where Element == CGRect {
  fileprivate func applyOffset(
    with axialAlignment: ContainerBlock.AxialAlignment,
    layoutDirection: ContainerBlock.LayoutDirection,
    shift: AxialAlignmentManager.Shift
  ) -> [CGRect] {
    switch axialAlignment {
    case .leading, .center, .trailing:
      guard case let .single(offset) = shift else {
        assertionFailure("leading, center and trailing alignment must be single shift")
        return []
      }
      return compactMap {
        let frame = $0.offset(by: offset.toCGPoint(with: layoutDirection)).roundedToScreenScale
        return frame.isValidAndFinite ? frame : nil
      }

    case .spaceBetween, .spaceAround, .spaceEvenly:
      guard case let .array(offsets) = shift else {
        assertionFailure("spaceBetween, spaceAround и spaceEvenly alignment must be array shift")
        return []
      }
      return zip(self, offsets.map { $0.toCGPoint(with: layoutDirection) }).compactMap {
        let frame = $0.offset(by: $1).roundedToScreenScale
        return frame.isValidAndFinite ? frame : nil
      }
    }
  }
}

extension CGFloat {
  fileprivate func toCGPoint(
    with layoutDirection: ContainerBlock.LayoutDirection
  ) -> CGPoint {
    switch layoutDirection {
    case .horizontal:
      return CGPoint(x: self, y: 0)
    case .vertical:
      return CGPoint(x: 0, y: self)
    }
  }
}
