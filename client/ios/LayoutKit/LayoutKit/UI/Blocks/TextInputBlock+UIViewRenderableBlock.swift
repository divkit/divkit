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
    inputView.setText(value: textValue, typo: textTypo)
    inputView.setHint(hint)
    inputView.backgroundColor = backgroundColor.systemColor
    inputView.setKeyboardAppearance(keyboardAppearance)
    inputView.setKeyboardType(keyboardType)
    inputView.setParentScrollView(parentScrollView)
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TextInputBlockView
  }
}

private final class TextInputBlockView: BlockView, VisibleBoundsTrackingLeaf {
  private let view = UITextView()
  private let hintView = UILabel()
  private weak var parentScrollView: ScrollView?
  private var tapGestureRecognizer: UITapGestureRecognizer?
  private var keyboardOpeningInProgress = false
  private var keyboardHeight: CGFloat = 0
  private var textValue: Binding<String>? = nil

  override var backgroundColor: UIColor? {
    didSet {
      view.backgroundColor = backgroundColor
    }
  }

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  override init(frame: CGRect) {
    super.init(frame: frame)

    view.isEditable = true
    view.isSelectable = true
    view.showsVerticalScrollIndicator = false
    view.autocorrectionType = .no
    view.backgroundColor = .clear
    view.textContainerInset = .zero
    view.delegate = self

    hintView.backgroundColor = .clear
    hintView.numberOfLines = 0
    hintView.isUserInteractionEnabled = false
    hintView.isHidden = true
    hintView.contentMode = .center

    addSubview(view)
    addSubview(hintView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    view.frame = bounds
    hintView.frame = bounds
    hintView.frame.origin = CGPoint(x: cusorOffset, y: 0)
    hintView.sizeToFit()
  }

  func setKeyboardAppearance(_ appearance: TextInputBlock.KeyboardAppearance) {
    view.keyboardAppearance = appearance.uiValue
  }

  func setKeyboardType(_ type: TextInputBlock.KeyboardType) {
    view.keyboardType = type.uiType
  }

  func setParentScrollView(_ parentScrollView: ScrollView?) {
    self.parentScrollView = parentScrollView
  }

  func setText(value: Binding<String>, typo: Typo) {
    self.textValue = value
    view.attributedText = value.wrappedValue.with(typo: typo)
    view.typingAttributes = typo.attributes
    updateHintVisibility()
  }

  func setHint(_ value: NSAttributedString) {
    hintView.attributedText = value
  }

  private func updateHintVisibility() {
    hintView.isHidden = !view.attributedText.isEmpty
  }

  override func didMoveToWindow() {
    if window != nil {
      startKeyboardTracking()
    } else {
      stopAllTracking()
    }
  }

  private func startKeyboardTracking() {
    let notificationCenter = NotificationCenter.default
    notificationCenter.addObserver(
      self,
      selector: #selector(keyboardWillShow),
      name: UIResponder.keyboardWillShowNotification,
      object: nil
    )
    notificationCenter.addObserver(
      self,
      selector: #selector(keyboardDidShow),
      name: UIResponder.keyboardDidShowNotification,
      object: nil
    )
    notificationCenter.addObserver(
      self,
      selector: #selector(keyboardWillHide),
      name: UIResponder.keyboardWillHideNotification,
      object: nil
    )
  }

  private func stopAllTracking() {
    NotificationCenter.default.removeObserver(self)
  }

  @objc private func keyboardWillShow(notification: Notification) {
    keyboardOpeningInProgress = true
    if let keyboardFrame = notification
      .userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
      let keyboardRectangle = keyboardFrame.cgRectValue
      keyboardHeight = keyboardRectangle.height
    }
  }

  @objc private func keyboardDidShow(_: Notification) {
    keyboardOpeningInProgress = false
  }

  @objc private func keyboardWillHide(_: Notification) {
    keyboardHeight = 0
  }
}

extension TextInputBlockView: UITextViewDelegate {
  func textViewDidBeginEditing(_ textView: UITextView) {
    let frameInWindow = textView.convert(textView.frame, to: nil)
    let bottomPoint = frameInWindow.maxY + additionalOffset
    scrollToVisible(bottomPoint)
    startListeningTap()
  }

  func textViewDidChange(_ textView: UITextView) {
    updateHintVisibility()
    textValue?.setValue(textView.attributedText.string, responder: textView)
  }

  func textViewDidEndEditing(_ textView: UITextView) {
    stopListeningTap()
  }

  private func startListeningTap() {
    guard tapGestureRecognizer == nil else { return }
    guard let scrollView = parentScrollView else { return }
    let tapRecognizer = UITapGestureRecognizer(
      target: self,
      action: #selector(dissmissKeyboard)
    )
    tapRecognizer.cancelsTouchesInView = false
    scrollView.addGestureRecognizer(tapRecognizer)
    tapGestureRecognizer = tapRecognizer
  }

  @objc private func dissmissKeyboard() {
    view.resignFirstResponder()
  }

  private func stopListeningTap() {
    guard tapGestureRecognizer != nil else { return }
    guard let scrollView = parentScrollView else { return }
    scrollView.removeGestureRecognizer(tapGestureRecognizer!)
    tapGestureRecognizer = nil
  }

  private func scrollToVisible(_ targetY: CGFloat) {
    guard let scrollView = parentScrollView else { return }
    let frameInWindow = scrollView.convert(scrollView.frame, to: nil)
    var visibleY = frameInWindow.maxY + scrollView.contentOffset.y
    if keyboardOpeningInProgress {
      visibleY = visibleY - keyboardHeight
    }
    if targetY > visibleY {
      let scrollPoint = CGPoint(
        x: 0,
        y: scrollView.contentOffset.y + targetY - visibleY
      )
      scrollView.setContentOffset(scrollPoint, animated: true)
    }
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

private let additionalOffset = 25.0
private let cusorOffset = 5
