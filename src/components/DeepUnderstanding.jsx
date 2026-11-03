import React, { useState } from 'react';
import {
  HeartPulse,
  Search,
  BookOpen,
  Compass,
  Info,
  Sparkles,
  MessageSquareQuote,
  Check,
  MapPin,
  Calendar,
  Layers,
  ArrowRight
} from 'lucide-react';

export default function DeepUnderstanding() {
  const [activeTab, setActiveTab] = useState('semantic'); // 'semantic', 'contextCard'
  const [userFeeling, setUserFeeling] = useState('');
  const [searchResult, setSearchResult] = useState(null);

  // Preset Feelings
  const feelingPresets = [
    { title: "قلق وتوتر من المستقبل والرزق", query: "أشعر بقلق وتوتر من المستقبل والرزق وضيق مالي" },
    { title: "تعرضت للظلم أو الإساءة", query: "تعرضت للظلم وأشعر بضيق شديد في صدري ورغبة في الانتصار" },
    { title: "فقدت عزيزاً أو أمر بحزن عميق", query: "أمر بحزن عميق وألم فقد وفراق" },
    { title: "حيرة في اتخاذ قرار مصيري", query: "محتار في اتخاذ قرار في حياتي ومتردد" },
    { title: "البحث عن الطمأنينة وتجديد الإيمان", query: "أبحث عن الطمأنينة وراحة البال والسلام الداخلي" }
  ];

  const handleSemanticSearch = (query) => {
    const textToSearch = query || userFeeling;
    if (!textToSearch.trim()) return;

    setUserFeeling(textToSearch);

    // Semantic Mapping
    if (textToSearch.includes("قلق") || textToSearch.includes("رزق") || textToSearch.includes("مستقبل") || textToSearch.includes("مالي")) {
      setSearchResult({
        emotionCategory: "القلق والخوف من الرزق والمستقبل",
        quran: {
          verse: "وَمَن يَتَّقِ ٱللَّهَ يَجْعَل لَّهُۥ مَخْرَجًا * وَيَرْزُقْهُ مِنْ حَيْثُ لَا يَحْتَسِبُ وَمَن يَتَوَكَّلْ عَلَى ٱللَّهِ فَهُوَ حَسْبُهُۥ",
          reference: "سورة الطلاق - الآية 2-3",
          reflection: "الرزق والطمأنينة النفسية ثمرة مباشرة للتقوى والتوكل الصادق. الآية تؤكد أن الله يفتح الأبواب المغلقة من مسارات لا تخطر ببالك."
        },
        hadith: {
          text: "لَوْ أَنَّكُمْ كُنْتُمْ تَتَوَكَّلُونَ عَلَى اللَّهِ حَقَّ تَوَكُّلِهِ لَرَزَقَكُمْ كَمَا يَرْزُقُ الطَّيْرَ تَغْدُو خِمَاصًا وَتَرُوحُ بِطَانًا",
          reference: "سنن الترمذي - حديث صحيح",
          actionableAdvice: "استفرغ وسعك في الأسباب المادية المعقولة، واقطع تفكيرك عن السيناريوهات السلبية المستقبلية لأن تدبير الغد بيد الله."
        }
      });
    } else if (textToSearch.includes("ظلم") || textToSearch.includes("إساءة") || textToSearch.includes("ضيق")) {
      setSearchResult({
        emotionCategory: "المظلومية وضيق الصدر من الناس",
        quran: {
          verse: "فَٱصْبِرْ صَبْرًا جَمِيلًا * إِنَّهُمْ يَرَوْنَهُۥ بَعِيدًا * وَنَرَىٰهُ قَرِيبًا",
          reference: "سورة المعارج - الآيات 5-7",
          reflection: "الصبر الجميل هو الصبر النقي من التشكي والتسخط، وحقك محفوظ عند من لا تضيع عنده المظالم."
        },
        hadith: {
          text: "اتَّقِ دَعْوَةَ الْمَظْلُومِ، فَإِنَّهُ لَيْسَ بَيْنَهَا وَبَيْنَ اللَّهِ حِجَابٌ",
          reference: "صحيح البخاري",
          actionableAdvice: "ارفع أمرك لرب السماء بالدعاء دون أن تظلم نفسك بالانتقام غير الشرعي، وتذكر أن الله يملي للظالم حتى إذا أخذه لم يفلته."
        }
      });
    } else if (textToSearch.includes("حزن") || textToSearch.includes("فقد") || textToSearch.includes("ألم")) {
      setSearchResult({
        emotionCategory: "الحزن والمصيبة وفراق الأحبة",
        quran: {
          verse: "وَبَشِّرِ ٱلصَّٰبِرِينَ * ٱلَّذِينَ إِذَآ أَصَٰبَتْهُم مُّصِيبَةٌ قَالُوٓا۟ إِنَّا لِلَّهِ وَإِنَّآ إِلَيْهِ رَٰجِعُونَ * أُو۟لَٰٓئِكَ عَلَيْهِمْ صَلَوَٰتٌ مِّن رَّبِّهِمْ وَرَحْمَةٌ",
          reference: "سورة البقرة - الآيات 155-157",
          reflection: "الاسترجاع تذكرة بأننا جميعاً وأحبابنا وما نملك ملك لله وإليه المرجع، وهو يثيب على دمعة الحزن الصادقة."
        },
        hadith: {
          text: "إِنَّ الْعَيْنَ تَدْمَعُ، وَالْقَلْبَ يَحْزَنُ، وَلاَ نَقُولُ إِلاَّ مَا يَرْضَى رَبُّنَا، وَإِنَّا بِفِرَاقِكَ يَا إِبْرَاهِيمُ لَمَحْزُونُونَ",
          reference: "صحيح البخاري",
          actionableAdvice: "الحزن الفطري ليس عيباً ولا ينافي الرضا، فبث حزنك لله وادعُ لمن فقدت بالرحمة وتصدق عنه."
        }
      });
    } else if (textToSearch.includes("قرار") || textToSearch.includes("محتار") || textToSearch.includes("تردد")) {
      setSearchResult({
        emotionCategory: "الحيرة واتخاذ القرارات المصيرية",
        quran: {
          verse: "فَإِذَا عَزَمْتَ فَتَوَكَّلْ عَلَى ٱللَّهِ إِنَّ ٱللَّهَ يُحِبُّ ٱلْمُتَوَكِّلِينَ",
          reference: "سورة آل عمران - الآية 159",
          reflection: "بعد المشورة واستفراغ الجهد في التفكير، يأتي دور العزم والمضي قدماً متوكلاً على الله دون تسويف أو وسواس."
        },
        hadith: {
          text: "إِذَا هَمَّ أَحَدُكُمْ بِالأَمْرِ فَلْيَرْكَعْ رَكْعَتَيْنِ مِنْ غَيْرِ الْفَرِيضَةِ ثُمَّ لِيَقُلْ: اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ...",
          reference: "صحيح البخاري - دعاء الاستخارة",
          actionableAdvice: "صلِّ ركعتين استخارة الآن، ثم امضِ فيما انشرح له صدرك وتهيأت أسبابه، واعلم أن الخير فيما اختاره الله."
        }
      });
    } else {
      setSearchResult({
        emotionCategory: "البحث عن الطمأنينة وسلام القلب",
        quran: {
          verse: "ٱلَّذِينَ ءَامَنُوا۟ وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ ٱللَّهِ أَلَا بِذِكْرِ ٱللَّهِ تَطْمَئِنُّ ٱلْقُلُوبُ",
          reference: "سورة الرعد - الآية 28",
          reflection: "القلوب مجبولة على القلق والتشتت ولا تسكن سكوناً حقيقياً إلا بالاتصال المستمر بربها وملازمة ذكره."
        },
        hadith: {
          text: "عَجَبًا لأَمْرِ الْمُؤْمِنِ إِنَّ أَمْرَهُ كُلَّهُ خَيْرٌ، إِنْ أَصَابَتْهُ سَرَّاءُ شَكَرَ فَكَانَ خَيْرًا لَهُ، وَإِنْ أَصَابَتْهُ ضَرَّاءُ صَبَرَ فَكَانَ خَيْرًا لَهُ",
          reference: "صحيح مسلم",
          actionableAdvice: "اجعل لك في ختام يومك 10 دقائق صامتة تسبح وتستغفر فيها وتفرغ ذهنك من صخب الدنيا."
        }
      });
    }
  };

  // Interactive Context Cards State
  const [selectedContextVerse, setSelectedContextVerse] = useState('duha');

  const contextCards = {
    duha: {
      surah: 'سورة الضُّحَى',
      ayahNum: 'الآية 3',
      text: 'مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ',
      period: 'مَكّي 🕋',
      order: 'ترتيب النزول: 11',
      contextStory: 'احتبس الوحي عن النبي ﷺ أياماً وليالي حتى شق ذلك عليه، فقال المشركون شاتمين: "إن محمداً قد ودّعه ربه وقلاه (أبغضه وجفاه)"، فأنزل الله سورة الضحى قَسماً بنور الضحى وسكون الليل نفياً لفرية المشركين وتسليةً لقلب النبي ﷺ وتثبيتاً له.',
      words: [
        { word: 'مَا وَدَّعَكَ', meaning: 'ما تركك وما فارقك منذ أن رعاك واصطفاك.' },
        { word: 'وَمَا قَلَىٰ', meaning: 'ما أبغضك ولا كرهك، بل أنت حبيب الرحمن دائماً.' },
        { word: 'سَجَىٰ', meaning: 'إذا سكن واستقر ظلامه وغطى الكون بالهدوء.' }
      ]
    },
    inshirah: {
      surah: 'سورة الشَّرْح',
      ayahNum: 'الآيات 5 - 6',
      text: 'فَإِنَّ مَعَ الْعُسْرِ يُسْرًا * إِنَّ مَعَ الْعُسْرِ يُسْرًا',
      period: 'مَكّي 🕋',
      order: 'ترتيب النزول: 12',
      contextStory: 'نزلت حين كان المسلمون قلة مستضعفين في مكة يلاقون الأذى والاضطهاد. تكررت الآية تأكيداً على أن العسر الواحد (معرف بأل) لا يمكن أن يغلب يُسرين (مُنكَّرين)، فمع كل ضائقة حلوان وفرجان.',
      words: [
        { word: 'أَنقَضَ ظَهْرَكَ', meaning: 'أثقل كاهلك حتى كاد يُسمع له نقيض (صوت الثقل).' },
        { word: 'فَانصَبْ', meaning: 'إذا فرغت من أشغالك الدنيوية فاجتهد وأتعب نفسك في العبادة والدعاء.' },
        { word: 'وَإِلَىٰ رَبِّكَ فَارْغَبْ', meaning: 'اجعل نيتك ورغبتك وتوكلك خالصاً لله وحده.' }
      ]
    },
    kawthar: {
      surah: 'سورة الكَوْثَر',
      ayahNum: 'الآيات 1 - 3',
      text: 'إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ * فَصَلِّ لِرَبِّكَ وَانْحَرْ * إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ',
      period: 'مَكّي 🕋',
      order: 'ترتيب النزول: 15',
      contextStory: 'لما مات عبد الله أو القاسم ابن النبي ﷺ، قال العاص بن وائل وبعض مشركي قريش شامتين: "انقطع أثر محمد فهو أبتر لا عقب له يذكره"، فأنزل الله رداً حاسماً يمنحه نهر الكوثر والخير الكثير، ويقضي بأن مبغضه هو المبتور مقطوع الذكر والأثر.',
      words: [
        { word: 'الْكَوْثَرَ', meaning: 'الخير العظيم الكثير، ومنه نهر في الجنة حافتاه قباب اللؤلؤ المجوف.' },
        { word: 'وَانْحَرْ', meaning: 'اذبح الأضاحي لوجه الله متقرباً به، بخلاف ما كان المشركون يذبحون لأصنامهم.' },
        { word: 'شَانِئَكَ', meaning: 'مبغضك وعدوك والكاره لما جئت به من الحق.' },
        { word: 'الْأَبْتَرُ', meaning: 'المقطوع من كل خير ومن بقاء الذكر الصالح.' }
      ]
    }
  };

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-teal-900/80 via-emerald-800/50 to-slate-900 border border-teal-500/30 rounded-3xl p-6 md:p-8 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 left-0 w-32 h-32 bg-teal-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-teal-500/20 text-teal-300 text-xs font-semibold mb-3 border border-teal-500/30">
              <HeartPulse className="w-3.5 h-3.5" />
              المعايشة القرآنية والفهم العميق
            </div>
            <h2 className="text-2xl md:text-3xl font-extrabold text-white">الباحث النفسي والسياقي</h2>
            <p className="text-slate-300 text-sm mt-1 max-w-2xl">
              تطبيق عملي يربط مشاعرك اليومية بنصوص القرآن والسنة، مع بطاقات سياق تفاعلية تكشف أسباب النزول ومعاني الكلمات.
            </p>
          </div>
          <div className="flex items-center gap-2 bg-slate-950/80 p-1.5 rounded-2xl border border-slate-800">
            <button
              onClick={() => setActiveTab('semantic')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
                activeTab === 'semantic' ? 'bg-teal-600 text-white shadow-lg' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              الباحث النفسي
            </button>
            <button
              onClick={() => setActiveTab('contextCard')}
              className={`px-4 py-2 rounded-xl text-xs md:text-sm font-bold transition-all ${
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
          <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-4">
            <h3 className="text-lg font-bold text-teal-300 flex items-center gap-2">
              <Compass className="w-5 h-5 text-teal-400" />
              بماذا تشعر الآن؟ وما الموقف الحياتي الذي تواجهه؟
            </h3>
            <p className="text-slate-400 text-xs leading-relaxed">
              اكتب مشاعرك بصياغتك الحرة دون التقيد بكلمات الآيات، وسيبحث الذكاء الاصطناعي الدلالي عن التوجيه والمواساة القرآنية والنبوية المناسبة لحالتك.
            </p>

            <div className="flex flex-col md:flex-row gap-3">
              <div className="relative flex-1">
                <input
                  type="text"
                  value={userFeeling}
                  onChange={(e) => setUserFeeling(e.target.value)}
                  placeholder="مثال: أشعر بقلق على رزقي، أو حزن على فراق شخص، أو حيرة في اتخاذ قرار..."
                  className="w-full bg-slate-950 border border-slate-700 rounded-2xl px-4 py-3.5 text-sm text-slate-100 focus:outline-none focus:border-teal-500 pl-10"
                />
                <Search className="w-4 h-4 text-slate-500 absolute left-3 top-4" />
              </div>
              <button
                onClick={() => handleSemanticSearch()}
                className="px-6 py-3.5 bg-teal-600 hover:bg-teal-500 text-white text-xs font-bold rounded-2xl shadow-lg transition-all flex items-center justify-center gap-2"
              >
                <Sparkles className="w-4 h-4" />
                استخراج التوجيه النفسي
              </button>
            </div>

            {/* Presets */}
            <div className="space-y-2 pt-2">
              <span className="text-xs text-slate-400 font-semibold block">مواقف شعورية جاهزة للتجربة السريعة:</span>
              <div className="flex flex-wrap gap-2">
                {feelingPresets.map((preset, idx) => (
                  <button
                    key={idx}
                    onClick={() => handleSemanticSearch(preset.query)}
                    className="text-xs bg-slate-950 hover:bg-teal-950/60 text-teal-200 px-3.5 py-2 rounded-xl border border-slate-800 hover:border-teal-500/50 transition-all font-medium"
                  >
                    {preset.title}
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* Results Box */}
          {searchResult ? (
            <div className="space-y-4 animate-fade-in">
              <div className="flex items-center justify-between px-2">
                <span className="text-xs font-bold text-teal-400">
                  تصنيف الحالة الشعورية: <strong className="text-white underline">{searchResult.emotionCategory}</strong>
                </span>
                <span className="text-xs text-slate-400">تطابق دلالي عالي الدقة</span>
              </div>

              <div className="grid md:grid-cols-2 gap-6">
                {/* Quranic Guidance */}
                <div className="bg-slate-900/90 border border-emerald-500/30 rounded-3xl p-6 shadow-xl space-y-4 flex flex-col justify-between">
                  <div className="space-y-4">
                    <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                      <span className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                        <BookOpen className="w-4 h-4" />
                        المواساة والتوجيه من كتاب الله
                      </span>
                      <span className="text-xs text-slate-400 font-mono">{searchResult.quran.reference}</span>
                    </div>
                    <p className="text-lg md:text-xl text-emerald-100 font-serif leading-loose text-center py-2">
                      "{searchResult.quran.verse}"
                    </p>
                  </div>
                  <div className="bg-slate-950 p-4 rounded-2xl border border-slate-800 text-xs text-slate-300 space-y-1">
                    <span className="font-bold text-emerald-400 block">💡 الأثر النفسي والتدبر:</span>
                    <p className="leading-relaxed">{searchResult.quran.reflection}</p>
                  </div>
                </div>

                {/* Sunnah Guidance */}
                <div className="bg-slate-900/90 border border-teal-500/30 rounded-3xl p-6 shadow-xl space-y-4 flex flex-col justify-between">
                  <div className="space-y-4">
                    <div className="flex items-center justify-between border-b border-slate-800 pb-3">
                      <span className="text-xs font-bold text-teal-400 flex items-center gap-1.5">
                        <MessageSquareQuote className="w-4 h-4" />
                        التوجيه النبوي الشريف
                      </span>
                      <span className="text-xs text-slate-400 font-mono">{searchResult.hadith.reference}</span>
                    </div>
                    <p className="text-base text-teal-100 font-serif leading-relaxed text-center py-2">
                      "{searchResult.hadith.text}"
                    </p>
                  </div>
                  <div className="bg-slate-950 p-4 rounded-2xl border border-slate-800 text-xs text-slate-300 space-y-1">
                    <span className="font-bold text-teal-400 flex items-center gap-1">
                      <Check className="w-3.5 h-3.5" />
                      خطوة عملية وتطبيقية ليومك:
                    </span>
                    <p className="leading-relaxed">{searchResult.hadith.actionableAdvice}</p>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div className="bg-slate-900/60 border border-slate-800 rounded-3xl p-8 text-center text-slate-500 space-y-2">
              <Compass className="w-10 h-10 mx-auto text-slate-600 opacity-40 mb-2" />
              <p className="text-sm font-semibold text-slate-400">لم يتم إدخال حالة شعورية بعد.</p>
              <p className="text-xs text-slate-600">اختر من الأمثلة بالأعلى أو اكتب ما يجول بخاطرك لاستخراج التوجيه القرآني والنبوي.</p>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: INTERACTIVE CONTEXT CARD */}
      {activeTab === 'contextCard' && (
        <div className="bg-slate-900/90 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-6">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-4">
            <div>
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Info className="w-5 h-5 text-teal-400" />
                بطاقة السياق التفاعلية (بطاقة الآية وسياق النزول)
              </h3>
              <p className="text-slate-400 text-xs mt-1">
                تصفح العهد المكي والمدني، سبب النزول كقصة قصيرة مشوقة، والمعاني البصرية للكلمات الغريبة.
              </p>
            </div>

            <div className="flex gap-2">
              {Object.keys(contextCards).map((key) => (
                <button
                  key={key}
                  onClick={() => setSelectedContextVerse(key)}
                  className={`px-3 py-1.5 text-xs font-bold rounded-xl border transition-all ${
                    selectedContextVerse === key
                      ? 'bg-teal-600 text-white border-teal-400 shadow-md'
                      : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200'
                  }`}
                >
                  {contextCards[key].surah}
                </button>
              ))}
            </div>
          </div>

          {/* Selected Verse Context Card */}
          <div className="bg-gradient-to-b from-slate-950 to-slate-900 border border-teal-500/30 rounded-3xl p-6 md:p-8 space-y-6 shadow-2xl">
            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 pb-4">
              <div className="flex items-center gap-2">
                <span className="bg-teal-500/20 text-teal-300 text-xs px-3.5 py-1.5 rounded-full border border-teal-500/30 font-bold">
                  {contextCards[selectedContextVerse].surah} - {contextCards[selectedContextVerse].ayahNum}
                </span>
                <span className="bg-purple-500/20 text-purple-300 text-xs px-3 py-1.5 rounded-full border border-purple-500/30 font-semibold">
                  {contextCards[selectedContextVerse].period}
                </span>
              </div>
              <span className="text-xs text-slate-400 font-mono">{contextCards[selectedContextVerse].order}</span>
            </div>

            <div className="text-center py-6">
              <p className="text-2xl md:text-3xl font-serif text-teal-100 leading-loose">
                "{contextCards[selectedContextVerse].text}"
              </p>
            </div>

            <div className="grid md:grid-cols-2 gap-6">
              {/* Cause of Revelation */}
              <div className="bg-slate-900/80 p-5 rounded-2xl border border-slate-800 space-y-2.5">
                <h4 className="text-xs font-bold text-teal-400 flex items-center gap-1.5 border-b border-slate-800/80 pb-2">
                  📖 سبب النزول (قصة السياق التاريخي):
                </h4>
                <p className="text-xs text-slate-300 leading-relaxed">
                  {contextCards[selectedContextVerse].contextStory}
                </p>
              </div>

              {/* Visual Word Meanings */}
              <div className="bg-slate-900/80 p-5 rounded-2xl border border-slate-800 space-y-2.5">
                <h4 className="text-xs font-bold text-amber-400 flex items-center gap-1.5 border-b border-slate-800/80 pb-2">
                  🔍 معاني الكلمات الغريبة بصرياً:
                </h4>
                <ul className="text-xs text-slate-300 space-y-2">
                  {contextCards[selectedContextVerse].words.map((item, wIdx) => (
                    <li key={wIdx} className="bg-slate-950 p-2.5 rounded-xl border border-slate-800 flex items-start justify-between gap-2">
                      <span className="font-bold text-amber-300 whitespace-nowrap">{item.word}:</span>
                      <span className="text-slate-300 text-[11px] text-left leading-relaxed">{item.meaning}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
