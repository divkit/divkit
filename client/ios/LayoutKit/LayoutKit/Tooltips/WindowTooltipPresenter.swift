#if os(iOS)
import UIKit
import VGSL

final class WindowTooltipPresenter: TooltipPresenter {
  private(set) var tooltipWindowManager: TooltipWindowManager?

  private var proxyVCs: [String: UIViewController] = [:]

  func prepare() -> (constraint: CGRect, coordinateSpace: UIView?)? {
    setupIfNeeded()
    guard let tooltipWindowManager else { return nil }
    let modalWindow = tooltipWindowManager.modalWindow
    return (modalWindow.bounds.inset(by: modalWindow.safeAreaInsets), nil)
  }

  func present(_ view: TooltipContainerView, for tooltip: DefaultTooltipManager.Tooltip) {
    guard let tooltipWindowManager else { return }
    let mainWindow = tooltipWindowManager.mainWindow
    let modalWindow = tooltipWindowManager.modalWindow

    if tooltip.params.mode == .modal {
      let vc = ProxyViewController(
        viewController: mainWindow.rootViewController ?? UIViewController()
      )
      vc.view = view
      proxyVCs[tooltip.params.id] = vc

      // Passing the statusBarStyle control to `rootViewController` of the main window.
      // Window won't rotate if `rootViewController` is not set.
      modalWindow.rootViewController = vc
      tooltipWindowManager.showModalWindow()
    } else {
      mainWindow.addSubview(view)
    }

    view.frame = modalWindow.bounds
  }

  @MainActor
  func currentTooltipView() async -> UIView? {
    guard let tooltipWindow = tooltipWindowManager?.modalWindow else {
      return nil
    }

    while !tooltipWindow.isKeyWindow {
      await Task.yield()
    }

    guard let tooltipView = tooltipWindow.subviews.first else {
      return nil
    }

    return tooltipView
  }

  func onClosed(tooltipID: String, hasRemainingModals: Bool) {
    proxyVCs.removeValue(forKey: tooltipID)?.removeFromParent()
    if !hasRemainingModals {
      tooltipWindowManager?.hideModalWindow()
    }
  }

  func reset() {
    proxyVCs.removeAll()
    tooltipWindowManager = nil
  }

  private func setupIfNeeded() {
    guard tooltipWindowManager == nil else { return }

    let scenes = UIApplication.shared.connectedScenes
    guard let windowScene = scenes
      .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
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

private final class ProxyViewController: UIViewController {
  override var preferredStatusBarStyle: UIStatusBarStyle {
    viewController.preferredStatusBarStyle
  }

  private let viewController: UIViewController

  init(viewController: UIViewController) {
    self.viewController = viewController
    super.init(nibName: nil, bundle: nil)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
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
#endif
