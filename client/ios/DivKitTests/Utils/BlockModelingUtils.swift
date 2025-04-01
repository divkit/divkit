@testable import DivKit
import DivKitTestsSupport
import Foundation
@testable import LayoutKit
import VGSL
import XCTest

func makeBlock(
  _ div: Div,
  context: DivBlockModelingContext = DivBlockModelingContext(),
  ignoreErrors: Bool = false,
  stateId: Int = 0
) -> StateBlock {
  let block = try! divData(div, stateId: stateId).makeBlock(context: context) as! StateBlock
  if !ignoreErrors, let error = context.errorsStorage.errors.first {
    XCTFail(error.message)
  }
  return block
}

func separatorBlock() -> Block {
  SeparatorBlock(
    color: color("#14000000")
  )
}

func textBlock(
  widthTrait: LayoutTrait = .resizable,
  text: String,
  path: UIElementPath
) -> Block {
  TextBlock(
    widthTrait: widthTrait,
    text: text.withTypo(),
    verticalAlignment: .leading,
    accessibilityElement: nil,
    path: path
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
  static let root = DivBlockModelingContext.testCardId.path
}

extension DivAction {
  func uiAction(
    pathSuffix: String
  ) -> UserInterfaceAction? {
    let pathComponents = pathSuffix.split(separator: "/")
    var context = DivBlockModelingContext.default
    for pathComponent in pathComponents {
      context = context.modifying(pathSuffix: String(pathComponent))
    }
    return uiAction(context: context)
  }
}

func uiAction(
  logId: String,
  path: UIElementPath = .root,
  url: String
) -> UserInterfaceAction {
  UserInterfaceAction(
    payload: divActionPayload(
      [
        "log_id": .string(logId),
        "is_enabled": .bool(true),
        "url": .string(url),
      ],
      path: path,
      url: url
    ),
    path: .root + logId
  )
}

func divActionPayload(
  _ json: JSONDictionary,
  path: UIElementPath = .root,
  url: String? = nil
) -> UserInterfaceAction.Payload {
  .divAction(
    params: UserInterfaceAction.DivActionParams(
      action: .object(json),
      path: path,
      source: .tap,
      url: url.map { URL(string: $0)! },
      localValues: [:]
    )
  )
}

extension Block {
  func unwrap<T>() throws -> T {
    let wrapperBlock = try XCTUnwrap(self as? WrapperBlock)
    return try XCTUnwrap(wrapperBlock.child as? T)
  }
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

func expression<T>(_ expression: String) -> DivKit.Expression<T> {
  .link(ExpressionLink<T>(rawValue: expression)!)
}
