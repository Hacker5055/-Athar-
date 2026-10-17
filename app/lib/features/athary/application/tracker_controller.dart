import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../core/settings/settings_controller.dart' show sharedPreferencesProvider;
import '../data/tracker_repository.dart';
import '../domain/daily_record.dart';
import '../domain/habit.dart';
import '../domain/tracker_state.dart';

export '../data/tracker_repository.dart' show trackerKey;
export '../domain/daily_record.dart' show civilDay;

/// اليوم المدني الحالي (محلي) — يُقرأ لحظة البناء.
///
/// لا نجعله StreamProvider: الشاشة تُعاد بناؤها عند أي تغيير في
/// [trackerProvider]، والحدّ الأدنى المقبول هو تحديثه عند فتح الشاشة.
final todayProvider = Provider<DateTime>((ref) => civilDay(DateTime.now()));

final trackerRepositoryProvider = Provider<TrackerRepository>((ref) {
  final prefs = ref.watch(sharedPreferencesProvider);
  return TrackerRepository(prefs);
});

/// حالة «أثري» — محفوظة تحت `athar.tracker.v1`.
final trackerProvider =
    StateNotifierProvider<TrackerController, TrackerState>((ref) {
  final repository = ref.watch(trackerRepositoryProvider);
  return TrackerController(repository);
});

class TrackerController extends StateNotifier<TrackerState> {
  TrackerController(this._repository) : super(_repository.load());

  final TrackerRepository _repository;

  /// يبدّل عبادة اليوم (أو يوم بعينه — للتصحيح والاختبار).
  Future<void> toggle(HabitId habit, {DateTime? at}) async {
    final day = civilDay(at ?? DateTime.now());
    state = state.toggle(day, habit).pruned(day);
    await _repository.save(state);
  }

  /// يمسح كل السجلات — لا رجعة فيه (مُحاط بتأكيد في الواجهة).
  Future<void> clearAll() async {
    state = TrackerState.empty;
    await _repository.save(state);
  }
}
