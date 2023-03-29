import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

extension TextInputBlock {
  public static func makeBlockView() -> BlockView { TextInputBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let inputView = view as! TextInputBlockView
    inputView.setText(value: textValue, typo: textTypo)
    inputView.setHint(hint)
    inputView.setHighlightColor(highlightColor)
    inputView.setKeyboardType(keyboardType)
    inputView.setMultiLineMode(multiLineMode)
    inputView.setMaxVisibleLines(maxVisibleLines)
    inputView.setSelectAllOnFocus(selectAllOnFocus)
    inputView.setParentScrollView(parentScrollView)
    inputView.setOnFocusActions(onFocusActions)
    inputView.setOnBlurActions(onBlurActions)
    inputView.setPath(path)
    inputView.setObserver(observer)
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
  private var scrollingWasDone = false
  private var textValue: Binding<String>?
  private var selectAllOnFocus = false
  private var onFocusActions: [UserInterfaceAction] = []
  private var onBlurActions: [UserInterfaceAction] = []
  private var path: UIElementPath?
  private weak var observer: ElementStateObserver?
  private var isRightToLeft = false

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  override init(frame: CGRect) {
    super.init(frame: frame)

    isRightToLeft = UIView
      .userInterfaceLayoutDirection(for: singleLineInput.semanticContentAttribute) == .rightToLeft

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
    updateMultiLineOffset()
    singleLineInput.frame = bounds
    hintView.frame = bounds
    hintView.sizeToFit()
    hintView.frame.origin = CGPoint(x: offsetX(hintView), y: offsetY(hintView))

    let tapGesture = UITapGestureRecognizer(target: self, action: #selector(onTapGesture(sender:)))
    addGestureRecognizer(tapGesture)
  }

  @objc func onTapGesture(sender _: UITapGestureRecognizer) {
    if singleLineInput.isHidden {
      multiLineInput.becomeFirstResponder()
    } else {
      singleLineInput.becomeFirstResponder()
    }
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

  func setMaxVisibleLines(_ maxVisibleLines: Int?) {
    hintView.numberOfLines = maxVisibleLines ?? 0
  }

  func setSelectAllOnFocus(_ selectAllOnFocus: Bool) {
    self.selectAllOnFocus = selectAllOnFocus
  }

  func setParentScrollView(_ parentScrollView: ScrollView?) {
    self.parentScrollView = parentScrollView
  }

  func setOnFocusActions(_ onFocusActions: [UserInterfaceAction]) {
    self.onFocusActions = onFocusActions
  }

  func setOnBlurActions(_ onBlurActions: [UserInterfaceAction]) {
    self.onBlurActions = onBlurActions
  }

  func setPath(_ path: UIElementPath) {
    self.path = path
  }

  func setObserver(_ observer: ElementStateObserver?) {
    self.observer = observer
  }

  func setText(value: Binding<String>, typo: Typo) {
    let textTypo = isRightToLeft ? typo.with(alignment: .right) : typo
    self.textValue = value
    let attributedText = value.wrappedValue.with(typo: textTypo)
    multiLineInput.attributedText = attributedText
    singleLineInput.attributedText = attributedText
    multiLineInput.typingAttributes = typo.attributes
    singleLineInput.typingAttributes = typo.attributes
    updateHintVisibility()
    updateMultiLineOffset()
  }

  func setHint(_ value: NSAttributedString) {
    hintView.attributedText = value
  }

  private func updateHintVisibility() {
    hintView.isHidden = !currentText.isEmpty
  }

  private func updateMultiLineOffset() {
    guard !multiLineInput.isHidden else { return }
    multiLineInput.frame.origin = CGPoint(x: 0, y: multiLineOffsetY)
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
  }

  private func stopAllTracking() {
    NotificationCenter.default.removeObserver(self)
  }

  @objc private func keyboardWillShow(notification: Notification) {
    if let keyboardFrame = notification
      .userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
      let keyboardRectangle = keyboardFrame.cgRectValue
      let keyboardHeight = keyboardRectangle.height
      tryScrollToMultiLine(keyboardHeight)
      tryScrollToSingleLine(keyboardHeight)
    }
  }

  private func tryScrollToMultiLine(_ keyboardHeight: CGFloat) {
    guard !scrollingWasDone, multiLineInput.isFirstResponder,
          !multiLineInput.isHidden else { return }
    let frameInWindow = multiLineInput.convert(multiLineInput.frame, to: nil)
    let cursorPoint = frameInWindow.origin.y + multiLineInput.contentSize
      .height - multiLineOffsetY + additionalOffset
    scrollToVisible(targetY: cursorPoint, keyboardHeight: keyboardHeight)
  }

  private func tryScrollToSingleLine(_ keyboardHeight: CGFloat) {
    guard !scrollingWasDone, singleLineInput.isFirstResponder,
          !singleLineInput.isHidden else { return }
    let frameInWindow = singleLineInput.convert(singleLineInput.frame, to: nil)
    let bottomPoint = frameInWindow.maxY + additionalOffset
    scrollToVisible(targetY: bottomPoint, keyboardHeight: keyboardHeight)
  }

  private func scrollToVisible(targetY: CGFloat, keyboardHeight: CGFloat) {
    guard let scrollView = parentScrollView else { return }
    let scrollPoint = CGPoint(
      x: scrollView.contentOffset.x,
      y: scrollView.contentOffset.y
    )
    scrollView.setContentOffset(scrollPoint, animated: false)
    let frameInWindow = scrollView.convert(scrollView.frame, to: nil)
    var visibleY = frameInWindow.maxY + scrollView.contentOffset.y
    let scrollViewBottomOffset = UIScreen.main.bounds.height - visibleY
    var hiddenScrollViewHeight = keyboardHeight - scrollViewBottomOffset
    if hiddenScrollViewHeight < 0 {
      hiddenScrollViewHeight = 0
    }
    visibleY = visibleY - hiddenScrollViewHeight
    if targetY > visibleY {
      let scrollPoint = CGPoint(
        x: scrollView.contentOffset.x,
        y: scrollView.contentOffset.y + targetY - visibleY
      )
      scrollView.setContentOffset(scrollPoint, animated: false)
    }
    scrollingWasDone = true
  }
}

extension TextInputBlockView {
  func inputViewDidBeginEditing(_ view: UIView) {
    startListeningTap()
    if selectAllOnFocus {
      view.selectAll(nil)
    }
    onFocus()
  }

  func inputViewDidChange(_ view: UIView) {
    updateHintVisibility()
    textValue?.setValue(currentText, responder: view)
  }

  func inputViewDidEndEditing(_: UIView) {
    stopListeningTap()
    scrollingWasDone = false
    onBlur()
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

  private func onFocus() {
    onFocusActions.perform(sendingFrom: self)
    guard let path = path else { return }
    observer?.elementStateChanged(FocusViewState(isFocused: true), forPath: path)
  }

  private func onBlur() {
    onBlurActions.perform(sendingFrom: self)
    guard let path = path else { return }
    observer?.elementStateChanged(FocusViewState(isFocused: false), forPath: path)
  }
}

extension TextInputBlockView: UITextViewDelegate {
  func textViewDidBeginEditing(_ textView: UITextView) {
    inputViewDidBeginEditing(textView)
  }

  func textViewDidChange(_ textView: UITextView) {
    updateMultiLineOffset()
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

extension TextInputBlockView {
  private var multiLineOffsetY: CGFloat {
    let emptySpace = multiLineInput.bounds.size.height - multiLineInput.contentSize.height
    guard emptySpace > 0 else { return 0 }
    return emptySpace / 2
  }

  private func offsetX(_ view: UIView) -> CGFloat {
    if isRightToLeft {
      let emptySpace = bounds.size.width - view.bounds.size.width
      guard emptySpace > 0 else { return 0 }
      return emptySpace - cusorOffset
    }
    return cusorOffset
  }

  private func offsetY(_ view: UIView) -> CGFloat {
    let emptySpace = bounds.size.height - view.bounds.size.height
    guard emptySpace > 0 else { return 0 }
    return emptySpace / 2
  }

  private var cusorOffset: CGFloat {
    if singleLineInput.isHidden {
      return multiLineCusorOffset
    } else {
      return singleLineCusorOffset
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
private let singleLineCusorOffset = 2.0
private let multiLineCusorOffset = 5.0
