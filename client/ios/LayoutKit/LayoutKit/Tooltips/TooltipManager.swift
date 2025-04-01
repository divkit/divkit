import VGSL

#if os(iOS)
import UIKit
#endif

/// The `TooltipActionPerformer` protocol defines actions for showing and hiding tooltips within the
/// DivKit.
///
/// Conforming to this protocol allows your class to perform actions related to tooltips. It
/// introduces two methods to control the display of tooltips:
/// - `showTooltip(info:)`: Use this method to show a tooltip with the provided `TooltipInfo`.
/// - `hideTooltip(id:)`: Use this method to hide the tooltip identified by the given `id`.
public protocol TooltipActionPerformer {
  /// Shows a tooltip with the provided `TooltipInfo`.
  ///
  /// - Parameter info: The `TooltipInfo` containing the necessary information to display the
  /// tooltip.
  func showTooltip(info: TooltipInfo)

  /// Hides the tooltip identified by the given `id`.
  ///
  /// - Parameter id: The identifier of the tooltip to hide.
  func hideTooltip(id: String)
}

#if os(iOS)
/// The `TooltipManager` protocol is a dependency responsible for processing and displaying tooltips
/// in the application.
///
/// Conforming to this protocol allows your class to act as a tooltip manager, handling various
/// tooltip-related tasks. It combines three other protocols as its superclasses:
/// - `TooltipActionPerformer`: Provides functionality for performing actions related to tooltips.
/// - `RenderingDelegate`: Acts as a delegate for rendering tooltips.
///
/// The `TooltipManager` protocol introduces two methods to handle tooltip anchor views:
/// - `tooltipAnchorViewAdded(anchorView:)`: Notifies the manager when a tooltip anchor view is
/// added to the view hieararchy.
/// - `tooltipAnchorViewRemoved(anchorView:)`: Notifies the manager when a tooltip anchor view is
/// removed from the view hieararchy.
public protocol TooltipManager: AnyObject, TooltipActionPerformer, RenderingDelegate {
  /// Notifies the manager when a tooltip anchor view is added to the view hieararchy.
  ///
  /// - Parameter anchorView: The event that anchorView appeared in view hierarchy.  An anchor view
  /// is the view to which a tooltip is attached.
  func tooltipAnchorViewAdded(anchorView: TooltipAnchorView)

  /// Notifies the manager when a tooltip anchor view is removed from the view hieararchy.
  ///
  /// - Parameter anchorView: The event that anchorView disappeared from view hierarchy.  An anchor
  /// view is the view to which a tooltip is attached.
  func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView)

  /// Removes all tooltips.
  func reset()

  /// Sets handler for the tooltip view UI events.
  func setHandler(_ handler: @escaping (UIActionEvent) -> Void)
}

extension TooltipManager {
  public func setHandler(_: @escaping (UIActionEvent) -> Void) {}
}

public class DefaultTooltipManager: TooltipManager {
  public struct Tooltip {
    public let params: BlockTooltipParams
    public let view: VisibleBoundsTrackingView
  }

  public var shownTooltips: Property<Set<String>>

  private var handleAction: (UIActionEvent) -> Void
  private var existingAnchorViews = WeakCollection<TooltipAnchorView>()
  private var showingTooltips = [String: TooltipContainerView]()
  private(set) var tooltipWindowManager: TooltipWindowManager?
  private var previousOrientation = UIDevice.current.orientation

  public init(
    shownTooltips: Property<Set<String>> = Property(),
    handleAction: @escaping (UIActionEvent) -> Void = { _ in }
  ) {
    self.handleAction = handleAction
    self.shownTooltips = shownTooltips

    NotificationCenter.default.addObserver(
      self,
      selector: #selector(orientationDidChange),
      name: UIDevice.orientationDidChangeNotification,
      object: nil
    )
  }

  public func showTooltip(info: TooltipInfo) {
    setupTooltipWindowManager()
    guard let tooltipWindowManager,
          !showingTooltips.keys.contains(info.id)
    else { return }

    Task { @MainActor in
      let modalWindow = tooltipWindowManager.modalWindow
      let windowBounds = modalWindow.bounds.inset(by: modalWindow.safeAreaInsets)

      guard let tooltip = await existingAnchorViews.compactMap(
        concurrencyLimit: 1,
        transform: { await $0?.makeTooltip(id: info.id, in: windowBounds) }
      ).first else { return }

      weak var proxyVC: UIViewController?
      let view = TooltipContainerView(
        tooltip: tooltip,
        handleAction: handleAction,
        onCloseAction: { [weak self] in
          guard let self else { return }
          proxyVC?.removeFromParent()
          showingTooltips.removeValue(forKey: tooltip.params.id)
          if !showingTooltips.contains(where: { $1.isModal }) {
            self.tooltipWindowManager?.hideModalWindow()
          }
        }
      )

      let mainWindow = tooltipWindowManager.mainWindow
      // Passing the statusBarStyle control to `rootViewController` of the main window
      let vc = ProxyViewController(
        viewController: mainWindow.rootViewController ?? UIViewController(),
        viewDidAppear: { UIAccessibility.post(notification: .screenChanged, argument: view) }
      )
      vc.view = view
      proxyVC = vc

      if tooltip.params.mode == .modal {
        // Window won't rotate if `rootViewController` is not set
        modalWindow.rootViewController = vc
        tooltipWindowManager.showModalWindow()
        view.frame = modalWindow.bounds
      } else {
        guard let rootViewController = mainWindow.rootViewController else {
          return assertionFailure("Failed to read root view controller of key window")
        }
        rootViewController.addChild(vc)
        rootViewController.view.addSubview(view)
        view.frame = rootViewController.view.bounds
      }

      view.animateAppear()

      showingTooltips[info.id] = view
      let duration = tooltip.params.duration
      if !duration.isZero {
        try await Task.sleep(nanoseconds: UInt64(duration.nanoseconds))
        hideTooltip(id: tooltip.params.id)
      }
    }
  }

  public func hideTooltip(id: String) {
    guard let tooltipView = showingTooltips[id] else { return }
    tooltipView.close(animated: true)
  }

  public func tooltipAnchorViewAdded(anchorView: TooltipAnchorView) {
    existingAnchorViews.append(anchorView)
  }

  public func tooltipAnchorViewRemoved(anchorView: TooltipAnchorView) {
    existingAnchorViews.remove(anchorView)
  }

  public func reset() {
    showingTooltips.values.forEach { $0.close(animated: false) }
    showingTooltips = [:]
    tooltipWindowManager = nil
  }

  public func setHandler(_ handler: @escaping (UIActionEvent) -> Void) {
    handleAction = handler
  }

  deinit {
    NotificationCenter.default.removeObserver(
      self,
      name: UIDevice.orientationDidChangeNotification,
      object: nil
    )
  }

  @objc func orientationDidChange(_: Notification) {
    let orientation = UIDevice.current.orientation
    guard orientation != previousOrientation, !orientation.isFlat else { return }
    if !(orientation.isPortrait && previousOrientation.isPortrait) {
      reset()
    }
    previousOrientation = orientation
  }

  private func setupTooltipWindowManager() {
    guard tooltipWindowManager == nil else { return }

    let scenes = UIApplication.shared.connectedScenes
    guard let windowScene = scenes.first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
          let keyWindow = windowScene.windows.first(where: { $0.isKeyWindow }) else {
      return
    }

    tooltipWindowManager = TooltipWindowManager(
      mainWindow: keyWindow,
      modalWindow: modified(UIWindow(windowScene: windowScene)) {
        $0.windowLevel = UIWindow.Level.alert + 1
        $0.isHidden = true
      }
    )
  }
}

extension TooltipAnchorView {
  @MainActor
  fileprivate func makeTooltip(
    id: String,
    in constraint: CGRect
  ) async -> DefaultTooltipManager.Tooltip? {
    guard let tooltip = tooltips.first(where: { $0.id == id }) else {
      return nil
    }

    let tooltipView = await tooltip.tooltipViewFactory?() ?? tooltip.block.makeBlockView()

    let targetRect = window != nil ?
      convert(bounds, to: nil) :
      frame

    tooltipView.frame = tooltip.calculateFrame(
      targeting: targetRect,
      constrainedBy: constraint,
      useLegacyWidth: tooltip.useLegacyWidth
    )

    return DefaultTooltipManager.Tooltip(
      params: tooltip.params,
      view: tooltipView
    )
  }
}

private final class ProxyViewController: UIViewController {
  private let viewController: UIViewController
  private let viewDidAppear: Action

  init(
    viewController: UIViewController,
    viewDidAppear: @escaping Action
  ) {
    self.viewController = viewController
    self.viewDidAppear = viewDidAppear
    super.init(nibName: nil, bundle: nil)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override var preferredStatusBarStyle: UIStatusBarStyle {
    viewController.preferredStatusBarStyle
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    viewDidAppear()
  }
}

final class TooltipWindowManager {
  let mainWindow: UIWindow
  let modalWindow: UIWindow

  init(mainWindow: UIWindow, modalWindow: UIWindow) {
    self.mainWindow = mainWindow
    self.modalWindow = modalWindow
  }

  func showModalWindow() {
    mainWindow.accessibilityElementsHidden = true
    modalWindow.isHidden = false
    modalWindow.makeKeyAndVisible()
  }

  func hideModalWindow() {
    mainWindow.accessibilityElementsHidden = false
    modalWindow.isHidden = true
  }
}
#else
public protocol TooltipManager: AnyObject, TooltipActionPerformer, RenderingDelegate {}

public final class DefaultTooltipManager: TooltipManager {
  public init() {}

  public func showTooltip(info _: TooltipInfo) {}
  public func hideTooltip(id _: String) {}
}
#endif

extension TooltipManager {
  public func mapView(_: BlockView, to _: BlockViewID) {}
  public func reset() {}
}
