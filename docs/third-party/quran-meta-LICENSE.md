MIT License

Copyright (c) 2020 Quran-Center

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---

المصدر: `quran-meta` 7.0.0 — حدود الأجزاء والصفحات والأحزاب والأثمان
لثماني روايات. استعملنا منه قائمة **حفص** وحدها:

- `JuzList` — بداية كل جزء (٣٠ جزءًا + حارسَان)
- `PageList` — بداية كل صفحة (٦٠٤ صفحات + حارسان) برواية حفص
- عدد آيات كل سورة (من `chapters.json` في مصدر نصّ القرآن)

⚠️ هذه **أرقام بنيوية** لا نصّ قرآني، ولذلك تُرفع إلى المستودع
(`docs/09` §1 يستثني نصّ القرآن لا حدوده). وهي مولّدة في
`QuranMeta.kt` لا محفوظة كأصل، فتُختبر على JVM بلا أصول.
