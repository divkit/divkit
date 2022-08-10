// Copyright 2022 Yandex LLC. All rights reserved.

import Foundation

final class AuthChallengeHandler: ChallengeHandling {
  private let nextHandler: ChallengeHandling?
  private let trustedHosts: [String]
  private let request: URLRequest

  init(
    nextHandler: ChallengeHandling?,
    trustedHosts: [String],
    request: URLRequest
  ) {
    self.nextHandler = nextHandler
    self.trustedHosts = trustedHosts
    self.request = request
  }

  func handleChallenge(
    with protectionSpace: URLProtectionSpace,
    completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void
  ) {
    #if INTERNAL_BUILD
    if let host = request.url?.host, trustedHosts.contains(host) {
      completionHandler(.useCredential, URLCredential(trust: protectionSpace.serverTrust!))
      return
    }
    #endif

    if let nextHandler = nextHandler {
      nextHandler.handleChallenge(
        with: protectionSpace,
        completionHandler: completionHandler
      )
    } else {
      completionHandler(.performDefaultHandling, nil)
    }
  }
}
