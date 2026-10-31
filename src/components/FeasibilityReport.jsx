import React, { useState } from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, Layers, ShieldAlert, Code2, BarChart3, HelpCircle, ArrowRight } from 'lucide-react';

export default function FeasibilityReport() {
  const [filter, setFilter] = useState('all'); // 'all', 'feasible', 'challenging'

  const feasibleFeatures = [
    {
      id: 'spaced-rep',
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      domain: 'الحفظ والقرآن',
      feasibility: 'سهلة وعالية الجدوى (95%)',
      reason: 'تعتمد على خوارزميات رياضية مثبتة (مثل SM-2 أو FSRS) لتتبع فترات نسيان الذاكرة بناءً على سجل إجابات المستخدم، وتخزينها محلياً في IndexedDB أو عبر السحابة دون تكلفة خوادم عالية.'
    },
    {
      id: 'mind-maps',
      title: 'الربط البصري والذهني للآيات (الخرائط الموضوعية)',
      domain: 'الحفظ والقرآن',
      feasibility: 'سهلة ومبتكرة (90%)',
      reason: 'هيكلة معرفية وقواعد بيانات تربط الآيات بموضوعاتها ووحداتها البلاغية مع رسومات تفاعلية؛ لا تتطلب معالجة خلفية معقدة بل تعتمد على تجربة واجهة مستخدم (UI/UX) غنية.'
    },
    {
      id: 'semantic-search',
      title: 'الباحث الدلالي والنفسي (القرآن والسنة حسب المشاعر)',
      domain: 'المعايشة والفهم',
      feasibility: 'عالية الجدوى مع تقنيات AI الحديثة (85%)',
      reason: 'استخدام نماذج التضمين النصي (Text Embeddings) مع قواعد بيانات متجهة (Vector DB) للبحث عن المشاعر والمعاني وربطها بالآيات والأحاديث الصحيحة دون الحاجة لمطابقة كلمات حرفية.'
    },
    {
      id: 'context-card',
      title: 'التفسير التفاعلي وبطاقة السياق للآيات',
      domain: 'المعايشة والفهم',
      feasibility: 'قابلة للتطبيق بالكامل (95%)',
      reason: 'تنظيم محتوى التفاسير المعتمدة وأسباب النزول في واجهات بطاقات تفاعلية موجزة وسريعة التحميل مع إبراز معاني المفردات الغريبة بوضوح.'
    },
    {
      id: 'salah-planner',
      title: 'منظم اليوم المعتمد على مواقيت الصلاة (Salah-Centric Planner)',
      domain: 'إنتاجية المسلم',
      feasibility: 'عالية الجدوى وفائقة القيمة (90%)',
      reason: 'حساب مواقيت الصلاة فلكياً حسب إحداثيات المدينة، وتوزيع المهام على الفترات الزمنية بين الصلوات ديناميكياً مع تعديل تلقائي لتغير المواقيت اليومي.'
    },
    {
      id: 'zakat-inheritance',
      title: 'حاسبة المواريث والزكاة التفاعلية',
      domain: 'المعاملات المالية',
      feasibility: 'قابلة للتطبيق بالكامل (92%)',
      reason: 'القواعد الفقهية للمواريث والزكاة محددة حسابياً وقطعية في الشريعة، والربط بأسعار الذهب اللحظية يتم عبر واجهات برمجية مالية قياسية (Financial APIs).'
    },
    {
      id: 'mosque-map',
      title: 'خريطة مرافق المساجد التفاعلية (Crowdsourced Map)',
      domain: 'المجتمع',
      feasibility: 'عالية الجدوى (88%)',
      reason: 'استخدام خدمات الخرائط (Leaflet/Mapbox/Google Maps) مع قاعدة بيانات لتسجيل وتحديث تقييمات المصلين لمصليات النساء والنظافة وتسهيلات ذوي الإعاقة.'
    },
    {
      id: 'khatma-groups',
      title: 'ختمات وتحديات العائلة والأصدقاء التنافسية',
      domain: 'المجتمع',
      feasibility: 'عالية الجدوى (90%)',
      reason: 'غرف مشتركة تعتمد على مزامنة البيانات اللحظية (WebSockets / Firebase) مع دعم وضع الخصوصية والاسم المجهول لمنع الرياء.'
    },
    {
      id: 'kids-stories',
      title: 'القصص الأخلاقية التفاعلية للأطفال (اختر مسارك بأخلاقك)',
      domain: 'الجيل الجديد',
      feasibility: 'عالية الجدوى وتربوية بامتياز (95%)',
      reason: 'تعتمد على أشجار السيناريوهات المتشعبة (Branching Decision Tree) لعرض مواقف تربوية وقرارات مع ربط نتائجها بالسنة النبوية الشريفة.'
    }
  ];

  const challengingFeatures = [
    {
      id: 'recitation-ai',
      title: 'مصحح التلاوة الصوتي المباشر لأحكام التجويد (AI Recitation Corrector)',
      domain: 'الحفظ والقرآن',
      status: 'صعب ومعقد تقنياً (Challenging)',
      reasons: [
        'تحدي دقة النموذج الصوتي التجويدي: أحكام التجويد (أزمنة المدود، مراتب الغنة، أحكام القلقلة، مخارج الحروف المتقاربة) تتطلب نماذج ذكاء اصطناعي صوتية فائقة التخصص مجهزة بـ Audio Signal Processing عالي الدقة، ونماذج ASR القياسية (مثل Whisper) غير مخصصة أصلاً لرصد كسور أجزاء الثانية في الغنن والمدود.',
        'الضوضاء واختلاف الروايات واللكنات: تباين مستويات الصوت، وجود صدى، أو لكنات المستخدمين الجغرافية المختلفة ينتج عنها تنبيهات خاطئة (False Positives) تشتت انتباه الحافظ وتقلل ثقته في دقة التصحيح.',
        'الحل العملي والبديل: تقديم التحقق من صحة الكلمات والتشكيل كخطوة أولى، وتوفير التسميع التفاعلي التجريبي مع توجيه المستخدم للاستماع لتلاوات كبار القراء للمقارنة.'
      ]
    },
    {
      id: 'geofence-silent',
      title: 'الوضع الهادئ التلقائي في المسجد بالخلفية (Automated Geofencing Silent Mode)',
      domain: 'إنتاجية المسلم',
      status: 'صعب بسبب قيود أنظمة التشغيل (OS Permissions & Battery)',
      reasons: [
        'سياسات الخصوصية الصارمة لأنظمة iOS و Android: تفرض شركتا Apple و Google قيوداً أمنية مشددة تمنع التطبيقات العادية من تغيير إعدادات الصوت للنظام (Ringer Mode / Do Not Disturb) تلقائياً في الخلفية بدون موافقة يدوية فورية لحماية المستخدم.',
        'استنزاف البطارية وضعف إشارة GPS: تتبع الموقع الجغرافي المستمر في الخلفية (Background Geofencing) يستهلك طاقة البطارية بكثافة، بالإضافة إلى ضعف إشارات الأقمار الصناعية داخل المباني والمساجد الخرسانية الكبيرة.',
        'الحل العملي والبديل: إرسال إشعار تفاعلي سريع عند الاقتراب أو عند دخول وقت الإقامة بضغطة زر واحدة لتفعيل وضع الصامت، أو استخدام اختصارات النظام (Apple Shortcuts / Android Routines).'
      ]
    },
    {
      id: 'sensor-athkar',
      title: 'أذكار ذكية تلقائية عبر المستشعرات والبلوتوث (Sensor-Based Athkar)',
      domain: 'إنتاجية المسلم',
      status: 'معقد بسبب قيود الخلفية والتنبيهات المزعجة',
      reasons: [
        'قيود الخلفية على البلوتوث والمستشعرات: لا تسمح أنظمة الهواتف الحديثة للتطبيقات بالاستماع المستمر لتغيرات البلوتوث والمستشعرات في الخلفية دون خدمة دائمة (Foreground Service) تتطلب إشعاراً ثابتاً يزعج المستخدم.',
        'احتمالية الإنذار الخاطئ: الاقتران بسماعة لاسلكية قد يُفسر خطأً بأنه اتصال بسيارة فيطلق دعاء الركوب، أو المرور في طريق سريع بجانب مركز تجاري قد يطلق دعاء السوق.',
        'الحل العملي والبديل: اقتراح بطاقات الأذكار السياقية داخل التطبيق أو إشعار ذكي لطيف بضغطة واحدة عندما يفتح المستخدم التطبيق.'
      ]
    },
    {
      id: 'contract-scanner',
      title: 'ماسح العقود والمعاملات المالية الشرعي (AI Legal Contract Scanner)',
      domain: 'المعاملات المالية',
      status: 'حساس فقهياً ومسؤولية قانونية (High Liability)',
      reasons: [
        'المسؤولية الشرعية والقانونية: الاعتماد الآلي على الذكاء الاصطناعي في الفتوى والبت بصحة المعاملات والعقود المالية قد يؤدي لتبعات خطيرة في حال حدوث أي خطأ في التحليل، فالفتوى تتطلب فهماً لمقاصد العقد وظروف الطرفين.',
        'تعقيد المصطلحات القانونية المحلية: صياغات العقود تتنوع بشكل كبير من دولة لأخرى وبحسب القوانين المنظمة، مما يصعب تعميمه دون مراجعة مستشار فقهي متخصص.',
        'الحل العملي والبديل: وضع التطبيق كمستشار تنبيهي وتوعوي يبرز الشروط المشبوهة ويقترح بدائل، مع تنبيه واضح بضرورة الرجوع للعلماء والهيئات الشرعية المعتمدة.'
      ]
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
              <h2 className="text-2xl font-bold text-white">دراسة وتقييم جدوى الميزات المقترحة</h2>
              <p className="text-xs text-slate-300 mt-1">
                تحليل تقني وقانوني شامل يحدد الميزات القابلة للإضافة المباشرة، والميزات الصعبة مع بيان أسباب القيود والبدائل الهندسية.
              </p>
            </div>
          </div>

          {/* Quick Metrics */}
          <div className="flex items-center gap-3 bg-slate-950 p-2 rounded-xl border border-slate-800 text-xs">
            <div className="text-center px-3 py-1 bg-emerald-950/40 rounded-lg border border-emerald-500/30">
              <span className="text-emerald-400 font-extrabold text-base block">{feasibleFeatures.length}</span>
              <span className="text-[10px] text-slate-400">ميزات عالية الجدوى</span>
            </div>
            <div className="text-center px-3 py-1 bg-red-950/40 rounded-lg border border-red-500/30">
              <span className="text-red-400 font-extrabold text-base block">{challengingFeatures.length}</span>
              <span className="text-[10px] text-slate-400">ميزات تتطلب حلولاً بديلة</span>
            </div>
          </div>
        </div>
      </div>

      {/* FILTER TABS */}
      <div className="flex items-center gap-2 border-b border-slate-800 pb-2">
        <button
          onClick={() => setFilter('all')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            filter === 'all' ? 'bg-slate-800 text-white border border-slate-700' : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          كافة الميزات المقترحة (الكل)
        </button>
        <button
          onClick={() => setFilter('feasible')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            filter === 'feasible' ? 'bg-emerald-600 text-white shadow-md' : 'text-slate-400 hover:text-emerald-300'
          }`}
        >
          <CheckCircle2 className="w-3.5 h-3.5" />
          الميزات القابلة للإضافة ({feasibleFeatures.length})
        </button>
        <button
          onClick={() => setFilter('challenging')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
            filter === 'challenging' ? 'bg-red-600 text-white shadow-md' : 'text-slate-400 hover:text-red-300'
          }`}
        >
          <XCircle className="w-3.5 h-3.5" />
          الميزات الصعبة ومعوقاتها ({challengingFeatures.length})
        </button>
      </div>

      {/* SECTION 1: FEASIBLE FEATURES */}
      {(filter === 'all' || filter === 'feasible') && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3">
            <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2">
              <CheckCircle2 className="w-5 h-5 text-emerald-400" />
              أولاً: الميزات القابلة للإضافة والتنفيذ الفوري المباشر
            </h3>
            <span className="text-xs text-emerald-400 bg-emerald-950/60 px-3 py-1 rounded-full border border-emerald-500/30">
              قيمة ابتكارية عالية وجدوى هندسية مؤكدة
            </span>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
            {feasibleFeatures.map((item) => (
              <div key={item.id} className="bg-slate-950 p-4 rounded-xl border border-slate-800 flex flex-col justify-between hover:border-emerald-500/40 transition-all group">
                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] bg-slate-900 text-slate-400 px-2.5 py-0.5 rounded-full border border-slate-800">
                      {item.domain}
                    </span>
                    <span className="text-[10px] text-emerald-400 font-bold bg-emerald-950/40 px-2 py-0.5 rounded border border-emerald-500/20">
                      {item.feasibility}
                    </span>
                  </div>
                  <h4 className="text-xs font-bold text-white group-hover:text-emerald-300 transition-colors">
                    {item.title}
                  </h4>
                  <p className="text-[11px] text-slate-400 leading-relaxed">
                    {item.reason}
                  </p>
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
              ثانياً: الميزات الصعبة تقنياً أو تنظيمياً وأسباب القيود والبدائل المعتمدة
            </h3>
            <span className="text-xs text-amber-400 bg-amber-950/60 px-3 py-1 rounded-full border border-amber-500/30">
              تتطلب معالجة بديلة أو ضوابط تنظيمية
            </span>
          </div>

          <div className="space-y-4">
            {challengingFeatures.map((item) => (
              <div key={item.id} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-3">
                <div className="flex flex-col md:flex-row md:items-center justify-between gap-2 border-b border-slate-800 pb-2">
                  <h4 className="text-sm font-bold text-amber-300 flex items-center gap-2">
                    <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                    {item.title}
                  </h4>
                  <span className="text-[11px] bg-red-950/40 text-red-300 px-3 py-1 rounded-full border border-red-500/30 self-start md:self-auto">
                    {item.status}
                  </span>
                </div>

                <div className="space-y-2">
                  {item.reasons.map((r, rIdx) => (
                    <div key={rIdx} className="p-3 bg-slate-900/60 rounded-xl border border-slate-800/80 text-xs text-slate-300 leading-relaxed">
                      <span className="font-semibold text-emerald-400">
                        {r.startsWith('الحل العملي') ? '💡 الحل البديل المعتمد في أثر: ' : `نقطة ${rIdx + 1}: `}
                      </span>
                      {r.replace('الحل العملي والبديل: ', '')}
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
