import CoreGraphics
import VGSL

/// Size expressed in scroll-oriented coordinates: `along` the scroll axis and `across` it.
struct AxialSize: Equatable {
  var along: CGFloat
  var across: CGFloat
}

/// Available space in scroll-oriented coordinates. `nil` means the axis is unbounded.
struct AxialBounds: Equatable {
  static let unbounded = AxialBounds(along: nil, across: nil)

  var along: CGFloat?
  var across: CGFloat?
}

extension ScrollDirection {
  func axial(_ size: CGSize) -> AxialSize {
    switch self {
    case .horizontal:
      AxialSize(along: size.width, across: size.height)
    case .vertical:
      AxialSize(along: size.height, across: size.width)
    }
  }

  func size(_ axial: AxialSize) -> CGSize {
    switch self {
    case .horizontal:
      CGSize(width: axial.along, height: axial.across)
    case .vertical:
      CGSize(width: axial.across, height: axial.along)
    }
  }

  func bounds(_ size: CGSize?) -> AxialBounds {
    guard let size else { return .unbounded }
    let axial = axial(size)
    return AxialBounds(along: axial.along, across: axial.across)
  }

  /// Bounds for measuring the intrinsic height: only the width is known.
  func bounds(forWidth width: CGFloat) -> AxialBounds {
    switch self {
    case .horizontal:
      AxialBounds(along: width, across: nil)
    case .vertical:
      AxialBounds(along: nil, across: width)
    }
  }

  func along(_ point: CGPoint) -> CGFloat {
    switch self {
    case .horizontal:
      point.x
    case .vertical:
      point.y
    }
  }

  func across(_ point: CGPoint) -> CGFloat {
    switch self {
    case .horizontal:
      point.y
    case .vertical:
      point.x
    }
  }

  func maxAlong(_ rect: CGRect) -> CGFloat {
    switch self {
    case .horizontal:
      rect.maxX
    case .vertical:
      rect.maxY
    }
  }

  func maxAcross(_ rect: CGRect) -> CGFloat {
    switch self {
    case .horizontal:
      rect.maxY
    case .vertical:
      rect.maxX
    }
  }

  func rect(
    along: (origin: CGFloat, size: CGFloat),
    across: (origin: CGFloat, size: CGFloat)
  ) -> CGRect {
    switch self {
    case .horizontal:
      CGRect(x: along.origin, y: across.origin, width: along.size, height: across.size)
    case .vertical:
      CGRect(x: across.origin, y: along.origin, width: across.size, height: along.size)
    }
  }
}

extension Alignment {
  /// Origin of an item aligned between `minimum` and `maximum`.
  /// An item larger than the space is aligned as requested and may stick out on either side.
  func origin(
    of dimension: CGFloat,
    minimum: CGFloat,
    maximum: CGFloat
  ) -> CGFloat {
    switch self {
    case .leading:
      minimum
    case .center:
      floor((minimum + maximum - dimension) / 2)
    case .trailing:
      maximum - dimension
    }
  }
}

extension ScrollDirection {
  /// Size of the content: the farthest item edges plus the trailing insets.
  func contentSize(
    of frames: [CGRect],
    trailingGap: CGFloat,
    trailingCrossInset: CGFloat
  ) -> CGSize {
    guard !frames.isEmpty else {
      return .zero
    }
    return size(AxialSize(
      along: frames.map(maxAlong).max()! + trailingGap,
      across: frames.map(maxAcross).max()! + trailingCrossInset
    ))
  }
}

/// Cross-axis slots scroll items are aligned in: `count` columns of `size`,
/// separated by `spacing` and starting at `leading`.
struct CrossAxisSlots {
  var leading: CGFloat
  var size: CGFloat
  var spacing: CGFloat = 0
  var count: Int = 1
}

extension ScrollDirection {
  /// Places items one after another along the scroll axis.
  ///
  /// An item goes to the current column; the layout moves to the next column once the current one
  /// becomes at least as long as it. Across the scroll axis an item is aligned within its column.
  ///
  /// - Parameter gaps: The leading gap followed by the gap after each item.
  func placeItems(
    _ sizes: [AxialSize],
    alignments: [Alignment],
    gaps: [CGFloat],
    slots: CrossAxisSlots
  ) -> [CGRect] {
    var alongOrigins = [CGFloat](repeating: gaps[0], times: UInt(slots.count))
    var column = 0
    return zip3(sizes, alignments, gaps.dropFirst()).map { size, alignment, gap in
      let slotOrigin = slots.leading + (slots.size + slots.spacing) * CGFloat(column)
      let frame = rect(
        along: (origin: alongOrigins[column], size: size.along),
        across: (
          origin: alignment.origin(
            of: size.across,
            minimum: slotOrigin,
            maximum: slotOrigin + slots.size
          ),
          size: size.across
        )
      )
      alongOrigins[column] = maxAlong(frame) + gap
      let nextColumn = column == alongOrigins.lastElementIndex ? 0 : column + 1
      if alongOrigins[column] >= alongOrigins[nextColumn] {
        column = nextColumn
      }
      return frame
    }
  }
}

enum CrossAxisSizing {
  static func boundedCrossAxisSize(
    crossAxis: CGFloat?,
    crossInsets: SideInsets,
    columnCount: Int = 1,
    crossSpacing: CGFloat = 0
  ) -> CGFloat? {
    crossAxis.map {
      max(
        0,
        ($0 - crossInsets.sum - crossSpacing * CGFloat(columnCount - 1)) / CGFloat(columnCount)
      )
    }
  }

  static func constrainedCrossAxisSize(
    intrinsicSize: CGFloat,
    minSize: CGFloat,
    boundedCrossAxis: CGFloat?
  ) -> CGFloat {
    if let boundedCrossAxis {
      let cappedByViewportSize = min(intrinsicSize, boundedCrossAxis)
      return max(cappedByViewportSize, minSize)
    } else {
      return max(intrinsicSize, minSize)
    }
  }
}
