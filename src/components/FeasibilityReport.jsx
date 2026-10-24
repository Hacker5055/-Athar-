import React from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, Layers, ShieldAlert, Code2, BarChart3, Percent, Check, AlertTriangle } from 'lucide-react';

export default function FeasibilityReport() {
  const feasibleFeatures = [
    {
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      category: 'خوارزميات وحفظ البيانات',
      reason: 'سهلة التنفيذ عبر خوارزميات التكرار (مثل SM-2 المستخدمة في Anki)، وتخزين بيانات حفظ المستخدم محلياً أو على خوادم سحابية.'
    },
    {
      title: 'الربط البصري والذهني للآيات (الخرائط الذهنية)',
      category: 'واجهات وتصميم محتوى',
      reason: 'يعتمد على تصاميم بصرية وهياكل بيانات موضوعية مرتبطة برقم السورة والآيات، وهو مميز جداً وغير مكلف تقنياً.'
    },
    {
      title: 'الباحث الدلالي والنفسي للقرآن والسنة',
      category: 'ذكاء اصطناعي ونصوص (Vector Embeddings)',
      reason: 'يمكن تطبيقه بكفاءة عالية باستخدام نماذج تضمين النصوص (Text Embeddings/Vector Search) لربط الموقف الشعوري بالآيات والأحاديث المناسبة.'
    },
    {
      title: 'التفسير التفاعلي وبطاقة السياق',
      category: 'تغذية بصرية وبيانات',
      reason: 'تجميع واستعراض بيانات أسباب النزول والتفسير المعتمَد في بطاقات تفاعلية جذابة وسريعة الاستجابة.'
    },
    {
      title: 'منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)',
      category: 'فلك وقوائم مهام',
      reason: 'حساب مواقيت الصلاة باستخدام معادلات الفلك أو APIs معروفة، وتوزيع قائمة المهام بين الصلوات ديناميكياً.'
    },
    {
      title: 'حاسبة المواريث والزكاة التفاعلية',
      category: 'حسابات فقهية وAPIs أسعار',
      reason: 'خوارزميات رياضية وفقهية واضحة ومحددة مع ربط بسعر الذهب المباشر عبر APIs مالية بسيطة.'
    },
    {
      title: 'خريطة مرافق المساجد التفاعلية (Crowdsourced Map)',
      category: 'خرائط وتفاعل مجتمعي',
      reason: 'تعتمد على خدمات الخرائط المعتادة (مثل OpenStreetMap أو Google Maps API) ونظام تقييم ومراجعة من المستخدمين.'
    },
    {
      title: 'ختمات وتحديات العائلة والأقصص التفاعلية للأطفال',
      category: 'قواعد بيانات فورية وسرد قصصي',
      reason: 'تعتمد على غرف خاصة (Realtime Database) وهياكل سيناريوهات تفاعلية (Branching Storytelling) ممتعة وآمنة.'
    }
  ];

  const challengingFeatures = [
    {
      title: 'مصحح التلاوة الصوتي للتجويد المباشر (AI Tajweed Recitation Corrector)',
      category: 'معالجة صوتية مخصصة (Audio DSP & NLU)',
      reasons: [
        'تحدي دقة النموذج الصوتي: أحكام التجويد (كالمدود بالإشارات الزمنية الدقيقة، الغنة، القلقلة، ومخارج الحروف المتشابهة كالضاد والظاء) تتطلب نماذج صوتية مخصصة جداً (Highly Specialized Speech Recognition & Audio Signal Processing) مدربة على آلاف الساعات الترتيلية لمختلف القراء واللكنات.',
        'الضوضاء وتنوع نبرات الصوت: اختلاف أداء المستخدمين وتجويدهم الفعلي والضوضاء المحيطة قد يؤدي إلى إنذارات خاطئة تسبب إرباك الحافظ بدلاً من تصحيحه.'
      ]
    },
    {
      title: 'الوضع الهادئ التلقائي في المسجد بالخلفية (Geofencing & Background Silent Mode)',
      category: 'صلاحيات نظام التشغيل (Mobile OS Permissions)',
      reasons: [
        'قيود أنظمة التشغيل (iOS & Android Permissions): تفرض أنظمة iOS و Android حديثاً قيوداً صارمة جداً على تطبيقات الخلفية (Background Services) وتغيير إعدادات النظام الصوتية تلقائياً دون تدخل يدوي مباشر من المستخدم لأسباب أمنية وللحفاظ على البطارية.',
        'استهلاك البطارية ودقة الـ GPS: تتبع الموقع المستمر بدقة عالية للتعرف على دخول المسجد قد يستنزف بطارية الهاتف سريعاً، وقد تضعف إشارة الـ GPS داخل المباني الخرسانية.'
      ]
    },
    {
      title: 'أذكار ذكية تلقائية عبر المستشعرات (Automatic Sensor-Based Athkar)',
      category: 'مستشعرات خلفية وتنبيهات',
      reasons: [
        'قيود الوصول للبلوتوث والمستشعرات في الخلفية: عدم إمكانية فتح التطبيق أو إظهار نوافذ منبثقة (Popups) أثناء قفل الشاشة إلا عبر إشعارات محدودة (Push Notifications) يقلل من سلاسة التجربة.',
        'الإنذارات الخاطئة: الاقتران ببلوتوث مكبر صوت قد لا يكون سيارة، أو المرور بجانب مول دون دخوله قد يطلق إشعارات مزعجة غير مرغوبة.'
      ]
    },
    {
      title: 'ماسح العقود والمعاملات المالية الشرعي (AI Financial Contract Legal Scanner)',
      category: 'تحفظ شرعي واستشارة قانونية',
      reasons: [
        'التحفظ الشرعي والتنفيذي: الاعتماد الكلي على الذكاء الاصطناعي في الفتوى وإصدار الأحكام القانونية والشرعية للعقود قد يترتب عليه مسؤولية قانونية وشرعية خطيرة في حال حدوث خطأ أو فهم خاطئ للبند العائدي.',
        'تعقيد المصطلحات القانونية: صياغة العقود تتنوع بشكل واسع وتستخدم مصطلحات قانونية محلية تختلف من دولة لأخرى وتتطلب مراجعة بشرية مستمرة.'
      ]
    }
  ];

  const totalFeatures = feasibleFeatures.length + challengingFeatures.length;
  const feasiblePercent = Math.round((feasibleFeatures.length / totalFeatures) * 100);
  const challengingPercent = 100 - feasiblePercent;

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 border border-slate-700 rounded-2xl p-6 shadow-xl">
        <div className="flex items-center gap-3 mb-2">
          <div className="p-2.5 bg-emerald-500/20 text-emerald-400 rounded-xl border border-emerald-500/30">
            <Cpu className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-white">الدراسة التقنية وجدول تقييم الميزات</h2>
            <p className="text-xs text-slate-300 mt-0.5">
              تحليل تقني ودراسة جدوى لميزات تطبيق <span className="text-emerald-400 font-bold">أثَر</span> (الميزات القابلة للإضافة الفورية والميزات المعقدة تقنياً/شرعياً مع الأسباب).
            </p>
          </div>
        </div>
      </div>

      {/* METRICS DASHBOARD */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Total Features */}
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-lg flex items-center justify-between">
          <div>
            <span className="text-xs text-slate-400 block mb-1">إجمالي الميزات المقترحة</span>
            <div className="text-3xl font-extrabold text-white font-mono">{totalFeatures} ميزة</div>
            <span className="text-[10px] text-slate-500 mt-1 block">تغطي 6 محاور رئيسية للتطبيق</span>
          </div>
          <div className="p-3 bg-blue-500/10 text-blue-400 rounded-2xl border border-blue-500/20">
            <BarChart3 className="w-7 h-7" />
          </div>
        </div>

        {/* Feasible Rate */}
        <div className="bg-slate-900/90 border border-emerald-500/30 rounded-2xl p-5 shadow-lg flex items-center justify-between">
          <div>
            <span className="text-xs text-emerald-400 block mb-1 font-semibold">الميزات القابلة للإضافة الفورية</span>
            <div className="text-3xl font-extrabold text-emerald-400 font-mono">
              {feasibleFeatures.length} <span className="text-sm text-emerald-300/80">({feasiblePercent}%)</span>
            </div>
            <span className="text-[10px] text-emerald-500/90 mt-1 block">تنفَّذ بدقة عالية وتكلفة تقنية متوازنة</span>
          </div>
          <div className="p-3 bg-emerald-500/20 text-emerald-400 rounded-2xl border border-emerald-500/30">
            <CheckCircle2 className="w-7 h-7" />
          </div>
        </div>

        {/* Challenging Rate */}
        <div className="bg-slate-900/90 border border-amber-500/30 rounded-2xl p-5 shadow-lg flex items-center justify-between">
          <div>
            <span className="text-xs text-amber-400 block mb-1 font-semibold">الميزات المعقدة/الصعبة</span>
            <div className="text-3xl font-extrabold text-amber-400 font-mono">
              {challengingFeatures.length} <span className="text-sm text-amber-300/80">({challengingPercent}%)</span>
            </div>
            <span className="text-[10px] text-amber-500/90 mt-1 block">تتطلب نماذج صوتية مخصصة أو موافقة النظام</span>
          </div>
          <div className="p-3 bg-amber-500/20 text-amber-400 rounded-2xl border border-amber-500/30">
            <AlertTriangle className="w-7 h-7" />
          </div>
        </div>
      </div>

      {/* FEASIBILITY BAR CHART GRAPH */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 space-y-3">
        <div className="flex justify-between items-center text-xs text-slate-300 font-semibold">
          <span>مقياس جدوى التنفيذ التقني (Feasibility Metric Score)</span>
          <span className="text-emerald-400 font-mono">{feasiblePercent}% قابل للتنفيذ المباشر</span>
        </div>
        <div className="w-full bg-slate-950 h-3 rounded-full overflow-hidden flex border border-slate-800">
          <div style={{ width: `${feasiblePercent}%` }} className="bg-emerald-500 h-full transition-all"></div>
          <div style={{ width: `${challengingPercent}%` }} className="bg-amber-500 h-full transition-all"></div>
        </div>
        <div className="flex items-center gap-6 text-[11px] text-slate-400 pt-1">
          <div className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
            <span>قابلة للإضافة المباشرة ({feasibleFeatures.length})</span>
          </div>
          <div className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-amber-500"></span>
            <span>تحتاج حظراً/تطويراً خاصاً أو إشرافاً شرعياً ({challengingFeatures.length})</span>
          </div>
        </div>
      </div>

      {/* FEASIBLE FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2 border-b border-slate-800 pb-3">
          <CheckCircle2 className="w-5 h-5 text-emerald-400" />
          أولاً: الميزات القابلة للإضافة والتطبيق الفوري ({feasibleFeatures.length} ميزات)
        </h3>

        <div className="grid md:grid-cols-2 gap-4">
          {feasibleFeatures.map((item, idx) => (
            <div key={idx} className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-1.5 hover:border-emerald-500/30 transition-all flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between gap-2 mb-1">
                  <h4 className="text-xs font-bold text-emerald-300 flex items-center gap-1.5">
                    <Sparkles className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                    {item.title}
                  </h4>
                </div>
                <p className="text-[11px] text-slate-400 leading-relaxed">{item.reason}</p>
              </div>
              <div className="pt-2 mt-2 border-t border-slate-900 flex justify-between items-center text-[10px]">
                <span className="text-slate-500">التصنيف الفني:</span>
                <span className="text-emerald-400 bg-emerald-950/60 px-2 py-0.5 rounded border border-emerald-500/20">{item.category}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* CHALLENGING FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <h3 className="text-lg font-bold text-red-400 flex items-center gap-2 border-b border-slate-800 pb-3">
          <XCircle className="w-5 h-5 text-red-400" />
          ثانياً: الميزات الصعبة أو المعقدة تقنياً/تنظيمياً ({challengingFeatures.length} ميزات مع التفاصيل)
        </h3>

        <div className="space-y-4">
          {challengingFeatures.map((item, idx) => (
            <div key={idx} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-3">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-2">
                <h4 className="text-xs font-bold text-amber-300 flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                  {item.title}
                </h4>
                <span className="text-[10px] bg-amber-950/60 text-amber-300 px-2.5 py-0.5 rounded border border-amber-500/20 self-start md:self-auto">
                  {item.category}
                </span>
              </div>

              <ul className="space-y-2 text-xs text-slate-300">
                {item.reasons.map((reason, rIdx) => (
                  <li key={rIdx} className="bg-slate-900/70 p-3 rounded-lg border border-slate-800/80 leading-relaxed text-slate-300">
                    <span className="font-semibold text-slate-200">السبب {rIdx + 1}:</span> {reason}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
