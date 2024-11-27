import CoreGraphics

enum ShimmerGradientGeometry {
  static func points(for angle: CGFloat) -> (CGPoint, CGPoint) {
    var ang = (-angle).truncatingRemainder(dividingBy: 360)

    if ang < 0 { ang = 360 + ang }

    let n: CGFloat = 0.5

    let startPoint, endPoint: CGPoint
    switch ang {
    case 0...45, 315...360:
      startPoint = CGPoint(x: 0, y: n * tanx(ang) + n)
      endPoint = CGPoint(x: 1, y: n * tanx(-ang) + n)

    case 45...135:
      startPoint = CGPoint(x: n * tanx(ang - 90) + n, y: 1)
      endPoint = CGPoint(x: n * tanx(-ang - 90) + n, y: 0)

    case 135...225:
      startPoint = CGPoint(x: 1, y: n * tanx(-ang) + n)
      endPoint = CGPoint(x: 0, y: n * tanx(ang) + n)

    case 225...315:
      startPoint = CGPoint(x: n * tanx(-ang - 90) + n, y: 0)
      endPoint = CGPoint(x: n * tanx(ang - 90) + n, y: 1)

    default:
      startPoint = CGPoint(x: 0, y: n)
      endPoint = CGPoint(x: 1, y: n)
    }

    return (startPoint, endPoint)
  }

  static func frameScaledToAspectFill(for rect: CGRect) -> CGRect {
    var gradientFrame = rect

    if rect.width > rect.height {
      gradientFrame.origin = CGPoint(x: 0, y: -(rect.width - rect.height) / 2)
      gradientFrame.size = CGSize(width: rect.width, height: rect.width)
    } else if rect.height > rect.width {
      gradientFrame.origin = CGPoint(x: -(rect.height - rect.width) / 2, y: 0)
      gradientFrame.size = CGSize(width: rect.height, height: rect.height)
    }

    return gradientFrame
  }
}

private func tanx(_ theta: CGFloat) -> CGFloat {
  tan(theta * CGFloat.pi / 180)
}
