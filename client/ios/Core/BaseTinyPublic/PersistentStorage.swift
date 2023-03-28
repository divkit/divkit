// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

@objc(YDAAsyncPersistentStorage)
public protocol AsyncPersistentStorage {
  func readObject(forKey key: String, completion: @escaping (NSCoding?, NSError?) -> Void)

  // Transfering nil as first object should remove it from storage
  func writeObject(_ object: NSCoding?, forKey key: String, completion: (() -> Void)?)
}

extension AsyncPersistentStorage {
  public func readObject(forKey key: String, completion: @escaping (NSCoding?) -> Void) {
    readObject(forKey: key, completion: { value, _ in completion(value) })
  }
}

// Implementation of this class should provide fast access to load/store methods
// No long-term file operation or hard calculating is allowed here
@objc(YCCSyncPersistentStorage)
public protocol SyncPersistentStorage {
  @objc(objectForKeyedSubscript:)
  func object(forKey key: String) -> NSCoding?

  // Transfering nil as first object should remove it from storage
  @objc(setObject:forKeyedSubscript:)
  func setObject(_ object: NSCoding?, forKey key: String)
}

extension SyncPersistentStorage {
  public subscript(key: String) -> NSCoding? {
    get { object(forKey: key) }
    set { setObject(newValue, forKey: key) }
  }
}
