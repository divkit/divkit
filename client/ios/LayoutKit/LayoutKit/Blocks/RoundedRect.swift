import CoreGraphics

import CommonCorePublic

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

    // (iOS 11) corner radius must be not greater than half rect height:
    // Assertion failed: (corner_height >= 0 && 2 * corner_height <= CGRectGetHeight(rect)),
    // function CGPathCreateWithRoundedRect,
    // file /BuildRoot/Library/Caches/com.apple.xbs/Sources/Quartz2D_Sim/Quartz2D-1125.2.1/CoreGraphics/Paths/CGPath.cc,
    // line 190.
    let maxRadius = floor(size.minDimension / 2)

    let r0 = clamp(cornerRadii.topLeft, min: 0, max: maxRadius) - inset
    let r1 = clamp(cornerRadii.topRight, min: 0, max: maxRadius) - inset
    let r2 = clamp(cornerRadii.bottomRight, min: 0, max: maxRadius) - inset
    let r3 = clamp(cornerRadii.bottomLeft, min: 0, max: maxRadius) - inset

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
