import CoreGraphics

public protocol GalleryViewModelDelegate: AnyObject {
  func onContentOffsetChanged(_ contentOffset: CGFloat, in model: GalleryViewModel)
}
