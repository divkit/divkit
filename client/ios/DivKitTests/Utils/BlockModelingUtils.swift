@testable import DivKit
@testable import LayoutKit

import Foundation

import BaseUIPublic
import CommonCorePublic

func makeBlock(
  _ div: Div,
  context: DivBlockModelingContext = .default
) -> StateBlock {
  try! divData(div).makeBlock(context: context) as! StateBlock
}

func separatorBlock() -> Block {
  SeparatorBlock(
    color: color("#14000000")
  )
}

func textBlock(text: String) -> Block {
  TextBlock(
    widthTrait: .resizable,
    text: text.withTypo(),
    verticalAlignment: .leading,
    accessibilityElement: nil
  )
}

extension AccessibilityElement {
  static let `default` = accessibility(traits: .none, label: nil)
}

func accessibility(
  traits: AccessibilityElement.Traits = .none,
  label: String? = nil,
  identifier: String? = nil,
  hideElementWithChildren: Bool = false
) -> AccessibilityElement {
  AccessibilityElement(
    traits: traits,
    strings: AccessibilityElement.Strings(
      label: label,
      identifier: identifier
    ),
    hideElementWithChildren: hideElementWithChildren
  )
}

extension ActionAnimation {
  static let `default` = ActionAnimation(
    touchDown: [TransitioningAnimation(
      kind: .fade,
      start: 1.0,
      end: 0.6,
      duration: 0.1,
      delay: 0,
      timingFunction: .easeInEaseOut
    )],
    touchUp: [TransitioningAnimation(
      kind: .fade,
      start: 0.6,
      end: 1.0,
      duration: 0.1,
      delay: 0,
      timingFunction: .easeInEaseOut
    )]
  )
}

extension UIElementPath {
  static let root = UIElementPath(DivKitTests.cardId.rawValue)
}

extension DivAction {
  var uiAction: UserInterfaceAction? {
    uiAction(context: .default)
  }
}

func uiAction(logId: String, url: String) -> UserInterfaceAction {
  UserInterfaceAction(
    payload: divActionPayload(
      [
        "log_id": .string(logId),
        "is_enabled": .bool(true),
        "url": .string(url),
      ],
      url: url
    ),
    path: .root + logId
  )
}

func divActionPayload(
  _ json: JSONDictionary,
  url: String? = nil
) -> UserInterfaceAction.Payload {
  .divAction(
    params: UserInterfaceAction.DivActionParams(
      action: .object(json),
      cardId: DivKitTests.cardId.rawValue,
      source: .tap,
      url: url.map { URL(string: $0)! }
    )
  )
}

extension String {
  func withTypo(size: CGFloat = 12, weight: DivFontWeight = .regular) -> NSAttributedString {
    let typo = Typo(
      font: DivBlockModelingContext.default.fontProvider.font(weight: weight, size: size)
    )
    return NSAttributedString(string: self).with(typo: typo)
  }
}

func color(_ color: String) -> Color {
  Color.color(withHexString: color)!
}

func url(_ string: String) -> URL {
  URL(string: string)!
}

func expression<T>(_ expression: String) -> Expression<T> {
  .link(ExpressionLink<T>(rawValue: expression)!)
}
