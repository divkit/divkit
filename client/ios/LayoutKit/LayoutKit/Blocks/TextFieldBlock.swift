import CoreGraphics
import Foundation
import VGSL

public final class TextFieldBlock: Block {
  public struct SideControl: Equatable {
    public let image: Image
    public let tintColor: Color?
    public let action: UserInterfaceAction
    public let accessibilityElement: AccessibilityElement?

    public init(
      image: Image,
      tintColor: Color? = nil,
      action: UserInterfaceAction,
      accessibilityElement: AccessibilityElement? = nil
    ) {
      self.image = image
      self.tintColor = tintColor
      self.action = action
      self.accessibilityElement = accessibilityElement
    }
  }

  public enum ToolbarType: Equatable {
    case `default`(Toolbar)
    case custom(Block)
  }

  public struct Toolbar: Equatable {
    public let doneButtonAction: UserInterfaceAction

    public init(doneButtonAction: UserInterfaceAction) {
      self.doneButtonAction = doneButtonAction
    }
  }

  public enum Placeholders: Equatable {
    case separate(Separate)
    case fieldOnly(NSAttributedString)

    public struct Separate: Equatable {
      public struct AnimatableAttributes: Equatable {
        public let size: FontSize
        public let height: FontLineHeight
        public let color: Color

        public init(size: FontSize, height: FontLineHeight, color: Color) {
          self.size = size
          self.height = height
          self.color = color
        }
      }

      public struct Field: Equatable {
        public enum Position {
          case normal
          case center
        }

        public let attributes: AnimatableAttributes
        public let position: Position

        public init(attributes: AnimatableAttributes, position: Position) {
          self.attributes = attributes
          self.position = position
        }
      }

      public let text: String
      public let headerAttributes: AnimatableAttributes
      public let gap: CGFloat
      public let field: Field
      public let fontWeight: FontWeight

      public init(
        text: String,
        headerAttributes: AnimatableAttributes,
        gap: CGFloat,
        field: Field,
        fontWeight: FontWeight
      ) {
        self.text = text
        self.headerAttributes = headerAttributes
        self.gap = gap
        self.field = field
        self.fontWeight = fontWeight
      }
    }

  }

  public enum TextAutocapitalizationType {
    case none
    case words
    case sentences
    case allCharacters
  }

  public enum TextAutocorrectionType {
    case `default`
    case no
    case yes
  }

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

  public enum Interaction: Equatable {
    case unchanged
    case enabled(isFirstResponder: Bool)
    case disabled
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
  public let placeholders: Placeholders?
  public let backgroundColor: Color
  public let maxIntrinsicNumberOfLines = 1
  public let keyboardAppearance: KeyboardAppearance
  public let updateAction: UserInterfaceAction
  public let returnButtonPressedAction: UserInterfaceAction?
  public let didBeginEditingAction: UserInterfaceAction?
  public let autocapitalizationType: TextAutocapitalizationType
  public let autocorrectionType: TextAutocorrectionType
  public let isSecureTextEntry: Bool
  public let rightControl: SideControl?
  public let toolbar: ToolbarType?
  public let keyboardType: KeyboardType
  public let interaction: Interaction
  public let accessibilityElement: AccessibilityElement?

  public var isVerticallyResizable: Bool { heightTrait.isResizable }
  public var isHorizontallyResizable: Bool { widthTrait.isResizable }

  public var isVerticallyConstrained: Bool { heightTrait.isConstrained }
  public var isHorizontallyConstrained: Bool { widthTrait.isConstrained }

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

  public var widthOfHorizontallyNonResizableBlock: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case .intrinsic,
         .weighted:
      assertionFailure("cannot get widthOfHorizontallyNonResizableBlock for text block")
      return 0
    }
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    guard case let .weighted(value) = heightTrait else {
      assertionFailure("try to get weight for non resizable block")
      return .default
    }
    return value
  }

  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    guard case let .weighted(value) = widthTrait else {
      assertionFailure("try to get weight for non resizable block")
      return .default
    }
    return value
  }

  private var gap: CGFloat {
    switch placeholders {
    case let .separate(placeholders):
      placeholders.gap
    case .fieldOnly, .none:
      0
    }
  }

  public init(
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    text: NSAttributedString,
    placeholders: Placeholders? = nil,
    backgroundColor: Color = .clear,
    keyboardAppearance: KeyboardAppearance = defaultKeyboardAppearance,
    updateAction: UserInterfaceAction,
    returnButtonPressedAction: UserInterfaceAction? = nil,
    didBeginEditingAction: UserInterfaceAction? = nil,
    autocapitalizationType: TextAutocapitalizationType = .none,
    autocorrectionType: TextAutocorrectionType = .no,
    isSecureTextEntry: Bool = false,
    rightControl: SideControl? = nil,
    toolbar: ToolbarType? = nil,
    keyboardType: KeyboardType = .default,
    interaction: Interaction = .unchanged,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.text = text
    self.placeholders = placeholders
    self.backgroundColor = backgroundColor
    self.keyboardAppearance = keyboardAppearance
    self.updateAction = updateAction
    self.returnButtonPressedAction = returnButtonPressedAction
    self.didBeginEditingAction = didBeginEditingAction
    self.autocapitalizationType = autocapitalizationType
    self.autocorrectionType = autocorrectionType
    self.isSecureTextEntry = isSecureTextEntry
    self.rightControl = rightControl
    self.toolbar = toolbar
    self.keyboardType = keyboardType
    self.interaction = interaction
    self.accessibilityElement = accessibilityElement
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let headerHeight = getHeaderHeight(forWidth: width)
      let textHeight = text.heightForWidth(width, maxNumberOfLines: maxIntrinsicNumberOfLines)
      let height = ceil(textHeight + headerHeight + gap)
      return clamp(height, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public func heightOfVerticallyNonResizableBlock(forWidth width: CGFloat) -> CGFloat {
    switch heightTrait {
    case .intrinsic:
      let headerHeight = getHeaderHeight(forWidth: width)
      let textHeight = text.heightForWidth(width, maxNumberOfLines: maxIntrinsicNumberOfLines)
      return ceil(textHeight + headerHeight + gap)
    case let .fixed(value):
      return value
    case .weighted:
      assertionFailure("cannot get heightOfVerticallyNonResizableBlock for weighted block")
      return 0
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? TextFieldBlock else {
      return false
    }
    return self == other
  }

  public func getImageHolders() -> [ImageHolder] { [] }

  private func getHeaderHeight(forWidth _: CGFloat) -> CGFloat {
    switch placeholders {
    case let .separate(placeholders):
      placeholders.headerAttributes.height.rawValue
    case .fieldOnly, .none:
      0
    }
  }

}

extension TextFieldBlock {
  public static func ==(lhs: TextFieldBlock, rhs: TextFieldBlock) -> Bool {
    lhs.widthTrait == rhs.widthTrait
      && lhs.heightTrait == rhs.heightTrait
      && lhs.text == rhs.text
      && lhs.placeholders == rhs.placeholders
      && lhs.keyboardAppearance == rhs.keyboardAppearance
      && lhs.maxIntrinsicNumberOfLines == rhs.maxIntrinsicNumberOfLines
      && lhs.isSecureTextEntry == rhs.isSecureTextEntry
      && lhs.rightControl == rhs.rightControl
      && lhs.toolbar == rhs.toolbar
      && lhs.keyboardType == rhs.keyboardType
      && lhs.interaction == rhs.interaction
      && lhs.accessibilityElement == rhs.accessibilityElement
  }
}

extension TextFieldBlock.SideControl {
  public static func ==(lhs: TextFieldBlock.SideControl, rhs: TextFieldBlock.SideControl) -> Bool {
    imagesDataAreEqual(lhs.image, rhs.image)
      && lhs.tintColor == rhs.tintColor
      && lhs.action == rhs.action
      && lhs.accessibilityElement == rhs.accessibilityElement
  }
}

extension TextFieldBlock.Placeholders.Separate.AnimatableAttributes {
  public static func ==(
    lhs: TextFieldBlock.Placeholders.Separate.AnimatableAttributes,
    rhs: TextFieldBlock.Placeholders.Separate.AnimatableAttributes
  ) -> Bool {
    lhs.size == rhs.size && lhs.color == rhs.color && lhs.height == rhs.height
  }
}

extension TextFieldBlock.ToolbarType {
  public static func ==(
    lhs: TextFieldBlock.ToolbarType,
    rhs: TextFieldBlock.ToolbarType
  ) -> Bool {
    switch (lhs, rhs) {
    case let (.default(lhs), .default(rhs)):
      lhs == rhs
    case let (.custom(lhs), .custom(rhs)):
      lhs == rhs
    default:
      false
    }
  }
}

extension TextFieldBlock: LayoutCachingDefaultImpl {}
extension TextFieldBlock: ElementStateUpdatingDefaultImpl {}
