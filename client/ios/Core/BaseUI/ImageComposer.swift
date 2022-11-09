import CoreImage

public enum ImageComposerType: FilterProtocol {
  case sourceAtop

  public var name: String {
    switch self {
    case .sourceAtop:
      return "CISourceAtopCompositing"
    }
  }

  public var parameters: [String: Any] {
    switch self {
    case .sourceAtop:
      return [:]
    }
  }

  public var imageComposer: ImageComposer {
    { backgroundImage in
      { image in
        let parameters: [String: Any] = [
          kCIInputImageKey: image,
          kCIInputBackgroundImageKey: backgroundImage,
        ]
        let filter = CIFilter(name: name, parameters: self.parameters.merging(parameters) { $1 })
        return filter?.outputImage
      }
    }
  }
}
