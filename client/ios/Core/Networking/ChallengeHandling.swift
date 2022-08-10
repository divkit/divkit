// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public typealias ChallengeHandlers = (
  webViewChallengeHandler: ChallengeHandling,
  webContentChallengeHandler: ChallengeHandling?
)

public protocol ChallengeHandling {
  func handleChallenge(
    with protectionSpace: URLProtectionSpace,
    completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void
  )
}

public var externalURLSessionChallengeHandler: ChallengeHandling?
