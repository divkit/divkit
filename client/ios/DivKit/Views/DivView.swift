import LayoutKit
import UIKit
import VGSL

/// ``DivView`` is a view that allows you to draw the layout for `DivKit`.
/// She is able to handle actions, switch states correctly, as well as update the layout if
/// necessary.
///
/// ## Example
/// You can find examples of div cards in the
/// [samples](https://github.com/divkit/divkit/tree/main/test_data/samples) or
/// [testing](https://github.com/divkit/divkit/tree/main/test_data/regression_test_data) sections of
/// the demo app.

public final class DivView: VisibleBoundsTrackingView {
  private let divKitComponents: DivKitComponents
  private let preloader: DivViewPreloader
  private var blockSubscription: Disposable?
  private var shouldRecalculateVisibilitySubscription: Disposable?

  private var shouldRecalculateVisibility = true

  private var blockProvider: DivBlockProvider? {
    didSet {
      onVisibleBoundsChanged(to: bounds)
    }
  }

  private var blockView: BlockView? {
    didSet {
      if blockView !== oldValue {
        oldValue?.removeFromSuperview()
        blockView.flatMap(addSubview(_:))
      }

      if blockView != nil {
        shouldRecalculateVisibility = true
      }
    }
  }

  private let boundsTracker = BoundsTracker()

  /// Initializes a new `DivView` instance.
  ///
  /// - Parameters:
  ///  - divKitComponents: The ``DivKitComponents`` instance used for configuration and handling of
  /// the DivView.
  public init(
    divKitComponents: DivKitComponents,
    divViewPreloader: DivViewPreloader? = nil
  ) {
    self.divKitComponents = divKitComponents
    preloader = divViewPreloader ?? DivViewPreloader(divKitComponents: divKitComponents)
    super.init(frame: .zero)
    let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(handleTap))
    tapGestureRecognizer.delegate = self
    tapGestureRecognizer.cancelsTouchesInView = false
    addGestureRecognizer(tapGestureRecognizer)
  }

  /// Sets the source of the ``DivView`` and updates the layout.
  /// - Parameters:
  /// - source: The source of the ``DivView``, specified using `JSON` data, a `Data` object, or
  /// ``DivData``.
  /// - debugParams: Optional debug configurations for the ``DivView``.
  /// - shouldResetPreviousCardData: Specifies whether to clear the data of the previous card when
  /// updating the ``DivView`` with new content.
  public func setSource(
    _ source: DivViewSource,
    debugParams: DebugParams = DebugParams(),
    shouldResetPreviousCardData: Bool = false
  ) async {
    if shouldResetPreviousCardData, let blockProvider {
      divKitComponents.reset(cardId: blockProvider.cardId)
    }
    blockProvider = preloader.blockProvider(for: source.id.cardId)
    blockSubscription = blockProvider?.$block.currentAndNewValues.addObserver { [weak self] in
      self?.update(block: $0)
    }
    shouldRecalculateVisibilitySubscription = blockProvider?
      .$shouldRecalculateVisibility.currentAndNewValues.addObserver { [weak self] in
        self?.shouldRecalculateVisibility = $0
      }
    preloader.setSourceTask?.cancel()
    await preloader.setSource(source, debugParams: debugParams)
  }

  /// Sets the source of the ``DivView`` and updates the layout.
  /// - Parameters:
  /// - source: The source of the ``DivView``, specified using `JSON` data, a `Data` object, or
  /// ``DivData``.
  /// - debugParams: Optional debug configurations for the ``DivView``.
  /// - shouldResetPreviousCardData: Specifies whether to clear the data of the previous card when
  /// updating the ``DivView`` with new content.
  @_spi(Legacy)
  public func setSource(
    _ source: DivViewSource,
    debugParams: DebugParams = DebugParams(),
    shouldResetPreviousCardData: Bool = false
  ) {
    if shouldResetPreviousCardData, let blockProvider {
      divKitComponents.reset(cardId: blockProvider.cardId)
    }
    blockProvider = preloader.blockProvider(for: source.id.cardId)
    blockSubscription = blockProvider?.$block.currentAndNewValues.addObserver { [weak self] in
      self?.update(block: $0)
    }
    shouldRecalculateVisibilitySubscription = blockProvider?
      .$shouldRecalculateVisibility.currentAndNewValues.addObserver { [weak self] in
        self?.shouldRecalculateVisibility = $0
      }
    preloader.setSource(source, debugParams: debugParams)
  }

  /// Sets the source of the ``DivView`` and updates the layout.
  /// - Parameters:
  /// - cardId: ID of the card to show. (The card will not be shown if it has not been previously
  /// uploaded to ``DivViewPreloader``)
  /// - shouldResetPreviousCardData: Specifies whether to clear the data of the previous card when
  /// updating the ``DivView`` with new content.
  public func showCardId(_ cardId: DivCardID, shouldResetPreviousCardData: Bool = false) {
    if shouldResetPreviousCardData, let blockProvider {
      divKitComponents.reset(cardId: blockProvider.cardId)
    }
    blockProvider = preloader.blockProvider(for: cardId)
    blockSubscription = blockProvider?.$block.currentAndNewValues.addObserver { [weak self] in
      self?.update(block: $0)
    }
    shouldRecalculateVisibilitySubscription = blockProvider?
      .$shouldRecalculateVisibility.currentAndNewValues.addObserver { [weak self] in
        self?.shouldRecalculateVisibility = $0
      }
  }

  /// Sets the parent scroll view for the DivView.
  /// - Parameter parentScrollView: The parent scroll view to set.
  /// This is used when you require a sub-scroll in a `DivInput`.
  public func setParentScrollView(_ parentScrollView: ScrollView) {
    blockProvider?.parentScrollView = parentScrollView
  }

  /// Applies the patch to the card with id.
  /// - Parameters:
  /// - patch: `DivPatch` that should be applied to the card.
  /// - cardId: ID of the card to which the patch should be applied.
  public func applyPatch(_ patch: DivPatch, cardId: DivCardID) {
    blockProvider?.update(reasons: [.patch(cardId, patch)])
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func layoutSubviews() {
    super.layoutSubviews()
    guard let blockView else { return }

    blockView.frame = bounds
    blockView.layoutIfNeeded()

    if shouldRecalculateVisibility {
      blockView.onVisibleBoundsChanged(
        from: boundsTracker.previousBounds,
        to: boundsTracker.lastVisibleBounds
      )

      shouldRecalculateVisibility = false
      boundsTracker.updatePreviousBounds()
    }
  }

  public override func layoutSublayers(of layer: CALayer) {
    super.layoutSublayers(of: layer)

    boundsDidChange()
  }

  public override func removeFromSuperview() {
    super.removeFromSuperview()
    blockView?.onVisibleBoundsChanged(
      from: boundsTracker.lastVisibleBounds,
      to: .zero
    )
  }

  /// Returns ``DivCardSize`` of the ``DivView``.
  ///
  /// - Returns: The calculated ``DivCardSize``.
  public var cardSize: DivViewSize? {
    blockProvider?.cardSize
  }

  /// Adds an observer to listen for ``DivView`` estimated size changes.
  ///
  /// - Parameters:
  /// - `onCardSizeChanged`: A closure that gets invoked whenever a ``DivView`` estimated size
  /// changes.
  ///
  /// - Returns: A `Disposable` which can be used to unregister the observer when it's no longer
  /// needed.
  public func addObserver(_ onCardSizeChanged: @escaping (DivViewSize) -> Void) -> Disposable {
    preloader.changeEvents.addObserver { [weak self] in
      guard $0.cardId == self?.blockProvider?.cardId else { return }
      onCardSizeChanged($0.estimatedSize)
    }
  }

  /// Returns the intrinsic content size of the ``DivView``.
  ///
  /// - Returns: The calculated intrinsic content size.
  public override var intrinsicContentSize: CGSize {
    guard let cardSize else {
      return CGSize(width: DivView.noIntrinsicMetric, height: DivView.noIntrinsicMetric)
    }
    let width: CGFloat
    switch cardSize.width {
    case .matchParent:
      width = bounds.width == 0 ? DivView.noIntrinsicMetric : bounds.width
    case let .desired(value):
      width = value
    case .dependsOnOtherDimensionSize:
      assertionFailure("Width depends on other dimension size")
      width = DivView.noIntrinsicMetric
    }
    let height: CGFloat = switch cardSize.height {
    case .matchParent:
      bounds.height == 0 ? DivView.noIntrinsicMetric : bounds.height
    case let .desired(value):
      value
    case let .dependsOnOtherDimensionSize(heightForWidth):
      heightForWidth(width)
    }
    return CGSize(width: width, height: height)
  }

  private func update(block: Block) {
    let renderingDelegate: RenderingDelegate? = if blockProvider?.id != nil,
                                                   let divCardId = blockProvider?.cardId {
      divKitComponents.renderingDelegate(for: divCardId)
    } else {
      nil
    }
    if let blockView, block.canConfigureBlockView(blockView) {
      block.configureBlockView(
        blockView,
        observer: self,
        overscrollDelegate: nil,
        renderingDelegate: renderingDelegate
      )
    } else {
      blockView = block.makeBlockView(
        observer: self,
        renderingDelegate: renderingDelegate
      )
    }
    invalidateIntrinsicContentSize()
    setNeedsLayout()
  }

  private func boundsDidChange() {
    if blockProvider?.cardSize?.width == .matchParent ||
      blockProvider?.cardSize?.height == .matchParent {
      invalidateIntrinsicContentSize()
    }
  }

  @objc private func handleTap() {
    clearFocus()
  }

  /// Notifies the DivView about changes in its visible bounds.
  /// - Parameters:
  ///  - to: The new bounds rectangle.
  public func onVisibleBoundsChanged(to: CGRect) {
    guard boundsTracker.append(to) else { return }

    shouldRecalculateVisibility = true
    if window == nil {
      forceLayout()
    } else {
      setNeedsLayout()
    }
  }

  /// Use ``onVisibleBoundsChanged(to:)`` instead.
  public func onVisibleBoundsChanged(from _: CGRect, to: CGRect) {
    onVisibleBoundsChanged(to: to)
  }
}

extension DivView: ElementStateObserver {
  public func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    divKitComponents.blockStateStorage.elementStateChanged(state, forPath: path)
    blockProvider?.update(withStates: [path: state])
  }

  public func focusedElementChanged(isFocused: Bool, forPath path: UIElementPath) {
    divKitComponents.blockStateStorage.focusedElementChanged(isFocused: isFocused, forPath: path)
    blockProvider?.update(path: path, isFocused: isFocused)
  }

  public func clearFocus() {
    let blockStateStorage = divKitComponents.blockStateStorage
    let focusedElement = blockStateStorage.getFocusedElement()
    guard focusedElement != nil else {
      return
    }
    blockStateStorage.clearFocus()
    UIApplication.shared.sendAction(
      #selector(UIResponder.resignFirstResponder),
      to: nil,
      from: nil,
      for: nil
    )
  }
}

extension DivView: UIActionEventPerforming {
  public func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject) {
    switch event.payload {
    case .empty:
      break
    case let .url(url):
      divKitComponents.urlHandler.handle(url, sender: self)
    case let .menu(menu):
      nearestViewController?.showMenu(menu, actionPerformer: self)
    case let .divAction(params):
      divKitComponents.actionHandler.handle(params: params, sender: sender)
    }
  }
}

extension DivView {
  public override var accessibilityElements: [Any]? {
    get {
      guard let elements = self.blockProvider?.accessibilityElementsStorage?
        .getAccessibilityElements(from: self),
        !elements.isEmpty else {
        return super.accessibilityElements
      }

      return elements
    }
    set {}
  }

  private class BoundsTracker {
    private(set) var lastVisibleBounds = CGRect.zero
    private(set) var previousBounds = CGRect.zero

    func append(_ bounds: CGRect) -> Bool {
      if lastVisibleBounds != bounds {
        previousBounds = lastVisibleBounds
        lastVisibleBounds = bounds

        return true
      }

      return false
    }

    func updatePreviousBounds() {
      previousBounds = lastVisibleBounds
    }
  }
}

extension UIResponder {
  fileprivate var nearestViewController: UIViewController? {
    next as? UIViewController ?? next?.nearestViewController
  }
}

extension UIViewController {
  fileprivate func showMenu(
    _ menu: Menu,
    actionPerformer: UIActionEventPerforming
  ) {
    let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
    for item in menu.items {
      let action = UIAlertAction(title: item.text, style: .default) { _ in
        let events = item.actions.map {
          UIActionEvent(uiAction: $0, originalSender: self)
        }
        actionPerformer.perform(uiActionEvents: events, from: self)
      }
      alert.addAction(action)
    }
    present(alert, animated: true)
  }
}

extension DivView: UIGestureRecognizerDelegate {
  public func gestureRecognizer(
    _ gestureRecognizer: UIGestureRecognizer,
    shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
  ) -> Bool {
    guard let view = gestureRecognizer.view,
          let otherView = otherGestureRecognizer.view else { return false }
    return view.isDescendant(of: otherView)
  }
}
