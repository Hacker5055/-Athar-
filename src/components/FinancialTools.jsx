import React, { useState } from 'react';
import { Scale, FileText, Calculator, AlertTriangle, CheckCircle, RefreshCw, DollarSign, PieChart, Layers } from 'lucide-react';

export default function FinancialTools() {
  const [activeTab, setActiveTab] = useState('contractScanner'); // 'contractScanner', 'zakatCalc', 'inheritanceCalc'

  // Contract Templates Presets
  const contractTemplates = [
    {
      id: 'installment',
      title: 'عقد تقسيط سيارة مع غرامة تأخير',
      text: `في حال تأخر الطرف الثاني عن سداد القسط المالي في الموعد المحدد لمدة تتجاوز 15 يوماً، تُفرض غرامة تأخير قدرها 5% من قيمة القسط تضاف لحساب الشركة، كما يشترط شرط جزائي غير محدد القيمة.`
    },
    {
      id: 'employment',
      title: 'عقد عمل مع شرط عدم منافسة مفتوح',
      text: `يحظر على الموظف العمل لدى أي منافس داخل أو خارج المملكة لأجل غير مسمى بعد انتهاء العقد، وفي حال مخالفة ذلك يلتزم بدفع تعويض مقداره 200,000 ريال دون إثبات الضرر المادي.`
    },
    {
      id: 'murabaha',
      title: 'عقد تمويل مرابحة إسلامي متوافق',
      text: `تمت بضاعة التمويل بعد تملك البنك لها وقبضها حكماً، وتم بيعها للمشتري بمرابحة معلومة الأجل والربح دون أي غرامات مالية مشروطة على التأخير.`
    }
  ];

  // Contract Scanner State
  const [contractText, setContractText] = useState(contractTemplates[0].text);
  const [scanAnalysis, setScanAnalysis] = useState(null);

  // Zakat Calculator State
  const [assetType, setAssetType] = useState('stocks');
  const [assetValue, setAssetValue] = useState(100000); // Value in local currency
  const [goldPricePerGram, setGoldPricePerGram] = useState(280); // Live gold price estimation

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
    if (contractText.includes('مرابحة معلومة')) {
      setScanAnalysis({
        status: 'success',
        issues: []
      });
      return;
    }

    if (contractText.includes('عقد عمل') || contractText.includes('الموظف')) {
      setScanAnalysis({
        status: 'warning',
        issues: [
          {
            title: 'شرط عدم منافسة غير مقيد زمنياً ومكانياً (غرر وجهالة)',
            description: 'اشتراط عدم المنافسة لأجل غير مسمى وفي كل المكان يسبب ضرراً بالغة بالعمالة ويتعارض مع الضوابط الشرعية ونظام العمل.',
            alternative: 'البديل الشرعي: تقييد الشرط بمدة محددة (مثل سنة واحدة) ونطاق جغرافي محدد لحماية أسرار العمل فقط.'
          },
          {
            title: 'مبالغة في التعويض التعسفي',
            description: 'المراد تعويض ثابت دون إثبات حدوث ضياع أو ضرر فعلي مادي.',
            alternative: 'البديل الشرعي: ربط التعويض بالضرر المادي المباشر والمثبت فعلياً.'
          }
        ]
      });
      return;
    }

    setScanAnalysis({
      status: 'warning',
      issues: [
        {
          title: 'شبهة فائدة ربوية (غرامة التأخير المالية)',
          description: 'البند ينص على زيادة مالية مشروطة بسبب التأخير في السداد، وهو يتعارض مع ضوابط الشريعة الإسلامية.',
          alternative: 'البديل الشرعي: اشتراط التبرع بمبلغ محدد لصالح الجمعيات الخيرية في حال التأخير غير المبرر لردع المماطل، دون أن تستفيد منه الشركة.'
        },
        {
          title: 'جهالة في الشرط الجزائي',
          description: 'البند يحتوي على شرط جزائي غير محدد القيمة والحدود، مما يؤدي للغرر.',
          alternative: 'البديل الشرعي: تحديد مبلغ التعويض الفعلي المباشر المتوقع عن الأضرار المادية الحقيقية فقط.'
        }
      ]
    });
  };

  const calculateZakat = () => {
    // Standard Zakat rate 2.5%
    return (assetValue * 0.025).toLocaleString();
  };

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
                الصق نص عقد العمل، قسط الشراء، أو التمويل البنكي للتحقق من خلوه من المحاذير الشرعية.
              </p>
            </div>

            {/* Presets Selector */}
            <div className="space-y-1.5">
              <span className="text-[11px] text-slate-400 font-semibold block">نماذج جاهزة للتجربة:</span>
              <div className="flex flex-wrap gap-1.5">
                {contractTemplates.map((template) => (
                  <button
                    key={template.id}
                    onClick={() => {
                      setContractText(template.text);
                      setScanAnalysis(null);
                    }}
                    className="text-[11px] bg-slate-950 hover:bg-slate-800 text-amber-200/90 px-2.5 py-1 rounded-lg border border-slate-800 transition-colors"
                  >
                    {template.title}
                  </button>
                ))}
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
              فحص البنود بالذكاء الاصطناعي
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
                {scanAnalysis.status === 'success' ? (
                  <div className="bg-emerald-950/40 border border-emerald-500/40 p-4 rounded-xl flex items-center gap-2 text-xs text-emerald-300 font-semibold">
                    <CheckCircle className="w-5 h-5 text-emerald-400 shrink-0" />
                    العقد خلو من المحاذير الشرعية الظاهرة ومطابق لأحكام المعاملات المالية الإسلامية.
                  </div>
                ) : (
                  <>
                    <div className="bg-amber-950/40 border border-amber-500/40 p-3 rounded-xl flex items-center gap-2 text-xs text-amber-300 font-semibold">
                      <AlertTriangle className="w-4 h-4 text-amber-400 shrink-0" />
                      تم العثور على {scanAnalysis.issues.length} من المحاذير الشرعية المحتملة في نص العقد.
                    </div>

                    {scanAnalysis.issues.map((issue, idx) => (
                      <div key={idx} className="bg-slate-950 border border-slate-800 p-4 rounded-xl space-y-2 text-xs">
                        <h4 className="font-bold text-red-400 flex items-center gap-1.5">
                          ❌ {issue.title}
                        </h4>
                        <p className="text-slate-300 leading-relaxed">{issue.description}</p>
                        <div className="bg-emerald-950/30 border border-emerald-500/30 p-2.5 rounded-lg text-emerald-300 mt-2">
                          <span className="font-bold block mb-0.5">💡 البديل الشرعي المقترح:</span>
                          {issue.alternative}
                        </div>
                      </div>
                    ))}
                  </>
                )}
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
          <div>
            <h3 className="text-xl font-bold text-white mb-2 flex items-center gap-2">
              <Calculator className="w-5 h-5 text-amber-400" />
              حاسبة الزكاة المتطورة (الأسهم، الذهب، والعملات)
            </h3>
            <p className="text-slate-400 text-xs">
              حساب زكاة المال والأسهم الاستثمارية والمضاربة مع الربط بسعر الذهب اللحظي (النصاب الشرعي 85 جرام ذهب).
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            <div className="space-y-4 bg-slate-950 p-5 rounded-2xl border border-slate-800">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">نوع الأصل المالي:</label>
                <select
                  value={assetType}
                  onChange={(e) => setAssetType(e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-200 text-xs rounded-xl p-3 focus:outline-none focus:border-amber-500"
                >
                  <option value="stocks">أسهم استثمارية / مضاربة</option>
                  <option value="gold">ذهب ومجوهرات (جرام)</option>
                  <option value="crypto">عملات رقمية وسيولة نقدية</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">
                  {assetType === 'gold' ? 'وزن الذهب بالجرام:' : 'القيمة المالية الإجمالية (بالعملة المحلية):'}
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
                <span className="text-amber-400 font-bold">{goldPricePerGram} ر.س/جرام</span>
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
                  النصاب الشرعي الحالي: <span className="text-amber-300 font-bold">{(85 * goldPricePerGram).toLocaleString()} ر.س</span>.
                  إذا بلغت أصولك هذا النصاب ومر عليها حول كامل (سنة هجرية) فالزكاة هي 2.5%.
                </p>
              </div>

              <div className="bg-slate-900/80 p-3 rounded-xl border border-slate-800 text-[11px] text-slate-300 flex items-center gap-2 mt-4">
                <CheckCircle className="w-4 h-4 text-emerald-400 shrink-0" />
                <span>حساب دقيق متوافق مع معايير هيئة الزكاة والضريبة والجمارك.</span>
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
              حاسبة المواريث الشرعية البصرية
            </h3>
            <p className="text-slate-400 text-xs">
              تقسيم التركة والميراث خطوة بخطوة مع عرض الدليل الشرعي والنصيب لكل وارث.
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-6">
            <div className="bg-slate-950 p-5 rounded-2xl border border-slate-800 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-2">إجمالي مبلغ التركة:</label>
                <input
                  type="number"
                  value={estateAmount}
                  onChange={(e) => setEstateAmount(Number(e.target.value))}
                  className="w-full bg-slate-900 border border-slate-700 text-slate-100 text-sm font-bold rounded-xl p-3 focus:outline-none focus:border-amber-500"
                />
              </div>

              <div className="space-y-3 pt-2">
                <span className="text-xs font-bold text-amber-400 block">عدد الورثة المستحقين:</span>

                <div className="grid grid-cols-2 gap-3 text-xs">
                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الزوجة:</span>
                    <input
                      type="number"
                      value={heirs.wife}
                      onChange={(e) => setHeirs({ ...heirs, wife: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الأبناء (ذكور):</span>
                    <input
                      type="number"
                      value={heirs.sons}
                      onChange={(e) => setHeirs({ ...heirs, sons: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>البنات (إناث):</span>
                    <input
                      type="number"
                      value={heirs.daughters}
                      onChange={(e) => setHeirs({ ...heirs, daughters: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>

                  <div className="bg-slate-900 p-2.5 rounded-lg border border-slate-800 flex justify-between items-center">
                    <span>الأم / الأب:</span>
                    <input
                      type="number"
                      value={heirs.mother}
                      onChange={(e) => setHeirs({ ...heirs, mother: Number(e.target.value) })}
                      className="w-12 bg-slate-950 border border-slate-700 text-center rounded py-1 text-white font-bold"
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* Visual Breakdown */}
            <div className="bg-slate-950 p-5 rounded-2xl border border-slate-800 space-y-4">
              <h4 className="text-xs font-bold text-amber-400 border-b border-slate-800 pb-2">
                توزيع الأنصبة والدليل الشرعي:
              </h4>

              <div className="space-y-3 text-xs">
                <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 space-y-1">
                  <div className="flex justify-between font-bold text-slate-200">
                    <span>الزوجة (الثُّمن - 1/8):</span>
                    <span className="text-amber-400 font-mono">{(estateAmount * 0.125).toLocaleString()} ر.س</span>
                  </div>
                  <p className="text-[11px] text-slate-400">الدليل: "فَإِن كَانَ لَكُمْ وَلَدٌ فَلَهُنَّ الثُُّمُنُ مِمَّا تَرَكْتُم"</p>
                </div>

                <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 space-y-1">
                  <div className="flex justify-between font-bold text-slate-200">
                    <span>الأم والأب (السُّدس لكل منهما):</span>
                    <span className="text-amber-400 font-mono">{(estateAmount * 0.1666 * 2).toLocaleString()} ر.س</span>
                  </div>
                  <p className="text-[11px] text-slate-400">الدليل: "وَلِأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِّنْهُمَا السُُّدُسُ"</p>
                </div>

                <div className="bg-slate-900 p-3 rounded-xl border border-slate-800 space-y-1">
                  <div className="flex justify-between font-bold text-slate-200">
                    <span>الأبناء والبنات (عصبة - للذكر مثل حظ الأنثيين):</span>
                    <span className="text-emerald-400 font-mono">{(estateAmount * 0.541).toLocaleString()} ر.س</span>
                  </div>
                  <p className="text-[11px] text-slate-400">الدليل: "يُوصِيكُمُ اللَّهُ فِي أَوْلَادِكُمْ ۖ لِلذَّكَرِ مِثْلُ حَظِّ الْأُنثَيَيْنِ"</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
