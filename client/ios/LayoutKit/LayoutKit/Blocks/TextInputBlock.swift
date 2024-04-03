import CoreGraphics
import Foundation

import BasePublic
import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

public final class TextInputBlock: BlockWithTraits {
  public enum InputType: Equatable {
    public enum KeyboardType: Equatable {
      case `default`
      case asciiCapable
      case numbersAndPunctuation
      case URL
      case numberPad
      case phonePad
      case namePhonePad
      case emailAddress
      case decimalPad
      case twitter
      case webSearch
      case asciiCapableNumberPad
    }

    public struct SelectionItem: Equatable {
      public let value: String
      public let text: String

      public init(value: String, text: String) {
        self.value = value
        self.text = text
      }
    }

    case keyboard(KeyboardType)
    case selection([SelectionItem])

    public static let `default`: Self = .keyboard(.default)
  }

  public enum TextAlignmentHorizontal: Equatable {
    case left
    case center
    case right
    case start
    case end
  }

  public enum TextAlignmentVertical: Equatable {
    case top
    case center
    case bottom
  }

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let hint: NSAttributedString
  public let textValue: Binding<String>
  public let rawTextValue: Binding<String>?
  public let textTypo: Typo
  public let multiLineMode: Bool
  public let inputType: InputType
  public let highlightColor: Color?
  public let maxVisibleLines: Int?
  public let selectAllOnFocus: Bool
  public let maskValidator: MaskValidator?
  public let path: UIElementPath
  public let isFocused: Bool
  public let onFocusActions: [UserInterfaceAction]
  public let onBlurActions: [UserInterfaceAction]
  public let textAlignmentHorizontal: TextAlignmentHorizontal
  public let textAlignmentVertical: TextAlignmentVertical
  public weak var parentScrollView: ScrollView?
  public let validators: [TextInputValidator]?
  public let layoutDirection: UserInterfaceLayoutDirection
  public let isEnabled: Bool

  public init(
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    hint: NSAttributedString,
    textValue: Binding<String>,
    rawTextValue: Binding<String>? = nil,
    textTypo: Typo,
    multiLineMode: Bool = true,
    inputType: InputType = .default,
    highlightColor: Color? = nil,
    maxVisibleLines: Int? = nil,
    selectAllOnFocus: Bool = false,
    maskValidator: MaskValidator? = nil,
    path: UIElementPath,
    isFocused: Bool = false,
    onFocusActions: [UserInterfaceAction] = [],
    onBlurActions: [UserInterfaceAction] = [],
    parentScrollView: ScrollView? = nil,
    validators: [TextInputValidator]? = nil,
    layoutDirection: UserInterfaceLayoutDirection,
    textAlignmentHorizontal: TextAlignmentHorizontal = .start,
    textAlignmentVertical: TextAlignmentVertical = .center,
    isEnabled: Bool = true
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.hint = hint
    self.textValue = textValue
    self.rawTextValue = rawTextValue
    self.textTypo = textTypo
    self.multiLineMode = multiLineMode
    self.inputType = inputType
    self.highlightColor = highlightColor
    self.maxVisibleLines = maxVisibleLines
    self.selectAllOnFocus = selectAllOnFocus
    self.maskValidator = maskValidator
    self.path = path
    self.isFocused = isFocused
    self.onFocusActions = onFocusActions
    self.onBlurActions = onBlurActions
    self.parentScrollView = parentScrollView
    self.validators = validators
    self.layoutDirection = layoutDirection
    self.textAlignmentHorizontal = textAlignmentHorizontal
    self.textAlignmentVertical = textAlignmentVertical
    self.isEnabled = isEnabled
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      value
    case let .intrinsic(_, minSize, _):
      minSize
    case .weighted:
      0
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      guard let maxVisibleLines else {
        let textHeight = ceil(textForMeasuring.sizeForWidth(width).height)
        return clamp(textHeight, min: minSize, max: maxSize)
      }
      let textHeight = ceil(
        textForMeasuring
          .heightForWidth(width, maxNumberOfLines: maxVisibleLines)
      )
      return clamp(textHeight, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  private var textForMeasuring: NSAttributedString {
    guard multiLineMode else {
      return defaultTextForMeasuring.with(typo: textTypo)
    }
    let text = textValue.value
    if text.isEmpty {
      if hint.isEmpty {
        return defaultTextForMeasuring.with(typo: textTypo)
      }
      return hint
    }
    return text.with(typo: textTypo)
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? TextInputBlock else {
      return false
    }
    return self == other
  }

  public func getImageHolders() -> [ImageHolder] { [] }
}

extension TextInputBlock {
  public static func ==(lhs: TextInputBlock, rhs: TextInputBlock) -> Bool {
    lhs.widthTrait == rhs.widthTrait
      && lhs.heightTrait == rhs.heightTrait
      && lhs.hint == rhs.hint
      && lhs.layoutDirection == rhs.layoutDirection
      && lhs.textAlignmentHorizontal == rhs.textAlignmentHorizontal
      && lhs.textAlignmentVertical == rhs.textAlignmentVertical
      && lhs.textTypo == rhs.textTypo
      && lhs.textValue.value == rhs.textValue.value
      && lhs.rawTextValue?.value == rhs.rawTextValue?.value
      && lhs.multiLineMode == rhs.multiLineMode
      && lhs.maxVisibleLines == rhs.maxVisibleLines
      && lhs.inputType == rhs.inputType
      && lhs.selectAllOnFocus == rhs.selectAllOnFocus
      && lhs.highlightColor == rhs.highlightColor
  }
}

extension TextInputBlock: LayoutCachingDefaultImpl {}
extension TextInputBlock: ElementStateUpdatingDefaultImpl {}

private let defaultTextForMeasuring = "A"
