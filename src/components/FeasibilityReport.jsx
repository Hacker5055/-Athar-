import React from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, Layers, ShieldAlert, Code2 } from 'lucide-react';

export default function FeasibilityReport() {
  const feasibleFeatures = [
    {
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      reason: 'سهلة التنفيذ عبر خوارزميات التكرار (مثل SM-2 المستخدمة في Anki)، وتخزين بيانات حفظ المستخدم محلياً أو على خوادم سحابية.'
    },
    {
      title: 'الربط البصري والذهني للآيات (الخرائط الذهنية)',
      reason: 'يعتمد على تصاميم بصرية وهياكل بيانات موضوعية مرتبطة برقم السورة والآيات، وهو مميز جداً وغير مكلف تقنياً.'
    },
    {
      title: 'الباحث الدلالي والنفسي للقرآن والسنة',
      reason: 'يمكن تطبيقه بكفاءة عالية باستخدام نماذج تضمين النصوص (Text Embeddings/Vector Search) لربط الموقف الشعوري بالآيات والأحاديث المناسبة.'
    },
    {
      title: 'التفسير التفاعلي وبطاقة السياق',
      reason: 'تجميع واستعراض بيانات أسباب النزول والتفسير المعتمَد في بطاقات تفاعلية جذابة وسريعة الاستجابة.'
    },
    {
      title: 'منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)',
      reason: 'حساب مواقيت الصلاة باستخدام معادلات الفلك أو APIs معروفة، وتوزيع قائمة المهام بين الصلوات ديناميكياً.'
    },
    {
      title: 'حاسبة المواريث والزكاة التفاعلية',
      reason: 'خوارزميات رياضية وفقهية واضحة ومحددة مع ربط بسعر الذهب المباشر عبر APIs مالية بسيطة.'
    },
    {
      title: 'خريطة مرافق المساجد التفاعلية (Crowdsourced Map)',
      reason: 'تعتمد على خدمات الخرائط المعتادة (مثل OpenStreetMap أو Google Maps API) ونظام تقييم ومراجعة من المستخدمين.'
    },
    {
      title: 'ختمات وتحديات العائلة والأصدقاء والقصص التفاعلية للأطفال',
      reason: 'تعتمد على غرف خاصة (Realtime Database) وهياكل سيناريوهات تفاعلية (Branching Storytelling) ممتعة وآمنة.'
    }
  ];

  const challengingFeatures = [
    {
      title: 'مصحح التلاوة الصوتي للتجويد المباشر (AI Tajweed Recitation Corrector)',
      reasons: [
        'تحدي دقة النموذج الصوتي: أحكام التجويد (كالمدود بالإشارات الزمنية الدقيقة، الغنة، القلقلة، ومخارج الحروف المتشابهة كالضاد والظاء) تتطلب نماذج صوتية مخصصة جداً (Highly Specialized Speech Recognition & Audio Signal Processing) مدربة على آلاف الساعات الترتيلية لمختلف القراء واللكنات.',
        'الضوضاء وتنوع نبرات الصوت: اختلاف أداء المستخدمين وتجويدهم الفعلي والضوضاء المحيطة قد يؤدي إلى إنذارات خاطئة تسبب إرباك الحافظ بدلاً من تصحيحه.'
      ]
    },
    {
      title: 'الوضع الهادئ التلقائي في المسجد بالخلفية (Geofencing & Background Silent Mode)',
      reasons: [
        'قيود أنظمة التشغيل (iOS & Android Permissions): تفرض أنظمة iOS و Android حديثاً قيوداً صارمة جداً على تطبيقات الخلفية (Background Services) وتغيير إعدادات النظام الصوتية تلقائياً دون تدخل يدوي مباشر من المستخدم لأسباب أمنية وللحفاظ على البطارية.',
        'استهلاك البطارية ودقة الـ GPS: تتبع الموقع المستمر بدقة عالية للتعرف على دخول المسجد قد يستنزف بطارية الهاتف سريعاً، وقد تضعف إشارة الـ GPS داخل المباني الخرسانية.'
      ]
    },
    {
      title: 'أذكار ذكية تلقائية عبر المستشعرات (Automatic Sensor-Based Athkar)',
      reasons: [
        'قيود الوصول للبلوتوث والمستشعرات في الخلفية: عدم إمكانية فتح التطبيق أو إظهار نوافذ منبثقة (Popups) أثناء قفل الشاشة إلا عبر إشعارات محدودة (Push Notifications) يقلل من سلاسة التجربة.',
        'الإنذارات الخاطئة: الاقتران ببلوتوث مكبر صوت قد لا يكون سيارة، أو المرور بجانب مول دون دخوله قد يطلق إشعارات مزعجة غير مرغوبة.'
      ]
    },
    {
      title: 'ماسح العقود والمعاملات المالية الشرعي (AI Financial Contract Legal Scanner)',
      reasons: [
        'التحفظ الشرعي والتنفيذي: الاعتماد الكلي على الذكاء الاصطناعي في الفتوى وإصدار الأحكام القانونية والشرعية للعقود قد يترتب عليه مسؤولية قانونية وشرعية خطيرة في حال حدوث خطأ أو فهم خاطئ للبند العائدي.',
        'تعقيد المصطلحات القانونية: صياغة العقود تتنوع بشكل واسع وتستخدم مصطلحات قانونية محلية تختلف من دولة لأخرى وتتطلب مراجعة بشرية مستمرة.'
      ]
    }
  ];

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
              تحليل تقني شامل ودقيق للميزات القابلة للإضافة المباشرة والميزات الصعبة مع توضيح الأسباب الهندسية والتنظيمية.
            </p>
          </div>
        </div>
      </div>

      {/* FEASIBLE FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2 border-b border-slate-800 pb-3">
          <CheckCircle2 className="w-5 h-5 text-emerald-400" />
          أولاً: الميزات القابلة للإضافة والتطبيق الفوري (High Feasibility & Value)
        </h3>

        <div className="grid md:grid-cols-2 gap-4">
          {feasibleFeatures.map((item, idx) => (
            <div key={idx} className="bg-slate-950 p-4 rounded-xl border border-slate-800 space-y-1.5 hover:border-emerald-500/30 transition-all">
              <h4 className="text-xs font-bold text-emerald-300 flex items-center gap-1.5">
                <Sparkles className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                {item.title}
              </h4>
              <p className="text-[11px] text-slate-400 leading-relaxed">{item.reason}</p>
            </div>
          ))}
        </div>
      </div>

      {/* CHALLENGING FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <h3 className="text-lg font-bold text-red-400 flex items-center gap-2 border-b border-slate-800 pb-3">
          <XCircle className="w-5 h-5 text-red-400" />
          ثانياً: الميزات الصعبة أو المعقدة تقنياً/تنظيمياً مع الأسباب التفصيلية
        </h3>

        <div className="space-y-4">
          {challengingFeatures.map((item, idx) => (
            <div key={idx} className="bg-slate-950 p-5 rounded-xl border border-slate-800 space-y-3">
              <h4 className="text-xs font-bold text-amber-300 flex items-center gap-2">
                <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                {item.title}
              </h4>

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
