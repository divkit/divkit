import UIKit

import CommonCorePublic

public final class SegmentedProgressView: UIView {
  public var currentTimestamp: Double = 0 {
    didSet {
      guard currentTimestamp != oldValue else { return }
      setNeedsDisplay()
    }
  }

  public var fillColor: Color {
    didSet {
      guard fillColor != oldValue else { return }
      setNeedsLayoutAndReloadLayers()
    }
  }

  public var segmentBackgroundColor: Color {
    didSet {
      guard segmentBackgroundColor != oldValue else { return }
      setNeedsLayoutAndReloadLayers()
    }
  }

  public var intervals: [Double] = [] {
    didSet {
      guard intervals != oldValue else { return }
      setNeedsLayoutAndReloadLayers()
    }
  }

  public init(fillColor: Color, segmentBackgroundColor: Color) {
    self.fillColor = fillColor
    self.segmentBackgroundColor = segmentBackgroundColor
    super.init(frame: .zero)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  private var segmentLayers: [SegmentLayer] = []
  private var isReloadSegmentLayersNeeded = true

  public override func layoutSubviews() {
    super.layoutSubviews()

    if isReloadSegmentLayersNeeded {
      isReloadSegmentLayersNeeded = false
      reloadSegmentLayers()
    }

    guard !intervals.isEmpty else { return }

    let count = CGFloat(intervals.count)
    let totalSpacing = segmentsSpacing * (count - 1)
    let segmentWidth = (bounds.width - totalSpacing) / count

    for (index, segmentLayer) in segmentLayers.enumerated() {
      let x = CGFloat(index) * (segmentWidth + segmentsSpacing)
      segmentLayer.frame = CGRect(
        x: x,
        y: 0,
        width: segmentWidth,
        height: bounds.height
      )
    }
  }

  public override func draw(_: CGRect) {
    updateProgress()
  }

  public func setNeedsLayoutAndReloadLayers() {
    isReloadSegmentLayersNeeded = true
    setNeedsLayout()
  }
}

extension SegmentedProgressView {
  fileprivate var activeSegmentIndex: Int? {
    segmentLayers.firstIndex(where: { $0.isActive })
  }

  fileprivate func reloadSegmentLayers() {
    layer.sublayers?.forEach { $0.removeFromSuperlayer() }
    segmentLayers = intervals.map { _ in
      modified(SegmentLayer()) {
        $0.backgroundColor = segmentBackgroundColor.cgColor
        $0.fillColor = fillColor.cgColor
      }
    }
    segmentLayers.forEach { layer.addSublayer($0) }
  }

  fileprivate func updateProgress() {
    for (index, segmentLayer) in segmentLayers.enumerated() {
      segmentLayer.progress = calculateSegmentProgress(at: index)
    }
  }

  fileprivate func calculateSegmentProgress(at index: Int) -> Double {
    let interval = intervals[index]
    guard interval > 0 else { return 0 }

    let segmentStart = intervals.prefix(index).reduce(0, +)
    let progressInterval = max(0, currentTimestamp - segmentStart)
    return min(progressInterval / interval, 1)
  }
}

private class SegmentLayer: CALayer {
  var fillColor: CGColor?

  var progress: Double = 0 {
    didSet {
      guard progress != oldValue else { return }
      setNeedsDisplay()
    }
  }

  var isActive: Bool {
    progress < 1
  }

  override init() {
    super.init()

    masksToBounds = true
    cornerRadius = 2
  }

  override init(layer: Any) {
    super.init(layer: layer)

    guard let layer = layer as? SegmentLayer else {
      assertionFailure("unknown layer inside init(layer: Any)")
      return
    }

    progress = layer.progress
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func draw(in ctx: CGContext) {
    let progressRect = CGRect(
      x: 0,
      y: 0,
      width: CGFloat(progress) * bounds.width,
      height: bounds.height
    )
    ctx.setFillColor(fillColor ?? Color.white.cgColor)
    ctx.fill(progressRect)
  }
}

private let segmentsSpacing: CGFloat = 4
