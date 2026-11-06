import React, { useState } from 'react';
import {
  CheckCircle2,
  XCircle,
  AlertCircle,
  Sparkles,
  Cpu,
  ShieldAlert,
  Code2,
  Clock,
  BarChart3,
  Sliders,
  HelpCircle,
  Smartphone,
  Mic,
  Scale,
  Layers,
  ChevronDown,
  ChevronUp
} from 'lucide-react';

export default function FeasibilityReport() {
  const [filter, setFilter] = useState('all'); // 'all', 'feasible', 'challenging'
  const [expandedSection, setExpandedSection] = useState(null);

  const feasibleFeatures = [
    {
      id: 'f1',
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      category: 'الحفظ والمراجعة الذكية',
      difficulty: 'منخفضة - متوسطة',
      implementationTime: '2 - 3 أسابيع',
      techStack: 'خوارزمية SM-2 أو FSRS، تخزين IndexedDB / SQLite محلي أو Cloud DB',
      reason: 'الخوارزمية رياضية معروفة ومثبتة النجاح في تطبيقات مثل Anki. تعتمد على تسجيل تقييم المستخدم أو سجل أخطائه لحساب موعد المراجعة القادمة دون متطلبات خوادم معقدة.',
      impact: 'عالية جداً: ترفع نسبة استبقاء الحفظ إلى أكثر من 85% وتمنع تفلت السور.'
    },
    {
      id: 'f2',
      title: 'الربط البصري والذهني للآيات (الخرائط الذهنية الموضوعية)',
      category: 'الحفظ والمراجعة الذكية',
      difficulty: 'منخفضة تقنياً (تتطلب تدقيق محتوى)',
      implementationTime: '1 - 2 أسبوع',
      techStack: 'هياكل بيانات JSON مرتبطة ببيانات المصحف، مكتبات عرض شجرية أو بطاقات تفاعلية',
      reason: 'تقنياً هي عبارة عن تمثيل بيانات هيكلي (Hierarchical Trees). التحدي الأكبر فيها ليس برمجياً بل بحثياً لاعتماد تقسيمات موضوعية موثوقة من كتب التفسير ومحاور السور.',
      impact: 'عالية: حل جذري للخلط بين المتشابهات والتيه في السور الطويلة كالبقرة والأنعام.'
    },
    {
      id: 'f3',
      title: 'الباحث الدلالي والنفسي (القرآن والسنة حسب حالتك)',
      category: 'المعايشة والفهم العميق',
      difficulty: 'متوسطة',
      implementationTime: '3 - 4 أسابيع',
      techStack: 'Vector Embeddings (نماذج مثل text-embedding-3-small أو Arabic BGE)، قاعدة بيانات متجهية (Pinecone / Supabase pgvector)',
      reason: 'تقنية البحث الدلالي أصبحت ناضجة جداً وسهلة الاستخدام عبر الـ Embeddings، حيث يتم تحويل مشاعر المستخدم والآيات إلى متجهات وحساب التقارب الدلالي (Cosine Similarity) بدقة فائقة.',
      impact: 'ثورية: تحول التطبيق من مجرد أرشيف نصوص إلى مرشد روحي ونفسي يومي للمسلم.'
    },
    {
      id: 'f4',
      title: 'التفسير التفاعلي والسياقي (بطاقة السياق وأسباب النزول)',
      category: 'المعايشة والفهم العميق',
      difficulty: 'منخفضة',
      implementationTime: '1 - 2 أسبوع',
      techStack: 'قواعد بيانات نصوص التفسير وأسباب النزول (تفسير ابن كثير، الطبري، والسعدي) مع واجهة Cards عصرية',
      reason: 'البيانات التاريخية وأسباب النزول متاحة ومحققة. العمل يرتكز على تصميم تجربة مستخدم (UX) رشيقة تعرض القصة القصيرة وسياق العهد المكي والمدني والمعاني الغريبة فور النقر على الآية.',
      impact: 'ممتازة: تزيد من فهم المعنى الحقيقي للآيات وسياقها بعيداً عن الفهم الخاطئ المجتزأ.'
    },
    {
      id: 'f5',
      title: 'منظم اليوم حول الصلاة (Salah-Centric Planner)',
      category: 'إنتاجية المسلم',
      difficulty: 'منخفضة - متوسطة',
      implementationTime: '2 أسبوع',
      techStack: 'مكتبات حساب المواقيت الفلكية (Adhan.js)، تقويم تفاعلي، تنبيهات محلية (Local Notifications)',
      reason: 'حساب مواقيت الصلاة عبر خطوط الطول والعرض دقيق وموثوق بمكتبات مفتوحة المصدر مجربة. يتم ربط فترات المهام ديناميكياً بدلاً من الساعات الثابتة.',
      impact: 'كبيرة: إعادة ترتيب أولويات المسلم اليومية لتكون الصلاة هي المحور وليس العكس.'
    },
    {
      id: 'f6',
      title: 'حاسبة المواريث والزكاة التفاعلية',
      category: 'المعاملات المالية والاجتماعية',
      difficulty: 'متوسطة (تتطلب دقة في القواعد الشرعية)',
      implementationTime: '2 - 3 أسابيع',
      techStack: 'محرك شجري لتوزيع الأنصبة الفقهية (حالات الحجب والتعصيب)، API مجاني لأسعار الذهب اللحظية',
      reason: 'علم الفرائض مبني على قواعد فقهية ورياضية محددة ومنطقية تماماً يمكن تحويلها إلى خوارزميات محكمة مع ربط أسعار الذهب لحساب النصاب بدقة.',
      impact: 'عملية جداً: توفير وسيلة سهلة وموثوقة لتقسيم التركات وحساب زكاة الأموال والأسهم.'
    },
    {
      id: 'f7',
      title: 'خريطة مرافق المساجد التفاعلية (Crowdsourced Map)',
      category: 'المجتمع والعمل الجماعي',
      difficulty: 'متوسطة',
      implementationTime: '3 أسابيع',
      techStack: 'Mapbox / Google Maps / Leaflet مع Firebase أو Supabase للتحديثات الفورية والتقييم',
      reason: 'تقنية الخرائط التشاركية شائعة ومباشرة التنفيذ، وتتيح للمستخدمين إضافة مرافق المسجد (كراسي كبار السن، مصلى نساء، نظافة الوضوء) بنظام مراجعات بسيط.',
      impact: 'خدمية رفيعة: حل عملي لمعاناة العائلات وكبار السن وذوي الهمم عند السفر والتنقل.'
    },
    {
      id: 'f8',
      title: 'ختمات العائلة وتحديات الأصدقاء والقصص الأخلاقية التفاعلية للأطفال',
      category: 'المجتمع وقسم الأطفال',
      difficulty: 'منخفضة - متوسطة',
      implementationTime: '2 - 3 أسابيع',
      techStack: 'Realtime Database، شجرة سيناريوهات سردية (Branching Story System)',
      reason: 'الختمات التشاركية تعتمد على مزامنة الأجزاء المقروءة، وسيناريوهات قصص الأطفال تعتمد على مسارات متعددة الخيارات (Choose-Your-Own-Adventure) التفاعلية.',
      impact: 'تربوية واجتماعية ممتازة: بناء عادة القراءة والأخلاق الحميدة للأطفال وتوثيق الصلة الأسرية.'
    }
  ];

  const challengingFeatures = [
    {
      id: 'c1',
      title: 'مصحح التلاوة الصوتي المباشر للتجويد بالذكاء الاصطناعي',
      category: 'الحفظ والمراجعة الذكية',
      difficulty: 'معقدة وصعبة جداً (High R&D Complexity)',
      techBarriers: 'معالجة الإشارات الصوتية (DSP)، نماذج ASR تجويدية خاصة، زمن استجابة منخفض (Low Latency)',
      reasons: [
        {
          head: 'تحدي النماذج الصوتية المتخصصة بأحكام التجويد:',
          body: 'نماذج التعرف الصوتي العامة (مثل Whisper أو Google STT) مصممة لفهم الكلام العادي، وليس دقة الترتيل وأزمنة المدود وحركات التشكيل اللحظية. التجويد يحتوي على تفاصيل دقيقة (مثل مقادير المد الطبيعي والمتصل والمنفصل، درجات الغنة، ترقيق وتفخيم اللام والراء، والقلقلة بمستوياتها)، وهذا يتطلب تدريب نماذج مخصصة بالكامل على عشرات آلاف الساعات الصوتية المرتلة والموسومة بدقة متناهية.'
        },
        {
          head: 'الضوضاء واختلاف المقامات واللكنات الصوتية:',
          body: 'تلاوة المستخدم في بيئة حقيقية تختلف باختلاف طبقة الصوت، سرعة الترتيل (حدر أو تدوير أو تحقيق)، والضوضاء المحيطة. هذا يولد نسبة إنذارات خاطئة مرتفعة (False Positives)، مما قد يخطّئ تلاوة صحيحة أو العكس، وهو أمر حساس جداً في كتاب الله.'
        },
        {
          head: 'التكلفة التشغيلية للخوادم السحابية (Inference Cost):',
          body: 'معالجة الصوت واستخراج الخصائص الصوتية اللحظية يتطلب خوادم GPU قوية ومكلفة جداً، أو نماذج مدمجة بالجهاز (On-Device) تستهلك الذاكرة والبطارية بشكل ملحوظ.'
        }
      ],
      recommendedAlternative: 'البديل العملي الممكن: فحص صحة الكلمات وترتيب الآيات بالصوت (Word Alignment)، مع إتاحة خيار استماع القارئ لترتيل شيخ متقن ومطابقته ذاتياً، أو استخدام التسجيل الصوتي لمراجعته من قبل معلم إلكتروني أو مجتمع الحفظ.'
    },
    {
      id: 'c2',
      title: 'الوضع الهادئ التلقائي في المسجد بالخلفية (Geofencing & Background Silent Mode)',
      category: 'إنتاجية المسلم',
      difficulty: 'شبه مستحيلة على iOS / مقيدة بشدة على Android',
      techBarriers: 'قيود أنظمة التشغيل (OS Permissions)، حظر التحكم في مستوى الصوت بالخلفية، سياسات متجر التطبيقات',
      reasons: [
        {
          head: 'قيود نظام Apple iOS الصارمة:',
          body: 'أبل تحظر تماماً وبشكل قاطع على أي تطبيق تغيير مفتاح الرنين (Mute Switch) أو تعديل إعدادات عدم الإزعاج (Do Not Disturb) أو صوت النظام في الخلفية تلقائياً دون تدخل يدوي مباشر من المستخدم عبر اختصارات Siri Shortcuts، وذلك لأسباب تتعلق بالخصوصية وتجربة الاستخدام.'
        },
        {
          head: 'قيود نظام Android الحديث ووضع Doze:',
          body: 'أنظمة Android الحديثة (Android 12+) تفرض أذونات مشددة جداً لـ (Do Not Disturb Access) وتقتل خدمات الخلفية تلقائياً لتوفير الطاقة، وتتطلب من المستخدم منح أذونات متقدمة غير اعتيادية قد ترفضها سياسات Google Play إذا لم يكن التطبيق متخصصاً في إدارة الهاتف.'
        },
        {
          head: 'استنزاف البطارية وضعف إشارة الـ GPS داخل المساجد:',
          body: 'تشغيل الـ GPS بدقة عالية في الخلفية يستنزف بطارية الهاتف سريعاً، وبمجرد دخول المسجد (مبنى خرساني كبير) تضعف إشارة الأقمار الصناعية مما يسبب تأخر تفعيل الوضع الصامت إلى ما بعد انتهاء الصلاة أو تفعيله خارج المسجد خطأً.'
        }
      ],
      recommendedAlternative: 'البديل العملي الممكن: إرسال إشعار تفاعلي سريع عند الاقتراب من المسجد أو عند حلول وقت الصلاة مع زر مباشر (ضغطة واحدة لتحويل الهاتف لوضع الصامت عبر التنبيهات المسموحة)، أو التكامل مع خاصية أتمتة التركيز (Focus Modes / Automation) في iOS و Android.'
    },
    {
      id: 'c3',
      title: 'أذكار ذكية تلقائية عبر المستشعرات (بلوتوث السيارة، التحرك، الموقع)',
      category: 'إنتاجية المسلم',
      difficulty: 'عالية (تحديات دقة وإزعاج المستخدم)',
      techBarriers: 'حظر النوافذ المنبثقة التلقائية، تداخل الأجهزة، التصنيف الخاطئ للحركة',
      reasons: [
        {
          head: 'عدم إمكانية فتح شاشة التطبيق قسراً (Screen Takeover):',
          body: 'لا تسمح أنظمة التشغيل لتطبيق في الخلفية بفتح نافذة شاشة منبثقة مفاجئة على الشاشة لقراءة الذكر إلا عن طريق إشعار دفع (Push Notification) عادي قد يتجاهله المستخدم أو يفوت وقته.'
        },
        {
          head: 'كثرة الإنذارات الخاطئة (False Triggers):',
          body: 'الاقتران بجهاز بلوتوث قد يكون سماعة رأس في المنزل وليس سيارة؛ والتحرك بسرعة في قطار أو حافلة ليس بالضرورة سفراً يستوجب دعاء السفر؛ والمرور بالسيارة بجانب مول أو مركز تجاري قد يطلق دعاء دخول السوق بشكل عشوائي مما يسبب إزعاج المستخدم.'
        }
      ],
      recommendedAlternative: 'البديل العملي الممكن: ودجت تفاعلي على الشاشة الرئيسية وشاشة القفل (Lock Screen Widget) يتغير محتواه وسياقه بذكاء وفق الوقت والنشاط، مع إتاحة وصول فوري بلمسة واحدة دون إزعاج بالإشعارات.'
    },
    {
      id: 'c4',
      title: 'ماسح العقود والمعاملات المالية وإصدار الأحكام الفقهية بالذكاء الاصطناعي',
      category: 'المعاملات المالية والشرعية',
      difficulty: 'مخاطرة قانونية وشرعية عالية جداً (High Liability & Hallucination)',
      techBarriers: 'هلوسة النماذج اللغوية (LLM Hallucinations)، تباين الفتاوى واختلاف المذاهب، المسؤولية القانونية',
      reasons: [
        {
          head: 'خطورة إصدار فتاوى ملزمة عبر الذكاء الاصطناعي (التبعة الشرعية):',
          body: 'العقود المالية معقدة وشديدة الحساسية؛ أي خطأ في تصنيف بند كربا أو تجويز معاملة باطلة يضع التطبيق في حرج شرعي وقانوني عظيم. النماذج اللغوية قد تصاب بالهلوسة أو تخلط بين المذاهب واللوائح التنظيمية الخاصة بكل بلد.'
        },
        {
          head: 'اختلاف وتنوع الصياغات القانونية المحلية:',
          body: 'بنود التمويل العقاري والتأمين تختلف بنودها بحسب قوانين البنك المركزي في كل دولة، ولا يمكن لبوت عام إعطاء حكم نهائي دون استشارة مستشار شرعي وقانوني متخصص.'
        }
      ],
      recommendedAlternative: 'البديل العملي الممكن: فحص استرشادي تحذيري (Advisory Checklist) يبرز الكلمات المفتاحية الحساسة الشائعة (مثل: "غرامة تأخير"، "زيادة مشروطة"، "فوائد متراكمة") مع التنبيه الصريح بأن الأداة للتوعية ولفت الانتباه فقط وليست فتوى شرعية معتمدة.'
    }
  ];

  const totalFeatures = feasibleFeatures.length + challengingFeatures.length;
  const feasibleCount = feasibleFeatures.length;
  const challengingCount = challengingFeatures.length;

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 border border-slate-700 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-emerald-500/5 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-emerald-500/20 text-emerald-400 rounded-2xl border border-emerald-500/30 shadow-inner">
              <Cpu className="w-7 h-7" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-2xl font-black text-white">دراسة الجدوى الفنية وتقييم الميزات</h2>
                <span className="text-xs bg-slate-800 text-slate-300 px-2 py-0.5 rounded-full border border-slate-700">
                  تحليل هندسي وتنظيمي شامل
                </span>
              </div>
              <p className="text-xs text-slate-300 mt-1">
                حساب وتصنيف الميزات القابلة للإضافة الفورية مقابل الميزات الصعبة والمعقدة مع الأسباب الهندسية والبدائل الذكية.
              </p>
            </div>
          </div>

          {/* Quick Metrics */}
          <div className="flex items-center gap-3 bg-slate-950/70 p-2.5 rounded-xl border border-slate-800 text-xs">
            <div className="text-center px-3 border-l border-slate-800">
              <span className="block text-slate-400 text-[10px]">إجمالي الميزات</span>
              <span className="text-sm font-black text-white font-mono">{totalFeatures}</span>
            </div>
            <div className="text-center px-3 border-l border-slate-800">
              <span className="block text-emerald-400 text-[10px]">قابلة للتنفيذ المباشر</span>
              <span className="text-sm font-black text-emerald-400 font-mono">{feasibleCount} ({Math.round((feasibleCount / totalFeatures) * 100)}%)</span>
            </div>
            <div className="text-center px-3">
              <span className="block text-red-400 text-[10px]">صعبة / قيود نظام</span>
              <span className="text-sm font-black text-red-400 font-mono">{challengingCount} ({Math.round((challengingCount / totalFeatures) * 100)}%)</span>
            </div>
          </div>
        </div>
      </div>

      {/* FILTER BUTTONS */}
      <div className="flex items-center gap-2 bg-slate-900/80 p-2 rounded-xl border border-slate-800">
        <button
          onClick={() => setFilter('all')}
          className={`px-4 py-2 rounded-lg text-xs font-bold transition-all ${
            filter === 'all' ? 'bg-slate-800 text-white shadow' : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          عرض جميع الميزات ({totalFeatures})
        </button>
        <button
          onClick={() => setFilter('feasible')}
          className={`px-4 py-2 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
            filter === 'feasible' ? 'bg-emerald-600 text-white shadow' : 'text-emerald-400 hover:text-emerald-300'
          }`}
        >
          <CheckCircle2 className="w-3.5 h-3.5" />
          الميزات القابلة للإضافة ({feasibleCount})
        </button>
        <button
          onClick={() => setFilter('challenging')}
          className={`px-4 py-2 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 ${
            filter === 'challenging' ? 'bg-red-600 text-white shadow' : 'text-red-400 hover:text-red-300'
          }`}
        >
          <XCircle className="w-3.5 h-3.5" />
          الميزات الصعبة والمعقدة ({challengingCount})
        </button>
      </div>

      {/* SECTION 1: FEASIBLE FEATURES */}
      {(filter === 'all' || filter === 'feasible') && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-400" />
              الميزات القابلة للإضافة والتطبيق الفوري في الإصدار الأول (Feasible Features)
            </h3>
            <span className="text-xs bg-emerald-500/10 text-emerald-300 border border-emerald-500/20 px-3 py-1 rounded-full font-semibold">
              جاهزة للتنفيذ البرمجي
            </span>
          </div>

          <p className="text-xs text-slate-300 leading-relaxed">
            هذه الميزات تعتمد على تقنيات برمجية ناضجة، خوارزميات محددة، وقواعد بيانات هيكلية لا تتعارض مع سياسات أنظمة التشغيل، مما يجعلها تحقق أعلى قيمة للمستخدم بأقل مخاطر تشغيلية.
          </p>

          <div className="grid md:grid-cols-2 gap-4 pt-2">
            {feasibleFeatures.map((item) => (
              <div
                key={item.id}
                className="bg-slate-950 p-5 rounded-xl border border-slate-800 hover:border-emerald-500/40 transition-all space-y-3 flex flex-col justify-between shadow-md group"
              >
                <div>
                  <div className="flex items-start justify-between gap-2 mb-2">
                    <span className="text-[10px] bg-slate-900 text-slate-400 px-2 py-0.5 rounded border border-slate-800 font-mono">
                      {item.category}
                    </span>
                    <span className="text-[10px] bg-emerald-500/20 text-emerald-300 px-2 py-0.5 rounded-full font-semibold border border-emerald-500/30">
                      الصعوبة: {item.difficulty}
                    </span>
                  </div>

                  <h4 className="text-sm font-bold text-emerald-300 flex items-center gap-1.5 group-hover:text-emerald-200 transition-colors">
                    <Sparkles className="w-4 h-4 text-emerald-400 shrink-0" />
                    {item.title}
                  </h4>

                  <p className="text-xs text-slate-300 mt-2 leading-relaxed">
                    {item.reason}
                  </p>
                </div>

                <div className="pt-3 border-t border-slate-900 space-y-2 text-[11px]">
                  <div className="flex items-center justify-between text-slate-400">
                    <span className="flex items-center gap-1">
                      <Code2 className="w-3.5 h-3.5 text-slate-500" />
                      التقنيات المقترحة:
                    </span>
                    <span className="text-slate-200 font-mono text-[10px]">{item.techStack}</span>
                  </div>
                  <div className="bg-emerald-950/20 p-2 rounded-lg border border-emerald-500/20 text-emerald-300/90">
                    <span className="font-bold">القيمة المضافة للمستخدم:</span> {item.impact}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* SECTION 2: CHALLENGING FEATURES */}
      {(filter === 'all' || filter === 'challenging') && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h3 className="text-lg font-bold text-red-400 flex items-center gap-2">
              <XCircle className="w-5 h-5 text-red-400" />
              الميزات الصعبة أو المعقدة تقنياً وتنظيمياً مع تحليل الأسباب والبدائل
            </h3>
            <span className="text-xs bg-red-500/10 text-red-300 border border-red-500/20 px-3 py-1 rounded-full font-semibold">
              تتطلب حذر وتدابير خاصة
            </span>
          </div>

          <p className="text-xs text-slate-300 leading-relaxed">
            هذه الميزات تواجه عوائق صلبة ناجمة عن: قيود وسياسات أمان أنظمة الهواتف (Apple iOS & Google Android)، دقة المعالجة الصوتية لتفاصيل التجويد الدقيقة، استنزاف البطارية، أو التبعات الشرعية والقانونية.
          </p>

          <div className="space-y-4 pt-2">
            {challengingFeatures.map((item) => (
              <div
                key={item.id}
                className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-4 shadow-md"
              >
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-2 border-b border-slate-800 pb-3">
                  <div className="flex items-center gap-2">
                    <div className="p-1.5 bg-red-500/20 text-red-400 rounded-lg">
                      <AlertCircle className="w-4 h-4" />
                    </div>
                    <div>
                      <h4 className="text-sm font-bold text-amber-300">
                        {item.title}
                      </h4>
                      <span className="text-[10px] text-slate-400">{item.category}</span>
                    </div>
                  </div>
                  <span className="text-[11px] bg-red-950/60 text-red-300 px-3 py-1 rounded-full border border-red-500/30 font-semibold self-start md:self-auto">
                    مستوى التحدي: {item.difficulty}
                  </span>
                </div>

                {/* Tech Barriers Box */}
                <div className="bg-slate-900/60 p-3 rounded-lg border border-slate-800 text-xs flex items-center gap-2">
                  <ShieldAlert className="w-4 h-4 text-amber-400 shrink-0" />
                  <span className="font-bold text-slate-300">العوائق التقنية:</span>
                  <span className="text-slate-400">{item.techBarriers}</span>
                </div>

                {/* Detailed Reasons */}
                <div className="space-y-2">
                  <span className="text-xs font-bold text-slate-300 block">الأسباب الهندسية والتنظيمية المفصلة:</span>
                  <div className="grid md:grid-cols-3 gap-3">
                    {item.reasons.map((reason, rIdx) => (
                      <div key={rIdx} className="bg-slate-900/90 p-3.5 rounded-lg border border-slate-800/80 text-xs space-y-1.5">
                        <span className="font-bold text-red-400 block text-[11px]">
                          {rIdx + 1}. {reason.head}
                        </span>
                        <p className="text-slate-300 text-[11px] leading-relaxed">
                          {reason.body}
                        </p>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Recommended Practical Alternative */}
                <div className="bg-emerald-950/30 border border-emerald-500/30 p-3.5 rounded-xl text-xs space-y-1">
                  <span className="font-bold text-emerald-300 flex items-center gap-1.5">
                    <Sparkles className="w-4 h-4 text-emerald-400" />
                    كيف نتغلب على هذا التحدي في تطبيق أثر؟ (الحل والبديل الهندسي الذكي):
                  </span>
                  <p className="text-slate-300 leading-relaxed text-[11px]">
                    {item.recommendedAlternative}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
