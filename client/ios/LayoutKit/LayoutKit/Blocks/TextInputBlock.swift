import CoreGraphics
import Foundation

import Base
import BaseUI
import CommonCore
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
    self.parentScrollView = parentScrollView
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case .intrinsic,
         .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case .intrinsic:
      let attributedText = getAttributedText()
      guard let maxVisibleLines = maxVisibleLines else {
        let textHeight = attributedText.sizeForWidth(width).height
        return ceil(textHeight)
      }
      let textHeight = attributedText.heightForWidth(width, maxNumberOfLines: maxVisibleLines)
      return ceil(textHeight)
    case .weighted:
      return 0
    }
  }

  private func getAttributedText() -> NSAttributedString {
    let text = textValue.wrappedValue
    if text.isEmpty {
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
