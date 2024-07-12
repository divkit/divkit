import Foundation

import VGSL

/// The `DivPatchProvider` protocol is responsible for downloading DivKit patches.
///
/// The protocol requires the implementation of two methods:
/// - `getPatch(url:completion:)`: This method is used to download a patch from the specified URL.
/// The patch data should be returned through the `DivPatchProviderCompletion` completion handler.
/// - `cancelRequests()`: This method allows DivKit to cancel any ongoing patch download requests.
///
/// By default, the `DivPatchProvider` downloads patches from the network via the
/// `requestPerformer`. However, by adopting this protocol, you can provide your own patch download
/// implementation to suit your specific requirements.
public protocol DivPatchProvider {
  /// Downloads a patch from the specified URL.
  ///
  /// - Parameters:
  ///   - url: The URL from which to download the patch.
  ///   - completion: The completion handler to be called when the download is complete.
  ///                 The `Result` object contains the downloaded patch data on success or an
  /// `Error` on failure.
  func getPatch(url: URL, completion: @escaping DivPatchProviderCompletion)

  /// Cancels any ongoing patch download requests.
  func cancelRequests()
}

public typealias DivPatchProviderCompletion = (Result<DivPatch, Error>) -> Void

public func parseDivPatch(_ data: Data) throws -> DivPatch {
  let dataJson = try JSONSerialization.jsonObject(with: data) as? JsonDictionary ?? [:]
  let templates = DivTemplates(
    dictionary: dataJson["templates"] as? JsonDictionary ?? [:]
  )
  return try templates.parseValue(
    type: DivPatchTemplate.self,
    from: dataJson["patch"] as? JsonDictionary ?? [:]
  ).unwrap()
}

private typealias JsonDictionary = [String: Any]
