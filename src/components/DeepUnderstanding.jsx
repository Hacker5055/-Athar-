import React, { useState } from 'react';
import { HeartPulse, Search, BookOpen, Compass, Info, Sparkles, MessageSquareQuote, Check, ChevronDown, Layers } from 'lucide-react';

export default function DeepUnderstanding() {
  const [activeTab, setActiveTab] = useState('semantic'); // 'semantic', 'contextCard'
  const [userFeeling, setUserFeeling] = useState('');
  const [searchResult, setSearchResult] = useState(null);

  // Selected verse for context card
  const [selectedVerseIndex, setSelectedVerseIndex] = useState(0);

  // Preset Feelings
  const feelingPresets = [
    "أشعر بقلق وتوتر من المستقبل والرزق",
    "تعرضت للظلم وأشعر بضيق شديد في صدري",
    "أبحث عن الطمأنينة والسلام الداخلي ونبذ الخوف",
    "أمر بابتلاء واختبار صعب في حياتي ومرضي"
  ];

  const handleSemanticSearch = (query) => {
    const textToSearch = query || userFeeling;
    if (!textToSearch.trim()) return;

    setUserFeeling(textToSearch);

    // Mock Semantic Search Logic based on input keywords
    if (textToSearch.includes("قلق") || textToSearch.includes("رزق") || textToSearch.includes("مستقبل")) {
      setSearchResult({
        quran: {
          verse: "وَمَن يَتَّقِ ٱللَّهَ يَجْعَل لَّهُۥ مَخْرَجًا * وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ وَمَن يَتَوَكَّلْ عَلَى ٱللَّهِ فَهُوَ حَسْبُهُۥ",
          reference: "سورة الطلاق - الآيتان 2-3",
          reflection: "الرزق والطمأنينة النفسية مرتبطان بالتقوى والتوكل الصادق. الآية تفيد بأن الله يفتح الأبواب المغلقة من حيث لا تدري، والتوكل يزيل هم الغد."
        },
        hadith: {
          text: "لو أَنَّكُمْ كُنْتُمْ تَتَوَكَّلُونَ عَلَى اللَّهِ حَقَّ تَوَكُّلِهِ لَرَزَقَكُمْ كَمَا يَرْزُقُ الطَّيْرَ تَغْدُو خِمَاصًا وَتَرُوحُ بِطَانًا",
          reference: "سنن الترمذي - حديث صحيح",
          actionableAdvice: "بذل السبب المادي المتاح اليوم، وتفويض النتيجة لله مع التوقف عن استباق الأحداث السلبية."
        }
      });
    } else if (textToSearch.includes("ظلم") || textToSearch.includes("ضيق")) {
      setSearchResult({
        quran: {
          verse: "فَٱصْبِرْ صَبْرًا جَمِيلًا * إِنَّهُمْ يَرَوْنَهُۥ بَعِيدًا * وَنَرَىٰهُ قَرِيبًا",
          reference: "سورة المعارج - الآيات 5-7",
          reflection: "الصبر الجميل هو الصبر الهادئ الذي لا شكوى فيه لغير الله، وفرج الله وعدالته أقرب بكثير مما يتخيله الإنسان في لحظة ألمه."
        },
        hadith: {
          text: "اتَّقِ دَعْوَةَ الْمَظْلُومِ، فَإِنَّهُ لَيْسَ بَيْنَهَا وَبَيْنَ اللَّهِ حِجَابٌ",
          reference: "صحيح البخاري",
          actionableAdvice: "احرص على ألا تظلم أحداً، وثق بأن مظلمتك محفوفة بعناية الله المطلقة التي لا يضيع معها حق."
        }
      });
    } else if (textToSearch.includes("ابتلاء") || textToSearch.includes("مرض")) {
      setSearchResult({
        quran: {
          verse: "وَبَشِّرِ الصَّابِرِينَ * الَّذِينَ إِذَا أَصَابَتْهُم مُّصِيبَةٌ قَالُوا إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ * أُولَٰئِكَ عَلَيْهِمْ صَلَوَاتٌ مِّن رَّبِّهِمْ وَرَحْمَةٌ",
          reference: "سورة البقرة - الآيات 155-157",
          reflection: "الابتلاء سنة الله في خلقه لرفع الدرجات وتكفير السيئات، والاسترجاع يملأ القلب بالسكينة واليقين."
        },
        hadith: {
          text: "مَا يُصِيبُ الْمُسْلِمَ مِنْ نَصَبٍ وَلا وَصَبٍ وَلا هَمٍّ وَلا حُزْنٍ حَتَّى الشَّوْكَةِ يُشَاكُهَا إِلا كَفَّرَ اللَّهُ بِهَا مِنْ خَطَايَاهُ",
          reference: "صحيح البخاري ومسلم",
          actionableAdvice: "احتسب ألمك وتعبك كأجر مطهر، وكرر دعاء الاسترجاع بيقين صادق."
        }
      });
    } else {
      setSearchResult({
        quran: {
          verse: "ٱلَّذِينَ ءَامَنُوا۟ وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ ٱللَّهِ ۗ أَلَا بِذِكْرِ ٱللَّهِ تَطْمَئِنُّ ٱلْقُلُوبُ",
          reference: "سورة الرعد - الآية 28",
          reflection: "القلب لا يجد السكن والراحة الحقيقية إلا بالاتصال بالخالق سبحانه وتعالى ومداومة الذكر."
        },
        hadith: {
          text: "عَجَبًا لأَمْرِ المؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ لَهُ خَيْرٌ، إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
          reference: "صحيح مسلم",
          actionableAdvice: "خصص 10 دقائق بعد صلاة المغرب للاستغفار والتسبيح في خلوة لتصفية الذهن وتجديد اليقين."
        }
      });
    }
  };

  const contextCards = [
    {
      verse: "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ",
      surah: "سورة الضُّحَى - الآية 3",
      era: "عهد مَكّي 🕋",
      order: "ترتيب النزول: 11",
      story: "احتبس الوحي عن النبي ﷺ أربعة ألقال أو أيام، فقال المشركون شاتمين: 'إن محمداً قد ودّعه ربه وقلاه (أبغضه)'، فحزن رسول الله، فأنزل الله هذه السورة تسلية لنبيه ونفياً قاطعاً لما زعموه وإثباتاً لعنايته الدائمة به.",
      meanings: [
        { word: "مَا وَدَّعَكَ", mean: "ما تركك وما قطع صلته بك منذ أن اصطفاك." },
        { word: "وَمَا قَلَىٰ", mean: "ما أبغضك ولا كرهك قط منذ خلقك." },
        { word: "سَجَىٰ", mean: "إذا أظلم وسكن وغطى الكون بظلامه." }
      ]
    },
    {
      verse: "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ * فَصَلِّ لِرَبِّكَ وَانْحَرْ * إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ",
      surah: "سورة الكوثر - الآيات 1-3",
      era: "عهد مَكّي 🕋",
      order: "ترتيب النزول: 15",
      story: "لما مات القاسم وعبد الله ابنا رسول الله ﷺ، فرح العاص بن وائل السهمي وقال: 'إن محمداً أبتر لا عقب له (أي مقطوع النسل والذكر)'، فأنزل الله الكوثر رداً عليه وبياناً لخلود ذكر النبي وعظم نهره في الجنة وبتر ذكر أعدائه.",
      meanings: [
        { word: "الْكَوْثَرَ", mean: "الخير الكثير الدائم، وهو نهر عظيم في الجنة حوضه كبياض اللبن." },
        { word: "شَانِئَكَ", mean: "مبغضك وعدوك." },
        { word: "الْأَبْتَرُ", mean: "المقطوع من كل خير والمنقطع ذكره وأثره." }
      ]
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-teal-900/80 via-emerald-800/50 to-slate-900 border border-teal-500/30 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-teal-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-teal-500/20 text-teal-300 text-xs font-semibold mb-3 border border-teal-500/30">
              <HeartPulse className="w-3.5 h-3.5" />
              البحث الدلالي والنفسي التفاعلي
            </div>
            <h2 className="text-2xl md:text-3xl font-bold text-white">المعايشة والفهم العميق</h2>
            <p className="text-slate-300 text-sm mt-1">
              ربط الحالة النفسية والشعورية بآيات القرآن والسنة الشريفة، واستعراض التفسير السياقي والتاريخي التفاعلي.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/60 p-1.5 rounded-xl border border-slate-800">
            <button
              onClick={() => setActiveTab('semantic')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
                activeTab === 'semantic' ? 'bg-teal-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الباحث النفسي
            </button>
            <button
              onClick={() => setActiveTab('contextCard')}
              className={`px-4 py-2 rounded-lg text-xs md:text-sm font-medium transition-all cursor-pointer ${
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
              ماذا تشعر الآن؟ أو ما الموقف والتحدي الذي تمر به؟
            </h3>
            <p className="text-slate-400 text-xs mb-4">
              اكتب تعبيراً حرّاً عن مشاعرك أو ما يقلقك، وسيقوم محرك البحث الدلالي الذكي باستخراج الآيات والأحاديث الصحيحة التي تواسيك وتوجهك عملياً.
            </p>

            <div className="flex flex-col md:flex-row gap-3 mb-4">
              <div className="relative flex-1">
                <input
                  type="text"
                  value={userFeeling}
                  onChange={(e) => setUserFeeling(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSemanticSearch()}
                  placeholder="مثال: أشعر بقلق من المستقبل، أو تعرضت لظلم، أو أبحث عن سكينة..."
                  className="w-full bg-slate-950 border border-slate-700 rounded-xl px-4 py-3 text-sm text-slate-100 focus:outline-none focus:border-teal-500 pl-10"
                />
                <Search className="w-4 h-4 text-slate-500 absolute left-3 top-3.5" />
              </div>
              <button
                onClick={() => handleSemanticSearch()}
                className="px-6 py-3 bg-teal-600 hover:bg-teal-500 text-white text-sm font-semibold rounded-xl shadow-md transition-all flex items-center justify-center gap-2 cursor-pointer"
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
                    className="text-xs bg-slate-800/80 hover:bg-slate-700/80 text-teal-200/90 px-3 py-1.5 rounded-lg border border-slate-700 transition-colors cursor-pointer"
                  >
                    {preset}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Results Box */}
          {searchResult && (
            <div className="grid md:grid-cols-2 gap-6 animate-fade-in">
              {/* Quranic Guidance */}
              <div className="bg-slate-900/90 border border-emerald-500/30 rounded-2xl p-6 shadow-lg space-y-4">
                <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                  <span className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                    <BookOpen className="w-4 h-4" />
                    المواساة من كتاب الله
                  </span>
                  <span className="text-xs text-slate-400">{searchResult.quran.reference}</span>
                </div>
                <p className="text-xl text-emerald-100 font-serif leading-relaxed text-center py-2 font-bold">
                  "{searchResult.quran.verse}"
                </p>
                <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-xs text-slate-300 space-y-1">
                  <span className="font-semibold text-emerald-400 block">💡 التوجيه النفسي والإيماني:</span>
                  <p className="leading-relaxed">{searchResult.quran.reflection}</p>
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
                <p className="text-base text-teal-100 font-serif leading-relaxed text-center py-2 font-medium">
                  "{searchResult.hadith.text}"
                </p>
                <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 text-xs text-slate-300 space-y-1">
                  <span className="font-semibold text-teal-400 block flex items-center gap-1">
                    <Check className="w-3.5 h-3.5" />
                    خطوة تطبيقية في يومك:
                  </span>
                  <p className="leading-relaxed">{searchResult.hadith.actionableAdvice}</p>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: INTERACTIVE CONTEXT CARD */}
      {activeTab === 'contextCard' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-lg space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white mb-1 flex items-center gap-2">
                <Info className="w-5 h-5 text-teal-400" />
                بطاقة السياق التفاعلية (بطاقة الآية وأسباب النزول)
              </h3>
              <p className="text-slate-400 text-xs">
                عرض سياق النزول، العهد (مكي/مدني)، المعاني البصرية للكلمات الغريبة، وأسباب النزول في قصة قصيرة واضحة.
              </p>
            </div>

            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400">اختر الآية للعرض:</span>
              <div className="flex gap-2">
                {contextCards.map((c, i) => (
                  <button
                    key={i}
                    onClick={() => setSelectedVerseIndex(i)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-semibold cursor-pointer transition-all ${
                      selectedVerseIndex === i ? 'bg-teal-600 text-white' : 'bg-slate-950 text-slate-400 border border-slate-800'
                    }`}
                  >
                    نموذج {i + 1}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Verse Context Card */}
          {(() => {
            const card = contextCards[selectedVerseIndex];
            return (
              <div className="bg-gradient-to-b from-slate-950 to-slate-900 border border-teal-500/30 rounded-2xl p-6 space-y-6 shadow-2xl">
                <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 pb-4">
                  <div className="flex items-center gap-2">
                    <span className="bg-teal-500/20 text-teal-300 text-xs px-3 py-1 rounded-full border border-teal-500/30 font-semibold">
                      {card.surah}
                    </span>
                    <span className="bg-purple-500/20 text-purple-300 text-xs px-2.5 py-1 rounded-full border border-purple-500/30">
                      {card.era}
                    </span>
                  </div>
                  <span className="text-xs text-slate-400 font-mono">{card.order}</span>
                </div>

                <div className="text-center py-4">
                  <p className="text-2xl md:text-3xl font-serif text-teal-100 leading-loose font-bold">
                    "{card.verse}"
                  </p>
                </div>

                <div className="grid md:grid-cols-2 gap-4">
                  {/* Cause of Revelation */}
                  <div className="bg-slate-900/80 p-5 rounded-xl border border-slate-800 space-y-2">
                    <h4 className="text-xs font-bold text-teal-400 flex items-center gap-1.5">
                      📖 سبب النزول والقصة التاريخية:
                    </h4>
                    <p className="text-xs text-slate-300 leading-relaxed">
                      {card.story}
                    </p>
                  </div>

                  {/* Visual Word Meanings */}
                  <div className="bg-slate-900/80 p-5 rounded-xl border border-slate-800 space-y-2">
                    <h4 className="text-xs font-bold text-amber-400 flex items-center gap-1.5">
                      🔍 معاني الكلمات بصرياً:
                    </h4>
                    <ul className="text-xs text-slate-300 space-y-2">
                      {card.meanings.map((m, mIdx) => (
                        <li key={mIdx} className="flex items-center justify-between bg-slate-950 p-2.5 rounded-lg border border-slate-800">
                          <span className="font-bold text-amber-300">{m.word}:</span>
                          <span className="text-slate-300 text-[11px]">{m.mean}</span>
                        </li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>
            );
          })()}
        </div>
      )}
    </div>
  );
}
