// Copyright 2022 Yandex LLC. All rights reserved.

import CoreImage

public typealias ImageFilter = (CIImage) -> CIImage?
public typealias ImageGenerator = () -> CIImage?
public typealias ImageComposer = (CIImage) -> ImageFilter

public func combine(
  _ f1: @escaping ImageFilter,
  _ f2: @escaping ImageFilter
) -> ImageFilter {
  { image in f1(image).flatMap { f2($0) } }
}

public func combine(
  _ f1: @escaping ImageFilter,
  _ f2: @escaping ImageFilter,
  _ f3: @escaping ImageFilter
) -> ImageFilter {
  combine(combine(f1, f2), f3)
}
