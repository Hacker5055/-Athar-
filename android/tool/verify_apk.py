#!/usr/bin/env python3
"""يتحقّق أن الـ APK يحمل البيانات التي لا يعمل التطبيق بدونها.

⚠️ لماذا يوجد هذا السكربت؟
البيئة التي نبني فيها لا تستطيع تنزيل الـ APK وفحصه، فالفحص الوحيد
الموثوق هو أن يفحصه الـ CI بنفسه بعد البناء. وبدونه قد يبنى التطبيق
«أخضر» وهو لا يحمل نصّ القرآن أو الأذكار — عطل لا يظهر في الاختبارات
ولا في البناء، بل على جهاز المستخدم حين يفتح تبويبًا فارغًا.

⚠️ ولماذا نفحص الحجم لا الوجود فقط؟
ملف موجود وفارغ أسوأ من ملف غائب: يمرّ فحص الوجود، ثم يعطي قائمة
فارغة بلا سبب ظاهر.
"""
import sys
import zipfile
from pathlib import Path

REQUIRED = {
    "assets/adhkar/hisn.json": 100_000,
    "assets/quran/uthmani.json": 1_000_000,
    "assets/quran/chapters.json": 50_000,
}


def main() -> int:
    if len(sys.argv) != 2:
        print("الاستخدام: verify_apk.py <مسار الـ APK>")
        return 2

    apk = Path(sys.argv[1])
    if not apk.exists():
        print("❌ لا وجود للملف: %s" % apk)
        return 2

    with zipfile.ZipFile(apk) as archive:
        entries = {info.filename: info for info in archive.infolist()}
        print("📦 %s — %d ملفًا داخله" % (apk.name, len(entries)))

        failed = False
        for name, minimum in REQUIRED.items():
            info = entries.get(name)
            if info is None:
                print("❌ غائب: %s" % name)
                failed = True
            elif info.file_size < minimum:
                print("❌ صغير جدًا: %s = %d بايت (الحد الأدنى %d)" % (
                    name, info.file_size, minimum))
                failed = True
            else:
                print("✅ %s — %s بايت" % (name, format(info.file_size, ",")))

    if failed:
        print("\n❌ الـ APK ناقص بيانات — لن يُرفع")
        return 1
    print("\n✅ كل البيانات داخل الـ APK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
