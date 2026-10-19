import React, { useState } from 'react';
import { HeartPulse, Search, BookOpen, Compass, Info, Sparkles, MessageSquareQuote, Check } from 'lucide-react';

export default function DeepUnderstanding() {
  const [activeTab, setActiveTab] = useState('semantic'); // 'semantic', 'contextCard'
  const [userFeeling, setUserFeeling] = useState('');
  const [searchResult, setSearchResult] = useState(null);

  // Preset Feelings
  const feelingPresets = [
    "أشعر بقلق وتوتر من المستقبل والرزق",
    "تعرضت للظلم وأشعر بضيق شديد في صدري",
    "أبحث عن الطمأنينة والسلام الداخلي",
    "أمر بابتلاء واختبار صعب في حياتي"
  ];

  const handleSemanticSearch = (query) => {
    const textToSearch = query || userFeeling;
    if (!textToSearch.trim()) return;

    setUserFeeling(textToSearch);

    // Mock Semantic Search Logic based on input keywords
    if (textToSearch.includes("قلق") || textToSearch.includes("رزق") || textToSearch.includes("مستقبل")) {
      setSearchResult({
        quran: {
          verse: "وَمَن يَتَّقِ ٱللَّهَ يَجْعَل لَّهُۥ مَخْرَجًا وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ وَمَن يَتَوَكَّلْ عَلَى ٱللَّهِ فَهُوَ حَسْبُهُۥ",
          reference: "سورة الطلاق - الآية 2-3",
          reflection: "الرزق والطمأنينة مرتبطان بالتقوى والتوكل الصادق. الآية تفيد بأن الله يفتح الأبواب المغلقة من حيث لا تدري."
        },
        hadith: {
          text: "لو أَنَّكُمْ كُنْتُمْ تَتَوَكَّلُونَ عَلَى اللَّهِ حَقَّ تَوَكُّلِهِ لَرَزَقَكُمْ كَمَا يَرْزُقُ الطَّيْرَ تَغْدُو خِمَاصًا وَتَرُوحُ بِطَانًا",
          reference: "سنن الترمذي - حديث صحيح",
          actionableAdvice: "خذ بالأسباب المادية المتاحة ثم فوّض الأمر للرب دون الخوف السلبي من القادم."
        }
      });
    } else if (textToSearch.includes("ظلم") || textToSearch.includes("ضيق")) {
      setSearchResult({
        quran: {
          verse: "فَٱصْبِرْ صَبْرًا جَمِيلًا * إِنَّهُمْ يَرَوْنَهُۥ بَعِيدًا * وَنَرَىٰهُ قَرِيبًا",
          reference: "سورة المعارج - الآية 5-7",
          reflection: "الصبر الجميل هو الصبر الذي لا شكوى فيه لغير الله، والفرج أقرب مما يتخيله الإنسان."
        },
        hadith: {
          text: "اتَّقِ دَعْوَةَ الْمَظْلُومِ، فَإِنَّهُ لَيْسَ بَيْنَهَا وَبَيْنَ اللَّهِ حِجَابٌ",
          reference: "صحيح البخاري",
          actionableAdvice: "احرص على ألا تظلم أحداً، واطمئن بأن مظلمتك محفوقة بعناية الله وعدله."
        }
      });
    } else {
      setSearchResult({
        quran: {
          verse: "أَلَا بِذِكْرِ ٱللَّهِ تَطْمَئِنُّ ٱلْقُلُوبُ",
          reference: "سورة الرعد - الآية 28",
          reflection: "القلب لا يجد السكن والراحة الحقيقية إلا بالاتصال بالخالق سبحانه وتعالى."
        },
        hadith: {
          text: "عَجَبًا لأَمْرِ المؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ لَهُ خَيْرٌ",
          reference: "صحيح مسلم",
          actionableAdvice: "خصص 10 دقائق يومياً للاستغفار والتسبيح في مكان هادئ لتجديد الطاقة الإيمانية."
        }
      });
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-teal-900/80 via-emerald-800/50 to-slate-900 border border-teal-500/30 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-teal-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-teal-500/20 text-teal-300 text-xs font-semibold mb-3 border border-teal-500/30">
              <HeartPulse className="w-3.5 h-3.5" />
              البحث الدلالي والنفسي
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">المعايشة والفهم العميق</h2>
            <p className="text-slate-300 text-sm mt-1">
              ربط الحالة النفسية بآيات القرآن والسنة الشريفة، واستعراض التفسير السياقي والتاريخي التفاعلي.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('semantic')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'semantic' ? 'bg-teal-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الباحث النفسي
            </button>
            <button
              onClick={() => setActiveTab('contextCard')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all ${
                activeTab === 'contextCard' ? 'bg-teal-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              بطاقة السياق والتفسير
            </button>
          </div>
        </div>
      </div>

      {/* TAB 1: SEMANTIC & EMOTIONAL SEARCH */}
      {activeTab === 'semantic' && (
        <div className="space-y-6">
          <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg">
            <h3 className="text-lg font-bold text-teal-300 mb-2 flex items-center gap-2">
              <Compass className="w-5 h-5 text-teal-400" />
              ماذا تشعر الآن؟ أو ما هو الموقف الذي يمر بك؟
            </h3>
            <p className="text-slate-400 text-xs mb-4">
              اكتب تعبيراً حرّاً عن شعورك دون الحاجة لحفظ كلمات الآيات، وسنستخرج لك المواساة والتوجيه الشرعي المناسب.
            </p>

            <div className="flex flex-col md:flex-row gap-3 mb-4">
              <div className="relative flex-1">
                <input
                  type="text"
                  value={userFeeling}
                  onChange={(e) => setUserFeeling(e.target.value)}
                  placeholder="مثال: أشعر بقلق وتوتر بخصوص رزقي أو خائف من اتخاذ قرار..."
                  className="w-full bg-slate-950 border border-slate-700 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-teal-500 pl-10"
                />
                <Search className="w-4 h-4 text-slate-500 absolute left-3 top-3.5" />
              </div>
              <button
                onClick={() => handleSemanticSearch()}
                className="px-6 py-3 bg-teal-600 hover:bg-teal-500 text-white text-sm font-semibold rounded-xl shadow-md transition-all flex items-center justify-center gap-2"
              >
                <Sparkles className="w-4 h-4" />
                استخراج التوجيه
              </button>
            </div>

            {/* Presets */}
            <div className="space-y-2">
              <span className="text-xs text-slate-400 font-semibold">أمثلة مقترحة للبحث الشعوري:</span>
              <div className="flex flex-wrap gap-2">
                {feelingPresets.map((preset, idx) => (
                  <button
                    key={idx}
                    onClick={() => handleSemanticSearch(preset)}
                    className="text-xs bg-slate-800/80 hover:bg-slate-700/80 text-teal-200/90 px-3 py-1.5 rounded-lg border border-slate-700 transition-colors"
                  >
                    {preset}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Results Box */}
          {searchResult && (
            <div className="grid md:grid-cols-2 gap-6">
              {/* Quranic Guidance */}
              <div className="bg-slate-900/90 border border-emerald-500/30 rounded-2xl p-6 shadow-lg space-y-4">
                <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                  <span className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                    <BookOpen className="w-4 h-4" />
                    المواساة من كتاب الله
                  </span>
                  <span className="text-xs text-slate-400">{searchResult.quran.reference}</span>
                </div>
                <p className="text-xl text-emerald-100 font-serif leading-relaxed text-center py-2">
                  "{searchResult.quran.verse}"
                </p>
                <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-xs text-slate-300 space-y-1">
                  <span className="font-semibold text-emerald-400 block">💡 التوجيه النفسي والإيماني:</span>
                  <p>{searchResult.quran.reflection}</p>
                </div>
              </div>

              {/* Sunnah & Practical Guidance */}
              <div className="bg-slate-900/90 border border-teal-500/30 rounded-2xl p-6 shadow-lg space-y-4">
                <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                  <span className="text-xs font-bold text-teal-400 flex items-center gap-1.5">
                    <MessageSquareQuote className="w-4 h-4" />
                    التوجيه النبوي الشريف
                  </span>
                  <span className="text-xs text-slate-400">{searchResult.hadith.reference}</span>
                </div>
                <p className="text-base text-teal-100 font-serif leading-relaxed text-center py-2">
                  "{searchResult.hadith.text}"
                </p>
                <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-xs text-slate-300 space-y-1">
                  <span className="font-semibold text-teal-400 block flex items-center gap-1">
                    <Check className="w-3.5 h-3.5" />
                    خطوة تطبيقية في يومك:
                  </span>
                  <p>{searchResult.hadith.actionableAdvice}</p>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: INTERACTIVE CONTEXT CARD */}
      {activeTab === 'contextCard' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="border-b border-slate-800 pb-4">
            <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
              <Info className="w-5 h-5 text-teal-400" />
              بطاقة السياق التفاعلية (بطاقة الآية)
            </h3>
            <p className="text-slate-400 text-sm">
              عرض سياق النزول، العهد (مكي/مدني)، المعاني البصرية للكلمات الغريبة، وأسباب النزول كقصة قصيرة.
            </p>
          </div>

          {/* Sample Verse Context Card */}
          <div className="bg-gradient-to-b from-slate-950 to-slate-900 border border-teal-500/30 rounded-2xl p-6 space-y-6 shadow-2xl">
            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 pb-4">
              <div className="flex items-center gap-2">
                <span className="bg-teal-500/20 text-teal-300 text-xs px-3 py-1 rounded-full border border-teal-500/30 font-semibold">
                  سورة الضُّحَى - الآية 3
                </span>
                <span className="bg-purple-500/20 text-purple-300 text-xs px-2.5 py-1 rounded-full border border-purple-500/30">
                  عهد مَكّي 🕋
                </span>
              </div>
              <span className="text-xs text-slate-400">ترتيب النزول: 11</span>
            </div>

            <div className="text-center py-4">
              <p className="text-2xl md:text-3xl font-serif text-teal-100 leading-loose">
                "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ"
              </p>
            </div>

            <div className="grid md:grid-cols-2 gap-4">
              {/* Cause of Revelation */}
              <div className="bg-slate-900/80 p-4 rounded-xl border border-slate-800 space-y-2">
                <h4 className="text-xs font-bold text-teal-400 flex items-center gap-1.5">
                  📖 سبب النزول (قصة مختصرة):
                </h4>
                <p className="text-xs text-slate-300 leading-relaxed">
                  احتبس الوحي عن النبي ﷺ أربعة ألقال أو أيام، فقال المشركون: "إن محمداً قد ودّعه ربه وقلاه (أبغضه)"، فأنزل الله هذه السورة تسلية لنبيه ونفياً لما زعموه.
                </p>
              </div>

              {/* Visual Word Meanings */}
              <div className="bg-slate-900/80 p-4 rounded-xl border border-slate-800 space-y-2">
                <h4 className="text-xs font-bold text-amber-400 flex items-center gap-1.5">
                  🔍 معاني الكلمات الغريبة بصرياً:
                </h4>
                <ul className="text-xs text-slate-300 space-y-1.5">
                  <li className="flex items-center justify-between bg-slate-950 p-2 rounded border border-slate-800">
                    <span className="font-bold text-amber-300">مَا وَدَّعَكَ:</span>
                    <span className="text-slate-400">ما تركك وما قطعك منذ أن اختارك.</span>
                  </li>
                  <li className="flex items-center justify-between bg-slate-950 p-2 rounded border border-slate-800">
                    <span className="font-bold text-amber-300">وَمَا قَلَىٰ:</span>
                    <span className="text-slate-400">ما أبغضك ولا كرهك قط.</span>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
