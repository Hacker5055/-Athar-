import React, { useState } from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, ShieldAlert, ArrowRightLeft, Layers, Wrench, Info } from 'lucide-react';

export default function FeasibilityReport() {
  const [filter, setFilter] = useState('all'); // 'all', 'feasible', 'challenging'

  const feasibleFeatures = [
    {
      id: 'f1',
      category: 'الحفظ والمراجعة',
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      feasibility: 'عالية جداً (95%)',
      reason: 'سهلة التنفيذ عبر خوارزميات التكرار المنطقية (مثل خوارزمية SM-2 المستخدمة في Anki)، وتخزين بيانات حفظ المستخدم وعدد الأخطاء محلياً (IndexedDB/LocalStorage) أو سحابياً.',
      impact: 'تحسين تثبيت الحفظ بنسبة تتجاوز 40% وتوفير وقت المراجعين.'
    },
    {
      id: 'f2',
      category: 'الحفظ والمراجعة',
      title: 'الربط البصري والذهني للآيات (خرائط ذهنية للقرآن)',
      feasibility: 'عالية جداً (90%)',
      reason: 'تعتمد على تصنيف وتجزئة موضوعية لآيات السور مسبقاً في قاعدة بيانات مهيكلة، مع واجهة تفاعلية شجرية جذابة، وهي خفيفة الأداء ولا تتطلب خوادم مكلفة.',
      impact: 'مساعدة الحفاظ على التمييز بين المتشابهات ورؤية سياق القصة كاملاً.'
    },
    {
      id: 'f3',
      category: 'المعايشة والفهم',
      title: 'الباحث الدلالي والنفسي (القرآن والسنة حسب حالتك)',
      feasibility: 'عالية (85%)',
      reason: 'تطبيق مباشر باستخدام تقنيات تضمين النصوص الدلالية (Text Embeddings & Vector Search) عبر نماذج لغوية مفتوحة المصدر أو APIs لربط المعنى العاطفي والشعوري بآيات القرآن والأحاديث الصحيحة دون الاعتماد على الكلمات الحرفية.',
      impact: 'جعل القرآن منهج حياة ومواسياً للمستخدم في مشاعره اليومية.'
    },
    {
      id: 'f4',
      category: 'المعايشة والفهم',
      title: 'التفسير التفاعلي والسياقي (بطاقة السياق)',
      feasibility: 'عالية جداً (95%)',
      reason: 'تجهيز هيكل بيانات منظم يربط كل آية ببياناتها التاريخية (مكي/مدني، ترتيب النزول، سبب النزول كقصة قصيرة، وغريب الكلمات)، واستدعاؤها بسلاسة وفورية عند النقر.',
      impact: 'تعميق فهم المتدبر وسرعة استيعاب معاني الآيات وأسبابها.'
    },
    {
      id: 'f5',
      category: 'إنتاجية المسلم',
      title: 'منظم اليوم حول الصلاة (Salah-Centric Planner)',
      feasibility: 'عالية (90%)',
      reason: 'حساب مواقيت الصلاة عبر معادلات فلكية دقيقة (أو عبر مكتبات مفتوحة المصدر كـ Adhan library) محلياً بدون إنترنت، وربط مهام المستخدم بفترات الصلوات ديناميكياً بدلاً من الساعات الجامدة.',
      impact: 'إعادة ترتيب أولويات المسلم وجعل الصلاة هي محور تنظيم اليوم الحقيقي.'
    },
    {
      id: 'f6',
      category: 'المعاملات المالية',
      title: 'حاسبة المواريث والزكاة التفاعلية',
      feasibility: 'عالية جداً (95%)',
      reason: 'خوارزميات فقهية ورياضية محددة بدقة، مع ربط اختياري بأسعار الذهب اللحظية عبر APIs مجانية أو إدخال يدوي. قواعد تقسيم الميراث في الشريعة محكمة وقابلة للبرمجة المنطقية التامة.',
      impact: 'تسهيل حساب الواجبات الشرعية المعقدة كزكاة الأسهم وتقسيم التركات بشفافية تامة.'
    },
    {
      id: 'f7',
      category: 'المجتمع والعمل الجماعي',
      title: 'خريطة مرافق المساجد التشاركية (Crowdsourced Map)',
      feasibility: 'عالية (85%)',
      reason: 'تعتمد على واجهات الخرائط (OpenStreetMap / Google Maps) مع نظام مراجعات وتقييمات من المجتمع (Crowdsourcing) يحدد توفر مصلى سيدات، نظافة، ومدخل لذوي الاحتياجات الخاصة.',
      impact: 'خدمة كبار السن، المسافرين، والنساء وتوفير معلومات موثوقة عن بيوت الله.'
    },
    {
      id: 'f8',
      category: 'المجتمع والجيل الجديد',
      title: 'ختمات العائلة والقصص الأخلاقية التفاعلية للأطفال',
      feasibility: 'عالية جداً (90%)',
      reason: 'الختمات تعتمد على قواعد بيانات آنية (Realtime DB) مع ميزة حجب الأسماء لمنع الرياء، والقصص التفاعلية تعتمد على سيناريوهات شجرية متفرعة (Branching Decision Trees) سهلة التطوير وممتعة.',
      impact: 'تعزيز الأثر التربوي وغرس الأخلاق الإسلامية بروح المنافسة والخصوصية.'
    }
  ];

  const challengingFeatures = [
    {
      id: 'c1',
      category: 'الحفظ والمراجعة',
      title: 'مصحح التلاوة الصوتي المباشر للتجويد ومخارج الحروف',
      feasibility: 'صعبة ومعقدة تقنياً (35%)',
      technicalReasons: [
        'أحكام التجويد الدقيقة (مقادير المدود 2-4-6 حركات، مراتب الغنة، تفخيم وترقيق الراء، القلقلة، ومخارج الحروف المتقاربة كالضاد والظاء والطاء والتاء) تتطلب نماذج صوتية ذكاء اصطناعي مخصصة للغاية (Fine-tuned Audio Signal Processing & Speech Recognition) غير متوفرة في النماذج الجاهزة الشائعة (مثل Whisper أو Google STT).',
        'تأثير بيئة التسجيل وتنوع اللهجات وأعمار القراء (أطفال، كبار سن، لهجات متعددة) والضوضاء المحيطة ينتج عنه نسبة إنذارات خاطئة (False Positives) تشتت الحافظ وتضعف الثقة بالتطبيق.',
        'تكلفة المعالجة اللحظية العالية وزمن الاستجابة (Latency)؛ فتحليل الصوت عبر خوادم AI سحابية لحظة بلحظة يرفع فاتورة الاستضافة ويتطلب سرعة إنترنت عالية.'
      ],
      practicalAlternative: 'البديل الهندسي العملي: البدء أولاً بـ "مصحح الكلمات والتشكيل والحفظ النصي" (Word-Level ASR)، ومقارنة تلاوة المستخدم بمقاطع مشايخ مسجلة مقسمة آية بآية، مع تقديم تدريب سمعي تفاعلي لكل حكم تجويدي.'
    },
    {
      id: 'c2',
      category: 'إنتاجية المسلم',
      title: 'الوضع الهادئ التلقائي في المسجد بالخلفية (Automatic Geofencing Silence)',
      feasibility: 'صعبة تنظيمياً ونظامياً (30%)',
      technicalReasons: [
        'قيود أنظمة التشغيل الصارمة (iOS & Android): يمنع نظام iOS نهائياً التطبيقات الخارجية من كتم صوت الهاتف أو تغيير وضع الرنين (Silent Mode / Do Not Disturb) دون تحكم مباشر من المستخدم لحماية الخصوصية ومنع التطبيقات من تعطيل تنبيهات الطوارئ.',
        'في نظام أندرويد يتطلب الأمر إذن نظام خطير وحساس (Do Not Disturb Access) يُربك المستخدمين وترفضه متاجر التطبيقات أحياناً إن لم يكن التطبيق مخصصاً لإدارة النظام.',
        'استهلاك البطارية وضعف GPS: تتبع الموقع في الخلفية المستمر بدقة Geofence عالية يستنزف بطارية الهاتف سريعاً، فضلاً عن ضعف إشارة الأقمار الصناعية داخل المساجد الخرسانية والمصليات تحت الأرض.'
      ],
      practicalAlternative: 'البديل الهندسي العملي: إرسال إشعار تفاعلي ذكي فوري عند دخول النطاق أو قبل الإقامة بزر واحد بنقرة سريعة (Actionable Notification) لتفعيل الصامت، أو التنسيق مع ميزة "تركيز الصلاة" (Focus Mode / Shortcuts) في iOS وأندرويد.'
    },
    {
      id: 'c3',
      category: 'إنتاجية المسلم',
      title: 'أذكار ذكية تلقائية عبر المستشعرات (Automatic Sensor Athkar)',
      feasibility: 'متوسطة الصعوبة مع قيود تجربة الاستخدام (50%)',
      technicalReasons: [
        'قيود أنظمة التشغيل على البلوتوث والمستشعرات بالخلفية: تفرض آبل وجوجل قيوداً على الفحص المستمر للبلوتوث والموقع وسرعة الحركة بالخلفية لتوفير الطاقة والخصوصية.',
        'تداخل الإشارات والأخطاء السلوكية: اقتران الهاتف بسماعة رأس قد يفسره التطبيق كسيارة، أو المرور بجوار سوق تجاري في طريق سريع قد يطلق دعاء دخول السوق بشكل خاطئ ومزعج.'
      ],
      practicalAlternative: 'البديل الهندسي العملي: ربط الميزة بويدجت شاشة القفل التفاعلي، واختصارات النظام (Shortcuts / Routine Automation)، أو بطاقات أذكار السياق التفاعلية المعروضة داخل التطبيق.'
    },
    {
      id: 'c4',
      category: 'المعاملات المالية',
      title: 'ماسح العقود الشرعي الذكي (AI Financial Contract Legal Scanner)',
      feasibility: 'صعبة شرعياً وقانونياً (40%)',
      technicalReasons: [
        'المسؤولية الشرعية والقانونية الحساسة: أي خطأ في تحليل عقد بنكي أو تمويلي قد يوقع المستخدم في معاملة ربوية أو يترتب عليه مسؤولية مالية جسيمة مع عدم وجود جهة إفتاء مرخصة تعتمد أحكام الذكاء الاصطناعي.',
        'تعقيد المصطلحات القانونية المحلية: تختلف بنود العقود وصياغتها من دولة لأخرى وبحسب المذاهب الفقهية، والذكاء الاصطناعي التوليدي قد يعاني من الهلوسة الفقهية (Legal/Fiqh Hallucinations).'
      ],
      practicalAlternative: 'البديل الهندسي العملي: جعل الميزة "مساعد استرشادي وتثقيفي للشروط المشبوهة الشائعة" (مع تنبيه إخلاء مسؤولية صريح ومطالبة المستخدم باستشارة جهة إفتاء معتمدة)، واقتصار الفحص على البنود المتفق عليها كغرامات التأخير الصريحة والفوائد المركبة.'
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 border border-slate-700 rounded-2xl p-6 shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-emerald-500/20 text-emerald-400 rounded-xl border border-emerald-500/30">
              <Cpu className="w-7 h-7" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-2xl font-bold text-white">دراسة الجدوى الفنية والتقييم الشامل</h2>
                <span className="bg-emerald-500/20 text-emerald-300 text-xs px-2.5 py-0.5 rounded-full border border-emerald-500/30 font-bold">
                  تحليل 10 ميزات
                </span>
              </div>
              <p className="text-xs text-slate-300 mt-1">
                حساب تفصيلي للميزات القابلة للإضافة الفورية والميزات الصعبة مع توضيح العوائق التقنية والتنظيمية والبدائل الهندسية.
              </p>
            </div>
          </div>

          <div className="flex items-center gap-1.5 bg-slate-950 p-1.5 rounded-xl border border-slate-800 text-xs">
            <button
              onClick={() => setFilter('all')}
              className={`px-3 py-1.5 rounded-lg font-medium transition-all ${filter === 'all' ? 'bg-emerald-600 text-white' : 'text-slate-400 hover:text-slate-200'}`}
            >
              الكل (10)
            </button>
            <button
              onClick={() => setFilter('feasible')}
              className={`px-3 py-1.5 rounded-lg font-medium transition-all ${filter === 'feasible' ? 'bg-emerald-600 text-white' : 'text-slate-400 hover:text-slate-200'}`}
            >
              القابلة للإضافة (6)
            </button>
            <button
              onClick={() => setFilter('challenging')}
              className={`px-3 py-1.5 rounded-lg font-medium transition-all ${filter === 'challenging' ? 'bg-red-600 text-white' : 'text-slate-400 hover:text-slate-200'}`}
            >
              الصعبة والعوائق (4)
            </button>
          </div>
        </div>
      </div>

      {/* SUMMARY MATRIX METRICS */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-slate-900/80 p-4 rounded-xl border border-emerald-500/30 space-y-1">
          <span className="text-xs text-emerald-400 font-bold flex items-center gap-1">
            <CheckCircle2 className="w-4 h-4" />
            الميزات القابلة للإضافة
          </span>
          <p className="text-2xl font-black text-white">6 ميزات رئيسية</p>
          <span className="text-[10px] text-slate-400">تطبيق مباشر وسريع وقيمة عالية</span>
        </div>

        <div className="bg-slate-900/80 p-4 rounded-xl border border-red-500/30 space-y-1">
          <span className="text-xs text-red-400 font-bold flex items-center gap-1">
            <XCircle className="w-4 h-4" />
            الميزات الصعبة تقنياً/تنظيمياً
          </span>
          <p className="text-2xl font-black text-white">4 ميزات دقيقة</p>
          <span className="text-[10px] text-slate-400">تحتاج حلولاً وبدائل ذكية</span>
        </div>

        <div className="bg-slate-900/80 p-4 rounded-xl border border-blue-500/30 space-y-1">
          <span className="text-xs text-blue-400 font-bold flex items-center gap-1">
            <Layers className="w-4 h-4" />
            البنية التحتية المطلوبة
          </span>
          <p className="text-2xl font-black text-white">Web & Mobile</p>
          <span className="text-[10px] text-slate-400">React + AI APIs + GPS</span>
        </div>

        <div className="bg-slate-900/80 p-4 rounded-xl border border-amber-500/30 space-y-1">
          <span className="text-xs text-amber-400 font-bold flex items-center gap-1">
            <ShieldAlert className="w-4 h-4" />
            مستوى الأمان والموثوقية
          </span>
          <p className="text-2xl font-black text-white">100% بدون رياء</p>
          <span className="text-[10px] text-slate-400">حماية الخصوصية وموثوقية شرعية</span>
        </div>
      </div>

      {/* FEASIBLE FEATURES SECTION */}
      {(filter === 'all' || filter === 'feasible') && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-400" />
              أولاً: الميزات القابلة للإضافة والتطبيق العملي السلس (High Feasibility)
            </h3>
            <span className="text-xs bg-emerald-500/20 text-emerald-300 px-3 py-1 rounded-full border border-emerald-500/30 font-semibold">
              جاهزة للتنفيذ المباشر
            </span>
          </div>

          <div className="grid md:grid-cols-2 gap-4">
            {feasibleFeatures.map((item) => (
              <div key={item.id} className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-2 hover:border-emerald-500/40 transition-all flex flex-col justify-between">
                <div>
                  <div className="flex justify-between items-start gap-2 mb-2">
                    <span className="text-[10px] bg-slate-900 text-slate-400 px-2 py-0.5 rounded border border-slate-800">
                      {item.category}
                    </span>
                    <span className="text-xs font-bold text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded border border-emerald-500/20">
                      {item.feasibility}
                    </span>
                  </div>
                  <h4 className="text-xs md:text-sm font-bold text-white flex items-center gap-1.5">
                    <Sparkles className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                    {item.title}
                  </h4>
                  <p className="text-[11px] text-slate-400 leading-relaxed mt-2">{item.reason}</p>
                </div>
                <div className="pt-2 border-t border-slate-900 text-[11px] text-emerald-300/90 font-medium">
                  🎯 الأثر: {item.impact}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* CHALLENGING FEATURES SECTION */}
      {(filter === 'all' || filter === 'challenging') && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h3 className="text-lg font-bold text-red-400 flex items-center gap-2">
              <XCircle className="w-5 h-5 text-red-400" />
              ثانياً: الميزات الصعبة أو المعقدة تقنياً وتنظيمياً مع الأسباب والبدائل
            </h3>
            <span className="text-xs bg-red-500/20 text-red-300 px-3 py-1 rounded-full border border-red-500/30 font-semibold">
              تتطلب حلولاً بديلة
            </span>
          </div>

          <div className="space-y-4">
            {challengingFeatures.map((item) => (
              <div key={item.id} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-3">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-900 pb-2">
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] bg-slate-900 text-slate-400 px-2 py-0.5 rounded border border-slate-800">
                      {item.category}
                    </span>
                    <h4 className="text-xs md:text-sm font-bold text-amber-300 flex items-center gap-1.5">
                      <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                      {item.title}
                    </h4>
                  </div>
                  <span className="text-xs font-bold text-red-400 bg-red-500/10 px-2.5 py-1 rounded-full border border-red-500/20 shrink-0">
                    {item.feasibility}
                  </span>
                </div>

                <div className="space-y-2">
                  <span className="text-[11px] font-bold text-slate-300 block">العوائق التقنية والتنظيمية:</span>
                  <ul className="space-y-1.5 text-xs text-slate-300">
                    {item.technicalReasons.map((reason, rIdx) => (
                      <li key={rIdx} className="bg-slate-900/80 p-2.5 rounded-lg border border-slate-800/80 leading-relaxed text-slate-300 flex items-start gap-2">
                        <span className="text-amber-400 font-bold mt-0.5 shrink-0">#{rIdx + 1}</span>
                        <span>{reason}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                <div className="bg-emerald-950/30 border border-emerald-500/30 p-3 rounded-lg text-xs space-y-1">
                  <div className="flex items-center gap-1.5 text-emerald-400 font-bold">
                    <Wrench className="w-3.5 h-3.5" />
                    <span>الحل الهندسي والبديل الذكي المقترح:</span>
                  </div>
                  <p className="text-slate-300 leading-relaxed">{item.practicalAlternative}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
