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
    inputView.setHighlightColor(highlightColor)
    inputView.setKeyboardType(keyboardType)
    inputView.setMultiLineMode(multiLineMode)
    inputView.setSelectAllOnFocus(selectAllOnFocus)
    inputView.setParentScrollView(parentScrollView)
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TextInputBlockView
  }
}

private final class TextInputBlockView: BlockView, VisibleBoundsTrackingLeaf {
  private let multiLineInput = UITextView()
  private let singleLineInput = UITextField()
  private let hintView = UILabel()
  private weak var parentScrollView: ScrollView?
  private var tapGestureRecognizer: UITapGestureRecognizer?
  private var keyboardOpeningInProgress = false
  private var keyboardHeight: CGFloat = 0
  private var textValue: Binding<String>? = nil
  private var selectAllOnFocus = false

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  override init(frame: CGRect) {
    super.init(frame: frame)

    multiLineInput.isEditable = true
    multiLineInput.isSelectable = true
    multiLineInput.showsVerticalScrollIndicator = false
    multiLineInput.autocorrectionType = .no
    multiLineInput.backgroundColor = .clear
    multiLineInput.textContainerInset = .zero
    multiLineInput.delegate = self

    singleLineInput.isHidden = true
    singleLineInput.autocorrectionType = .no
    singleLineInput.backgroundColor = .clear
    singleLineInput.delegate = self
    singleLineInput.addTarget(self, action: #selector(textFieldDidChange), for: .editingChanged)

    hintView.backgroundColor = .clear
    hintView.numberOfLines = 0
    hintView.isUserInteractionEnabled = false
    hintView.isHidden = true
    hintView.contentMode = .center

    addSubview(multiLineInput)
    addSubview(singleLineInput)
    addSubview(hintView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    multiLineInput.frame = bounds
    singleLineInput.frame = bounds
    hintView.frame = bounds
    hintView.frame.origin = CGPoint(x: cusorOffset, y: 0)
    hintView.sizeToFit()
  }

  private var currentText: String {
    guard singleLineInput.isHidden else {
      return singleLineInput.attributedText?.string ?? ""
    }
    return multiLineInput.attributedText.string
  }

  func setKeyboardType(_ type: TextInputBlock.KeyboardType) {
    multiLineInput.keyboardType = type.uiType
    singleLineInput.keyboardType = type.uiType
  }

  func setMultiLineMode(_ multiLineMode: Bool) {
    multiLineInput.isHidden = !multiLineMode
    singleLineInput.isHidden = multiLineMode
  }

  func setHighlightColor(_ color: Color?) {
    multiLineInput.tintColor = color?.systemColor
    singleLineInput.tintColor = color?.systemColor
  }

  func setSelectAllOnFocus(_ selectAllOnFocus: Bool) {
    self.selectAllOnFocus = selectAllOnFocus
  }

  func setParentScrollView(_ parentScrollView: ScrollView?) {
    self.parentScrollView = parentScrollView
  }

  func setText(value: Binding<String>, typo: Typo) {
    self.textValue = value
    let attributedText = value.wrappedValue.with(typo: typo)
    multiLineInput.attributedText = attributedText
    singleLineInput.attributedText = attributedText
    multiLineInput.typingAttributes = typo.attributes
    singleLineInput.typingAttributes = typo.attributes
    updateHintVisibility()
  }

  func setHint(_ value: NSAttributedString) {
    hintView.attributedText = value
  }

  private func updateHintVisibility() {
    hintView.isHidden = !currentText.isEmpty
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

extension TextInputBlockView {
  func inputViewDidBeginEditing(_ view: UIView) {
    let frameInWindow = view.convert(view.frame, to: nil)
    let bottomPoint = frameInWindow.maxY + additionalOffset
    scrollToVisible(bottomPoint)
    startListeningTap()
    if selectAllOnFocus {
      view.selectAll(nil)
    }
  }

  func inputViewDidChange(_ view: UIView) {
    updateHintVisibility()
    textValue?.setValue(currentText, responder: view)
  }

  func inputViewDidEndEditing(_ view: UIView) {
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
    multiLineInput.resignFirstResponder()
    singleLineInput.resignFirstResponder()
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

extension TextInputBlockView: UITextViewDelegate {
  func textViewDidBeginEditing(_ textView: UITextView) {
    inputViewDidBeginEditing(textView)
  }

  func textViewDidChange(_ textView: UITextView) {
    inputViewDidChange(textView)
  }

  func textViewDidEndEditing(_ textView: UITextView) {
    inputViewDidEndEditing(textView)
  }
}

extension TextInputBlockView: UITextFieldDelegate {
  func textFieldDidBeginEditing(_ textField: UITextField) {
    inputViewDidBeginEditing(textField)
  }

  @objc private func textFieldDidChange() {
    inputViewDidChange(singleLineInput)
  }

  func textFieldDidEndEditing(_ textField: UITextField) {
    inputViewDidEndEditing(textField)
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
