// Copyright 2022 Yandex LLC. All rights reserved.

import BaseUI
import CommonCore

extension TextInputBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var result = """
    TextView \(widthTrait) x \(heightTrait) {
    \(text.prettyDebugDescription.indented())
    """

    result += "\n  Background color: \(backgroundColor)"

    result += "\n Keyboard type: \(keyboardType)"

    if keyboardAppearance != TextInputBlock.defaultKeyboardAppearance {
      result += "\n  Keyboard appearance: \(keyboardAppearance)"
    }

    result += "\n}"

    return result
  }
}
