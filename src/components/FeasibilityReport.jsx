import React from 'react';
import { CheckCircle2, XCircle, AlertCircle, Sparkles, Cpu, Table, FileText, Layers, ShieldAlert, ArrowUpRight } from 'lucide-react';

export default function FeasibilityReport() {
  const allFeaturesMatrix = [
    {
      category: '1. الحفظ والمراجعة الذكية',
      feature: 'مصحح التلاوة الصوتي للتجويد المباشر',
      status: 'صعبة / معقدة',
      feasibility: 'صعبة جداً',
      reason: 'تتطلب نماذج معالجة صوتية وتجويدية مخصصة متقدمة لتمييز زمن المد والغنّة والقلقلة بدون إنذارات خاطئة.'
    },
    {
      category: '1. الحفظ والمراجعة الذكية',
      feature: 'خطة التكرار المتباعد (Spaced Repetition)',
      status: 'قابلة للإضافة',
      feasibility: 'سهلة وعالية جدوى',
      reason: 'تعتمد على خوارزمية رياضية (SM-2/Anki) لتخزين وحساب مواعيد المراجعة استناداً للأخطاء.'
    },
    {
      category: '1. الحفظ والمراجعة الذكية',
      feature: 'الربط البصري والذهني (الخرائط الذهنية)',
      status: 'قابلة للإضافة',
      feasibility: 'متاحة وفورية',
      reason: 'هيكلة بيانات السور حسب المحاور الموضوعية وعرضها ببطاقات بصرية جذابة.'
    },
    {
      category: '2. المعايشة والفهم العميق',
      feature: 'الباحث الدلالي والنفسي (القرآن والسنة)',
      status: 'قابلة للإضافة',
      feasibility: 'متاحة وكفاءة عالية',
      reason: 'استخدام نماذج التضمين اللغوي (Vector Embeddings) لربط المشاعر والتجارب بالنصوص الشرعية.'
    },
    {
      category: '2. المعايشة والفهم العميق',
      feature: 'التفسير التفاعلي وبطاقة السياق',
      status: 'قابلة للإضافة',
      feasibility: 'متاحة وفورية',
      reason: 'ربط أسباب النزول والتفسير ومعاني الكلمات بغلاف تفاعلي بصري متكامل.'
    },
    {
      category: '3. إنتاجية المسلم وتنظيم الحياة',
      feature: 'منظم اليوم حول الصلاة (Salah-Centric Planner)',
      status: 'قابلة للإضافة',
      feasibility: 'سهلة وممتازة',
      reason: 'حساب مواقيت الصلاة عبر APIs الفلكية وربط قائمة المهام بالفترات الفاصلة.'
    },
    {
      category: '3. إنتاجية المسلم وتنظيم الحياة',
      feature: 'الوضع الهادئ التلقائي (Geofencing)',
      status: 'صعبة / معقدة',
      feasibility: 'صعبة تنظيمياً/تقنياً',
      reason: 'قيود أنظمة iOS و Android الشديدة على تشغيل الخدمات بالخلفية وتغيير الصوت تلقائياً واستنزاف البطارية.'
    },
    {
      category: '3. إنتاجية المسلم وتنظيم الحياة',
      feature: 'أذكار ذكية مرتبطة بالسياق (المستشعرات)',
      status: 'صعبة / معقدة',
      feasibility: 'متوسطة إلى صعبة',
      reason: 'قيود وصول المستشعرات بالخلفية وتحديد السرعة وبلوتوث السيارة بدون إرباك المستخدم بإشعارات خاطئة.'
    },
    {
      category: '4. المعاملات المالية والاجتماعية',
      feature: 'ماسح العقود الفقهي (AI Legal Scanner)',
      status: 'صعبة / معقدة',
      feasibility: 'صعبة تحفّظية',
      reason: 'الخطورة الشرعية والقانونية للاعتماد الفوري على AI في الفتوى والعقود بدون مراجعة بشرية متخصصة.'
    },
    {
      category: '4. المعاملات المالية والاجتماعية',
      feature: 'حاسبة المواريث والزكاة التفاعلية',
      status: 'قابلة للإضافة',
      feasibility: 'متاحة وفورية',
      reason: 'قوانين فقهية ورياضية ثابته ومحددة مع إمكانية ربط سعر الذهب اللحظي عبر API.'
    },
    {
      category: '5. المجتمع والعمل الجماعي',
      feature: 'خريطة مرافق المساجد (Crowdsourced Map)',
      status: 'قابلة للإضافة',
      feasibility: 'متاحة وسلسة',
      reason: 'تعتمد على خرائط تفاعلية ونظام تقييم ومراجعة مجتمعي متطور.'
    },
    {
      category: '5. المجتمع والعمل الجماعي',
      feature: 'ختمات وتحديات العائلة والأصلقاء',
      status: 'قابلة للإضافة',
      feasibility: 'متاحة وسلسة',
      reason: 'مجموعات خاصة وربط قواعد البيانات اللحظية (Realtime DB) لحفظ الخصوصية بدون رياء.'
    },
    {
      category: '6. قسم الأطفال والجيل الجديد',
      feature: 'القصص الأخلاقية التفاعلية (اختر مسارك)',
      status: 'قابلة للإضافة',
      feasibility: 'متاحة وممتعة',
      reason: 'سيناريوهات تفاعلية متفرعة (Branching Storytelling) تعزز القيم والأخلاق النبوية.'
    }
  ];

  const feasibleFeaturesDetail = [
    {
      title: '1. خطة مراجعة بنظام التكرار المتباعد (Spaced Repetition)',
      reason: 'سهلة التنفيذ عبر خوارزميات التكرار (مثل SM-2 المستخدمة في Anki)، وتخزين بيانات حفظ المستخدم محلياً أو على خوادم سحابية.'
    },
    {
      title: '2. الربط البصري والذهني للآيات (الخرائط الذهنية)',
      reason: 'يعتمد على تصاميم بصرية وهياكل بيانات موضوعية مرتبطة برقم السورة والآيات، وهو مميز جداً وغير مكلف تقنياً.'
    },
    {
      title: '3. الباحث الدلالي والنفسي للقرآن والسنة',
      reason: 'يمكن تطبيقه بكفاءة عالية باستخدام نماذج تضمين النصوص (Text Embeddings/Vector Search) لربط الموقف الشعوري بالآيات والأحاديث المناسبة.'
    },
    {
      title: '4. التفسير التفاعلي وبطاقة السياق',
      reason: 'تجميع واستعراض بيانات أسباب النزول والتفسير المعتمَد في بطاقات تفاعلية جذابة وسريعة الاستجابة.'
    },
    {
      title: '5. منظم اليوم المعتمد على الصلاة (Salah-Centric Planner)',
      reason: 'حساب مواقيت الصلاة باستخدام معادلات الفلك أو APIs معروفة، وتوزيع قائمة المهام بين الصلوات ديناميكياً.'
    },
    {
      title: '6. حاسبة المواريث والزكاة التفاعلية',
      reason: 'خوارزميات رياضية وفقهية واضحة ومحددة مع ربط بسعر الذهب المباشر عبر APIs مالية بسيطة.'
    },
    {
      title: '7. خريطة مرافق المساجد التفاعلية (Crowdsourced Map)',
      reason: 'تعتمد على خدمات الخرائط المعتادة (مثل OpenStreetMap أو Google Maps API) ونظام تقييم ومراجعة من المستخدمين.'
    },
    {
      title: '8. ختمات وتحديات العائلة والأصدقاء والقصص التفاعلية للأطفال',
      reason: 'تعتمد على غرف خاصة (Realtime Database) وهياكل سيناريوهات تفاعلية (Branching Storytelling) ممتعة وآمنة.'
    }
  ];

  const challengingFeaturesDetail = [
    {
      title: '1. مصحح التلاوة الصوتي للتجويد المباشر (AI Tajweed Recitation Corrector)',
      reasons: [
        'تحدي دقة النموذج الصوتي: أحكام التجويد (كالمدود بالإشارات الزمنية الدقيقة، الغنة، القلقلة، ومخارج الحروف المتشابهة كالضاد والظاء) تتطلب نماذج صوتية مخصصة جداً (Highly Specialized Speech Recognition & Audio Signal Processing) مدربة على آلاف الساعات الترتيلية لمختلف القراء واللكنات.',
        'الضوضاء وتنوع نبرات الصوت: اختلاف أداء المستخدمين وتجويدهم الفعلي والضوضاء المحيطة قد يؤدي إلى إنذارات خاطئة تسبب إرباك الحافظ بدلاً من تصحيحه.'
      ],
      workaround: 'الحل البديل العملي: الاستعانة بالتسميع النصي الذكي، مع توفير مقاطع صبيانية وترتيلية مقارنة، ومصادقة معلمين متخصصين للمراحل المتقدمة.'
    },
    {
      title: '2. الوضع الهادئ التلقائي في المسجد بالخلفية (Geofencing & Background Silent Mode)',
      reasons: [
        'قيود أنظمة التشغيل (iOS & Android Permissions): تفرض أنظمة iOS و Android حديثاً قيوداً صارمة جداً على تطبيقات الخلفية (Background Services) وتغيير إعدادات النظام الصوتية تلقائياً دون تدخل يدوي مباشر من المستخدم لأسباب أمنية وللحفاظ على البطارية.',
        'استهلاك البطارية ودقة الـ GPS: تتبع الموقع المستمر بدقة عالية للتعرف على دخول المسجد قد يستنزف بطارية الهاتف سريعاً، وقد تضعف إشارة الـ GPS داخل المباني الخرسانية.'
      ],
      workaround: 'الحل البديل العملي: الاعتماد على الإشعارات التذكيرية الذكية (Smart Notifications/Shortcuts) التي تنبّه المستخدم بتفعيل الوضع الهادئ بنقرة واحدة عند اقتراب وقت الصلاة أو دخول نطاق المسجد.'
    },
    {
      title: '3. أذكار ذكية تلقائية عبر المستشعرات (Automatic Sensor-Based Athkar)',
      reasons: [
        'قيود الوصول للبلوتوث والمستشعرات في الخلفية: عدم إمكانية فتح التطبيق أو إظهار نوافذ منبثقة (Popups) أثناء قفل الشاشة إلا عبر إشعارات محدودة (Push Notifications) يقلل من سلاسة التجربة.',
        'الإنذارات الخاطئة: الاقتران ببلوتوث مكبر صوت قد لا يكون سيارة، أو المرور بجانب مول دون دخوله قد يطلق إشعارات مزعجة غير مرغوبة.'
      ],
      workaround: 'الحل البديل العملي: إظهار أذكار واقتراحات ذكية في أعلى الواجهة الرئيسية بناءً على الوقت والموقع التقريبي والمناسبة دون الحاجة لتتبع مستمر مستنزف للبطارية.'
    },
    {
      title: '4. ماسح العقود والمعاملات المالية الشرعي (AI Financial Contract Legal Scanner)',
      reasons: [
        'التحفظ الشرعي والتنفيذي: الاعتماد الكلي على الذكاء الاصطناعي في الفتوى وإصدار الأحكام القانونية والشرعية للعقود قد يترتب عليه مسؤولية قانونية وشرعية خطيرة في حال حدوث خطأ أو فهم خاطئ للبند العائدي.',
        'تعقيد المصطلحات القانونية: صياغة العقود تتنوع بشكل واسع وتستخدم مصطلحات قانونية محلية تختلف من دولة لأخرى وتتطلب مراجعة بشرية مستمرة.'
      ],
      workaround: 'الحل البديل العملي: تقديم فحص استرشادي تحذيري يحدد الشروط الشائعة (مثل غرامات التأخير المالية والجهالة) مع تنبيه المستخدم بضرورة مراجعة مستشار شرعي وقانوني مختص.'
    }
  ];

  return (
    <div className="space-y-8">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-slate-800 to-slate-900 border border-slate-700 rounded-2xl p-6 shadow-xl">
        <div className="flex items-center gap-3 mb-2">
          <div className="p-2.5 bg-emerald-500/20 text-emerald-400 rounded-xl border border-emerald-500/30">
            <Cpu className="w-6 h-6" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-white">الدراسة التقنية وجدول تقييم الميزات</h2>
            <p className="text-xs text-slate-300 mt-0.5">
              تحليل تقني شامل ودقيق للميزات القابلة للإضافة المباشرة والميزات الصعبة مع توضيح الأسباب الهندسية والشرعية والتنظيمية.
            </p>
          </div>
        </div>
      </div>

      {/* MATRIX / SUMMARY TABLE */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2 border-b border-slate-800 pb-3">
          <Table className="w-5 h-5 text-emerald-400" />
          مصفوفة تقييم وإمكانية تطبيق كافة الميزات الـ 13
        </h3>

        <div className="overflow-x-auto">
          <table className="w-full text-right text-xs text-slate-300">
            <thead className="bg-slate-950 text-slate-200 border-b border-slate-800">
              <tr>
                <th className="p-3">المجال / القسم</th>
                <th className="p-3">اسم الميزة المقترحة</th>
                <th className="p-3">حالة الإضافة</th>
                <th className="p-3">درجة الجدوى</th>
                <th className="p-3">السبب والتقييم المختصر</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {allFeaturesMatrix.map((item, idx) => (
                <tr key={idx} className="hover:bg-slate-800/40 transition-colors">
                  <td className="p-3 font-semibold text-slate-400 whitespace-nowrap">{item.category}</td>
                  <td className="p-3 font-bold text-white whitespace-nowrap">{item.feature}</td>
                  <td className="p-3 whitespace-nowrap">
                    <span className={`px-2.5 py-1 rounded-full text-[11px] font-bold ${
                      item.status === 'قابلة للإضافة'
                        ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                        : 'bg-red-500/20 text-red-300 border border-red-500/30'
                    }`}>
                      {item.status}
                    </span>
                  </td>
                  <td className="p-3 font-medium text-slate-300 whitespace-nowrap">{item.feasibility}</td>
                  <td className="p-3 text-slate-400 leading-relaxed">{item.reason}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* FEASIBLE FEATURES SECTION */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
        <h3 className="text-lg font-bold text-emerald-400 flex items-center gap-2 border-b border-slate-800 pb-3">
          <CheckCircle2 className="w-5 h-5 text-emerald-400" />
          أولاً: تفاصيل الميزات القابلة للإضافة والتطبيق الفوري (High Feasibility & Value)
        </h3>

        <div className="grid md:grid-cols-2 gap-4">
          {feasibleFeaturesDetail.map((item, idx) => (
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
          ثانياً: تفاصيل الميزات الصعبة أو المعقدة تقنياً/تنظيمياً مع الأسباب والحلول البديلة
        </h3>

        <div className="space-y-4">
          {challengingFeaturesDetail.map((item, idx) => (
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

              <div className="bg-emerald-950/30 border border-emerald-500/30 p-3 rounded-lg text-xs text-emerald-300">
                <span className="font-bold flex items-center gap-1 mb-0.5">
                  <ArrowUpRight className="w-4 h-4 text-emerald-400" />
                  {item.workaround}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
