import CoreGraphics
import VGSL

public struct ResizableBlockMeasure {
  public enum Measure {
    case resizable(
      LayoutTrait.Weight,
      minSize: CGFloat = 0,
      maxSize: CGFloat = .infinity,
      reservedSpace: CGFloat = 0
    )
    case nonResizable
  }

  private var sizes: IndexingIterator<[CGFloat]>

  public init(
    measures: [Measure],
    lengthAvailableForResizableBlocks: CGFloat
  ) {
    self.sizes = Self.distribute(
      measures: measures,
      lengthAvailable: max(0, lengthAvailableForResizableBlocks)
    ).makeIterator()
  }

  private static func distribute(
    measures: [Measure],
    lengthAvailable: CGFloat
  ) -> [CGFloat] {
    var availableSpace = lengthAvailable
    var sizes = [CGFloat](repeating: 0, count: measures.count)
    var frozen = [Bool](repeating: false, count: measures.count)
    var totalActiveWeight: CGFloat = 0

    for measure in measures {
      if case let .resizable(weight, _, _, _) = measure {
        totalActiveWeight += weight.rawValue
      }
    }

    var didFreeze = true
    while didFreeze, totalActiveWeight > 0 {
      didFreeze = false
      let perWeight = availableSpace / totalActiveWeight

      // CSS "resolve flexible lengths": size every unfrozen block at its weighted share, clamp it
      // to
      // [min, max], and sum the violations. Freeze only the violators whose direction matches the
      // net violation sign (or, when the net is zero, every remaining violator). This converges to
      // the unique correct solution regardless of child order. Freezing every violator in one pass
      // against a single (stale) `perWeight` could pin a max-violator that would actually fit once
      // a
      // min-violator's larger share is accounted for. Non-violating blocks are left untouched here
      // and sized by the weighted-rounding pass below.
      var totalViolation: CGFloat = 0
      for i in measures.indices where !frozen[i] {
        guard case let .resizable(weight, minSize, maxSize, _) = measures[i] else { continue }
        let base = weight.rawValue * perWeight
        totalViolation += clamp(base, min: minSize, max: maxSize) - base
      }

      for i in measures.indices where !frozen[i] {
        guard case let .resizable(weight, minSize, maxSize, _) = measures[i] else { continue }
        let base = weight.rawValue * perWeight
        let frozenSize: CGFloat
        if base < minSize, totalViolation >= 0 {
          frozenSize = minSize
        } else if base > maxSize, totalViolation <= 0 {
          frozenSize = maxSize
        } else {
          continue
        }
        sizes[i] = frozenSize
        frozen[i] = true
        availableSpace -= frozenSize
        totalActiveWeight -= weight.rawValue
        didFreeze = true
      }
    }

    var unfrozenIndices: [Int] = []
    for i in measures.indices where !frozen[i] {
      if case .resizable = measures[i] {
        unfrozenIndices.append(i)
      }
    }

    let perWeight = totalActiveWeight > 0 ? availableSpace / totalActiveWeight : 0
    var cumulative: CGFloat = 0
    for (k, i) in unfrozenIndices.enumerated() {
      guard case let .resizable(weight, _, _, _) = measures[i] else { continue }
      let nextCumulative = cumulative + weight.rawValue * perWeight
      let length: CGFloat = if k == unfrozenIndices.count - 1 {
        availableSpace.flooredToScreenScale - cumulative.flooredToScreenScale
      } else {
        nextCumulative.flooredToScreenScale - cumulative.flooredToScreenScale
      }
      sizes[i] = length
      cumulative = nextCumulative
    }

    var result: [CGFloat] = []
    for i in measures.indices {
      if case let .resizable(_, _, _, reservedSpace) = measures[i] {
        result.append(sizes[i] + reservedSpace)
      }
    }
    return result
  }

  public mutating func measureNext(_ measure: Measure) -> CGFloat {
    switch measure {
    case .nonResizable:
      0
    case .resizable:
      sizes.next() ?? 0
    }
  }
}
