#if os(iOS)
import UIKit
import VGSL

final class TextMasksLayer: CALayer {
  private var maskLayers: [CALayer] = []
  private var currentRuns = [MaskRun]()

  func update(with runs: [MaskRun]) {
    maskLayers.forEach { $0.removeFromSuperlayer() }
    maskLayers.removeAll()

    maskLayers = runs.enumerated().compactMap { _, run -> CALayer? in
      guard let maskLayer = createMaskLayer(run: run) else {
        return nil
      }
      maskLayer.opacity = run.mask.isEnabled ? 1.0 : 0.0

      addSublayer(maskLayer)
      return maskLayer
    }
  }

  private func createMaskLayer(
    run: MaskRun
  ) -> CALayer? {
    switch run.mask.type {
    case .solid:
      let layer = CALayer()
      layer.frame = run.rect
      layer.backgroundColor = run.mask.color
      return layer

    case let .particles(particleSize, density):
      return createEmitterLayer(
        for: run,
        particleSize: particleSize,
        density: density
      )
    }
  }

  private func createEmitterLayer(
    for run: MaskRun,
    particleSize: CGFloat,
    density: CGFloat
  ) -> CAEmitterLayer? {
    let rect = run.rect
    let attribute = run.mask

    let emitter = CAEmitterLayer()
    emitter.frame = rect
    emitter.seed = UInt32(seedConstant)

    emitter.emitterShape = .rectangle
    emitter.emitterSize = rect.size
    emitter.emitterPosition = CGPoint(x: rect.width / 2, y: rect.height / 2)

    emitter.renderMode = .additive

    let cell = CAEmitterCell()

    let particleImageSize = CGSize(width: particleSize, height: particleSize)
    UIGraphicsBeginImageContextWithOptions(particleImageSize, false, 0)
    guard let ctx = UIGraphicsGetCurrentContext() else {
      return nil
    }
    ctx.setFillColor(attribute.color)
    ctx.fillEllipse(in: CGRect(origin: .zero, size: particleImageSize))
    guard let particleImage = UIGraphicsGetImageFromCurrentImageContext() else {
      return nil
    }
    UIGraphicsEndImageContext()

    cell.contents = particleImage.cgImage
    cell.contentsScale = 1.0

    let area = rect.width * rect.height
    cell.lifetime = 1.0

    cell.emissionRange = .pi * 2
    cell.birthRate = Float(area * density * particleSize) / 2
    cell.alphaRange = 1
    cell.alphaSpeed = 1
    cell.lifetimeRange = 0.5
    cell.scale = 0.5

    if attribute.isAnimated {
      cell.velocityRange = 20
    } else {
      emitter.timeOffset = convertTime(seedConstant, from: nil)
      emitter.beginTime = seedConstant
      emitter.speed = 0
    }

    emitter.emitterCells = [cell]
    return emitter
  }
}

private let seedConstant = 42.0
#endif
