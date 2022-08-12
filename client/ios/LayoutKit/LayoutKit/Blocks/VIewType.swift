// Copyright 2022 Yandex LLC. All rights reserved.

#if os(iOS)
import UIKit
#endif

#if os(iOS)
public typealias ViewType = UIView
#else
public typealias ViewType = AnyObject
#endif
