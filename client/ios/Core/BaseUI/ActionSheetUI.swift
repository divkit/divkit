// Copyright 2020 Yandex LLC. All rights reserved.

import Foundation

public protocol ActionSheetUI: AnyObject {
  func showActionSheet(_ model: ActionSheetModel)
}
