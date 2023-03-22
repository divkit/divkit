import QuartzCore

extension CALayer {
  func apply(boundary: BoundaryTrait) {
    let boundary = boundary.makeInfo(for: bounds.size)
    cornerRadius = boundary.radius
    maskedCorners = boundary.corners
    mask = boundary.layer
  }
}
