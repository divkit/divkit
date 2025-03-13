import UIKit
import VGSL

extension TextInputBlock {
  public static func makeBlockView() -> BlockView { TextInputBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let inputView = view as! TextInputBlockView
    inputView.setLayoutDirection(layoutDirection)
    inputView.setInputType(inputType)
    inputView.setInputAccessoryView(accessoryView)
    inputView.setAutocorrection(autocorrection)
    inputView.setSecure(isSecure)
    inputView.setAutocapitalizationType(autocapitalizationType)
    inputView.setEnterKeyType(enterKeyType)
    inputView.setValidators(validators)
    inputView.setFilters(filters)
    inputView.setTextAlignmentHorizontal(textAlignmentHorizontal)
    inputView.setTextAlignmentVertical(textAlignmentVertical)
    inputView.setText(
      textValue: textValue,
      rawTextValue: rawTextValue,
      typo: textTypo,
      mask: maskValidator
    )
    inputView.setHint(hint)
    inputView.setHighlightColor(highlightColor)
    inputView.setMultiLineMode(multiLineMode)
    inputView.setMaxVisibleLines(maxVisibleLines)
    inputView.setSelectAllOnFocus(selectAllOnFocus)
    inputView.setParentScrollView(parentScrollView)
    inputView.setIsFocused(isFocused, shouldClear: shouldClearFocus.value)
    inputView.setOnFocusActions(onFocusActions)
    inputView.setOnBlurActions(onBlurActions)
    inputView.setEnterKeyActions(enterKeyActions)
    inputView.setPath(path)
    inputView.setObserver(observer)
    inputView.setIsEnabled(isEnabled)
    inputView.setMaxLength(maxLength)
    inputView.paddings = paddings ?? .zero
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TextInputBlockView
  }
}

private final class TextInputBlockView: BlockView, VisibleBoundsTrackingLeaf {
  private let multiLineInput = PatchedUITextView()
  private let singleLineInput = PatchedUITextField()
  private let selectionView = UIPickerView()
  private let hintView = UILabel()
  private weak var parentScrollView: ScrollView?
  private var tapGestureRecognizer: UITapGestureRecognizer?
  private var scrollingWasDone = false
  private var textValue: Binding<String>
  private var rawTextValue: Binding<String>
  private var selectAllOnFocus = false
  private var maskedViewModel: MaskedInputViewModel?
  private var onFocusActions: [UserInterfaceAction] = []
  private var onBlurActions: [UserInterfaceAction] = []
  private var enterKeyActions: [UserInterfaceAction] = []
  private var path: UIElementPath?
  private weak var observer: ElementStateObserver?
  private var typo: Typo?
  private var selectionItems: [TextInputBlock.InputType.SelectionItem]?
  private let userInputPipe = SignalPipe<MaskedInputViewModel.Action>()
  private var filters: [TextInputFilter]?
  private var validators: [TextInputValidator]?
  private let disposePool = AutodisposePool()
  private var layoutDirection: UserInterfaceLayoutDirection = .leftToRight
  private var textAlignmentHorizontal: TextInputBlock.TextAlignmentHorizontal = .start
  private var textAlignmentVertical: TextInputBlock.TextAlignmentVertical = .center
  private var isInputFocused = false
  private var keyboardHeight: CGFloat?
  private var maxLength: Int?

  var paddings: EdgeInsets = .zero

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  override init(frame: CGRect) {
    textValue = .zero
    rawTextValue = .zero
    super.init(frame: frame)

    multiLineInput.isEditable = true
    multiLineInput.isSelectable = true
    multiLineInput.showsVerticalScrollIndicator = false
    multiLineInput.backgroundColor = .clear
    multiLineInput.delegate = self
    multiLineInput.textContainer.lineFragmentPadding = 0
    multiLineInput.returnKeyType = .default

    singleLineInput.isHidden = true
    singleLineInput.backgroundColor = .clear
    singleLineInput.delegate = self
    singleLineInput.addTarget(self, action: #selector(textFieldDidChange), for: .editingChanged)
    singleLineInput.contentVerticalAlignment = .top
    singleLineInput.returnKeyType = .default

    hintView.backgroundColor = .clear
    hintView.numberOfLines = 0
    hintView.isUserInteractionEnabled = false
    hintView.isHidden = true

    selectionView.delegate = self
    selectionView.dataSource = self

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
    multiLineInput.textContainerInset = paddings
    updateScrollOnMultilineChange()
    updateMultiLineOffset()

    singleLineInput.frame = bounds
    singleLineInput.paddings = paddings
    updateSingleLineOffset()

    hintView.frame.size = hintView.sizeThatFits(bounds.size.inset(by: paddings))
    updateHintViewOffset()

    let tapGesture = UITapGestureRecognizer(target: self, action: #selector(onTapGesture(sender:)))
    addGestureRecognizer(tapGesture)
  }

  @objc func onTapGesture(sender _: UITapGestureRecognizer) {
    focusTextInput()
  }

  private func focusTextInput() {
    if singleLineInput.isHidden {
      multiLineInput.becomeFirstResponder()
    } else {
      singleLineInput.becomeFirstResponder()
    }
  }

  private func clearFocus() {
    if singleLineInput.isHidden {
      if multiLineInput.isFirstResponder {
        multiLineInput.resignFirstResponder()
      }
    } else {
      if singleLineInput.isFirstResponder {
        singleLineInput.resignFirstResponder()
      }
    }
  }

  private var currentText: String {
    guard singleLineInput.isHidden else {
      return singleLineInput.attributedText?.string ?? ""
    }
    return multiLineInput.attributedText.string
  }

  func setLayoutDirection(_ layoutDirection: UserInterfaceLayoutDirection) {
    if layoutDirection != self.layoutDirection {
      self.layoutDirection = layoutDirection
      updateHintViewOffset()
    }
  }

  func setInputType(_ type: TextInputBlock.InputType) {
    switch type {
    case let .keyboard(type):
      multiLineInput.inputView = nil
      multiLineInput.tintColor = nil
      selectionItems = nil
      setKeyboardType(type)
    case let .selection(items):
      selectionItems = items
      multiLineInput.tintColor = multiLineInput.backgroundColor
      multiLineInput.inputView = selectionView
      setKeyboardType(.default)
    }
  }

  private func setKeyboardType(_ type: TextInputBlock.InputType.KeyboardType) {
    multiLineInput.keyboardType = type.uiType
    singleLineInput.keyboardType = type.uiType
  }

  func setInputAccessoryView(_ accessoryView: ViewType?) {
    multiLineInput.inputAccessoryView = accessoryView
    singleLineInput.inputAccessoryView = accessoryView
  }

  func setAutocorrection(_ isEnabled: Bool) {
    let uiType: UITextAutocorrectionType = isEnabled ? .yes : .no
    multiLineInput.autocorrectionType = uiType
    singleLineInput.autocorrectionType = uiType
  }

  func setSecure(_ isSecure: Bool) {
    multiLineInput.isSecureTextEntry = isSecure
    singleLineInput.isSecureTextEntry = isSecure
  }

  func setAutocapitalizationType(_ type: TextInputBlock.AutocapitalizationType) {
    singleLineInput.autocapitalizationType = type.uiType
    multiLineInput.autocapitalizationType = type.uiType
  }

  func setEnterKeyType(_ type: TextInputBlock.EnterKeyType) {
    singleLineInput.returnKeyType = type.uiType
    multiLineInput.returnKeyType = type.uiType
  }

  func setTextAlignmentHorizontal(_ alignment: TextInputBlock.TextAlignmentHorizontal) {
    textAlignmentHorizontal = alignment
  }

  func setTextAlignmentVertical(_ alignment: TextInputBlock.TextAlignmentVertical) {
    textAlignmentVertical = alignment
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

  func setIsFocused(_ isFocused: Bool, shouldClear: Bool) {
    isInputFocused = isFocused
    if isFocused {
      if allSuperviewsAreVisible() {
        focusTextInput()
      }
    } else if shouldClear {
      clearFocus()
    }
  }

  func setIsEnabled(_ isEnabled: Bool) {
    if singleLineInput.isHidden {
      multiLineInput.isEditable = isEnabled
    } else {
      singleLineInput.isEnabled = isEnabled
    }
  }

  func setOnFocusActions(_ onFocusActions: [UserInterfaceAction]) {
    self.onFocusActions = onFocusActions
  }

  func setOnBlurActions(_ onBlurActions: [UserInterfaceAction]) {
    self.onBlurActions = onBlurActions
  }

  func setEnterKeyActions(_ actions: [UserInterfaceAction]) {
    self.enterKeyActions = actions
  }

  func setPath(_ path: UIElementPath) {
    self.path = path
  }

  func setObserver(_ observer: ElementStateObserver?) {
    self.observer = observer
  }

  func setText(
    textValue: Binding<String>,
    rawTextValue: Binding<String>?,
    typo: Typo,
    mask: MaskValidator?
  ) {
    self.typo = typo.with(alignment: textAlignment)
    self.textValue = textValue
    if let mask, let rawTextValue {
      setupMaskedViewModelIfNeeded(mask: mask, rawTextValue: rawTextValue)
    } else {
      let text: String = if let selectionItems = self.selectionItems {
        selectionItems.first { $0.value == textValue.value }?.text ?? ""
      } else {
        textValue.value
      }
      setTextData(text)
    }
    updateHintVisibility()
    updateMultiLineOffset()
    updateSingleLineOffset()
    updateValidation()
  }

  func setValidators(_ validators: [TextInputValidator]?) {
    self.validators = validators
  }

  func setFilters(_ filters: [TextInputFilter]?) {
    self.filters = filters
  }

  func setHint(_ value: NSAttributedString) {
    hintView.attributedText = value
    setNeedsLayout()
  }

  func setMaxLength(_ value: Int?) {
    maxLength = value
  }

  private func updateHintVisibility() {
    hintView.isHidden = !currentText.isEmpty
  }

  private func updateMultiLineOffset() {
    guard !multiLineInput.isHidden else { return }
    multiLineInput.frame.origin = CGPoint(x: 0, y: multiLineOffsetY)
  }

  private func updateSingleLineOffset() {
    guard !singleLineInput.isHidden else { return }
    singleLineInput.frame.origin = CGPoint(x: 0, y: singleLineOffsetY)
  }

  private func updateScrollOnMultilineChange() {
    guard let keyboardHeight else { return }
    scrollingWasDone = false
    tryScrollToMultiLine(keyboardHeight)
  }

  private func updateHintViewOffset() {
    guard !hintView.isHidden else { return }
    hintView.frame.origin = CGPoint(x: hintViewOffsetX, y: hintViewOffsetY)
  }

  private func updateValidation() {
    let accessibilityLabel = validators?.compactMap {
      let isValid = $0.validate(currentText)
      $0.isValid.value = isValid
      return isValid ? nil : $0.message()
    }.joined(separator: ". ")
    singleLineInput.accessibilityLabel = accessibilityLabel
    multiLineInput.accessibilityLabel = accessibilityLabel
  }

  private func setTextData(_ text: String) {
    guard let typo else { return }
    let attributedText = text.with(typo: typo)
    multiLineInput.attributedText = attributedText
    if let selectedRange = singleLineInput.selectedTextRange {
      singleLineInput.attributedText = attributedText
      singleLineInput.selectedTextRange = selectedRange
    }
    multiLineInput.typingAttributes = typo.attributes
    singleLineInput.defaultTextAttributes = typo.attributes
  }

  private func setupMaskedViewModelIfNeeded(mask: MaskValidator, rawTextValue: Binding<String>) {
    self.rawTextValue = rawTextValue
    guard self.maskedViewModel == nil else {
      maskedViewModel?.rawText = rawTextValue.value
      maskedViewModel?.maskValidator = mask
      maskedViewModel?.typo = typo
      return
    }
    self.maskedViewModel = MaskedInputViewModel(
      rawText: self.rawTextValue.value,
      maskValidator: mask,
      typo: typo,
      signal: userInputPipe.signal
    )
    maskedViewModel?.$cursorPosition.currentAndNewValues.addObserver { [weak self] range in
      guard let self, let range else { return }
      DispatchQueue.main.async {
        self.multiLineInput.selectedRange = range
        if let textFieldPosition = self.singleLineInput.position(
          from: self.singleLineInput.beginningOfDocument,
          offset: range.location
        ), self.singleLineInput.selectedTextRange?.start != textFieldPosition {
          self.singleLineInput.selectedTextRange = self.singleLineInput.textRange(
            from: textFieldPosition,
            to: textFieldPosition
          )
        }
      }
    }.dispose(in: disposePool)
    maskedViewModel?.$rawText.currentAndNewValues.addObserver { [weak self] input in
      guard let self else { return }
      self.rawTextValue.value = input
    }.dispose(in: disposePool)

    maskedViewModel?.$text.currentAndNewValues
      .addObserver { [weak self] input in
        guard let self else { return }
        DispatchQueue.main.async {
          self.setTextData(input)
          self.textValue.value = input
          self.updateHintVisibility()
        }
      }.dispose(in: disposePool)
  }

  override func didMoveToWindow() {
    if window != nil {
      startKeyboardTracking()
      if isInputFocused {
        // The didMoveToWindow method in UIView is not a direct replacement for the viewDidAppear
        // method in UIViewController.
        // This causes the focusTextInput method to be called too early, before the view is actually
        // in the view hierarchy.
        // As a result, the keyboard does not appear as expected.
        DispatchQueue.main.async {
          self.focusTextInput()
        }
      }
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
      self.keyboardHeight = keyboardHeight
      tryScrollToMultiLine(keyboardHeight)
      tryScrollToSingleLine(keyboardHeight)
    }
  }

  private func tryScrollToMultiLine(_ keyboardHeight: CGFloat) {
    guard !scrollingWasDone, multiLineInput.isFirstResponder,
          !multiLineInput.isHidden else { return }

    let frameInWindow = if let textRange = multiLineInput.selectedTextRange {
      multiLineInput.convert(
        multiLineInput.caretRect(for: textRange.start),
        to: nil
      )
    } else {
      multiLineInput.convert(multiLineInput.bounds, to: nil)
    }

    let cursorPoint = frameInWindow.origin.y + additionalOffset
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
    if let pickerView = multiLineInput.inputView as? UIPickerView {
      let index = selectionItems?.firstIndex { $0.value == textValue.value } ?? 0
      pickerView.selectRow(index, inComponent: 0, animated: false)
      textValue.value = selectionItems?[index].value ?? ""
    }

    startListeningTap()
    if selectAllOnFocus {
      view.selectAll(nil)
    }
    onFocus()
  }

  func inputViewDidChange(_: UIView) {
    updateHintVisibility()
    textValue.value = currentText
  }

  func inputViewDidEndEditing(_: UIView) {
    stopListeningTap()
    scrollingWasDone = false
    keyboardHeight = nil
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
    isInputFocused = true
    guard let path else { return }
    observer?.focusedElementChanged(isFocused: true, forPath: path)
  }

  private func onBlur() {
    onBlurActions.perform(sendingFrom: self)
    isInputFocused = false
    guard let path else { return }
    observer?.focusedElementChanged(isFocused: false, forPath: path)
  }

  private func inputViewReplaceTextIn(
    view _: UIView,
    range: Range<String.Index>,
    text: String
  ) -> Bool {
    if maskedViewModel != nil {
      if text == "" {
        if range.isEmpty {
          if range.lowerBound > currentText.startIndex {
            userInputPipe.send(.clear(pos: currentText.index(before: range.lowerBound)))
          }
        } else {
          userInputPipe
            .send(.clearRange(range: range))
        }
      } else {
        userInputPipe.send(.insert(string: text, range: range))
      }
      return false
    }

    if let filters, text != "" {
      return filters.allSatisfy { filter in
        filter(currentText + text)
      }
    }

    if let maxLength, text != "" {
      let updatedText = currentText.replacingCharactersInRange(range, withString: text)
      return updatedText.result.count <= maxLength
    }

    return true
  }
}

extension TextInputBlockView: UITextViewDelegate {
  func textViewDidBeginEditing(_ textView: UITextView) {
    inputViewDidBeginEditing(textView)
  }

  func textViewDidChange(_ textView: UITextView) {
    updateMultiLineOffset()
    inputViewDidChange(textView)
    updateScrollOnMultilineChange()
  }

  func textViewDidEndEditing(_ textView: UITextView) {
    inputViewDidEndEditing(textView)
  }

  func textView(
    _: UITextView,
    shouldChangeTextIn _: NSRange,
    replacementText text: String
  ) -> Bool {
    guard let range = Range(multiLineInput.selectedRange, in: currentText) else {
      return false
    }
    return inputViewReplaceTextIn(view: self, range: range, text: text)
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

  func textField(
    _ textField: UITextField,
    shouldChangeCharactersIn range: NSRange,
    replacementString string: String
  ) -> Bool {
    let nsRange: NSRange
    if let selectedRange = textField.selectedTextRange {
      let location = textField.offset(from: textField.beginningOfDocument, to: selectedRange.start)
      let length = textField.offset(from: selectedRange.start, to: selectedRange.end)
      nsRange = NSRange(location: location, length: length)
    } else {
      nsRange = range
    }

    guard let range = Range(nsRange, in: currentText) else {
      return false
    }

    return inputViewReplaceTextIn(view: textField, range: range, text: string)
  }

  func textFieldShouldReturn(_: UITextField) -> Bool {
    guard !enterKeyActions.isEmpty else {
      return true
    }

    enterKeyActions.perform(sendingFrom: self)
    return false
  }
}

extension TextInputBlockView {
  private var hintViewOffsetX: CGFloat {
    let emptySpace = bounds.size.width - hintView.bounds.size.width
    switch textAlignment {
    case .left:
      return cusorOffset + paddings.left
    case .center:
      guard emptySpace > 0 else { return 0 }
      return (emptySpace - cusorOffset + paddings.left - paddings.right) / 2
    case .right:
      guard emptySpace > 0 else { return 0 }
      return emptySpace - cusorOffset - paddings.right
    default:
      assertionFailure("Only .left, .center and .right cases of textAlignment are implemented")
      return cusorOffset
    }
  }

  private var multiLineOffsetY: CGFloat {
    offsetY(
      emptySpace: multiLineInput.bounds.height - multiLineInput.contentSize.height
    )
  }

  private var singleLineOffsetY: CGFloat {
    offsetY(
      emptySpace: singleLineInput.bounds.height - singleLineInput.intrinsicContentSize.height
    )
  }

  private var hintViewOffsetY: CGFloat {
    offsetY(
      emptySpace: bounds.size.height - hintView.bounds.size.height,
      paddings: paddings
    )
  }

  private func offsetY(emptySpace: CGFloat, paddings: EdgeInsets = .zero) -> CGFloat {
    guard emptySpace > 0 else { return 0 }
    switch textAlignmentVertical {
    case .top:
      return paddings.top
    case .center:
      return (emptySpace + paddings.top - paddings.bottom) / 2
    case .bottom:
      return emptySpace - paddings.bottom
    }
  }

  private var cusorOffset: CGFloat {
    if singleLineInput.isHidden {
      multiLineCusorOffset
    } else {
      singleLineCusorOffset
    }
  }
}

extension UIView {
  fileprivate func allSuperviewsAreVisible() -> Bool {
    var inspectedView: UIView? = self
    while let currentView = inspectedView {
      guard !currentView.isHidden, currentView.alpha > 0 else { return false }
      inspectedView = currentView.superview
    }
    return true
  }
}

extension TextInputBlockView: UIPickerViewDataSource {
  func numberOfComponents(in _: UIPickerView) -> Int {
    1
  }

  func pickerView(_: UIPickerView, numberOfRowsInComponent _: Int) -> Int {
    selectionItems?.count ?? 0
  }

  func pickerView(_: UIPickerView, didSelectRow row: Int, inComponent _: Int) {
    textValue.value = selectionItems?[row].value ?? ""
  }
}

extension TextInputBlockView: UIPickerViewDelegate {
  func pickerView(_: UIPickerView, titleForRow row: Int, forComponent _: Int) -> String? {
    selectionItems?[row].text
  }
}

extension TextInputBlock.InputType.KeyboardType {
  fileprivate var uiType: UIKeyboardType {
    switch self {
    case .default:
      .default
    case .URL:
      .URL
    case .phonePad:
      .phonePad
    case .namePhonePad:
      .namePhonePad
    case .emailAddress:
      .emailAddress
    case .decimalPad:
      .decimalPad
    }
  }
}

extension TextInputBlockView {
  fileprivate var textAlignment: TextAlignment {
    switch textAlignmentHorizontal {
    case .left:
      .left
    case .center:
      .center
    case .right:
      .right
    case .start:
      layoutDirection == .rightToLeft ? .right : .left
    case .end:
      layoutDirection == .rightToLeft ? .left : .right
    }
  }
}

extension TextInputBlock.TextAlignmentVertical {
  fileprivate var contentAlignment: UIControl.ContentVerticalAlignment {
    switch self {
    case .top:
      .top
    case .center:
      .center
    case .bottom:
      .bottom
    }
  }
}

extension TextInputBlock.AutocapitalizationType {
  fileprivate var uiType: UITextAutocapitalizationType {
    switch self {
    case .none: .none
    case .words: .words
    case .sentences: .sentences
    case .allCharacters: .allCharacters
    }
  }
}

extension TextInputBlock.EnterKeyType {
  fileprivate var uiType: UIReturnKeyType {
    switch self {
    case .default: .default
    case .search: .search
    case .send: .send
    case .done: .done
    case .go: .go
    }
  }
}

private class PatchedUITextField: UITextField {
  var paddings: EdgeInsets = .zero

  override func cut(_ sender: Any?) {
    copy(sender)
    super.cut(sender)
  }

  override func textRect(forBounds bounds: CGRect) -> CGRect {
    let rect = super.textRect(forBounds: bounds)
    return rect.inset(by: paddings)
  }

  override func editingRect(forBounds bounds: CGRect) -> CGRect {
    let rect = super.editingRect(forBounds: bounds)
    return rect.inset(by: paddings)
  }
}

private class PatchedUITextView: UITextView {
  override func cut(_ sender: Any?) {
    copy(sender)
    super.cut(sender)
  }
}

private let additionalOffset = 40.0
private let singleLineCusorOffset = 2.0
private let multiLineCusorOffset = 0.0
