import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

extension TextFieldBlock {
  public static func makeBlockView() -> BlockView { TextFieldBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let field = view as! TextFieldBlockView
    field.text = text
    field.placeholders = placeholders
    field.backgroundColor = backgroundColor.systemColor
    field.keyboardAppearance = keyboardAppearance
    field.updateAction = updateAction
    field.returnButtonPressedAction = returnButtonPressedAction
    field.didBeginEditingAction = didBeginEditingAction
    field.autocapitalizationType = autocapitalizationType.uiType
    field.autocorrectionType = autocorrectionType.uiType
    field.maxNumberOfLines = maxIntrinsicNumberOfLines
    field.isSecureTextEntry = isSecureTextEntry
    field.rightControl = rightControl
    field.toolbar = toolbar
    field.keyboardType = keyboardType.uiType
    field.updateInteraction(interaction)
    field.accessibility = accessibilityElement
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TextFieldBlockView
  }
}

private final class TextFieldBlockView: BlockView, VisibleBoundsTrackingLeaf {
  var text: NSAttributedString? {
    get {
      textField.attributedText
    }
    set {
      updateSecureTextStyle(from: newValue)
      textField.setTextPreservingPosition(newValue)
      updatePlaceholdersVisibility()
    }
  }

  var textLabel: NSAttributedString? {
    switch placeholders {
    case let .separate(placeholders):
      let attributes = headerIsHidden ? placeholders.field.attributes : placeholders
        .headerAttributes
      let typo = Typo(size: attributes.size, weight: placeholders.fontWeight)
        .with(height: attributes.height).with(color: attributes.color)
      return placeholders.text.with(typo: typo)
    case .fieldOnly, .none:
      return nil
    }
  }

  var placeholders: TextFieldBlock.Placeholders? {
    didSet {
      updatePlaceholdersVisibility()
    }
  }

  var headerIsHidden = true {
    didSet {
      let animated = headerIsHidden != oldValue
      layoutTextField(animated: animated)
    }
  }

  var keyboardAppearance: TextFieldBlock.KeyboardAppearance {
    get { textField.keyboardAppearance.blockValue }
    set { textField.keyboardAppearance = newValue.uiValue }
  }

  var updateAction: UserInterfaceAction?
  var returnButtonPressedAction: UserInterfaceAction?
  var didBeginEditingAction: UserInterfaceAction?

  private var lastModelAndLayout = (model: Model.empty, layout: Layout.empty)

  private func updateSecureTextStyle(from string: NSAttributedString?) {
    let color: SystemColor = string.attribute(key: .foregroundColor, defaultValue: .black)
    textField.defaultTextAttributes[.foregroundColor] = color
  }

  override var backgroundColor: UIColor? {
    didSet {
      placeholderLabel.backgroundColor = backgroundColor
      textField.backgroundColor = backgroundColor
    }
  }

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  var autocapitalizationType: UITextAutocapitalizationType {
    get { textField.autocapitalizationType }
    set { textField.autocapitalizationType = newValue }
  }

  var autocorrectionType: UITextAutocorrectionType {
    get { textField.autocorrectionType }
    set { textField.autocorrectionType = newValue }
  }

  var isSecureTextEntry: Bool {
    get { textField.isSecureTextEntry }
    set { textField.isSecureTextEntry = newValue }
  }

  var keyboardType: UIKeyboardType {
    get { textField.keyboardType }
    set { textField.keyboardType = newValue }
  }

  func updateInteraction(_ interaction: TextFieldBlock.Interaction) {
    switch interaction {
    case .unchanged:
      break
    case let .enabled(isFirstResponder):
      textField.isUserInteractionEnabled = true
      guard textField.window != nil else { break }
      if isFirstResponder {
        textField.becomeFirstResponder()
      } else {
        textField.resignFirstResponder()
      }
    case .disabled:
      textField.isUserInteractionEnabled = false
    }
  }

  var accessibility: AccessibilityElement? {
    didSet {
      textField.applyAccessibility(accessibility)
    }
  }

  var rightControl: TextFieldBlock.SideControl? {
    didSet {
      guard rightControl != oldValue else { return }

      if let control = rightControl {
        let controlView = ImageBlock(
          imageHolder: control.image,
          tintColor: control.tintColor
        )
        .addingDecorations(
          actions: NonEmptyArray(control.action),
          accessibilityElement: control.accessibilityElement
        )
        .makeBlockView()
        controlView.frame = CGRect(origin: .zero, size: control.image.size)
        controlView.applyAccessibility(control.accessibilityElement)
        textField.rightView = controlView
        textField.rightViewMode = .always
      } else {
        textField.rightView = nil
      }
    }
  }

  var toolbar: TextFieldBlock.Toolbar? {
    didSet {
      guard toolbar != oldValue else { return }

      guard toolbar != nil else {
        textField.inputAccessoryView = nil
        return
      }

      let keyboardToolbar = UIToolbar()
      keyboardToolbar.sizeToFit()
      let cancelButton = UIBarButtonItem(
        barButtonSystemItem: .cancel,
        target: self,
        action: #selector(onToolbarCancelButtonPressed)
      )
      let flexibleSpaceButton = UIBarButtonItem(
        barButtonSystemItem: .flexibleSpace,
        target: nil,
        action: nil
      )
      let doneButton = UIBarButtonItem(
        barButtonSystemItem: .done,
        target: self,
        action: #selector(onToolbarDoneButtonPressed)
      )
      keyboardToolbar.items = [cancelButton, flexibleSpaceButton, doneButton]
      textField.inputAccessoryView = keyboardToolbar
    }
  }

  var maxNumberOfLines = 1

  private let placeholderLabel = Label()
  private let animationLabel = Label()
  private let textField = UITextField()

  override init(frame: CGRect) {
    super.init(frame: frame)

    textField.addTarget(self, action: #selector(onValueChanged), for: .editingChanged)
    textField.delegate = self

    addSubview(animationLabel)
    addSubview(placeholderLabel)
    addSubview(textField)

    let tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(onViewTapped))
    addGestureRecognizer(tapRecognizer)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    layoutTextField(animated: false)
  }

  private func layoutTextField(animated: Bool) {
    let currentModel = Model(
      placeholders: placeholders,
      text: text,
      size: bounds.size,
      headerIsHidden: headerIsHidden
    )

    if lastModelAndLayout.model != currentModel {
      switch placeholders {
      case let .separate(placeholders):
        let headerHeight = placeholders.headerAttributes.height.rawValue
        let labelHeight = headerIsHidden ? placeholders.field.attributes.height
          .rawValue : headerHeight
        let fieldHeight = bounds.height - headerHeight - placeholders.gap
        let offsetY = headerIsHidden ? placeholders.getOffsetY(
          height: labelHeight,
          heightTextField: fieldHeight,
          blockSize: bounds.size
        ) : 0
        let labelFrame = CGRect(
          x: 0,
          y: offsetY,
          width: bounds.width,
          height: labelHeight
        )
        let fieldFrame = CGRect(
          x: 0,
          y: headerHeight + placeholders.gap,
          width: bounds.width,
          height: fieldHeight
        )
        let currentLayout = Layout(
          labelFrame: labelFrame,
          fieldFrame: fieldFrame
        )
        lastModelAndLayout = (currentModel, currentLayout)
      case .fieldOnly, .none:
        let currentLayout = Layout(
          labelFrame: .zero,
          fieldFrame: bounds
        )
        lastModelAndLayout = (currentModel, currentLayout)
      }
    }

    textField.frame = lastModelAndLayout.layout.fieldFrame

    let completion: (Bool) -> Void = { [self] _ in
      placeholderLabel.transform = CGAffineTransform.identity
      placeholderLabel.frame = lastModelAndLayout.layout.labelFrame
    }

    let labelLayoutBeforeAnimation = { [self] in
      let labelFrame = lastModelAndLayout.layout.labelFrame
      let centerOffset = (placeholderLabel.frameWithoutTransform.size - labelFrame.size) / 2
      placeholderLabel.center = placeholderLabel.center - CGPoint(
        x: centerOffset.width,
        y: centerOffset.height
      )
      placeholderLabel.bounds = CGRect(center: .zero, size: labelFrame.size)
      if let scale = getPlaceholderScale() {
        let offsetX = placeholderLabel.frameWithoutTransform.size.width * (1 - scale) / 2
        let offsetY = placeholderLabel.frameWithoutTransform.size.height * (1 - scale) / 2
        placeholderLabel.transform = placeholderLabel.transform.translatedBy(
          x: -offsetX,
          y: -offsetY
        ).scaled(by: scale)
      }
    }

    let labelLayout = { [self] in
      let labelFrame = lastModelAndLayout.layout.labelFrame
      placeholderLabel.transform = CGAffineTransform.identity.translatedBy(
        x: labelFrame.origin.x - placeholderLabel.frameWithoutTransform.origin.x,
        y: labelFrame.origin.y - placeholderLabel.frameWithoutTransform.origin.y
      )
      animationLabel.transform = placeholderLabel.transform
    }

    labelLayoutBeforeAnimation()
    configureAnimationLabel()
    placeholderLabel.attributedText = textLabel

    if animated {
      updateLabelAlpha()
      UIView.animate(duration: 0.3, animations: { [self] in
        labelLayout()
        updateLabelAlpha()
      }, completion: completion)
    } else {
      labelLayout()
    }
  }

  private func updateLabelAlpha() {
    animationLabel.alpha = placeholderLabel.alpha
    placeholderLabel.alpha = 1 - placeholderLabel.alpha
  }

  private func configureAnimationLabel() {
    switch placeholders {
    case let .separate(placeholders):
      animationLabel.alpha = 0
      animationLabel.bounds = placeholderLabel.bounds
      animationLabel.center = placeholderLabel.center
      animationLabel.transform = placeholderLabel.transform
      animationLabel.attributedText = placeholderLabel.attributedText
      animationLabel.textColor = headerIsHidden
        ? placeholders.headerAttributes.color
        : placeholders.field.attributes.color
    case .fieldOnly, .none:
      break
    }
  }

  private func getPlaceholderScale() -> CGFloat? {
    switch placeholders {
    case let .separate(placeholders):
      let headerSize = placeholders.headerAttributes.size.rawValue
      let fieldSize = placeholders.field.attributes.size.rawValue

      if headerIsHidden {
        return headerSize / fieldSize
      } else {
        return fieldSize / headerSize
      }
    case .fieldOnly, .none:
      return nil
    }
  }

  @objc private func onValueChanged() {
    guard let action = updateAction else {
      return
    }

    // NB: Intentionally skip analytics here
    UIActionEvent(
      uiAction: action.withParameter(
        name: "text",
        value: textField.text ?? ""
      ),
      originalSender: self
    ).sendFrom(self)
  }

  @objc private func onViewTapped() {
    textField.becomeFirstResponder()
  }

  private func onReturnButtonPressed() {
    guard let action = returnButtonPressedAction else {
      return
    }
    UIActionEvent(uiAction: action, originalSender: self).sendFrom(self)
  }

  private func onBeginEditing() {
    guard let action = didBeginEditingAction else { return }
    UIActionEvent(uiAction: action, originalSender: self).sendFrom(self)
  }

  @objc private func onToolbarDoneButtonPressed() {
    guard let action = toolbar?.doneButtonAction else {
      return
    }
    textField.resignFirstResponder()
    UIActionEvent(uiAction: action, originalSender: self).sendFrom(self)
  }

  @objc private func onToolbarCancelButtonPressed() {
    textField.resignFirstResponder()
  }

  private func updatePlaceholdersVisibility() {
    switch placeholders {
    case .separate:
      if textField.isFirstResponder || !text.isNilOrEmpty {
        headerIsHidden = false
      } else {
        headerIsHidden = true
      }
    case let .fieldOnly(fieldPlaceholder):
      textField.attributedPlaceholder = fieldPlaceholder
    case .none:
      break
    }
  }

  struct Model: Equatable {
    let placeholders: TextFieldBlock.Placeholders?
    let text: NSAttributedString?
    let size: CGSize
    let headerIsHidden: Bool
  }

  struct Layout {
    let labelFrame: CGRect
    let fieldFrame: CGRect
  }
}

extension TextFieldBlockView: UITextFieldDelegate {
  func textFieldDidBeginEditing(_: UITextField) {
    updatePlaceholdersVisibility()
    onBeginEditing()
  }

  func textFieldDidEndEditing(_: UITextField) {
    updatePlaceholdersVisibility()
  }

  func textFieldShouldReturn(_ textField: UITextField) -> Bool {
    textField.resignFirstResponder()
    onReturnButtonPressed()
    return false
  }
}

extension UITextField {
  fileprivate func setTextPreservingPosition(_ newText: NSAttributedString?) {
    guard let newText = newText else {
      attributedText = nil
      return
    }
    guard let oldRange = selectedTextRange else {
      attributedText = newText
      return
    }
    let oldLength = attributedText?.length ?? 0
    let lenDiff = newText.length - oldLength
    attributedText = newText
    let newPosition = position(from: oldRange.end, offset: lenDiff) ?? endOfDocument
    selectedTextRange = textRange(from: newPosition, to: newPosition)
  }
}

extension TextFieldBlock.Placeholders.Separate {
  fileprivate func getOffsetY(
    height: CGFloat,
    heightTextField: CGFloat,
    blockSize: CGSize
  ) -> CGFloat {
    switch field.position {
    case .center:
      return (blockSize.height - height) / 2
    case .normal:
      return blockSize.height - (heightTextField + height) / 2
    }
  }
}

extension TextFieldBlock.KeyboardAppearance {
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
  fileprivate var blockValue: TextFieldBlock.KeyboardAppearance {
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

extension TextFieldBlockView.Model {
  fileprivate static let empty = TextFieldBlockView.Model(
    placeholders: nil,
    text: NSAttributedString(),
    size: .zero,
    headerIsHidden: true
  )
}

extension TextFieldBlockView.Layout {
  fileprivate static let empty = TextFieldBlockView.Layout(
    labelFrame: .zero,
    fieldFrame: .zero
  )
}

extension TextFieldBlock.TextAutocapitalizationType {
  fileprivate var uiType: UITextAutocapitalizationType {
    switch self {
    case .none:
      return .none
    case .words:
      return .words
    case .sentences:
      return .sentences
    case .allCharacters:
      return .allCharacters
    }
  }
}

extension TextFieldBlock.TextAutocorrectionType {
  fileprivate var uiType: UITextAutocorrectionType {
    switch self {
    case .default:
      return .default
    case .no:
      return .no
    case .yes:
      return .yes
    }
  }
}

extension TextFieldBlock.KeyboardType {
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

extension UserInterfaceAction {
  fileprivate func withParameter(name: String, value: String) -> UserInterfaceAction {
    UserInterfaceAction(
      url: url!.URLByAddingGETParameters([name: value]),
      path: path
    )
  }
}
