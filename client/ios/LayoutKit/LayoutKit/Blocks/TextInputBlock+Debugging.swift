import VGSL

extension TextInputBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var result = """
    TextView \(widthTrait) x \(heightTrait) {
    """

    result += "\n  Hint text: \(hint)"

    result += "\n  Variable text: \(textValue.value)"

    result += "\n  Input type: \(inputType)"

    result += "\n}"

    return result
  }
}
