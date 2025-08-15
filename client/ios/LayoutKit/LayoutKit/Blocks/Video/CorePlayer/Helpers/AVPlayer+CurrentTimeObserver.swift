import AVFoundation
import Foundation
import VGSL

extension AVPlayer {
  func addPeriodicCurrentTimeObserver(
    interval: TimeInterval,
    queue: DispatchQueue? = nil,
    handler: @escaping (TimeInterval) -> Void
  ) -> Disposable {
    let observer = addPeriodicTimeObserver(
      forInterval: CMTime(seconds: interval, preferredTimescale: CMTimeScale(NSEC_PER_SEC)),
      queue: queue,
      using: { time in
        time.safeSeconds.map { handler($0.notNegative) }
      }
    )

    return Disposable { [weak self] in
      self?.removeTimeObserver(observer)
    }
  }
}

extension CMTime {
  var safeSeconds: Double? {
    if !isValid || isIndefinite {
      return nil
    }
    return seconds
  }
}

extension BinaryFloatingPoint {
  var notNegative: Self {
    self < 0 ? 0 : self
  }
}
