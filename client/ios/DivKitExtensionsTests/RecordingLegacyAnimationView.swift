import DivKitExtensions
import UIKit
import XCTest

@MainActor
class RecordingLegacyAnimationView: UIView, @MainActor AsyncSourceAnimatableView {
  let sourceReceived = XCTestExpectation(description: "Animation source received")
  let sourceFinished = XCTestExpectation(description: "Animation source installed")
  let configured = XCTestExpectation(description: "Playback applied after loading")
  var isPlaying = false
  private(set) var sourceCount = 0

  private let holdSource: Bool
  private var sourceContinuation: CheckedContinuation<Void, Never>?

  init(holdSource: Bool) {
    self.holdSource = holdSource
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func play() {
    isPlaying = true
    configured.fulfill()
  }

  func pause() {
    isPlaying = false
    configured.fulfill()
  }

  func setSourceAsync(_: AnimationSourceType) async {
    sourceCount += 1
    if holdSource {
      await withCheckedContinuation { continuation in
        sourceContinuation = continuation
        sourceReceived.fulfill()
      }
    } else {
      sourceReceived.fulfill()
    }
    sourceFinished.fulfill()
  }

  func completeSource() {
    sourceContinuation?.resume()
    sourceContinuation = nil
  }
}
