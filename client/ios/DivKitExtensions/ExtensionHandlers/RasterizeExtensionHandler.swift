#if os(iOS)
import DivKit
import LayoutKit

public final class RasterizeExtensionHandler: DivExtensionHandler {
  public let id = "rasterize"

  public init() {}

  public func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    RasterizeBlock(child: block)
  }
}
#endif
