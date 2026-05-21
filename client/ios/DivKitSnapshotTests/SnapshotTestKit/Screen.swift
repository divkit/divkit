import CoreGraphics

struct Screen {
  let size: CGSize
  let scale: CGFloat

  static func makeForScale(_ scale: CGFloat) -> Screen {
    Screen(size: CGSize(width: 414, height: 736), scale: scale)
  }
}
