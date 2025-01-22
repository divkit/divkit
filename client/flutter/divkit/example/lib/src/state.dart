import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

final navigatorKey =
    GlobalKey<NavigatorState>(debugLabel: 'DivKit Playground App');
const inputVariable = 'input_variable';
const pollingInterval = 'polling_interval';

// Create a provider for SharedPreferences
final prefsProvider = Provider<SharedPreferences>((ref) {
  throw UnimplementedError(
      'SharedPreferences has not been initialized yet. Ensure it is initialized before running the app.');
});

// First, create a provider for polling rate
final pollingIntervalProvider =
    StateNotifierProvider<PollingIntervalNotifier, int?>((ref) {
  final prefs = ref.watch(prefsProvider);
  return PollingIntervalNotifier(prefs);
});

// Create a StateNotifier to manage the polling rate state
class PollingIntervalNotifier extends StateNotifier<int?> {
  final SharedPreferences prefs;

  PollingIntervalNotifier(this.prefs) : super(prefs.getInt(pollingInterval));

  void update(int? newRate) {
    if (newRate == null) {
      prefs.remove(pollingInterval);
    } else {
      prefs.setInt(pollingInterval, newRate);
    }
    state = newRate;
  }
}

final reloadNProvider = StateProvider((ref) => 0);
final nightModeProvider = StateProvider((ref) => false);
final isRTLProvider = StateProvider((ref) => false);
