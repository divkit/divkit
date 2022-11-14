// Copyright 2022 Yandex LLC. All rights reserved.

import Foundation
import UIKit

public protocol RemoteImageViewContentProtocol: UIView, ImageViewProtocol {
  func setImage(_ image: UIImage?, animated: Bool?)
}
