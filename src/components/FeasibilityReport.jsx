import React from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, Layers, ShieldAlert, Code2, Wrench, BarChart3, HelpCircle } from 'lucide-react';

export default function FeasibilityReport() {
  const feasibleFeatures = [
    {
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      category: 'الحفظ والمراجعة',
      complexity: 'منخفضة - متوسطة',
      reason: 'سهلة التنفيذ برمجياً باستخدام خوارزميات التكرار المتباعد الرياضية المجرّبة (مثل SuperMemo SM-2 أو FSRS)، وتخزين سجل مراجعات المستخدم محلياً أو سحابياً.',
      impact: 'عالي جداً في تثبيت الحفظ ومعالجة ضعف الذاكرة'
    },
    {
      title: 'الربط البصري والذهني للآيات (الخرائط الذهنية والمحاور الموضوعية)',
      category: 'الحفظ والمعايشة',
      complexity: 'منخفضة تقنياً (تعتمد على إعداد المحتوى)',
      reason: 'تعتمد على هيكلة بيانات شجرية وموضوعية جاهزة وموثقة لكل سورة، مع تصاميم واجهات بصرية تفاعلية وسريعة.',
      impact: 'يساعد الحفاظ في فهم ترابط الآيات وتفادي المتشابهات'
    },
    {
      title: 'الباحث الدلالي والنفسي (القرآن والسنة حسب حالتك الشعورية)',
      category: 'المعايشة والفهم',
      complexity: 'متوسطة',
      reason: 'يمكن تطبيقها بكفاءة عالية باستخدام تقنيات البحث بالمتجهات (Vector Search & Text Embeddings) لمطابقة المعاني والمشاعر بآيات وأحاديث وسير نبوية مصنفة بدقة.',
      impact: 'يجعل التطبيق ملاذاً نفسياً وإيمانياً يومياً للمستخدم'
    },
    {
      title: 'التفسير التفاعلي وبطاقة سياق الآية وأسباب النزول',
      category: 'الفهم والتدبر',
      complexity: 'منخفضة',
      reason: 'تعتمد على بنية بيانات غنية تدمج أسباب النزول المعتمدة والمعاني اللغوية في بطاقات تفاعلية أنيقة تظهر عند النقر على الآية.',
      impact: 'يزيل الفجوة المعرفية بين القراءة العابرة والتدبر العميق'
    },
    {
      title: 'منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)',
      category: 'إنتاجية المسلم',
      complexity: 'متوسطة',
      reason: 'حساب مواقيت الصلاة الفلكية محلياً في الهاتف (أو عبر APIs موثوقة) وربط المهام بفترات ما بين الصلوات، مع إعادة جدولة المهام ديناميكياً بحسب التغير اليومي لمواقيت الصلاة.',
      impact: 'إعادة ضبط بوصلة يوم المسلم ليدور حول العبادة والإنتاجية'
    },
    {
      title: 'حاسبة الزكاة المتطورة (أسهم، ذهب، عملات)',
      category: 'المعاملات المالية',
      complexity: 'منخفضة',
      reason: 'قواعد حسابية فقهية واضحة ومحددة، مع إمكانية ربط سعر الذهب اللحظي عبر واجهات برمجية مالية مفتوحة.',
      impact: 'تيسير إخراج الفريضة المالية بدقة متناهية وسرعة'
    },
    {
      title: 'حاسبة المواريث الشرعية البصرية خطوة بخطوة',
      category: 'المعاملات المالية',
      complexity: 'متوسطة',
      reason: 'تطبيق خوارزميات علم الفرائض وقواعد الحجب والعصبة وأصحاب الفروض، وعرض التوزيع برسم بياني مع الدليل الشرعي لكل وارث.',
      impact: 'توضيح أحكام التركات المعقدة للعامة بأسلوب مبسط وشفاف'
    },
    {
      title: 'خريطة مرافق المساجد التفاعلية (Crowdsourced Mosque Map)',
      category: 'المجتمع والعمل الجماعي',
      complexity: 'متوسطة',
      reason: 'تعتمد على واجهات الخرائط (مثل OpenStreetMap أو Mapbox) ونظام تقييم مساهماتي من رواد المسجد مع شارات تأكيد.',
      impact: 'حل مشكلة حقيقية للعائلات وذوي الاحتياجات الخاصة وكبار السن'
    },
    {
      title: 'ختمات وتحديات العائلة والأصدقاء (مع وضع الحفاظ على الخصوصية)',
      category: 'المجتمع والعمل الجماعي',
      complexity: 'متوسطة',
      reason: 'غرف مغلقة بقواعد بيانات فورية (Real-time DB)، وتوفير خيار إخفاء هوية القارئ منعاً للرياء وتعزيزاً للإخلاص.',
      impact: 'تشجيع الورد اليومي بروح الفريق والتنافس المحمود'
    },
    {
      title: 'القصص الأخلاقية التفاعلية للأطفال والشباب (اختر مسارك بأخلاقك)',
      category: 'الجيل الجديد',
      complexity: 'منخفضة - متوسطة',
      reason: 'هياكل سيناريوهات شجرية متفرعة (Branching Narrative Interactive Story) مبنية على مواقف من السيرة والتاريخ، لا تتطلب كلفة برمجية عالية.',
      impact: 'غرس القيم النبوية العملية في نفوس الناشئة بأسلوب مشوق'
    }
  ];

  const challengingFeatures = [
    {
      title: 'مصحح التلاوة الصوتي للتجويد المباشر (AI Tajweed Recitation Corrector)',
      category: 'الحفظ والمراجعة',
      challengeLevel: 'معقدة جداً (High Difficulty)',
      reasons: [
        'تحدي دقة التعرّف على الصوت القرآني (Speech Recognition vs. Phonetic/Acoustic Analysis): النماذج المعتادة (مثل Whisper أو Google STT) تفهم الكلمات لكنها غير مصممة لرصد زمن المدود (حركتان، 4، 6)، أو الغنة ومراتبها، أو دقة القلقلة وإطباق الظاء وتمييزها عن الضاد.',
        'تنوع اللهجات، النبرات الصوتية، والضوضاء المحيطة: أداء المستخدمين يتفاوت بشكل هائل، وأي نموذج تجويدي ذكاء اصطناعي حالي قد يعطي نسب خطأ مزعجة (False Positives)، مما قد يحبط الحافظ أو يصحح له بشكل غير شرعي.',
        'تكلفة المعالجة الفورية المرتفعة: تحتاج نماذج معالجة الإشارات الصوتية إلى قدرات حوسبية سحابية عالية الاستهلاك وزمن استجابة منخفض جداً.'
      ],
      practicalSolution: 'الحل العملي والبديل: تطبيق نظام هجين، حيث يتعرف الذكاء الاصطناعي على صحة الكلمات والتشكيل (ASR التشكيل)، بينما تُتاح خاصية إرسال المقاطع الصوتية لمعلمين ومقرئين مجازين للتصحيح البشري الموثوق، مع استخدام الذكاء الاصطناعي لفحص القراءات التجريبية.'
    },
    {
      title: 'الوضع الهادئ التلقائي في المسجد في الخلفية (Background Geofencing & Auto-Silent)',
      category: 'إنتاجية المسلم',
      challengeLevel: 'قيود نظام التشغيل صارمة (OS Restrictions)',
      reasons: [
        'قيود أنظمة التشغيل (iOS & Android Permissions): تمنع شركة Apple (iOS) تماماً التطبيقات العادية من تغيير الملف الصوتي للهاتف (Mute / Ringer Switch) تلقائياً لأسباب تتعلق بالخصوصية والأمان، كما تفرض Google في Android قيوداً قاسية على ميزة (Do Not Disturb Access) وتصنفها كأذونات عالية الخطورة.',
        'استنزاف البطارية وضعف إشارة GPS داخل المباني: التتبع الجغرافي المستمر يفرغ البطارية سريعاً، والمساجد مبانٍ ضخمة محصنة خرسانياً تضعف فيها إشارة الأقمار الصناعية فتتأخر الاستجابة لحين خروج المستخدم.'
      ],
      practicalSolution: 'الحل العملي والبديل: إرسال إشعار تفاعلي فوري عند الاقتراب من المسجد أو وقت الأذان (Interactive Notification) بضغطة زر واحدة لتحويل الهاتف إلى الوضع الصامت، أو استخدام وضع التركيز المجدول (iOS Focus Mode / Android Routine).'
    },
    {
      title: 'أذكار ذكية تلقائية عبر مستشعرات الحركة والبلوتوث بالخلفية',
      category: 'إنتاجية المسلم',
      challengeLevel: 'صعوبة متوسطة - قيود خلفية التطبيق (Background Execution)',
      reasons: [
        'محدودية فتح الشاشة أو بث التنبيهات من الخلفية: لا يُسمح للتطبيق بإظهار واجهات كاملة مفاجئة أثناء قفل الشاشة عند ركوب السيارة أو دخول السوق.',
        'الإنذارات الخاطئة (False Triggers): قد يتصل الهاتف بسماعة بلوتوث منزلية فيظن التطبيق أنها سيارة، أو قد يمر المستخدم بجانب مجمع تجاري بالسيارة فيظن أنه دخل السوق.'
      ],
      practicalSolution: 'الحل العملي والبديل: الاستفادة من واجهات التعرف على الأنشطة (Activity Recognition API) وإرسال إشعار لطيف ومقتضب في شريط الإشعارات بدعاء السياق مع بطاقة سريعة.'
    },
    {
      title: 'ماسح العقود والمعاملات المالية الشرعي الآلي (AI Shariah Legal Scanner)',
      category: 'المعاملات المالية',
      challengeLevel: 'مسؤولية شرعية وقانونية عالية (High Legal & Religious Liability)',
      reasons: [
        'خطورة الفتوى الآلية والتبعات القانونية: الحكم على عقد مالي بأنه ربوي أو غير جائز بناءً على تحليل ذكاء اصطناعي قد يوقع المستخدم في حرج شرعي أو خسارة مالية، فلا يجوز الاعتماد على نموذج لغوي كبديل عن الهيئات الشرعية المعتمدة.',
        'غموض الصياغات وتعدد المذاهب واللوائح: تختلف الصياغات القانونية للعقود بين الدول والشركات، وتتطلب فهماً دقيقاً للمقاصد واللوائح المعمول بها.'
      ],
      practicalSolution: 'الحل العملي والبديل: جعل الأداة "مستشاراً توعوياً استرشادياً" يبرز البنود الحساسة (مثل بند غرامة التأخير أو الغرر) وينبه المستخدم للاستفسار عنها من مفتٍ شرعي معتمد، مع تقديم المراجع الشرعية المقارنة والبدائل المعتمدة لدى المجامع الفقهية.'
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 border border-slate-700 rounded-2xl p-6 shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-2.5 bg-emerald-500/20 text-emerald-400 rounded-xl border border-emerald-500/30">
              <Cpu className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-xl md:text-2xl font-bold text-white">الدراسة التقنية وجدول تقييم الميزات لتطبيق "أثَر"</h2>
              <p className="text-xs text-slate-300 mt-0.5">
                تحليل هندسي وتنظيمي شامل للميزات القابلة للإضافة الفورية والميزات الصعبة مع توضيح الأسباب والبدائل العملية.
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="bg-emerald-500/20 text-emerald-300 text-xs px-3 py-1 rounded-full border border-emerald-500/30 font-bold">
              10 ميزات قابلة للإضافة
            </span>
            <span className="bg-amber-500/20 text-amber-300 text-xs px-3 py-1 rounded-full border border-amber-500/30 font-bold">
              4 ميزات تحتاج معالجة خاصة
            </span>
          </div>
        </div>
      </div>

      {/* SUMMARY STATS */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-slate-900/90 border border-slate-800 p-4 rounded-xl space-y-1">
          <span className="text-[11px] text-slate-400">إجمالي الميزات المدروسة</span>
          <p className="text-2xl font-bold text-white font-mono">14 ميزة</p>
        </div>
        <div className="bg-slate-900/90 border border-emerald-500/30 p-4 rounded-xl space-y-1">
          <span className="text-[11px] text-emerald-400 font-semibold">جاهزة للتنفيذ المباشر</span>
          <p className="text-2xl font-bold text-emerald-300 font-mono">71% (10)</p>
        </div>
        <div className="bg-slate-900/90 border border-amber-500/30 p-4 rounded-xl space-y-1">
          <span className="text-[11px] text-amber-400 font-semibold">تحديات تقنية / قيود نظام</span>
          <p className="text-2xl font-bold text-amber-300 font-mono">21% (3)</p>
        </div>
        <div className="bg-slate-900/90 border border-red-500/30 p-4 rounded-xl space-y-1">
          <span className="text-[11px] text-red-400 font-semibold">مسؤولية فقهية وقانونية</span>
          <p className="text-2xl font-bold text-red-300 font-mono">8% (1)</p>
        </div>
      </div>

      {/* FEASIBLE FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-3">
          <h3 className="text-base sm:text-lg font-bold text-emerald-400 flex items-center gap-2">
            <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />
            أولاً: الميزات القابلة للإضافة والتطبيق الفوري في تطبيق أثَر (High Feasibility)
          </h3>
          <span className="text-xs text-slate-400">جدوى تقنية ممتازة وتكلفة تشغيل ملائمة</span>
        </div>

        <div className="grid md:grid-cols-2 gap-4">
          {feasibleFeatures.map((item, idx) => (
            <div key={idx} className="bg-slate-950 p-4 rounded-xl border border-slate-800/90 space-y-2 hover:border-emerald-500/40 transition-all flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between gap-2 mb-1.5">
                  <span className="text-[10px] bg-emerald-500/10 text-emerald-400 px-2 py-0.5 rounded border border-emerald-500/20 font-medium">
                    {item.category}
                  </span>
                  <span className="text-[10px] text-slate-400 bg-slate-900 px-2 py-0.5 rounded">
                    صعوبة: {item.complexity}
                  </span>
                </div>
                <h4 className="text-xs sm:text-sm font-bold text-slate-100 flex items-center gap-1.5">
                  <Sparkles className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                  {item.title}
                </h4>
                <p className="text-xs text-slate-400 leading-relaxed mt-2">{item.reason}</p>
              </div>

              <div className="pt-2 border-t border-slate-800/70 text-[11px] text-emerald-300/90 flex items-center gap-1.5">
                <span className="font-semibold text-slate-300">الأثر في التطبيق:</span>
                <span>{item.impact}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* CHALLENGING FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-3">
          <h3 className="text-base sm:text-lg font-bold text-amber-400 flex items-center gap-2">
            <XCircle className="w-5 h-5 text-amber-400 shrink-0" />
            ثانياً: الميزات الصعبة أو المقيدة بنظام التشغيل مع الأسباب والبدائل الهندسية
          </h3>
          <span className="text-xs text-slate-400">تحليل الأسباب التقنية والتنظيمية + الحلول البديلة</span>
        </div>

        <div className="space-y-4">
          {challengingFeatures.map((item, idx) => (
            <div key={idx} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-3">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800/80 pb-2.5">
                <h4 className="text-sm font-bold text-amber-300 flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                  {item.title}
                </h4>
                <span className="text-[10px] bg-red-500/20 text-red-300 px-2.5 py-1 rounded-full border border-red-500/30 font-semibold w-fit">
                  {item.challengeLevel}
                </span>
              </div>

              <div className="space-y-2">
                <span className="text-xs text-slate-300 font-semibold block">الأسباب الهندسية والتنظيمية:</span>
                <ul className="space-y-2 text-xs text-slate-400">
                  {item.reasons.map((reason, rIdx) => (
                    <li key={rIdx} className="bg-slate-900/70 p-3 rounded-lg border border-slate-800/80 leading-relaxed text-slate-300 flex items-start gap-2">
                      <span className="w-5 h-5 rounded-full bg-slate-800 flex items-center justify-center shrink-0 text-[10px] font-bold text-amber-400">
                        {rIdx + 1}
                      </span>
                      <p className="leading-relaxed">{reason}</p>
                    </li>
                  ))}
                </ul>
              </div>

              <div className="bg-emerald-950/30 border border-emerald-500/30 p-3.5 rounded-xl text-xs text-emerald-200 space-y-1">
                <span className="font-bold flex items-center gap-1.5 text-emerald-400">
                  <Wrench className="w-4 h-4" />
                  الحل البديل المقترح هندسياً في تطبيق أثر:
                </span>
                <p className="text-slate-300 leading-relaxed">{item.practicalSolution}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
