// Copyright 2022 Yandex LLC. All rights reserved.

import UIKit

extension UIMenuController {
  public func presentMenu(from view: UIView, in rect: CGRect) {
    if #available(iOS 13.0, *) {
      showMenu(from: view, rect: rect)
    } else {
      setTargetRect(rect, in: view)
      setMenuVisible(true, animated: true)
    }
  }

  public func hideMenu(animated: Bool) {
    if #available(iOS 13.0, *) {
      hideMenu()
    } else {
      setMenuVisible(false, animated: animated)
    }
  }
}
