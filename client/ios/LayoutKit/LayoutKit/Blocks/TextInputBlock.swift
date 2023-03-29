import CoreGraphics
import Foundation

import BasePublic
import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

public final class TextInputBlock: BlockWithTraits {
  public enum KeyboardType {
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

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let hint: NSAttributedString
  public let textValue: Binding<String>
  public let textTypo: Typo
  public let multiLineMode: Bool
  public let keyboardType: KeyboardType
  public let highlightColor: Color?
  public let maxVisibleLines: Int?
  public let selectAllOnFocus: Bool
  public let path: UIElementPath
  public let onFocusActions: [UserInterfaceAction]
  public let onBlurActions: [UserInterfaceAction]
  public weak var parentScrollView: ScrollView?

  public init(
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    hint: NSAttributedString,
    textValue: Binding<String>,
    textTypo: Typo,
    multiLineMode: Bool = true,
    keyboardType: KeyboardType = .default,
    highlightColor: Color? = nil,
    maxVisibleLines: Int? = nil,
    selectAllOnFocus: Bool = false,
    path: UIElementPath,
    onFocusActions: [UserInterfaceAction] = [],
    onBlurActions: [UserInterfaceAction] = [],
    parentScrollView: ScrollView? = nil
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.hint = hint
    self.textValue = textValue
    self.textTypo = textTypo
    self.multiLineMode = multiLineMode
    self.keyboardType = keyboardType
    self.highlightColor = highlightColor
    self.maxVisibleLines = maxVisibleLines
    self.selectAllOnFocus = selectAllOnFocus
    self.path = path
    self.onFocusActions = onFocusActions
    self.onBlurActions = onBlurActions
    self.parentScrollView = parentScrollView
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, _):
      return minSize
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      guard let maxVisibleLines = maxVisibleLines else {
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
    let text = textValue.wrappedValue
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
      && lhs.textValue.wrappedValue == rhs.textValue.wrappedValue
      && lhs.multiLineMode == rhs.multiLineMode
      && lhs.maxVisibleLines == rhs.maxVisibleLines
      && lhs.keyboardType == rhs.keyboardType
      && lhs.selectAllOnFocus == rhs.selectAllOnFocus
      && lhs.highlightColor == rhs.highlightColor
  }
}

extension TextInputBlock: LayoutCachingDefaultImpl {}
extension TextInputBlock: ElementStateUpdatingDefaultImpl {}

private let defaultTextForMeasuring = "A"
