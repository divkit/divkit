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

  public enum KeyboardAppearance {
    case `default`
    case light
    case dark
  }

  public static let defaultKeyboardAppearance = KeyboardAppearance.light

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let text: NSAttributedString
  public let keyboardAppearance: KeyboardAppearance
  public let keyboardType: KeyboardType
  public let backgroundColor: Color
  public let maxIntrinsicNumberOfLines = 1
  public weak var parentScrollView: ScrollView?

  public init(
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    text: NSAttributedString,
    keyboardAppearance: KeyboardAppearance = defaultKeyboardAppearance,
    keyboardType: KeyboardType = .default,
    backgroundColor: Color = .clear,
    parentScrollView: ScrollView? = nil
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.text = text
    self.keyboardAppearance = keyboardAppearance
    self.keyboardType = keyboardType
    self.backgroundColor = backgroundColor
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
      let textHeight = text.heightForWidth(width, maxNumberOfLines: maxIntrinsicNumberOfLines)
      return ceil(textHeight)
    case .weighted:
      return 0
    }
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
      && lhs.text == rhs.text
      && lhs.keyboardAppearance == rhs.keyboardAppearance
      && lhs.maxIntrinsicNumberOfLines == rhs.maxIntrinsicNumberOfLines
      && lhs.keyboardType == rhs.keyboardType
  }
}

extension TextInputBlock: LayoutCachingDefaultImpl {}
extension TextInputBlock: ElementStateUpdatingDefaultImpl {}
