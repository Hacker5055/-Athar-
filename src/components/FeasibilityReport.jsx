import React from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, Layers, ShieldAlert, Code2, ArrowUpRight, BatteryCharging, Shield, Smartphone, Sliders } from 'lucide-react';

export default function FeasibilityReport() {
  const feasibleFeatures = [
    {
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      domain: 'الحفظ والمراجعة الذكية',
      difficulty: 'سهلة إلى متوسطة',
      reason: 'تعتمد على خوارزميات رياضية مثبتة وشهيرة (مثل خوارزمية SM-2 أو FSRS) لتحديد مواعيد المراجعة بناءً على استجابات المستخدم السابقة ومعدل الخطأ، وتخزن بيانات الحفظ محلياً أو سحابياً بكفاءة عالية وبدون استهلاك موارد ضخمة.'
    },
    {
      title: 'الربط البصري والذهني للآيات (الخرائط الذهنية والموضوعية)',
      domain: 'الحفظ والمراجعة الذكية',
      difficulty: 'سهلة',
      reason: 'تعتمد على هيكلة شجرية وبيانات محددة مسبقاً (JSON/Graph) لتقسيم السور إلى محاور وقصص ورسومات بيانية واضحة، ولا تحتاج لمعالجة معقدة أثناء التشغيل.'
    },
    {
      title: 'الباحث الدلالي والنفسي (القرآن والسنة حسب حالتك الشعورية)',
      domain: 'المعايشة والفهم العميق',
      difficulty: 'متوسطة',
      reason: 'يمكن تطبيقها بنجاح عبر تقنيات التضمين اللغوي والبحث الشعاعي (Vector Search / Embeddings) لمطابقة الكلمات الدلالية والمشاعر (قلق، ظلم، فرح، تردد) مع نصوص القرآن والحديث المفسرة والمفهرسة مسبقاً.'
    },
    {
      title: 'التفسير التفاعلي وبطاقة السياق (Context Card)',
      domain: 'المعايشة والفهم العميق',
      difficulty: 'سهلة',
      reason: 'تعتمد على قاعدة بيانات موثقة لأسباب النزول (مكي/مدني)، ومعاني الكلمات الغريبة والقصص التوضيحية، وعرضها في واجهات تفاعلية وسلسة عند النقر على الآية.'
    },
    {
      title: 'منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)',
      domain: 'إنتاجية المسلم',
      difficulty: 'سهلة إلى متوسطة',
      reason: 'حساب مواقيت الصلاة بدقة عبر المعادلات الفلكية المعروفة (أو مكتبات مثل Adhan.js)، مع إعادة جدولة وتوزيع المهام اليومية بين فترات الصلوات الخمس ديناميكياً بحسب الموقع الجغرافي للمستخدم.'
    },
    {
      title: 'حاسبة المواريث والزكاة التفاعلية',
      domain: 'المعاملات المالية والشرعية',
      difficulty: 'متوسطة',
      reason: 'قواعد قسمة التركات وأنصبة الورثة محددة بدقة في الفقه الإسلامي ويمكن صياغتها كخوارزمية شرطية واضحة، وحساب الزكاة مرتبط بأسعار الذهب اللحظية عبر واجهات برمجة تطبيقات (APIs) مالية بسيطة ومجانية.'
    },
    {
      title: 'خريطة مرافق المساجد التفاعلية (Crowdsourced Mosque Map)',
      domain: 'المجتمع والعمل الجماعي',
      difficulty: 'متوسطة',
      reason: 'تطبيق نمط البيانات التشاركية (Crowdsourcing) عبر خدمات الخرائط المفتوحة (OpenStreetMap أو Mapbox أو Google Maps) لتمكين المصلين من إضافة وتقييم المرافق (مصلى سيدات، نظافة، ذوي الاحتياجات الخاصة).'
    },
    {
      title: 'ختمات وتحديات العائلة والأصدقاء (Private Groups)',
      domain: 'المجتمع والعمل الجماعي',
      difficulty: 'سهلة إلى متوسطة',
      reason: 'تنفيذ غرف افتراضية خاصة باستخدام قواعد بيانات في الوقت الفعلي (Firebase / Supabase) لمتابعة تقدم القراءة والورد اليومي مع خيار الخصوصية التامة دون رياء.'
    },
    {
      title: 'القصص الأخلاقية التفاعلية للأطفال (اختر مسارك بأخلاقك)',
      domain: 'قسم الأطفال والجيل الجديد',
      difficulty: 'سهلة',
      reason: 'بناء سيناريوهات شجرية متفرعة (Branching Narratives) ذات مغزى أخلاقي وسلوكي مستمد من السيرة النبوية، واختيار الطفل لمساره مع توضيح العواقب والدروس التربوية.'
    }
  ];

  const challengingFeatures = [
    {
      title: 'مصحح التلاوة الصوتي للتجويد المباشر (AI Tajweed Recitation Corrector)',
      domain: 'الحفظ والمراجعة الذكية',
      badge: 'صعوبة تقنية عالية جداً',
      color: 'red',
      reasons: [
        {
          head: 'دقة معالجة الإشارات الصوتية ومخارج الحروف الدقيقة:',
          detail: 'أحكام التجويد (كالمد المتصل والمنفصل بعدد حركات زمني محدد، الغنة بمقدار حركتين، القلقلة الصغرى والكبرى، والتمييز الصوتي الدقيق بين الضاد والظاء أو الثاء والسين) تتطلب نماذج صوتية فائقة التخصص (Acoustic Phonetic Models) مدربة على مئات آلاف الساعات الترتيلية لمختلف الفئات العمرية واللهجات.'
        },
        {
          head: 'محدودية المعالجة على الهاتف والضوضاء المحيطة:',
          detail: 'معالجة الصوت فائق الدقة محلياً على الهواتف يستهلك المعالج والبطارية بشدة، بينما إرسال الصوت للخوادم السحابية يضيف تأخيراً زمنياً (Latency) قد يفسد تجربة التسميع الفوري، بجانب أثر ضوضاء البيئة المحيطة التي تؤدي لإنذارات خاطئة.'
        }
      ],
      practicalAlternative: 'الحل المقترح حالياً: البدء بفحص مطابقة الكلمات والتشكيل (Speech-to-Text alignment) المتاح بدقة مقبولة، وإضافة تنبيهات تجويدية استرشادية مكتوبة لكل آية، مع إتاحة التحقق الدقيق للتجويد كأداة تسجيل واستشارة لمشرف أو عبر نماذج صوتية متقدمة في الخلفية.'
    },
    {
      title: 'الوضع الهادئ التلقائي في المسجد بالخلفية (Geofencing & Background Silent Mode)',
      domain: 'إنتاجية المسلم',
      badge: 'قيود أنظمة التشغيل (OS Permissions)',
      color: 'amber',
      reasons: [
        {
          head: 'قيود سياسات أبل وجوجل الصارمة (iOS & Android Background Restrictions):',
          detail: 'تمنع أنظمة iOS التطبيقات منعاً باتاً من التعديل التلقائي على مفتاح الصامت (Mute Switch) أو نمط الصوت دون تدخل يدوي للمستخدم. كما تشدد Google Play في نظام Android 13+ شروط منح صلاحيات Do Not Disturb (DND Access) في الخلفية.'
        },
        {
          head: 'استنزاف البطارية وضعف إشارة الـ GPS داخل المساجد:',
          detail: 'المراقبة الجغرافية المستمرة (High-accuracy Background Geofencing) تستنزف طاقة الهاتف سريعاً، فضلاً عن ضعف وتشتت إشارة الأقمار الصناعية داخل المباني الخرسانية الكبيرة للمساجد، مما يسبب عدم دقة في تحديد لحظة الدخول أو الخروج.'
        }
      ],
      practicalAlternative: 'الحل المقترح حالياً: إرسال إشعار تفاعلي سريع (Rich Push Notification) بنقرة واحدة عند الاقتراب من وقت الصلاة أو النطاق الجغرافي للمسجد لتفعيل وضع الخشوع، أو إرشاد المستخدم لإعداد أتمتة عبر Apple Shortcuts و Android Modes.'
    },
    {
      title: 'الأذكار الذكية التلقائية المعتمدة على المستشعرات (Automatic Sensor-Based Athkar)',
      domain: 'إنتاجية المسلم',
      badge: 'قيود الخلفية ومعدل الإنذار الخاطئ',
      color: 'amber',
      reasons: [
        {
          head: 'منع النوافذ المنبثقة من الخلفية:',
          detail: 'لا تسمح أنظمة الهواتف بفتح الشاشة أو إظهار نوافذ منبثقة (Popups) تلقائياً عند قفل الجهاز، ويكون التطبيق محصوراً في إرسال إشعار نصي عادي قد يفوته المستخدم.'
        },
        {
          head: 'ارتفاع معدل الإيجابيات الكاذبة (False Positives):',
          detail: 'الاتصال ببلوتوث مكبر صوت خارجي قد لا يعني ركوب سيارة، والمرور بجوار مركز تجاري أو محطة وقود قد يرسل دعاء السوق بالخطأ، مما يسبب إزعاج المستخدم وإلغائه للتنبيهات.'
        }
      ],
      practicalAlternative: 'الحل المقترح حالياً: دمج مستشعرات النشاط الحركي (Activity Recognition API) بحذر مع إشعار ذكي مهذب وغير متكرر، وإتاحة بطاقات الأذكار السريعة القابلة للفتح السريع بضغطة واحدة من شاشة القفل أو الودجت (Widget).'
    },
    {
      title: 'ماسح العقود والمعاملات المالية الشرعي (AI Financial Legal Contract Scanner)',
      domain: 'المعاملات المالية والشرعية',
      badge: 'مسؤولية شرعية وقانونية وتنوع الصياغات',
      color: 'red',
      reasons: [
        {
          head: 'خطورة الخطأ والمسؤولية الفقهية والقانونية:',
          detail: 'الاعتماد على نموذج ذكاء اصطناعي لإجازة أو تحريم بنود عقد مالي قد يورط المستخدم في شبهات ربوية أو التزامات باطلة شرعاً في حال أخطأ النموذج أو أساء فهم عبارة قانونية ملتوية.'
        },
        {
          head: 'تعقيد وتنوع الصياغات القانونية المحلية:',
          detail: 'تختلف صياغات العقود والمصطلحات القانونية بين الدول الإسلامية والهيئات الرقابية الشرعية للمصارف، ما يجعل التعميم التلقائي بدون تدقيق بشري مصدراً للخلل.'
        }
      ],
      practicalAlternative: 'الحل المقترح حالياً: تقديم الميزة كـ "مساعد استرشادي أولي" مع إخلاء مسؤولية صريح، يقوم بتظليل البنود المشهورة (مثل غرامات التأخير الصريحة والشرط الجزائي المبهم) واقتراح صياغات بديلة متوافقة مع مجمع الفقه الإسلامي، مع إمكانية توجيه الاستشارة لمختص فقهي.'
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 border border-slate-700 rounded-2xl p-6 shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-emerald-500/20 text-emerald-400 rounded-2xl border border-emerald-500/30">
              <Cpu className="w-7 h-7" />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-white">الدراسة التقنية وجدول تقييم الميزات لتطبيق "أثَر"</h2>
              <p className="text-xs text-slate-300 mt-1">
                تحليل معمق للميزات القابلة للإضافة المباشرة مقابل الميزات المعقدة برمجياً وتنظيمياً مع البدائل العملية المقترحة.
              </p>
            </div>
          </div>
          <div className="flex items-center gap-3 bg-slate-950/80 px-4 py-2.5 rounded-xl border border-slate-800 text-xs">
            <span className="text-emerald-400 font-bold">9 ميزات قابلة للتطبيق الفوري</span>
            <span className="text-slate-600">|</span>
            <span className="text-red-400 font-bold">4 ميزات تواجه تحديات هندسية/تنظيمية</span>
          </div>
        </div>
      </div>

      {/* SUMMARY STATS BAR */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
        <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-xl">
          <span className="text-xs text-slate-400 block mb-1">الميزات المجدية تقنياً</span>
          <span className="text-2xl font-extrabold text-emerald-400">69%</span>
          <span className="text-[10px] text-slate-500 block mt-1">تعتمد على APIs وخوارزميات معيارية</span>
        </div>
        <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-xl">
          <span className="text-xs text-slate-400 block mb-1">الميزات ذات التحديات</span>
          <span className="text-2xl font-extrabold text-amber-400">31%</span>
          <span className="text-[10px] text-slate-500 block mt-1">تحتاج معالجة صوتية أو صلاحيات نظام حرجة</span>
        </div>
        <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-xl">
          <span className="text-xs text-slate-400 block mb-1">الأثر على تجربة المستخدم</span>
          <span className="text-2xl font-extrabold text-blue-400">استثنائي</span>
          <span className="text-[10px] text-slate-500 block mt-1">تخصيص كامل وذكاء تفاعلي عملي</span>
        </div>
        <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-xl">
          <span className="text-xs text-slate-400 block mb-1">نهج التنفيذ في تطبيق أثر</span>
          <span className="text-2xl font-extrabold text-purple-400">تفاعلي متكامل</span>
          <span className="text-[10px] text-slate-500 block mt-1">محاكاة عملية لكافة السيناريوهات</span>
        </div>
      </div>

      {/* FEASIBLE FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2">
            <CheckCircle2 className="w-5 h-5 text-emerald-400" />
            أولاً: الميزات القابلة للإضافة والتطبيق الفوري (Feasible & High-Impact Features)
          </h3>
          <span className="text-xs bg-emerald-500/20 text-emerald-300 px-3 py-1 rounded-full border border-emerald-500/30">
            جاهزة للتطبيق البرمجي
          </span>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          {feasibleFeatures.map((item, idx) => (
            <div key={idx} className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-2 hover:border-emerald-500/40 transition-all flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between text-[11px] mb-2">
                  <span className="text-slate-400 bg-slate-900 px-2 py-0.5 rounded border border-slate-800">{item.domain}</span>
                  <span className="text-emerald-400 font-semibold">{item.difficulty}</span>
                </div>
                <h4 className="text-xs font-bold text-emerald-300 flex items-center gap-1.5 mb-2 leading-relaxed">
                  <Sparkles className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                  {item.title}
                </h4>
                <p className="text-[11px] text-slate-400 leading-relaxed">{item.reason}</p>
              </div>
              <div className="pt-2 border-t border-slate-900 flex items-center text-[10px] text-emerald-400/80">
                <span>✓ مطبّق ومدمج في واجهات "أثر" الحالية</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* CHALLENGING FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-5">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h3 className="text-lg font-bold text-red-400 flex items-center gap-2">
            <XCircle className="w-5 h-5 text-red-400" />
            ثانياً: الميزات المعقدة أو الصعبة مع الأسباب التقنية والتنظيمية والبدائل المقترحة
          </h3>
          <span className="text-xs bg-red-500/20 text-red-300 px-3 py-1 rounded-full border border-red-500/30">
            تحديات معمارية وتنظيمية
          </span>
        </div>

        <div className="space-y-5">
          {challengingFeatures.map((item, idx) => (
            <div key={idx} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-4 hover:border-slate-700 transition-all">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-2 border-b border-slate-900 pb-3">
                <div className="flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                  <h4 className="text-sm font-bold text-white">{item.title}</h4>
                </div>
                <div className="flex items-center gap-2">
                  <span className="text-[11px] text-slate-400">{item.domain}</span>
                  <span className={`text-[10px] font-bold px-2.5 py-1 rounded-full border ${
                    item.color === 'red' ? 'bg-red-500/20 text-red-300 border-red-500/30' : 'bg-amber-500/20 text-amber-300 border-amber-500/30'
                  }`}>
                    {item.badge}
                  </span>
                </div>
              </div>

              {/* Reasons Breakdown */}
              <div className="grid md:grid-cols-2 gap-3 text-xs">
                {item.reasons.map((r, rIdx) => (
                  <div key={rIdx} className="bg-slate-900/80 p-3.5 rounded-lg border border-slate-800/80 space-y-1.5">
                    <span className="font-bold text-amber-300 block">العائق {rIdx + 1}: {r.head}</span>
                    <p className="text-slate-300 text-[11px] leading-relaxed">{r.detail}</p>
                  </div>
                ))}
              </div>

              {/* Practical Alternative */}
              <div className="bg-emerald-950/20 border border-emerald-500/30 p-3.5 rounded-xl text-xs space-y-1">
                <span className="font-bold text-emerald-400 flex items-center gap-1.5">
                  <CheckCircle2 className="w-4 h-4" />
                  الحل والبديل الهندسي الذكي في تطبيق "أثَر":
                </span>
                <p className="text-slate-300 text-[11px] leading-relaxed">
                  {item.practicalAlternative}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
