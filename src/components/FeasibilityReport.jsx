import React, { useState } from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, Layers, ShieldAlert, Code2, ArrowUpRight, HelpCircle } from 'lucide-react';

export default function FeasibilityReport() {
  const [filter, setFilter] = useState('all'); // 'all', 'feasible', 'challenging'

  const features = [
    // 1. Quran
    {
      id: 1,
      category: '1. الحفظ والمراجعة الذكية',
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      status: 'feasible',
      complexity: 'متوسطة - متاحة بسهولة',
      summary: 'قابلة للإضافة والتنفيذ المباشر',
      details: 'تعتمد على تطبيق خوارزمية SM-2 أو FSRS الرياضية (المستخدمة في أنكي). تسجل الآيات التي يتردد فيها المستخدم أو يخطئ، وجدولة مراجعتها بفترات تتباعد (ساعات، أيام، أسابيع). البنية التحتية البرمجية خفيفة وتعمل بكفاءة دون تكلفة استضافة باهظة.'
    },
    {
      id: 2,
      category: '1. الحفظ والمراجعة الذكية',
      title: 'الربط البصري والذهني للآيات (خرائط ذهنية للقرآن)',
      status: 'feasible',
      complexity: 'منخفضة تقنياً - تتطلب جهداً بحثياً في المحتوى',
      summary: 'قابلة للإضافة والتنفيذ المباشر',
      details: 'تقسيم السور موضوعياً وهيكلتها كـ Graph أو Tree في واجهة المستخدم. تتطلب إعداد قاعدة بيانات محتوى موضوعي موثوق، وتنفيذها كواجهات رسومية تفاعلية جذابة وسلسة للمستخدم.'
    },
    {
      id: 3,
      category: '1. الحفظ والمراجعة الذكية',
      title: 'مصحح التلاوة الصوتي للتجويد المباشر (AI Tajweed Corrector)',
      status: 'challenging',
      complexity: 'معقدة وصعبة جداً (High Complexity)',
      summary: 'صعبة التنفيذ الفوري / تتطلب استثمارات ضخمة',
      details: 'تتطلب نماذج ذكاء اصطناعي صوتية فائقة التخصص (Acoustic Phonetic Models) مدربة على أحكام التجويد الدقيقة (أزمنة المدود، مخارج الحروف كـ الضاد والظاء، ترقيق وتفخيم الراءات، الغنن والقلقلة). النماذج الحالية مثل Whisper تتعرف على نص الكلام فقط (Speech-to-Text) وليس نطق التجويد الدقيق، كما أن الضوضاء ولهجات المستخدمين ترفع نسبة الخطأ وتشتت الحافظ.'
    },

    // 2. Deep Understanding
    {
      id: 4,
      category: '2. المعايشة والفهم العميق',
      title: 'الباحث الدلالي والنفسي (القرآن والسنة حسب حالتك)',
      status: 'feasible',
      complexity: 'متاحة وفعالة جداً (High Value)',
      summary: 'قابلة للإضافة والتنفيذ المباشر',
      details: 'تطبيق تقنية البحث الدلالي المتجهي (Vector Embeddings & Semantic Search) باستخدام نماذج لغوية تضمينية، بحيث يتم تحويل مشاعر المستخدم ("أشعر بقلق من المستقبل"، "تعرضت لظلم") إلى إحداثيات ومطابقتها بمواضيع الآيات والأحاديث والقصص النبوية ذات الصلة.'
    },
    {
      id: 5,
      category: '2. المعايشة والفهم العميق',
      title: 'التفسير التفاعلي والسياقي (بطاقة السياق)',
      status: 'feasible',
      complexity: 'سهلة ومباشرة',
      summary: 'قابلة للإضافة والتنفيذ المباشر',
      details: 'ربط كل آية ببيانات معتمدة لأسباب النزول (قصة موجزة)، وبيان العهد المكي والمدني، مع معجم بصري لمعاني الكلمات الغريبة. لا توجد أي عوائق تقنية.'
    },

    // 3. Productivity
    {
      id: 6,
      category: '3. إنتاجية المسلم وتنظيم الحياة',
      title: 'منظم اليوم المعتمد على مواقيت الصلاة (Salah-Centric Planner)',
      status: 'feasible',
      complexity: 'سهلة ومبتكرة',
      summary: 'قابلة للإضافة والتنفيذ المباشر',
      details: 'حساب مواقيت الصلاة عبر معادلات الفلك أو APIs معتمدة، وتصميم إدارة المهام ديناميكياً لتقسم اليوم إلى فترات الصلاة (بين الفجر والشروق، بين الظهر والعصر...) مع تحديث المواعيد تلقائياً بتغير أوقات الصلاة السنوية.'
    },
    {
      id: 7,
      category: '3. إنتاجية المسلم وتنظيم الحياة',
      title: 'الوضع الهادئ التلقائي في المسجد (Geofencing & Auto-Silent)',
      status: 'challenging',
      complexity: 'صعبة بسبب قيود أنظمة التشغيل (OS Restrictions)',
      summary: 'صعبة التنفيذ التلقائي الكامل بالخلفية',
      details: 'تفرض شركتا Apple و Google قيوداً أمنية صارمة على تطبيقات الطرف الثالث تمنع تغيير الملف الصوتي للجهاز (Ringer / Do Not Disturb) برمجياً في الخلفية بدون تدخل يدوي مباشر لحماية خصوصية المستخدم، إضافة إلى استنزاف فحص الـ GPS الدائم للبطارية وضعف إشارة الأقمار الصناعية داخل المباني الخرسانية للمساجد.'
    },
    {
      id: 8,
      category: '3. إنتاجية المسلم وتنظيم الحياة',
      title: 'أذكار ذكية مرتبطة بالمستشعرات والبلوتوث تلقائياً',
      status: 'challenging',
      complexity: 'متوسطة إلى صعبة (قيود الخلفية والإزعاج)',
      summary: 'صعبة التنفيذ بسلاسة تامة',
      details: 'تتطلب الوصول إلى البلوتوث وتحديد النشاط الحركي في الخلفية. أنظمة التشغيل تقيّد تشغيل التطبيق وإظهار النوافذ التلقائية عند القفل. كما يوجد خطر كبير للإشعارات المضللة (False Positives)، مثل الاقتران بسماعة بلوتوث منزلية تُعامل كأنها سيارة، أو المرور بجانب مول يطلق دعاء السوق دون رغبة المستخدم.'
    },

    // 4. Financial
    {
      id: 9,
      category: '4. المعاملات المالية والاجتماعية',
      title: 'حاسبة المواريث والزكاة التفاعلية',
      status: 'feasible',
      complexity: 'مباشرة ومحددة بدقة',
      summary: 'قابلة للإضافة والتنفيذ المباشر',
      details: 'خوارزميات فقهية ورياضية واضحة ومقننة، يمكن ربطها بمزودي أسعار الذهب والفضة والعملات اللحظية عبر REST APIs، مع تفصيل النصيب والدليل الشرعي لكل وارث.'
    },
    {
      id: 10,
      category: '4. المعاملات المالية والاجتماعية',
      title: 'ماسح العقود والاتفاقيات الذكي (المستشار الفقهي)',
      status: 'challenging',
      complexity: 'صعبة بسبب المسؤولية الشرعية والقانونية (Legal Liability)',
      summary: 'صعبة كفتوى قطعية / ممكنة فقط كأداة إرشادية أولية',
      details: 'الاعتماد على LLM في تقييم شرعية العقود ينطوي على مخاطر "الهلوسة" والتفسير الخاطئ لمصطلحات قانونية محلية تختلف من بلد لآخر. أي خطأ في تحليل شرط جزائي أو فائدة مركبة قد يوقع المستخدم في تعامل محرم مع تحمله خسائر مالية، لذا لا بد من وضع إخلاء مسؤولية واقتصار دوره على تسليط الضوء الإرشادي مع حث المستخدم على مراجعة مفتٍ أو محامٍ شرعي.'
    },

    // 5. Community & Kids
    {
      id: 11,
      category: '5. المجتمع والعمل الجماعي',
      title: 'خريطة مرافق المساجد التشاركية (Crowdsourced Mosque Map)',
      status: 'feasible',
      complexity: 'متاحة وناجحة مجتمعياً',
      summary: 'قابلة للإضافة والتنفيذ المباشر',
      details: 'استخدام خرائط Mapbox أو OpenStreetMap مع نظام تقييمات وتصويت المصلين على توفر مصلى سيدات، نظافة دورات المياه، كراسي كبار السن، ومصاعد ذوي الاحتياجات الخاصة.'
    },
    {
      id: 12,
      category: '5. المجتمع والعمل الجماعي',
      title: 'ختمات وتحديات العائلة والأصدقاء والقصص التفاعلية للأطفال',
      status: 'feasible',
      complexity: 'متاحة وذات أثر نفسي وتربوي هائل',
      summary: 'قابلة للإضافة والتنفيذ المباشر',
      details: 'تنفيذ مجموعات خاصة برمز مشاركة مع إمكانية إخفاء الأسماء لمنع الرياء، وقصص تفاعلية تعتمد على شجرة خيارات وسيناريوهات تصرف نبوية تعزز منظومة القيم لدى النشء.'
    }
  ];

  const filteredFeatures = filter === 'all'
    ? features
    : features.filter(f => f.status === filter);

  const feasibleCount = features.filter(f => f.status === 'feasible').length;
  const challengingCount = features.filter(f => f.status === 'challenging').length;

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
              <h2 className="text-2xl font-bold text-white">دراسة وتقييم جدوى الميزات المقترحة</h2>
              <p className="text-xs text-slate-300 mt-1">
                تقرير تحليلي تقني وشرعي وتنظيمي مفصل يوضح الميزات القابلة للإضافة الفورية والميزات الصعبة مع الأسباب والحلول البديلة.
              </p>
            </div>
          </div>

          {/* Quick Metrics */}
          <div className="flex items-center gap-3">
            <div className="bg-emerald-950/40 border border-emerald-500/40 px-3.5 py-2 rounded-xl text-center">
              <span className="text-[10px] text-emerald-400 block font-semibold">الميزات القابلة للإضافة</span>
              <span className="text-xl font-black text-emerald-300 font-mono">{feasibleCount} ميزات</span>
            </div>
            <div className="bg-amber-950/40 border border-amber-500/40 px-3.5 py-2 rounded-xl text-center">
              <span className="text-[10px] text-amber-400 block font-semibold">الميزات الصعبة/المعقدة</span>
              <span className="text-xl font-black text-amber-300 font-mono">{challengingCount} ميزات</span>
            </div>
          </div>
        </div>

        {/* Filter Controls */}
        <div className="flex items-center gap-2 mt-6 pt-4 border-t border-slate-800">
          <span className="text-xs text-slate-400 ml-2">تصفية العرض:</span>
          <button
            onClick={() => setFilter('all')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
              filter === 'all' ? 'bg-slate-700 text-white shadow' : 'bg-slate-950 text-slate-400 hover:text-slate-200 border border-slate-800'
            }`}
          >
            الكل ({features.length})
          </button>
          <button
            onClick={() => setFilter('feasible')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 ${
              filter === 'feasible' ? 'bg-emerald-600 text-white shadow' : 'bg-slate-950 text-slate-400 hover:text-slate-200 border border-slate-800'
            }`}
          >
            <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
            قابلة للإضافة فوراً ({feasibleCount})
          </button>
          <button
            onClick={() => setFilter('challenging')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 ${
              filter === 'challenging' ? 'bg-amber-600 text-white shadow' : 'bg-slate-950 text-slate-400 hover:text-slate-200 border border-slate-800'
            }`}
          >
            <AlertCircle className="w-3.5 h-3.5 text-amber-400" />
            صعبة ومعقدة تقنياً/تنظيمياً ({challengingCount})
          </button>
        </div>
      </div>

      {/* Feature Cards Grid */}
      <div className="grid md:grid-cols-2 gap-4">
        {filteredFeatures.map((feat) => {
          const isFeasible = feat.status === 'feasible';
          return (
            <div
              key={feat.id}
              className={`p-5 rounded-2xl border transition-all space-y-3 ${
                isFeasible
                  ? 'bg-slate-900/90 border-slate-800 hover:border-emerald-500/40'
                  : 'bg-slate-900/90 border-slate-800 hover:border-amber-500/40'
              }`}
            >
              <div className="flex items-start justify-between gap-2">
                <span className="text-[11px] font-semibold text-slate-400 bg-slate-950 px-2.5 py-1 rounded-md border border-slate-800">
                  {feat.category}
                </span>
                <span
                  className={`text-[11px] font-bold px-2.5 py-1 rounded-full flex items-center gap-1 ${
                    isFeasible
                      ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                      : 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                  }`}
                >
                  {isFeasible ? <CheckCircle2 className="w-3.5 h-3.5" /> : <AlertCircle className="w-3.5 h-3.5" />}
                  {feat.summary}
                </span>
              </div>

              <div>
                <h3 className="text-sm font-bold text-white mb-1 flex items-center gap-1.5">
                  {feat.title}
                </h3>
                <span className="text-[11px] text-slate-400 block font-medium">
                  مستوى الجهد والتنفيذ: <span className={isFeasible ? 'text-emerald-400' : 'text-amber-400'}>{feat.complexity}</span>
                </span>
              </div>

              <div className="bg-slate-950 p-3.5 rounded-xl border border-slate-800 text-xs text-slate-300 leading-relaxed">
                <span className="font-semibold text-slate-200 block mb-1">
                  {isFeasible ? '✅ سبب الجدوى وإمكانية التطبيق:' : '⚠️ أسباب الصعوبة والتحديات الرئيسية:'}
                </span>
                {feat.details}
              </div>
            </div>
          );
        })}
      </div>

      {/* Architectural Summary Section */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 space-y-4">
        <h3 className="text-base font-bold text-white flex items-center gap-2 border-b border-slate-800 pb-3">
          <Layers className="w-5 h-5 text-emerald-400" />
          خلاصة التوصيات الفنية لبناء تطبيق "أثر" (Athar)
        </h3>

        <div className="grid md:grid-cols-3 gap-4 text-xs">
          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-2">
            <h4 className="font-bold text-emerald-400">1. المرحلة الأولى (MVP السريع والفعال):</h4>
            <p className="text-slate-400 leading-relaxed">
              إطلاق ميزات التكرار المتباعد، الخرائط الذهنية، الباحث الدلالي الشعوري، منظم اليوم بالصلوات، حاسبة الزكاة والمواريث، والقصص التفاعلية، لأنها تمنح تجربة فريدة فورية دون مخاطر برمجية أو تكاليف مرتفعة.
            </p>
          </div>

          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-2">
            <h4 className="font-bold text-amber-400">2. المعالجة الذكية للقيود:</h4>
            <p className="text-slate-400 leading-relaxed">
              بالنسبة للوضع الصامت والأذكار، بدلاً من التفعيل القسري بالخلفية المرفوض من أنظمة التشغيل، يتم استخدام إشعارات سريعة قابلة للتفاعل (Interactive Action Notifications) أو اختصارات Siri Shortcuts / Android Routines.
            </p>
          </div>

          <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-2">
            <h4 className="font-bold text-purple-400">3. المسؤولية الشرعية ومصحح الصوت:</h4>
            <p className="text-slate-400 leading-relaxed">
              تقديم ماسح العقود كمستشار مساعد "استرشادي" مع تنبيه قانوني وشرعي واضح، وتطوير مصحح التلاوة تدريجياً عبر ربطه بخدمات STT متطورة مع وضع هامش خطأ تعليمي واضح.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
