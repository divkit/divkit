import UIKit

extension UIViewController {
  func showAlert(
    title: String? = nil,
    message: String? = nil
  ) {
    let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
    alert.addAction(UIAlertAction(title: "OK", style: .default))
    present(alert, animated: true)
  }
}
