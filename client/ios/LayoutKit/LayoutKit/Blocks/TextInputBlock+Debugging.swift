import BaseUI
import CommonCore

extension TextInputBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var result = """
    TextView \(widthTrait) x \(heightTrait) {
    """
    
    result += "\n  Hint text: \(hint)"

    result += "\n  Variable text: \(textValue.wrappedValue)"

    result += "\n  Background color: \(backgroundColor)"

    result += "\n  Keyboard type: \(keyboardType)"

    if keyboardAppearance != TextInputBlock.defaultKeyboardAppearance {
      result += "\n  Keyboard appearance: \(keyboardAppearance)"
    }

    result += "\n}"

    return result
  }
}
