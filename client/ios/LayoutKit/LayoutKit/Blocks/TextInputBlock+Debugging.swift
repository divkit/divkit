import BaseUIPublic
import CommonCorePublic

extension TextInputBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var result = """
    TextView \(widthTrait) x \(heightTrait) {
    """

    result += "\n  Hint text: \(hint)"

    result += "\n  Variable text: \(textValue.wrappedValue)"

    result += "\n  Keyboard type: \(keyboardType)"

    result += "\n}"

    return result
  }
}
