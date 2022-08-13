// Copyright 2019 Yandex LLC. All rights reserved.

import UIKit

@_implementationOnly import AudioToolbox

public protocol PhysicalFeedbackGenerator {
  func prepare()
  func generateFeedback()
}

// Base is exported with minimal iOS version 9.0.
// It's needed by RealTimeAnalytics pod, which is integrated in YXMobileMetrica.
@available(iOS 10, *)
@available(tvOS, unavailable)
extension UIImpactFeedbackGenerator: PhysicalFeedbackGenerator {
  public func generateFeedback() {
    impactOccurred()
  }
}

public final class AudioFeedbackGenerator: PhysicalFeedbackGenerator {
  public init() {}

  public func prepare() {}

  public func generateFeedback() {
    AudioServicesPlaySystemSoundWithCompletion(kSystemSoundID_Vibrate, nil)
  }
}
