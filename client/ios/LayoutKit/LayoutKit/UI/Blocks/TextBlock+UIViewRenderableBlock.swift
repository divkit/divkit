import Foundation
import UIKit
import VGSL

extension TextBlock {
  public static func makeBlockView() -> BlockView { TextBlockContainer() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let textBlockContainer = view as! TextBlockContainer
    textBlockContainer.textGradient = textGradient
    
    let intrinsicHeight: GetIntrinsicTextHeight? = if autoEllipsize {
      nil
    } else {
      { [weak self] width in
        self?.calculateTextIntrinsicContentHeight(for: width)
      }
    }
    
    textBlockContainer.model = .init(
      images: images + truncationImages,
      attachments: attachments + truncationAttachments,
      text: text,
      verticalPosition: verticalAlignment.position,
      source: Variable { [weak self] in self },
      accessibility: accessibilityElement,
      truncationToken: truncationToken,
      canSelect: canSelect,
      additionalTextInsets: additionalTextInsets,
      intrinsicHeight: intrinsicHeight
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TextBlockContainer
  }
}

private final class TextBlockContainer: BlockView, VisibleBoundsTrackingLeaf {
  private let textBlockView = TextBlockView()
  private var gradientView: UIView?

  var textGradient: Gradient? {
    didSet {
      guard textGradient == nil || textGradient != oldValue else {
        return
      }
      if let textGradient {
        gradientView = textGradient.uiView
        currentView = gradientView
      } else {
        gradientView = nil
        currentView = textBlockView
      }
    }
  }

  private var currentView: UIView? {
    didSet {
      guard currentView !== oldValue else {
        return
      }
      oldValue?.removeFromSuperview()
      if let currentView {
        addSubview(currentView)
      }
      setNeedsLayout()
    }
  }

  var model: TextBlockView.Model! {
    didSet {
      guard model == nil || model != oldValue else {
        return
      }
      textBlockView.model = model
      isUserInteractionEnabled = model.isUserInteractionEnabled
      applyAccessibilityFromScratch(model.accessibility)
    }
  }

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  override init(frame: CGRect) {
    super.init(frame: frame)
    backgroundColor = .clear
    isUserInteractionEnabled = false
    layer.contentsGravity = .center
  }

  @available(*, unavailable)
  required init(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    let textFrame = if let intrinsicHeight = model.intrinsicHeight?(bounds.width), intrinsicHeight > bounds.height {
      CGRect(
        origin: bounds.origin, 
        size: CGSize(
          width: bounds.width,
          height: intrinsicHeight
        )
      )
    } else {
      bounds
    }
    textBlockView.frame = textFrame
    gradientView?.frame = textFrame

    gradientView?.mask = textBlockView
  }
}

private typealias GetIntrinsicTextHeight = (CGFloat) -> CGFloat?

private final class TextBlockView: UIView {
  struct Model: ReferenceEquatable {
    let images: [TextBlock.InlineImage]
    let attachments: [TextAttachment]
    let text: NSAttributedString
    let verticalPosition: NSAttributedString.VerticalPosition
    let source: Variable<AnyObject?>
    let accessibility: AccessibilityElement?
    let truncationToken: NSAttributedString?
    let canSelect: Bool
    let additionalTextInsets: EdgeInsets
    let intrinsicHeight: GetIntrinsicTextHeight?
  }

  private var selectedRange: Range<Int>? {
    didSet {
      if selectedRange != oldValue {
        setNeedsDisplay()
      }
    }
  }

  private var activePointer: ActivePointer?
  private var selectionRect: CGRect? {
    didSet {
      guard let delayedSelectionTapGesture else { return }
      handleSelectionTapEnded(delayedSelectionTapGesture)
      self.delayedSelectionTapGesture = nil
    }
  }

  private var textLayout: AttributedStringLayout<ActionsAttribute>?

  private var tapRecognizer: UITapGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      tapRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

  private var hideSelectionMenuTapRecognizer: UITapGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      hideSelectionMenuTapRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

  private var doubleTapSelectionRecognizer: UITapGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      doubleTapSelectionRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

  private var longTapSelectionRecognizer: UILongPressGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      longTapSelectionRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

  private var panSelectionRecognizer: UIPanGestureRecognizer? {
    didSet {
      oldValue.flatMap(removeGestureRecognizer(_:))
      panSelectionRecognizer.flatMap(addGestureRecognizer(_:))
    }
  }

  private var delayedSelectionTapGesture: UITapGestureRecognizer?

  private var imageRequests: [Cancellable] = [] {
    didSet {
      oldValue.forEach { $0.cancel() }
    }
  }

  private var imagesReferences: [UIImage?] = []

  var model: Model! {
    didSet {
      guard model == nil || model != oldValue else {
        return
      }

      precondition(model.images.count == model.attachments.count)

      if model.images == oldValue?.images {
        imageRequests = model.images.indices
          .filter { model.attachments[$0].image == nil }
          .compactMap(requestImage)
      } else {
        imagesReferences = Array(repeating: nil, times: UInt(model.images.count))
        imageRequests = model.images.indices.compactMap(requestImage)
      }

      isUserInteractionEnabled = model.isUserInteractionEnabled

      configureRecognizers()

      setNeedsDisplay()
    }
  }

  override init(frame: CGRect) {
    super.init(frame: frame)

    contentMode = .redraw
    backgroundColor = .clear
    isUserInteractionEnabled = false
    layer.contentsGravity = .center
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) { fatalError("init(coder:) has not been implemented") }

  private func configureRecognizers() {
    if model.canSelect {
      let doubleTapSelectionRecognizer = UITapGestureRecognizer(
        target: self,
        action: #selector(handleSelectionDoubleTap(_:))
      )
      doubleTapSelectionRecognizer.numberOfTapsRequired = 2
      self.doubleTapSelectionRecognizer = doubleTapSelectionRecognizer

      self.longTapSelectionRecognizer = UILongPressGestureRecognizer(
        target: self,
        action: #selector(handleSelectionLongTap(_:))
      )

      let panSelectionRecognizer = UIPanGestureRecognizer(
        target: self,
        action: #selector(handleSelectionPan(_:))
      )
      panSelectionRecognizer.delegate = self
      self.panSelectionRecognizer = panSelectionRecognizer

      hideSelectionMenuTapRecognizer = UITapGestureRecognizer(
        target: self,
        action: #selector(handleHideSelectionMenuTap(_:))
      )
    } else {
      self.doubleTapSelectionRecognizer = nil
      self.longTapSelectionRecognizer = nil
      self.panSelectionRecognizer = nil
      self.hideSelectionMenuTapRecognizer = nil
    }

    tapRecognizer = if model.text.hasActions || model.truncationToken?.hasActions == true {
      UITapGestureRecognizer(
        target: self,
        action: #selector(handleTap(_:))
      )
    } else {
      nil
    }
  }

  override func draw(_ rect: CGRect) {
    guard let model else { return }
    let textLayout: AttributedStringLayout<ActionsAttribute> = model.text.drawAndGetLayout(
      inContext: UIGraphicsGetCurrentContext()!,
      verticalPosition: model.verticalPosition,
      rect: rect,
      textInsets: model.additionalTextInsets,
      truncationToken: model.truncationToken,
      actionKey: ActionsAttribute.Key,
      backgroundKey: BackgroundAttribute.Key,
      borderKey: BorderAttribute.Key,
      selectedRange: selectedRange
    )
    self.textLayout = textLayout
    selectionRect = model.text.drawSelection(
      context: UIGraphicsGetCurrentContext()!,
      rect: rect,
      linesLayout: textLayout.lines,
      selectedRange: selectedRange
    )
  }

  override func point(inside point: CGPoint, with _: UIEvent?) -> Bool {
    guard let textLayout else {
      return false
    }
    return model.canSelect && bounds.contains(point)
      || textLayout.runsWithAction.contains { $0.rect.contains(point) }
  }

  @objc private func handleHideSelectionMenuTap(_: UITapGestureRecognizer) {
    UIMenuController.shared.hideMenu(animated: true)
  }

  @objc private func handleTap(_ gesture: UITapGestureRecognizer) {
    guard gesture.state == .ended, let textLayout else {
      return
    }
    let tapLocation = gesture.location(in: gesture.view)
    for run in textLayout.runsWithAction {
      if run.rect.contains(tapLocation) {
        run.action.actions.perform(sendingFrom: self)
      }
    }
  }

  @objc private func handleSelectionLongTap(_ gesture: UILongPressGestureRecognizer) {
    switch gesture.state {
    case .began:
      handleSelectionTapBegan(gesture)
    case .ended:
      handleSelectionTapEnded(gesture)
    default:
      return
    }
  }

  @objc private func handleSelectionDoubleTap(_ gesture: UITapGestureRecognizer) {
    switch gesture.state {
    case .ended:
      delayedSelectionTapGesture = gesture
      handleSelectionTapBegan(gesture)
    default:
      return
    }
  }

  func handleSelectionTapBegan(_ gesture: UIGestureRecognizer) {
    becomeFirstResponder()
    let elementIndex = textLayout?.getTapElementIndex(
      from: gesture.location(in: gesture.view)
        .applying(CGAffineTransform(translationX: 0, y: bounds.height).scaledBy(x: 1, y: -1))
    )
    UIMenuController.shared.hideMenu(animated: true)
    if let elementIndex {
      let prefix = model.text.string.prefix(elementIndex)
      let suffix = model.text.string.suffix(from: prefix.endIndex)
      let trailing = elementIndex + suffix.distance(
        from: suffix.startIndex,
        to: suffix.firstIndex { $0.isWhitespace || $0.isNewline } ?? suffix.endIndex
      )
      let leading = prefix.lastIndex { $0.isWhitespace || $0.isNewline }
        .flatMap { prefix.distance(from: prefix.startIndex, to: $0) + 1 } ?? 0
      selectedRange = leading..<trailing
    }
  }

  func handleSelectionTapEnded(_: UIGestureRecognizer) {
    guard let selectionRect else {
      return
    }
    UIMenuController.shared.presentMenu(from: self, in: selectionRect)
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(resetSelecting),
      name: UIMenuController.willHideMenuNotification,
      object: nil
    )
  }

  @objc private func handleSelectionPan(_ gesture: UIPanGestureRecognizer) {
    let point = gesture.location(in: gesture.view)
    let transformedPoint = point
      .applying(CGAffineTransform(translationX: 0, y: bounds.height).scaledBy(x: 1, y: -1))
    let elementIndex = textLayout?.getTapElementIndex(
      from: transformedPoint
    )
    switch gesture.state {
    case .began:
      guard let selectedRange,
            let elementIndex else {
        return
      }
      let lineIndex = textLayout?.lines.lastIndex(where: {
        transformedPoint.y <= $0.verticalOffset
      }) ?? 0
      let prevLineElementIndex = textLayout?.getTapElementIndex(
        from: CGPoint(
          x: point.x,
          y: textLayout?.lines.element(at: lineIndex - 1)?.verticalOffset
            .advanced(by: -1) ?? .infinity
        )
      )
      let nextLineElementIndex = textLayout?.getTapElementIndex(
        from: CGPoint(
          x: point.x,
          y: textLayout?.lines.element(at: lineIndex + 1)?.verticalOffset.advanced(by: -1) ?? 0
        )
      )
      let nearestIndex = [elementIndex, prevLineElementIndex, nextLineElementIndex]
        .compactMap(identity).first { min(
          abs($0 - selectedRange.lowerBound),
          abs($0 - selectedRange.upperBound)
        ) < 5 }
      guard let nearestIndex else {
        return
      }
      activePointer =
        (
          abs(nearestIndex - selectedRange.lowerBound) <
            abs(nearestIndex - selectedRange.upperBound)
        ) ?
        .leading : .trailing
    case .changed:
      guard let activePointer, let elementIndex,
            let selectedRange else {
        return
      }
      switch activePointer {
      case .leading:
        self.selectedRange = min(elementIndex, selectedRange.upperBound)..<selectedRange.upperBound
      case .trailing:
        self.selectedRange = selectedRange.lowerBound..<max(selectedRange.lowerBound, elementIndex)
      }
    case .ended, .cancelled, .failed:
      activePointer = nil
      guard let selectionRect else {
        return
      }
      UIMenuController.shared.presentMenu(from: self, in: selectionRect)
    default:
      return
    }
    setNeedsDisplay()
  }

  override var canBecomeFirstResponder: Bool {
    model.canSelect
  }

  override func resignFirstResponder() -> Bool {
    let result = super.resignFirstResponder()
    NotificationCenter.default.removeObserver(
      self,
      name: UIMenuController.willHideMenuNotification,
      object: nil
    )
    return result
  }

  @objc private func resetSelecting() {
    selectedRange = nil
    selectionRect = nil
  }

  override func canPerformAction(_ action: Selector, withSender _: Any?) -> Bool {
    action == #selector(copy(_:))
  }

  override func copy(_: Any?) {
    let maxSymbolIndex = textLayout?.lines
      .compactMap { !$0.isTruncated ? $0.range.upperBound : -1 }.max()

    if let selectedRange {
      UIPasteboard.general.string = model.text.string[Range(uncheckedBounds: (
        selectedRange.lowerBound,
        min(selectedRange.upperBound, maxSymbolIndex ?? .max)
      ))]
    }
    selectedRange = nil
    setNeedsDisplay()
  }

  private func requestImage(at index: Int) -> Cancellable? {
    let modelImage = model.images[index]
    return modelImage.holder.requestImageWithCompletion {
      [source = model.source, weak self] in
      guard let strongSelf = self, let image = $0,
            strongSelf.model.source.value === source.value else { return }
      strongSelf.model.attachments[index].image = image
        .withTintColor(modelImage.tintColor)
      strongSelf.imagesReferences[index] = image
      strongSelf.setNeedsDisplay()
    }
  }
}

extension TextBlockView: UIGestureRecognizerDelegate {
  override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    if gestureRecognizer == panSelectionRecognizer,
       selectedRange == nil {
      false
    } else {
      super.gestureRecognizerShouldBegin(gestureRecognizer)
    }
  }
}

extension Image {
  fileprivate func withTintColor(_ color: Color?) -> Image {
    guard let color else { return self }
    return self.redrawn(withTintColor: color)
  }
}

extension Alignment {
  fileprivate var position: NSAttributedString.VerticalPosition {
    switch self {
    case .leading: .top
    case .center: .center
    case .trailing: .bottom
    }
  }
}

extension NSAttributedString {
  fileprivate var hasActions: Bool {
    var hasActions = false
    enumerateAttribute(
      ActionsAttribute.Key,
      in: NSRange(location: 0, length: length)
    ) { value, _, stop in
      if value != nil {
        hasActions = true
        stop.pointee = true
      }
    }
    return hasActions
  }
}

private enum ActivePointer {
  case leading
  case trailing
}

extension AttributedStringLayout {
  fileprivate func getTapElementIndex(from point: CGPoint) -> Int? {
    let lineLayout = lines.last(where: { point.y <= $0.verticalOffset })
    guard let lineLayout,
          point.x > lineLayout.horizontalOffset else {
      return nil
    }
    if !lineLayout.isTruncated {
      return CTLineGetStringIndexForPosition(
        lineLayout.line,
        point.movingX(by: -lineLayout.horizontalOffset)
      )
    } else {
      let previousLineRange = lines.last { $0 != lineLayout }?.range
      return (previousLineRange?.upperBound ?? 0) +
        CTLineGetStringIndexForPosition(
          lineLayout.line,
          point.movingX(by: -lineLayout.horizontalOffset)
        )
    }
  }
}

extension Gradient {
  fileprivate var uiView: UIView {
    switch self {
    case let .linear(gradient):
      LinearGradientView(gradient)
    case let .radial(gradient):
      RadialGradientView(gradient)
    case let .box(color):
      BoxShadowView(shadowColor: color)
    }
  }
}

extension TextBlockView.Model {
  fileprivate var isUserInteractionEnabled: Bool {
    canSelect || text.hasActions || truncationToken?.hasActions == true
  }
}
