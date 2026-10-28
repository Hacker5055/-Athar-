import React from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, ShieldAlert, FileText, Check, ArrowRight } from 'lucide-react';

export default function FeasibilityReport() {
  const feasibleFeatures = [
    {
      id: 1,
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      category: 'الحفظ والمراجعة',
      reason: 'سهلة التنفيذ خوارزمياً عبر معادلات التكرار (مثل SM-2 / Anki algorithm)، مع تخزين سجل أخطاء المستخدم وحالة كل سورة محلياً أو سحابياً لتحديد الأوقات المثالية للمراجعة قبل النسيان.'
    },
    {
      id: 2,
      title: 'الربط البصري والذهني للآيات (الخرائط الذهنية)',
      category: 'الحفظ والمراجعة',
      reason: 'تعتمد على تنظيم وتصنيف المواضيع والقصص لكل سورة وتنسيقها بأسلوب بصري تفاعلي (UI/UX) يربط المتشابهات والتسلسل الموضوعي بفاعلية وبدون تعقيد تقني.'
    },
    {
      id: 3,
      title: 'الباحث الدلالي والنفسي (القرآن والسنة حسب حالتك)',
      category: 'المعايشة والفهم',
      reason: 'ممكنة بدقة عالية عبر نماذج تضمين النصوص (Text Embeddings & Vector Databases) لربط المشاعر والأحداث اليومية للمستخدم بالآيات والأحاديث والقصص النبوية وتفاسيرها.'
    },
    {
      id: 4,
      title: 'التفسير التفاعلي وبطاقة السياق',
      category: 'المعايشة والفهم',
      reason: 'تجميع بيانات أسباب النزول والمعاني الغريبة والعهد (مكي/مدني) من أمهات التفاسير وعرضها في بطاقة سياق جذابة ومبسطة للمستخدم عند النقر على أي آية.'
    },
    {
      id: 5,
      title: 'منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)',
      category: 'إنتاجية المسلم',
      reason: 'حساب مواقيت الصلاة الفلكية بدقة متناهية حسب الموقع الجغرافي أو عبر APIs موثوقة، وتقسيم قائمة المهام ديناميكياً بين الفترات (الفجر-الشروق، الظهر-العصر... إلخ).'
    },
    {
      id: 6,
      title: 'حاسبة المواريث والزكاة التفاعلية',
      category: 'المعاملات المالية',
      reason: 'تعتمد على خوارزميات حسابية وفق القواعد الفقهية الصريحة للفرائض والزكاة، مع إمكانية ربط أسعار الذهب والعملات اللحظية عبر APIs اقتصادية قياسية.'
    },
    {
      id: 7,
      title: 'خريطة مرافق المساجد التفاعلية (Crowdsourced Map)',
      category: 'المجتمع والعمل الجماعي',
      reason: 'تعتمد على خراط OpenStreetMap أو Google Maps مع قاعدة بيانات تقييمية ومشاركات المصلين لمرافق المسجد (مصلى النساء، النظافة، كبار السن، ذوي الاحتياجات الخاصة).'
    },
    {
      id: 8,
      title: 'ختمات وتحديات العائلة والقصص التفاعلية للأطفال',
      category: 'المجتمع والجيل الجديد',
      reason: 'تعتمد على برمجيات الغرف الخاصة (Private Realtime Database) مع ميزة إخفاء الأسماء لمنع الرياء، وهياكل سيناريوهات القراءة القصصية التفاعلية (Branching Storytelling).'
    }
  ];

  const challengingFeatures = [
    {
      id: 1,
      title: 'مصحح التلاوة الصوتي للتجويد المباشر (AI Tajweed Recitation Corrector)',
      category: 'الحفظ والمراجعة',
      reasons: [
        'تحدي دقة النموذج الصوتي التجويدي: قياس أحكام التجويد الدقيقة (كالمدود بالزمن بالمللي ثانية، الغنة، القلقلة، ومخارج الحروف المتقاربة كالضاد والظاء أو الطاء والتاء) يتطلب نماذج معالجة صوتية مخصصة جداً (Highly Specialized Audio Signal Processing & Speech Recognition) مدربة على آلاف الساعات الترتيلية لمختلف اللكنات والأصوات.',
        'الضوضاء واختلاف الأداء الصوتي: اختلاف نبرات صوت المستخدمين (أطفال، كبار سن، نساء) والضوضاء المحيطة قد يتسبب بإنذارات خاطئة تسبب إرباك الحافظ وتشتيته بدلاً من تصحيحه.'
      ],
      workaround: 'البديل الموصى به: الاعتماد أولاً على مطابقة الكلمات والتشكيل (Speech-to-Text Verses Matching) مع محاكاة إرشادات تجويدية للآيات وقراءات مرجعية.'
    },
    {
      id: 2,
      title: 'الوضع الهادئ التلقائي في المسجد بالخلفية (Geofencing & Silent Mode)',
      category: 'إنتاجية المسلم',
      reasons: [
        'قيود نظم التشغيل الصارمة (iOS & Android Background Restrictions): تفرض أنظمة الهواتف الحديثة قيوداً شديدة لحماية الخصوصية والبطارية تمنع التطبيقات من تغيير وضع الصوت والتعديل على إعدادات النظام تلقائياً من الخلفية دون إذن صريح في كل مرة.',
        'استهلاك البطارية ودقة الـ GPS: التتبع الدائم والمستمر لموقع المستخدم يستنزف البطارية سريعاً، كما أن إشارة الـ GPS تضعف كثيراً داخل المباني الخرسانية والمسابك مما قد يسبب خطأ في تحديد الدخول.'
      ],
      workaround: 'البديل الموصى به: إرسال إشعار تفاعلي سريع بنقرة واحدة عند الاقتراب من المسجد أو وقت الإقامة لتفعيل الوضع الصامت بنقرة واحدة.'
    },
    {
      id: 3,
      title: 'أذكار ذكية تلقائية عبر المستشعرات (Automatic Sensor-Based Athkar)',
      category: 'إنتاجية المسلم',
      reasons: [
        'قيود الوصول للمستشعرات والبلوتوث في الخلفية: تمنع أنظمة التشغيل فتح نوافذ منبثقة أو تشغيل صوتي تلقائي عند قفل الشاشة، ويقتصر ذلك على إشعارات Push Notifications.',
        'الإنذارات الخاطئة (False Positives): قد يتصل الهاتف ببلوتوث سماعة شخصية بدلاً من سيارة، أو يمر بجوار مركز تجاري دون دخوله، مما يطلق أذكاراً غير مناسبة للموقف وتسبب إزعاجاً.'
      ],
      workaround: 'البديل الموصى به: اعتماد إشعارات ذكية مجدولة ومربوطة بأحداث معينة بشرط تأكيد المستخدم أو تفاعله.'
    },
    {
      id: 4,
      title: 'ماسح العقود والمعاملات المالية الشرعي (AI Financial Contract Legal Scanner)',
      category: 'المعاملات المالية',
      reasons: [
        'المسؤولية الشرعية والقانونية: الاعتماد الكلي على الذكاء الاصطناعي لإصدار أحكام وفتاوى شرعية وبطلان عقود مالية قد يتسبب بمسؤولية قانونية وشرعية خطيرة في حال الفهم الخاطئ للبند أو السياق.',
        'تعقيد اللغويات والمصطلحات القانونية المحلية: تنوع صياغة العقود بين الدول والأنظمة القانونية المحلية يتطلب تدقيقاً بشرياً من متخصصين ولا يكفي فيه التوليد الآلي النصي.'
      ],
      workaround: 'البديل الموصى به: استخراج البنود الحساسة للتنبيه كأداة مساعدة مع إظهار إخلاء مسؤولية والتوجيه لاستشارة أهل العلم والشرع.'
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
              <h2 className="text-2xl font-bold text-white">الدراسة التقنية وتقييم الجدوى المباشرة</h2>
              <p className="text-xs text-slate-300 mt-1">
                تحليل متكامل للميزات القابلة للإضافة المباشرة والميزات الصعبة مع التوضيح التقني والتنظيمي وأفضل البدائل الهندسية.
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <div className="bg-emerald-950/60 border border-emerald-500/40 px-3.5 py-2 rounded-xl text-center">
              <span className="block text-lg font-black text-emerald-400 font-mono">{feasibleFeatures.length}</span>
              <span className="text-[10px] text-emerald-200 font-bold">ميزات عالية القابلية</span>
            </div>
            <div className="bg-amber-950/60 border border-amber-500/40 px-3.5 py-2 rounded-xl text-center">
              <span className="block text-lg font-black text-amber-400 font-mono">{challengingFeatures.length}</span>
              <span className="text-[10px] text-amber-200 font-bold">ميزات صعبة ومقيدة</span>
            </div>
          </div>
        </div>
      </div>

      {/* FEASIBLE FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2">
            <CheckCircle2 className="w-5 h-5 text-emerald-400" />
            أولاً: الميزات القابلة للإضافة والتطبيق المباشر (High Feasibility & Instant Impact)
          </h3>
          <span className="text-xs text-slate-400 bg-emerald-500/10 text-emerald-300 px-2.5 py-1 rounded-full border border-emerald-500/20">
            جاهزة للتنفيذ التفاعلي
          </span>
        </div>

        <div className="grid md:grid-cols-2 gap-4">
          {feasibleFeatures.map((item) => (
            <div key={item.id} className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-2 hover:border-emerald-500/30 transition-all">
              <div className="flex items-center justify-between">
                <h4 className="text-xs font-bold text-emerald-300 flex items-center gap-1.5">
                  <Sparkles className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                  {item.title}
                </h4>
                <span className="text-[10px] bg-slate-900 text-slate-400 px-2 py-0.5 rounded border border-slate-800">
                  {item.category}
                </span>
              </div>
              <p className="text-[11px] text-slate-300 leading-relaxed">{item.reason}</p>
            </div>
          ))}
        </div>
      </div>

      {/* CHALLENGING FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <h3 className="text-lg font-bold text-amber-400 flex items-center gap-2">
            <XCircle className="w-5 h-5 text-amber-400" />
            ثانياً: الميزات الصعبة والمعقدة تقنياً/تنظيمياً مع أسبابها والحلول البديلة
          </h3>
          <span className="text-xs text-amber-300 bg-amber-500/10 px-2.5 py-1 rounded-full border border-amber-500/20">
            تتطلب حلولاً هندسية وتصاريح خاصة
          </span>
        </div>

        <div className="space-y-4">
          {challengingFeatures.map((item) => (
            <div key={item.id} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-3">
              <div className="flex items-center justify-between border-b border-slate-800/80 pb-2">
                <h4 className="text-xs font-bold text-amber-300 flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                  {item.title}
                </h4>
                <span className="text-[10px] bg-slate-900 text-slate-400 px-2 py-0.5 rounded border border-slate-800">
                  {item.category}
                </span>
              </div>

              <div className="space-y-2">
                <span className="text-[11px] font-bold text-slate-400 block">الأسباب والتحديات الهندسية:</span>
                {item.reasons.map((reason, rIdx) => (
                  <div key={rIdx} className="bg-slate-900/70 p-3 rounded-lg border border-slate-800/80 text-xs text-slate-300 leading-relaxed">
                    <span className="font-semibold text-amber-400">التحدي {rIdx + 1}:</span> {reason}
                  </div>
                ))}
              </div>

              <div className="bg-emerald-950/30 border border-emerald-500/30 p-3 rounded-xl text-xs text-emerald-200 flex items-start gap-2">
                <ArrowRight className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                <div>
                  <span className="font-bold text-emerald-400 block mb-0.5">💡 الحل والبديل الهندسي الموصى به للتطبيق:</span>
                  <p>{item.workaround}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
