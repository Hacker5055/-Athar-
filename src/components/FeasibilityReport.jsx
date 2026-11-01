import React from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, Layers, ShieldAlert, Code2 } from 'lucide-react';

export default function FeasibilityReport() {
  const feasibleFeatures = [
    {
      category: '1. الحفظ والمراجعة الذكية',
      title: 'خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      reason: 'سهلة التنفيذ ومثبتة النجاح؛ تعتمد على خوارزميات جدول التكرار المتباعد (مثل SM-2 أو FSRS) عبر تتبع تاريخ إجابات الحافظ وحساب زمن النسيان تلقائياً.'
    },
    {
      category: '1. الحفظ والمراجعة الذكية',
      title: 'الربط البصري والذهني للآيات (الخرائط الموضوعية)',
      reason: 'تعتمد على هياكل بيانات موضوعية ثابتة وموثقة لتقسيم السور إلى محاور وقصص، وتصميم بصري تفاعلي يسهل حفظ تسلسل المعاني.'
    },
    {
      category: '2. المعايشة والفهم العميق',
      title: 'الباحث الدلالي والنفسي (القرآن والسنة حسب حالتك الشعورية)',
      reason: 'سهلة التطبيق بنماذج تضمين النصوص (Text Embeddings) والبحث المتجهي (Vector Search) لربط مشاعر المستخدم بالآيات والأحاديث الصحيحة دون قيود تقنية معقدة.'
    },
    {
      category: '2. المعايشة والفهم العميق',
      title: 'التفسير التفاعلي وبطاقة السياق (Context Card)',
      reason: 'استعراض أسباب النزول، نوع العهد (مكي/مدني)، وتوضيح معاني الكلمات الغريبة بصرياً؛ تعتمد على قواعد بيانات تفسيرية موثوقة وعرض بطاقات غنية وسريعة.'
    },
    {
      category: '3. إنتاجية المسلم',
      title: 'منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)',
      reason: 'تطبيق إدارة مهام مبتكر يربط المهام بالفترات الشرعية الخمس بدلاً من الساعات الثابتة، مع حساب مواقيت الصلاة رياضياً أو عبر APIs موثوقة.'
    },
    {
      category: '4. المعاملات المالية',
      title: 'حاسبة المواريث والزكاة التفاعلية',
      reason: 'معادلات فقهية ورياضية محددة بدقة (الفرائض، وحساب زكاة الذهب والأسهم بنسبة 2.5%) مع جلب سعر الذهب اللحظي عبر واجهات مالية مفتوحة.'
    },
    {
      category: '5. المجتمع والعمل الجماعي',
      title: 'خريطة مرافق المساجد التفاعلية (Crowdsourced Map)',
      reason: 'تعتمد على خدمات الخرائط (Mapbox / OSM) وقاعدة بيانات جماعية تتيح للمصلين تقييم المصليات وتوافر خدمات كراسي كبار السن ومصلى النساء.'
    },
    {
      category: '5. المجتمع والعمل الجماعي',
      title: 'ختمات وتحديات العائلة والأصدقاء (Private Groups)',
      reason: 'تعتمد على غرف خاصة متزامنة (Real-time DB) لمتابعة إنجاز الورد القرآني مع خيار الاسم المستعار لمنع الرياء وضمان الإخلاص.'
    },
    {
      category: '6. الأطفال والجيل الجديد',
      title: 'القصص الأخلاقية التفاعلية (اختر مسارك بأخلاقك)',
      reason: 'تعتمد على شجرة سيناريوهات تفاعلية (Branching Decision Trees) سهلة التطوير وعالية التأثير التربوي للأطفال واليافعين.'
    }
  ];

  const challengingFeatures = [
    {
      category: '1. الحفظ والمراجعة الذكية',
      title: 'مصحح التلاوة الصوتي للتجويد المباشر (AI Real-time Tajweed Corrector)',
      reasons: [
        'تعقيد معالجة الإشارات الصوتية (DSP): أحكام التجويد الدقيقة مثل مقادير المدود بالثواني، الغنة، القلقلة، والصفات (الهمس والجهر) تتطلب نماذج صوتية فائقة التخصص تفرق بين ترتيل القرآن والكلام العادي.',
        'ندرة البيانات التدريبية ذات التصنيف التجويدي الدقيق: نماذج التعرّف على الصوت العامة (مثل Whisper) تكتشف الكلمات فقط دون التشكيل الصوتي أو قواعد التجويد الدقيقة.',
        'حساسية التخطئة الدينية: أي خطأ في النموذج الصوتي (False Positives/Negatives) قد يربك الحافظ أو يخطئ تلاوة صحيحة بلكنة مغايرة، مما يثير اعتراض المستخدمين والمشايخ.'
      ]
    },
    {
      category: '3. إنتاجية المسلم',
      title: 'الوضع الهادئ التلقائي التام بالخلفية في المسجد (Background Geofencing Silent Mode)',
      reasons: [
        'قيود أنظمة التشغيل الصارمة (iOS & Android Privacy Restrictions): تمنع شركة Apple وتطبيقات Google تغيير نغمات النظام أو تحويل الجهاز للوضع الصامت تلقائياً من الخلفية دون إذن يدوي فوري، حماية لخصوصية المستخدم وتحكمه.',
        'استنزاف البطارية (Battery Drain): تشغيل نظام تحديد المواقع (GPS) بدقة عالية بالخلفية لمراقبة نطاق 30-50 متراً يستهلك طاقة الهاتف بسرعة كبيرة.',
        'ضعف إشارات الـ GPS داخل المباني الخرسانية: غالباً ما تنقطع إشارة الساتلايت داخل مساجد الباطون والأقبية، مما يسبب عدم استجابة الميزة أو تأخرها.'
      ]
    },
    {
      category: '3. إنتاجية المسلم',
      title: 'الأذكار التلقائية عبر مستشعرات البلوتوث والحركة بالخلفية',
      reasons: [
        'قيود الخلفية والإشعارات المقيدة: لا تتيح منصات الهواتف فتح التطبيق أو إطلاق شاشات كاملة أثناء إغلاق الهاتف عند الاقتران بالبلوتوث، بل تكتفي بإشعار عادي قد لا يلاحظه السائق.',
        'الإنذارات الزائفة (False Triggers): قد يقترن الهاتف بسماعة منزلية أو بلوتوث خارجي فيظن التطبيق أنها سيارة، أو قد يمر المستخدم بجانب مركز تجاري فيطلق دعاء السوق دون رغبة منه.'
      ]
    },
    {
      category: '4. المعاملات المالية',
      title: 'المستشار الفقهي الذكي لتحليل العقود القانونية والمالية الكاملة',
      reasons: [
        'المسؤولية الشرعية والقانونية (Legal & Sharia Liability): الاعتماد على الذكاء الاصطناعي التوليدي في إجازة عقد مالي أو تحريمه ينطوي على مخاطر كبرى لأن الذكاء الاصطناعي قد يُهلوس (Hallucinate) أو يغفل ثغرة خفية.',
        'تنوع واختلاف الفتاوى والمذاهب: تختلف الهيئات الشرعية في البنوك الإسلامية حول بعض المعاملات (مثل التورق، وغرامات التأخير غير الربوية، والشروط الجزائية)، ولا يمكن اختزالها في خوارزمية قطعية دون مراجعة فقيه مختص.'
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
              <span className="text-[10px] bg-slate-900 text-emerald-400/80 px-2 py-0.5 rounded border border-slate-800 inline-block">
                {item.category}
              </span>
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
              <div className="flex flex-wrap items-center justify-between gap-2 border-b border-slate-900 pb-2">
                <h4 className="text-xs font-bold text-amber-300 flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
                  {item.title}
                </h4>
                <span className="text-[10px] bg-red-950/40 text-red-300 px-2 py-0.5 rounded border border-red-800/40">
                  {item.category}
                </span>
              </div>

              <ul className="space-y-2 text-xs text-slate-300">
                {item.reasons.map((reason, rIdx) => (
                  <li key={rIdx} className="bg-slate-900/70 p-3 rounded-lg border border-slate-800/80 leading-relaxed text-slate-300">
                    <span className="font-semibold text-amber-400">العائق {rIdx + 1}:</span> {reason}
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
