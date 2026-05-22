import CoreGraphics
import VGSL

private let subdivisionSamplesPerSegment = 16

extension [(color: Color, location: CGFloat)] {
  // Rewrites gradient stops so that CGGradient / CAGradientLayer's straight
  // RGBA interpolation visually matches premultiplied-alpha interpolation.
  // Without this, a transparent → opaque pair bleeds the transparent stop's
  // irrelevant RGB into the midpoint — e.g. #0000 → #FFFFFFFF renders as
  // mid-grey on iOS while Android's native shader gives white with alpha.
  //
  // Two-step fix:
  //   1. Each α=0 stop inherits RGB from its nearest non-transparent neighbor.
  //      Straight RGBA interpolation of (r,g,b,0) → (r,g,b,a) is identical to
  //      premultiplied, so the canonical case becomes pixel-correct without
  //      any added stops.
  //   2. For any adjacent pair where both alpha *and* RGB differ, insert
  //      samples computed in premultiplied space. (When alpha matches or RGB
  //      matches across the pair, straight == premultiplied by linearity.)
  //      With 16 samples per segment the worst-case alpha step is 1/17,
  //      putting residual error well under 1/255 in any composited output.
  //
  // Examples:
  //   [(#0000, 0), (#FFFFFFFF, 1)]      → step 1 rewrites #0000 to #FFFFFF00 →
  //                                       step 2 skips (RGB now equal) →
  //                                       2 stops out, white fading in.
  //   [(#FF000040, 0), (#0000FFFF, 1)]  → step 1 no-op (neither α=0) →
  //                                       step 2 subdivides into 18 stops.
  public func premultipliedSubdivided() -> [Gradient.Point] {
    guard count >= 2 else { return self }

    let normalized = normalizingTransparentStops()

    var result: [Gradient.Point] = []
    result.reserveCapacity(normalized.count + (normalized.count - 1) * subdivisionSamplesPerSegment)

    for i in 0..<(normalized.count - 1) {
      let lhs = normalized[i]
      let rhs = normalized[i + 1]
      result.append(lhs)

      if lhs.color.alpha == rhs.color.alpha { continue }
      if lhs.color.red == rhs.color.red,
         lhs.color.green == rhs.color.green,
         lhs.color.blue == rhs.color.blue { continue }
      if lhs.location == rhs.location { continue }

      let a1 = lhs.color.alpha
      let a2 = rhs.color.alpha
      let pmR1 = lhs.color.red * a1
      let pmG1 = lhs.color.green * a1
      let pmB1 = lhs.color.blue * a1
      let pmR2 = rhs.color.red * a2
      let pmG2 = rhs.color.green * a2
      let pmB2 = rhs.color.blue * a2

      for sample in 1...subdivisionSamplesPerSegment {
        let p = CGFloat(sample) / CGFloat(subdivisionSamplesPerSegment + 1)
        let pmR = pmR1 + (pmR2 - pmR1) * p
        let pmG = pmG1 + (pmG2 - pmG1) * p
        let pmB = pmB1 + (pmB2 - pmB1) * p
        let a = a1 + (a2 - a1) * p

        let color = if a > 0 {
          Color(
            red: (pmR / a).clamp(0...1),
            green: (pmG / a).clamp(0...1),
            blue: (pmB / a).clamp(0...1),
            alpha: a.clamp(0...1)
          )
        } else {
          Color(red: 0, green: 0, blue: 0, alpha: 0)
        }
        let location = lhs.location + (rhs.location - lhs.location) * p
        result.append((color: color, location: location))
      }
    }
    result.append(normalized[normalized.count - 1])
    return result
  }

  fileprivate func normalizingTransparentStops() -> [Gradient.Point] {
    guard contains(where: { $0.color.alpha > 0 }) else { return self }

    return enumerated().map { i, stop in
      guard stop.color.alpha == 0,
            let neighbor = nearestNonTransparentColor(around: i)
      else { return stop }
      return (
        color: Color(red: neighbor.red, green: neighbor.green, blue: neighbor.blue, alpha: 0),
        location: stop.location
      )
    }
  }

  fileprivate func nearestNonTransparentColor(around i: Int) -> Color? {
    for offset in 1..<count {
      let left = i - offset
      if left >= 0, self[left].color.alpha > 0 {
        return self[left].color
      }
      let right = i + offset
      if right < count, self[right].color.alpha > 0 {
        return self[right].color
      }
    }
    return nil
  }
}
