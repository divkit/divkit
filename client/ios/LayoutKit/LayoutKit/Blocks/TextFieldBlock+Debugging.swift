import BaseUIPublic
import CommonCorePublic

extension TextFieldBlock: CustomDebugStringConvertible {
  public var debugDescription: String {
    var result = """
    TextField \(widthTrait) x \(heightTrait) {
    \(text.prettyDebugDescription.indented())
    """

    switch placeholders {
    case let .separate(placeholders):
      let headerTypo = Typo(
        size: placeholders.headerAttributes.size,
        weight: placeholders.fontWeight
      ).with(height: placeholders.headerAttributes.height)
        .with(color: placeholders.headerAttributes.color)
      let fieldTypo = Typo(
        size: placeholders.field.attributes.size,
        weight: placeholders.fontWeight
      ).with(height: placeholders.field.attributes.height)
        .with(color: placeholders.field.attributes.color)
      result +=
        "\n  Header placeholder:\(placeholders.text.with(typo: headerTypo).prettyDebugDescription.indented().dropFirst())"
      result +=
        "\n  Text placeholder:\(placeholders.text.with(typo: fieldTypo).prettyDebugDescription.indented().dropFirst())"
      if placeholders.gap.isApproximatelyEqualTo(0) {
        result += "\n  Header to text gap: \(placeholders.gap)"
      }
    case let .fieldOnly(fieldPlaceholder):
      result +=
        "\n  Text placeholder:\(fieldPlaceholder.prettyDebugDescription.indented().dropFirst())"
    case .none:
      break
    }

    result += "\n  Background color: \(backgroundColor)"

    if maxIntrinsicNumberOfLines != 1 {
      result += "\n  Max lines: \(maxIntrinsicNumberOfLines)"
    }

    if keyboardAppearance != TextFieldBlock.defaultKeyboardAppearance {
      result += "\n  Keyboard appearance: \(keyboardAppearance)"
    }

    result += "\n  Update action: \(updateAction)"

    if isSecureTextEntry {
      result += "\n  Secure entry"
    }

    if let control = rightControl {
      result += "\n  RightControl: \(control)"
    }

    if let toolbar = toolbar {
      result += "\n  Toolbar: \(toolbar)"
    }
    result += "\n}"

    return result
  }
}
