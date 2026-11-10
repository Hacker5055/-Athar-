import React, { useState } from 'react';
import { Scale, FileText, Calculator, AlertTriangle, CheckCircle, RefreshCw, DollarSign, PieChart, Info, ShieldCheck } from 'lucide-react';

export default function FinancialTools() {
  const [activeTab, setActiveTab] = useState('contractScanner'); // 'contractScanner', 'zakatCalc', 'inheritanceCalc'

  // Contract Scanner State
  const sampleContracts = {
    carInstallment: `في حال تأخر الطرف الثاني عن سداد القسط المالي في الموعد المحدد لمدة تتجاوز 15 يوماً، تُفرض غرامة تأخير قدرها 5% من قيمة القسط تضاف لحساب الشركة الدائنة كتعويض مالي، ويتحمل المشتري أي نفقات تحصيل غير محددة.`,
    employment: `يلتزم الطرف الثاني بعدم العمل لدى أي منافس لمدة 10 سنوات بعد انتهاء العقد في كافة أنحاء العالم، وتخضع أي نزاعات لغرامة جزائية مسبقة بمقدار راتب 3 سنوات دون حاجة لإثبات الضرر الفعلي.`,
    mudaraba: `اتفق الطرفان على المضاربة برأس مال 100,000 ريال، ويضمن الطرف الثاني (المضارب) استرداد رأس المال كاملاً للطرف الأول مع ضمان عائد ربحي ثابت لا يقل عن 12% سنوياً مهما كانت نتائج النشاط التجاري.`
  };

  const [contractText, setContractText] = useState(sampleContracts.carInstallment);
  const [scanAnalysis, setScanAnalysis] = useState(null);

  // Zakat Calculator State
  const [assetType, setAssetType] = useState('stocks');
  const [assetValue, setAssetValue] = useState(100000); // Value in local currency
  const [goldPricePerGram, setGoldPricePerGram] = useState(285); // Live gold price estimation SAR
  const [goldKarat, setGoldKarat] = useState(24);

  // Inheritance State
  const [heirs, setHeirs] = useState({
    wife: 1,
    sons: 2,
    daughters: 1,
    father: 1,
    mother: 1
  });
  const [estateAmount, setEstateAmount] = useState(1200000);

  const handleScanContract = () => {
    if (contractText.includes('غرامة تأخير') || contractText.includes('5%')) {
      setScanAnalysis({
        status: 'warning',
        contractType: 'عقد تقسيط / بيع بالمرابحة',
        issues: [
          {
            title: 'شبهة ربا الجاهلية (غرامة التأخير المالية المضافة للدائن)',
            description: 'البند ينص على فرض زيادة مالية على المدين المعسر أو المتأخر تعود لصالح الشركة المقرضة/البائعة، وهذا هو عين ربا الديون المحرم بالإجماع (إما أن تقضي وإما أن تربي).',
            alternative: 'البديل الشرعي المعتمد: اشتراط التزام المدين بالتبرع بمبلغ غرامة مقطوع لحساب جمعية خيرية مرخصة لردع المماطل، دون أن يدخل سنت واحد في حساب الشركة البائعة.'
          },
          {
            title: 'جهالة في نفقات التحصيل والشرط الجزائي',
            description: 'عبارة (نفقات تحصيل غير محددة) تفتح باب الغرر والنزاع وتتعارض مع مبدأ الشفافية في العقود المالية الإسلامية.',
            alternative: 'البديل الشرعي: تحديد سقف التكاليف القضائية الفعلية المباشرة الموثقة فقط.'
          }
        ]
      });
    } else if (contractText.includes('يضمن') || contractText.includes('ثابت')) {
      setScanAnalysis({
        status: 'danger',
        contractType: 'عقد مضاربة / استثمار تجاري',
        issues: [
          {
            title: 'بطلان عقد المضاربة لضمان رأس المال ونسبة الربح المقطوعة',
            description: 'في الفقه الإسلامي، المضارب أمين لا يضمن رأس المال إلا في حال التعدي أو التفريط، واشتراط ربح ثابت مقطوع يقلب العقد من مضاربة شرعية إلى قرض بفائدة ربوية صريحة.',
            alternative: 'البديل الشرعي: اقتسام الأرباح بنسبة شائعة (مثلاً 60% لرب المال و 40% للمضارب من الربح المحقق فعلاً)، وتحمّل الخسارة المالية من رأس المال إن لم يكن هناك إهمال.'
          }
        ]
      });
    } else {
      setScanAnalysis({
        status: 'info',
        contractType: 'عقد عمل ومنافسة',
        issues: [
          {
            title: 'غلو وإجحاف في شرط منع المنافسة (مدة مفرطة ونطاق عالمي)',
            description: 'منع العامل لمدة 10 سنوات وعلى مستوى العالم فيه تضييق على حق التكسب المشروع الذي كفلته الشريعة، والشرط الجزائي دون إثبات الضرر باطل شرعاً ونظاماً.',
            alternative: 'البديل الشرعي والنظامي: قصر المنع على المنافسين المباشرين في نفس المدينة/المنطقة ولفترة معقولة (لا تتجاوز سنة أو سنتين بحسب المادة 83 من نظام العمل السعودي).'
          }
        ]
      });
    }
  };

  const calculateZakat = () => {
    // Standard Zakat rate 2.5% for lunar year
    let zakatableAmount = assetValue;
    if (assetType === 'gold') {
      const goldGramEquivalent = assetValue * (goldKarat / 24);
      // Nisab is 85 grams pure gold
      if (goldGramEquivalent < 85) return 0;
      return Math.round(goldGramEquivalent * goldPricePerGram * 0.025).toLocaleString();
    }
    const nisabValue = 85 * goldPricePerGram;
    if (zakatableAmount < nisabValue) return 0;
    return Math.round(zakatableAmount * 0.025).toLocaleString();
  };

  // Inheritance calculation breakdown
  const calculateInheritance = () => {
    let wifeShare = 0;
    let motherShare = 0;
    let fatherShare = 0;
    let childrenShare = 0;

    const hasChildren = heirs.sons > 0 || heirs.daughters > 0;

    // Wife: 1/8 if children exist, else 1/4
    wifeShare = heirs.wife > 0 ? (hasChildren ? estateAmount * 0.125 : estateAmount * 0.25) : 0;

    // Mother: 1/6 if children exist or multiple siblings, else 1/3
    motherShare = heirs.mother > 0 ? (hasChildren ? estateAmount * (1 / 6) : estateAmount * (1 / 3)) : 0;

    // Father: 1/6 with sons
    fatherShare = heirs.father > 0 ? estateAmount * (1 / 6) : 0;

    // Residue for children (Asaba: 2 parts for son, 1 for daughter)
    const residue = Math.max(0, estateAmount - (wifeShare + motherShare + fatherShare));
    childrenShare = residue;

    const totalHeads = (heirs.sons * 2) + heirs.daughters;
    const onePart = totalHeads > 0 ? residue / totalHeads : 0;
    const sonShare = onePart * 2;
    const daughterShare = onePart;

    return {
      wifeShare: Math.round(wifeShare),
      motherShare: Math.round(motherShare),
      fatherShare: Math.round(fatherShare),
      residue: Math.round(residue),
      sonShare: Math.round(sonShare),
      daughterShare: Math.round(daughterShare)
    };
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
              المستشار الفقهي والمالي الذكي
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">المعاملات المالية والشرعية</h2>
            <p className="text-slate-300 text-sm mt-1">
              ماسح العقود الذكي للتنبيه على الشروط الشرعية، حاسبة الزكاة للأسهم والذهب، وحاسبة المواريث البصرية.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('contractScanner')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'contractScanner' ? 'bg-amber-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              ماسح العقود
            </button>
            <button
              onClick={() => setActiveTab('zakatCalc')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'zakatCalc' ? 'bg-amber-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              حاسبة الزكاة
            </button>
            <button
              onClick={() => setActiveTab('inheritanceCalc')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
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
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
            <div>
              <h3 className="text-lg font-bold text-amber-400 flex items-center gap-2 mb-1">
                <FileText className="w-5 h-5" />
                تحليل بنود العقود والاتفاقيات
              </h3>
              <p className="text-slate-400 text-xs">
                الصق نص عقد العمل، قسط الشراء، أو التمويل البنكي للتحقق من خلوه من المحاذير الشرعية واقتراح البدائل.
              </p>
            </div>

            {/* Contract Samples */}
            <div>
              <span className="text-xs text-slate-400 block mb-1.5 font-medium">نماذج عقود تجريبية جاهزة للفحص:</span>
              <div className="grid grid-cols-3 gap-2">
                <button
                  onClick={() => { setContractText(sampleContracts.carInstallment); setScanAnalysis(null); }}
                  className="py-1.5 px-2 bg-slate-950 hover:bg-slate-800 border border-slate-800 text-[11px] rounded-lg text-slate-300"
                >
                  عقد تقسيط سيارة
                </button>
                <button
                  onClick={() => { setContractText(sampleContracts.mudaraba); setScanAnalysis(null); }}
                  className="py-1.5 px-2 bg-slate-950 hover:bg-slate-800 border border-slate-800 text-[11px] rounded-lg text-slate-300"
                >
                  عقد استثمار ومضاربة
                </button>
                <button
                  onClick={() => { setContractText(sampleContracts.employment); setScanAnalysis(null); }}
                  className="py-1.5 px-2 bg-slate-950 hover:bg-slate-800 border border-slate-800 text-[11px] rounded-lg text-slate-300"
                >
                  عقد عمل ومنافسة
                </button>
              </div>
            </div>

            <textarea
              rows={6}
              value={contractText}
              onChange={(e) => setContractText(e.target.value)}
              className="w-full bg-slate-950 border border-slate-700 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-amber-500 leading-relaxed font-sans"
              placeholder="ضع نص العقد هنا..."
            />

            <button
              onClick={handleScanContract}
              className="w-full py-3 bg-amber-600 hover:bg-amber-500 text-white text-xs font-bold rounded-xl shadow-md transition-all flex items-center justify-center gap-2"
            >
              <RefreshCw className="w-4 h-4" />
              فحص البنود بالذكاء الاصطناعي الفقهي
            </button>

            <div className="bg-slate-950/70 p-3 rounded-xl border border-slate-800 text-[11px] text-slate-400 flex items-start gap-2">
              <ShieldCheck className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
              <p>تنبيه إخلاء مسؤولية: هذا الفحص استرشادي توعوي مبني على معايير الأيوفي (AAOIFI) ولا يُغني عن مراجعة الهيئات الشرعية المعتمدة.</p>
            </div>
          </div>

          {/* Analysis Result */}
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-4">
            <h3 className="text-lg font-bold text-slate-200 flex items-center gap-2">
              <Scale className="w-5 h-5 text-amber-400" />
              نتائج الفحص والبدائل الشرعية
            </h3>

            {scanAnalysis ? (
              <div className="space-y-3 animate-fade-in">
                <div className="bg-amber-950/40 border border-amber-500/40 p-3 rounded-xl flex items-center justify-between text-xs text-amber-300 font-semibold">
                  <div className="flex items-center gap-2">
                    <AlertTriangle className="w-4 h-4 text-amber-400 shrink-0" />
                    تم رصد {scanAnalysis.issues.length} من الملاحظات الشرعية/النظامية.
                  </div>
                  <span className="text-[10px] bg-amber-500/20 px-2 py-0.5 rounded text-amber-300 border border-amber-500/30">
                    {scanAnalysis.contractType}
                  </span>
                </div>

                {scanAnalysis.issues.map((issue, idx) => (
                  <div key={idx} className="bg-slate-950 border border-slate-800 p-4 rounded-xl space-y-2 text-xs">
                    <h4 className="font-bold text-red-400 flex items-center gap-1.5">
                      ❌ {issue.title}
                    </h4>
                    <p className="text-slate-300 leading-relaxed">{issue.description}</p>
                    <div className="bg-emerald-950/30 border border-emerald-500/30 p-2.5 rounded-lg text-emerald-300 mt-2">
                      <span className="font-bold block mb-0.5">💡 البديل الشرعي المعتمد:</span>
                      {issue.alternative}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="h-64 flex flex-col items-center justify-center border-2 border-dashed border-slate-800 rounded-xl text-slate-500 p-6 text-center">
                <FileText className="w-10 h-10 mb-2 opacity-30" />
                <p className="text-xs">اضغط على زر الفحص لمعالجة بنود العقد المكتوبة.</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 2: ZAKAT CALCULATOR */}
      {activeTab === 'zakatCalc' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-800 pb-3">
            <div>
              <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                <Calculator className="w-5 h-5 text-amber-400" />
                حاسبة الزكاة المتطورة (الأسهم، الذهب، والعملات)
              </h3>
              <p className="text-slate-400 text-xs">
                حساب زكاة المال والأسهم الاستثمارية والمضاربة مع الربط بسعر الذهب اللحظي (النصاب الشرعي 85 جرام ذهب خالص).
              </p>
            </div>
            <div className="bg-amber-500/10 border border-amber-500/30 px-3 py-1.5 rounded-xl text-xs text-amber-300 font-semibold shrink-0">
              النصاب: {(85 * goldPricePerGram).toLocaleString()} ر.س
            </div>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            <div className="space-y-4 bg-slate-950 p-5 rounded-2xl border border-slate-800">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">نوع الأصل المالي الخاضع للزكاة:</label>
                <select
                  value={assetType}
                  onChange={(e) => setAssetType(e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-200 text-xs rounded-xl p-3 focus:outline-none focus:border-amber-500"
                >
                  <option value="stocks">أسهم مضاربة وتجارة (تقوّم بسعر السوق)</option>
                  <option value="gold">ذهب ومجوهرات استثمارية مدخرة</option>
                  <option value="crypto">عملات رقمية وسيولة نقدية في الحسابات</option>
                </select>
              </div>

              {assetType === 'gold' && (
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-2">عيار الذهب:</label>
                  <select
                    value={goldKarat}
                    onChange={(e) => setGoldKarat(Number(e.target.value))}
                    className="w-full bg-slate-900 border border-slate-700 text-slate-200 text-xs rounded-xl p-3 focus:outline-none focus:border-amber-500"
                  >
                    <option value={24}>عيار 24 (ذهب خالص 100%)</option>
                    <option value={21}>عيار 21 (ذهب 87.5%)</option>
                    <option value={18}>عيار 18 (ذهب 75%)</option>
                  </select>
                </div>
              )}

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">
                  {assetType === 'gold' ? 'وزن الذهب الكلي بالجرام:' : 'القيمة المالية الإجمالية (بالعملة المحلية):'}
                </label>
                <input
                  type="number"
                  value={assetValue}
                  onChange={(e) => setAssetValue(Number(e.target.value))}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-100 text-sm font-bold rounded-xl p-3 focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="flex items-center justify-between text-xs bg-slate-900 p-3 rounded-xl border border-slate-800">
                <span className="text-slate-400">سعر جرام الذهب عيار 24 المفترض:</span>
                <input
                  type="number"
                  value={goldPricePerGram}
                  onChange={(e) => setGoldPricePerGram(Number(e.target.value))}
                  className="w-20 bg-slate-950 border border-slate-700 text-amber-400 font-bold text-center rounded px-2 py-1 text-xs"
                />
              </div>
            </div>

            {/* Result Box */}
            <div className="bg-gradient-to-br from-amber-950/50 to-slate-950 p-6 rounded-2xl border border-amber-500/30 flex flex-col justify-between shadow-xl">
              <div className="space-y-3">
                <span className="text-xs font-bold text-amber-400">مقدار الزكاة الواجب إخراجها:</span>
                <div className="text-3xl md:text-4xl font-extrabold text-white font-mono">
                  {calculateZakat()} <span className="text-sm font-sans text-amber-300 font-normal">ر.س</span>
                </div>
                <p className="text-xs text-slate-400 leading-relaxed pt-2 border-t border-slate-800/80">
                  النصاب الشرعي: <span className="text-amber-300 font-bold">{(85 * goldPricePerGram).toLocaleString()} ر.س</span>.
                  إذا بلغت أصولك هذا النصاب ومضى عليها حول قمري كامل (354 يوماً) فالواجب إخراج ربع العشر (2.5%).
                </p>
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
          <div>
            <h3 className="text-xl font-bold text-white mb-2 flex items-center gap-2">
              <PieChart className="w-5 h-5 text-amber-400" />
              حاسبة المواريث الشرعية البصرية (علم الفرائض)
            </h3>
            <p className="text-slate-400 text-xs">
              تقسيم التركة والميراث خطوة بخطوة بالدليل الشرعي لكل وارث مع تطبيق قواعد أصحاب الفروض والعصبة.
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            <div className="bg-slate-950 p-5 rounded-2xl border border-slate-800 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">إجمالي مبلغ التركة الصافي (بعد سداد الديون والوصية):</label>
                <input
                  type="number"
                  value={estateAmount}
                  onChange={(e) => setEstateAmount(Number(e.target.value))}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-100 text-sm font-bold rounded-xl p-3 focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="space-y-3 pt-2">
                <span className="text-xs font-bold text-amber-400 block">عدد الورثة الأحياء المستحقين:</span>

                <div className="grid grid-cols-2 gap-3 text-xs">
                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الزوجة:</span>
                    <input
                      type="number"
                      min={0}
                      max={4}
                      value={heirs.wife}
                      onChange={(e) => setHeirs({ ...heirs, wife: Math.max(0, Number(e.target.value)) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الأبناء (ذكور):</span>
                    <input
                      type="number"
                      min={0}
                      value={heirs.sons}
                      onChange={(e) => setHeirs({ ...heirs, sons: Math.max(0, Number(e.target.value)) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>البنات (إناث):</span>
                    <input
                      type="number"
                      min={0}
                      value={heirs.daughters}
                      onChange={(e) => setHeirs({ ...heirs, daughters: Math.max(0, Number(e.target.value)) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الأم:</span>
                    <input
                      type="number"
                      min={0}
                      max={1}
                      value={heirs.mother}
                      onChange={(e) => setHeirs({ ...heirs, mother: Math.max(0, Number(e.target.value)) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الأب:</span>
                    <input
                      type="number"
                      min={0}
                      max={1}
                      value={heirs.father}
                      onChange={(e) => setHeirs({ ...heirs, father: Math.max(0, Number(e.target.value)) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Visual Breakdown */}
            <div className="bg-slate-950 p-5 rounded-2xl border border-slate-800 space-y-3">
              <h4 className="text-xs font-bold text-amber-400 border-b border-slate-800 pb-2">
                توزيع الأنصبة والدليل الشرعي المفصل:
              </h4>

              <div className="space-y-2.5 text-xs">
                {heirs.wife > 0 && (
                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 space-y-1">
                    <div className="flex justify-between font-bold text-slate-200">
                      <span>الزوجة (الثُّمن لوجود الفرع الوارث):</span>
                      <span className="text-amber-400 font-mono">{inheritanceBreakdown.wifeShare.toLocaleString()} ر.س</span>
                    </div>
                    <p className="text-[11px] text-slate-400">الدليل: "فَإِن كَانَ لَكُمْ وَلَدٌ فَلَهُنَّ الثُُّمُنُ مِمَّا تَرَكْتُم"</p>
                  </div>
                )}

                {heirs.mother > 0 && (
                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 space-y-1">
                    <div className="flex justify-between font-bold text-slate-200">
                      <span>الأم (السُّدس):</span>
                      <span className="text-amber-400 font-mono">{inheritanceBreakdown.motherShare.toLocaleString()} ر.س</span>
                    </div>
                    <p className="text-[11px] text-slate-400">الدليل: "وَلِأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِّنْهُمَا السُُّدُسُ"</p>
                  </div>
                )}

                {heirs.father > 0 && (
                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 space-y-1">
                    <div className="flex justify-between font-bold text-slate-200">
                      <span>الأب (السُّدس فرضاً مع وجود الابن):</span>
                      <span className="text-amber-400 font-mono">{inheritanceBreakdown.fatherShare.toLocaleString()} ر.س</span>
                    </div>
                    <p className="text-[11px] text-slate-400">الدليل: "وَلِأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِّنْهُمَا السُُّدُسُ"</p>
                  </div>
                )}

                {(heirs.sons > 0 || heirs.daughters > 0) && (
                  <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 space-y-1.5">
                    <div className="flex justify-between font-bold text-slate-200">
                      <span>الأبناء والبنات (عصبة بالغير):</span>
                      <span className="text-emerald-400 font-mono">{inheritanceBreakdown.residue.toLocaleString()} ر.س</span>
                    </div>
                    <div className="flex justify-between text-[11px] text-emerald-300 bg-slate-950 p-1.5 rounded">
                      <span>نصيب كل ابن ذكر: {inheritanceBreakdown.sonShare.toLocaleString()} ر.س</span>
                      <span>نصيب كل ابنة أنثى: {inheritanceBreakdown.daughterShare.toLocaleString()} ر.س</span>
                    </div>
                    <p className="text-[11px] text-slate-400">الدليل: "يُوصِيكُمُ اللَّهُ فِي أَوْلَادِكُمْ ۖ لِلذَّكَرِ مِثْلُ حَظِّ الْأُنثَيَيْنِ"</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
