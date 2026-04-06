import DivKit
import Foundation
import VGSL

public final class SVGImageHolderFactory: DivImageHolderFactory {
  private let requester: URLResourceRequesting
  private let imageProcessingQueue: OperationQueueType
  private let svgDecoder = SVGDecoder()

  public init(
    requester: URLResourceRequesting,
    imageProcessingQueue: OperationQueueType? = nil
  ) {
    self.requester = requester
    self.imageProcessingQueue = imageProcessingQueue ?? OperationQueue(
      name: "tech.divkit.svg-image-processing",
      qos: .userInitiated
    )
  }

  public convenience init(
    requestPerformer: URLRequestPerforming,
    imageProcessingQueue: OperationQueueType? = nil
  ) {
    self.init(
      requester: NetworkURLResourceRequester(performer: requestPerformer),
      imageProcessingQueue: imageProcessingQueue
    )
  }

  public func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    let decoder: (
      @Sendable (_ data: Data) -> Image?
    )? = { [weak self] in
      self?.svgDecoder.decode(data: $0)
    }

    return if let url {
      RemoteImageHolder(
        url: url,
        placeholder: placeholder,
        requester: requester,
        imageProcessingQueue: imageProcessingQueue,
        imageDecoder: decoder
      )
    } else {
      placeholder?.toAsyncDataImageHolder(
        imageProcessingQueue: imageProcessingQueue,
        decoder: decoder
      ) ?? NilImageHolder()
    }
  }
}
