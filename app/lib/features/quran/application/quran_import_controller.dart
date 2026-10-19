import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../data/import/import_payload.dart';
import '../data/import/quran_importer.dart';
import 'quran_providers.dart';

/// حالة عملية الاستيراد كما تراها الواجهة.
enum ImportPhase { idle, picking, importing, done, error }

class ImportState {
  const ImportState({
    this.phase = ImportPhase.idle,
    this.errors = const <String>[],
    this.report,
  });

  final ImportPhase phase;
  final List<String> errors;

  /// تقرير آخر استيراد ناجح.
  final ImportReport? report;

  bool get busy =>
      phase == ImportPhase.picking || phase == ImportPhase.importing;

  ImportState copyWith({
    ImportPhase? phase,
    List<String>? errors,
    ImportReport? report,
  }) =>
      ImportState(
        phase: phase ?? this.phase,
        errors: errors ?? this.errors,
        report: report ?? this.report,
      );
}

/// يختار ملف `quran-import.json` ويستورده.
///
/// ### لماذا `file_picker` لا مسار ثابت؟
/// على أندرويد ١١+ لا يمكن قراءة ملف من `Downloads` بلا صلاحية واسعة
/// (`MANAGE_EXTERNAL_STORAGE`) ترفضها متاجر التطبيقات. مُنتقي الملفات
/// يستخدم SAF ويمرّر ملفًا يختاره المستخدم بنفسه — بلا أي صلاحية.
class QuranImportController extends StateNotifier<ImportState> {
  QuranImportController(this._importer);

  final QuranImporter _importer;

  /// يفتح مُنتقي الملفات ثم يستورد.
  ///
  /// **الترتيب مهم:** نتحقّق من الرخصة والمحتوى قبل لمس قاعدة البيانات.
  /// لو فشل أي شرط، تبقى القاعدة كما هي — لا استيراد جزئي.
  Future<bool> pickAndImport() async {
    state = state.copyWith(phase: ImportPhase.picking, errors: const <String>[]);

    final result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: const ['json'],
      withData: false,
    );

    final path = result?.files.singleOrNull?.path;
    if (path == null) {
      // المستخدم ألغى — ليست حالة خطأ
      state = state.copyWith(phase: ImportPhase.idle);
      return false;
    }

    state = state.copyWith(phase: ImportPhase.importing);

    String text;
    try {
      text = await File(path).readAsString();
    } catch (error) {
      state = state.copyWith(
        phase: ImportPhase.error,
        errors: <String>['تعذّر قراءة الملف: $error'],
      );
      return false;
    }

    final parsed = ImportPayload.parse(text);
    if (!parsed.ok) {
      state = state.copyWith(
        phase: ImportPhase.error,
        errors: parsed.errors,
      );
      return false;
    }

    final report = await _importer.import(parsed.payload!);
    if (!report.ok) {
      state = state.copyWith(phase: ImportPhase.error, errors: report.errors);
      return false;
    }

    state = state.copyWith(phase: ImportPhase.done, report: report);
    return true;
  }

  /// يمسح كل بيانات القرآن (قبل إعادة الاستيراد).
  Future<void> clear() async {
    state = state.copyWith(phase: ImportPhase.importing);
    await _importer.clear();
    state = state.copyWith(
      phase: ImportPhase.idle,
      report: null,
      errors: const <String>[],
    );
  }

  void reset() => state = const ImportState();
}

final quranImportProvider =
    StateNotifierProvider<QuranImportController, ImportState>((ref) {
  return QuranImportController(ref.watch(quranImporterProvider));
});
