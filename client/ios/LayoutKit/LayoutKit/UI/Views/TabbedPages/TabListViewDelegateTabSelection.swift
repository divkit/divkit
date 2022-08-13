// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import LayoutKitInterface

protocol TabListViewDelegateTabSelection: AnyObject {
  func tabListViewDidScrollTo(_ offset: CGFloat)
  func tabListViewDidSelectItemAt(_ index: Int, withUrl url: URL?, path: UIElementPath)
}
