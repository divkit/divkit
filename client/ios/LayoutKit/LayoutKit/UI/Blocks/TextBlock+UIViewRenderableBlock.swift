import Foundation
import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

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
    textBlockContainer.model = .init(
      images: images + truncationImages,
      attachments: attachments + truncationAttachments,
      text: text,
      verticalPosition: verticalAlignment.position,
      source: Variable { [weak self] in self },
      accessibility: accessibilityElement,
      truncationToken: truncationToken,
      canSelect: canSelect
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
      if let textGradient = textGradient {
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
      if let currentView = currentView {
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
      applyAccessibility(model.accessibility)
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

    textBlockView.frame = bounds
    gradientView?.frame = bounds

    gradientView?.mask = textBlockView
  }
}

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

  private lazy var tapRecognizer = UITapGestureRecognizer(
    target: self,
    action: #selector(handleTap(_:))
  )

  private lazy var hideSelectionMenuTapRecognizer = UITapGestureRecognizer(
    target: self,
    action: #selector(handleHideSelectionMenuTap(_:))
  )

  private lazy var doubleTapSelectionRecognizer = {
    let result = UITapGestureRecognizer(
      target: self,
      action: #selector(handleSelectionDoubleTap(_:))
    )
    result.numberOfTapsRequired = 2
    return result
  }()

  private lazy var longTapSelectionRecognizer = UILongPressGestureRecognizer(
    target: self,
    action: #selector(handleSelectionLongTap(_:))
  )

  private lazy var panSelectionRecognizer = {
    let result = UIPanGestureRecognizer(
      target: self,
      action: #selector(handleSelectionPan(_:))
    )
    result.delegate = self
    return result
  }()

  private var delayedSelectionTapGesture: UITapGestureRecognizer?

  private var imageRequests: [Cancellable] = [] {
    didSet {
      oldValue.forEach { $0.cancel() }
    }
  }

  var model: Model! {
    didSet {
      guard model == nil || model != oldValue else {
        return
      }

      precondition(model.images.count == model.attachments.count)
      imageRequests = model.images.indices
        .filter { model.attachments[$0].image == nil }
        .compactMap(requestImage)

      isUserInteractionEnabled = model.isUserInteractionEnabled

      if model.canSelect {
        addGestureRecognizer(doubleTapSelectionRecognizer)
        addGestureRecognizer(longTapSelectionRecognizer)
        addGestureRecognizer(panSelectionRecognizer)
        addGestureRecognizer(hideSelectionMenuTapRecognizer)
      } else {
        removeGestureRecognizer(doubleTapSelectionRecognizer)
        removeGestureRecognizer(longTapSelectionRecognizer)
        removeGestureRecognizer(panSelectionRecognizer)
        removeGestureRecognizer(hideSelectionMenuTapRecognizer)
      }

      if model.text.hasActions || model.truncationToken?.hasActions == true {
        addGestureRecognizer(tapRecognizer)
      } else {
        removeGestureRecognizer(tapRecognizer)
      }

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

  override func draw(_ rect: CGRect) {
    guard let model = model else { return }
    let textLayout: AttributedStringLayout<ActionsAttribute> = model.text.drawAndGetLayout(
      inContext: UIGraphicsGetCurrentContext()!,
      verticalPosition: model.verticalPosition,
      rect: rect,
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
    guard let textLayout = textLayout else {
      return false
    }
    return model.canSelect && bounds.contains(point)
      || textLayout.runsWithAction.contains { $0.rect.contains(point) }
  }

  @objc private func handleHideSelectionMenuTap(_: UITapGestureRecognizer) {
    UIMenuController.shared.hideMenu(animated: true)
  }

  @objc private func handleTap(_ gesture: UITapGestureRecognizer) {
    guard gesture.state == .ended, let textLayout = textLayout else {
      return
    }
    let tapLocation = gesture.location(in: gesture.view)
    textLayout.runsWithAction.forEach {
      if $0.rect.contains(tapLocation) {
        $0.action.actions.perform(sendingFrom: self)
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
    if let elementIndex = elementIndex {
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
    guard let selectionRect = selectionRect else {
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
      guard let selectedRange = selectedRange,
            let elementIndex = elementIndex else {
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
      guard let nearestIndex = nearestIndex else {
        return
      }
      activePointer =
        (
          abs(nearestIndex - selectedRange.lowerBound) <
            abs(nearestIndex - selectedRange.upperBound)
        ) ?
        .leading : .trailing
    case .changed:
      guard let activePointer = activePointer, let elementIndex = elementIndex,
            let selectedRange = selectedRange else {
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
      guard let selectionRect = selectionRect else {
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

    if let selectedRange = selectedRange {
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
      strongSelf.setNeedsDisplay()
    }
  }
}

extension TextBlockView: UIGestureRecognizerDelegate {
  override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    if gestureRecognizer == panSelectionRecognizer,
       selectedRange == nil {
      return false
    } else {
      return super.gestureRecognizerShouldBegin(gestureRecognizer)
    }
  }
}

extension Image {
  fileprivate func withTintColor(_ color: Color?) -> Image {
    guard let color = color else { return self }
    return self.redrawn(withTintColor: color)
  }
}

extension Alignment {
  fileprivate var position: NSAttributedString.VerticalPosition {
    switch self {
    case .leading: return .top
    case .center: return .center
    case .trailing: return .bottom
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
    guard let lineLayout = lineLayout,
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
      return LinearGradientView(gradient)
    case let .radial(gradient):
      return RadialGradientView(gradient)
    case let .box(color):
      return BoxShadowView(shadowColor: color)
    }
  }
}

extension TextBlockView.Model {
  fileprivate var isUserInteractionEnabled: Bool {
    canSelect || text.hasActions || truncationToken?.hasActions == true
  }
}
