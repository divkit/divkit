// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

public protocol KeyValueDirectStoringSupporting {}
extension Data: KeyValueDirectStoringSupporting {}
extension CFString: KeyValueDirectStoringSupporting {}
extension NSString: KeyValueDirectStoringSupporting {}
extension String: KeyValueDirectStoringSupporting {}
extension NSNumber: KeyValueDirectStoringSupporting {}
extension Bool: KeyValueDirectStoringSupporting {}
extension UInt: KeyValueDirectStoringSupporting {}
extension Int: KeyValueDirectStoringSupporting {}
extension Int64: KeyValueDirectStoringSupporting {}
extension Date: KeyValueDirectStoringSupporting {}
extension NSArray: KeyValueDirectStoringSupporting {}
extension Array: KeyValueDirectStoringSupporting where Element: KeyValueDirectStoringSupporting {}
extension NSDictionary: KeyValueDirectStoringSupporting {}
extension Dictionary: KeyValueDirectStoringSupporting {}
extension TimeInterval: KeyValueDirectStoringSupporting {}
