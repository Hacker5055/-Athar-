/* ═══════════════════════════════════════════════════════════════
   app.js — منطق البروتوتايب التفاعلي
   ═══════════════════════════════════════════════════════════════ */

/* ─────────────── الحالة ─────────────── */
const state = {
  lang: 'ar', theme: 'light', tab: 'home', stack: [],
  city: 'cairo', method: 'egyptian', madhab: 'shafi',
  arabicDigits: true,
  quranFont: 22, showTrans: 'en', showTafsir: true, reciter: 0, playing: false,
  lastRead: { surah: 36, ayah: 12 },
  wirdDone: 1, wirdTotal: 4,
  adhkar: {}, selAyah: null,
  tasbih: { n: 0, target: 33, preset: 0 },
  tracker: {}, streak: 12,
  heading: 0, hasSensor: false, anno: true,
};

const el = id => document.getElementById(id);
const esc = s => String(s).replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));

/* ─────────────── تنسيق الأرقام والوقت ─────────────── */
const AR_DIGITS = ['٠','١','٢','٣','٤','٥','٦','٧','٨','٩'];
function dg(v) {
  const s = String(v);
  return (LANG === 'ar' && state.arabicDigits) ? s.replace(/[0-9]/g, d => AR_DIGITS[+d]) : s;
}
const pad2 = n => String(n).padStart(2, '0');

function fmtTime(h) {
  h = ((h % 24) + 24) % 24;
  let hh = Math.floor(h), mm = Math.round((h - hh) * 60);
  if (mm === 60) { mm = 0; hh = (hh + 1) % 24; }
  if (LANG === 'ar') {
    const p = hh >= 12 ? 'م' : 'ص';
    let h12 = hh % 12; if (h12 === 0) h12 = 12;
    return dg(h12) + ':' + dg(pad2(mm)) + ' ' + p;
  }
  if (LANG === 'fr') return pad2(hh) + 'h' + pad2(mm);
  const p = hh >= 12 ? 'PM' : 'AM';
  let h12 = hh % 12; if (h12 === 0) h12 = 12;
  return h12 + ':' + pad2(mm) + ' ' + p;
}
function cityNowHours() {
  const d = new Date();
  const tz = Calc.tzOffset(city().tz);
  return ((d.getUTCHours() + d.getUTCMinutes() / 60 + tz) % 24 + 24) % 24;
}
const city = () => CITIES.find(c => c.id === state.city) || CITIES[0];
const cityName = () => city()[LANG] || city().ar;

function getTimes(date) {
  const c = city();
  return Calc.prayerTimes(date || new Date(), c.lat, c.lng, Calc.tzOffset(c.tz), {
    method: state.method, madhab: state.madhab,
  });
}
const PRAYER_ORDER = ['fajr', 'dhuhr', 'asr', 'maghrib', 'isha'];

/* ─────────────── الأيقونات ─────────────── */
const svg = (p, extra = '') =>
  `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" ${extra}>${p}</svg>`;
const IC = {
  home: svg('<path d="M3 10.5 12 3l9 7.5"/><path d="M5.5 9.5V20h13V9.5"/>'),
  quran: svg('<path d="M4 5.5A1.5 1.5 0 0 1 5.5 4H11v16H5.5A1.5 1.5 0 0 1 4 18.5v-13Z"/><path d="M20 5.5A1.5 1.5 0 0 0 18.5 4H13v16h5.5a1.5 1.5 0 0 0 1.5-1.5v-13Z"/>'),
  adhkar: svg('<circle cx="12" cy="10" r="7"/><circle cx="12" cy="3" r="1.5"/><circle cx="19" cy="10" r="1.2"/><circle cx="5" cy="10" r="1.2"/><circle cx="12" cy="17" r="1.2"/>'),
  qibla: svg('<circle cx="12" cy="12" r="9"/><path d="m15.8 8.2-2.3 4.9-4.9 2.3 2.3-4.9z"/>'),
  tasbih: svg('<path d="M20 12a8 8 0 1 1-2.6-5.9"/><path d="M20.4 4.5v4.2h-4.2"/>'),
  more: svg('<circle cx="5" cy="12" r="1.6" fill="currentColor" stroke="none"/><circle cx="12" cy="12" r="1.6" fill="currentColor" stroke="none"/><circle cx="19" cy="12" r="1.6" fill="currentColor" stroke="none"/>'),
  search: svg('<circle cx="11" cy="11" r="7"/><path d="m20 20-3.6-3.6"/>'),
  gear: svg('<circle cx="12" cy="12" r="3"/><path d="M12 2.5v2.6M12 18.9v2.6M2.5 12h2.6M18.9 12h2.6M5.3 5.3l1.9 1.9M16.8 16.8l1.9 1.9M18.7 5.3l-1.9 1.9M7.2 16.8l-1.9 1.9"/>'),
  globe: svg('<circle cx="12" cy="12" r="9"/><path d="M3 12h18"/><path d="M12 3a15 15 0 0 1 0 18 15 15 0 0 1 0-18"/>'),
  back: svg('<path d="m15 5-7 7 7 7"/>', 'class="flip"'),
  chev: svg('<path d="m9 5 7 7-7 7"/>', 'class="flip"'),
  check: svg('<path d="m4 12.5 5 5L20 6.5"/>'),
  bookmark: svg('<path d="M6.5 4h11v17l-5.5-4-5.5 4z"/>'),
  share: svg('<path d="M4 12.5V19a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1v-6.5"/><path d="m8 8 4-4 4 4"/><path d="M12 4v12"/>'),
  play: svg('<path d="M7 4.5 19.5 12 7 19.5z" fill="currentColor" stroke="none"/>'),
  pause: svg('<path d="M8 4.5v15M16 4.5v15" stroke-width="2.2"/>'),
  plus: svg('<path d="M12 5v14M5 12h14"/>'),
  minus: svg('<path d="M5 12h14"/>'),
  calendar: svg('<rect x="3" y="5" width="18" height="16" rx="2.5"/><path d="M3 10h18M8 3v4M16 3v4"/>'),
  moon: svg('<path d="M20.5 14.8A8.8 8.8 0 1 1 9.2 3.5a6.8 6.8 0 0 0 11.3 11.3Z"/>'),
  sun: svg('<circle cx="12" cy="12" r="4.2"/><path d="M12 2v2.4M12 19.6V22M2 12h2.4M19.6 12H22M4.9 4.9l1.7 1.7M17.4 17.4l1.7 1.7M19.1 4.9l-1.7 1.7M6.6 17.4l-1.7 1.7"/>'),
  volume: svg('<path d="M4 9.5v5h3.5L12 18V6L7.5 9.5Z"/><path d="M16 8.5a5 5 0 0 1 0 7"/>'),
  star: svg('<path d="m12 3.5 2.6 5.8 6.3.6-4.8 4.2 1.4 6.2L12 17.1l-5.5 3.2 1.4-6.2-4.8-4.2 6.3-.6z"/>'),
  download: svg('<path d="M12 3v12"/><path d="m7 11 5 5 5-5"/><path d="M4 20h16"/>'),
  clock: svg('<circle cx="12" cy="12" r="9"/><path d="M12 7v5.2l3.4 2"/>'),
  leaf: svg('<path d="M5 19c0-8 6-13 15-13 0 9-5 14-11 14H5Z"/><path d="M5 19c3-5 7-8 12-9.5"/>'),
  grid: svg('<rect x="3.5" y="3.5" width="7" height="7" rx="1.8"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.8"/><rect x="3.5" y="13.5" width="7" height="7" rx="1.8"/><rect x="13.5" y="13.5" width="7" height="7" rx="1.8"/>'),
  kaaba: svg('<path d="M4 10h16v9H4z"/><path d="M6.5 10V6.5A1.5 1.5 0 0 1 8 5h8a1.5 1.5 0 0 1 1.5 1.5V10"/><path d="M9.5 19v-4h5v4"/>'),
};

/* ─────────────── ملاحظات التصميم ─────────────── */
const NOTES = {
  home: [
    '<b>بطاقة الصلاة القادمة أولًا.</b> المستخدم بيفتح التطبيق عشان يعرف «باقي قد إيه» — فالعدّاد التنازلي أكبر عنصر في الشاشة وبأعلى تباين.',
    '<b>شبكة مواقيت ٣×٢.</b> كل الوقت ظاهر في شاشة واحدة بدون تمرير، والصلاة الحالية مظلّلة بلون أساسي مع شريط ذهبي تحتها.',
    '<b>«تابع القراءة».</b> اختصار مباشر لآخر موضع — بيختصر ٣ نقرات وبيشجّع على الاستمرار.',
    '<b>وردي + أذكاري.</b> ربط الالتزام اليومي بحلقات تقدم مرئية: دي بالضبط قيمة «أثري»، مش مجرد أرقام.',
    '<b>إجراءات سريعة في النصف السفلي.</b> كلها في متناول الإبهام لتجربة استخدام بيد واحدة.',
  ],
  quran: [
    '<b>بحث لاصق في الأعلى.</b> البحث عن سورة أو آية تاني أهم مدخل بعد القراءة نفسها.',
    '<b>تصفية بنقرة واحدة.</b> شرائح أفقية (الكل/مكية/مدنية) بدل قائمة منسدلة.',
    '<b>رقم السورة داخل مربّع مزخرف.</b> إحساس المصحف + تنظيم بصري واضح في RTL.',
    '<b>اسم السورة بالخط النسخي</b> مع التحتي باللاتيني — ضروري للمغتربين وغير الناطقين بالعربية.',
  ],
  surah: [
    '<b>بسملة بخط المصحف</b> في أعلى كل سورة (عدا التوبة) — بتحدد نبرة الشاشة كلها.',
    '<b>النص العثماني بحجم ٢٢ قابل للتكبير حتى ٤٠.</b> راحة القراءة أهم من كثافة المعلومات.',
    '<b>رقم الآية في دائرة مزخرفة</b> بدل القوسين، وبنفس الخطوط العثمانية المعتادة.',
    '<b>الترجمة والتفسير تحت الآية مباشرة</b> بوزن أخف وحجم أصغر عشان ما يزاحمش النص العربي.',
    '<b>شريط تلاوة ثابت أسفل الشاشة</b> — تحكم دائم في الصوت بدون ما يغطّي النص.',
  ],
  adhkar: [
    '<b>شبكة تصنيفات ٢×٢.</b> أذكار الصباح والمساء هما أكتر مدخلين استخدامًا، فهما في الأول.',
    '<b>شريط تقدّم ذهبي</b> يوضّح نسبة الإتمام لكل مجموعة بدون فتحها.',
    '<b>عدّاد داخل كل ذكر.</b> الأذكار أفعال مش نصوص تُقرأ — العدّاد بيخلّيها تتحقق فعليًا.',
    '<b>التخريج تحت كل ذكر.</b> مصداقية شرعية: لا ذكر بدون مصدر، ودي كمان ميزة ثقة.',
  ],
  qibla: [
    '<b>القرص هو اللي يدور، مش الإبرة.</b> إبقاء علامة الشمال ثابتة أسهل في الفهم من دوران الإبرة.',
    '<b>الكعبة في مركز المؤشّر</b> — رمز بصري مباشر أوضح من أي سهم.',
    '<b>تنبيه المعايرة.</b> المستشعرات بتحتاج معايرة؛ تجاهل الحالة دي = تجربة سيئة ومراجعات سلبية.',
    '<b>المسافة والزاوية تحت البوصلة</b> بتدي إحساس بالدقة والثقة.',
  ],
  tracker: [
    '<b>سلسلة الأيام (Streak) برقم كبير.</b> أقوى محفّز سلوكي معروف، ومعبّر عنه بوضوح.',
    '<b>شبكة ٣٠ يوم على غرار مساهمات GitHub.</b> تكثيف لوني حسب عدد العبادات في اليوم.',
    '<b>العبادات قابلة للتخصيص.</b> المستخدم هو اللي يختار «أثره» — ملوش حدود مفروضة.',
    '<b>اللون مش الإشارة الوحيدة.</b> دائمًا مع نص/أيقونة، احترامًا لإمكانية الوصول.',
  ],
  more: [
    '<b>تبويب «المزيد» يجمع الأدوات الثانوية</b> عشان الشريط السفلي يفضّل ٥ عناصر فقط (قاعدة الاستخدام بيد واحدة).',
    '<b>اللغة والمظهر في الأعلى.</b> أكتر إعدادين مطلوبين، خصوصًا للجمهور الفرنكفوني والمغتربين.',
    '<b>«المصادر والتراخيص» عنصر أساسي مش مخفي.</b> الشفافية هنا بتبني ثقة ما بتبنيهوش أي حملة تسويق.',
  ],
  settings: [
    '<b>تجميع الإعدادات في ٣ مجموعات</b> (عام / العبادة / المحتوى) بدل قائمة طويلة مسطّحة.',
    '<b>كل إعداد يوضّح قيمته الحالية على اليسار</b> بدل ما يفتح شاشة تانية لمجرد المعرفة.',
    '<b>المفاتيح (switches) بدل القوائم المنسدلة</b> للخيارات الثنائية — نقرة واحدة بدل نقرتين.',
  ],
  tasbih: [
    '<b>عدّاد دائري كبير.</b> الهدف من الشاشة فِعل واحد: النقر. فكل المساحة ليه.',
    '<b>حلقة التقدّم هي نفسها زر النقر</b> — دمج العنصر المرئي والعنصر التفاعلي.',
    '<b>قوالب جاهزة بالأعلى</b> (سبحان الله / الحمد لله / الله أكبر…) لتبديل سريع بدون كتابة.',
  ],
  calendar: [
    '<b>التاريخين جنب بعض.</b> الهجري هو الأساسي، والميلادي مرجع — مش العكس.',
    '<b>المناسبات مع عدد الأيام المتبقية.</b> معلومة قابلة للتنفيذ فورًا.',
    '<b>إزاحة هجرية قابلة للضبط</b> لأن الحساب الفلكي مختلف عن الرؤية الشرعية.',
  ],
  duas: [
    '<b>بطاقة لكل دعاء</b> بمساحة قراءة مريحة — الأدعية بتُقرأ مش تُ skim.',
    '<b>المصدر/التخريج في سطر صغير بالأسفل.</b> توثيق دائم بدون إزعاج بصري.',
    '<b>ترجمة اختيارية بلغة التطبيق</b> للمستخدمين غير الناطقين بالعربية.',
  ],
  names: [
    '<b>شبكة ٢ أعمدة.</b> الأسماء قصيرة فالعمود الواحد هيهدر مساحة.',
    '<b>الاسم العربي بالخط النسخي الثقيل</b> والمعنى تحته بلغة التطبيق.',
    '<b>رقم ترتيبي صغير</b> يساعد الحفّاظ على المتابعة (١ إلى ٩٩).',
  ],
  prayer: [
    '<b>المواقيت في صفوف واضحة</b> مع تمييز الوقت الحالي — نفس بيانات الرئيسية لكن بتفصيل أكبر.',
    '<b>اسم طريقة الحساب ظاهر بشفافية.</b> الاختلاف بين الطرق بيوصل لـ ٢٠ دقيقة، والمستخدم لازم يعرف.',
    '<b>إعدادات لكل صلاة على حدة</b> (صوت، تذكير، تعديل) بدل مقاس واحد للكل.',
  ],
};

/* ═══════════════════════════════════════════════════════════════
   الشاشات
   ═══════════════════════════════════════════════════════════════ */

/* ── الشاشة الرئيسية ── */
function screenHome() {
  const PT = getTimes(), now = cityNowHours();
  let next = PRAYER_ORDER[0], minDiff = 99;
  PRAYER_ORDER.forEach(k => {
    let d = t[k] - now; if (d <= 0) d += 24;
    if (d < minDiff) { minDiff = d; next = k; }
  });

  const hijri = Calc.toHijri(new Date(), 0);
  const hm = Calc.HIJRI_MONTHS[LANG] || Calc.HIJRI_MONTHS.ar;

  const grid = ['fajr', 'sunrise', 'dhuhr', 'asr', 'maghrib', 'isha'].map(k => {
    let cls = 'prayer';
    if (k === next) cls += ' is-now';
    else if (PT[k] < now) cls += ' is-past';
    return `<div class="${cls}">
      <div class="prayer__name">${t(k)}</div>
      <div class="prayer__time">${fmtTime(PT[k])}</div>
    </div>`;
  }).join('');

  const lr = SURAHS.find(s => s.n === state.lastRead.surah);
  const qa = [
    ['quran', IC.quran, 'qa_quran', "data-act='go' data-tab='quran'"],
    ['adhkar', IC.adhkar, 'qa_adhkar', "data-act='go' data-tab='adhkar'"],
    ['qibla', IC.qibla, 'qa_qibla', "data-act='go' data-tab='qibla'"],
    ['tasbih', IC.leaf, 'qa_tasbih', "data-act='more' data-page='tasbih'"],
    ['duas', IC.star, 'qa_duas', "data-act='more' data-page='duas'"],
    ['names', IC.grid, 'qa_names', "data-act='more' data-page='names'"],
  ].map(([id, ico, key, act]) =>
    `<div class="qa__item" ${act}><div class="qa__ico">${ico}</div><div class="qa__lbl">${t(key)}</div></div>`
  ).join('');

  const wirdP = Math.round(state.wirdDone / state.wirdTotal * 100);
  const adhkP = 40;

  return `
  <div class="hero" data-anno="1">
    <div class="hero__badge">${cityName()}</div>
    <div class="hero__label">${t('next_prayer')}</div>
    <h2 class="hero__name">${t(next)}</h2>
    <div class="hero__count" id="cd">--:--:--</div>
    <div class="hero__meta"><span>${Calc.METHODS[state.method][LANG]}</span><span>·</span><span>${t(state.madhab)}</span></div>
  </div>

  <div class="sec-title"><span data-anno="2">${t('today')}</span>
    <span class="sec-title__more" data-act="more" data-page="prayer">${t('monthly_calendar')} ›</span></div>
  <div class="prayers" data-anno="2">${grid}</div>

  <div class="sec-title">${t('continue_reading')}</div>
  <div class="card row row--between" data-act="openReader" data-s="${lr.n}" data-anno="3" style="cursor:pointer">
    <div class="row" style="gap:12px">
      <div style="width:40px;height:40px;border-radius:12px;background:var(--primary-100);color:var(--primary);display:grid;place-items:center">${IC.quran}</div>
      <div>
        <div style="font-family:var(--font-display);font-size:17px;font-weight:700">سورة ${lr.ar}</div>
        <div class="tiny muted">${t('ayah_no')} ${dg(state.lastRead.ayah)} · ${LANG === 'ar' ? lr.ar : lr[LANG]}</div>
      </div>
    </div>
    <div style="color:var(--ink-40);display:grid;place-items:center">${IC.chev}</div>
  </div>

  <div class="sec-title">${t('adhkar_today')}</div>
  <div class="card" data-anno="4">
    <div class="wird">
      <div class="ring" style="--p:${wirdP}"><span class="ring__txt">${dg(state.wirdDone)}/${dg(state.wirdTotal)}</span></div>
      <div class="grow">
        <div style="font-size:14px;font-weight:600">${t('wird_title')}</div>
        <div class="tiny muted">${t('wird_sub')}</div>
      </div>
      <button class="btn btn--soft btn--sm">${t('done')}</button>
    </div>
    <div class="wird">
      <div class="ring" style="--p:${adhkP};--c:var(--accent)"><span class="ring__txt">${dg(adhkP)}%</span></div>
      <div class="grow">
        <div style="font-size:14px;font-weight:600">${t('adhkar_today')}</div>
        <div class="tiny muted">${t('morning')} · ${t('evening')}</div>
      </div>
      <button class="btn btn--ghost btn--sm" data-act="go" data-tab="adhkar">${t('adhkar')}</button>
    </div>
  </div>

  <div class="sec-title">${t('quick_actions')}</div>
  <div class="qa" data-anno="5">${qa}</div>

  <div class="sec-title">${t('verse_of_day')}</div>
  <div class="ayahcard">
    <p class="ayahcard__txt">إِنَّا نَحۡنُ نُحۡيِ ٱلۡمَوۡتَىٰ وَنَكۡتُبُ مَا قَدَّمُواْ وَءَاثَٰرَهُمۡۚ وَكُلَّ شَيۡءٍ أَحۡصَيۡنَٰهُ فِيٓ إِمَامٖ مُّبِينٖ</p>
    <div class="ayahcard__ref">سورة يس · ${t('ayah_no')} ${dg(12)}</div>
  </div>`;
}

/* ── المواقيت ── */
function screenPrayer() {
  const PT = getTimes(), now = cityNowHours();
  const rows = ['fajr', 'sunrise', 'dhuhr', 'asr', 'maghrib', 'isha'].map(k => {
    const isNow = k !== 'sunrise' && Math.abs(PT[k] - now) < 0.5;
    return `<div class="surah-item" style="border-bottom:1px solid var(--border)">
      <div class="grow">
        <div style="font-weight:600;font-size:15px">${t(k)} ${isNow ? `<span class="chip is-on" style="padding:2px 8px;font-size:10px">${t('now')}</span>` : ''}</div>
        <div class="tiny muted">${fmtTime(PT[k])}</div>
      </div>
      <div style="font-size:19px;font-weight:600;font-variant-numeric:tabular-nums">${fmtTime(PT[k])}</div>
    </div>`;
  }).join('');

  return `
  <div class="card" data-anno="1">
    <div class="row row--between" style="margin-bottom:8px">
      <div style="font-family:var(--font-display);font-size:17px;font-weight:700">${t('prayer_times')}</div>
      <div class="tiny muted">${cityName()}</div>
    </div>
    ${rows}
  </div>

  <div class="card" data-anno="2">
    <div style="font-weight:600;margin-bottom:10px">${t('calc_settings')}</div>
    <div class="setrow" data-act="sheet" data-page="prayerSettings">
      <div class="setrow__ico">${IC.gear}</div>
      <div class="grow"><div class="setrow__t">${t('calc_settings')}</div>
        <div class="setrow__s">${Calc.METHODS[state.method][LANG]}</div></div>
      <div class="setrow__v">${IC.chev}</div>
    </div>
  </div>

  <button class="btn btn--ghost btn--block" data-anno="3" data-act="sheet" data-page="prayerSettings">
    ${IC.volume} ${t('notifications')} & ${t('adhan_sound')}
  </button>
  <div class="tiny muted" style="text-align:center;margin-top:10px">${t('demo_data')}</div>`;
}

/* ── فهرس القرآن ── */
function screenQuran() {
  const list = SURAHS.map(s => `
    <div class="surah-item" data-act="openReader" data-s="${s.n}">
      <div class="surah-num">${dg(s.n)}</div>
      <div class="grow">
        <div class="surah-name">${s.ar}</div>
        <div class="surah-meta">${t(s.type)} · ${dg(s.v)} ${t('verses')}</div>
      </div>
      <div class="surah-tr">${LANG === 'ar' ? s.en : s[LANG]}</div>
    </div>`).join('');

  return `
  <div class="field" data-anno="1" style="margin-bottom:12px">
    ${IC.search}
    <input placeholder="${t('search_ph')}" id="qSearch" data-input="search" autocomplete="off">
  </div>
  <div class="chiprow" data-anno="2">
    <div class="chip is-on" data-act="filter" data-f="all">${t('filter_all')}</div>
    <div class="chip" data-act="filter" data-f="meccan">${t('filter_meccan')}</div>
    <div class="chip" data-act="filter" data-f="medinan">${t('filter_medinan')}</div>
  </div>
  <div class="card" style="padding:6px 14px" data-anno="3" id="surahList">
    ${list}
  </div>
  <div class="card card--flat" data-act="openReader" data-s="${state.lastRead.surah}" style="cursor:pointer">
    <div class="row" style="gap:12px">
      <div style="width:40px;height:40px;border-radius:12px;background:var(--primary-100);color:var(--primary);display:grid;place-items:center">${IC.bookmark}</div>
      <div class="grow"><div style="font-weight:600">${t('continue_reading')}</div>
      <div class="tiny muted">سورة ${SURAHS.find(s => s.n === state.lastRead.surah).ar} · ${t('ayah_no')} ${dg(state.lastRead.ayah)}</div></div>
      ${IC.chev}
    </div>
  </div>`;
}

/* ── قارئ السورة ── */
function screenSurah() {
  const n = state.currentSurah;
  const s = SURAHS.find(x => x.n === n);
  let verses = AYAHS[n] || [];
  const has = verses.length > 0;
  const isKursi = (n === 2 && state.lastRead.ayah === 255);

  let body;
  if (!has) {
    body = `<div class="empty">
      <div class="empty__ico">📖</div>
      <div class="empty__t">${s.ar}</div>
      <div class="empty__s">${LANG === 'ar'
        ? 'نص هذه السورة غير مضمّن في نسخة البروتوتايب (عيّنة سور فقط).<br>في التطبيق الكامل: المصحف كامل أوفلاين.'
        : 'This surah’s text is not bundled in the prototype sample.<br>The full app ships the complete Quran offline.'}</div>
    </div>`;
  } else {
    const tr = (TRANS[n] && TRANS[n][state.showTrans]) || null;
    body = `<div class="reader" style="--qfs:${state.quranFont}px">
      ${n !== 1 && n !== 9 ? `<p class="reader__basmala" data-anno="1">بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ</p>` : ''}
      ${verses.map((v, i) => {
        const num = i + 1;
        const taf = state.showTafsir && TAFSIR[n] && TAFSIR[n][num];
        return `<div style="margin-bottom:16px">
          <p class="ayah ${state.selAyah === num ? 'is-sel' : ''}" data-anno="2" data-act="selAyah" data-a="${num}">${v} <span class="ayah__mark">${dg(num)}</span></p>
          ${tr && tr[i] ? `<div class="tr-line" data-anno="4">${dg(num)}. ${esc(tr[i])}</div>` : ''}
          ${taf ? `<div class="tafsir" data-anno="4"><div class="tafsir__h">${t('tafsir')} · ${t('ayah_no')} ${dg(num)}</div>${esc(taf)}</div>` : ''}
        </div>`;
      }).join('')}
    </div>`;
  }

  const tr = (TRANS[n] && TRANS[n][state.showTrans]) || null;
  return `
  ${body}
  <div class="player" style="margin-top:14px" data-anno="5">
    <button class="player__btn" data-act="togglePlay">${state.playing ? IC.pause : IC.play}</button>
    <div class="grow">
      <div style="font-size:12px;font-weight:600">${RECITERS[state.reciter][LANG]}</div>
      <div class="tiny muted">${s.ar} · ${dg(s.v)} ${t('verses')}</div>
    </div>
    <div class="player__wave">
      ${Array.from({ length: 14 }).map((_, i) => `<i class="${state.playing && i % 3 !== 2 ? 'on' : ''}" style="animation-delay:${i * 90}ms;height:${state.playing ? 30 + (i * 37) % 70 : 25}%"></i>`).join('')}
    </div>
  </div>
  <button class="btn btn--ghost btn--block" style="margin-top:12px" data-act="sheet" data-page="readerSettings">
    ${IC.gear} ${t('reader_settings')}
  </button>`;
}

/* ── الأذكار ── */
function screenAdhkar() {
  const cats = ADHKAR.map(c => {
    const key = c.id;
    const prog = (state.adhkar[key] || []).reduce((a, b, i) => a + Math.min(b, c.items[i].count), 0);
    const total = c.items.reduce((a, b) => a + b.count, 0);
    const p = total ? Math.round(prog / total * 100) : 0;
    return `<div class="cat" data-act="openAdhkar" data-cat="${key}" data-anno="1">
      <div class="cat__ico">${c.icon}</div>
      <div class="cat__t">${t(key) || key}</div>
      <div class="cat__n">${dg(c.items.length)} ${LANG === 'ar' ? 'أذكار' : LANG === 'fr' ? 'invocations' : 'items'}</div>
      <div class="cat__bar" data-anno="2"><i style="width:${p}%"></i></div>
    </div>`;
  }).join('');

  return `
  <div class="cat-grid" data-anno="1">${cats}</div>

  <div class="sec-title">${t('dua_library')}</div>
  <div class="card" style="padding:4px 14px">
    ${DUAS.slice(0, 3).map((d, i) => `
      <div class="surah-item" data-act="openDua" data-i="${i}">
        <div class="grow">
          <div style="font-family:var(--font-quran);font-size:16px;line-height:1.9">${d.ar.length > 68 ? d.ar.slice(0, 68) + '…' : d.ar}</div>
          <div class="tiny muted" style="margin-top:3px">${d.ref}</div>
        </div>
        ${IC.chev}
      </div>`).join('')}
  </div>
  <button class="btn btn--ghost btn--block" data-act="more" data-page="duas">${t('dua_library')} (${dg(DUAS.length)})</button>

  <div class="sec-title">${t('names_title')}</div>
  <div class="card row row--between" data-act="more" data-page="names" style="cursor:pointer" data-anno="4">
    <div class="row" style="gap:12px">
      <div style="width:40px;height:40px;border-radius:12px;background:var(--accent-100);color:var(--warn);display:grid;place-items:center">${IC.grid}</div>
      <div><div style="font-weight:600">${t('names_title')}</div><div class="tiny muted">${dg(NAMES.length)} ${LANG === 'ar' ? 'اسمًا' : LANG === 'fr' ? 'noms' : 'names'}</div></div>
    </div>
    ${IC.chev}
  </div>`;
}

function screenAdhkarSet() {
  const c = ADHKAR.find(x => x.id === state.currentCat) || ADHKAR[0];
  const counts = state.adhkar[c.id] || c.items.map(() => 0);
  state.adhkar[c.id] = counts;

  const items = c.items.map((it, i) => {
    const n = counts[i], done = n >= it.count;
    const p = Math.min(100, Math.round(n / it.count * 100));
    return `<div class="dhikr ${done ? 'is-done' : ''}" data-anno="3">
      <p class="dhikr__txt">${it.text.replace(/\n/g, '<br>')}</p>
      <div class="dhikr__foot">
        <div class="counter">
          <button data-act="decDhikr" data-c="${c.id}" data-i="${i}">${IC.minus}</button>
          <span class="counter__n">${dg(n)}/${dg(it.count)}</span>
          <button data-act="incDhikr" data-c="${c.id}" data-i="${i}">${IC.plus}</button>
        </div>
        <div class="dhikr__ref">${it.ref}</div>
      </div>
      <div class="progressbar"><i style="width:${p}%"></i></div>
      ${done ? `<div class="tiny" style="color:var(--success);margin-top:8px;font-weight:600">${t('complete_all')}</div>` : ''}
    </div>`;
  }).join('');

  return `<div class="card card--flat" style="background:transparent;padding:0">
    ${items}
    <button class="btn btn--ghost btn--block" style="margin-top:6px" data-act="resetCat" data-c="${c.id}">${t('reset')}</button>
  </div>`;
}

/* ── الأدعية ── */
function screenDuas() {
  return `<div class="card card--flat" style="background:transparent;padding:0">
    ${DUAS.map((d, i) => `
      <div class="dhikr" data-act="openDua" data-i="${i}" data-anno="1" style="cursor:pointer">
        <p class="dhikr__txt">${d.ar}</p>
        ${state.showTrans !== 'none' ? `<div class="tr-line" data-anno="2" style="border-bottom:0;padding-bottom:4px">${esc(d[LANG] || d.en)}</div>` : ''}
        <div class="dhikr__ref" data-anno="3">${t('source')}: ${d.ref}</div>
      </div>`).join('')}
  </div>`;
}

/* ── أسماء الله الحسنى ── */
function screenNames() {
  return `<div class="names">
    ${NAMES.map((nm, i) => `
      <div class="name" data-act="openName" data-i="${i}" data-anno="1">
        <div class="name__idx">${dg(i + 1)}</div>
        <div class="name__ar">${nm[0]}</div>
        <div class="name__tr">${esc(nm[LANG === 'ar' ? 1 : LANG === 'fr' ? 2 : 1])}</div>
      </div>`).join('')}
  </div>
  <div class="tiny muted" style="text-align:center;margin-top:16px">${t('names_sub')}</div>`;
}

/* ── القبلة ── */
function screenQibla() {
  const c = city();
  const bearing = Calc.qiblaBearing(c.lat, c.lng);
  const dist = Calc.distanceKm(c.lat, c.lng, Calc.KAABA.lat, Calc.KAABA.lng);

  const ticks = ['N', '45', 'E', '135', 'S', '225', 'W', '315'].map((lbl, i) =>
    `<div class="compass__tick" style="transform:rotate(${i * 45}deg)"><span class="${lbl.length === 1 ? 'major' : ''}" style="transform:rotate(${-i * 45}deg)">${lbl}</span></div>`
  ).join('');

  return `
  <div class="compass-wrap" data-anno="1">
    <div class="compass">
      <div class="compass__dial" id="dial" style="transform:rotate(${-state.heading}deg)">${ticks}</div>
      <div class="compass__ring"></div>
      <div class="compass__n" title="${LANG === 'ar' ? 'اتجاه مقدمة الجهاز' : LANG === 'fr' ? 'Haut du téléphone' : 'Top of device'}"></div>
      <div class="compass__needle" id="needle" style="transform:rotate(${bearing - state.heading}deg)" data-anno="2">
        <div class="kaaba">🕋</div>
      </div>
    </div>
  </div>

  <div class="qibla-stats" style="margin-top:18px" data-anno="4">
    <div class="stat"><div class="stat__v">${dg(bearing.toFixed(1))}°</div><div class="stat__l">${t('bearing')}</div></div>
    <div class="stat"><div class="stat__v">${dg(Math.round(dist).toLocaleString('en-US'))}</div><div class="stat__l">${t('distance')} (كم)</div></div>
  </div>

  <div class="card" data-anno="3" style="margin-top:14px">
    <div class="row" style="gap:10px">
      <div style="color:var(--warn);display:grid;place-items:center">${IC.volume}</div>
      <div class="grow tiny">${t('calibrate')}</div>
    </div>
  </div>

  <div class="card">
    <div class="tiny muted" style="margin-bottom:8px">${t('simulate')}</div>
    <input type="range" class="slider" min="0" max="360" value="${Math.round(state.heading)}" data-input="heading">
  </div>

  <button class="btn btn--ghost btn--block">❓ ${t('how_without')}</button>`;
}

/* ── المزيد ── */
function screenMore() {
  const items = [
    ['tracker', IC.leaf, 'tracker_title', 'tracker_sub'],
    ['qibla', IC.qibla, 'qibla_title', 'qibla_hint'],
    ['calendar', IC.calendar, 'calendar_title', 'upcoming'],
    ['settings', IC.gear, 'settings', 'general'],
    ['duas', IC.star, 'dua_library', 'source'],
    ['names', IC.grid, 'names_title', 'names_sub'],
  ].map(([page, ico, title, sub]) => `
    <div class="setrow" data-act="more" data-page="${page}" data-anno="1">
      <div class="setrow__ico">${ico}</div>
      <div class="grow"><div class="setrow__t">${t(title)}</div><div class="setrow__s">${t(sub)}</div></div>
      <div class="setrow__v" style="color:var(--ink-40)">${IC.chev}</div>
    </div>`).join('');

  return `
  <div class="card" style="padding:6px 14px" data-anno="1">${items}</div>

  <div class="card" data-anno="2">
    <div class="row" style="gap:10px;margin-bottom:12px"><div style="color:var(--primary)">${IC.globe}</div>
      <div style="font-weight:600">${t('language')}</div></div>
    <div class="seg">
      ${['ar', 'en', 'fr'].map(l => `<button class="seg__btn ${LANG === l ? 'is-on' : ''}" data-act="lang" data-l="${l}">${l === 'ar' ? 'العربية' : l === 'en' ? 'English' : 'Français'}</button>`).join('')}
    </div>
    <div class="row" style="gap:10px;margin:16px 0 12px"><div style="color:var(--primary)">${LANG === 'ar' ? IC.moon : IC.sun}</div>
      <div style="font-weight:600">${t('theme')}</div></div>
    <div class="seg">
      ${['light', 'dark'].map(th => `<button class="seg__btn ${state.theme === th ? 'is-on' : ''}" data-act="theme" data-t="${th}">${t('theme_' + th)}</button>`).join('')}
    </div>
  </div>

  <div class="setrow" data-act="sheet" data-page="about" data-anno="3" style="padding:14px;border:1px solid var(--border);border-radius:var(--r-card);margin-bottom:12px;background:var(--surface)">
    <div class="setrow__ico">${IC.star}</div>
    <div class="grow"><div class="setrow__t">${t('about')}</div><div class="setrow__s">${t('tagline')}</div></div>
    <div class="setrow__v" style="color:var(--ink-40)">${IC.chev}</div>
  </div>

  <button class="btn btn--ghost btn--block" data-act="sheet" data-page="sources">📜 ${t('sources')}</button>
  <div class="tiny muted" style="text-align:center;margin-top:16px;line-height:1.9">
    ${t('privacy')}: ${LANG === 'ar' ? 'لا إعلانات · لا حسابات · لا جمع بيانات' : LANG === 'fr' ? 'Pas de pub · Pas de compte · Aucune collecte' : 'No ads · No accounts · No tracking'}
  </div>`;
}

/* ── المتتبع «أثري» ── */
function screenTracker() {
  seedTracker();
  const base = new Date(); base.setHours(0, 0, 0, 0);
  const cells = [];
  for (let i = 29; i >= 0; i--) {
    const d = new Date(base.getTime() - i * 86400000);
    const rec = state.tracker[keyOf(d)] || {};
    const done = HABITS.filter(h => rec[h.id]).length;
    const lvl = done === 0 ? '' : done <= 2 ? 'l1' : done <= 4 ? 'l2' : done <= 6 ? 'l3' : 'l4';
    cells.push(`<div class="cell ${lvl}" title="${d.toDateString()} · ${done}"></div>`);
  }
  const todayRec = state.tracker[keyOf(new Date())] || {};
  const habits = HABITS.map(h => `
    <div class="habit">
      <div class="check ${todayRec[h.id] ? 'is-on' : ''}" data-act="toggleHabit" data-h="${h.id}">${IC.check}</div>
      <div style="font-size:19px">${h.icon}</div>
      <div class="grow"><div style="font-size:14px;font-weight:500">${h[LANG] || h.ar}</div></div>
      ${IC.chev}
    </div>`).join('');

  const doneToday = HABITS.filter(h => todayRec[h.id]).length;

  return `
  <div class="streak" data-anno="1">
    <div style="font-size:26px">🔥</div>
    <div class="grow">
      <div class="streak__n">${dg(state.streak)}</div>
      <div class="tiny muted">${t('streak_days')}</div>
    </div>
    <div style="text-align:end">
      <div style="font-size:19px;font-weight:700">${dg(doneToday)}/${dg(HABITS.length)}</div>
      <div class="tiny muted">${t('today')}</div>
    </div>
  </div>

  <div class="sec-title">${t('last_30')}</div>
  <div class="card" data-anno="2"><div class="grid30">${cells.join('')}</div>
    <div class="row row--between tiny muted" style="margin-top:10px">
      <span>${LANG === 'ar' ? 'أقل' : LANG === 'fr' ? 'Moins' : 'Less'}</span>
      <span>${LANG === 'ar' ? 'أكثر' : LANG === 'fr' ? 'Plus' : 'More'}</span>
    </div>
  </div>

  <div class="sec-title">${t('habits')}</div>
  <div class="card" style="padding:4px 14px" data-anno="3">${habits}</div>
  <div class="card card--flat" data-anno="4">
    <div class="tiny muted" style="line-height:1.9">
      ${LANG === 'ar'
        ? '💡 في الإصدار الكامل: ملخص أسبوعي، أهداف مخصصة، ويوم راحة واحد أسبوعيًا يحافظ على السلسلة.'
        : '💡 In the full app: weekly summary, custom goals, and one weekly rest day that preserves the streak.'}
    </div>
  </div>`;
}

/* ── السبحة ── */
const TASBIH_PRESETS = [
  { ar: 'سُبۡحَانَ ٱللَّهِ', en: 'Subhan Allah', fr: 'Subhan Allah' },
  { ar: 'ٱلۡحَمۡدُ لِلَّهِ', en: 'Alhamdulillah', fr: 'Alhamdulillah' },
  { ar: 'ٱللَّهُ أَكۡبَرُ', en: 'Allahu Akbar', fr: 'Allahu Akbar' },
  { ar: 'لَآ إِلَٰهَ إِلَّا ٱللَّهُ', en: 'La ilaha illa Allah', fr: 'La ilaha illa Allah' },
  { ar: 'أَسۡتَغۡفِرُ ٱللَّهَ', en: 'Astaghfirullah', fr: 'Astaghfirullah' },
  { ar: 'ٱللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ', en: 'Salawat upon the Prophet', fr: 'Salawat sur le Prophète' },
];

function screenTasbih() {
  const st = state.tasbih;
  const p = Math.min(100, st.n / st.target * 100);
  const phrase = TASBIH_PRESETS[st.preset];
  return `
  <div class="chiprow">
    ${TASBIH_PRESETS.map((x, i) => `<div class="chip ${i === st.preset ? 'is-on' : ''}" data-act="preset" data-i="${i}">${x.ar}</div>`).join('')}
  </div>

  <div class="tasbih" style="margin-top:10px">
    <div class="tasbih__dial" style="--p:${p}" data-act="tap" data-anno="1">
      <div class="tasbih__inner">
        <div class="tasbih__n">${dg(st.n)}</div>
        <div class="tasbih__t">${t('of')} ${dg(st.target)}</div>
      </div>
    </div>
    <div class="tasbih__phrase">${phrase.ar}</div>
  </div>

  <div class="chiprow" style="justify-content:center" data-anno="2">
    ${[33, 66, 99, 100, 1000].map(x => `<div class="chip ${st.target === x ? 'is-on' : ''}" data-act="target" data-v="${x}">${dg(x)}</div>`).join('')}
  </div>

  <div class="row" style="gap:12px;margin-top:8px">
    <button class="btn btn--ghost grow" data-act="tap">${t('tap_to_count')}</button>
    <button class="btn btn--icon btn--ghost" data-act="resetTasbih">${IC.minus}</button>
  </div>
  <div class="tiny muted" style="text-align:center;margin-top:12px">
    ${LANG === 'ar' ? 'في الإصدار الكامل: اهتزاز عند الإتمام، أزرار الصوت، وسجل جلسات.' : 'In the full app: haptic feedback, volume-key counting, and session history.'}
  </div>`;
}

/* ── التقويم ── */
function screenCalendar() {
  const d = new Date();
  const h = Calc.toHijri(d, 0);
  const hm = Calc.HIJRI_MONTHS[LANG] || Calc.HIJRI_MONTHS.ar;
  const evs = EVENTS.map(e => {
    const days = daysToHijri(e.m, e.d);
    return `<div class="surah-item">
      <div class="grow"><div style="font-weight:600">${e[LANG] || e.ar}</div>
        <div class="tiny muted">${dg(e.d)} ${hm[e.m - 1]} ${dg(h.year + (e.m < h.month || (e.m === h.month && e.d < h.day) ? 1 : 0))}</div></div>
      <div class="chip" style="background:var(--accent-100);color:var(--warn);border-color:transparent">${dg(days)} ${LANG === 'ar' ? 'يوم' : LANG === 'fr' ? 'j' : 'd'}</div>
    </div>`;
  }).sort((a, b) => 0).join('');

  return `
  <div class="card" style="text-align:center;padding:22px 16px">
    <div class="tiny muted">${t('hijri')}</div>
    <div style="font-family:var(--font-display);font-size:27px;font-weight:700;margin:6px 0 2px">
      ${dg(h.day)} ${hm[h.month - 1]} ${dg(h.year)}
    </div>
    <div class="tiny muted">${d.toLocaleDateString(LANG === 'ar' ? 'ar-EG' : LANG === 'fr' ? 'fr-FR' : 'en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</div>
  </div>

  <div class="sec-title">${t('upcoming')}</div>
  <div class="card" style="padding:4px 14px">${evs}</div>

  <div class="card card--flat" data-anno="3">
    <div class="tiny muted" style="line-height:1.9">
      ${LANG === 'ar'
        ? '⚙️ التاريخ الهجري محسوب بالخوارزمية الجدولية. التطبيق يوفّر إزاحة ±يوم/يومين لمطابقة رؤية بلدك.'
        : '⚙️ Hijri dates use the tabular algorithm. The app offers a ±1/±2 day offset to match your country’s moonsighting.'}
    </div>
  </div>`;
}

/* ── الإعدادات ── */
function screenSettings() {
  return `
  <div class="card" style="padding:4px 14px" data-anno="1">
    <div class="setrow" data-act="sheet" data-page="pickLang">
      <div class="setrow__ico">${IC.globe}</div>
      <div class="grow"><div class="setrow__t">${t('language')}</div></div>
      <div class="setrow__v">${LANG === 'ar' ? 'العربية' : LANG === 'en' ? 'English' : 'Français'}</div>
    </div>
    <div class="setrow" data-act="sheet" data-page="pickTheme">
      <div class="setrow__ico">${IC.moon}</div>
      <div class="grow"><div class="setrow__t">${t('theme')}</div></div>
      <div class="setrow__v">${t('theme_' + (state.theme === 'auto' ? 'auto' : state.theme))}</div>
    </div>
    <div class="setrow" data-act="sheet" data-page="pickSize">
      <div class="setrow__ico">${IC.quran}</div>
      <div class="grow"><div class="setrow__t">${t('text_size')}</div></div>
      <div class="setrow__v">${t('size_normal')}</div>
    </div>
  </div>

  <div class="card" style="padding:4px 14px" data-anno="2">
    <div class="setrow" data-act="sheet" data-page="prayerSettings">
      <div class="setrow__ico">${IC.clock}</div>
      <div class="grow"><div class="setrow__t">${t('prayer_times')}</div>
        <div class="setrow__s">${Calc.METHODS[state.method][LANG]}</div></div>
      <div class="setrow__v" style="color:var(--ink-40)">${IC.chev}</div>
    </div>
    <div class="setrow">
      <div class="setrow__ico">${IC.volume}</div>
      <div class="grow"><div class="setrow__t">${t('adhan_sound')}</div></div>
      <div class="miniswitch on"></div>
    </div>
    <div class="setrow">
      <div class="setrow__ico">${IC.bookmark}</div>
      <div class="grow"><div class="setrow__t">${t('reminder_before')}</div></div>
      <div class="setrow__v">${dg(10)} ${LANG === 'ar' ? 'دقيقة' : LANG === 'fr' ? 'min' : 'min'}</div>
    </div>
  </div>

  <div class="card" style="padding:4px 14px">
    <div class="setrow" data-act="sheet" data-page="sources">
      <div class="setrow__ico">📜</div>
      <div class="grow"><div class="setrow__t">${t('sources')}</div></div>
      <div class="setrow__v" style="color:var(--ink-40)">${IC.chev}</div>
    </div>
    <div class="setrow">
      <div class="setrow__ico">🔒</div>
      <div class="grow"><div class="setrow__t">${t('privacy')}</div></div>
      <div class="setrow__v" style="color:var(--ink-40)">${IC.chev}</div>
    </div>
  </div>`;
}

/* ── الورقة السفلية ── */
function sheetBody(page) {
  switch (page) {
    case 'readerSettings': return {
      title: t('reader_settings'), sub: t('font_size'),
      body: `
        <div class="tiny muted" style="margin-bottom:8px">${t('font_size')} — <span id="fontLbl">${dg(state.quranFont)}</span>px</div>
        <input type="range" class="slider" min="16" max="40" value="${state.quranFont}" data-input="font">
        <hr class="sep">
        <div class="tiny muted" style="margin-bottom:8px">${t('translation')}</div>
        <div class="chiprow">
          ${['none', 'en', 'fr'].map(l => `<div class="chip ${state.showTrans === l ? 'is-on' : ''}" data-act="setTrans" data-v="${l}">${l === 'none' ? t('none') : l === 'en' ? 'English' : 'Français'}</div>`).join('')}
        </div>
        <div class="row row--between" style="margin-top:10px">
          <div><div style="font-weight:600;font-size:14px">${t('tafsir')}</div><div class="tiny muted">${LANG === 'ar' ? 'التفسير المختصر (أوفلاين)' : 'Concise tafsir (offline)'}</div></div>
          <div class="miniswitch ${state.showTafsir ? 'on' : ''}" data-act="setTafsir"></div>
        </div>
        <hr class="sep">
        <div class="tiny muted" style="margin-bottom:8px">${t('reciter')}</div>
        <div style="max-height:180px;overflow-y:auto">
          ${RECITERS.map((r, i) => `<div class="setrow" data-act="setReciter" data-i="${i}">
            <div class="grow"><div class="setrow__t">${r[LANG]}</div></div>
            ${i === state.reciter ? `<div style="color:var(--primary)">${IC.check}</div>` : ''}
          </div>`).join('')}
        </div>
        <hr class="sep">
        <button class="btn btn--ghost btn--block">${IC.download} ${t('download_offline')}</button>`
    };
    case 'prayerSettings': return {
      title: t('calc_settings'), sub: cityName(),
      body: `
        <div class="tiny muted" style="margin-bottom:8px">${t('calc_settings')}</div>
        ${Object.entries(Calc.METHODS).map(([k, m]) => `
          <div class="setrow" data-act="setMethod" data-v="${k}">
            <div class="grow"><div class="setrow__t">${m[LANG]}</div>
              <div class="setrow__s">فجر ${dg(m.fajr)}° · ${m.ishaInterval ? 'عشاء بعد المغرب ' + dg(m.ishaInterval) + '′' : 'عشاء ' + dg(m.isha) + '°'}</div></div>
            ${state.method === k ? `<div style="color:var(--primary)">${IC.check}</div>` : ''}
          </div>`).join('')}
        <hr class="sep">
        <div class="tiny muted" style="margin-bottom:8px">${t('madhab_label')}</div>
        <div class="chiprow">
          ${['shafi', 'hanafi'].map(m => `<div class="chip ${state.madhab === m ? 'is-on' : ''}" data-act="setMadhab" data-v="${m}">${t(m)}</div>`).join('')}
        </div>`
    };
    case 'about': return {
      title: t('about_title'), sub: t('tagline'),
      body: `<p style="font-size:14px;line-height:2;color:var(--ink-70);margin:0">${t('about_body')}</p>
        <hr class="sep">
        <div class="setrow"><div class="grow"><div class="setrow__t">${LANG === 'ar' ? 'الإصدار' : LANG === 'fr' ? 'Version' : 'Version'}</div></div><div class="setrow__v">0.1.0 (${LANG === 'ar' ? 'بروتوتايب' : 'prototype'})</div></div>
        <div class="setrow"><div class="grow"><div class="setrow__t">${LANG === 'ar' ? 'الإعلانات' : LANG === 'fr' ? 'Publicités' : 'Ads'}</div></div><div class="setrow__v" style="color:var(--success)">${LANG === 'ar' ? 'لا يوجد' : LANG === 'fr' ? 'Aucune' : 'None'}</div></div>
        <div class="setrow"><div class="grow"><div class="setrow__t">${LANG === 'ar' ? 'العمل أوفلاين' : LANG === 'fr' ? 'Hors ligne' : 'Offline'}</div></div><div class="setrow__v" style="color:var(--success)">${dg(100)}%</div></div>`
    };
    case 'sources': return {
      title: t('sources'), sub: 'Provenance & licenses',
      body: `
        <div class="tiny" style="line-height:2;color:var(--ink-70)">
          <b style="color:var(--ink)">${LANG === 'ar' ? 'النص القرآني' : 'Quran text'}</b><br>
          ${LANG === 'ar'
            ? 'نسخة عثمانية برواية حفص. يُنصح بمصدر يوثّق ترخيص كل إصدار (مثل مشروع quran-json الموثّق)، وممنوع استخدام واجهات مقيدة تجاريًا مثل Quran.com v4.'
            : 'Uthmani script (Hafs). Prefer a source that documents licensing per edition (e.g. the quran-json project). APIs restricted to non-commercial use (Quran.com v4) must not be used.'}
          <hr class="sep">
          <b style="color:var(--ink)">${LANG === 'ar' ? 'التلاوات' : 'Audio'}</b><br>
          MP3Quran.net · Islamic Network CDN — ${LANG === 'ar' ? 'إعادة النشر مسموحة مع ذكر المصدر (راجع الشروط لكل قارئ).' : 'redistribution allowed with attribution (verify per reciter).'}
          <hr class="sep">
          <b style="color:var(--ink)">${LANG === 'ar' ? 'الأذكار' : 'Adhkar'}</b><br>
          ${LANG === 'ar' ? 'حصن المسلم (سعيد القحطاني) + كتب الحديث الصحيحة، مع التخريج الكامل.' : 'Hisn al-Muslim (Sa’eed Al-Qahtani) + authenticated hadith collections, fully referenced.'}
          <hr class="sep">
          <b style="color:var(--ink)">${LANG === 'ar' ? 'المواقيت والقبلة' : 'Times & Qibla'}</b><br>
          ${LANG === 'ar' ? 'حسابات فلكية محلية على الجهاز — لا مصدر بيانات خارجي.' : 'Computed astronomically on-device — no external data source.'}
        </div>`
    };
    case 'pickLang': return {
      title: t('language'), sub: '',
      body: ['ar', 'en', 'fr'].map(l => `<div class="setrow" data-act="lang" data-l="${l}">
        <div class="grow"><div class="setrow__t">${l === 'ar' ? 'العربية' : l === 'en' ? 'English' : 'Français'}</div></div>
        ${LANG === l ? `<div style="color:var(--primary)">${IC.check}</div>` : ''}</div>`).join('')
    };
    case 'pickTheme': return {
      title: t('theme'), sub: '',
      body: ['light', 'dark', 'auto'].map(x => `<div class="setrow" data-act="theme" data-t="${x}">
        <div class="grow"><div class="setrow__t">${t('theme_' + x)}</div></div>
        ${state.theme === x ? `<div style="color:var(--primary)">${IC.check}</div>` : ''}</div>`).join('')
    };
    case 'pickSize': return {
      title: t('text_size'), sub: '',
      body: ['small', 'normal', 'large', 'xlarge'].map(x => `<div class="setrow">
        <div class="grow"><div class="setrow__t">${t('size_' + x)}</div></div>
        ${x === 'normal' ? `<div style="color:var(--primary)">${IC.check}</div>` : ''}</div>`).join('')
    };
    case 'openDua': {
      const d = DUAS[state.currentDua] || DUAS[0];
      return { title: t('dua_library'), sub: d.ref, body: `
        <p class="dhikr__txt" style="font-size:20px">${d.ar}</p>
        <div class="tr-line" style="border:0">${esc(d[LANG] || d.en)}</div>
        <div class="row" style="gap:10px;margin-top:8px">
          <button class="btn btn--soft grow">${IC.bookmark} ${t('bookmark')}</button>
          <button class="btn btn--ghost grow">${IC.share} ${t('share')}</button>
        </div>` };
    }
    case 'openName': {
      const nm = NAMES[state.currentName] || NAMES[0];
      return { title: nm[0], sub: `${t('names_title')} · ${dg((state.currentName || 0) + 1)}`, body: `
        <div style="text-align:center;padding:18px 0">
          <div style="font-family:var(--font-display);font-size:42px;font-weight:700;line-height:1.5">${nm[0]}</div>
          <div style="font-size:15px;color:var(--ink-70);margin-top:14px">${esc(nm[LANG === 'fr' ? 2 : 1])}</div>
        </div>
        <button class="btn btn--soft btn--block">${IC.volume} ${LANG === 'ar' ? 'استماع' : LANG === 'fr' ? 'Écouter' : 'Listen'}</button>` };
    }
  }
  return { title: '', sub: '', body: '' };
}

/* ═══════════════════════════════════════════════════════════════
   التنقل والعرض
   ═══════════════════════════════════════════════════════════════ */
const TABS = [
  { id: 'home', icon: IC.home, label: 'tab_home' },
  { id: 'quran', icon: IC.quran, label: 'tab_quran' },
  { id: 'adhkar', icon: IC.adhkar, label: 'tab_adhkar' },
  { id: 'tasbih', icon: IC.tasbih, label: 'tab_tasbih' },
  { id: 'more', icon: IC.more, label: 'tab_more' },
];

const SCREENS = {
  home: screenHome, quran: screenQuran, adhkar: screenAdhkar, qibla: screenQibla,
  more: screenMore, prayer: screenPrayer, surah: screenSurah, adhkarSet: screenAdhkarSet,
  duas: screenDuas, names: screenNames, tracker: screenTracker, tasbih: screenTasbih,
  calendar: screenCalendar, settings: screenSettings,
};

function currentScreen() { return state.stack[state.stack.length - 1] || 'home'; }

function render() {
  const scr = currentScreen();
  const c = city();

  /* appbar */
  let title = t('app_name'), sub = '', back = false;
  if (scr === 'home') {
    const h = new Date().getHours();
    const g = h < 12 ? 'hi_morning' : h < 17 ? 'hi_afternoon' : h < 21 ? 'hi_evening' : 'hi_night';
    title = t(g); sub = cityName();
  } else if (scr === 'surah') {
    const s = SURAHS.find(x => x.n === state.currentSurah);
    title = 'سورة ' + s.ar; sub = `${t(s.type)} · ${dg(s.v)} ${t('verses')}`; back = true;
  } else if (scr === 'adhkarSet') {
    title = t(state.currentCat) || state.currentCat; back = true;
  } else if (['prayer', 'quran', 'adhkar', 'qibla', 'more', 'duas', 'names', 'tracker', 'tasbih', 'calendar', 'settings'].includes(scr)) {
    const map = { prayer: 'prayer_times', quran: 'quran_index', adhkar: 'adhkar_cat', qibla: 'qibla_title', more: 'more_title', duas: 'dua_library', names: 'names_title', tracker: 'tracker_title', tasbih: 'tasbih_title', calendar: 'calendar_title', settings: 'settings_title' };
    title = t(map[scr]);
    back = ['prayer', 'duas', 'names', 'tracker', 'tasbih', 'calendar', 'settings'].includes(scr);
  }

  el('appbar').innerHTML = `
    ${back ? `<button class="iconbtn" data-act="back" style="margin-inline-end:2px">${IC.back}</button>` : ''}
    <div class="grow" style="min-width:0">
      <h2 class="appbar__title">${title}</h2>
      ${sub ? `<p class="appbar__sub">${sub}</p>` : ''}
    </div>
    ${scr === 'surah' ? `<button class="iconbtn" data-act="sheet" data-page="readerSettings">${IC.gear}</button>` : ''}
    ${scr === 'quran' ? `<button class="iconbtn">${IC.search}</button>` : ''}
    <button class="iconbtn" data-act="sheet" data-page="pickLang">${IC.globe}</button>`;

  /* content */
  el('content').innerHTML = (SCREENS[scr] || screenHome)();
  el('content').scrollTop = 0;

  /* tabbar */
  el('tabbar').innerHTML = TABS.map(tb =>
    `<button class="tab ${state.tab === tb.id ? 'is-active' : ''}" data-tab="${tb.id}">
      ${tb.icon}<span>${t(tb.label)}</span></button>`).join('');

  renderNotes(scr);
  updateStateDump(scr);
  tick();
}

/* ── الورقة السفلية ── */
function openSheet(page) {
  const s = sheetBody(page);
  el('sheetHost').classList.add('is-open');
  el('sheetHost').innerHTML = `
    <div class="sheet-scrim" data-act="closeSheet"></div>
    <div class="sheet">
      <div class="sheet__grab"></div>
      <h3 class="sheet__title">${s.title}</h3>
      ${s.sub ? `<p class="sheet__sub">${s.sub}</p>` : ''}
      ${s.body}
    </div>`;
}
function closeSheet() {
  el('sheetHost').classList.remove('is-open');
  setTimeout(() => { if (!el('sheetHost').classList.contains('is-open')) el('sheetHost').innerHTML = ''; }, 340);
}

/* ── العدّاد الحيّ ── */
function tick() {
  const PT = getTimes(), now = cityNowHours();
  let next = PRAYER_ORDER[0], minDiff = 99;
  PRAYER_ORDER.forEach(k => { let d = PT[k] - now; if (d <= 0) d += 24; if (d < minDiff) { minDiff = d; next = k; } });
  const cd = el('cd');
  if (cd) {
    const total = Math.floor(minDiff * 3600);
    const hh = Math.floor(total / 3600), mm = Math.floor(total % 3600 / 60), ss = total % 60;
    cd.textContent = `${pad2(hh)}:${pad2(mm)}:${pad2(ss)}`
      .replace(/[0-9]/g, d => (LANG === 'ar' && state.arabicDigits) ? AR_DIGITS[+d] : d);
  }
  const st = el('statusTime');
  if (st) st.textContent = fmtTime(now).replace(/\s?(ص|م|AM|PM)$/, '');
}

/* ── ملاحظات التصميم ── */
function renderNotes(scr) {
  const notes = NOTES[scr] || [];
  el('notesList').innerHTML = notes.map((n, i) =>
    `<li class="note" data-note="${i + 1}"><span class="note__n">${i + 1}</span><span class="note__t">${n}</span></li>`).join('')
    || `<li class="note"><span class="note__t">${LANG === 'ar' ? 'لا توجد ملاحظات لهذه الشاشة.' : 'No notes for this screen.'}</span></li>`;

  const layer = el('annoLayer');
  layer.innerHTML = '';
  if (!state.anno) return;
  const screenRect = document.querySelector('.phone__screen').getBoundingClientRect();
  const content = el('content');
  const isRtl = document.documentElement.dir === 'rtl';
  content.querySelectorAll('[data-anno]').forEach(node => {
    const r = node.getBoundingClientRect();
    if (r.bottom < screenRect.top || r.top > screenRect.bottom) return;
    const pin = document.createElement('div');
    pin.className = 'pin';
    pin.textContent = node.dataset.anno;
    pin.style.top = (r.top - screenRect.top + 12) + 'px';
    pin.style.left = ((isRtl ? r.left - screenRect.left + 11 : r.right - screenRect.left - 11)) + 'px';
    pin.innerHTML = node.dataset.anno.replace(/[0-9]/g, d => AR_DIGITS[+d]);
    pin.dataset.pin = node.dataset.anno;
    pin.onclick = () => hot(node.dataset.anno);
    layer.appendChild(pin);
  });
}
function hot(n) {
  document.querySelectorAll('.note').forEach(x => x.classList.toggle('is-hot', x.dataset.note === n));
  document.querySelectorAll('.pin').forEach(x => x.classList.toggle('is-hot', x.dataset.pin === n));
}

/* ── المتتبع ── */
const keyOf = d => `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
function seedTracker() {
  const base = new Date(); base.setHours(0, 0, 0, 0);
  for (let i = 0; i < 30; i++) {
    const d = new Date(base.getTime() - i * 86400000);
    const k = keyOf(d);
    if (state.tracker[k]) continue;
    const o = {};
    HABITS.forEach((h, idx) => {
      const s = (d.getDate() * 7 + idx * 13 + d.getMonth() * 3) % 10;
      o[h.id] = s > (idx === 0 ? 6 : idx > 4 ? 6 : 3);
    });
    state.tracker[k] = o;
  }
}
/* تحويل هجري → ميلادي تقريبي لحساب الأيام المتبقية */
function daysToHijri(m, d) {
  const h = Calc.toHijri(new Date(), 0);
  let y = h.year;
  const jdToday = Math.floor((new Date()).getTime() / 86400000) + 2440587.5;
  const hijriToJd = (yy, mm, dd) =>
    Math.floor((11 * yy + 3) / 30) + 354 * yy + 30 * mm - Math.floor((mm - 1) / 2) + dd + 1948440 - 385;
  let diff = hijriToJd(y, m, d) - jdToday;
  if (diff < 0) diff = hijriToJd(y + 1, m, d) - jdToday;
  return diff;
}

/* ── الأحداث ── */
document.addEventListener('click', e => {
  const tab = e.target.closest('[data-tab]');
  if (tab) {
    const id = tab.dataset.tab;
    state.tab = id; state.stack = [id];
    if (id === 'quran' && !state.currentSurah) state.currentSurah = state.lastRead.surah;
    return render();
  }
  const node = e.target.closest('[data-act]');
  if (!node) return;
  const act = node.dataset.act;

  switch (act) {
    case 'go': { state.tab = node.dataset.tab; state.stack = [node.dataset.tab]; break; }
    case 'more': { state.stack.push(node.dataset.page); break; }
    case 'back': { if (state.stack.length > 1) state.stack.pop(); break; }
    case 'openReader': { state.currentSurah = +node.dataset.s; state.stack.push('surah'); break; }
    case 'openAdhkar': { state.currentCat = node.dataset.cat; state.stack.push('adhkarSet'); break; }
    case 'openDua': { state.currentDua = +node.dataset.i; openSheet('openDua'); return; }
    case 'openName': { state.currentName = +node.dataset.i; openSheet('openName'); return; }
    case 'sheet': openSheet(node.dataset.page); return;
    case 'closeSheet': closeSheet(); return;
    case 'lang': setLang(node.dataset.l); return;
    case 'theme': setTheme(node.dataset.t); break;
    case 'setMethod': state.method = node.dataset.v; render(); openSheet('prayerSettings'); return;
    case 'setMadhab': state.madhab = node.dataset.v; render(); openSheet('prayerSettings'); return;
    case 'setTrans': state.showTrans = node.dataset.v; break;
    case 'setTafsir': state.showTafsir = !state.showTafsir; break;
    case 'setReciter': state.reciter = +node.dataset.i; break;
    case 'togglePlay': state.playing = !state.playing; break;
    case 'selAyah': state.selAyah = state.selAyah === +node.dataset.a ? null : +node.dataset.a; break;
    case 'incDhikr': {
      const c = ADHKAR.find(x => x.id === node.dataset.c), i = +node.dataset.i;
      state.adhkar[c.id] = state.adhkar[c.id] || c.items.map(() => 0);
      state.adhkar[c.id][i] = Math.min(c.items[i].count, state.adhkar[c.id][i] + 1); break;
    }
    case 'decDhikr': {
      const c = ADHKAR.find(x => x.id === node.dataset.c), i = +node.dataset.i;
      state.adhkar[c.id] = state.adhkar[c.id] || c.items.map(() => 0);
      state.adhkar[c.id][i] = Math.max(0, state.adhkar[c.id][i] - 1); break;
    }
    case 'resetCat': state.adhkar[node.dataset.c] = []; break;
    case 'tap': state.tasbih.n++; break;
    case 'resetTasbih': state.tasbih.n = 0; break;
    case 'target': state.tasbih.target = +node.dataset.v; break;
    case 'preset': state.tasbih.preset = +node.dataset.i; break;
    case 'toggleHabit': {
      const k = keyOf(new Date());
      state.tracker[k] = state.tracker[k] || {};
      state.tracker[k][node.dataset.h] = !state.tracker[k][node.dataset.h]; break;
    }
    case 'filter': filterSurahsType(node.dataset.f); return;
  }
  render();
});

function filterSurahs(q) {
  const list = el('surahList'); if (!list) return;
  const norm = s => s.replace(/[\u064B-\u0652\u0670\u0640]/g, '')
    .replace(/[أإآٱ]/g, 'ا').replace(/ى/g, 'ي').replace(/ة/g, 'ه').toLowerCase();
  const nq = norm(q || '');
  list.innerHTML = SURAHS.filter(s =>
    !nq || norm(s.ar).includes(nq) || s.en.toLowerCase().includes(nq.toLowerCase()) ||
    s.fr.toLowerCase().includes(nq.toLowerCase()) || String(s.n) === q
  ).map(s => `
    <div class="surah-item" data-act="openReader" data-s="${s.n}">
      <div class="surah-num">${dg(s.n)}</div>
      <div class="grow"><div class="surah-name">${s.ar}</div>
        <div class="surah-meta">${t(s.type)} · ${dg(s.v)} ${t('verses')}</div></div>
      <div class="surah-tr">${LANG === 'ar' ? s.en : s[LANG]}</div>
    </div>`).join('') || `<div class="empty"><div class="empty__ico">🔍</div><div class="empty__t">${LANG === 'ar' ? 'لا نتائج' : 'No results'}</div></div>`;
}
function filterSurahsType(f) {
  document.querySelectorAll('[data-act="filter"]').forEach(c => c.classList.toggle('is-on', c.dataset.f === f));
  const list = el('surahList'); if (!list) return;
  list.innerHTML = SURAHS.filter(s => f === 'all' || s.type === f).map(s => `
    <div class="surah-item" data-act="openReader" data-s="${s.n}">
      <div class="surah-num">${dg(s.n)}</div>
      <div class="grow"><div class="surah-name">${s.ar}</div>
        <div class="surah-meta">${t(s.type)} · ${dg(s.v)} ${t('verses')}</div></div>
      <div class="surah-tr">${LANG === 'ar' ? s.en : s[LANG]}</div>
    </div>`).join('');
}

/* ── أحداث الإدخال (مفوّضة) ── */
document.addEventListener('input', e => {
  const n = e.target, kind = n && n.dataset ? n.dataset.input : null;
  if (!kind) return;
  if (kind === 'search') return filterSurahs(n.value);
  if (kind === 'heading') return setHeading(+n.value);
  if (kind === 'font') {
    state.quranFont = +n.value;
    const r = document.querySelector('.reader');
    if (r) r.style.setProperty('--qfs', n.value + 'px');
    const lbl = el('fontLbl'); if (lbl) lbl.textContent = dg(n.value);
  }
});

/* ── الموالفات الخارجية ── */
function setLang(l) {
  LANG = state.lang = l;
  document.documentElement.lang = l;
  document.documentElement.dir = l === 'ar' ? 'rtl' : 'ltr';
  document.querySelectorAll('[data-lang]').forEach(b => b.classList.toggle('is-on', b.dataset.lang === l));
  document.title = `${t('app_name')} — ${LANG === 'ar' ? 'بروتوتايب تفاعلي' : 'Interactive prototype'}`;
  el('sideIntro').textContent = LANG === 'ar'
    ? 'نموذج تفاعلي لتطبيق إسلامي شامل (صلاة · قرآن · أذكار · قبلة · متتبع). اضغط على التبويبات والبطاقات — كل حاجة قابلة للنقر.'
    : LANG === 'fr'
      ? 'Prototype interactif d’une application islamique complète (prière · Coran · invocations · Qibla · suivi). Tout est cliquable.'
      : 'An interactive prototype of an all-in-one Islamic app (prayer · Quran · adhkar · qibla · tracker). Everything is clickable.';
  render();
  if (el('sheetHost').classList.contains('is-open')) closeSheet();
}
function setTheme(th) {
  state.theme = th;
  const dark = th === 'dark' || (th === 'auto' && window.matchMedia('(prefers-color-scheme: dark)').matches);
  el('phone').className = 'phone ' + (dark ? 'theme-dark' : 'theme-light');
  document.querySelectorAll('[data-theme]').forEach(b => b.classList.toggle('is-on', b.dataset.theme === th));
  setTimeout(renderNotes, 60);
}
function setFont(v) { state.quranFont = v; render(); }
function setHeading(v) {
  state.heading = v;
  const c = city(), bearing = Calc.qiblaBearing(c.lat, c.lng);
  const needle = el('needle'), dial = el('dial');
  if (needle) needle.style.transform = `rotate(${bearing - v}deg)`;
  if (dial) dial.style.transform = `rotate(${-v}deg)`;
}

/* ── البوصلة (مستشعر حقيقي إن وُجد) ── */
function initCompass() {
  let got = false;
  const handler = e => {
    let h = null;
    if (typeof e.webkitCompassHeading === 'number') h = e.webkitCompassHeading;
    else if (e.alpha != null) h = (360 - e.alpha) % 360;
    if (h == null) return;
    got = true; state.hasSensor = true;
    state.heading = h;
    setHeading(h);
  };
  window.addEventListener('deviceorientation', handler, true);
  setTimeout(() => { if (!got) state.hasSensor = false; }, 2000);
}

/* ── شريط الحالة ── */
function updateStateDump(scr) {
  el('stateDump').textContent =
    `screen   : ${scr}\nlang     : ${LANG}\ntheme    : ${state.theme}\ncity     : ${state.city}\nmethod   : ${state.method}\nmadhab   : ${state.madhab}\nquranUI  : ${state.quranFont}px · tr=${state.showTrans}\nstack    : ${state.stack.join(' › ')}`;
}

/* ── الإقلاع ── */
function init() {
  /* قوائم التحكم */
  el('citySelect').innerHTML = CITIES.map(c => `<option value="${c.id}">${c.ar}</option>`).join('');
  el('citySelect').value = state.city;
  el('methodSelect').innerHTML = Object.entries(Calc.METHODS)
    .map(([k, m]) => `<option value="${k}">${m.ar}</option>`).join('');
  el('methodSelect').value = state.method;
  el('citySelect').onchange = e => { state.city = e.target.value; render(); };
  el('methodSelect').onchange = e => { state.method = e.target.value; render(); };
  el('annoToggle').onchange = e => { state.anno = e.target.checked; renderNotes(currentScreen()); };

  document.querySelectorAll('[data-lang]').forEach(b => b.onclick = () => setLang(b.dataset.lang));
  document.querySelectorAll('[data-theme]').forEach(b => b.onclick = () => setTheme(b.dataset.theme));

  state.currentSurah = state.lastRead.surah;
  state.stack = ['home'];
  setLang('ar');
  setTheme('light');
  initCompass();

  setInterval(tick, 1000);
  window.addEventListener('resize', () => renderNotes(currentScreen()));
  el('content').addEventListener('scroll', () => {
    clearTimeout(window.__st);
    window.__st = setTimeout(() => renderNotes(currentScreen()), 80);
  }, { passive: true });
}
document.addEventListener('DOMContentLoaded', init);
