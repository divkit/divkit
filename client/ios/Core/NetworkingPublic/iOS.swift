// Copyright 2021 Yandex LLC. All rights reserved.

import UIKit

#if !os(tvOS)
extension UIApplication: NetworkActivityIndicatorUI {}

@available(iOSApplicationExtension, unavailable)
public let networkActivityIndicatorController = NetworkActivityIndicatorController(
  UI: UIApplication
    .shared
)
#endif

extension NetworkActivityIndicatorController {
  public static var stub: Self { Self(UI: NetworkActivityIndicatorStub()) }
}

private final class NetworkActivityIndicatorStub: NetworkActivityIndicatorUI {
  var isNetworkActivityIndicatorVisible = false
}
