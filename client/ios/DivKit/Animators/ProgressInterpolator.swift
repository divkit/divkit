import Foundation

protocol ProgressInterpolator {
  func interpolate(progress: CGFloat) -> CGFloat
}

struct LinearInterpolator: ProgressInterpolator {
  func interpolate(progress: CGFloat) -> CGFloat {
    progress
  }
}

struct EaseInOutInterpolator: ProgressInterpolator {
  func interpolate(progress: CGFloat) -> CGFloat {
    progress < 0.5 ? 2 * progress * progress : -1 + (4 - 2 * progress) * progress
  }
}

struct EaseInterpolator: ProgressInterpolator {
  func interpolate(progress: CGFloat) -> CGFloat {
    progress * progress * (3 - 2 * progress)
  }
}

struct EaseInInterpolator: ProgressInterpolator {
  func interpolate(progress: CGFloat) -> CGFloat {
    progress * progress
  }
}

struct EaseOutInterpolator: ProgressInterpolator {
  func interpolate(progress: CGFloat) -> CGFloat {
    progress * (2 - progress)
  }
}

struct SpringInterpolator: ProgressInterpolator {
  func interpolate(progress: CGFloat) -> CGFloat {
    let dampingRatio: CGFloat = 0.5
    let response: CGFloat = 0.5
    return 1 - (pow(2, -10 * progress) * cos(progress * .pi * (response / dampingRatio)))
  }
}
