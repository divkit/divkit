// Generated code. Do not modify.

import '../utils/parsing_utils.dart';
import 'div_action_typed.dart';
import 'div_download_callbacks.dart';

abstract class DivSightAction {
  DivDownloadCallbacks? get downloadCallbacks;

  // default value: true
  Expression<bool> get isEnabled;

  Expression<String> get logId;

  // constraint: number >= 0; default value: 1
  Expression<int> get logLimit;

  Map<String, dynamic>? get payload;

  Expression<Uri>? get referer;

  DivActionTyped? get typed;

  Expression<Uri>? get url;
}
