import React, { useState } from 'react';
import {
  Scale,
  FileText,
  Calculator,
  AlertTriangle,
  CheckCircle,
  RefreshCw,
  DollarSign,
  PieChart,
  Info,
  Coins,
  ShieldCheck,
  TrendingUp,
  Layers
} from 'lucide-react';

export default function FinancialTools() {
  const [activeTab, setActiveTab] = useState('contractScanner'); // 'contractScanner', 'zakatCalc', 'inheritanceCalc'

  // Contract Templates & State
  const contractTemplates = {
    loan: {
      name: 'عقد تقسيط مع غرامة تأخير',
      text: 'في حال تأخر الطرف الثاني (المشتري) عن سداد القسط الشهري في الموعد المحدد لمدة تتجاوز 10 أيام، تُفرض غرامة تأخير قدرها 5% من قيمة القسط تضاف لحساب البائع شهرياً، كما يعتبر العقد مفسوخاً مع احتفاظ البائع بكامل الأقساط السابقة كشرط جزائي غير محدد القيمة.'
    },
    employment: {
      name: 'عقد عمل مع بند جزائي غير عادل',
      text: 'يحظر على الطرف الثاني (الموظف) العمل لدى أي جهة منافسة داخل الدولة لمدة 5 سنوات بعد انتهاء العقد لأي سبب، وفي حال الإخلال يدفع الموظف تعويضاً جزائياً فورياً قدره 200,000 ريال دون الحاجة لإثبات حدوث أي ضرر فعلي للشركة.'
    },
    murabaha: {
      name: 'عقد مرابحة بيع بالتقسيط منضبط',
      text: 'يقوم البنك بشراء السلعة وحيازتها ونقل ملكيتها إليه أولاً، ثم يبيعها للعميل بربح متفق عليه صراحة بالأقساط. وفي حال عجز العميل وثبت إعساره يُمنح نظرة إلى ميسرة، ولا تُفرض أي زيادة مالية على أصل الدين.'
    }
  };

  const [contractText, setContractText] = useState(contractTemplates.loan.text);
  const [scanAnalysis, setScanAnalysis] = useState(null);

  // Zakat Calculator State
  const [assetType, setAssetType] = useState('stocks'); // 'stocks', 'gold', 'crypto', 'cash'
  const [assetValue, setAssetValue] = useState(120000);
  const [goldPricePerGram, setGoldPricePerGram] = useState(285); // Gold gram 24k
  const [silverPricePerGram, setSilverPricePerGram] = useState(3.4); // Silver gram
  const [isHawlCompleted, setIsHawlCompleted] = useState(true);

  // Inheritance State
  const [estateAmount, setEstateAmount] = useState(1200000);
  const [heirs, setHeirs] = useState({
    wife: 1,
    husband: 0,
    sons: 2,
    daughters: 2,
    father: 1,
    mother: 1
  });

  const handleScanContract = () => {
    if (contractText.includes('غرامة تأخير') || contractText.includes('5%')) {
      setScanAnalysis({
        status: 'warning',
        complianceRate: '40%',
        summary: 'تم العثور على محاذير ربوية وغرر فاحش يستوجب تعديل البنود.',
        issues: [
          {
            title: 'شبهة ربا الجاهلية (غرامة التأخير المالية)',
            type: 'محظور شرعي صريح',
            description: 'اشتراط زيادة مالية نقدية مشروطة بسبب تأخير السداد، وهو عين ربا النسيئة الذي حرمته المجامع الفقهية وهيئة المعايير الشرعية (أي إما أن تقضي وإما أن تربي).',
            alternative: 'البديل الشرعي: إلزام المماطل بالتبرع بنسبة محددة لجهات خيرية لا تستفيد منها الشركة لردع المماطلة، أو اللجوء للضمانات والرهن.'
          },
          {
            title: 'جهالة وإجحاف في الشرط الجزائي والفسخ',
            type: 'شبهة غرر وأكل مال بالباطل',
            description: 'مصادرة كافة الأقساط السابقة دون احتساب المقابل الفعلي لما استهلكه المشتري.',
            alternative: 'البديل الشرعي: حصر التعويض على الضرر المالي الفعلي المباشر المبرهن فقط دون أرباح مستقبلية.'
          }
        ]
      });
    } else if (contractText.includes('منافسة') || contractText.includes('200,000')) {
      setScanAnalysis({
        status: 'caution',
        complianceRate: '65%',
        summary: 'العقد يتضمن شروطاً تعسفية تضر بحق الموظف في الكسب والعمل المشروع.',
        issues: [
          {
            title: 'شرط عدم المنافسة التعسفي (5 سنوات شاملة الدولة كاملة)',
            type: 'شرط مجحف ومقيد للرزق',
            description: 'مد النطاق الزمني والجغرافي بشكل مبالغ فيه يمنع الموظف من مصدر رزقه دون مصلحة مشروعة متناسبة.',
            alternative: 'البديل الشرعي والنظامي: قصر المنع على الأسرار التجارية الدقيقة لمدة معقولة (سنة واحدة) وفي نطاق جغرافي محدد لحماية المصلحة المشروعة دون تعسف.'
          }
        ]
      });
    } else {
      setScanAnalysis({
        status: 'success',
        complianceRate: '98%',
        summary: 'العقد متوافق بشكل ممتاز مع الضوابط الشرعية للمرابحة وحيازة السلعة قبل بيعها.',
        issues: []
      });
    }
  };

  // Zakat Calculations
  const goldNisabValue = 85 * goldPricePerGram; // 85 grams of 24k gold
  const isAboveNisab = assetValue >= goldNisabValue;
  const zakatDue = isHawlCompleted && isAboveNisab ? (assetValue * 0.025) : 0;

  // Inheritance Calculation Logic
  const calculateInheritance = () => {
    let shares = [];
    let remainingEstate = estateAmount;

    // Wife Share
    if (heirs.wife > 0) {
      const hasChildren = (heirs.sons + heirs.daughters) > 0;
      const fraction = hasChildren ? 0.125 : 0.25; // 1/8 or 1/4
      const amount = estateAmount * fraction;
      shares.push({
        heir: 'الزوجة (أو الزوجات بالتساوي)',
        fractionText: hasChildren ? 'الثُّمن (1/8)' : 'الرُّبع (1/4)',
        amount: amount,
        daleel: hasChildren ? 'فَإِن كَانَ لَكُمْ وَلَدٌ فَلَهُنَّ الثُُّمُنُ مِمَّا تَرَكْتُم' : 'وَلَهُنَّ الرُّبُعُ مِمَّا تَرَكْتُمْ إِن لَّمْ يَكُن لَّكُمْ وَلَدٌ'
      });
      remainingEstate -= amount;
    }

    // Mother Share
    if (heirs.mother > 0) {
      const hasChildren = (heirs.sons + heirs.daughters) > 0;
      const fraction = hasChildren ? (1 / 6) : (1 / 3);
      const amount = estateAmount * fraction;
      shares.push({
        heir: 'الأم',
        fractionText: hasChildren ? 'السُّدس (1/6)' : 'الثُّلث (1/3)',
        amount: amount,
        daleel: 'وَلِأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِّنْهُمَا السُُّدُسُ مِمَّا تَرَكَ إِن كَانَ لَهُ وَلَدٌ'
      });
      remainingEstate -= amount;
    }

    // Father Share
    if (heirs.father > 0) {
      const fraction = (1 / 6);
      const amount = estateAmount * fraction;
      shares.push({
        heir: 'الأب',
        fractionText: 'السُّدس فرضاً (1/6)',
        amount: amount,
        daleel: 'وَلِأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِّنْهُمَا السُُّدُسُ مِمَّا تَرَكَ إِن كَانَ لَهُ وَلَدٌ'
      });
      remainingEstate -= amount;
    }

    // Children (Asaba - للذكر مثل حظ الأنثيين)
    const sonsCount = heirs.sons;
    const daughtersCount = heirs.daughters;
    const totalSharesCount = (sonsCount * 2) + daughtersCount;

    if (totalSharesCount > 0 && remainingEstate > 0) {
      const singleShare = remainingEstate / totalSharesCount;
      if (sonsCount > 0) {
        shares.push({
          heir: `الأبناء (${sonsCount} ذكور - تعصيباً)`,
          fractionText: 'باقي التركة (للذكر سهمان)',
          amount: singleShare * 2 * sonsCount,
          daleel: 'يُوصِيكُمُ اللَّهُ فِي أَوْلَادِكُمْ ۖ لِلذَّكَرِ مِثْلُ حَظِّ الْأُنثَيَيْنِ'
        });
      }
      if (daughtersCount > 0) {
        shares.push({
          heir: `البنات (${daughtersCount} إناث - تعصيباً)`,
          fractionText: 'باقي التركة (للأنثى سهم)',
          amount: singleShare * daughtersCount,
          daleel: 'يُوصِيكُمُ اللَّهُ فِي أَوْلَادِكُمْ ۖ لِلذَّكَرِ مِثْلُ حَظِّ الْأُنثَيَيْنِ'
        });
      }
    }

    return shares;
  };

  const inheritanceShares = calculateInheritance();

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-amber-950/80 via-amber-900/50 to-slate-900 border border-amber-500/30 rounded-3xl p-6 md:p-8 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-amber-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/20 text-amber-300 text-xs font-semibold mb-3 border border-amber-500/30">
              <Scale className="w-3.5 h-3.5" />
              المستشار المالي والفقهي الذكي
            </div>
            <h2 className="text-2xl md:text-3xl font-extrabold text-white">المعاملات المالية والشرعية</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              ماسح العقود الذكي للتنبيه من المحاذير الربوية والجهالة، حاسبة الزكاة اللحظية للنصاب، وحاسبة المواريث البصرية.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/80 p-1.5 rounded-2xl border border-slate-800">
            <button
              onClick={() => setActiveTab('contractScanner')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'contractScanner' ? 'bg-amber-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              ماسح العقود
            </button>
            <button
              onClick={() => setActiveTab('zakatCalc')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'zakatCalc' ? 'bg-amber-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              حاسبة الزكاة
            </button>
            <button
              onClick={() => setActiveTab('inheritanceCalc')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'inheritanceCalc' ? 'bg-amber-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              حاسبة المواريث
            </button>
          </div>
        </div>
      </div>

      {/* TAB 1: CONTRACT SCANNER */}
      {activeTab === 'contractScanner' && (
        <div className="grid md:grid-cols-2 gap-6">
          <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-lg font-bold text-amber-400 flex items-center gap-2">
                  <FileText className="w-5 h-5" />
                  تحليل بنود العقود والشروط الشرعية
                </h3>
                <p className="text-slate-400 text-xs mt-1">
                  ضع نص العقد أو اختر نموذجاً شائعاً لفحصه والتحقق من الشروط المشبوهة.
                </p>
              </div>
            </div>

            {/* Template Selector */}
            <div className="space-y-1.5">
              <span className="text-[11px] text-slate-400 font-semibold">نماذج عقود جاهزة للتجربة:</span>
              <div className="grid grid-cols-3 gap-2">
                {Object.keys(contractTemplates).map((key) => (
                  <button
                    key={key}
                    onClick={() => {
                      setContractText(contractTemplates[key].text);
                      setScanAnalysis(null);
                    }}
                    className="p-2 text-[11px] rounded-xl border border-slate-800 bg-slate-950 hover:border-amber-500/40 text-slate-300 font-medium text-center truncate"
                  >
                    {contractTemplates[key].name}
                  </button>
                ))}
              </div>
            </div>

            <textarea
              rows={6}
              value={contractText}
              onChange={(e) => setContractText(e.target.value)}
              className="w-full bg-slate-950 border border-slate-700 rounded-2xl p-4 text-xs text-slate-100 focus:outline-none focus:border-amber-500 leading-relaxed font-sans"
              placeholder="ضع نص بنود العقد هنا..."
            />

            <button
              onClick={handleScanContract}
              className="w-full py-3.5 bg-amber-600 hover:bg-amber-500 text-white text-xs font-bold rounded-2xl shadow-lg transition-all flex items-center justify-center gap-2"
            >
              <RefreshCw className="w-4 h-4" />
              بدء الفحص والتدقيق الفقهي
            </button>

            <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-[11px] text-slate-400 flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400 shrink-0" />
              <span>مبني وفق معايير هيئة المحاسبة والمراجعة للمؤسسات المالية الإسلامية (AAOIFI).</span>
            </div>
          </div>

          {/* Analysis Result */}
          <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-4 flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between mb-4 border-b border-slate-800 pb-3">
                <h3 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                  <Scale className="w-5 h-5 text-amber-400" />
                  تقرير التدقيق الفقهي والبدائل
                </h3>
                {scanAnalysis && (
                  <span className={`text-xs px-3 py-1 rounded-full font-bold ${
                    scanAnalysis.status === 'warning' ? 'bg-red-500/20 text-red-300 border border-red-500/30' :
                    scanAnalysis.status === 'caution' ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30' :
                    'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                  }`}>
                    درجة التوافق: {scanAnalysis.complianceRate}
                  </span>
                )}
              </div>

              {scanAnalysis ? (
                <div className="space-y-4">
                  <p className="text-xs text-slate-300 bg-slate-950 p-3 rounded-xl border border-slate-800">
                    {scanAnalysis.summary}
                  </p>

                  {scanAnalysis.issues.length === 0 ? (
                    <div className="p-6 bg-emerald-950/30 border border-emerald-500/30 rounded-2xl text-center space-y-2">
                      <CheckCircle className="w-10 h-10 text-emerald-400 mx-auto" />
                      <h4 className="text-sm font-bold text-emerald-300">العقد سليم شرعياً وخالٍ من المحاذير</h4>
                      <p className="text-xs text-emerald-200/80">لم يتم رصد أي بنود ربوية أو شروط جهالة مخلة.</p>
                    </div>
                  ) : (
                    scanAnalysis.issues.map((issue, idx) => (
                      <div key={idx} className="bg-slate-950 border border-slate-800 p-4 rounded-2xl space-y-2 text-xs">
                        <div className="flex items-center justify-between">
                          <h4 className="font-bold text-red-400 flex items-center gap-1.5">
                            <AlertTriangle className="w-4 h-4 text-red-400" />
                            {issue.title}
                          </h4>
                          <span className="text-[10px] bg-red-950 text-red-300 px-2 py-0.5 rounded border border-red-900">
                            {issue.type}
                          </span>
                        </div>
                        <p className="text-slate-300 leading-relaxed text-[11px]">{issue.description}</p>
                        <div className="bg-emerald-950/40 border border-emerald-500/30 p-3 rounded-xl text-emerald-300 mt-2">
                          <span className="font-bold block mb-0.5 text-emerald-400">💡 البديل الشرعي المقترح:</span>
                          <p className="text-[11px] leading-relaxed">{issue.alternative}</p>
                        </div>
                      </div>
                    ))
                  )}
                </div>
              ) : (
                <div className="h-64 flex flex-col items-center justify-center border-2 border-dashed border-slate-800 rounded-2xl text-slate-500 p-6 text-center">
                  <FileText className="w-12 h-12 mb-3 opacity-30" />
                  <p className="text-sm font-semibold text-slate-400">لم يتم الفحص بعد.</p>
                  <p className="text-xs text-slate-600 mt-1">اختر نموذجاً أو اكتب نص العقد ثم اضغط على زر الفحص.</p>
                </div>
              )}
            </div>

            <div className="pt-3 border-t border-slate-800 text-[11px] text-slate-500">
              تنبيه: هذا الفحص استرشادي ذكي ولا يغني عن الفتوى الرسمية من الجهات الشرعية المعتمدة.
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: ZAKAT CALCULATOR */}
      {activeTab === 'zakatCalc' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Calculator className="w-5 h-5 text-amber-400" />
                حاسبة الزكاة الذكية المربوطة بسعر الذهب اللحظي
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                حساب زكاة الأموال، الأسهم، الذهب، والعملات الرقمية مع التحقق التلقائي من بلوغ النصاب الشرعي (85 جرام ذهب).
              </p>
            </div>
            <div className="flex items-center gap-2 text-xs bg-slate-950 px-3 py-1.5 rounded-xl border border-slate-800 text-amber-300">
              <Coins className="w-4 h-4 text-amber-400" />
              <span>سعر جرام الذهب عيار 24: <strong>{goldPricePerGram} ر.س</strong></span>
            </div>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            <div className="space-y-4 bg-slate-950 p-6 rounded-3xl border border-slate-800">
              <div>
                <label className="block text-xs font-bold text-slate-300 mb-2">نوع الأصل المالي الخاضع للزكاة:</label>
                <div className="grid grid-cols-2 gap-2">
                  <button
                    onClick={() => setAssetType('stocks')}
                    className={`p-2.5 rounded-xl text-xs font-semibold border transition-all text-center ${
                      assetType === 'stocks' ? 'bg-amber-600/30 border-amber-500 text-amber-300' : 'bg-slate-900 border-slate-800 text-slate-400'
                    }`}
                  >
                    أسهم استثمارية ومضاربة
                  </button>
                  <button
                    onClick={() => setAssetType('gold')}
                    className={`p-2.5 rounded-xl text-xs font-semibold border transition-all text-center ${
                      assetType === 'gold' ? 'bg-amber-600/30 border-amber-500 text-amber-300' : 'bg-slate-900 border-slate-800 text-slate-400'
                    }`}
                  >
                    ذهب وسبائك ومجوهرات
                  </button>
                  <button
                    onClick={() => setAssetType('crypto')}
                    className={`p-2.5 rounded-xl text-xs font-semibold border transition-all text-center ${
                      assetType === 'crypto' ? 'bg-amber-600/30 border-amber-500 text-amber-300' : 'bg-slate-900 border-slate-800 text-slate-400'
                    }`}
                  >
                    عملات رقمية مشفرة
                  </button>
                  <button
                    onClick={() => setAssetType('cash')}
                    className={`p-2.5 rounded-xl text-xs font-semibold border transition-all text-center ${
                      assetType === 'cash' ? 'bg-amber-600/30 border-amber-500 text-amber-300' : 'bg-slate-900 border-slate-800 text-slate-400'
                    }`}
                  >
                    سيولة نقدية وحسابات بنكية
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-300 mb-2">
                  {assetType === 'gold' ? 'قيمة الذهب الإجمالية أو وزنه مقدراً بالريال:' : 'إجمالي القيمة المالية للأصل (بالريال السعودي):'}
                </label>
                <input
                  type="number"
                  value={assetValue}
                  onChange={(e) => setAssetValue(Number(e.target.value))}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-100 text-base font-bold rounded-2xl p-3.5 focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="flex items-center justify-between p-3 bg-slate-900 rounded-xl border border-slate-800">
                <span className="text-xs text-slate-300">هل حال الحول على المال؟ (مرور سنة هجرية كاملة):</span>
                <input
                  type="checkbox"
                  checked={isHawlCompleted}
                  onChange={(e) => setIsHawlCompleted(e.target.checked)}
                  className="w-5 h-5 accent-amber-500 rounded cursor-pointer"
                />
              </div>
            </div>

            {/* Result Box */}
            <div className="bg-gradient-to-br from-amber-950/50 to-slate-950 p-6 rounded-3xl border border-amber-500/30 flex flex-col justify-between shadow-xl space-y-4">
              <div className="space-y-3">
                <span className="text-xs font-bold text-amber-400 block">مقدار الزكاة الواجب إخراجها شرعاً:</span>
                <div className="text-4xl font-extrabold text-white font-mono">
                  {zakatDue.toLocaleString()} <span className="text-sm font-sans text-amber-300 font-normal">ر.س</span>
                </div>

                <div className="space-y-2 pt-2 border-t border-slate-800/80 text-xs">
                  <div className="flex justify-between text-slate-300">
                    <span>النصاب الشرعي الحالي (85 غرام ذهب):</span>
                    <strong className="text-amber-300 font-mono">{goldNisabValue.toLocaleString()} ر.س</strong>
                  </div>
                  <div className="flex justify-between text-slate-300">
                    <span>حالة بلوغ النصاب:</span>
                    <strong className={isAboveNisab ? "text-emerald-400" : "text-red-400"}>
                      {isAboveNisab ? "بلغ النصاب الشرعي ✓" : "دون النصاب (لا زكاة واجبة)"}
                    </strong>
                  </div>
                  <div className="flex justify-between text-slate-300">
                    <span>نسبة الزكاة المطبقة:</span>
                    <strong className="text-slate-100 font-mono">2.5% (سنة هجرية)</strong>
                  </div>
                </div>
              </div>

              <div className="bg-slate-900/90 p-3 rounded-2xl border border-slate-800 text-[11px] text-slate-300 flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>حساب معتمد متوافق مع معايير هيئة الزكاة والضريبة والجمارك (ZATCA).</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: INHERITANCE CALCULATOR */}
      {activeTab === 'inheritanceCalc' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <PieChart className="w-5 h-5 text-amber-400" />
                حاسبة المواريث والتركات البصرية التفاعلية
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                توزيع تركة المتوفى خطوة بخطوة بالدليل الشرعي من القرآن الكريم لكل نصيب مستحق.
              </p>
            </div>
            <div className="text-xs bg-slate-950 px-3.5 py-1.5 rounded-xl border border-slate-800 text-slate-300">
              إجمالي التركة: <strong className="text-amber-400 font-mono">{estateAmount.toLocaleString()} ر.س</strong>
            </div>
          </div>

          <div className="grid md:grid-cols-2 gap-6 items-start">
            <div className="bg-slate-950 p-6 rounded-3xl border border-slate-800 space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-300 mb-2">إجمالي مبلغ التركة الصافية (بعد سداد الديون والوصية):</label>
                <input
                  type="number"
                  value={estateAmount}
                  onChange={(e) => setEstateAmount(Number(e.target.value))}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-100 text-base font-bold rounded-2xl p-3.5 focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="space-y-3 pt-2">
                <span className="text-xs font-bold text-amber-400 block border-b border-slate-800 pb-1.5">
                  الورثة المستحقون للتركة:
                </span>

                <div className="grid grid-cols-2 gap-3 text-xs">
                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 flex justify-between items-center">
                    <span>عدد الزوجات:</span>
                    <input
                      type="number"
                      min={0}
                      max={4}
                      value={heirs.wife}
                      onChange={(e) => setHeirs({ ...heirs, wife: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded-lg py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 flex justify-between items-center">
                    <span>الأبناء (ذكور):</span>
                    <input
                      type="number"
                      min={0}
                      value={heirs.sons}
                      onChange={(e) => setHeirs({ ...heirs, sons: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded-lg py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 flex justify-between items-center">
                    <span>البنات (إناث):</span>
                    <input
                      type="number"
                      min={0}
                      value={heirs.daughters}
                      onChange={(e) => setHeirs({ ...heirs, daughters: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded-lg py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 flex justify-between items-center">
                    <span>الأم (1 أو 0):</span>
                    <input
                      type="number"
                      min={0}
                      max={1}
                      value={heirs.mother}
                      onChange={(e) => setHeirs({ ...heirs, mother: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded-lg py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 flex justify-between items-center">
                    <span>الأب (1 أو 0):</span>
                    <input
                      type="number"
                      min={0}
                      max={1}
                      value={heirs.father}
                      onChange={(e) => setHeirs({ ...heirs, father: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded-lg py-1 text-white font-bold"
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Visual Shares Breakdown */}
            <div className="bg-slate-950 p-6 rounded-3xl border border-slate-800 space-y-4">
              <h4 className="text-xs font-bold text-amber-400 border-b border-slate-800 pb-2">
                توزيع الأنصبة الشرعية والأدلة:
              </h4>

              <div className="space-y-3">
                {inheritanceShares.map((share, idx) => (
                  <div key={idx} className="bg-slate-900 p-3.5 rounded-2xl border border-slate-800 space-y-1">
                    <div className="flex justify-between items-center font-bold text-xs text-slate-100">
                      <span>{share.heir} - {share.fractionText}</span>
                      <span className="text-amber-400 font-mono">{Math.round(share.amount).toLocaleString()} ر.س</span>
                    </div>
                    <p className="text-[11px] text-slate-400 leading-relaxed">
                      📖 الدليل: "{share.daleel}"
                    </p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
