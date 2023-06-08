// Copyright 2015 Yandex LLC. All rights reserved.

import Foundation

@_implementationOnly import SystemConfiguration

public protocol NetworkReachabilityObserverDelegate: AnyObject {
  func reachabilityObserver(
    _ observer: NetworkReachabilityObserving,
    targetDidBecomeReachable reachable: Bool
  )
}

public protocol NetworkReachabilityObserving: AnyObject {
  var observingStarted: Bool { get }
  // weak
  var delegate: NetworkReachabilityObserverDelegate? { get set }

  @discardableResult
  func startObserving() -> Bool

  @discardableResult
  func stopObserving() -> Bool
}

private enum ReachabilityStatus {
  case notReachable, reachableViaWiFi, reachableViaWWAN, unknown
}

public final class NetworkReachabilityObserver: NetworkReachabilityObserving {
  private var targetReachabilityFlags: SCNetworkReachabilityFlags? {
    didSet {
      if targetReachabilityFlags != nil {
        self.delegate?.reachabilityObserver(self, targetDidBecomeReachable: self.targetReachable)
      }
    }
  }

  public private(set) var observingStarted = false

  private var targetStatus: ReachabilityStatus {
    guard let flags = self.targetReachabilityFlags else {
      return .unknown
    }

    return reachabilityStatusForFlags(flags)
  }

  public var targetReachable: Bool {
    targetStatus == .reachableViaWWAN || targetStatus == .reachableViaWiFi
  }

  public weak var delegate: NetworkReachabilityObserverDelegate?

  private let reachabilityHandle: SCNetworkReachability

  public init?(hostname: String) {
    Thread.assertIsMain()
    guard let reachabilityHandle = SCNetworkReachabilityCreateWithName(nil, hostname)
    else { return nil }

    self.reachabilityHandle = reachabilityHandle
    var context = SCNetworkReachabilityContext()
    context.info = Unmanaged.passUnretained(self).toOpaque()
    let callbackAssigned = SCNetworkReachabilitySetCallback(reachabilityHandle, { _, flags, info in
      Thread.assertIsMain()
      let networkReachabilityObserver = Unmanaged<NetworkReachabilityObserver>.fromOpaque(info!)
        .takeUnretainedValue()
      networkReachabilityObserver.targetReachabilityFlags = flags
    }, &context)

    if !callbackAssigned {
      return nil
    }
  }

  deinit {
    Thread.assertIsMain()
    SCNetworkReachabilitySetDispatchQueue(reachabilityHandle, nil)
    SCNetworkReachabilitySetCallback(reachabilityHandle, nil, nil)
  }

  public func startObserving() -> Bool {
    Thread.assertIsMain()
    if observingStarted { return true }

    if SCNetworkReachabilitySetDispatchQueue(reachabilityHandle, DispatchQueue.main) {
      observingStarted = true
    }

    return observingStarted
  }

  public func stopObserving() -> Bool {
    Thread.assertIsMain()
    if !observingStarted { return true }
    observingStarted = !SCNetworkReachabilitySetDispatchQueue(reachabilityHandle, nil)
    targetReachabilityFlags = nil
    return observingStarted
  }
}

private func reachabilityStatusForFlags(_ flags: SCNetworkReachabilityFlags) -> ReachabilityStatus {
  let canConnectAutomaticaly = flags.contains(.connectionRequired) || flags
    .contains(.connectionOnTraffic)
  let canConnectWithoutUserInteraction = canConnectAutomaticaly && !flags
    .contains(.interventionRequired)
  let reachable = flags
    .contains(.reachable) &&
    (!flags.contains(.connectionRequired) || canConnectWithoutUserInteraction)

  guard reachable else { return .notReachable }

  #if os(iOS) && (arch(arm) || arch(arm64))
  return flags.contains(.isWWAN) ? .reachableViaWWAN : .reachableViaWiFi
  #else
  return .reachableViaWiFi
  #endif
}
