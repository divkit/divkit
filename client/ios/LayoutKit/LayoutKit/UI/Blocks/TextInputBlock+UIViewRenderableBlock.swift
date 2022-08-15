import UIKit

import BaseUI
import CommonCore
import LayoutKitInterface

extension TextInputBlock {
  public static func makeBlockView() -> BlockView { TextInputBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let inputView = view as! TextInputBlockView
    inputView.text = text
    inputView.backgroundColor = backgroundColor.systemColor
    inputView.setKeyboardAppearance(keyboardAppearance)
    inputView.setKeyboardType(keyboardType)
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TextInputBlockView
  }
}

private final class TextInputBlockView: BlockView, VisibleBoundsTrackingLeaf {
  var text: NSAttributedString? {
    get {
      view.attributedText
    }
    set {
      updateSecureTextStyle(from: newValue)
      view.attributedText = newValue
    }
  }

  private func updateSecureTextStyle(from string: NSAttributedString?) {
    let color: SystemColor = string.attribute(key: .foregroundColor, defaultValue: .black)
    view.typingAttributes[.foregroundColor] = color
  }

  override var backgroundColor: UIColor? {
    didSet {
      view.backgroundColor = backgroundColor
    }
  }

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  func setKeyboardAppearance(_ appearance: TextInputBlock.KeyboardAppearance) {
    view.keyboardAppearance = appearance.uiValue
  }

  func setKeyboardType(_ type: TextInputBlock.KeyboardType) {
    view.keyboardType = type.uiType
  }

  private let view = UITextView()

  override init(frame: CGRect) {
    super.init(frame: frame)

    view.isEditable = true
    view.isSelectable = true
    view.showsVerticalScrollIndicator = false
    view.autocorrectionType = .no
    view.backgroundColor = .clear
    view.textContainerInset = .zero

    addSubview(view)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    view.frame = bounds
  }
}

extension TextInputBlock.KeyboardAppearance {
  fileprivate var uiValue: UIKeyboardAppearance {
    switch self {
    case .default:
      return .default
    case .light:
      return .light
    case .dark:
      return .dark
    }
  }
}

extension UIKeyboardAppearance {
  fileprivate var blockValue: TextInputBlock.KeyboardAppearance {
    switch self {
    case .default, .light:
      return .light
    case .dark:
      return .dark
    @unknown default:
      return .light
    }
  }
}

extension TextInputBlock.KeyboardType {
  fileprivate var uiType: UIKeyboardType {
    switch self {
    case .default:
      return .default
    case .asciiCapable:
      return .asciiCapable
    case .numbersAndPunctuation:
      return .numbersAndPunctuation
    case .URL:
      return .URL
    case .numberPad:
      return .numberPad
    case .phonePad:
      return .phonePad
    case .namePhonePad:
      return .namePhonePad
    case .emailAddress:
      return .emailAddress
    case .decimalPad:
      return .decimalPad
    case .twitter:
      return .twitter
    case .webSearch:
      return .webSearch
    case .asciiCapableNumberPad:
      return .asciiCapableNumberPad
    }
  }
}
