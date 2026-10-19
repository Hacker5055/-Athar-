/* ═══════════════════════════════════════════════════════════════
   calc.js — حسابات فلكية حقيقية (بدون اعتماديات)
   - مواقيت الصلاة: خوارزمية PrayTimes.org (منسوبة للمصدر الأصلي)
   - التاريخ الهجري: الخوارزمية الجدولية الكويتية + إزاحة قابلة للضبط
   - اتجاه القبلة: الدائرة العظمى + مسافة هافرساين
   ═══════════════════════════════════════════════════════════════ */
const Calc = (() => {
  const PI = Math.PI;
  const d2r = d => d * PI / 180, r2d = r => r * 180 / PI;
  const sin = d => Math.sin(d2r(d)), cos = d => Math.cos(d2r(d)), tan = d => Math.tan(d2r(d));
  const asin = x => r2d(Math.asin(x)), acos = x => r2d(Math.acos(x));
  const atan2d = (y, x) => r2d(Math.atan2(y, x));
  const acot = x => r2d(Math.atan(1 / x));
  const fixHour = a => { a = a - 24 * Math.floor(a / 24); return a; };

  /* ── اليوم اليولياني ── */
  function julianDay(y, m, d) {
    const a = Math.floor((14 - m) / 12), yy = y + 4800 - a, mm = m + 12 * a - 3;
    return d + Math.floor((153 * mm + 2) / 5) + 365 * yy + Math.floor(yy / 4)
         - Math.floor(yy / 100) + Math.floor(yy / 400) - 32045;
  }

  /* ── موضع الشمس: الميل + معادلة الوقت ── */
  function sunPos(jd) {
    const D = jd - 2451545.0;
    const g = (357.529 + 0.98560028 * D) % 360;
    const q = (280.459 + 0.98564736 * D) % 360;
    const L = (q + 1.915 * sin(g) + 0.020 * sin(2 * g)) % 360;
    const e = 23.439 - 0.00000036 * D;
    const decl = asin(sin(e) * sin(L));
    let RA = fixHour(atan2d(cos(e) * sin(L), cos(L)) / 15);
    const eqt = q / 15 - RA;
    return { decl, eqt };
  }

  /* ── طرق الحساب ── */
  const METHODS = {
    egyptian:     { ar:'الهيئة المصرية العامة للمساحة', en:'Egyptian General Authority of Survey', fr:'Autorité égyptienne',            fajr:19.5, isha:17.5 },
    umm_alqura:   { ar:'أم القرى',                     en:'Umm Al-Qura (Makkah)',                  fr:'Umm Al-Qura (La Mecque)',         fajr:18.5, ishaInterval:90 },
    mwl:          { ar:'رابطة العالم الإسلامي',          en:'Muslim World League',                   fr:'Ligue islamique mondiale',        fajr:18,   isha:17 },
    isna:         { ar:'ISNA (أمريكا الشمالية)',        en:'ISNA (North America)',                  fr:'ISNA (Amérique du Nord)',         fajr:15,   isha:15 },
    karachi:      { ar:'جامعة العلوم الإسلامية، كراتشي',  en:'University of Islamic Sciences, Karachi', fr:'Univ. des sciences, Karachi',  fajr:18,   isha:18 },
    gulf:         { ar:'دول الخليج',                    en:'Gulf Region',                           fr:'Région du Golfe',                 fajr:19.5, ishaInterval:90 },
    turkey:       { ar:'تركيا (Diyanet)',               en:'Turkey (Diyanet)',                      fr:'Turquie (Diyanet)',               fajr:18,   isha:17 },
    france15:     { ar:'فرنسا ١٥°',                     en:'France 15° (UOIF)',                     fr:'France 15° (UOIF)',               fajr:15,   isha:15 },
    france18:     { ar:'فرنسا ١٨°',                     en:'France 18°',                            fr:'France 18°',                      fajr:18,   isha:18 },
    singapore:    { ar:'سنغافورة',                      en:'Singapore (MUIS)',                      fr:'Singapour (MUIS)',                fajr:20,   isha:18 },
    tehran:       { ar:'طهران / الجعفري',               en:'Tehran / Ja’fari',                      fr:'Téhéran / Ja’farite',             fajr:17.7, isha:14, maghrib:4.5 },
  };

  /* ── الحساب الرئيسي ── */
  function prayerTimes(date, lat, lng, tzHours, opts = {}) {
    const M = METHODS[opts.method || 'egyptian'] || METHODS.egyptian;
    const elevation = opts.elevation || 0;
    const asrFactor = opts.madhab === 'hanafi' ? 2 : 1;
    const jd = julianDay(date.getFullYear(), date.getMonth() + 1, date.getDate()) - lng / (15 * 24);

    const riseSetAngle = 0.833 + 0.0347 * Math.sqrt(elevation);
    const sp = t => sunPos(jd + t / 24);

    const sunAngleTime = (angle, t, ccw) => {
      const { decl, eqt } = sp(t * 24);
      const noon = fixHour(12 - eqt);
      const arg = (-sin(angle) - sin(decl) * sin(lat)) / (cos(decl) * cos(lat));
      if (arg < -1 || arg > 1) return NaN;
      const h = (1 / 15) * acos(arg);
      return ccw ? noon - h : noon + h;
    };
    const midDay = t => { const { eqt } = sp(t * 24); return fixHour(12 - eqt); };
    const asrTime = t => {
      const { decl } = sp(t * 24);
      return sunAngleTime(-acot(asrFactor + tan(Math.abs(lat - decl))), t, false);
    };

    let f = sunAngleTime(M.fajr, 5 / 24, true);
    let r = sunAngleTime(riseSetAngle, 6 / 24, true);
    let d = midDay(12 / 24);
    let a = asrTime(13 / 24);
    let s = sunAngleTime(riseSetAngle, 18 / 24, false);
    let m = M.maghrib ? sunAngleTime(M.maghrib, 18 / 24, false) : s;
    let i = M.ishaInterval ? (m + M.ishaInterval / 60) : sunAngleTime(M.isha, 18 / 24, false);

    /* خطوط العرض العالية: لا يوجد شفق حقيقي */
    if (isNaN(f) || isNaN(i)) {
      const night = 24 - (s - r);
      const portion = opts.highLatPortion || 0.5;   // منتصف الليل
      if (isNaN(f)) f = r - portion * night;
      if (isNaN(i)) i = s + portion * night;
    }

    const toLocal = h => fixHour(h + tzHours - lng / 15);
    const out = {
      fajr: toLocal(f), sunrise: toLocal(r), dhuhr: toLocal(d),
      asr: toLocal(a), maghrib: toLocal(m), isha: toLocal(i),
    };
    /* تعديل يدوي بالدقائق */
    const adj = opts.adjust || {};
    for (const k in out) out[k] = fixHour(out[k] + (adj[k] || 0) / 60);
    /* الإمساك */
    out.imsak = fixHour(out.fajr - 10 / 60);
    return out;
  }

  /* ── التاريخ الهجري (جدولي + إزاحة) ── */
  const HIJRI_MONTHS = {
    ar: ['محرّم','صفر','ربيع الأول','ربيع الآخر','جمادى الأولى','جمادى الآخرة','رجب','شعبان','رمضان','شوّال','ذو القعدة','ذو الحجة'],
    en: ['Muharram','Safar','Rabi al-Awwal','Rabi al-Thani','Jumada al-Ula','Jumada al-Akhirah','Rajab','Sha’ban','Ramadan','Shawwal','Dhul-Qi’dah','Dhul-Hijjah'],
    fr: ['Mouharram','Safar','Rabi al-Awwal','Rabi ath-Thani','Joumada al-Oula','Joumada al-Akhira','Rajab','Cha’ban','Ramadan','Chawwal','Dhoul-Qi’da','Dhoul-Hijja'],
  };
  function toHijri(date, offset = 0) {
    const jd = julianDay(date.getFullYear(), date.getMonth() + 1, date.getDate()) + offset;
    const l0 = jd - 1948440 + 10632;
    const n = Math.floor((l0 - 1) / 10631);
    let l = l0 - 10631 * n + 354;
    const j = Math.floor((10985 - l) / 5316) * Math.floor(50 * l / 17719)
            + Math.floor(l / 5670) * Math.floor(43 * l / 15238);
    l = l - Math.floor((30 - j) / 15) * Math.floor(17719 * j / 50)
          - Math.floor(j / 16) * Math.floor(15238 * j / 43) + 29;
    const m = Math.floor(24 * l / 709);
    const d = l - Math.floor(709 * m / 24);
    const y = 30 * n + j - 30;
    return { year: y, month: m, day: d };
  }

  /* ── القبلة ── */
  const KAABA = { lat: 21.4225, lng: 39.8262 };
  function qiblaBearing(lat, lng) {
    const φ1 = d2r(lat), φ2 = d2r(KAABA.lat), Δλ = d2r(KAABA.lng - lng);
    const y = Math.sin(Δλ);
    const x = Math.cos(φ1) * Math.tan(φ2) - Math.sin(φ1) * Math.cos(Δλ);
    return (r2d(Math.atan2(y, x)) + 360) % 360;
  }
  function distanceKm(lat1, lng1, lat2, lng2) {
    const R = 6371, dφ = d2r(lat2 - lat1), dλ = d2r(lng2 - lng1);
    const a = Math.sin(dφ / 2) ** 2 + Math.cos(d2r(lat1)) * Math.cos(d2r(lat2)) * Math.sin(dλ / 2) ** 2;
    return 2 * R * Math.asin(Math.sqrt(a));
  }

  /* ── إزاحة التوقيت من IANA ── */
  function tzOffset(iana) {
    try {
      const p = new Intl.DateTimeFormat('en-US', { timeZone: iana, timeZoneName: 'longOffset' })
        .formatToParts(new Date()).find(x => x.type === 'timeZoneName');
      const m = /GMT([+-])(\d{1,2}):(\d{2})/.exec(p.value);
      if (m) return (m[1] === '-' ? -1 : 1) * (+m[2] + +m[3] / 60);
    } catch (e) { /* fallback */ }
    return 0;
  }

  return { prayerTimes, toHijri, HIJRI_MONTHS, qiblaBearing, distanceKm, tzOffset, METHODS, KAABA };
})();
