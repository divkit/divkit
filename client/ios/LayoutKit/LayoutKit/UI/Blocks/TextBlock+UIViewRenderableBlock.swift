import Foundation
import UIKit

import BaseUI
import CommonCore
import LayoutKitInterface

extension TextBlock {
  public static func makeBlockView() -> BlockView { TextBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    (view as! TextBlockView).model = .init(
      images: images,
      attachments: attachments,
      text: text,
      verticalPosition: verticalAlignment.position,
      source: Variable { [weak self] in self },
      accessibility: accessibilityElement,
      truncationToken: truncationToken,
      canSelect: canSelect
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TextBlockView
  }
}

private final class TextBlockView: BlockView, VisibleBoundsTrackingLeaf {
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

  private var selectedRange: Range<Int>?
  private var activePointer: ActivePointer?
  private var selectionRect: CGRect?

  private var textLayout: AttributedStringLayout<ActionsAttribute>?

  private lazy var tapRecognizer = UITapGestureRecognizer(
    target: self,
    action: #selector(handleTap(_:))
  )

  private lazy var longTapSelectionRecognizer = UILongPressGestureRecognizer(
    target: self,
    action: #selector(handleSelectionLongTap(_:))
  )

  private lazy var panSelectionRecognizer = UIPanGestureRecognizer(
    target: self,
    action: #selector(handleSelectionPan(_:))
  )

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

      applyAccessibility(model?.accessibility)
      precondition(model.images.count == model.attachments.count)
      imageRequests = model.images.indices
        .filter { model.attachments[$0].image == nil }
        .compactMap(requestImage)

      if model.canSelect
        || model.text.hasActions
        || model.truncationToken?.hasActions == true {
        isUserInteractionEnabled = true
      } else {
        isUserInteractionEnabled = false
      }

      if model.canSelect {
        addGestureRecognizer(longTapSelectionRecognizer)
        addGestureRecognizer(panSelectionRecognizer)
      } else {
        removeGestureRecognizer(longTapSelectionRecognizer)
        removeGestureRecognizer(panSelectionRecognizer)
      }

      if model.text.hasActions || model.truncationToken?.hasActions == true {
        addGestureRecognizer(tapRecognizer)
      } else {
        removeGestureRecognizer(tapRecognizer)
      }

      setNeedsDisplay()
    }
  }

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  override init(frame: CGRect) {
    super.init(frame: frame)

    contentMode = .redraw
    backgroundColor = .clear
    isUserInteractionEnabled = false
    layer.contentsGravity = .center
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) { fatalError() }

  override func draw(_ rect: CGRect) {
    guard let model = model else { return }
    let textLayout: AttributedStringLayout<ActionsAttribute> = model.text.drawAndGetLayout(
      inContext: UIGraphicsGetCurrentContext()!,
      verticalPosition: model.verticalPosition,
      rect: rect,
      truncationToken: model.truncationToken,
      actionKey: ActionsAttribute.Key,
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
    becomeFirstResponder()
    let elementIndex = textLayout?.getTapElementIndex(
      from: gesture.location(in: gesture.view),
      viewHeight: bounds.height
    )
    switch gesture.state {
    case .began:
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
      setNeedsDisplay()
    case .ended:
      guard let selectionRect = selectionRect else {
        return
      }
      UIMenuController.shared.presentMenu(from: self, in: selectionRect)
    default:
      return
    }
  }

  @objc private func handleSelectionPan(_ gesture: UIPanGestureRecognizer) {
    let elementIndex = textLayout?.getTapElementIndex(
      from: gesture.location(in: gesture.view),
      viewHeight: bounds.height
    )
    switch gesture.state {
    case .began:
      guard let selectedRange = selectedRange,
            let elementIndex = elementIndex else {
        return
      }
      if min(
        abs(elementIndex - selectedRange.lowerBound),
        abs(elementIndex - selectedRange.upperBound)
      ) < 3 {
        activePointer =
          (
            abs(elementIndex - selectedRange.lowerBound) <
              abs(elementIndex - selectedRange.upperBound)
          ) ?
          .leading : .trailing
      }
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
    selectedRange = nil
    selectionRect = nil
    setNeedsDisplay()
    return result
  }

  override func canPerformAction(_ action: Selector, withSender _: Any?) -> Bool {
    action == #selector(copy(_:))
  }

  override func copy(_: Any?) {
    if let selectedRange = selectedRange {
      UIPasteboard.general.string = model.text.string[selectedRange]
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
  fileprivate func getTapElementIndex(from point: CGPoint, viewHeight: CGFloat) -> Int? {
    let transform = CGAffineTransform(translationX: 0, y: viewHeight).scaledBy(x: 1, y: -1)
    let transformedTapLocation = point.applying(transform)
    let lineLayout = lines.last(where: { transformedTapLocation.y <= $0.verticalOffset })
    guard let lineLayout = lineLayout,
          transformedTapLocation.x > lineLayout.horizontalOffset else {
      return nil
    }
    return CTLineGetStringIndexForPosition(
      lineLayout.line,
      transformedTapLocation.movingX(by: -lineLayout.horizontalOffset)
    )
  }
}
