import UIKit

import CommonCorePublic

final class ContextMenuDelegate: NSObject, UIContextMenuInteractionDelegate {
  private let contextMenu: ContextMenu
  private weak var view: UIView?
  public init(
    contextMenu: ContextMenu,
    view: UIView?
  ) {
    self.contextMenu = contextMenu
    self.view = view
  }

  @available(iOS 13.0, *)
  public func contextMenuInteraction(
    _: UIContextMenuInteraction,
    configurationForMenuAtLocation _: CGPoint
  ) -> UIContextMenuConfiguration? {
    UIContextMenuConfiguration(identifier: nil, previewProvider: makeVC) { [weak self] _ in
      guard let self = self, let sender = self.view else {
        return nil
      }
      return self.contextMenu.makeUIMenu(sender: sender)
    }
  }

  private func makeVC() -> UIViewController? {
    let viewController = UIViewController(nibName: nil, bundle: nil)
    viewController.view = contextMenu.preview.makeBlockView()
    viewController.preferredContentSize = contextMenu.preview.intrinsicSize
    return viewController
  }

  @available(iOS 13.0, *)
  public func contextMenuInteraction(
    _: UIContextMenuInteraction,
    previewForHighlightingMenuWithConfiguration _: UIContextMenuConfiguration
  ) -> UITargetedPreview? {
    guard let view = view else {
      return nil
    }

    let highlightedRect: CGRect = view.bounds // frame of snapshot

    let previewParameters = UIPreviewParameters()
    previewParameters.backgroundColor = .clear

    let previewTarget = UIPreviewTarget(
      container: view,
      center: CGPoint(x: highlightedRect.midX, y: highlightedRect.midY)
    )
    return UITargetedPreview(view: UIView(), parameters: previewParameters, target: previewTarget)
  }
}

extension ContextMenu {
  @available(iOS 13.0, *)
  func makeUIMenu(sender: UIResponder) -> UIMenu {
    let childrenItems: [UIAction] = items.map {
      let action = $0.action
      let attributes: UIMenuElement.Attributes = $0.isDestructive ? [.destructive] : []
      return UIAction(
        title: $0.text,
        image: $0.image,
        attributes: attributes,
        handler: { [action, sender] _ in
          action.perform(sendingFrom: sender)
        }
      )
    }
    return UIMenu(title: title ?? "", children: childrenItems)
  }
}
