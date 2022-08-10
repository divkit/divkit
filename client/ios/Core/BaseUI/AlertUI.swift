// Copyright 2020 Yandex LLC. All rights reserved.

import Foundation

public protocol AlertUI: AnyObject {
  func showAlert(_ model: AlertModel)
}
