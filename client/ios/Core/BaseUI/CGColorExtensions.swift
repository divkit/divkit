import CoreGraphics
import CoreImage

import BaseTiny

extension CGColor {
  public var rgba: RGBAColor {
    let color = CIColor(cgColor: self)
    return RGBAColor(
      red: color.red, green: color.green, blue: color.blue, alpha: color.alpha
    )
  }
}
