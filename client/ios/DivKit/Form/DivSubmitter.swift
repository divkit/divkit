import Foundation

/// The `DivSubmitter` protocol is responsible for submitting data from container.
///
/// The protocol requires the implementation of two methods:
/// - `submit(request:data:completion:)`: This method is used to submit data using specified request.
/// - `cancelRequests()`: This method allows DivKit to cancel any ongoing submit requests.
///
/// By default, the `DivSubmitter` submitting data to the network using the
/// `requestPerformer` and `POST` method.
/// However, by adopting this protocol, you can provide your own implementation to suit your specific requirements.
public protocol DivSubmitter {
  /// Submit data using specified request.
  ///
  /// - Parameters:
  ///   - request: The request for submitting data.
  ///   - data: The data from the container.
  ///   - completion: The completion handler to be called when the submit request is complete.
  func submit(
    request: SubmitRequest,
    data: [String: String],
    completion: @escaping DivSubmitterCompletion
  )

  /// Cancels any ongoing submit requests.
  func cancelRequests()
}

public typealias DivSubmitterCompletion = (Result<Void, NSError>) -> Void
