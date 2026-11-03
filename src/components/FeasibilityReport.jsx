import React, { useState } from 'react';
import {
  CheckCircle2,
  XCircle,
  AlertCircle,
  Sparkles,
  Cpu,
  Layers,
  ShieldAlert,
  Code2,
  BarChart3,
  ArrowRightCircle,
  Calendar,
  CheckSquare,
  Lightbulb
} from 'lucide-react';

export default function FeasibilityReport() {
  const [filter, setFilter] = useState('all'); // 'all', 'feasible', 'challenging'

  const feasibleFeatures = [
    {
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      category: 'الحفظ والمراجعة',
      difficulty: 'منخفضة - متوسطة',
      timeframe: '2 - 3 أسابيع',
      score: '95%',
      reason: 'سهلة التنفيذ عبر خوارزميات التكرار المتباعد المعتمدة (مثل SuperMemo SM-2 أو FSRS)، وتخزين بيانات تكرار المستخدم محلياً في IndexedDB أو مزامنتها عبر السحابة.'
    },
    {
      title: 'الربط البصري والذهني للآيات (الخرائط الذهنية الموضوعية)',
      category: 'الحفظ والمراجعة',
      difficulty: 'منخفضة',
      timeframe: '1 - 2 أسبوع',
      score: '98%',
      reason: 'هيكل بيانات وصفي معد مسبقاً (Structured JSON Metadata) يربط كل سورة بمحاورها ومقاطعها، وتصميم بصري انسيابي لا يتطلب خوارزميات معقدة.'
    },
    {
      title: 'الباحث الدلالي والنفسي للقرآن والسنة (الحالة الشعورية)',
      category: 'المعايشة والفهم',
      difficulty: 'متوسطة',
      timeframe: '3 - 4 أسابيع',
      score: '90%',
      reason: 'يمكن تطبيقه باستخدام نماذج تضمين النصوص العربية (Arabic Text Embeddings) مثل OpenAI أو Cohere مع قاعدة بيانات المتجهات (Vector DB كـ Pinecone أو Milvus) لمطابقة شعور المستخدم بالآيات.'
    },
    {
      title: 'التفسير التفاعلي وبطاقة سياق النزول والمعاني البصرية',
      category: 'المعايشة والفهم',
      difficulty: 'منخفضة',
      timeframe: '1 - 2 أسبوع',
      score: '98%',
      reason: 'الاعتماد على مصادر ومكتبات موثوقة (كـ مجمع الملك فهد، تفاسير ابن كثير والسعدي) معروضة بتصميم بطاقات تفاعلية أنيقة وتفاعلية.'
    },
    {
      title: 'منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)',
      category: 'إنتاجية المسلم',
      difficulty: 'منخفضة - متوسطة',
      timeframe: '2 أسبوع',
      score: '92%',
      reason: 'حساب مواقيت الصلاة عبر مكتبات فلكية شهيرة مثل Adhan.js، وإعادة جدولة وتوزيع المهام على الفترات (الفجر - الظهر - العصر - المغرب - العشاء) تلقائياً.'
    },
    {
      title: 'حاسبة المواريث والزكاة التفاعلية',
      category: 'المعاملات المالية',
      difficulty: 'متوسطة',
      timeframe: '3 أسابيع',
      score: '92%',
      reason: 'قواعد فقه المواريث والزكاة مبنية على معادلات حسابية واضحة المعالم، مع ربط سعر الذهب بـ API مجاني أو مدفوع لحساب النصاب اللحظي بدقة.'
    },
    {
      title: 'خريطة مرافق المساجد التشاركية (Crowdsourced Mosque Map)',
      category: 'المجتمع والجيل الجديد',
      difficulty: 'متوسطة',
      timeframe: '3 - 4 أسابيع',
      score: '88%',
      reason: 'استخدام خرائط Mapbox أو Google Maps مع قاعدة بيانات مجتمعية لحفظ التقييمات وتفاصيل المصليات وتوفير الكراسي والمداخل.'
    },
    {
      title: 'ختمات وتحديات العائلة والأصدقاء (Private Groups)',
      category: 'المجتمع والجيل الجديد',
      difficulty: 'متوسطة',
      timeframe: '2 - 3 أسابيع',
      score: '94%',
      reason: 'غرف خاصة مبنية على خدمات التزامن الفوري (مثل Firebase Realtime Database أو Supabase) مع نظام خصوصية لإخفاء الأسماء ومنع الرياء.'
    },
    {
      title: 'القصص التفاعلية للأطفال والناشئة (اختر مسارك بأخلاقك)',
      category: 'المجتمع والجيل الجديد',
      difficulty: 'منخفضة - متوسطة',
      timeframe: '2 أسبوع',
      score: '96%',
      reason: 'نظام سيناريوهات تفاعلية شجرية (Branching Story System) ومحتوى تربوي هادف وسهل الصياغة البرمجية.'
    }
  ];

  const challengingFeatures = [
    {
      title: 'مصحح التلاوة الصوتي للتجويد المباشر (AI Tajweed Recitation Corrector)',
      category: 'الحفظ والمراجعة',
      difficulty: 'شديدة التعقيد',
      feasibilityPercentage: '35%',
      reasons: [
        'تحدي دقة النموذج الصوتي التجويدي: قياس أحكام التجويد بدقة متناهية (مثل مقدار المدود بالحركات، الغنة، الإقلاب، القلقلة، ومخارج الحروف الصوتية المتشابهة كالضاد والظاء والطاء) يتطلب نماذج معالجة إشارات صوتية (Audio Signal Processing & Phoneme Recognition) مدربة على مئات الآلاف من ساعات الترتيل.',
        'تنوع اللكنات ونبرات الصوت وضوضاء البيئة: القراء يختلفون في النبرات والأعمار واللهجات، ووجود ضوضاء محيطة يؤدي لنسبة خطأ وإنذارات كاذبة (False Positives) قد تؤدي لتشتيت الحافظ.',
        'تكلفة الخوادم والمعالجة اللحظية: معالجة الصوت في خوادم GPU سريعة بزمن استجابة أقل من 500ms مكلفة جداً مالياً على مستوى التطبيقات الكبيرة.'
      ],
      recommendedAlternative: 'البديل العملي الموصى به: البدء بنموذج يتعرف على صحة الكلمات والتشكيل الأساسي (STT) مع تنبيهات توجيهية، وتقديم تقييم الأحكام المتقدمة بمساعدة مشرفين بشريين أو نماذج مخففة.'
    },
    {
      title: 'الوضع الهادئ التلقائي في المسجد بالخلفية (Geofencing & Background Silent Mode)',
      category: 'إنتاجية المسلم',
      difficulty: 'صعبة جداً (قيود أنظمة التشغيل)',
      feasibilityPercentage: '25%',
      reasons: [
        'قيود نظامي iOS و Android الصارمة: تمنع أنظمة التشغيل الحديثة (خاصة Apple iOS) التطبيقات تماماً من الوصول الصامت وتعديل إعدادات الرنين وكتم الصوت (Do Not Disturb / Silent Ring Switch) تلقائياً من الخلفية دون تفاعل يدوي للمستخدم لحماية الخصوصية.',
        'استنزاف البطارية والموقع الخفي: تتبع الموقع المستمر بدقة فائقة للدخول إلى مبنى المسجد يستنزف البطارية سريعاً وقد يؤدي لحذف التطبيق من قبل المستخدمين.',
        'ضعف إشارات GPS داخل المساجد الخرسانية الكبيرة، مما يؤخر التفعيل أو يؤدي لعدم اكتشاف الدخول.'
      ],
      recommendedAlternative: 'البديل العملي الموصى به: إرسال إشعار تفاعلي فوري (Interactive Rich Notification) عند اقتراب وقت الإقامة بنقرة واحدة لتحويل الهاتف لوضع التركيز، أو التكامل مع اختصارات Siri Shortcuts و Android Quick Settings.'
    },
    {
      title: 'أذكار ذكية تلقائية عبر مستشعرات الهاتف (Automatic Sensor & Context Athkar)',
      category: 'إنتاجية المسلم',
      difficulty: 'صعبة بسبب القيود والإنذارات الخاطئة',
      feasibilityPercentage: '45%',
      reasons: [
        'قيود الخلفية ونوافذ القفل: لا تسمح أنظمة الهواتف بفتح شاشة التطبيق فجأة أو تشغيل صوت مباشر عند ركوب السيارة أو دخول السوق إلا عبر إشعار عادي، مما يفقد الميزة بريقها الانسيابي.',
        'كثرة الإنذارات الكاذبة: اقتران الهاتف بسماعة بلوتوث منزلية قد يظنه التطبيق سيارة، أو المرور بالسيارة بجانب مركز تجاري قد يطلق دعاء دخول السوق بشكل خاطئ ومزعج.'
      ],
      recommendedAlternative: 'البديل العملي الموصى به: ربط الأذكار بأوقات الصلوات والجدول اليومي مع إتاحة ودجات سريعة (Widgets) على شاشة القفل بلمسة واحدة.'
    },
    {
      title: 'ماسح العقود والمعاملات المالية الشرعي الشامل (AI Shariah Legal Scanner)',
      category: 'المعاملات المالية',
      difficulty: 'عالية بسبب المسؤولية الفقهية والقانونية',
      feasibilityPercentage: '50%',
      reasons: [
        'المسؤولية الشرعية والقانونية: إصدار أحكام قطعية بالتحليل أو التحريم على عقود مالية معقدة عبر الذكاء الاصطناعي ينطوي على مخاطر شرعية كبرى في حال الخطأ أو سوء فهم صيغة قانونية محلية.',
        'اختلاف المذاهب والاجتهادات المعاصرة: بعض الشروط قد تكون محل خلاف بين المجامع الفقهية (كمجمع الفقه الإسلامي الدولي وهيئة المعايير الشرعية AAOIFI)، مما يصعب حسمه برمجياً بنموذج لغوي فقط.'
      ],
      recommendedAlternative: 'البديل العملي الموصى به: توضيح أن التطبيق "أداة إرشادية وتنبيهية للشبهات الشائعة" مع زر طلب استشارة موثقة من باحث شرعي معتمد.'
    }
  ];

  const totalFeatures = feasibleFeatures.length + challengingFeatures.length;
  const feasibleCount = feasibleFeatures.length;
  const challengingCount = challengingFeatures.length;
  const feasibleRate = Math.round((feasibleCount / totalFeatures) * 100);

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 border border-slate-700/80 rounded-3xl p-6 md:p-8 shadow-2xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-80 h-80 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 relative z-10">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-bold border border-emerald-500/30">
              <Cpu className="w-3.5 h-3.5" />
              دراسة الجدوى الفنية والهندسية الشاملة
            </div>
            <h2 className="text-2xl md:text-3xl font-extrabold text-white tracking-tight">
              تقييم الميزات المقترحة لتطبيق "أثَر"
            </h2>
            <p className="text-sm text-slate-300 max-w-2xl leading-relaxed">
              حساب دقيق ونقدي للميزات القابلة للإضافة والتنفيذ الفوري مقابل الميزات الصعبة أو المقيدة تقنياً بأنظمة التشغيل والمعايير الشرعية مع توضيح الأسباب والبدائل.
            </p>
          </div>

          {/* Quick Metrics */}
          <div className="flex items-center gap-3">
            <div className="bg-slate-950/80 border border-emerald-500/30 rounded-2xl p-4 text-center min-w-[110px] shadow-lg">
              <span className="text-2xl font-black text-emerald-400 font-mono">{feasibleCount}</span>
              <span className="block text-[11px] text-slate-400 mt-0.5">ميزات قابلة للتنفيذ</span>
            </div>
            <div className="bg-slate-950/80 border border-red-500/30 rounded-2xl p-4 text-center min-w-[110px] shadow-lg">
              <span className="text-2xl font-black text-red-400 font-mono">{challengingCount}</span>
              <span className="block text-[11px] text-slate-400 mt-0.5">ميزات صعبة/مقيدة</span>
            </div>
            <div className="bg-slate-950/80 border border-blue-500/30 rounded-2xl p-4 text-center min-w-[110px] shadow-lg">
              <span className="text-2xl font-black text-blue-400 font-mono">{feasibleRate}%</span>
              <span className="block text-[11px] text-slate-400 mt-0.5">نسبة قابلية التحقيق</span>
            </div>
          </div>
        </div>
      </div>

      {/* FILTER BUTTONS */}
      <div className="flex items-center gap-2 pb-1 border-b border-slate-800">
        <button
          onClick={() => setFilter('all')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            filter === 'all'
              ? 'bg-slate-800 text-white border border-slate-600 shadow'
              : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          كافة الميزات ({totalFeatures})
        </button>
        <button
          onClick={() => setFilter('feasible')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            filter === 'feasible'
              ? 'bg-emerald-600/90 text-white shadow-lg shadow-emerald-950'
              : 'text-emerald-400 hover:bg-emerald-950/30'
          }`}
        >
          <CheckCircle2 className="w-3.5 h-3.5" />
          الميزات القابلة للإضافة المباشرة ({feasibleCount})
        </button>
        <button
          onClick={() => setFilter('challenging')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            filter === 'challenging'
              ? 'bg-red-600/90 text-white shadow-lg shadow-red-950'
              : 'text-red-400 hover:bg-red-950/30'
          }`}
        >
          <XCircle className="w-3.5 h-3.5" />
          الميزات الصعبة والمقيدة تقنياً ({challengingCount})
        </button>
      </div>

      {/* FEASIBLE FEATURES SECTION */}
      {(filter === 'all' || filter === 'feasible') && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-5">
          <div className="flex items-center justify-between border-b border-slate-800 pb-4">
            <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-400" />
              أولاً: الميزات القابلة للإضافة والتطبيق الفوري في النسخة الحالية (High Feasibility)
            </h3>
            <span className="text-xs bg-emerald-500/10 text-emerald-300 px-3 py-1 rounded-full border border-emerald-500/20 font-semibold">
              مجموع {feasibleCount} ميزات جاهزة للبناء
            </span>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
            {feasibleFeatures.map((item, idx) => (
              <div
                key={idx}
                className="bg-slate-950 p-5 rounded-2xl border border-slate-800 hover:border-emerald-500/40 transition-all flex flex-col justify-between space-y-3 group"
              >
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-[10px] font-bold text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20">
                      {item.category}
                    </span>
                    <span className="text-[10px] text-slate-400 font-mono">نجاح: {item.score}</span>
                  </div>
                  <h4 className="text-xs font-bold text-slate-100 group-hover:text-emerald-300 transition-colors leading-snug">
                    {item.title}
                  </h4>
                  <p className="text-[11px] text-slate-400 mt-2 leading-relaxed">
                    {item.reason}
                  </p>
                </div>

                <div className="pt-2 border-t border-slate-900 flex items-center justify-between text-[10px] text-slate-500">
                  <span>الصعوبة: <strong className="text-slate-300 font-semibold">{item.difficulty}</strong></span>
                  <span>المدة: <strong className="text-emerald-400 font-semibold">{item.timeframe}</strong></span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* CHALLENGING FEATURES SECTION */}
      {(filter === 'all' || filter === 'challenging') && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex items-center justify-between border-b border-slate-800 pb-4">
            <h3 className="text-lg font-bold text-red-400 flex items-center gap-2">
              <XCircle className="w-5 h-5 text-red-400" />
              ثانياً: الميزات الصعبة أو المقيدة تقنياً وتنظيمياً مع تحليل الأسباب الهندسية
            </h3>
            <span className="text-xs bg-red-500/10 text-red-300 px-3 py-1 rounded-full border border-red-500/20 font-semibold">
              {challengingCount} ميزات تتطلب معالجات بديلة
            </span>
          </div>

          <div className="space-y-5">
            {challengingFeatures.map((item, idx) => (
              <div key={idx} className="bg-slate-950 p-6 rounded-2xl border border-slate-800 space-y-4">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-900 pb-3">
                  <div className="flex items-center gap-2">
                    <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                    <h4 className="text-sm font-bold text-slate-100">{item.title}</h4>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] bg-slate-900 text-slate-400 px-2.5 py-1 rounded-lg border border-slate-800">
                      تصنيف: {item.category}
                    </span>
                    <span className="text-[10px] bg-red-500/20 text-red-300 px-2.5 py-1 rounded-lg border border-red-500/30 font-bold">
                      {item.difficulty}
                    </span>
                  </div>
                </div>

                <div className="space-y-2">
                  <span className="text-xs font-bold text-amber-300 block">الأسباب الهندسية والتنظيمية:</span>
                  <div className="space-y-2">
                    {item.reasons.map((reason, rIdx) => (
                      <div key={rIdx} className="bg-slate-900/70 p-3 rounded-xl border border-slate-800 text-xs text-slate-300 leading-relaxed flex items-start gap-2.5">
                        <span className="w-5 h-5 rounded-full bg-slate-800 text-slate-400 text-[10px] font-bold flex items-center justify-center shrink-0 mt-0.5">
                          {rIdx + 1}
                        </span>
                        <p>{reason}</p>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Recommended Alternative */}
                <div className="bg-emerald-950/20 border border-emerald-500/30 p-3.5 rounded-xl text-xs text-emerald-300 flex items-start gap-2">
                  <Lightbulb className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                  <div>
                    <span className="font-bold block mb-0.5">الحل الهندسي والبديل الذكي المقترح لتطبيق أثر:</span>
                    <p className="text-emerald-200/90 leading-relaxed">{item.recommendedAlternative}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* EXECUTION ROADMAP MATRIX */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-4">
        <h3 className="text-base font-bold text-slate-100 flex items-center gap-2 border-b border-slate-800 pb-3">
          <Calendar className="w-4 h-4 text-emerald-400" />
          خطة الإطلاق ومراحل التطوير الموصى بها (Implementation Roadmap)
        </h3>

        <div className="grid md:grid-cols-3 gap-4 text-xs">
          <div className="bg-slate-950 p-4 rounded-2xl border border-slate-800 space-y-2">
            <span className="px-2.5 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 text-[10px] font-bold inline-block">
              المرحلة الأولى: MVP السريع
            </span>
            <h4 className="font-bold text-slate-200">الإنتاجية والفهم العميق</h4>
            <ul className="text-slate-400 space-y-1 list-disc list-inside text-[11px] leading-relaxed">
              <li>منظم الصلوات الخمس مع قائمة المهام</li>
              <li>الباحث الشعوري والدلالي للقرآن</li>
              <li>بطاقات سياق النزول والمعاني البصرية</li>
              <li>الخرائط الذهنية وتكرار المراجعة</li>
            </ul>
          </div>

          <div className="bg-slate-950 p-4 rounded-2xl border border-slate-800 space-y-2">
            <span className="px-2.5 py-0.5 rounded-full bg-blue-500/20 text-blue-300 text-[10px] font-bold inline-block">
              المرحلة الثانية: التفاعل والمجتمع
            </span>
            <h4 className="font-bold text-slate-200">المعاملات والمشاركة</h4>
            <ul className="text-slate-400 space-y-1 list-disc list-inside text-[11px] leading-relaxed">
              <li>حاسبة الزكاة وربط الذهب اللحظي</li>
              <li>حاسبة المواريث البصرية</li>
              <li>خريطة مرافق المساجد التشاركية</li>
              <li>غرف الختمات وقصص الأطفال التفاعلية</li>
            </ul>
          </div>

          <div className="bg-slate-950 p-4 rounded-2xl border border-slate-800 space-y-2">
            <span className="px-2.5 py-0.5 rounded-full bg-purple-500/20 text-purple-300 text-[10px] font-bold inline-block">
              المرحلة الثالثة: الذكاء الاصطناعي المتقدم
            </span>
            <h4 className="font-bold text-slate-200">الأتمتة والذكاء العميق</h4>
            <ul className="text-slate-400 space-y-1 list-disc list-inside text-[11px] leading-relaxed">
              <li>المصحح الصوتي الهجين (تدريب نماذج تجويد مخصصة)</li>
              <li>ماسح العقود الشرعي مع مراجعة هيئات معتمدة</li>
              <li>تكامل إشعارات الحساسات الذكية (Siri / Quick Tiles)</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
