import CoreGraphics

import VGSL

extension CGPath {
  public static func roundedRect(
    size: CGSize,
    cornerRadii: CornerRadii,
    inset: CGFloat = 0
  ) -> CGPath {
    let rect = CGRect(
      origin: CGPoint(x: inset, y: inset),
      size: CGSize(
        width: size.width - inset * 2,
        height: size.height - inset * 2
      )
    )
    let topLeft = rect.coordinate(ofCorner: .topLeft)
    let topRight = rect.coordinate(ofCorner: .topRight)
    let bottomLeft = rect.coordinate(ofCorner: .bottomLeft)
    let bottomRight = rect.coordinate(ofCorner: .bottomRight)

    var r0 = cornerRadii.topLeft - inset
    var r1 = cornerRadii.topRight - inset
    var r2 = cornerRadii.bottomRight - inset
    var r3 = cornerRadii.bottomLeft - inset

    let top = r0 + r1
    let bottom = r2 + r3
    let left = r0 + r3
    let right = r1 + r2

    if let f = [rect.width / top, rect.width / bottom, rect.height / left, rect.height / right]
      .min(),
      f > 0, f < 1 {
      r0 *= f
      r1 *= f
      r2 *= f
      r3 *= f
    }

    let p0 = topLeft.movingX(by: r0)
    let p1 = topRight.movingX(by: -r1)

    let p2 = topRight.movingY(by: r1)
    let p3 = bottomRight.movingY(by: -r2)

    let p4 = bottomRight.movingX(by: -r2)
    let p5 = bottomLeft.movingX(by: r3)

    let p6 = bottomLeft.movingY(by: -r3)
    let p7 = topLeft.movingY(by: r0)

    let path = CGMutablePath()
    path.move(to: p0)
    path.addLine(to: p1)
    path.addArc(tangent1End: topRight, tangent2End: p2, radius: r1)
    path.addLine(to: p3)
    path.addArc(tangent1End: bottomRight, tangent2End: p4, radius: r2)
    path.addLine(to: p5)
    path.addArc(tangent1End: bottomLeft, tangent2End: p6, radius: r3)
    path.addLine(to: p7)
    path.addArc(tangent1End: topLeft, tangent2End: p0, radius: r0)

    return path
  }
}
