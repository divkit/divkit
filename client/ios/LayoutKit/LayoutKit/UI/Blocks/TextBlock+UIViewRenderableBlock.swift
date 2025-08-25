#if os(iOS)
import Foundation
import UIKit
import VGSL

typealias StringLayout = AttributedStringLayout<RunWithBoundsAttribute>

extension TextBlock {
  public static func makeBlockView() -> BlockView { TextBlockContainer() }

  public func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    let textBlockContainer = view as! TextBlockContainer
    textBlockContainer.textGradientModel = gradientModel

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
      intrinsicHeight: intrinsicHeight,
      isFocused: isFocused
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TextBlockContainer
  }
}

private final class TextBlockContainer: BlockView, VisibleBoundsTrackingLeaf {
  var layoutReporter: LayoutReporter?
  var textGradientModel: TextBlock.GradientModel? {
    didSet {
      guard textGradientModel == nil || textGradientModel != oldValue else {
        return
      }

      gradientContainerView = GradientContainerView(model: textGradientModel, mask: textBlockView)
      currentView = gradientContainerView ?? textBlockView
    }
  }

  var model: TextBlockView.Model! {
    didSet {
      guard model == nil || model != oldValue else {
        return
      }
      textBlockView.model = model
      gradientContainerView?.configureRangedTextColor(textBlockViewModel: model)
      isUserInteractionEnabled = model.isUserInteractionEnabled
      applyAccessibilityFromScratch(model.accessibility)
    }
  }

  private let textBlockView = TextBlockView()
  private var gradientContainerView: GradientContainerView?

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
    layoutReporter?.willLayoutSubviews()

    let textFrame = if let intrinsicHeight = model.intrinsicHeight?(bounds.width),
                       intrinsicHeight > bounds.height {
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
    gradientContainerView?.frame = textFrame
    layoutReporter?.didLayoutSubviews()
  }
}

private class GradientContainerView: UIView {
  let gradientView: UIView

  private let model: TextBlock.GradientModel
  private let rangedTextWithColorTextBlockView: TextBlockView

  init?(model: TextBlock.GradientModel?, mask: UIView) {
    guard let model else {
      return nil
    }
    self.model = model
    gradientView = model.gradient.uiView
    gradientView.mask = mask

    rangedTextWithColorTextBlockView = TextBlockView()

    super.init(frame: .zero)

    gradientView.addSubview(rangedTextWithColorTextBlockView)
    addSubview(gradientView)
  }

  @available(*, unavailable)
  required init(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    gradientView.frame = bounds
    rangedTextWithColorTextBlockView.frame = bounds
  }

  func configureRangedTextColor(textBlockViewModel: TextBlockView.Model) {
    rangedTextWithColorTextBlockView.model = textBlockViewModel
      .updated(with: model.rangedTextWithColor)
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
    let isFocused: Bool
  }

  override var canBecomeFirstResponder: Bool {
    model.canSelect
  }

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
      selection = nil

      setNeedsDisplay()
    }
  }

  private var lastElementIndex: Int?

  private var masksLayer: TextMasksLayer?

  private var selection: TextSelection? {
    didSet {
      setNeedsDisplay()
    }
  }

  private var textLayout: StringLayout? {
    didSet {
      let runsWithMasks = textLayout?.runsWithMasks ?? []
      if oldValue?.runsWithMasks != runsWithMasks {
        updateMasksLayer(with: runsWithMasks)
      }
    }
  }

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
    guard let model else { return }
    self.textLayout = model.text.drawAndGetLayout(
      inContext: UIGraphicsGetCurrentContext()!,
      verticalPosition: model.verticalPosition,
      rect: rect,
      textInsets: model.additionalTextInsets,
      truncationToken: model.truncationToken,
      actionKey: RunWithBoundsAttribute.Key,
      backgroundKey: BackgroundAttribute.Key,
      borderKey: BorderAttribute.Key,
      rangeVerticalAlignmentKey: RangeVerticalAlignmentAttribute.Key,
      selectedRange: selection?.range
    )

    self.selection?.draw(rect)
  }

  override func point(inside point: CGPoint, with _: UIEvent?) -> Bool {
    guard let textLayout else {
      return false
    }
    return model.canSelect && bounds.contains(point)
      || textLayout.runsWithAction.contains { $0.rect.contains(point) }
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

  override func canPerformAction(_ action: Selector, withSender _: Any?) -> Bool {
    action == #selector(copy(_:))
  }

  override func copy(_: Any?) {
    if let selection {
      UIPasteboard.general.string = selection.getSelectedText()
    }

    selection = nil
    setNeedsDisplay()
  }

  func handleSelectionTapBegan(_ gesture: UIGestureRecognizer) {
    becomeFirstResponder()

    guard let textLayout else { return }

    UIMenuController.shared.hideMenu(animated: true)
    let textModel = TextSelection.TextModel(layout: textLayout, text: model.text)
    let point = TextSelection.Point(
      point: gesture.location(in: gesture.view),
      viewBounds: bounds
    )

    selection = TextSelection(textModel: textModel, point: point) { [weak self] in
      guard let delayedSelectionTapGesture = self?.delayedSelectionTapGesture else { return }
      self?.handleSelectionTapEnded(delayedSelectionTapGesture)
      self?.delayedSelectionTapGesture = nil
    }
  }

  func handleSelectionTapEnded(_: UIGestureRecognizer) {
    guard let selectionRect = selection?.rect else {
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

  private func updateMasksLayer(with runs: [MaskRun]) {
    guard !runs.isEmpty else {
      masksLayer?.removeFromSuperlayer()
      masksLayer = nil
      return
    }

    if masksLayer == nil {
      let maskLayer = TextMasksLayer()
      self.masksLayer = maskLayer
      layer.addSublayer(maskLayer)
    }

    masksLayer?.frame = bounds
    masksLayer?.update(with: runs)
  }

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

  @objc private func handleSelectionPan(_ gesture: UIPanGestureRecognizer) {
    let point = gesture.location(in: gesture.view)

    let selectionPoint = TextSelection.Point(
      point: point,
      viewBounds: bounds
    )
    switch gesture.state {
    case .began:
      guard let selection else {
        return
      }

      selection.setActivePointer(to: selectionPoint)
      lastElementIndex = nil
    case .changed:
      guard let selection,
            let elementIndex = selection.elementIndex(at: selectionPoint.verticallyInverted),
            elementIndex != lastElementIndex else {
        return
      }

      lastElementIndex = elementIndex
      selection.moveSelection(elementIndex)
    case .ended, .cancelled, .failed:
      lastElementIndex = nil
      selection?.showMenu(from: self)
    default:
      return
    }
    setNeedsDisplay()
  }

  @objc private func resetSelecting() {
    selection = nil
  }

  private func requestImage(at index: Int) -> Cancellable? {
    let modelImage = model.images[index]

    return modelImage.holder.requestImageWithCompletion {
      [source = model.source, weak self] in
      guard let strongSelf = self, let image = $0,
            strongSelf.model.source.value === source.value else { return }

      let tintedImage = image.withTintColor(
        modelImage.tintColor,
        mode: modelImage.tintMode
      )

      strongSelf.model.attachments[index].image = tintedImage
      strongSelf.imagesReferences[index] = image
      strongSelf.setNeedsDisplay()
    }
  }
}

extension TextBlockView: UIGestureRecognizerDelegate {
  override func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    if gestureRecognizer == panSelectionRecognizer,
       selection == nil {
      false
    } else {
      super.gestureRecognizerShouldBegin(gestureRecognizer)
    }
  }
}

extension Image {
  fileprivate func withTintColor(_ color: Color?, mode: TintMode) -> Image {
    guard
      let color else {
      return self
    }

    if mode == .sourceIn {
      return self.redrawn(withTintColor: color)
    }

    guard
      let coloredImage = ImageGeneratorType.constantColor(color: color.ciColor).imageGenerator(),
      let ciImage = self.ciImage ?? CIImage(image: self) else {
      return self
    }

    let tintModeFilter = { mode.composerType.imageComposer($0)(coloredImage)
    }

    let contentRect = CIVector(
      x: 0,
      y: 0,
      z: ciImage.extent.width,
      w: ciImage.extent.height
    )

    guard let filteredCIImage = combine(
      tintModeFilter,
      ImageCropType.crop(rect: contentRect).imageFilter
    )(ciImage) else {
      return self
    }

    let ciContext = CIContext(options: nil)
    guard let cgImage = ciContext.createCGImage(filteredCIImage, from: filteredCIImage.extent)
    else {
      return self
    }

    return Image(cgImage: cgImage)
  }
}

extension TintMode {
  fileprivate var composerType: ImageComposerType {
    switch self {
    case .sourceIn:
      .sourceIn
    case .sourceAtop:
      .sourceAtop
    case .darken:
      .darken
    case .lighten:
      .lighten
    case .multiply:
      .multiply
    case .screen:
      .screen
    }
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
      RunWithBoundsAttribute.Key,
      in: NSRange(location: 0, length: length)
    ) { value, _, stop in
      if (value as? RunWithBoundsAttribute)?.actions.isEmpty == false {
        hasActions = true
        stop.pointee = true
      }
    }
    return hasActions
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

  fileprivate func updated(with newText: NSAttributedString) -> Self {
    .init(
      images: images,
      attachments: attachments,
      text: newText,
      verticalPosition: verticalPosition,
      source: source,
      accessibility: accessibility,
      truncationToken: truncationToken,
      canSelect: canSelect,
      additionalTextInsets: additionalTextInsets,
      intrinsicHeight: intrinsicHeight,
      isFocused: isFocused
    )
  }
}
#endif
