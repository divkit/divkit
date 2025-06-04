import DivKit
import Foundation
import VGSL

public final class SVGImageHolderFactory: DivImageHolderFactory {
  private let requester: URLResourceRequesting
  private let imageProcessingQueue: OperationQueueType
  private let svgDecoder = SVGDecoder()

  public init(
    requester: URLResourceRequesting,
    imageProcessingQueue: OperationQueueType
  ) {
    self.requester = requester
    self.imageProcessingQueue = imageProcessingQueue
  }

  public convenience init(
    requestPerformer: URLRequestPerforming,
    imageProcessingQueue: OperationQueueType? = nil
  ) {
    self.init(
      requester: NetworkURLResourceRequester(performer: requestPerformer),
      imageProcessingQueue: imageProcessingQueue ?? OperationQueue(
        name: "tech.divkit.svg-image-processing",
        qos: .userInitiated
      )
    )
  }

  public func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    guard let url else {
      return placeholder?.toImageHolder() ?? NilImageHolder()
    }
    return RemoteImageHolder(
      url: url,
      placeholder: placeholder,
      requester: requester,
      imageProcessingQueue: imageProcessingQueue,
      imageDecoder: { [weak self] in
        self?.svgDecoder.decode(data: $0)
      }
    )
  }
}
