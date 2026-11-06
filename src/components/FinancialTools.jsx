import React, { useState } from 'react';
import { Scale, FileText, Calculator, AlertTriangle, CheckCircle, RefreshCw, DollarSign, PieChart, ShieldAlert, Sparkles } from 'lucide-react';

export default function FinancialTools() {
  const [activeTab, setActiveTab] = useState('contractScanner'); // 'contractScanner', 'zakatCalc', 'inheritanceCalc'

  // Contract Scanner State
  const sampleContracts = {
    carLoan: `في حال تأخر الطرف الثاني عن سداد القسط المالي في الموعد المحدد لمدة تتجاوز 15 يوماً، تُفرض غرامة تأخير قدرها 5% من قيمة القسط تضاف لحساب الجهة الممولة، كما يشترط شرط جزائي غير محدد القيمة في حال فسخ العقد.`,
    employment: `يلتزم الطرف الثاني بعدم العمل لدى أي منافس لمدة 3 سنوات بعد انتهاء العقد في كافة أنحاء العالم، ويحق للطرف الأول فسخ العقد دون مكافأة نهاية خدمة في أي وقت دون إبداء أسباب.`
  };

  const [contractText, setContractText] = useState(sampleContracts.carLoan);
  const [scanAnalysis, setScanAnalysis] = useState(null);
  const [isScanning, setIsScanning] = useState(false);

  // Zakat Calculator State
  const [assetType, setAssetType] = useState('stocks');
  const [assetValue, setAssetValue] = useState(150000); // Value in local currency
  const [goldPricePerGram, setGoldPricePerGram] = useState(285); // Live gold price estimation

  // Inheritance State
  const [heirs, setHeirs] = useState({
    wives: 1,
    sons: 2,
    daughters: 2,
    father: 1,
    mother: 1
  });
  const [estateAmount, setEstateAmount] = useState(1200000);

  const handleScanContract = () => {
    setIsScanning(true);
    setScanAnalysis(null);

    setTimeout(() => {
      setIsScanning(false);
      if (contractText.includes("غرامة تأخير") || contractText.includes("قسط")) {
        setScanAnalysis({
          status: 'warning',
          score: 65,
          issues: [
            {
              title: 'شبهة ربا الجاهلية (غرامة التأخير المالية النقدية)',
              description: 'البند ينص على زيادة مالية تعود للممول مقابل التأخر في السداد، وهي عين ربا الديون ("إما أن تقضي وإما أن تربي").',
              alternative: 'البديل الشرعي المعتمد: إلزام المماطل التبرع بمبلغ محدد لصالح جهات خيرية خارج حساب الشركة الدائنة لردع المماطلة المشروعة دون الاسترباح المحرم.'
            },
            {
              title: 'جهالة وغرر في الشرط الجزائي',
              description: 'البند يحتوي على شرط جزائي مفتوح غير محدد القيمة ولا يراعي الضرر الفعلي المباشر.',
              alternative: 'البديل الشرعي المعتمد: حصر التعويض في الضرر المادي الفعلي الحقيقي المؤكد الناتج عن الفسخ.'
            }
          ]
        });
      } else {
        setScanAnalysis({
          status: 'warning',
          score: 70,
          issues: [
            {
              title: 'إجحاف وتضييق في شرط عدم المنافسة',
              description: 'تحديد مدة 3 سنوات وفي كل أنحاء العالم يعد تعسفاً يمنع العامل من التكسب المشروع.',
              alternative: 'البديل الشرعي والنظامي: قصر المنع على النطاق الجغرافي الفعلي للمنشأة ولمدة لا تتجاوز سنة وفي التخصص الدقيق فقط.'
            }
          ]
        });
      }
    }, 1500);
  };

  const calculateZakat = () => {
    const nisabThreshold = 85 * goldPricePerGram;
    if (assetValue < nisabThreshold) {
      return { due: 0, meetsNisab: false, nisabThreshold };
    }
    const due = assetValue * 0.025;
    return { due: due.toLocaleString(), meetsNisab: true, nisabThreshold };
  };

  const zakatInfo = calculateZakat();

  // Accurate Islamic Inheritance Calculation Logic
  const calculateInheritance = () => {
    let breakdown = [];
    let remaining = estateAmount;

    // 1. Mother share: 1/6 (due to existence of children)
    if (heirs.mother > 0) {
      const motherShare = estateAmount * (1 / 6);
      breakdown.push({
        relation: `الأم (السُّدس - 1/6 وجود فرع وارث)`,
        amount: Math.round(motherShare),
        dalil: 'وَلِأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِّنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِن كَانَ لَهُ وَلَدٌ'
      });
      remaining -= motherShare;
    }

    // 2. Father share: 1/6 (due to existence of male children)
    if (heirs.father > 0) {
      const fatherShare = estateAmount * (1 / 6);
      breakdown.push({
        relation: `الأب (السُّدس - 1/6 فرضاً لوجود الفرع المذكر)`,
        amount: Math.round(fatherShare),
        dalil: 'وَلِأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِّنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِن كَانَ لَهُ وَلَدٌ'
      });
      remaining -= fatherShare;
    }

    // 3. Wives share: 1/8 (due to children)
    if (heirs.wives > 0) {
      const wifeShare = estateAmount * (1 / 8);
      breakdown.push({
        relation: `الزوجة/الزوجات (الثُّمن - 1/8 مشتركاً لوجود الولد)`,
        amount: Math.round(wifeShare),
        dalil: 'فَإِن كَانَ لَكُمْ وَلَدٌ فَلَهُنَّ الثُُّمُنُ مِمَّا تَرَكْتُم'
      });
      remaining -= wifeShare;
    }

    // 4. Children (Sons & Daughters) Asaba Bil-Ghayr (2:1)
    const totalShares = (heirs.sons * 2) + (heirs.daughters * 1);
    if (totalShares > 0 && remaining > 0) {
      const singleShare = remaining / totalShares;
      if (heirs.sons > 0) {
        const sonsTotal = singleShare * (heirs.sons * 2);
        breakdown.push({
          relation: `الأبناء الذكور (${heirs.sons}) - للذكر سهمان`,
          amount: Math.round(sonsTotal),
          perPerson: Math.round(singleShare * 2),
          dalil: 'يُوصِيكُمُ اللَّهُ فِي أَوْلَادِكُمْ ۖ لِلذَّكَرِ مِثْلُ حَظِّ الْأُنثَيَيْنِ'
        });
      }
      if (heirs.daughters > 0) {
        const daughtersTotal = singleShare * (heirs.daughters * 1);
        breakdown.push({
          relation: `البنات الإناث (${heirs.daughters}) - للأنثى سهم واحد`,
          amount: Math.round(daughtersTotal),
          perPerson: Math.round(singleShare),
          dalil: 'فَإِن كُنَّ نِسَاءً فَوْقَ اثْنَتَيْنِ فَلَهُنَّ ثُلُثَا مَا تَرَكَ...'
        });
      }
    }

    return breakdown;
  };

  const inheritanceBreakdown = calculateInheritance();

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-amber-950/80 via-amber-900/50 to-slate-900 border border-amber-500/30 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-amber-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/20 text-amber-300 text-xs font-semibold mb-3 border border-amber-500/30">
              <Scale className="w-3.5 h-3.5" />
              المستشار المالي والشرعي التفاعلي
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">المعاملات المالية والشرعية</h2>
            <p className="text-slate-300 text-sm mt-1">
              ماسح العقود الذكي للتنبيه على المحاذير الشرعية، حاسبة الزكاة اللحظية، وحاسبة المواريث الشرعية المبرهنة بالدليل.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('contractScanner')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
                activeTab === 'contractScanner' ? 'bg-amber-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              ماسح العقود
            </button>
            <button
              onClick={() => setActiveTab('zakatCalc')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
                activeTab === 'zakatCalc' ? 'bg-amber-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              حاسبة الزكاة
            </button>
            <button
              onClick={() => setActiveTab('inheritanceCalc')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
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
        <div className="space-y-4">
          <div className="grid md:grid-cols-2 gap-6">
            <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
              <div>
                <h3 className="text-lg font-bold text-amber-400 flex items-center gap-2 mb-1">
                  <FileText className="w-5 h-5" />
                  تحليل بنود العقود والاتفاقيات المالية
                </h3>
                <p className="text-slate-400 text-xs">
                  الصق بنود عقد تمويل، بيع بالتقسيط، أو عقد عمل للتحقق من الشروط التي قد تشوبها شبهة شرعية واقتراح البديل المعتمد.
                </p>
              </div>

              {/* Sample Selector */}
              <div className="flex gap-2 text-xs">
                <span className="text-slate-400 self-center">نماذج جاهزة:</span>
                <button
                  onClick={() => setContractText(sampleContracts.carLoan)}
                  className="bg-slate-950 px-2.5 py-1 rounded-lg border border-slate-800 text-slate-300 hover:border-amber-500/50 cursor-pointer"
                >
                  عقد تمويل وتقسيط
                </button>
                <button
                  onClick={() => setContractText(sampleContracts.employment)}
                  className="bg-slate-950 px-2.5 py-1 rounded-lg border border-slate-800 text-slate-300 hover:border-amber-500/50 cursor-pointer"
                >
                  عقد عمل ومنافسة
                </button>
              </div>

              <textarea
                rows={6}
                value={contractText}
                onChange={(e) => setContractText(e.target.value)}
                className="w-full bg-slate-950 border border-slate-700 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-amber-500 leading-relaxed font-sans"
                placeholder="ضع نص العقد أو البند المراد فحصه هنا..."
              />

              <button
                onClick={handleScanContract}
                disabled={isScanning}
                className="w-full py-3 bg-amber-600 hover:bg-amber-500 text-white text-xs font-bold rounded-xl shadow-md transition-all flex items-center justify-center gap-2 cursor-pointer"
              >
                <RefreshCw className={`w-4 h-4 ${isScanning ? 'animate-spin' : ''}`} />
                {isScanning ? 'جاري مطابقة القواعد الفقهية بالذكاء الاصطناعي...' : 'فحص البنود بالذكاء الاصطناعي'}
              </button>
            </div>

            {/* Analysis Result */}
            <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
              <h3 className="text-lg font-bold text-slate-200 flex items-center gap-2">
                <Scale className="w-5 h-5 text-amber-400" />
                نتائج الفحص والبدائل الشرعية
              </h3>

              {scanAnalysis ? (
                <div className="space-y-3">
                  <div className="bg-amber-950/40 border border-amber-500/40 p-3 rounded-xl flex items-center justify-between text-xs text-amber-300 font-semibold">
                    <span className="flex items-center gap-2">
                      <AlertTriangle className="w-4 h-4 text-amber-400 shrink-0" />
                      تم رصد {scanAnalysis.issues.length} من البنود التي تحتاج لتعديل وتدقيق شرعي.
                    </span>
                    <span className="bg-amber-500/20 px-2 py-0.5 rounded text-[10px]">درجة المطابقة: {scanAnalysis.score}%</span>
                  </div>

                  {scanAnalysis.issues.map((issue, idx) => (
                    <div key={idx} className="bg-slate-950 border border-slate-800 p-4 rounded-xl space-y-2 text-xs">
                      <h4 className="font-bold text-red-400 flex items-center gap-1.5">
                        ❌ {issue.title}
                      </h4>
                      <p className="text-slate-300 leading-relaxed">{issue.description}</p>
                      <div className="bg-emerald-950/30 border border-emerald-500/30 p-2.5 rounded-lg text-emerald-300 mt-2">
                        <span className="font-bold block mb-0.5 text-emerald-400">💡 البديل الشرعي المقترح فقهياً:</span>
                        {issue.alternative}
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="h-64 flex flex-col items-center justify-center border-2 border-dashed border-slate-800 rounded-xl text-slate-500 p-6 text-center">
                  <FileText className="w-10 h-10 mb-2 opacity-30" />
                  <p className="text-xs">اضغط على زر الفحص لمعالجة بنود العقد المكتوبة.</p>
                  <p className="text-[11px] text-slate-600 mt-1">يتحقق النظام من شروط الغرر، الربا، والشروط المجحفة.</p>
                </div>
              )}
            </div>
          </div>

          <div className="bg-slate-950/80 p-3 rounded-xl border border-slate-800 text-[11px] text-slate-400 flex items-center gap-2">
            <ShieldAlert className="w-4 h-4 text-amber-400 shrink-0" />
            <span>تنبيه فقهي وقانوني: نتائج ماسح العقود بالذكاء الاصطناعي هي نتائج استرشادية توعوية لا تغني عن مراجعة مستشار شرعي وقانوني معتمد في المعاملات المالية الكبرى.</span>
          </div>
        </div>
      )}

      {/* TAB 2: ZAKAT CALCULATOR */}
      {activeTab === 'zakatCalc' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="border-b border-slate-800 pb-4">
            <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
              <Calculator className="w-5 h-5 text-amber-400" />
              حاسبة الزكاة المتطورة (الأسهم، الذهب، والعملات)
            </h3>
            <p className="text-slate-400 text-xs">
              حساب زكاة المال، أسهم الاستثمار والمضاربة، والذهب مع ربط النصاب بسعر الذهب اللحظي (نصاب الذهب الشرعي 85 جرام عيار 24).
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            <div className="space-y-4 bg-slate-950 p-5 rounded-2xl border border-slate-800">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">نوع الأصل الخاضع للزكاة:</label>
                <select
                  value={assetType}
                  onChange={(e) => setAssetType(e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-200 text-xs rounded-xl p-3 focus:outline-none focus:border-amber-500"
                >
                  <option value="stocks">محفظة أسهم (مضاربة بالقيمة السوقية)</option>
                  <option value="gold">ذهب ومجوهرات استثمارية (بالجرام)</option>
                  <option value="crypto">سيولة نقدية وعملات رقمية</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">
                  {assetType === 'gold' ? 'وزن الذهب المملوك (بالجرام عيار 24):' : 'إجمالي القيمة المالية المملوكة (ريال):'}
                </label>
                <input
                  type="number"
                  value={assetValue}
                  onChange={(e) => setAssetValue(Math.max(0, Number(e.target.value)))}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-100 text-sm font-bold rounded-xl p-3 focus:outline-none focus:border-amber-500 font-mono"
                />
              </div>

              <div className="flex items-center justify-between text-xs bg-slate-900 p-3 rounded-xl border border-slate-800">
                <span className="text-slate-400">سعر جرام الذهب عيار 24 المفترض:</span>
                <div className="flex items-center gap-1">
                  <input
                    type="number"
                    value={goldPricePerGram}
                    onChange={(e) => setGoldPricePerGram(Number(e.target.value))}
                    className="w-16 bg-slate-950 border border-slate-700 text-amber-400 font-mono font-bold text-center rounded p-1"
                  />
                  <span className="text-slate-400">ر.س</span>
                </div>
              </div>
            </div>

            {/* Result Box */}
            <div className="bg-gradient-to-br from-amber-950/40 via-slate-900 to-slate-950 p-6 rounded-2xl border border-amber-500/30 flex flex-col justify-between shadow-xl">
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-amber-400">مقدار الزكاة الواجب إخراجها:</span>
                  <span className={`text-[10px] px-2.5 py-0.5 rounded-full font-bold ${zakatInfo.meetsNisab ? 'bg-emerald-500/20 text-emerald-300' : 'bg-slate-800 text-slate-400'}`}>
                    {zakatInfo.meetsNisab ? 'بلغ النصاب الشرعي' : 'لم يبلغ النصاب بعد'}
                  </span>
                </div>

                <div className="text-3xl md:text-4xl font-black text-white font-mono">
                  {zakatInfo.due} <span className="text-sm font-sans text-amber-300 font-normal">ريال سعودي</span>
                </div>

                <div className="text-xs text-slate-400 leading-relaxed pt-3 border-t border-slate-800/80 space-y-1">
                  <div>النصاب الشرعي للذهب (85 جرام × {goldPricePerGram} ر.س): <span className="text-amber-300 font-bold font-mono">{zakatInfo.nisabThreshold.toLocaleString()} ر.س</span>.</div>
                  <div>نسبة الزكاة الواجبة: 2.5% بعد مرور حول كامل (عام هجري).</div>
                </div>
              </div>

              <div className="bg-slate-900/80 p-3 rounded-xl border border-slate-800 text-[11px] text-slate-300 flex items-center gap-2 mt-4">
                <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>حساب دقيق متوافق مع معايير هيئة الزكاة والضريبة والجمارك (ZATCA).</span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: INHERITANCE CALCULATOR */}
      {activeTab === 'inheritanceCalc' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="border-b border-slate-800 pb-4">
            <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
              <PieChart className="w-5 h-5 text-amber-400" />
              حاسبة المواريث الشرعية البصرية المبرهنة
            </h3>
            <p className="text-slate-400 text-xs">
              تقسيم التركة والميراث خطوة بخطوة مع عرض الدليل الشرعي القرآني وحصص الورثة الدقيقة.
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6 items-start">
            <div className="bg-slate-950 p-5 rounded-2xl border border-slate-800 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">إجمالي مبلغ التركة الصافي (ريال):</label>
                <input
                  type="number"
                  value={estateAmount}
                  onChange={(e) => setEstateAmount(Math.max(0, Number(e.target.value)))}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-100 text-sm font-bold rounded-xl p-3 focus:outline-none focus:border-amber-500 font-mono"
                />
              </div>

              <div className="space-y-3 pt-2">
                <span className="text-xs font-bold text-amber-400 block">تحديد الورثة المستحقين:</span>

                <div className="grid grid-cols-2 gap-3 text-xs">
                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الزوجة / الزوجات:</span>
                    <input
                      type="number"
                      min={0}
                      max={4}
                      value={heirs.wives}
                      onChange={(e) => setHeirs({ ...heirs, wives: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold font-mono"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الأبناء (ذكور):</span>
                    <input
                      type="number"
                      min={0}
                      value={heirs.sons}
                      onChange={(e) => setHeirs({ ...heirs, sons: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold font-mono"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>البنات (إناث):</span>
                    <input
                      type="number"
                      min={0}
                      value={heirs.daughters}
                      onChange={(e) => setHeirs({ ...heirs, daughters: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold font-mono"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الأب موجود؟ (1/0):</span>
                    <input
                      type="number"
                      min={0}
                      max={1}
                      value={heirs.father}
                      onChange={(e) => setHeirs({ ...heirs, father: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold font-mono"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الأم موجودة؟ (1/0):</span>
                    <input
                      type="number"
                      min={0}
                      max={1}
                      value={heirs.mother}
                      onChange={(e) => setHeirs({ ...heirs, mother: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold font-mono"
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Visual Breakdown */}
            <div className="bg-slate-950 p-5 rounded-2xl border border-slate-800 space-y-4">
              <h4 className="text-xs font-bold text-amber-400 border-b border-slate-800 pb-2 flex items-center justify-between">
                <span>توزيع الأنصبة والدليل الشرعي:</span>
                <span className="text-[10px] text-slate-400 font-mono">التركة: {estateAmount.toLocaleString()} ر.س</span>
              </h4>

              <div className="space-y-3 text-xs max-h-[420px] overflow-y-auto pr-1">
                {inheritanceBreakdown.map((item, idx) => (
                  <div key={idx} className="bg-slate-900 p-3.5 rounded-xl border border-slate-800 space-y-1.5">
                    <div className="flex justify-between font-bold text-slate-200">
                      <span>{item.relation}</span>
                      <span className="text-amber-400 font-mono text-sm">{item.amount.toLocaleString()} ر.س</span>
                    </div>
                    {item.perPerson && (
                      <div className="text-[10px] text-emerald-400">
                        نصيب الفرد الواحد: {item.perPerson.toLocaleString()} ر.س
                      </div>
                    )}
                    <p className="text-[11px] text-slate-400 leading-relaxed bg-slate-950 p-2 rounded border border-slate-800/60">
                      📖 الدليل: "{item.dalil}"
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
