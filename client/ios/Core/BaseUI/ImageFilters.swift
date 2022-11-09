import CoreImage

public typealias ImageFilter = (CIImage) -> CIImage?
public typealias ImageGenerator = () -> CIImage?
public typealias ImageComposer = (CIImage) -> ImageFilter

public func combine(
  _ lhs: @escaping ImageFilter,
  _ rhs: @escaping ImageFilter
) -> ImageFilter {
  { image in lhs(image).flatMap { rhs($0) } }
}
