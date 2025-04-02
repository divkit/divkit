import CoreGraphics
import Foundation
import VGSL

public typealias TextInputFilter = (String) -> Bool

public final class TextInputBlock: BlockWithTraits {
  public enum InputType: Equatable {
    public enum KeyboardType: Equatable {
      case `default`
      case URL
      case phonePad
      case namePhonePad
      case emailAddress
      case decimalPad
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

  public enum AutocapitalizationType {
    case none
    case words
    case sentences
    case allCharacters
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

  public enum EnterKeyType: Equatable {
    case `default`
    case go
    case search
    case send
    case done
  }

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let hint: NSAttributedString
  public let textValue: Binding<String>
  public let rawTextValue: Binding<String>?
  public let textTypo: Typo
  public let multiLineMode: Bool
  public let inputType: InputType
  public var accessoryView: ViewType?
  public let autocapitalizationType: AutocapitalizationType
  public let enterKeyType: EnterKeyType
  public let highlightColor: Color?
  public let maxVisibleLines: Int?
  public let selectAllOnFocus: Bool
  public let maskValidator: MaskValidator?
  public let path: UIElementPath
  public let isFocused: Bool
  public let onFocusActions: [UserInterfaceAction]
  public let onBlurActions: [UserInterfaceAction]
  public let enterKeyActions: [UserInterfaceAction]
  public let textAlignmentHorizontal: TextAlignmentHorizontal
  public let textAlignmentVertical: TextAlignmentVertical
  public weak var parentScrollView: ScrollView?
  public let filters: [TextInputFilter]?
  public let validators: [TextInputValidator]?
  public let layoutDirection: UserInterfaceLayoutDirection
  public let paddings: EdgeInsets?
  public let isEnabled: Bool
  public let maxLength: Int?
  public let autocorrection: Bool
  public let isSecure: Bool

  let shouldClearFocus: Variable<Bool>

  public init(
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .intrinsic,
    hint: NSAttributedString,
    textValue: Binding<String>,
    rawTextValue: Binding<String>? = nil,
    textTypo: Typo,
    multiLineMode: Bool = true,
    inputType: InputType = .default,
    accessoryView: ViewType? = nil,
    autocapitalizationType: AutocapitalizationType = .sentences,
    enterKeyType: EnterKeyType = .default,
    highlightColor: Color? = nil,
    maxVisibleLines: Int? = nil,
    selectAllOnFocus: Bool = false,
    maskValidator: MaskValidator? = nil,
    path: UIElementPath,
    isFocused: Bool = false,
    onFocusActions: [UserInterfaceAction] = [],
    onBlurActions: [UserInterfaceAction] = [],
    enterKeyActions: [UserInterfaceAction] = [],
    parentScrollView: ScrollView? = nil,
    filters: [TextInputFilter]? = nil,
    validators: [TextInputValidator]? = nil,
    layoutDirection: UserInterfaceLayoutDirection,
    textAlignmentHorizontal: TextAlignmentHorizontal = .start,
    textAlignmentVertical: TextAlignmentVertical = .center,
    paddings: EdgeInsets? = nil,
    isEnabled: Bool = true,
    maxLength: Int? = nil,
    shouldClearFocus: Variable<Bool> = .constant(true),
    autocorrection: Bool = false,
    isSecure: Bool = false
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.hint = hint
    self.textValue = textValue
    self.rawTextValue = rawTextValue
    self.textTypo = textTypo
    self.multiLineMode = multiLineMode
    self.inputType = inputType
    self.accessoryView = accessoryView
    self.autocapitalizationType = autocapitalizationType
    self.enterKeyType = enterKeyType
    self.highlightColor = highlightColor
    self.maxVisibleLines = maxVisibleLines
    self.selectAllOnFocus = selectAllOnFocus
    self.maskValidator = maskValidator
    self.path = path
    self.isFocused = isFocused
    self.onFocusActions = onFocusActions
    self.onBlurActions = onBlurActions
    self.enterKeyActions = enterKeyActions
    self.parentScrollView = parentScrollView
    self.filters = filters
    self.validators = validators
    self.layoutDirection = layoutDirection
    self.textAlignmentHorizontal = textAlignmentHorizontal
    self.textAlignmentVertical = textAlignmentVertical
    self.paddings = paddings
    self.isEnabled = isEnabled
    self.maxLength = maxLength
    self.shouldClearFocus = shouldClearFocus
    self.autocorrection = autocorrection
    self.isSecure = isSecure
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
      let width = width - (paddings?.horizontal.sum ?? 0)
      let verticalPaddings = paddings?.vertical.sum ?? 0
      guard let maxVisibleLines else {
        let textHeight = ceil(textForMeasuring.sizeForWidth(width).height)
        return clamp(textHeight + verticalPaddings, min: minSize, max: maxSize)
      }
      let textHeight = ceil(
        textForMeasuring
          .heightForWidth(width, maxNumberOfLines: maxVisibleLines)
      )
      return clamp(textHeight + verticalPaddings, min: minSize, max: maxSize)
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
    lhs.autocapitalizationType == rhs.autocapitalizationType
      && lhs.enterKeyType == rhs.enterKeyType
      && lhs.enterKeyActions == rhs.enterKeyActions
      && lhs.heightTrait == rhs.heightTrait
      && lhs.highlightColor == rhs.highlightColor
      && lhs.hint == rhs.hint
      && lhs.inputType == rhs.inputType
      && lhs.isEnabled == rhs.isEnabled
      && lhs.isFocused == rhs.isFocused
      && lhs.layoutDirection == rhs.layoutDirection
      && lhs.maxVisibleLines == rhs.maxVisibleLines
      && lhs.multiLineMode == rhs.multiLineMode
      && lhs.paddings == rhs.paddings
      && lhs.rawTextValue?.value == rhs.rawTextValue?.value
      && lhs.selectAllOnFocus == rhs.selectAllOnFocus
      && lhs.textAlignmentHorizontal == rhs.textAlignmentHorizontal
      && lhs.textAlignmentVertical == rhs.textAlignmentVertical
      && lhs.textTypo == rhs.textTypo
      && lhs.textValue.value == rhs.textValue.value
      && lhs.widthTrait == rhs.widthTrait
      && lhs.autocorrection == rhs.autocorrection
      && lhs.isSecure == rhs.isSecure
  }
}

extension TextInputBlock: LayoutCachingDefaultImpl {}
extension TextInputBlock: ElementStateUpdatingDefaultImpl {}
extension TextInputBlock: ElementFocusUpdating {
  public func updated(path: UIElementPath, isFocused: Bool) throws -> TextInputBlock {
    guard path == self.path,
          isFocused != self.isFocused else {
      return self
    }

    return TextInputBlock(
      widthTrait: widthTrait,
      heightTrait: heightTrait,
      hint: hint,
      textValue: textValue,
      rawTextValue: rawTextValue,
      textTypo: textTypo,
      multiLineMode: multiLineMode,
      inputType: inputType,
      accessoryView: accessoryView,
      autocapitalizationType: autocapitalizationType,
      enterKeyType: enterKeyType,
      highlightColor: highlightColor,
      maxVisibleLines: maxVisibleLines,
      selectAllOnFocus: selectAllOnFocus,
      maskValidator: maskValidator,
      path: path,
      isFocused: isFocused,
      onFocusActions: onFocusActions,
      onBlurActions: onBlurActions,
      enterKeyActions: enterKeyActions,
      parentScrollView: parentScrollView,
      filters: filters,
      validators: validators,
      layoutDirection: layoutDirection,
      textAlignmentHorizontal: textAlignmentHorizontal,
      textAlignmentVertical: textAlignmentVertical,
      paddings: paddings,
      isEnabled: isEnabled,
      maxLength: maxLength,
      autocorrection: autocorrection,
      isSecure: isSecure
    )
  }
}

extension TextInputBlock: PathHolder {}

extension TextInputBlock {
  public func modifying(
    widthTrait: LayoutTrait? = nil,
    heightTrait: LayoutTrait? = nil,
    hint: NSAttributedString? = nil,
    textValue: Binding<String>? = nil,
    rawTextValue: Binding<String>? = nil,
    textTypo: Typo? = nil,
    multiLineMode: Bool? = nil,
    inputType: InputType? = nil,
    accessoryView: ViewType? = nil,
    autocapitalizationType: AutocapitalizationType? = nil,
    enterKeyType: EnterKeyType? = nil,
    highlightColor: Color? = nil,
    maxVisibleLines: Int? = nil,
    selectAllOnFocus: Bool? = nil,
    maskValidator: MaskValidator? = nil,
    path: UIElementPath? = nil,
    isFocused: Bool? = nil,
    onFocusActions: [UserInterfaceAction]? = nil,
    onBlurActions: [UserInterfaceAction]? = nil,
    enterKeyActions: [UserInterfaceAction]? = nil,
    parentScrollView: ScrollView? = nil,
    filters: [TextInputFilter]? = nil,
    validators: [TextInputValidator]? = nil,
    layoutDirection: UserInterfaceLayoutDirection? = nil,
    textAlignmentHorizontal: TextAlignmentHorizontal? = nil,
    textAlignmentVertical: TextAlignmentVertical? = nil,
    paddings: EdgeInsets? = nil,
    isEnabled: Bool? = nil,
    maxLength: Int? = nil,
    shouldClearFocus: Variable<Bool>? = nil,
    autocorrection: Bool? = nil,
    isSecure: Bool? = nil
  ) -> TextInputBlock {
    TextInputBlock(
      widthTrait: widthTrait ?? self.widthTrait,
      heightTrait: heightTrait ?? self.heightTrait,
      hint: hint ?? self.hint,
      textValue: textValue ?? self.textValue,
      rawTextValue: rawTextValue ?? self.rawTextValue,
      textTypo: textTypo ?? self.textTypo,
      multiLineMode: multiLineMode ?? self.multiLineMode,
      inputType: inputType ?? self.inputType,
      accessoryView: accessoryView ?? self.accessoryView,
      autocapitalizationType: autocapitalizationType ?? self.autocapitalizationType,
      enterKeyType: enterKeyType ?? self.enterKeyType,
      highlightColor: highlightColor ?? self.highlightColor,
      maxVisibleLines: maxVisibleLines ?? self.maxVisibleLines,
      selectAllOnFocus: selectAllOnFocus ?? self.selectAllOnFocus,
      maskValidator: maskValidator ?? self.maskValidator,
      path: path ?? self.path,
      isFocused: isFocused ?? self.isFocused,
      onFocusActions: onFocusActions ?? self.onFocusActions,
      onBlurActions: onBlurActions ?? self.onBlurActions,
      enterKeyActions: enterKeyActions ?? self.enterKeyActions,
      parentScrollView: parentScrollView ?? self.parentScrollView,
      filters: filters ?? self.filters,
      validators: validators ?? self.validators,
      layoutDirection: layoutDirection ?? self.layoutDirection,
      textAlignmentHorizontal: textAlignmentHorizontal ?? self.textAlignmentHorizontal,
      textAlignmentVertical: textAlignmentVertical ?? self.textAlignmentVertical,
      paddings: paddings ?? self.paddings,
      isEnabled: isEnabled ?? self.isEnabled,
      maxLength: maxLength ?? self.maxLength,
      shouldClearFocus: shouldClearFocus ?? self.shouldClearFocus,
      autocorrection: autocorrection ?? self.autocorrection,
      isSecure: isSecure ?? self.isSecure
    )
  }
}

private let defaultTextForMeasuring = "A"
