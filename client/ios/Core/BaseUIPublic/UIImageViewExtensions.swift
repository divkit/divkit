// Copyright 2022 Yandex LLC. All rights reserved.

import UIKit

extension UIImageView {
  // iOS 14+ starts describing screenshots, setting traits to none disables it
  @objc public func disableDescribingScreenshots() {
    accessibilityTraits = .none
  }
}
