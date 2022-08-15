import Foundation

import BaseTiny
import DivKit

extension EdgeInsets {
  public var asSafeAreaVariables: DivVariables {
    [
      "safe_area_top": .number(self.top),
      "safe_area_bottom": .number(self.bottom),
      "safe_area_left": .number(self.left),
      "safe_area_right": .number(self.right),
    ]
  }
}
