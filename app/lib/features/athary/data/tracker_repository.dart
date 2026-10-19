import 'package:shared_preferences/shared_preferences.dart';

import '../domain/tracker_state.dart';

const String trackerKey = 'athar.tracker.v1';

/// تخزين المتتبع — **ملف واحد مستقل** عن إعدادات التطبيق والصلاة.
///
/// لو تلف، نرجّع لحالة فارغة (صفر عبادات) ولا نؤثر على أي إعداد آخر.
/// أسوأ سيناريو: المستخدم يفقد سجله — ولا يفقد مواقيته ولا لغته.
class TrackerRepository {
  const TrackerRepository(this._prefs);

  final SharedPreferences _prefs;

  TrackerState load() => TrackerState.decode(_prefs.getString(trackerKey));

  Future<void> save(TrackerState state) =>
      _prefs.setString(trackerKey, state.encode());
}
